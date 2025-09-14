package com.lwms.backend.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ShipmentItems")
public class ShipmentItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer shipmentItemId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(precision = 19, scale = 2)
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "shipmentId", nullable = false)
    private Shipments shipment;

    @ManyToOne
    @JoinColumn(name = "itemId", nullable = false)
    private Inventory item;

	/**
	 * 
	 */
	public ShipmentItems() {

	}

	/**
	 * @param shipmentItemId
	 * @param quantity
	 * @param unitPrice
	 * @param totalPrice
	 * @param shipment
	 * @param item
	 */
	public ShipmentItems(Integer shipmentItemId, Integer quantity, BigDecimal unitPrice, BigDecimal totalPrice,
			Shipments shipment, Inventory item) {
		super();
		this.shipmentItemId = shipmentItemId;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.totalPrice = totalPrice;
		this.shipment = shipment;
		this.item = item;
	}

	public Integer getShipmentItemId() {
		return shipmentItemId;
	}

	public void setShipmentItemId(Integer shipmentItemId) {
		this.shipmentItemId = shipmentItemId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Shipments getShipment() {
		return shipment;
	}

	public void setShipment(Shipments shipment) {
		this.shipment = shipment;
	}

	public Inventory getItem() {
		return item;
	}

	public void setItem(Inventory item) {
		this.item = item;
	}


}