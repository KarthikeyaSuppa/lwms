package com.lwms.backend.controllers;

import com.lwms.backend.dto.ShipmentCreateRequest;
import com.lwms.backend.dto.ShipmentSummaryDto;
import com.lwms.backend.dto.ShipmentUpdateRequest;
import com.lwms.backend.services.ShipmentService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ShipmentsController
 * Does: Expose REST endpoints for listing, creating, updating, and deleting shipments.
 * Input: Query param q for listing; JSON bodies for create/update.
 * Output: JSON responses with ShipmentSummaryDto or 204 on delete.
 */
@RestController
public class ShipmentsController {

	private final ShipmentService shipmentService;

	public ShipmentsController(ShipmentService shipmentService) {
		this.shipmentService = shipmentService;
	}

	/**
	 * Does: List shipments filtered by optional text query.
	 * Input: q (optional). Paths: /lwms/shipments/api or /shipments/api.
	 * Output: 200 OK with List<ShipmentSummaryDto>.
	 */
	@GetMapping(value = {"/lwms/shipments/api", "/shipments/api"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ShipmentSummaryDto>> listShipments(@RequestParam(value = "q", required = false) String q) {
		return ResponseEntity.ok(shipmentService.listShipments(q));
	}

	/**
	 * Does: Create a new shipment.
	 * Input: JSON ShipmentCreateRequest. Paths: /lwms/shipments/api or /shipments/api.
	 * Output: 200 OK with created ShipmentSummaryDto.
	 */
	@PostMapping(value = {"/lwms/shipments/api", "/shipments/api"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ShipmentSummaryDto> createShipment(@RequestBody ShipmentCreateRequest req) {
		return ResponseEntity.ok(shipmentService.createShipment(req));
	}

	/**
	 * Does: Update an existing shipment by id.
	 * Input: id path variable, JSON ShipmentUpdateRequest. Paths: /lwms/shipments/api/{id} or /shipments/api/{id}.
	 * Output: 200 OK with updated ShipmentSummaryDto.
	 */
	@PatchMapping(value = {"/lwms/shipments/api/{id}", "/shipments/api/{id}"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ShipmentSummaryDto> updateShipment(@PathVariable("id") Integer id, @RequestBody ShipmentUpdateRequest req) {
		return ResponseEntity.ok(shipmentService.updateShipment(id, req));
	}

	/**
	 * Does: Delete a shipment by id.
	 * Input: id path variable. Paths: /lwms/shipments/api/{id} or /shipments/api/{id}.
	 * Output: 204 No Content on success.
	 */
	@DeleteMapping(value = {"/lwms/shipments/api/{id}", "/shipments/api/{id}"})
	public ResponseEntity<Void> deleteShipment(@PathVariable("id") Integer id) {
		shipmentService.deleteShipment(id);
		return ResponseEntity.noContent().build();
	}
} 