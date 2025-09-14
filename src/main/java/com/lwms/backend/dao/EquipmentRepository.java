package com.lwms.backend.dao;

import com.lwms.backend.entities.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
	Optional<Equipment> findBySerialNumber(String serialNumber);
} 