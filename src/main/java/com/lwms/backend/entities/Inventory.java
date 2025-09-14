package com.lwms.backend.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "Inventory", indexes = {
		@Index(name = "idx_inventory_item_code", columnList = "itemCode"),
		@Index(name = "idx_inventory_category_id", columnList = "categoryId"),
		@Index(name = "idx_inventory_location_id", columnList = "locationId"),
		@Index(name = "idx_inventory_supplier_id", columnList = "supplierId"),
		@Index(name = "idx_inventory_created_by", columnList = "createdBy")
})
public class Inventory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer itemId;

	@NotBlank
	@Size(max = 64)
	@Column(unique = true, nullable = false, length = 64)
	private String itemCode;

	@NotBlank
	@Size(max = 255)
	@Column(nullable = false, length = 255)
	private String itemName;

	private Integer quantity = 0;

	private Integer minStockLevel = 10;

	private Integer maxStockLevel = 1000;

	@Column(precision = 19, scale = 2)
	private BigDecimal unitPrice = BigDecimal.ZERO;

	@UpdateTimestamp
	private LocalDateTime lastUpdated;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoryId", nullable = false)
	@NotNull
	@JsonIgnore
	private Categories category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "locationId", nullable = false)
	@NotNull
	@JsonIgnore
	private Locations location;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "supplierId")
	@JsonIgnore
	private Suppliers supplier;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "createdBy", nullable = false)
	@NotNull
	@JsonIgnore
	private User createdBy;

	/**
	 * 
	 */
	public Inventory() {

	}

	public Inventory(Integer itemId, String itemCode, String itemName, Integer quantity, Integer minStockLevel,
			Integer maxStockLevel, BigDecimal unitPrice, LocalDateTime lastUpdated, Categories category, Locations location,
			Suppliers supplier, User createdBy) {
		super();
		this.itemId = itemId;
		this.itemCode = itemCode;
		this.itemName = itemName;
		this.quantity = quantity;
		this.minStockLevel = minStockLevel;
		this.maxStockLevel = maxStockLevel;
		this.unitPrice = unitPrice;
		this.lastUpdated = lastUpdated;
		this.category = category;
		this.location = location;
		this.supplier = supplier;
		this.createdBy = createdBy;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getMinStockLevel() {
		return minStockLevel;
	}

	public void setMinStockLevel(Integer minStockLevel) {
		this.minStockLevel = minStockLevel;
	}

	public Integer getMaxStockLevel() {
		return maxStockLevel;
	}

	public void setMaxStockLevel(Integer maxStockLevel) {
		this.maxStockLevel = maxStockLevel;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(LocalDateTime lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Categories getCategory() {
		return category;
	}

	public void setCategory(Categories category) {
		this.category = category;
	}

	public Locations getLocation() {
		return location;
	}

	public void setLocation(Locations location) {
		this.location = location;
	}

	public Suppliers getSupplier() {
		return supplier;
	}

	public void setSupplier(Suppliers supplier) {
		this.supplier = supplier;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}


}