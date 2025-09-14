package com.lwms.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Equipment")
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer equipmentId;

    @Column(nullable = false)
    private String equipmentName;

    @Column(nullable = false)
    private String equipmentType;

    @Column(unique = true)
    private String serialNumber;

    private String location;

    @Enumerated(EnumType.STRING)
    private Status status = Status.Active;

    private LocalDate purchaseDate;

    private LocalDate warrantyExpiry;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Status {
        Active, Maintenance, Inactive
    }

	public Equipment() {

	}

	/**
	 * @param equipmentId
	 * @param equipmentName
	 * @param equipmentType
	 * @param serialNumber
	 * @param location
	 * @param status
	 * @param purchaseDate
	 * @param warrantyExpiry
	 * @param createdAt
	 */
	public Equipment(Integer equipmentId, String equipmentName, String equipmentType, String serialNumber,
			String location, Status status, LocalDate purchaseDate, LocalDate warrantyExpiry, LocalDateTime createdAt) {
		super();
		this.equipmentId = equipmentId;
		this.equipmentName = equipmentName;
		this.equipmentType = equipmentType;
		this.serialNumber = serialNumber;
		this.location = location;
		this.status = status;
		this.purchaseDate = purchaseDate;
		this.warrantyExpiry = warrantyExpiry;
		this.createdAt = createdAt;
	}

	public Integer getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(Integer equipmentId) {
		this.equipmentId = equipmentId;
	}

	public String getEquipmentName() {
		return equipmentName;
	}

	public void setEquipmentName(String equipmentName) {
		this.equipmentName = equipmentName;
	}

	public String getEquipmentType() {
		return equipmentType;
	}

	public void setEquipmentType(String equipmentType) {
		this.equipmentType = equipmentType;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public LocalDate getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(LocalDate purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public LocalDate getWarrantyExpiry() {
		return warrantyExpiry;
	}

	public void setWarrantyExpiry(LocalDate warrantyExpiry) {
		this.warrantyExpiry = warrantyExpiry;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}


	
}