package com.lwms.backend.controllers;

import com.lwms.backend.dto.LocationCreateRequest;
import com.lwms.backend.dto.LocationSummaryDto;
import com.lwms.backend.dto.LocationUpdateRequest;
import com.lwms.backend.services.LocationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class LocationsController {

	private final LocationService locationService;

	public LocationsController(LocationService locationService) {
		this.locationService = locationService;
	}

	@GetMapping(value = {"/lwms/locations/api", "/locations/api"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LocationSummaryDto>> listLocations(@RequestParam(value = "q", required = false) String query) {
		return ResponseEntity.ok(locationService.listLocations(query));
	}

	@PostMapping(value = {"/lwms/locations/api", "/locations/api"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LocationSummaryDto> createLocation(@RequestBody LocationCreateRequest request) {
		LocationSummaryDto created = locationService.createLocation(request);
		return ResponseEntity.ok(created);
	}

	@PatchMapping(value = {"/lwms/locations/api/{id}", "/locations/api/{id}"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LocationSummaryDto> updateLocation(@PathVariable("id") Integer id, @RequestBody LocationUpdateRequest request) {
		return ResponseEntity.ok(locationService.updateLocation(id, request));
	}

	@DeleteMapping(value = {"/lwms/locations/api/{id}", "/locations/api/{id}"})
	public ResponseEntity<Void> deleteLocation(@PathVariable("id") Integer id) {
		locationService.deleteLocation(id);
		return ResponseEntity.noContent().build();
	}
} 