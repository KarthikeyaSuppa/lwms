package com.lwms.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "Suppliers")
public class Suppliers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer supplierId;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String supplierName;

    @Size(max = 255)
    @Column(length = 255)
    private String contactPerson;

    @Email
    @Size(max = 254)
    @Column(length = 254)
    private String email;

    @Size(max = 32)
    @Column(length = 32)
    private String phone;

    @Size(max = 500)
    @Column(length = 500)
    private String address;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

	/**
	 * 
	 */
	public Suppliers() {

	}


	public Suppliers(Integer supplierId, String supplierName, String contactPerson, String email, String phone,
			String address, Boolean isActive) {
		super();
		this.supplierId = supplierId;
		this.supplierName = supplierName;
		this.contactPerson = contactPerson;
		this.email = email;
		this.phone = phone;
		this.address = address;
		this.isActive = isActive;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
    
}