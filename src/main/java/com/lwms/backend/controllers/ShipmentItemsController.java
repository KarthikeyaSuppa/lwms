package com.lwms.backend.controllers;

import com.lwms.backend.dto.ShipmentItemCreateRequest;
import com.lwms.backend.dto.ShipmentItemSummaryDto;
import com.lwms.backend.dto.ShipmentItemUpdateRequest;
import com.lwms.backend.services.ShipmentItemsService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShipmentItemsController {

	private final ShipmentItemsService shipmentItemsService;

	public ShipmentItemsController(ShipmentItemsService shipmentItemsService) {
		this.shipmentItemsService = shipmentItemsService;
	}

	@GetMapping(value = {"/lwms/shipment-items/api/by-shipment-id/{shipmentId}", "/shipment-items/api/by-shipment-id/{shipmentId}"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ShipmentItemSummaryDto>> listByShipmentId(@PathVariable("shipmentId") Integer shipmentId) {
		return ResponseEntity.ok(shipmentItemsService.listByShipmentId(shipmentId));
	}

	@GetMapping(value = {"/lwms/shipment-items/api/by-shipment-number/{shipmentNumber}", "/shipment-items/api/by-shipment-number/{shipmentNumber}"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ShipmentItemSummaryDto>> listByShipmentNumber(@PathVariable("shipmentNumber") String shipmentNumber) {
		return ResponseEntity.ok(shipmentItemsService.listByShipmentNumber(shipmentNumber));
	}

	@GetMapping(value = {"/lwms/shipment-items/api", "/shipment-items/api"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ShipmentItemSummaryDto>> listAll(@RequestParam(value = "shipmentNumber", required = false) String shipmentNumber) {
		if (shipmentNumber != null && !shipmentNumber.isBlank()) {
			return ResponseEntity.ok(shipmentItemsService.listByShipmentNumber(shipmentNumber));
		}
		return ResponseEntity.ok(shipmentItemsService.listAll());
	}

	@PostMapping(value = {"/lwms/shipment-items/api", "/shipment-items/api"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> create(@RequestBody ShipmentItemCreateRequest req) {
		try {
			return ResponseEntity.ok(shipmentItemsService.create(req));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PatchMapping(value = {"/lwms/shipment-items/api/{id}", "/shipment-items/api/{id}"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ShipmentItemSummaryDto> update(@PathVariable("id") Integer id, @RequestBody ShipmentItemUpdateRequest req) {
		return ResponseEntity.ok(shipmentItemsService.update(id, req));
	}

	@DeleteMapping(value = {"/lwms/shipment-items/api/{id}", "/shipment-items/api/{id}"})
	public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
		shipmentItemsService.delete(id);
		return ResponseEntity.noContent().build();
	}
} 