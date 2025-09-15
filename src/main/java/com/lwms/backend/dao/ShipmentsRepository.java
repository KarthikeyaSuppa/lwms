package com.lwms.backend.dao;

import com.lwms.backend.entities.Shipments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentsRepository extends JpaRepository<Shipments, Integer> {
	Optional<Shipments> findByShipmentNumber(String shipmentNumber);
	List<Shipments> findByShipmentNumberContainingIgnoreCase(String q);
} 