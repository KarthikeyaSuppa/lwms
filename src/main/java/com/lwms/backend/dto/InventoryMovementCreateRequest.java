package com.lwms.backend.dto;

public class InventoryMovementCreateRequest {
	private Integer itemId;
	private String itemCode;
	private String movementType;
	private Integer quantity;
	private Integer fromLocationId;
	private Integer toLocationId;
	private String fromLocation;
	private String toLocation;
	private String referenceType;
	private Integer referenceId;
	private String notes;

	public Integer getItemId() { return itemId; }
	public void setItemId(Integer itemId) { this.itemId = itemId; }
	public String getItemCode() { return itemCode; }
	public void setItemCode(String itemCode) { this.itemCode = itemCode; }
	public String getMovementType() { return movementType; }
	public void setMovementType(String movementType) { this.movementType = movementType; }
	public Integer getQuantity() { return quantity; }
	public void setQuantity(Integer quantity) { this.quantity = quantity; }
	public Integer getFromLocationId() { return fromLocationId; }
	public void setFromLocationId(Integer fromLocationId) { this.fromLocationId = fromLocationId; }
	public Integer getToLocationId() { return toLocationId; }
	public void setToLocationId(Integer toLocationId) { this.toLocationId = toLocationId; }
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