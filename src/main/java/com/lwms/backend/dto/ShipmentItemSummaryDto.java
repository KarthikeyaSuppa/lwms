package com.lwms.backend.dto;

import java.math.BigDecimal;

public class ShipmentItemSummaryDto {
	private Integer shipmentItemId;
	private String shipmentNumber;
	private String itemCode;
	private Integer quantity;
	private BigDecimal unitPrice;
	private BigDecimal totalPrice;

	public Integer getShipmentItemId() { return shipmentItemId; }
	public void setShipmentItemId(Integer shipmentItemId) { this.shipmentItemId = shipmentItemId; }
	public String getShipmentNumber() { return shipmentNumber; }
	public void setShipmentNumber(String shipmentNumber) { this.shipmentNumber = shipmentNumber; }
	public String getItemCode() { return itemCode; }
	public void setItemCode(String itemCode) { this.itemCode = itemCode; }
	public Integer getQuantity() { return quantity; }
	public void setQuantity(Integer quantity) { this.quantity = quantity; }
	public BigDecimal getUnitPrice() { return unitPrice; }
	public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
	public BigDecimal getTotalPrice() { return totalPrice; }
	public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
} 