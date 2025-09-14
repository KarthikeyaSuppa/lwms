package com.lwms.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "MaintenanceSchedule")
public class MaintenanceSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scheduleId;

    @Column(nullable = false)
    private String taskDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceType maintenanceType;

    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.Medium;

    @Column(nullable = false)
    private LocalDateTime scheduledDate;

    private Integer estimatedDuration = 60;

    @Enumerated(EnumType.STRING)
    private Status status = Status.Scheduled;

    private LocalDateTime completedDate;

    private Integer actualDuration;

    @Column(precision = 10, scale = 2)
    private BigDecimal cost = BigDecimal.ZERO;

    private String notes;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "equipmentId", nullable = false)
    private Equipment equipment;

    @ManyToOne
    @JoinColumn(name = "assignedTo")
    private User assignedTo;

    @ManyToOne
    @JoinColumn(name = "createdBy", nullable = false)
    private User createdBy;

    public enum MaintenanceType {
        Preventive, Corrective, Predictive
    }

    public enum Priority {
        Low, Medium, High, Critical
    }

    public enum Status {
        Scheduled, In_Progress, Completed, Cancelled
    }

	/**
	 * 
	 */
	public MaintenanceSchedule() {

	}

	public MaintenanceSchedule(Integer scheduleId, String taskDescription, MaintenanceType maintenanceType,
			Priority priority, LocalDateTime scheduledDate, Integer estimatedDuration, Status status,
			LocalDateTime completedDate, Integer actualDuration, BigDecimal cost, String notes, LocalDateTime createdAt,
			Equipment equipment, User assignedTo, User createdBy) {
		super();
		this.scheduleId = scheduleId;
		this.taskDescription = taskDescription;
		this.maintenanceType = maintenanceType;
		this.priority = priority;
		this.scheduledDate = scheduledDate;
		this.estimatedDuration = estimatedDuration;
		this.status = status;
		this.completedDate = completedDate;
		this.actualDuration = actualDuration;
		this.cost = cost;
		this.notes = notes;
		this.createdAt = createdAt;
		this.equipment = equipment;
		this.assignedTo = assignedTo;
		this.createdBy = createdBy;
	}

	public Integer getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Integer scheduleId) {
		this.scheduleId = scheduleId;
	}

	public String getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public MaintenanceType getMaintenanceType() {
		return maintenanceType;
	}

	public void setMaintenanceType(MaintenanceType maintenanceType) {
		this.maintenanceType = maintenanceType;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public LocalDateTime getScheduledDate() {
		return scheduledDate;
	}

	public void setScheduledDate(LocalDateTime scheduledDate) {
		this.scheduledDate = scheduledDate;
	}

	public Integer getEstimatedDuration() {
		return estimatedDuration;
	}

	public void setEstimatedDuration(Integer estimatedDuration) {
		this.estimatedDuration = estimatedDuration;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public LocalDateTime getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(LocalDateTime completedDate) {
		this.completedDate = completedDate;
	}

	public Integer getActualDuration() {
		return actualDuration;
	}

	public void setActualDuration(Integer actualDuration) {
		this.actualDuration = actualDuration;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Equipment getEquipment() {
		return equipment;
	}

	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}

	public User getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(User assignedTo) {
		this.assignedTo = assignedTo;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}


}