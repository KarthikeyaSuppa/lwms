package com.lwms.backend.dao;

import com.lwms.backend.entities.Locations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationsRepository extends JpaRepository<Locations, Integer> {
	Optional<Locations> findByLocationCode(String locationCode);
} 