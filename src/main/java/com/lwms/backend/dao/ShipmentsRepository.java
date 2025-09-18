package com.lwms.backend.dao;

import com.lwms.backend.entities.Shipments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentsRepository extends JpaRepository<Shipments, Integer> {
/**
 * Lookup shipment by exact shipment number.
 * Input: shipmentNumber string.
 * Output: Optional shipment; read-only.
 */
Optional<Shipments> findByShipmentNumber(String shipmentNumber);

/**
 * Case-insensitive partial match on shipment number.
 * Input: q substring.
 * Output: List of shipments; read-only.
 */
List<Shipments> findByShipmentNumberContainingIgnoreCase(String q);
}
