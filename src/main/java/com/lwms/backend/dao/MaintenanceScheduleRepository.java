package com.lwms.backend.dao;

import com.lwms.backend.entities.MaintenanceSchedule;
import com.lwms.backend.entities.MaintenanceSchedule.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule, Integer> {
	/**
	 * List maintenance tasks by status.
	 * Input: Status enum value.
	 * Output: List of schedules; read-only.
	 */
	List<MaintenanceSchedule> findByStatus(Status status);

	/**
	 * List maintenance tasks assigned to a user by id.
	 * Input: userId integer.
	 * Output: List of schedules; read-only.
	 */
	List<MaintenanceSchedule> findByAssignedTo_UserId(Integer userId);
} 