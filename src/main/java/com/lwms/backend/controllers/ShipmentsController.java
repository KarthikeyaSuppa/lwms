package com.lwms.backend.controllers;

import com.lwms.backend.dto.ShipmentCreateRequest;
import com.lwms.backend.dto.ShipmentSummaryDto;
import com.lwms.backend.dto.ShipmentUpdateRequest;
import com.lwms.backend.services.ShipmentService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShipmentsController {

	private final ShipmentService shipmentService;

	public ShipmentsController(ShipmentService shipmentService) {
		this.shipmentService = shipmentService;
	}

	@GetMapping(value = {"/lwms/shipments/api", "/shipments/api"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ShipmentSummaryDto>> listShipments(@RequestParam(value = "q", required = false) String q) {
		return ResponseEntity.ok(shipmentService.listShipments(q));
	}

	@PostMapping(value = {"/lwms/shipments/api", "/shipments/api"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ShipmentSummaryDto> createShipment(@RequestBody ShipmentCreateRequest req) {
		return ResponseEntity.ok(shipmentService.createShipment(req));
	}

	@PatchMapping(value = {"/lwms/shipments/api/{id}", "/shipments/api/{id}"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ShipmentSummaryDto> updateShipment(@PathVariable("id") Integer id, @RequestBody ShipmentUpdateRequest req) {
		return ResponseEntity.ok(shipmentService.updateShipment(id, req));
	}

	@DeleteMapping(value = {"/lwms/shipments/api/{id}", "/shipments/api/{id}"})
	public ResponseEntity<Void> deleteShipment(@PathVariable("id") Integer id) {
		shipmentService.deleteShipment(id);
		return ResponseEntity.noContent().build();
	}
} 