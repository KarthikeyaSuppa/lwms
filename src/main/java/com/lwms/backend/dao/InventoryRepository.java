package com.lwms.backend.dao;

import com.lwms.backend.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
/**
 * Looksup inventory item by unique item code.
 * Input: itemCode string.
 * Output: Optional inventory; read-only.
 */
Optional<Inventory> findByItemCode(String itemCode);

/**
 * Case-insensitive partial search by code or name.
 * Input: itemCode, itemName substrings.
 * Output: List of inventory records; read-only.
 */
List<Inventory> findByItemCodeContainingIgnoreCaseOrItemNameContainingIgnoreCase(String itemCode, String itemName);

/**
 * Case-insensitive partial search by category name via relation.
 * Input: categoryName substring.
 * Output: List of inventory records; read-only.
 */
List<Inventory> findByCategory_CategoryNameContainingIgnoreCase(String categoryName);

// Reports methods
/**
 * Compute counts per stock status bucket using quantity vs thresholds.
 * Input: none.
 * Output: List of Object[] as [statusLabel, count]; read-only aggregation.
 */
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

/**
 * Does: Calculate total inventory value (quantity * unitPrice sum) safely.
 * Input: none.
 * Output: Double total value; read-only aggregation.
 */
@Query("SELECT COALESCE(SUM(i.quantity * i.unitPrice), 0) FROM Inventory i")
Double calculateTotalValue();
}
