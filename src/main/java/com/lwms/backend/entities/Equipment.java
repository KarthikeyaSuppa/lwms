package com.lwms.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "equipment", indexes = {
    @Index(name = "idx_equipment_location", columnList = "locationId")
})
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer equipmentId;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String equipmentName;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String equipmentType;

    @Size(max = 100)
    @Column(unique = true, length = 100)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    private Status status = Status.Active;

    private LocalDate purchaseDate;

    private LocalDate warrantyExpiry;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locationId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Locations location;

    public enum Status {
        Active, Maintenance, Inactive
    }

	public Equipment() {

	}

	public Equipment(Integer equipmentId, String equipmentName, String equipmentType, String serialNumber,
			Status status, LocalDate purchaseDate, LocalDate warrantyExpiry, LocalDateTime createdAt, Locations location) {
		super();
		this.equipmentId = equipmentId;
		this.equipmentName = equipmentName;
		this.equipmentType = equipmentType;
		this.serialNumber = serialNumber;
		this.status = status;
		this.purchaseDate = purchaseDate;
		this.warrantyExpiry = warrantyExpiry;
		this.createdAt = createdAt;
		this.location = location;
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

	public Locations getLocation() {
		return location;
	}

	public void setLocation(Locations location) {
		this.location = location;
	}


}