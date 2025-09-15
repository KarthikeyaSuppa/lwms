package com.lwms.backend.entities;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class MaintenanceScheduleStatusConverter implements AttributeConverter<MaintenanceSchedule.Status, String> {
    @Override
    public String convertToDatabaseColumn(MaintenanceSchedule.Status attribute) {
        if (attribute == null) return null;
        return switch (attribute) {
            case Scheduled -> "Scheduled";
            case In_Progress -> "In Progress";
            case Completed -> "Completed";
            case Cancelled -> "Cancelled";
        };
    }

    @Override
    public MaintenanceSchedule.Status convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return switch (dbData) {
            case "Scheduled" -> MaintenanceSchedule.Status.Scheduled;
            case "In Progress" -> MaintenanceSchedule.Status.In_Progress;
            case "Completed" -> MaintenanceSchedule.Status.Completed;
            case "Cancelled" -> MaintenanceSchedule.Status.Cancelled;
            default -> throw new IllegalArgumentException("Unknown status: " + dbData);
        };
    }
} 