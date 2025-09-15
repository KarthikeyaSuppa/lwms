package com.lwms.backend.controllers;

import com.lwms.backend.dto.InventoryCreateRequest;
import com.lwms.backend.dto.InventorySummaryDto;
import com.lwms.backend.dto.InventoryUpdateRequest;
import com.lwms.backend.services.InventoryService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InventoryController {

	private final InventoryService inventoryService;

	public InventoryController(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	@GetMapping(value = {"/lwms/inventory/api", "/inventory/api"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<InventorySummaryDto>> listInventory(@RequestParam(value = "q", required = false) String query) {
		return ResponseEntity.ok(inventoryService.listInventory(query));
	}

	@PostMapping(value = {"/lwms/inventory/api", "/inventory/api"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<InventorySummaryDto> create(@RequestBody InventoryCreateRequest req) {
		return ResponseEntity.ok(inventoryService.create(req));
	}

	@PatchMapping(value = {"/lwms/inventory/api/{id}", "/inventory/api/{id}"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<InventorySummaryDto> update(@PathVariable("id") Integer id, @RequestBody InventoryUpdateRequest req) {
		return ResponseEntity.ok(inventoryService.update(id, req));
	}

	@DeleteMapping(value = {"/lwms/inventory/api/{id}", "/inventory/api/{id}"})
	public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
		inventoryService.delete(id);
		return ResponseEntity.noContent().build();
	}
} 