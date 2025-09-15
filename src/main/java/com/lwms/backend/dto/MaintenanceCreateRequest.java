package com.lwms.backend.dto;

public class MaintenanceCreateRequest {
	private Integer equipmentId;
	private String taskDescription;
	private String maintenanceType;
	private String priority;
	private String scheduledDate;
	private Integer estimatedDuration;
	private Integer assignedTo;
	private String status;
	private String completedDate;
	private Integer actualDuration;
	private String cost;
	private String notes;
	private Integer createdBy;

	public Integer getEquipmentId() { return equipmentId; }
	public void setEquipmentId(Integer equipmentId) { this.equipmentId = equipmentId; }
	public String getTaskDescription() { return taskDescription; }
	public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
	public String getMaintenanceType() { return maintenanceType; }
	public void setMaintenanceType(String maintenanceType) { this.maintenanceType = maintenanceType; }
	public String getPriority() { return priority; }
	public void setPriority(String priority) { this.priority = priority; }
	public String getScheduledDate() { return scheduledDate; }
	public void setScheduledDate(String scheduledDate) { this.scheduledDate = scheduledDate; }
	public Integer getEstimatedDuration() { return estimatedDuration; }
	public void setEstimatedDuration(Integer estimatedDuration) { this.estimatedDuration = estimatedDuration; }
	public Integer getAssignedTo() { return assignedTo; }
	public void setAssignedTo(Integer assignedTo) { this.assignedTo = assignedTo; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
	public String getCompletedDate() { return completedDate; }
	public void setCompletedDate(String completedDate) { this.completedDate = completedDate; }
	public Integer getActualDuration() { return actualDuration; }
	public void setActualDuration(Integer actualDuration) { this.actualDuration = actualDuration; }
	public String getCost() { return cost; }
	public void setCost(String cost) { this.cost = cost; }
	public String getNotes() { return notes; }
	public void setNotes(String notes) { this.notes = notes; }
	public Integer getCreatedBy() { return createdBy; }
	public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
} 