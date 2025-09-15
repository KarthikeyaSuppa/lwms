package com.lwms.backend.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "shipmentitems", uniqueConstraints = {
    @UniqueConstraint(name = "unique_shipment_item", columnNames = {"shipmentId", "itemId"})
})
public class ShipmentItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer shipmentItemId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipmentId", nullable = false)
    @JsonIgnore
    private Shipments shipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itemId", nullable = false)
    @JsonIgnore
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