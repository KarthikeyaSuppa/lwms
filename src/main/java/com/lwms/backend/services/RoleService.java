package com.lwms.backend.services;

import com.lwms.backend.dao.RoleRepository;

import com.lwms.backend.dto.RoleSummaryDto;
import com.lwms.backend.dto.RoleUpdateRequest;
import com.lwms.backend.entities.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {
	private final RoleRepository roleRepository;

	public RoleService(RoleRepository roleRepository) { this.roleRepository = roleRepository; }

	@Transactional(readOnly = true)
	public List<RoleSummaryDto> list(String q) {
		List<Role> roles = roleRepository.findAll();
		if (StringUtils.hasText(q)) {
			String query = q.toLowerCase();
			roles = roles.stream().filter(r ->
					(r.getRoleName() != null && r.getRoleName().toLowerCase().contains(query)) ||
					(r.getDescription() != null && r.getDescription().toLowerCase().contains(query)) ||
					(r.getPermissions() != null && r.getPermissions().toLowerCase().contains(query))
			).collect(Collectors.toList());
		}
		return roles.stream().map(this::toDto).collect(Collectors.toList());
	}


	@Transactional
	public RoleSummaryDto update(Integer roleId, RoleUpdateRequest req) {
		Role r = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found: " + roleId));
		if (req.getRoleName() != null) r.setRoleName(req.getRoleName());
		if (req.getDescription() != null) r.setDescription(req.getDescription());
		if (req.getPermissions() != null) r.setPermissions(req.getPermissions());
		return toDto(roleRepository.save(r));
	}

	@Transactional
	public void delete(Integer roleId) {
		if (!roleRepository.existsById(roleId)) throw new RuntimeException("Role not found: " + roleId);
		roleRepository.deleteById(roleId);
	}

	private RoleSummaryDto toDto(Role r) {
		RoleSummaryDto dto = new RoleSummaryDto();
		dto.setRoleId(r.getRoleId());
		dto.setRoleName(r.getRoleName());
		dto.setDescription(r.getDescription());
		dto.setPermissions(r.getPermissions());
		return dto;
	}
} 