package com.lwms.backend.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "inventory", indexes = {
        @Index(name = "idx_item_code", columnList = "itemCode"),
        @Index(name = "idx_category", columnList = "categoryId"),
        @Index(name = "idx_location", columnList = "locationId")
})
public class Inventory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer itemId;

	@NotBlank
	@Size(max = 50)
	@Column(unique = true, nullable = false, length = 50)
	private String itemCode;

	@NotBlank
	@Size(max = 100)
	@Column(nullable = false, length = 100)
	private String itemName;

	private Integer quantity = 0;

	private Integer minStockLevel = 10;

	private Integer maxStockLevel = 1000;

	@Column(precision = 10, scale = 2)
	private BigDecimal unitPrice = BigDecimal.ZERO;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoryId", nullable = false)
	@NotNull
	@JsonIgnore
	private Categories category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "locationId", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@NotNull
	@JsonIgnore
	private Locations location;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "supplierId")
	@JsonIgnore
	private Suppliers supplier;

	/**
	 * 
	 */
	public Inventory() {

	}

	public Inventory(Integer itemId, String itemCode, String itemName, Integer quantity, Integer minStockLevel,
			Integer maxStockLevel, BigDecimal unitPrice, Categories category, Locations location,
			Suppliers supplier) {
		super();
		this.itemId = itemId;
		this.itemCode = itemCode;
		this.itemName = itemName;
		this.quantity = quantity;
		this.minStockLevel = minStockLevel;
		this.maxStockLevel = maxStockLevel;
		this.unitPrice = unitPrice;
		this.category = category;
		this.location = location;
		this.supplier = supplier;
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


}