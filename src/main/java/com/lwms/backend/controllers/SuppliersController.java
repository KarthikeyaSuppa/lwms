package com.lwms.backend.controllers;

import com.lwms.backend.dto.SupplierCreateRequest;
import com.lwms.backend.dto.SupplierSummaryDto;
import com.lwms.backend.dto.SupplierUpdateRequest;
import com.lwms.backend.services.SupplierService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SuppliersController {

	private final SupplierService supplierService;

	public SuppliersController(SupplierService supplierService) {
		this.supplierService = supplierService;
	}

	@GetMapping(value = {"/lwms/suppliers/api", "/suppliers/api"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<SupplierSummaryDto>> listSuppliers(
			@RequestParam(value = "q", required = false) String query,
			@RequestParam(value = "status", required = false) String status
	) {
		Boolean active = null;
		if (status != null) {
			if ("active".equalsIgnoreCase(status)) active = Boolean.TRUE;
			else if ("inactive".equalsIgnoreCase(status)) active = Boolean.FALSE;
		}
		return ResponseEntity.ok(supplierService.listSuppliers(query, active));
	}

	@PostMapping(value = {"/lwms/suppliers/api", "/suppliers/api"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SupplierSummaryDto> createSupplier(@RequestBody SupplierCreateRequest request) {
		SupplierSummaryDto created = supplierService.createSupplier(request);
		return ResponseEntity.ok(created);
	}

	@PatchMapping(value = {"/lwms/suppliers/api/{id}", "/suppliers/api/{id}"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SupplierSummaryDto> updateSupplier(@PathVariable("id") Integer id, @RequestBody SupplierUpdateRequest request) {
		return ResponseEntity.ok(supplierService.updateSupplier(id, request));
	}

	@DeleteMapping(value = {"/lwms/suppliers/api/{id}", "/suppliers/api/{id}"})
	public ResponseEntity<Void> deleteSupplier(@PathVariable("id") Integer id) {
		supplierService.deleteSupplier(id);
		return ResponseEntity.noContent().build();
	}
} 