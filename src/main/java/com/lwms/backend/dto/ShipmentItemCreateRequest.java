package com.lwms.backend.dto;

import java.math.BigDecimal;

public class ShipmentItemCreateRequest {
	private Integer shipmentId;
	private Integer itemId;
	private Integer quantity;
	private BigDecimal unitPrice;

	public Integer getShipmentId() { return shipmentId; }
	public void setShipmentId(Integer shipmentId) { this.shipmentId = shipmentId; }
	public Integer getItemId() { return itemId; }
	public void setItemId(Integer itemId) { this.itemId = itemId; }
	public Integer getQuantity() { return quantity; }
	public void setQuantity(Integer quantity) { this.quantity = quantity; }
	public BigDecimal getUnitPrice() { return unitPrice; }
	public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
} 