package com.lwms.backend.controllers;

import com.lwms.backend.dto.EquipmentCreateRequest;
import com.lwms.backend.dto.EquipmentSummaryDto;
import com.lwms.backend.dto.EquipmentUpdateRequest;
import com.lwms.backend.services.EquipmentService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EquipmentController {

	private final EquipmentService equipmentService;

	public EquipmentController(EquipmentService equipmentService) {
		this.equipmentService = equipmentService;
	}

	@GetMapping(value = {"/lwms/equipment/api", "/equipment/api"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<EquipmentSummaryDto>> listEquipment(@RequestParam(value = "q", required = false) String query) {
		return ResponseEntity.ok(equipmentService.listEquipment(query));
	}

	@PostMapping(value = {"/lwms/equipment/api", "/equipment/api"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EquipmentSummaryDto> createEquipment(@RequestBody EquipmentCreateRequest request) {
		EquipmentSummaryDto created = equipmentService.createEquipment(request);
		return ResponseEntity.ok(created);
	}

	@PatchMapping(value = {"/lwms/equipment/api/{id}", "/equipment/api/{id}"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EquipmentSummaryDto> updateEquipment(@PathVariable("id") Integer id, @RequestBody EquipmentUpdateRequest request) {
		return ResponseEntity.ok(equipmentService.updateEquipment(id, request));
	}

	@DeleteMapping(value = {"/lwms/equipment/api/{id}", "/equipment/api/{id}"})
	public ResponseEntity<Void> deleteEquipment(@PathVariable("id") Integer id) {
		equipmentService.deleteEquipment(id);
		return ResponseEntity.noContent().build();
	}
} 