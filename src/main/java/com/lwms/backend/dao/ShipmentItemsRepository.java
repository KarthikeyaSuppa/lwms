package com.lwms.backend.dao;

import com.lwms.backend.entities.ShipmentItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentItemsRepository extends JpaRepository<ShipmentItems, Integer> {
	List<ShipmentItems> findByShipment_ShipmentId(Integer shipmentId);
	List<ShipmentItems> findByShipment_ShipmentNumber(String shipmentNumber);
	ShipmentItems findByShipment_ShipmentIdAndItem_ItemId(Integer shipmentId, Integer itemId);
} 