package com.lwms.backend.dto;

import java.math.BigDecimal;

public class InventoryCreateRequest {
	private String itemCode;
	private String itemName;
	private String category; // label from UI
	private String location; // label from UI
	private Integer quantity;
	private Integer minStockLevel;
	private Integer maxStockLevel;
	private BigDecimal unitPrice;

	public String getItemCode() { return itemCode; }
	public void setItemCode(String itemCode) { this.itemCode = itemCode; }
	public String getItemName() { return itemName; }
	public void setItemName(String itemName) { this.itemName = itemName; }
	public String getCategory() { return category; }
	public void setCategory(String category) { this.category = category; }
	public String getLocation() { return location; }
	public void setLocation(String location) { this.location = location; }
	public Integer getQuantity() { return quantity; }
	public void setQuantity(Integer quantity) { this.quantity = quantity; }
	public Integer getMinStockLevel() { return minStockLevel; }
	public void setMinStockLevel(Integer minStockLevel) { this.minStockLevel = minStockLevel; }
	public Integer getMaxStockLevel() { return maxStockLevel; }
	public void setMaxStockLevel(Integer maxStockLevel) { this.maxStockLevel = maxStockLevel; }
	public BigDecimal getUnitPrice() { return unitPrice; }
	public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
} 