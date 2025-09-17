package com.lwms.backend.services;

import com.lwms.backend.dao.ShipmentsRepository;
import com.lwms.backend.dao.UserRepository;
import com.lwms.backend.dao.SuppliersRepository;
import com.lwms.backend.dao.ShipmentItemsRepository;
import com.lwms.backend.dto.ShipmentCreateRequest;
import com.lwms.backend.dto.ShipmentSummaryDto;
import com.lwms.backend.dto.ShipmentUpdateRequest;
import com.lwms.backend.dto.InventoryMovementCreateRequest;
import com.lwms.backend.dto.InventoryMovementUpdateRequest;
import com.lwms.backend.entities.ShipmentItems;
import com.lwms.backend.entities.Shipments;
import com.lwms.backend.entities.Suppliers;
import com.lwms.backend.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShipmentService {
	private final ShipmentsRepository shipmentsRepository;
	private final UserRepository userRepository;
	private final SuppliersRepository suppliersRepository;
	private final ShipmentItemsRepository shipmentItemsRepository;
	private final InventoryMovementsService movementsService;

	public ShipmentService(ShipmentsRepository shipmentsRepository, UserRepository userRepository, SuppliersRepository suppliersRepository, ShipmentItemsRepository shipmentItemsRepository, InventoryMovementsService movementsService) {
		this.shipmentsRepository = shipmentsRepository;
		this.userRepository = userRepository;
		this.suppliersRepository = suppliersRepository;
		this.shipmentItemsRepository = shipmentItemsRepository;
		this.movementsService = movementsService;
	}

	@Transactional(readOnly = true)
	public List<ShipmentSummaryDto> listShipments(String q) {
		List<Shipments> results;
		if (StringUtils.hasText(q)) {
			String query = q.trim();
			List<Shipments> byNumber = shipmentsRepository.findByShipmentNumberContainingIgnoreCase(query);
			List<Shipments> agg = new ArrayList<>(byNumber);
			// Type match (Inbound/Outbound)
			try {
				Shipments.ShipmentType type = Shipments.ShipmentType.valueOf(query.substring(0,1).toUpperCase() + query.substring(1).toLowerCase());
				agg.addAll(shipmentsRepository.findAll().stream().filter(s -> s.getShipmentType() == type).collect(Collectors.toList()));
			} catch (IllegalArgumentException ignore) {}
			results = new ArrayList<>(Map.copyOf(agg.stream().collect(Collectors.toMap(Shipments::getShipmentId, s -> s, (a,b)->a))).values());
		} else {
			results = shipmentsRepository.findAll();
		}
		return results.stream().map(this::toDto).collect(Collectors.toList());
	}

	@Transactional
	public ShipmentSummaryDto createShipment(ShipmentCreateRequest req) {
		Shipments s = new Shipments();
		s.setShipmentNumber(req.getShipmentNumber());
		if (StringUtils.hasText(req.getShipmentType())) s.setShipmentType(Shipments.ShipmentType.valueOf(req.getShipmentType()));
		if (StringUtils.hasText(req.getStatus())) s.setStatus(parseStatus(req.getStatus()));
		s.setOrigin(req.getOrigin());
		s.setDestination(req.getDestination());
		if (StringUtils.hasText(req.getExpectedDelivery())) s.setExpectedDeliveryDate(LocalDateTime.parse(req.getExpectedDelivery()));
		if (StringUtils.hasText(req.getActualDelivery())) s.setActualDeliveryDate(LocalDateTime.parse(req.getActualDelivery()));
		if (StringUtils.hasText(req.getTotalValue())) s.setTotalValue(new BigDecimal(req.getTotalValue()));
		resolveAndSetSupplier(s, req.getSupplierId(), req.getSupplier());
		s.setCreatedBy(resolveCurrentUser());
		Shipments saved = shipmentsRepository.save(s);
		return toDto(saved);
	}

	@Transactional
	public ShipmentSummaryDto updateShipment(Integer shipmentId, ShipmentUpdateRequest req) {
		Shipments s = shipmentsRepository.findById(shipmentId)
				.orElseThrow(() -> new RuntimeException("Shipment not found: " + shipmentId));
		Shipments.ShipmentType oldType = s.getShipmentType();
		Shipments.Status oldStatus = s.getStatus();
		if (req.getShipmentNumber() != null) s.setShipmentNumber(req.getShipmentNumber());
		if (req.getShipmentType() != null) s.setShipmentType(Shipments.ShipmentType.valueOf(req.getShipmentType()));
		if (req.getStatus() != null) s.setStatus(parseStatus(req.getStatus()));
		if (req.getOrigin() != null) s.setOrigin(req.getOrigin());
		if (req.getDestination() != null) s.setDestination(req.getDestination());
		if (req.getExpectedDelivery() != null) s.setExpectedDeliveryDate(LocalDateTime.parse(req.getExpectedDelivery()));
		if (req.getActualDelivery() != null) s.setActualDeliveryDate(LocalDateTime.parse(req.getActualDelivery()));
		if (req.getTotalValue() != null) s.setTotalValue(new BigDecimal(req.getTotalValue()));
		if (req.getSupplier() != null || req.getSupplierId() != null) resolveAndSetSupplier(s, req.getSupplierId(), req.getSupplier());
		Shipments saved = shipmentsRepository.save(s);
		boolean typeChanged = oldType != saved.getShipmentType();
		boolean statusChanged = oldStatus != saved.getStatus();
		if (typeChanged || statusChanged) {
			syncItemMovementsForShipment(saved);
		}
		return toDto(saved);
	}

	@Transactional
	public void deleteShipment(Integer shipmentId) {
		Shipments s = shipmentsRepository.findById(shipmentId).orElseThrow(() -> new RuntimeException("Shipment not found: " + shipmentId));
		// Remove movements for all items to revert inventory
		for (ShipmentItems si : shipmentItemsRepository.findByShipment_ShipmentId(shipmentId)) {
			var existing = movementsService.findByReferenceAndItem("Shipment", shipmentId, si.getItem().getItemId());
			if (existing != null) movementsService.delete(existing.getMovementId());
		}
		shipmentsRepository.deleteById(shipmentId);
	}

	private Shipments.Status parseStatus(String s) {
		return switch (s) {
			case "Planned" -> Shipments.Status.Planned;
			case "In Transit" -> Shipments.Status.In_Transit;
			case "Delivered" -> Shipments.Status.Delivered;
			case "Cancelled" -> Shipments.Status.Cancelled;
			default -> throw new IllegalArgumentException("Unknown status: " + s);
		};
	}

	private ShipmentSummaryDto toDto(Shipments s) {
		ShipmentSummaryDto dto = new ShipmentSummaryDto();
		dto.setShipmentId(s.getShipmentId());
		dto.setShipmentNumber(s.getShipmentNumber());
		dto.setShipmentType(s.getShipmentType() != null ? s.getShipmentType().name() : null);
		dto.setStatus(s.getStatus() != null ? switch (s.getStatus()) {
			case Planned -> "Planned";
			case In_Transit -> "In Transit";
			case Delivered -> "Delivered";
			case Cancelled -> "Cancelled";
		} : null);
		dto.setOrigin(s.getOrigin());
		dto.setDestination(s.getDestination());
		dto.setExpectedDelivery(s.getExpectedDeliveryDate() != null ? s.getExpectedDeliveryDate().toString() : null);
		dto.setActualDelivery(s.getActualDeliveryDate() != null ? s.getActualDeliveryDate().toString() : null);
		dto.setTotalValue(s.getTotalValue() != null ? s.getTotalValue().toString() : null);
		dto.setSupplier(s.getSupplier() != null ? s.getSupplier().getSupplierName() : null);
		return dto;
	}

	private void resolveAndSetSupplier(Shipments s, Integer supplierId, String supplierNameFallback) {
		if (supplierId != null) {
			Suppliers sup = suppliersRepository.findById(supplierId).orElse(null);
			s.setSupplier(sup);
			return;
		}
		if (!StringUtils.hasText(supplierNameFallback)) { s.setSupplier(null); return; }
		String v = supplierNameFallback.trim();
		Suppliers sup = suppliersRepository.search(v, null).stream()
				.filter(x -> x.getSupplierName() != null && x.getSupplierName().equalsIgnoreCase(v))
				.findFirst()
				.orElseGet(() -> suppliersRepository.search(v, null).stream().findFirst().orElse(null));
		s.setSupplier(sup);
	}

	private User resolveCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof org.springframework.security.core.userdetails.User springUser)) {
			throw new RuntimeException("Unauthenticated: createdBy required");
		}
		String username = springUser.getUsername();
		try {
			User u = new User();
			u.setUserId(Integer.parseInt(username));
			return u;
		} catch (NumberFormatException ignore) {
			return userRepository.findByUsername(username)
					.orElseThrow(() -> new RuntimeException("User not found for username: " + username));
		}
	}

	private void syncItemMovementsForShipment(Shipments shipment) {
		List<ShipmentItems> items = shipmentItemsRepository.findByShipment_ShipmentId(shipment.getShipmentId());
		boolean cancelled = shipment.getStatus() == Shipments.Status.Cancelled;
		String moveType = shipment.getShipmentType() == Shipments.ShipmentType.Inbound ? "IN" : "OUT";
		for (ShipmentItems si : items) {
			var existing = movementsService.findByReferenceAndItem("Shipment", shipment.getShipmentId(), si.getItem().getItemId());
			if (cancelled) {
				if (existing != null) movementsService.delete(existing.getMovementId());
				continue;
			}
			Integer toLocationId = si.getItem().getLocation() != null ? si.getItem().getLocation().getLocationId() : null;
			if (existing != null) {
				InventoryMovementUpdateRequest up = new InventoryMovementUpdateRequest();
				up.setItemId(si.getItem().getItemId());
				up.setMovementType(moveType);
				up.setQuantity(si.getQuantity());
				up.setToLocationId(toLocationId);
				up.setReferenceType("Shipment");
				up.setReferenceId(shipment.getShipmentId());
				up.setNotes("Auto-sync for shipment #" + shipment.getShipmentNumber());
				movementsService.update(existing.getMovementId(), up);
			} else {
				InventoryMovementCreateRequest cr = new InventoryMovementCreateRequest();
				cr.setItemId(si.getItem().getItemId());
				cr.setMovementType(moveType);
				cr.setQuantity(si.getQuantity());
				cr.setToLocationId(toLocationId);
				cr.setReferenceType("Shipment");
				cr.setReferenceId(shipment.getShipmentId());
				cr.setNotes("Auto-sync for shipment #" + shipment.getShipmentNumber());
				movementsService.create(cr);
			}
		}
	}
} 