package com.lwms.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "locations")
public class Locations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer locationId;

    @NotBlank
    @Size(max = 10)
    @Column(nullable = false, length = 10)
    private String zone;

    @NotBlank
    @Size(max = 10)
    @Column(nullable = false, length = 10)
    private String aisle;

    @NotBlank
    @Size(max = 10)
    @Column(nullable = false, length = 10)
    private String rack;

    @NotBlank
    @Size(max = 10)
    @Column(nullable = false, length = 10)
    private String shelf;

    private Integer capacity = 100;

    private Integer currentLoad = 0;

    @Enumerated(EnumType.STRING)
    private LocationType locationType = LocationType.Storage;

    private Boolean isActive = true;

    // Cascade deletions to dependent rows that reference this location
    @OneToMany(mappedBy = "location", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Inventory> inventories;

    @OneToMany(mappedBy = "location", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Equipment> equipmentList;

    @OneToMany(mappedBy = "fromLocation", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<InventoryMovements> movementsFrom;

    @OneToMany(mappedBy = "toLocation", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<InventoryMovements> movementsTo;

    public enum LocationType {
        Storage, Receiving, Shipping, Quality
    }

	/**
	 * 
	 */
	public Locations() {

	}

	/**
	 * @param locationId
	 * @param zone
	 * @param aisle
	 * @param rack
	 * @param shelf
	 * @param capacity
	 * @param currentLoad
	 * @param locationType
	 * @param isActive
	 */
	public Locations(Integer locationId, String zone, String aisle, String rack, String shelf,
			Integer capacity, Integer currentLoad, LocationType locationType, Boolean isActive) {
		super();
		this.locationId = locationId;
		this.zone = zone;
		this.aisle = aisle;
		this.rack = rack;
		this.shelf = shelf;
		this.capacity = capacity;
		this.currentLoad = currentLoad;
		this.locationType = locationType;
		this.isActive = isActive;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getAisle() {
		return aisle;
	}

	public void setAisle(String aisle) {
		this.aisle = aisle;
	}

	public String getRack() {
		return rack;
	}

	public void setRack(String rack) {
		this.rack = rack;
	}

	public String getShelf() {
		return shelf;
	}

	public void setShelf(String shelf) {
		this.shelf = shelf;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public Integer getCurrentLoad() {
		return currentLoad;
	}

	public void setCurrentLoad(Integer currentLoad) {
		this.currentLoad = currentLoad;
	}

	public LocationType getLocationType() {
		return locationType;
	}

	public void setLocationType(LocationType locationType) {
		this.locationType = locationType;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

}