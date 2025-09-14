package com.lwms.backend.dao;

import com.lwms.backend.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
	Optional<Inventory> findByItemCode(String itemCode);
} 