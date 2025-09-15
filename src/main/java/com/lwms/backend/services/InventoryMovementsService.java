package com.lwms.backend.services;

import com.lwms.backend.dao.InventoryMovementsRepository;
import com.lwms.backend.dao.InventoryRepository;
import com.lwms.backend.dao.LocationsRepository;
import com.lwms.backend.dto.InventoryMovementCreateRequest;
import com.lwms.backend.dto.InventoryMovementSummaryDto;
import com.lwms.backend.dto.InventoryMovementUpdateRequest;
import com.lwms.backend.entities.Inventory;
import com.lwms.backend.entities.InventoryMovements;
import com.lwms.backend.entities.Locations;
import com.lwms.backend.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryMovementsService {
	private final InventoryMovementsRepository movementsRepository;
	private final InventoryRepository inventoryRepository;
	private final LocationsRepository locationsRepository;

	public InventoryMovementsService(InventoryMovementsRepository movementsRepository, InventoryRepository inventoryRepository, LocationsRepository locationsRepository) {
		this.movementsRepository = movementsRepository;
		this.inventoryRepository = inventoryRepository;
		this.locationsRepository = locationsRepository;
	}

	@Transactional(readOnly = true)
	public List<InventoryMovementSummaryDto> list(String q) {
		List<InventoryMovements> results = StringUtils.hasText(q)
				? movementsRepository.findByItem_ItemCodeContainingIgnoreCase(q)
				: movementsRepository.findAll();
		return results.stream().map(this::toDto).collect(Collectors.toList());
	}

	@Transactional
	public InventoryMovementSummaryDto create(InventoryMovementCreateRequest req) {
		Inventory item = resolveItem(req.getItemId(), req.getItemCode());
		InventoryMovements m = new InventoryMovements();
		m.setItem(item);
		m.setMovementType(InventoryMovements.MovementType.valueOf(req.getMovementType()));
		m.setQuantity(req.getQuantity());
		if (req.getFromLocationId() != null) {
			Locations from = locationsRepository.findById(req.getFromLocationId()).orElseThrow(() -> new RuntimeException("Location not found: " + req.getFromLocationId()));
			m.setFromLocation(from);
		} else if (StringUtils.hasText(req.getFromLocation())) {
			Locations from = resolveLocationLabel(req.getFromLocation());
			m.setFromLocation(from);
		}
		if (req.getToLocationId() != null) {
			Locations to = locationsRepository.findById(req.getToLocationId()).orElseThrow(() -> new RuntimeException("Location not found: " + req.getToLocationId()));
			m.setToLocation(to);
		} else if (StringUtils.hasText(req.getToLocation())) {
			Locations to = resolveLocationLabel(req.getToLocation());
			m.setToLocation(to);
		}
		m.setReferenceType(req.getReferenceType());
		m.setReferenceId(req.getReferenceId());
		m.setNotes(req.getNotes());
		m.setCreatedBy(resolveCurrentUser());
		// Apply effect to inventory
		applyMovementEffect(item, m);
		InventoryMovements saved = movementsRepository.save(m);
		return toDto(saved);
	}

	@Transactional
	public InventoryMovementSummaryDto update(Integer movementId, InventoryMovementUpdateRequest req) {
		InventoryMovements m = movementsRepository.findById(movementId).orElseThrow(() -> new RuntimeException("Movement not found: " + movementId));
		// Capture previous state to revert
		Inventory prevItem = m.getItem();
		InventoryMovements.MovementType prevType = m.getMovementType();
		Integer prevQty = m.getQuantity();
		Locations prevFrom = m.getFromLocation();
		Locations prevTo = m.getToLocation();
		InventoryMovements prev = new InventoryMovements();
		prev.setItem(prevItem);
		prev.setMovementType(prevType);
		prev.setQuantity(prevQty);
		prev.setFromLocation(prevFrom);
		prev.setToLocation(prevTo);
		// Revert previous effect
		revertMovementEffect(prevItem, prev);
		// Apply updates
		if (req.getItemId() != null || StringUtils.hasText(req.getItemCode())) {
			m.setItem(resolveItem(req.getItemId(), req.getItemCode()));
		}
		if (req.getMovementType() != null) m.setMovementType(InventoryMovements.MovementType.valueOf(req.getMovementType()));
		if (req.getQuantity() != null) m.setQuantity(req.getQuantity());
		if (req.getFromLocationId() != null) {
			Locations from = locationsRepository.findById(req.getFromLocationId()).orElseThrow(() -> new RuntimeException("Location not found: " + req.getFromLocationId()));
			m.setFromLocation(from);
		} else if (StringUtils.hasText(req.getFromLocation())) {
			m.setFromLocation(resolveLocationLabel(req.getFromLocation()));
		}
		if (req.getToLocationId() != null) {
			Locations to = locationsRepository.findById(req.getToLocationId()).orElseThrow(() -> new RuntimeException("Location not found: " + req.getToLocationId()));
			m.setToLocation(to);
		} else if (StringUtils.hasText(req.getToLocation())) {
			m.setToLocation(resolveLocationLabel(req.getToLocation()));
		}
		if (req.getReferenceType() != null) m.setReferenceType(req.getReferenceType());
		if (req.getReferenceId() != null) m.setReferenceId(req.getReferenceId());
		if (req.getNotes() != null) m.setNotes(req.getNotes());
		// Apply new effect
		applyMovementEffect(m.getItem(), m);
		InventoryMovements saved = movementsRepository.save(m);
		return toDto(saved);
	}

	@Transactional
	public void delete(Integer movementId) {
		InventoryMovements m = movementsRepository.findById(movementId).orElseThrow(() -> new RuntimeException("Movement not found: " + movementId));
		// Revert effect before deleting
		revertMovementEffect(m.getItem(), m);
		movementsRepository.deleteById(movementId);
	}

	@Transactional(readOnly = true)
	public InventoryMovements findByReferenceAndItem(String referenceType, Integer referenceId, Integer itemId) {
		return movementsRepository.findFirstByReferenceTypeAndReferenceIdAndItem_ItemId(referenceType, referenceId, itemId).orElse(null);
	}

	private void applyMovementEffect(Inventory inv, InventoryMovements m) {
		if (inv == null || m == null) return;
		switch (m.getMovementType()) {
			case IN -> inv.setQuantity((inv.getQuantity() != null ? inv.getQuantity() : 0) + (m.getQuantity() != null ? m.getQuantity() : 0));
			case OUT -> inv.setQuantity((inv.getQuantity() != null ? inv.getQuantity() : 0) - (m.getQuantity() != null ? m.getQuantity() : 0));
			case TRANSFER -> {
				// no net quantity change; update location if toLocation provided
				if (m.getToLocation() != null) inv.setLocation(m.getToLocation());
			}
			case ADJUSTMENT -> inv.setQuantity((inv.getQuantity() != null ? inv.getQuantity() : 0) + (m.getQuantity() != null ? m.getQuantity() : 0));
		}
		inventoryRepository.save(inv);
	}

	private void revertMovementEffect(Inventory inv, InventoryMovements m) {
		if (inv == null || m == null) return;
		switch (m.getMovementType()) {
			case IN -> inv.setQuantity((inv.getQuantity() != null ? inv.getQuantity() : 0) - (m.getQuantity() != null ? m.getQuantity() : 0));
			case OUT -> inv.setQuantity((inv.getQuantity() != null ? inv.getQuantity() : 0) + (m.getQuantity() != null ? m.getQuantity() : 0));
			case TRANSFER -> {
				// If current location equals previous toLocation, revert to previous fromLocation
				if (m.getToLocation() != null && inv.getLocation() != null && inv.getLocation().getLocationId().equals(m.getToLocation().getLocationId())) {
					if (m.getFromLocation() != null) inv.setLocation(m.getFromLocation());
				}
			}
			case ADJUSTMENT -> inv.setQuantity((inv.getQuantity() != null ? inv.getQuantity() : 0) - (m.getQuantity() != null ? m.getQuantity() : 0));
		}
		inventoryRepository.save(inv);
	}

	private Inventory resolveItem(Integer itemId, String itemCode) {
		if (itemId != null) return inventoryRepository.findById(itemId)
				.orElseThrow(() -> new RuntimeException("Inventory item not found: " + itemId));
		if (StringUtils.hasText(itemCode)) return inventoryRepository.findByItemCode(itemCode)
				.orElseThrow(() -> new RuntimeException("Inventory item not found with code: " + itemCode));
		throw new IllegalArgumentException("itemId or itemCode is required");
	}

	private Locations resolveLocationLabel(String label) {
		String norm = normalize(label);
		return locationsRepository.findAll().stream()
				.filter(l -> normalize(computeLocationCode(l)).equals(norm))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Location not found for: " + label));
	}

	private String computeLocationCode(Locations loc) {
		String zone = loc.getZone(); String aisle = loc.getAisle(); String rack = loc.getRack(); String shelf = loc.getShelf();
		return (zone != null ? zone : "") + (aisle != null ? aisle : "") + "-" + (rack != null ? rack : "") + "-" + (shelf != null ? shelf : "");
	}

	private String normalize(String s) { return (s == null ? "" : s.replaceAll("[^A-Za-z0-9]","" ).toUpperCase()); }

	private User resolveCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof org.springframework.security.core.userdetails.User springUser)) {
			throw new RuntimeException("Unauthenticated: createdBy required");
		}
		User u = new User();
		u.setUserId(null);
		try { u.setUserId(Integer.parseInt(springUser.getUsername())); }
		catch (NumberFormatException ex) { throw new RuntimeException("Cannot resolve current user id from principal username: " + springUser.getUsername()); }
		return u;
	}

	private InventoryMovementSummaryDto toDto(InventoryMovements m) {
		InventoryMovementSummaryDto dto = new InventoryMovementSummaryDto();
		dto.setMovementId(m.getMovementId());
		dto.setItemCode(m.getItem() != null ? m.getItem().getItemCode() : null);
		dto.setMovementType(m.getMovementType() != null ? m.getMovementType().name() : null);
		dto.setQuantity(m.getQuantity());
		dto.setFromLocation(m.getFromLocation() != null ? m.getFromLocation().getZone() + m.getFromLocation().getAisle() + "-" + m.getFromLocation().getRack() + "-" + m.getFromLocation().getShelf() : null);
		dto.setToLocation(m.getToLocation() != null ? m.getToLocation().getZone() + m.getToLocation().getAisle() + "-" + m.getToLocation().getRack() + "-" + m.getToLocation().getShelf() : null);
		dto.setReferenceType(m.getReferenceType());
		dto.setReferenceId(m.getReferenceId());
		dto.setNotes(m.getNotes());
		return dto;
	}
} 