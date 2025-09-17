package com.lwms.backend.dto;

public class EquipmentUpdateRequest {
	private String equipmentName;
	private String equipmentType;
	private String serialNumber;
	private String location;
	private Integer locationId; // optional: prefer this if provided
	private String status;
	private String purchaseDate;
	private String warrantyExpiry;

	public String getEquipmentName() { return equipmentName; }
	public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }
	public String getEquipmentType() { return equipmentType; }
	public void setEquipmentType(String equipmentType) { this.equipmentType = equipmentType; }
	public String getSerialNumber() { return serialNumber; }
	public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
	public String getLocation() { return location; }
	public void setLocation(String location) { this.location = location; }
	public Integer getLocationId() { return locationId; }
	public void setLocationId(Integer locationId) { this.locationId = locationId; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
	public String getPurchaseDate() { return purchaseDate; }
	public void setPurchaseDate(String purchaseDate) { this.purchaseDate = purchaseDate; }
	public String getWarrantyExpiry() { return warrantyExpiry; }
	public void setWarrantyExpiry(String warrantyExpiry) { this.warrantyExpiry = warrantyExpiry; }
} 