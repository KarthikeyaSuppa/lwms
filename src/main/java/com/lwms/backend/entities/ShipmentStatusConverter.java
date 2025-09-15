package com.lwms.backend.entities;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ShipmentStatusConverter implements AttributeConverter<Shipments.Status, String> {
    @Override
    public String convertToDatabaseColumn(Shipments.Status attribute) {
        if (attribute == null) return null;
        return switch (attribute) {
            case Planned -> "Planned";
            case In_Transit -> "In Transit";
            case Delivered -> "Delivered";
            case Cancelled -> "Cancelled";
        };
    }

    @Override
    public Shipments.Status convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return switch (dbData) {
            case "Planned" -> Shipments.Status.Planned;
            case "In Transit" -> Shipments.Status.In_Transit;
            case "Delivered" -> Shipments.Status.Delivered;
            case "Cancelled" -> Shipments.Status.Cancelled;
            default -> throw new IllegalArgumentException("Unknown status: " + dbData);
        };
    }
} 