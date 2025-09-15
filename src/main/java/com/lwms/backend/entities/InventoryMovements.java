package com.lwms.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.JdbcTypeCode;
import java.sql.Types;

@Entity
@Table(name = "inventorymovements", indexes = {
        @Index(name = "idx_inv_mov_item", columnList = "itemId"),
        @Index(name = "idx_inv_mov_ref", columnList = "referenceId"),
        @Index(name = "idx_inv_mov_from", columnList = "fromLocationId"),
        @Index(name = "idx_inv_mov_to", columnList = "toLocationId")
})
public class InventoryMovements {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer movementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itemId", nullable = false)
    private Inventory item;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MovementType movementType;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fromLocationId")
    private Locations fromLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toLocationId")
    private Locations toLocation;

    @JdbcTypeCode(Types.CHAR)
    @Column(nullable = false, columnDefinition = "enum('Shipment','Transfer','Adjustment','Sale')")
    private String referenceType;

    @Column
    private Integer referenceId;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy", nullable = false)
    @JsonIgnore
    private User createdBy;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum MovementType { IN, OUT, TRANSFER, ADJUSTMENT }

	public InventoryMovements() {

	}

	public InventoryMovements(Integer movementId, Inventory item, MovementType movementType, Integer quantity,
			Locations fromLocation, Locations toLocation, String referenceType, Integer referenceId, String notes,
			User createdBy, LocalDateTime createdAt) {
		super();
		this.movementId = movementId;
		this.item = item;
		this.movementType = movementType;
		this.quantity = quantity;
		this.fromLocation = fromLocation;
		this.toLocation = toLocation;
		this.referenceType = referenceType;
		this.referenceId = referenceId;
		this.notes = notes;
		this.createdBy = createdBy;
		this.createdAt = createdAt;
	}

	public Integer getMovementId() { return movementId; }
	public void setMovementId(Integer movementId) { this.movementId = movementId; }
	public Inventory getItem() { return item; }
	public void setItem(Inventory item) { this.item = item; }
	public MovementType getMovementType() { return movementType; }
	public void setMovementType(MovementType movementType) { this.movementType = movementType; }
	public Integer getQuantity() { return quantity; }
	public void setQuantity(Integer quantity) { this.quantity = quantity; }
	public Locations getFromLocation() { return fromLocation; }
	public void setFromLocation(Locations fromLocation) { this.fromLocation = fromLocation; }
	public Locations getToLocation() { return toLocation; }
	public void setToLocation(Locations toLocation) { this.toLocation = toLocation; }
	public String getReferenceType() { return referenceType; }
	public void setReferenceType(String referenceType) { this.referenceType = referenceType; }
	public Integer getReferenceId() { return referenceId; }
	public void setReferenceId(Integer referenceId) { this.referenceId = referenceId; }
	public String getNotes() { return notes; }
	public void setNotes(String notes) { this.notes = notes; }
	public User getCreatedBy() { return createdBy; }
	public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
} 