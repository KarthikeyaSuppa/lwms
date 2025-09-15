package com.lwms.backend.dto;

public class ShipmentCreateRequest {
	private String shipmentNumber;
	private String shipmentType; // Inbound|Outbound
	private String supplier; // ID or Name string from UI
	private String origin;
	private String destination;
	private String status; // Planned|In Transit|Delivered|Cancelled
	private String expectedDelivery; // ISO local datetime
	private String actualDelivery; // ISO local datetime
	private String totalValue; // decimal as string

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