package com.lwms.backend.dao;

import com.lwms.backend.entities.Suppliers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface SuppliersRepository extends JpaRepository<Suppliers, Integer> {
/**
 * Looksup supplier by unique email.
 * Input: email string.
 * Output: Optional supplier; read-only.
 */
Optional<Suppliers> findByEmail(String email);

/**
 * Search suppliers by name/contact with optional active filter.
 * Input: q (substring match, case-insensitive), active (nullable to skip).
 * Output: List of suppliers; read-only.
 */
@Query(
    "SELECT s FROM Suppliers s " +
    "WHERE (:q IS NULL OR " +
    "LOWER(s.supplierName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
    "LOWER(s.contactPerson) LIKE LOWER(CONCAT('%', :q, '%'))) " +
    "AND (:active IS NULL OR s.isActive = :active)"
)
List<Suppliers> search(
    @Param("q") String q,
    @Param("active") Boolean active
);
}
