package com.lwms.backend.dto;

public class LocationUpdateRequest {
	private String zone;
	private String aisle;
	private String rack;
	private String shelf;
	private Integer capacity;
	private Integer currentLoad;
	private String locationType;
	private Boolean isActive;

	public String getZone() { return zone; }
	public void setZone(String zone) { this.zone = zone; }
	public String getAisle() { return aisle; }
	public void setAisle(String aisle) { this.aisle = aisle; }
	public String getRack() { return rack; }
	public void setRack(String rack) { this.rack = rack; }
	public String getShelf() { return shelf; }
	public void setShelf(String shelf) { this.shelf = shelf; }
	public Integer getCapacity() { return capacity; }
	public void setCapacity(Integer capacity) { this.capacity = capacity; }
	public Integer getCurrentLoad() { return currentLoad; }
	public void setCurrentLoad(Integer currentLoad) { this.currentLoad = currentLoad; }
	public String getLocationType() { return locationType; }
	public void setLocationType(String locationType) { this.locationType = locationType; }
	public Boolean getIsActive() { return isActive; }
	public void setIsActive(Boolean isActive) { this.isActive = isActive; }
} 