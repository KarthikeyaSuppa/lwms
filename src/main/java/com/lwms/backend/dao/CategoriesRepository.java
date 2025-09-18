package com.lwms.backend.dao;

import com.lwms.backend.entities.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Integer> {
	/**
	 * Finds category by unique category code.
	 * Input: categoryCode string.
	 * Output: Optional category; read-only.
	 */
	Optional<Categories> findByCategoryCode(String categoryCode);

	/**
	 * Case-insensitive partial search by name/code/description.
	 * Input: name, code, description substrings.
	 * Output: List of categories; read-only.
	 */
	List<Categories> findByCategoryNameContainingIgnoreCaseOrCategoryCodeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String code, String description);
} 