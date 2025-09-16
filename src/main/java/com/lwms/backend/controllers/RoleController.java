package com.lwms.backend.controllers;

import com.lwms.backend.dto.RoleCreateRequest;
import com.lwms.backend.dto.RoleSummaryDto;
import com.lwms.backend.dto.RoleUpdateRequest;
import com.lwms.backend.services.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/roles/api")
@CrossOrigin
public class RoleController {
	private final RoleService roleService;

	public RoleController(RoleService roleService) { this.roleService = roleService; }

	@GetMapping
	public List<RoleSummaryDto> list(@RequestParam(value = "q", required = false) String q) {
		return roleService.list(q);
	}

	@PostMapping
	public ResponseEntity<RoleSummaryDto> create(@RequestBody RoleCreateRequest req) {
		RoleSummaryDto created = roleService.create(req);
		return ResponseEntity.created(URI.create("/roles/api/" + created.getRoleId())).body(created);
	}

	@PatchMapping("/{roleId}")
	public RoleSummaryDto update(@PathVariable Integer roleId, @RequestBody RoleUpdateRequest req) {
		return roleService.update(roleId, req);
	}

	@DeleteMapping("/{roleId}")
	public ResponseEntity<Void> delete(@PathVariable Integer roleId) {
		roleService.delete(roleId);
		return ResponseEntity.noContent().build();
	}
} 