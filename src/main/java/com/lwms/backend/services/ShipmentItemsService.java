package com.lwms.backend.services;

import com.lwms.backend.dao.ShipmentItemsRepository;
import com.lwms.backend.dao.InventoryRepository;
import com.lwms.backend.dao.ShipmentsRepository;
import com.lwms.backend.dto.ShipmentItemCreateRequest;
import com.lwms.backend.dto.ShipmentItemSummaryDto;
import com.lwms.backend.dto.ShipmentItemUpdateRequest;
import com.lwms.backend.dto.InventoryMovementCreateRequest;
import com.lwms.backend.dto.InventoryMovementUpdateRequest;
import com.lwms.backend.entities.Inventory;
import com.lwms.backend.entities.InventoryMovements;
import com.lwms.backend.entities.ShipmentItems;
import com.lwms.backend.entities.Shipments;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShipmentItemsService {
	private final ShipmentItemsRepository shipmentItemsRepository;
	private final ShipmentsRepository shipmentsRepository;
	private final InventoryRepository inventoryRepository;
	private final InventoryMovementsService movementsService;

	public ShipmentItemsService(ShipmentItemsRepository shipmentItemsRepository, ShipmentsRepository shipmentsRepository, InventoryRepository inventoryRepository, InventoryMovementsService movementsService) {
		this.shipmentItemsRepository = shipmentItemsRepository;
		this.shipmentsRepository = shipmentsRepository;
		this.inventoryRepository = inventoryRepository;
		this.movementsService = movementsService;
	}

	@Transactional(readOnly = true)
	public List<ShipmentItemSummaryDto> listByShipmentId(Integer shipmentId) {
		return shipmentItemsRepository.findByShipment_ShipmentId(shipmentId).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<ShipmentItemSummaryDto> listByShipmentNumber(String shipmentNumber) {
		return shipmentItemsRepository.findByShipment_ShipmentNumber(shipmentNumber).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<ShipmentItemSummaryDto> listAll() {
		return shipmentItemsRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
	}

	@Transactional
	public ShipmentItemSummaryDto create(ShipmentItemCreateRequest req) {
		Shipments shipment = shipmentsRepository.findById(req.getShipmentId()).orElseThrow(() -> new RuntimeException("Shipment not found: " + req.getShipmentId()));
		Inventory item = inventoryRepository.findById(req.getItemId()).orElseThrow(() -> new RuntimeException("Inventory item not found: " + req.getItemId()));
		// Prevent duplicate shipment-item pair
		ShipmentItems existing = shipmentItemsRepository.findByShipment_ShipmentIdAndItem_ItemId(shipment.getShipmentId(), item.getItemId());
		if (existing != null) {
			throw new RuntimeException("Shipment item already exists for this shipment and item");
		}
		ShipmentItems si = new ShipmentItems();
		si.setShipment(shipment);
		si.setItem(item);
		si.setQuantity(req.getQuantity());
		// Use provided unit price if present; otherwise default to inventory unit price
		if (req.getUnitPrice() != null && req.getUnitPrice().signum() >= 0) {
			si.setUnitPrice(req.getUnitPrice());
		} else {
			si.setUnitPrice(item.getUnitPrice());
		}
		si.setTotalPrice(calculateTotal(req.getQuantity(), si.getUnitPrice()));
		ShipmentItems saved = shipmentItemsRepository.save(si);
		// Sync movement via service
		createOrUpdateMovement(saved);
		recalcShipmentTotal(shipment.getShipmentId());
		return toDto(saved);
	}

	@Transactional
	public ShipmentItemSummaryDto update(Integer shipmentItemId, ShipmentItemUpdateRequest req) {
		ShipmentItems si = shipmentItemsRepository.findById(shipmentItemId).orElseThrow(() -> new RuntimeException("Shipment item not found: " + shipmentItemId));
		if (req.getShipmentId() != null) {
			Shipments shipment = shipmentsRepository.findById(req.getShipmentId()).orElseThrow(() -> new RuntimeException("Shipment not found: " + req.getShipmentId()));
			si.setShipment(shipment);
		}
		if (req.getItemId() != null) {
			Inventory item = inventoryRepository.findById(req.getItemId()).orElseThrow(() -> new RuntimeException("Inventory item not found: " + req.getItemId()));
			si.setItem(item);
		}
		if (req.getQuantity() != null) si.setQuantity(req.getQuantity());
		if (req.getUnitPrice() != null) si.setUnitPrice(req.getUnitPrice());
		si.setTotalPrice(calculateTotal(si.getQuantity(), si.getUnitPrice()));
		ShipmentItems saved = shipmentItemsRepository.save(si);
		// Sync movement via service
		createOrUpdateMovement(saved);
		recalcShipmentTotal(saved.getShipment().getShipmentId());
		return toDto(saved);
	}

	@Transactional
	public void delete(Integer shipmentItemId) {
		ShipmentItems si = shipmentItemsRepository.findById(shipmentItemId).orElseThrow(() -> new RuntimeException("Shipment item not found: " + shipmentItemId));
		Integer shipmentId = si.getShipment().getShipmentId();
		// Delete associated movement via service (if present)
		deleteMovementIfExists(si);
		shipmentItemsRepository.delete(si);
		recalcShipmentTotal(shipmentId);
	}

	private void createOrUpdateMovement(ShipmentItems si) {
		Shipments shipment = si.getShipment();
		Inventory item = si.getItem();
		if (shipment == null || item == null) return;
		String type = shipment.getShipmentType() == Shipments.ShipmentType.Inbound ? "IN" : "OUT";
		InventoryMovementCreateRequest createReq = new InventoryMovementCreateRequest();
		createReq.setItemId(item.getItemId());
		createReq.setMovementType(type);
		createReq.setQuantity(si.getQuantity());
		createReq.setToLocationId(item.getLocation() != null ? item.getLocation().getLocationId() : null);
		createReq.setReferenceType("Shipment");
		createReq.setReferenceId(shipment.getShipmentId());
		createReq.setNotes("Auto-sync for shipment #" + shipment.getShipmentNumber());
		// Try to find existing movement and update, else create
		InventoryMovements existing = movementsService
				.findByReferenceAndItem("Shipment", shipment.getShipmentId(), item.getItemId());
		if (existing != null) {
			InventoryMovementUpdateRequest up = new InventoryMovementUpdateRequest();
			up.setItemId(item.getItemId());
			up.setMovementType(type);
			up.setQuantity(si.getQuantity());
			up.setToLocationId(createReq.getToLocationId());
			up.setReferenceType("Shipment");
			up.setReferenceId(shipment.getShipmentId());
			up.setNotes(createReq.getNotes());
			movementsService.update(existing.getMovementId(), up);
		} else {
			movementsService.create(createReq);
		}
	}

	private void deleteMovementIfExists(ShipmentItems si) {
		Shipments shipment = si.getShipment();
		Inventory item = si.getItem();
		if (shipment == null || item == null) return;
		InventoryMovements existing = movementsService
				.findByReferenceAndItem("Shipment", shipment.getShipmentId(), item.getItemId());
		if (existing != null) {
			movementsService.delete(existing.getMovementId());
		}
	}

	private BigDecimal calculateTotal(Integer quantity, BigDecimal unitPrice) {
		BigDecimal q = quantity != null ? BigDecimal.valueOf(quantity) : BigDecimal.ZERO;
		BigDecimal p = unitPrice != null ? unitPrice : BigDecimal.ZERO;
		return q.multiply(p);
	}

	private void recalcShipmentTotal(Integer shipmentId) {
		Shipments sh = shipmentsRepository.findById(shipmentId).orElse(null);
		if (sh == null) return;
		BigDecimal sum = shipmentItemsRepository.findByShipment_ShipmentId(shipmentId).stream()
				.map(ShipmentItems::getTotalPrice)
				.reduce(BigDecimal.ZERO, (a,b) -> a.add(b != null ? b : BigDecimal.ZERO));
		sh.setTotalValue(sum);
		shipmentsRepository.save(sh);
	}

	private ShipmentItemSummaryDto toDto(ShipmentItems si) {
		ShipmentItemSummaryDto dto = new ShipmentItemSummaryDto();
		dto.setShipmentItemId(si.getShipmentItemId());
		dto.setShipmentNumber(si.getShipment() != null ? si.getShipment().getShipmentNumber() : null);
		dto.setItemCode(si.getItem() != null ? si.getItem().getItemCode() : null);
		dto.setQuantity(si.getQuantity());
		dto.setUnitPrice(si.getUnitPrice());
		dto.setTotalPrice(si.getTotalPrice());
		return dto;
	}
} 