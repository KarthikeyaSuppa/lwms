package com.lwms.backend.controllers;

import com.lwms.backend.dto.CategoryCreateRequest;
import com.lwms.backend.dto.CategorySummaryDto;
import com.lwms.backend.dto.CategoryUpdateRequest;
import com.lwms.backend.services.CategoryService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoriesController {

	private final CategoryService categoryService;

	public CategoriesController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@GetMapping(value = {"/lwms/categories/api", "/categories/api"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<CategorySummaryDto>> listCategories(
			@RequestParam(value = "q", required = false) String query
	) {
		return ResponseEntity.ok(categoryService.listCategories(query));
	}

	@PostMapping(value = {"/lwms/categories/api", "/categories/api"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CategorySummaryDto> createCategory(@RequestBody CategoryCreateRequest request) {
		CategorySummaryDto created = categoryService.createCategory(request);
		return ResponseEntity.ok(created);
	}

	@PatchMapping(value = {"/lwms/categories/api/{id}", "/categories/api/{id}"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CategorySummaryDto> updateCategory(@PathVariable("id") Integer id, @RequestBody CategoryUpdateRequest request) {
		return ResponseEntity.ok(categoryService.updateCategory(id, request));
	}

	@DeleteMapping(value = {"/lwms/categories/api/{id}", "/categories/api/{id}"})
	public ResponseEntity<Void> deleteCategory(@PathVariable("id") Integer id) {
		categoryService.deleteCategory(id);
		return ResponseEntity.noContent().build();
	}
} 