package com.lwms.backend.services;

import com.lwms.backend.dao.CategoriesRepository;
import com.lwms.backend.dto.CategoryCreateRequest;
import com.lwms.backend.dto.CategorySummaryDto;
import com.lwms.backend.dto.CategoryUpdateRequest;
import com.lwms.backend.entities.Categories;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
	private final CategoriesRepository categoriesRepository;

	public CategoryService(CategoriesRepository categoriesRepository) {
		this.categoriesRepository = categoriesRepository;
	}

	@Transactional(readOnly = true)
	public List<CategorySummaryDto> listCategories(String query) {
		List<Categories> results;
		if (StringUtils.hasText(query)) {
			results = categoriesRepository
					.findByCategoryNameContainingIgnoreCaseOrCategoryCodeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, query);
		} else {
			results = categoriesRepository.findAll();
		}
		return results.stream().map(this::toDto).collect(Collectors.toList());
	}

	@Transactional
	public CategorySummaryDto createCategory(CategoryCreateRequest req) {
		validateCreate(req);
		Categories c = new Categories();
		c.setCategoryName(req.getCategoryName());
		c.setCategoryCode(req.getCategoryCode());
		c.setDescription(req.getDescription());
		Categories saved = categoriesRepository.save(c);
		return toDto(saved);
	}

	@Transactional
	public CategorySummaryDto updateCategory(Integer categoryId, CategoryUpdateRequest req) {
		Categories c = categoriesRepository.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));
		if (req.getCategoryName() != null) c.setCategoryName(req.getCategoryName());
		if (req.getCategoryCode() != null) c.setCategoryCode(req.getCategoryCode());
		if (req.getDescription() != null) c.setDescription(req.getDescription());
		Categories saved = categoriesRepository.save(c);
		return toDto(saved);
	}

	@Transactional
	public void deleteCategory(Integer categoryId) {
		if (!categoriesRepository.existsById(categoryId)) {
			throw new RuntimeException("Category not found: " + categoryId);
		}
		categoriesRepository.deleteById(categoryId);
	}

	private void validateCreate(CategoryCreateRequest req) {
		if (!StringUtils.hasText(req.getCategoryName())) {
			throw new IllegalArgumentException("categoryName is required");
		}
		if (!StringUtils.hasText(req.getCategoryCode())) {
			throw new IllegalArgumentException("categoryCode is required");
		}
		categoriesRepository.findByCategoryCode(req.getCategoryCode())
				.ifPresent(existing -> { throw new IllegalArgumentException("categoryCode already exists"); });
	}

	private CategorySummaryDto toDto(Categories c) {
		CategorySummaryDto dto = new CategorySummaryDto();
		dto.setCategoryId(c.getCategoryId());
		dto.setCategoryName(c.getCategoryName());
		dto.setCategoryCode(c.getCategoryCode());
		dto.setDescription(c.getDescription());
		return dto;
	}
} 