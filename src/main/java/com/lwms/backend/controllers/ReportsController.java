package com.lwms.backend.controllers;

import com.lwms.backend.services.ReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/reports/api")
@CrossOrigin(origins = "*")
public class ReportsController {

    @Autowired
    private ReportsService reportsService;

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics(@RequestParam(defaultValue = "30") int days) {
        try {
            Map<String, Object> metrics = reportsService.getKeyMetrics(days);
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/inventory-status")
    public ResponseEntity<?> getInventoryStatus(@RequestParam(defaultValue = "30") int days) {
        try {
            return ResponseEntity.ok(reportsService.getInventoryStatusDistribution(days));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/shipment-status")
    public ResponseEntity<?> getShipmentStatus(@RequestParam(defaultValue = "30") int days) {
        try {
            return ResponseEntity.ok(reportsService.getShipmentStatusOverview(days));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/equipment-heatmap")
    public ResponseEntity<?> getEquipmentHeatmap(@RequestParam(defaultValue = "30") int days) {
        try {
            return ResponseEntity.ok(reportsService.getEquipmentHeatmapData(days));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/movement-trends")
    public ResponseEntity<?> getMovementTrends(@RequestParam(defaultValue = "30") int days) {
        try {
            return ResponseEntity.ok(reportsService.getInventoryMovementTrends(days));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/location-utilization")
    public ResponseEntity<?> getLocationUtilization(@RequestParam(defaultValue = "30") int days) {
        try {
            return ResponseEntity.ok(reportsService.getLocationUtilization(days));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    
}
