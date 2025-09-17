package com.lwms.backend.controllers;

import com.lwms.backend.dto.MaintenanceCreateRequest;
import com.lwms.backend.dto.MaintenanceSummaryDto;
import com.lwms.backend.dto.MaintenanceUpdateRequest;
import com.lwms.backend.services.MaintenanceScheduleService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MaintenanceScheduleController {

	private final MaintenanceScheduleService maintenanceService;

	public MaintenanceScheduleController(MaintenanceScheduleService maintenanceService) {
		this.maintenanceService = maintenanceService;
	}

	@GetMapping(value = {"/lwms/maintenance-schedule/api", "/maintenance-schedule/api"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MaintenanceSummaryDto>> list(@RequestParam(value = "q", required = false) String q) {
		return ResponseEntity.ok(maintenanceService.list(q));
	}

	@PostMapping(value = {"/lwms/maintenance-schedule/api", "/maintenance-schedule/api"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MaintenanceSummaryDto> create(@RequestBody MaintenanceCreateRequest req) {
		return ResponseEntity.ok(maintenanceService.create(req));
	}

	@PatchMapping(value = {"/lwms/maintenance-schedule/api/{id}", "/maintenance-schedule/api/{id}"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@PathVariable("id") Integer id, @RequestBody MaintenanceUpdateRequest req) {
		try {
			return ResponseEntity.ok(maintenanceService.update(id, req));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@DeleteMapping(value = {"/lwms/maintenance-schedule/api/{id}", "/maintenance-schedule/api/{id}"})
	public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
		maintenanceService.delete(id);
		return ResponseEntity.noContent().build();
	}
} 