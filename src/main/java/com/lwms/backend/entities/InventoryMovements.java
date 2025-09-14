package com.lwms.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "InventoryMovements")
public class InventoryMovements {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer movementId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType movementType;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReferenceType referenceType;

    @Column(nullable = false)
    private Integer referenceId;

    private String notes;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "itemId", nullable = false)
    private Inventory item;

    @ManyToOne
    @JoinColumn(name = "fromLocationId")
    private Locations fromLocation;

    @ManyToOne
    @JoinColumn(name = "toLocationId")
    private Locations toLocation;

    @ManyToOne
    @JoinColumn(name = "createdBy", nullable = false)
    private User createdBy;

    public enum MovementType {
        IN, OUT, TRANSFER, ADJUSTMENT
    }

    public enum ReferenceType {
        Shipment, Transfer, Adjustment, Sale
    }

	/**
	 * 
	 */
	public InventoryMovements() {

	}

	/**
	 * @param movementId
	 * @param movementType
	 * @param quantity
	 * @param referenceType
	 * @param referenceId
	 * @param notes
	 * @param createdAt
	 * @param item
	 * @param fromLocation
	 * @param toLocation
	 * @param createdBy
	 */
	public InventoryMovements(Integer movementId, MovementType movementType, Integer quantity,
			ReferenceType referenceType, Integer referenceId, String notes, LocalDateTime createdAt, Inventory item,
			Locations fromLocation, Locations toLocation, User createdBy) {
		super();
		this.movementId = movementId;
		this.movementType = movementType;
		this.quantity = quantity;
		this.referenceType = referenceType;
		this.referenceId = referenceId;
		this.notes = notes;
		this.createdAt = createdAt;
		this.item = item;
		this.fromLocation = fromLocation;
		this.toLocation = toLocation;
		this.createdBy = createdBy;
	}

	public Integer getMovementId() {
		return movementId;
	}

	public void setMovementId(Integer movementId) {
		this.movementId = movementId;
	}

	public MovementType getMovementType() {
		return movementType;
	}

	public void setMovementType(MovementType movementType) {
		this.movementType = movementType;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public ReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(ReferenceType referenceType) {
		this.referenceType = referenceType;
	}

	public Integer getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(Integer referenceId) {
		this.referenceId = referenceId;
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

	public Inventory getItem() {
		return item;
	}

	public void setItem(Inventory item) {
		this.item = item;
	}

	public Locations getFromLocation() {
		return fromLocation;
	}

	public void setFromLocation(Locations fromLocation) {
		this.fromLocation = fromLocation;
	}

	public Locations getToLocation() {
		return toLocation;
	}

	public void setToLocation(Locations toLocation) {
		this.toLocation = toLocation;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}


}