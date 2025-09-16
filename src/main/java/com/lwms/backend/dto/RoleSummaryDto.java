package com.lwms.backend.dto;

public class RoleSummaryDto {
	private Integer roleId;
	private String roleName;
	private String description;
	private String permissions;

	public Integer getRoleId() { return roleId; }
	public void setRoleId(Integer roleId) { this.roleId = roleId; }
	public String getRoleName() { return roleName; }
	public void setRoleName(String roleName) { this.roleName = roleName; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public String getPermissions() { return permissions; }
	public void setPermissions(String permissions) { this.permissions = permissions; }
} 