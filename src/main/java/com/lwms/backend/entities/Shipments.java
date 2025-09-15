package com.lwms.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.JdbcTypeCode;
import java.sql.Types;

@Entity
@Table(name = "shipments", indexes = {
        @Index(name = "idx_shipments_shipment_number", columnList = "shipmentNumber"),
        @Index(name = "idx_shipments_supplier", columnList = "supplierId"),
        @Index(name = "idx_shipments_createdBy", columnList = "createdBy")
})
public class Shipments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer shipmentId;

    @Column(unique = true, nullable = false, length = 50)
    private String shipmentNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentType shipmentType;

    @Convert(converter = ShipmentStatusConverter.class)
    @JdbcTypeCode(Types.CHAR)
    @Column(columnDefinition = "enum('Planned','In Transit','Delivered','Cancelled')")
    private Status status = Status.Planned;

    @Column(nullable = false, length = 100)
    private String origin;

    @Column(nullable = false, length = 100)
    private String destination;

    private LocalDateTime expectedDeliveryDate;

    private LocalDateTime actualDeliveryDate;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalValue = BigDecimal.ZERO;

    @Column(insertable = false, updatable = false)
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

	public Shipments() {

	}

	public Shipments(Integer shipmentId, String shipmentNumber, ShipmentType shipmentType, Status status,
			String origin, String destination, LocalDateTime expectedDeliveryDate, LocalDateTime actualDeliveryDate, BigDecimal totalValue,
			LocalDateTime createdAt, Suppliers supplier, User createdBy) {
		super();
		this.shipmentId = shipmentId;
		this.shipmentNumber = shipmentNumber;
		this.shipmentType = shipmentType;
		this.status = status;
		this.origin = origin;
		this.destination = destination;
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

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
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