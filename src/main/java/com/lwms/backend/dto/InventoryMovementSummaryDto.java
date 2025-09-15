package com.lwms.backend.dto;

public class InventoryMovementSummaryDto {
	private Integer movementId;
	private String itemCode;
	private String movementType;
	private Integer quantity;
	private String fromLocation;
	private String toLocation;
	private String referenceType;
	private Integer referenceId;
	private String notes;

	public Integer getMovementId() { return movementId; }
	public void setMovementId(Integer movementId) { this.movementId = movementId; }
	public String getItemCode() { return itemCode; }
	public void setItemCode(String itemCode) { this.itemCode = itemCode; }
	public String getMovementType() { return movementType; }
	public void setMovementType(String movementType) { this.movementType = movementType; }
	public Integer getQuantity() { return quantity; }
	public void setQuantity(Integer quantity) { this.quantity = quantity; }
	public String getFromLocation() { return fromLocation; }
	public void setFromLocation(String fromLocation) { this.fromLocation = fromLocation; }
	public String getToLocation() { return toLocation; }
	public void setToLocation(String toLocation) { this.toLocation = toLocation; }
	public String getReferenceType() { return referenceType; }
	public void setReferenceType(String referenceType) { this.referenceType = referenceType; }
	public Integer getReferenceId() { return referenceId; }
	public void setReferenceId(Integer referenceId) { this.referenceId = referenceId; }
	public String getNotes() { return notes; }
	public void setNotes(String notes) { this.notes = notes; }
} 