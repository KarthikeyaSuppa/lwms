package com.lwms.backend.dto;

public class ShipmentSummaryDto {
	private Integer shipmentId;
	private String shipmentNumber;
	private String shipmentType;
	private String supplier; // label or id text as provided
	private String origin;
	private String destination;
	private String status; // Planned|In Transit|Delivered|Cancelled
	private String expectedDelivery;
	private String actualDelivery;
	private String totalValue;

	public Integer getShipmentId() { return shipmentId; }
	public void setShipmentId(Integer shipmentId) { this.shipmentId = shipmentId; }
	public String getShipmentNumber() { return shipmentNumber; }
	public void setShipmentNumber(String shipmentNumber) { this.shipmentNumber = shipmentNumber; }
	public String getShipmentType() { return shipmentType; }
	public void setShipmentType(String shipmentType) { this.shipmentType = shipmentType; }
	public String getSupplier() { return supplier; }
	public void setSupplier(String supplier) { this.supplier = supplier; }
	public String getOrigin() { return origin; }
	public void setOrigin(String origin) { this.origin = origin; }
	public String getDestination() { return destination; }
	public void setDestination(String destination) { this.destination = destination; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
	public String getExpectedDelivery() { return expectedDelivery; }
	public void setExpectedDelivery(String expectedDelivery) { this.expectedDelivery = expectedDelivery; }
	public String getActualDelivery() { return actualDelivery; }
	public void setActualDelivery(String actualDelivery) { this.actualDelivery = actualDelivery; }
	public String getTotalValue() { return totalValue; }
	public void setTotalValue(String totalValue) { this.totalValue = totalValue; }
} 