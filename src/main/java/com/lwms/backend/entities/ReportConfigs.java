package com.lwms.backend.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;

@Entity
@Table(name = "ReportConfigs")
public class ReportConfigs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer configId;

    @Column(nullable = false)
    private String reportName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType;

    @JdbcTypeCode(SqlTypes.JSON)
    private String parameters; // Stored as a JSON string

    @Enumerated(EnumType.STRING)
    private Schedule schedule = Schedule.None;

    private Boolean isActive = true;

    @Column(updatable = false)
    private LocalDateTime generatedOn = LocalDateTime.now();

    private String details;

    @ManyToOne
    @JoinColumn(name = "createdBy", nullable = false)
    private User createdBy;

    public enum ReportType {
        Inventory, Shipment, Maintenance, Performance
    }

    public enum Schedule {
        None, Daily, Weekly, Monthly
    }

	/**
	 * 
	 */
	public ReportConfigs() {

	}

	public ReportConfigs(Integer configId, String reportName, ReportType reportType, String parameters,
			Schedule schedule, Boolean isActive, LocalDateTime generatedOn, String details, User createdBy) {
		super();
		this.configId = configId;
		this.reportName = reportName;
		this.reportType = reportType;
		this.parameters = parameters;
		this.schedule = schedule;
		this.isActive = isActive;
		this.generatedOn = generatedOn;
		this.details = details;
		this.createdBy = createdBy;
	}

	public Integer getConfigId() {
		return configId;
	}

	public void setConfigId(Integer configId) {
		this.configId = configId;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public ReportType getReportType() {
		return reportType;
	}

	public void setReportType(ReportType reportType) {
		this.reportType = reportType;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public LocalDateTime getGeneratedOn() {
		return generatedOn;
	}

	public void setGeneratedOn(LocalDateTime generatedOn) {
		this.generatedOn = generatedOn;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}


}