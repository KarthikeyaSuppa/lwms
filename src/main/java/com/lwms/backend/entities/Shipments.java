package com.lwms.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Shipments", indexes = {
		@Index(name = "idx_shipments_shipment_number", columnList = "shipmentNumber")
})
public class Shipments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer shipmentId;

    @Column(unique = true, nullable = false)
    private String shipmentNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentType shipmentType;

    @Enumerated(EnumType.STRING)
    private Status status = Status.Planned;

    private LocalDateTime expectedDeliveryDate;

    private LocalDateTime actualDeliveryDate;

    @Column(precision = 19, scale = 2)
    private BigDecimal totalValue = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplierId")
    @JsonIgnore
    private Suppliers supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy", nullable = false)
    @JsonIgnore
    private User createdBy;

    public enum ShipmentType {
        Inbound, Outbound
    }

    public enum Status {
        Planned, In_Transit, Delivered, Cancelled
    }

	/**
	 * 
	 */
	public Shipments() {

	}


	public Shipments(Integer shipmentId, String shipmentNumber, ShipmentType shipmentType, Status status,
			LocalDateTime expectedDeliveryDate, LocalDateTime actualDeliveryDate, BigDecimal totalValue,
			LocalDateTime createdAt, Suppliers supplier, User createdBy) {
		super();
		this.shipmentId = shipmentId;
		this.shipmentNumber = shipmentNumber;
		this.shipmentType = shipmentType;
		this.status = status;
		this.expectedDeliveryDate = expectedDeliveryDate;
		this.actualDeliveryDate = actualDeliveryDate;
		this.totalValue = totalValue;
		this.createdAt = createdAt;
		this.supplier = supplier;
		this.createdBy = createdBy;
	}

	public Integer getShipmentId() {
		return shipmentId;
	}

	public void setShipmentId(Integer shipmentId) {
		this.shipmentId = shipmentId;
	}

	public String getShipmentNumber() {
		return shipmentNumber;
	}

	public void setShipmentNumber(String shipmentNumber) {
		this.shipmentNumber = shipmentNumber;
	}

	public ShipmentType getShipmentType() {
		return shipmentType;
	}

	public void setShipmentType(ShipmentType shipmentType) {
		this.shipmentType = shipmentType;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public LocalDateTime getExpectedDeliveryDate() {
		return expectedDeliveryDate;
	}

	public void setExpectedDeliveryDate(LocalDateTime expectedDeliveryDate) {
		this.expectedDeliveryDate = expectedDeliveryDate;
	}

	public LocalDateTime getActualDeliveryDate() {
		return actualDeliveryDate;
	}

	public void setActualDeliveryDate(LocalDateTime actualDeliveryDate) {
		this.actualDeliveryDate = actualDeliveryDate;
	}

	public BigDecimal getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Suppliers getSupplier() {
		return supplier;
	}

	public void setSupplier(Suppliers supplier) {
		this.supplier = supplier;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}


}