package com.lwms.backend.dao;

import com.lwms.backend.entities.InventoryMovements;
import com.lwms.backend.entities.InventoryMovements.ReferenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryMovementsRepository extends JpaRepository<InventoryMovements, Integer> {
	List<InventoryMovements> findByItem_ItemId(Integer itemId);
	List<InventoryMovements> findByReferenceTypeAndReferenceId(ReferenceType referenceType, Integer referenceId);
} 