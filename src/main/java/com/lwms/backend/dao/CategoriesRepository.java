package com.lwms.backend.dao;

import com.lwms.backend.entities.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Integer> {
	Optional<Categories> findByCategoryCode(String categoryCode);
} 