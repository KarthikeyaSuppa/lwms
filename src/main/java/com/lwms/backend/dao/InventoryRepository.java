package com.lwms.backend.dao;

import com.lwms.backend.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
Optional<Inventory> findByItemCode(String itemCode);
List<Inventory> findByItemCodeContainingIgnoreCaseOrItemNameContainingIgnoreCase(String itemCode, String itemName);
List<Inventory> findByCategory_CategoryNameContainingIgnoreCase(String categoryName);

// Reports methods
@Query("SELECT " +
       "CASE " +
       "WHEN i.quantity <= 0 THEN 'Out of Stock' " +
       "WHEN i.quantity < i.minStockLevel THEN 'Low Stock' " +
       "WHEN i.quantity > i.maxStockLevel THEN 'Overstock' " +
       "ELSE 'Optimal' " +
       "END as status, " +
       "COUNT(*) as count " +
       "FROM Inventory i " +
       "GROUP BY " +
       "CASE " +
       "WHEN i.quantity <= 0 THEN 'Out of Stock' " +
       "WHEN i.quantity < i.minStockLevel THEN 'Low Stock' " +
       "WHEN i.quantity > i.maxStockLevel THEN 'Overstock' " +
       "ELSE 'Optimal' " +
       "END")
List<Object[]> getInventoryStatusCounts();

@Query("SELECT COALESCE(SUM(i.quantity * i.unitPrice), 0) FROM Inventory i")
Double calculateTotalValue();
}
