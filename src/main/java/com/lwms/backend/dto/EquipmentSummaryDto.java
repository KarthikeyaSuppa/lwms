package com.lwms.backend.dto;

public class EquipmentSummaryDto {
	private Integer equipmentId;
	private String equipmentName;
	private String equipmentType;
	private String serialNumber;
	private String location; // free text label for UI
	private String status;
	private String purchaseDate; // ISO
	private String warrantyExpiry; // ISO

	public Integer getEquipmentId() { return equipmentId; }
	public void setEquipmentId(Integer equipmentId) { this.equipmentId = equipmentId; }
	public String getEquipmentName() { return equipmentName; }
	public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }
	public String getEquipmentType() { return equipmentType; }
	public void setEquipmentType(String equipmentType) { this.equipmentType = equipmentType; }
	public String getSerialNumber() { return serialNumber; }
	public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
	public String getLocation() { return location; }
	public void setLocation(String location) { this.location = location; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
	public String getPurchaseDate() { return purchaseDate; }
	public void setPurchaseDate(String purchaseDate) { this.purchaseDate = purchaseDate; }
	public String getWarrantyExpiry() { return warrantyExpiry; }
	public void setWarrantyExpiry(String warrantyExpiry) { this.warrantyExpiry = warrantyExpiry; }
} 