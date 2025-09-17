package com.lwms.backend.services;

import com.lwms.backend.dao.EquipmentRepository;
import com.lwms.backend.dao.LocationsRepository;
import com.lwms.backend.dto.EquipmentCreateRequest;
import com.lwms.backend.dto.EquipmentSummaryDto;
import com.lwms.backend.dto.EquipmentUpdateRequest;
import com.lwms.backend.entities.Equipment;
import com.lwms.backend.entities.Locations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EquipmentService {
	private final EquipmentRepository equipmentRepository;
	private final LocationsRepository locationsRepository;

	public EquipmentService(EquipmentRepository equipmentRepository, LocationsRepository locationsRepository) {
		this.equipmentRepository = equipmentRepository;
		this.locationsRepository = locationsRepository;
	}

	@Transactional(readOnly = true)
	public List<EquipmentSummaryDto> listEquipment(String query) {
		List<Equipment> results;
		if (StringUtils.hasText(query)) {
			String q = query.trim();
			List<Equipment> base = equipmentRepository
					.findByEquipmentNameContainingIgnoreCaseOrEquipmentTypeContainingIgnoreCaseOrSerialNumberContainingIgnoreCase(q, q, q);
			List<Equipment> agg = new ArrayList<>(base);
			// status match
			try {
				Equipment.Status st = Equipment.Status.valueOf(q.substring(0, 1).toUpperCase() + q.substring(1).toLowerCase());
				agg.addAll(equipmentRepository.findAll().stream().filter(e -> e.getStatus() == st).collect(Collectors.toList()));
			} catch (IllegalArgumentException ignore) {}
			// de-duplicate by id
			results = new ArrayList<>(Map.copyOf(agg.stream().collect(Collectors.toMap(Equipment::getEquipmentId, e -> e, (a,b)->a))).values());
		} else {
			results = equipmentRepository.findAll();
		}
		return results.stream().map(this::toDto).collect(Collectors.toList());
	}

	@Transactional
	public EquipmentSummaryDto createEquipment(EquipmentCreateRequest req) {
		validateCreate(req);
		Equipment e = new Equipment();
		e.setEquipmentName(req.getEquipmentName());
		e.setEquipmentType(req.getEquipmentType());
		e.setSerialNumber(req.getSerialNumber());
		if (StringUtils.hasText(req.getStatus())) e.setStatus(Equipment.Status.valueOf(req.getStatus()));
		if (StringUtils.hasText(req.getPurchaseDate())) e.setPurchaseDate(LocalDate.parse(req.getPurchaseDate()));
		if (StringUtils.hasText(req.getWarrantyExpiry())) e.setWarrantyExpiry(LocalDate.parse(req.getWarrantyExpiry()));
		// Link location by ID if provided
		if (req.getLocationId() != null) {
			Locations loc = locationsRepository.findById(req.getLocationId())
					.orElseThrow(() -> new IllegalArgumentException("Invalid locationId: " + req.getLocationId()))
			;
			e.setLocation(loc);
		}
		Equipment saved = equipmentRepository.save(e);
		return toDtoWithLocationLabel(saved, req.getLocation());
	}

	@Transactional
	public EquipmentSummaryDto updateEquipment(Integer equipmentId, EquipmentUpdateRequest req) {
		Equipment e = equipmentRepository.findById(equipmentId)
				.orElseThrow(() -> new RuntimeException("Equipment not found: " + equipmentId));
		if (req.getEquipmentName() != null) e.setEquipmentName(req.getEquipmentName());
		if (req.getEquipmentType() != null) e.setEquipmentType(req.getEquipmentType());
		if (req.getSerialNumber() != null) e.setSerialNumber(req.getSerialNumber());
		if (req.getStatus() != null) e.setStatus(Equipment.Status.valueOf(req.getStatus()));
		if (req.getPurchaseDate() != null) e.setPurchaseDate(LocalDate.parse(req.getPurchaseDate()));
		if (req.getWarrantyExpiry() != null) e.setWarrantyExpiry(LocalDate.parse(req.getWarrantyExpiry()));
		if (req.getLocationId() != null) {
			Locations loc = locationsRepository.findById(req.getLocationId())
					.orElseThrow(() -> new IllegalArgumentException("Invalid locationId: " + req.getLocationId()));
			e.setLocation(loc);
		}
		Equipment saved = equipmentRepository.save(e);
		return toDtoWithLocationLabel(saved, req.getLocation());
	}

	@Transactional
	public void deleteEquipment(Integer equipmentId) {
		if (!equipmentRepository.existsById(equipmentId)) {
			throw new RuntimeException("Equipment not found: " + equipmentId);
		}
		equipmentRepository.deleteById(equipmentId);
	}

	private void validateCreate(EquipmentCreateRequest req) {
		if (!StringUtils.hasText(req.getEquipmentName())) throw new IllegalArgumentException("equipmentName is required");
		if (!StringUtils.hasText(req.getEquipmentType())) throw new IllegalArgumentException("equipmentType is required");
	}

	private EquipmentSummaryDto toDto(Equipment e) {
		EquipmentSummaryDto dto = new EquipmentSummaryDto();
		dto.setEquipmentId(e.getEquipmentId());
		dto.setEquipmentName(e.getEquipmentName());
		dto.setEquipmentType(e.getEquipmentType());
		dto.setSerialNumber(e.getSerialNumber());
		dto.setStatus(e.getStatus() != null ? e.getStatus().name() : null);
		dto.setPurchaseDate(e.getPurchaseDate() != null ? e.getPurchaseDate().toString() : null);
		dto.setWarrantyExpiry(e.getWarrantyExpiry() != null ? e.getWarrantyExpiry().toString() : null);
		// Populate location label if linked
		Locations loc = e.getLocation();
		if (loc != null) {
			String label = (loc.getZone() != null ? (loc.getZone()) : "")
					+ (loc.getAisle() != null ? (loc.getAisle()) : "")
					+ (loc.getRack() != null ? ("-" + loc.getRack()) : "")
					+ (loc.getShelf() != null ? ("-" + loc.getShelf()) : "");
			dto.setLocation(label.trim());
		}
		return dto;
	}

	private EquipmentSummaryDto toDtoWithLocationLabel(Equipment e, String locationLabel) {
		EquipmentSummaryDto dto = toDto(e);
		if (!StringUtils.hasText(dto.getLocation())) {
			dto.setLocation(locationLabel); // UI supplies free-text location
		}
		return dto;
	}
} 