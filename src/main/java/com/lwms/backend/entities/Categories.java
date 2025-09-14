package com.lwms.backend.entities;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "Categories")
public class Categories {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;

    @Column(nullable = false)
    private String categoryName;

    @Column(unique = true, nullable = false)
    private String categoryCode;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentCategoryId")
    private Categories parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    private Set<Categories> subCategories;
    
    
	public Categories() {

	}


	public Categories(Integer categoryId, String categoryName, String categoryCode, String description,
			Categories parentCategory, Set<Categories> subCategories) {
		super();
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.categoryCode = categoryCode;
		this.description = description;
		this.parentCategory = parentCategory;
		this.subCategories = subCategories;
	}


	public Integer getCategoryId() {
		return categoryId;
	}


	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}


	public String getCategoryName() {
		return categoryName;
	}


	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}


	public String getCategoryCode() {
		return categoryCode;
	}


	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public Categories getParentCategory() {
		return parentCategory;
	}


	public void setParentCategory(Categories parentCategory) {
		this.parentCategory = parentCategory;
	}


	public Set<Categories> getSubCategories() {
		return subCategories;
	}


	public void setSubCategories(Set<Categories> subCategories) {
		this.subCategories = subCategories;
	}

    
    
}