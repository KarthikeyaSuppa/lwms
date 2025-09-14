package com.lwms.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Locations")
public class Locations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer locationId;

    @Column(unique = true, nullable = false)
    private String locationCode;

    @Column(nullable = false)
    private String zone;

    @Column(nullable = false)
    private String aisle;

    @Column(nullable = false)
    private String rack;

    @Column(nullable = false)
    private String shelf;

    private Integer capacity = 100;

    private Integer currentLoad = 0;

    @Enumerated(EnumType.STRING)
    private LocationType locationType = LocationType.Storage;

    private Boolean isActive = true;

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
	 * @param locationCode
	 * @param zone
	 * @param aisle
	 * @param rack
	 * @param shelf
	 * @param capacity
	 * @param currentLoad
	 * @param locationType
	 * @param isActive
	 */
	public Locations(Integer locationId, String locationCode, String zone, String aisle, String rack, String shelf,
			Integer capacity, Integer currentLoad, LocationType locationType, Boolean isActive) {
		super();
		this.locationId = locationId;
		this.locationCode = locationCode;
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

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
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