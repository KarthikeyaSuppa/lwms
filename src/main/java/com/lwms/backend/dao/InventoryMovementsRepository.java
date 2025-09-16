package com.lwms.backend.dao;

import com.lwms.backend.entities.InventoryMovements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryMovementsRepository extends JpaRepository<InventoryMovements, Integer> {
List<InventoryMovements> findByItem_ItemCodeContainingIgnoreCase(String itemCode);
Optional<InventoryMovements> findFirstByReferenceTypeAndReferenceIdAndItem_ItemId(String referenceType, Integer referenceId, Integer itemId);
}
