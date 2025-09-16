package com.lwms.backend.dao;

import com.lwms.backend.entities.Locations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationsRepository extends JpaRepository<Locations, Integer> {
List<Locations> findByZoneContainingIgnoreCaseOrAisleContainingIgnoreCaseOrRackContainingIgnoreCaseOrShelfContainingIgnoreCase(String zone, String aisle, String rack, String shelf);
List<Locations> findByLocationType(Locations.LocationType locationType);
List<Locations> findByIsActive(Boolean isActive);

// Reports methods - fixed to use actual field names
@Query("SELECT CONCAT(l.zone, '-', l.aisle, '-', l.rack, '-', l.shelf), l.capacity, l.currentLoad FROM Locations l WHERE l.isActive = true")
List<Object[]> getLocationUtilization();
}
