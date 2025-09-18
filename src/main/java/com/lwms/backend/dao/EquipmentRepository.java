package com.lwms.backend.dao;

import com.lwms.backend.entities.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
/**
 * Find equipment by unique serial number (equipment code).
 * Input: serialNumber string.
 * Output: Optional equipment; read-only.
 */
Optional<Equipment> findBySerialNumber(String serialNumber);

/**
 * Case-insensitive partial search by name/type/serial.
 * Input: name, type, serial (substrings).
 * Output: List of equipment; read-only.
 */
List<Equipment> findByEquipmentNameContainingIgnoreCaseOrEquipmentTypeContainingIgnoreCaseOrSerialNumberContainingIgnoreCase(String name, String type, String serial);
}
