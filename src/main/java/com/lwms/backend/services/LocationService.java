package com.lwms.backend.services;

import com.lwms.backend.dao.LocationsRepository;
import com.lwms.backend.dto.LocationCreateRequest;
import com.lwms.backend.dto.LocationSummaryDto;
import com.lwms.backend.dto.LocationUpdateRequest;
import com.lwms.backend.entities.Locations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationService {
	private final LocationsRepository locationsRepository;

	public LocationService(LocationsRepository locationsRepository) {
		this.locationsRepository = locationsRepository;
	}

	@Transactional(readOnly = true)
	public List<LocationSummaryDto> listLocations(String query) {
		List<Locations> results;
		if (StringUtils.hasText(query)) {
			String q = query.trim();
			List<Locations> textMatches = locationsRepository
					.findByZoneContainingIgnoreCaseOrAisleContainingIgnoreCaseOrRackContainingIgnoreCaseOrShelfContainingIgnoreCase(q, q, q, q);
			List<Locations> agg = new ArrayList<>(textMatches);
			// Try type match
			try {
				Locations.LocationType t = Locations.LocationType.valueOf(q.substring(0, 1).toUpperCase() + q.substring(1).toLowerCase());
				agg.addAll(locationsRepository.findByLocationType(t));
			} catch (IllegalArgumentException ignore) {}
			// Try status match
			if (q.equalsIgnoreCase("active")) agg.addAll(locationsRepository.findByIsActive(true));
			if (q.equalsIgnoreCase("inactive")) agg.addAll(locationsRepository.findByIsActive(false));
			// Distinct by id
			results = agg.stream().collect(Collectors.toMap(Locations::getLocationId, l -> l, (a,b)->a)).values().stream().collect(Collectors.toList());
		} else {
			results = locationsRepository.findAll();
		}
		return results.stream().map(this::toDto).collect(Collectors.toList());
	}

	@Transactional
	public LocationSummaryDto createLocation(LocationCreateRequest req) {
		validateCreate(req);
		Locations l = new Locations();
		l.setZone(req.getZone());
		l.setAisle(req.getAisle());
		l.setRack(req.getRack());
		l.setShelf(req.getShelf());
		l.setCapacity(req.getCapacity());
		l.setCurrentLoad(req.getCurrentLoad());
		if (StringUtils.hasText(req.getLocationType())) {
			l.setLocationType(Locations.LocationType.valueOf(req.getLocationType()));
		}
		l.setIsActive(req.getIsActive() != null ? req.getIsActive() : Boolean.TRUE);
		Locations saved = locationsRepository.save(l);
		return toDto(saved);
	}

	@Transactional
	public LocationSummaryDto updateLocation(Integer locationId, LocationUpdateRequest req) {
		Locations l = locationsRepository.findById(locationId)
				.orElseThrow(() -> new RuntimeException("Location not found: " + locationId));
		if (req.getZone() != null) l.setZone(req.getZone());
		if (req.getAisle() != null) l.setAisle(req.getAisle());
		if (req.getRack() != null) l.setRack(req.getRack());
		if (req.getShelf() != null) l.setShelf(req.getShelf());
		if (req.getCapacity() != null) l.setCapacity(req.getCapacity());
		if (req.getCurrentLoad() != null) l.setCurrentLoad(req.getCurrentLoad());
		if (req.getLocationType() != null) l.setLocationType(Locations.LocationType.valueOf(req.getLocationType()));
		if (req.getIsActive() != null) l.setIsActive(req.getIsActive());
		Locations saved = locationsRepository.save(l);
		return toDto(saved);
	}

	@Transactional
	public void deleteLocation(Integer locationId) {
		if (!locationsRepository.existsById(locationId)) {
			throw new RuntimeException("Location not found: " + locationId);
		}
		locationsRepository.deleteById(locationId);
	}

	private void validateCreate(LocationCreateRequest req) {
		if (!StringUtils.hasText(req.getZone())) throw new IllegalArgumentException("zone is required");
		if (!StringUtils.hasText(req.getAisle())) throw new IllegalArgumentException("aisle is required");
		if (!StringUtils.hasText(req.getRack())) throw new IllegalArgumentException("rack is required");
		if (!StringUtils.hasText(req.getShelf())) throw new IllegalArgumentException("shelf is required");
	}

	private String computeLocationCode(Locations l) {
		return (l.getZone() != null ? l.getZone() : "")
				+ (l.getAisle() != null ? l.getAisle() : "")
				+ "-" + (l.getRack() != null ? l.getRack() : "")
				+ "-" + (l.getShelf() != null ? l.getShelf() : "");
	}

	private LocationSummaryDto toDto(Locations l) {
		LocationSummaryDto dto = new LocationSummaryDto();
		dto.setLocationId(l.getLocationId());
		dto.setZone(l.getZone());
		dto.setAisle(l.getAisle());
		dto.setRack(l.getRack());
		dto.setShelf(l.getShelf());
		dto.setCapacity(l.getCapacity());
		dto.setCurrentLoad(l.getCurrentLoad());
		dto.setLocationType(l.getLocationType() != null ? l.getLocationType().name() : null);
		dto.setIsActive(l.getIsActive());
		dto.setLocationCode(computeLocationCode(l));
		return dto;
	}
} 