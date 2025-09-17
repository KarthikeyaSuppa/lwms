package com.lwms.backend.services;

import com.lwms.backend.dao.EquipmentRepository;
import com.lwms.backend.dao.MaintenanceScheduleRepository;
import com.lwms.backend.dao.UserRepository;
import com.lwms.backend.dto.MaintenanceCreateRequest;
import com.lwms.backend.dto.MaintenanceSummaryDto;
import com.lwms.backend.dto.MaintenanceUpdateRequest;
import com.lwms.backend.entities.Equipment;
import com.lwms.backend.entities.MaintenanceSchedule;
import com.lwms.backend.entities.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaintenanceScheduleService {
	private final MaintenanceScheduleRepository scheduleRepository;
	private final EquipmentRepository equipmentRepository;
	private final UserRepository userRepository;

	public MaintenanceScheduleService(MaintenanceScheduleRepository scheduleRepository, EquipmentRepository equipmentRepository, UserRepository userRepository) {
		this.scheduleRepository = scheduleRepository;
		this.equipmentRepository = equipmentRepository;
		this.userRepository = userRepository;
	}

	@Transactional(readOnly = true)
	public List<MaintenanceSummaryDto> list(String q) {
		List<MaintenanceSchedule> results = scheduleRepository.findAll();
		if (StringUtils.hasText(q)) {
			String qq = q.toLowerCase();
			results = results.stream().filter(ms ->
				(StringUtils.hasText(ms.getTaskDescription()) && ms.getTaskDescription().toLowerCase().contains(qq))
					|| (ms.getStatus() != null && toStatusString(ms.getStatus()).toLowerCase().contains(qq))
					|| (ms.getPriority() != null && ms.getPriority().name().toLowerCase().contains(qq))
					|| (ms.getMaintenanceType() != null && ms.getMaintenanceType().name().toLowerCase().contains(qq))
			).collect(Collectors.toList());
		}
		return results.stream().map(this::toDto).collect(Collectors.toList());
	}

	@Transactional
	public MaintenanceSummaryDto create(MaintenanceCreateRequest req) {
		MaintenanceSchedule ms = new MaintenanceSchedule();
		ms.setTaskDescription(req.getTaskDescription());
		ms.setMaintenanceType(parseType(req.getMaintenanceType()));
		ms.setPriority(parsePriority(req.getPriority()));
		if (StringUtils.hasText(req.getScheduledDate())) ms.setScheduledDate(LocalDateTime.parse(req.getScheduledDate()));
		if (req.getEstimatedDuration() != null) ms.setEstimatedDuration(req.getEstimatedDuration());
		ms.setStatus(parseStatus(req.getStatus()));
		if (StringUtils.hasText(req.getCompletedDate())) ms.setCompletedDate(LocalDateTime.parse(req.getCompletedDate()));
		if (req.getActualDuration() != null) ms.setActualDuration(req.getActualDuration());
		if (StringUtils.hasText(req.getCost())) ms.setCost(new BigDecimal(req.getCost()));
		ms.setNotes(req.getNotes());
		Equipment equipment = equipmentRepository.findById(req.getEquipmentId()).orElseThrow(() -> new RuntimeException("Equipment not found: " + req.getEquipmentId()));
		ms.setEquipment(equipment);
		if (req.getAssignedTo() != null) {
			User assigned = userRepository.findById(req.getAssignedTo()).orElseThrow(() -> new RuntimeException("User not found: " + req.getAssignedTo()));
			ms.setAssignedTo(assigned);
		}
		User createdBy = userRepository.findById(req.getCreatedBy()).orElseThrow(() -> new RuntimeException("User not found: " + req.getCreatedBy()));
		ms.setCreatedBy(createdBy);
		MaintenanceSchedule saved = scheduleRepository.save(ms);
		return toDto(saved);
	}

	@Transactional
	public MaintenanceSummaryDto update(Integer scheduleId, MaintenanceUpdateRequest req) {
		MaintenanceSchedule ms = scheduleRepository.findById(scheduleId).orElseThrow(() -> new RuntimeException("Maintenance not found: " + scheduleId));
		if (req.getTaskDescription() != null) ms.setTaskDescription(req.getTaskDescription());
		if (req.getMaintenanceType() != null) ms.setMaintenanceType(parseType(req.getMaintenanceType()));
		if (req.getPriority() != null) ms.setPriority(parsePriority(req.getPriority()));
		LocalDateTime sched = safeParseDateTime(req.getScheduledDate()); if (sched != null) ms.setScheduledDate(sched);
		if (req.getEstimatedDuration() != null) ms.setEstimatedDuration(req.getEstimatedDuration());
		if (StringUtils.hasText(req.getStatus())) ms.setStatus(parseStatus(req.getStatus()));
		LocalDateTime comp = safeParseDateTime(req.getCompletedDate()); if (comp != null) ms.setCompletedDate(comp);
		if (req.getActualDuration() != null) ms.setActualDuration(req.getActualDuration());
		BigDecimal cost = safeParseBigDecimal(req.getCost()); if (cost != null) ms.setCost(cost);
		if (req.getNotes() != null) ms.setNotes(req.getNotes());
		if (req.getEquipmentId() != null) {
			Equipment equipment = equipmentRepository.findById(req.getEquipmentId()).orElseThrow(() -> new RuntimeException("Equipment not found: " + req.getEquipmentId()));
			ms.setEquipment(equipment);
		}
		if (req.getAssignedTo() != null) {
			User assigned = userRepository.findById(req.getAssignedTo()).orElseThrow(() -> new RuntimeException("User not found: " + req.getAssignedTo()));
			ms.setAssignedTo(assigned);
		}
		MaintenanceSchedule saved = scheduleRepository.save(ms);
		return toDto(saved);
	}

	@Transactional
	public void delete(Integer scheduleId) {
		if (!scheduleRepository.existsById(scheduleId)) throw new RuntimeException("Maintenance not found: " + scheduleId);
		scheduleRepository.deleteById(scheduleId);
	}

	private MaintenanceSchedule.MaintenanceType parseType(String s) {
		if (!StringUtils.hasText(s)) return null;
		return MaintenanceSchedule.MaintenanceType.valueOf(s);
	}

	private MaintenanceSchedule.Priority parsePriority(String s) {
		if (!StringUtils.hasText(s)) return null;
		return MaintenanceSchedule.Priority.valueOf(s);
	}

	private MaintenanceSchedule.Status parseStatus(String s) {
		if (!StringUtils.hasText(s)) return null;
		return switch (s) {
			case "Scheduled" -> MaintenanceSchedule.Status.Scheduled;
			case "In Progress" -> MaintenanceSchedule.Status.In_Progress;
			case "Completed" -> MaintenanceSchedule.Status.Completed;
			case "Cancelled" -> MaintenanceSchedule.Status.Cancelled;
			default -> throw new IllegalArgumentException("Unknown status: " + s);
		};
	}

	private String toStatusString(MaintenanceSchedule.Status st) {
		return switch (st) {
			case Scheduled -> "Scheduled";
			case In_Progress -> "In Progress";
			case Completed -> "Completed";
			case Cancelled -> "Cancelled";
		};
	}

	private MaintenanceSummaryDto toDto(MaintenanceSchedule ms) {
		MaintenanceSummaryDto dto = new MaintenanceSummaryDto();
		dto.setScheduleId(ms.getScheduleId());
		dto.setEquipmentId(ms.getEquipment() != null ? String.valueOf(ms.getEquipment().getEquipmentId()) : null);
		dto.setEquipmentCode(ms.getEquipment() != null ? (ms.getEquipment().getSerialNumber() != null ? ms.getEquipment().getSerialNumber() : ms.getEquipment().getEquipmentName()) : null);
		dto.setTaskDescription(ms.getTaskDescription());
		dto.setMaintenanceType(ms.getMaintenanceType() != null ? ms.getMaintenanceType().name() : null);
		dto.setPriority(ms.getPriority() != null ? ms.getPriority().name() : null);
		dto.setScheduledDate(ms.getScheduledDate() != null ? ms.getScheduledDate().toString() : null);
		dto.setEstimatedDuration(ms.getEstimatedDuration());
		dto.setAssignedTo(ms.getAssignedTo() != null ? ms.getAssignedTo().getUsername() : null);
		dto.setStatus(ms.getStatus() != null ? toStatusString(ms.getStatus()) : null);
		dto.setCompletedDate(ms.getCompletedDate() != null ? ms.getCompletedDate().toString() : null);
		dto.setActualDuration(ms.getActualDuration());
		dto.setCost(ms.getCost() != null ? ms.getCost().toString() : null);
		dto.setNotes(ms.getNotes());
		return dto;
	}

	private LocalDateTime safeParseDateTime(String s) {
		if (!StringUtils.hasText(s)) return null;
		String v = s.trim().replace(' ', 'T');
		try { return LocalDateTime.parse(v); } catch (DateTimeParseException ex) { return null; }
	}

	private BigDecimal safeParseBigDecimal(String s) {
		if (!StringUtils.hasText(s)) return null;
		try { return new BigDecimal(s.trim()); } catch (NumberFormatException ex) { return null; }
	}
} 