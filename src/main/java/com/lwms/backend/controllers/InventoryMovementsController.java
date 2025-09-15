package com.lwms.backend.controllers;

import com.lwms.backend.dto.InventoryMovementCreateRequest;
import com.lwms.backend.dto.InventoryMovementSummaryDto;
import com.lwms.backend.dto.InventoryMovementUpdateRequest;
import com.lwms.backend.services.InventoryMovementsService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InventoryMovementsController {

	private final InventoryMovementsService movementsService;

	public InventoryMovementsController(InventoryMovementsService movementsService) {
		this.movementsService = movementsService;
	}

	@GetMapping(value = {"/lwms/inventory-movements/api", "/inventory-movements/api"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<InventoryMovementSummaryDto>> list(@RequestParam(value = "q", required = false) String q) {
		return ResponseEntity.ok(movementsService.list(q));
	}

	@PostMapping(value = {"/lwms/inventory-movements/api", "/inventory-movements/api"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<InventoryMovementSummaryDto> create(@RequestBody InventoryMovementCreateRequest req) {
		return ResponseEntity.ok(movementsService.create(req));
	}

	@PatchMapping(value = {"/lwms/inventory-movements/api/{id}", "/inventory-movements/api/{id}"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<InventoryMovementSummaryDto> update(@PathVariable("id") Integer id, @RequestBody InventoryMovementUpdateRequest req) {
		return ResponseEntity.ok(movementsService.update(id, req));
	}

	@DeleteMapping(value = {"/lwms/inventory-movements/api/{id}", "/inventory-movements/api/{id}"})
	public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
		movementsService.delete(id);
		return ResponseEntity.noContent().build();
	}
} 