package com.lwms.backend.dto;

public class SupplierUpdateRequest {
	private String supplierName;
	private String contactPerson;
	private String email;
	private String phone;
	private String address;
	private Boolean active;

	public String getSupplierName() { return supplierName; }
	public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
	public String getContactPerson() { return contactPerson; }
	public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getPhone() { return phone; }
	public void setPhone(String phone) { this.phone = phone; }
	public String getAddress() { return address; }
	public void setAddress(String address) { this.address = address; }
	public Boolean getActive() { return active; }
	public void setActive(Boolean active) { this.active = active; }
} 