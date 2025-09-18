package com.lwms.backend.dao;

import com.lwms.backend.entities.Locations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationsRepository extends JpaRepository<Locations, Integer> {
/**
 *  Case-insensitive search across zone/aisle/rack/shelf using partial matches.
 * Input: zone, aisle, rack, shelf (any may be empty for broader matches).
 * Output: List of matching locations; read-only.
 */
List<Locations> findByZoneContainingIgnoreCaseOrAisleContainingIgnoreCaseOrRackContainingIgnoreCaseOrShelfContainingIgnoreCase(String zone, String aisle, String rack, String shelf);

/**
 * Find all locations with the given type.
 * Input: locationType enum value.
 * Output: List of locations; read-only.
 */
List<Locations> findByLocationType(Locations.LocationType locationType);

/**
 * Filter locations by active flag.
 * Input: isActive (true/false).
 * Output: List of locations; read-only.
 */
List<Locations> findByIsActive(Boolean isActive);

// Reports methods - fixed to use actual field names
/**
 * Return utilization triples for active locations.
 * Input: none.
 * Output: List of Object[] as [locationCode, capacity, currentLoad]; read-only. for the report.
 */
@Query("SELECT CONCAT(l.zone, '-', l.aisle, '-', l.rack, '-', l.shelf), l.capacity, l.currentLoad FROM Locations l WHERE l.isActive = true")
List<Object[]> getLocationUtilization();
}
