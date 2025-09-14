package com.lwms.backend.dao;

import com.lwms.backend.entities.MaintenanceSchedule;
import com.lwms.backend.entities.MaintenanceSchedule.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule, Integer> {
	List<MaintenanceSchedule> findByStatus(Status status);
	List<MaintenanceSchedule> findByAssignedTo_UserId(Integer userId);
} 