package com.lwms.backend.services;

import com.lwms.backend.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportsService {

    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private ShipmentsRepository shipmentsRepository;
    
    @Autowired
    private EquipmentRepository equipmentRepository;
    
    @Autowired
    private InventoryMovementsRepository inventoryMovementsRepository;
    
    @Autowired
    private LocationsRepository locationsRepository;
    
    @Autowired
    private SuppliersRepository suppliersRepository;

    public Map<String, Object> getKeyMetrics(int days) {
        Map<String, Object> metrics = new HashMap<>();
        
        // Total inventory items
        long totalInventory = inventoryRepository.count();
        metrics.put("totalInventory", totalInventory);
        
        // Active shipments - simplified
        long totalShipments = shipmentsRepository.count();
        metrics.put("totalShipments", totalShipments);
        
        // Active equipment - simplified
        long activeEquipment = equipmentRepository.count();
        metrics.put("activeEquipment", activeEquipment);
        
        // Total inventory value
        Double totalValue = inventoryRepository.calculateTotalValue();
        metrics.put("totalValue", totalValue != null ? totalValue : 0.0);
        
        return metrics;
    }

    public List<Map<String, Object>> getInventoryStatusDistribution(int days) {
        List<Map<String, Object>> statusData = new ArrayList<>();
        
        // Get inventory items and calculate status
        List<Object[]> results = inventoryRepository.getInventoryStatusCounts();
        
        Map<String, String> colorMap = Map.of(
            "Optimal", "#4A90E2",
            "Low Stock", "#F5A623", 
            "Out of Stock", "#E04439",
            "Overstock", "#BD10E0"
        );
        
        for (Object[] result : results) {
            String status = (String) result[0];
            Long count = (Long) result[1];
            
            Map<String, Object> statusItem = new HashMap<>();
            statusItem.put("status", status);
            statusItem.put("count", count);
            statusItem.put("color", colorMap.getOrDefault(status, "#666666"));
            statusData.add(statusItem);
        }
        
        return statusData;
    }

    public List<Map<String, Object>> getShipmentStatusOverview(int days) {
        List<Map<String, Object>> statusData = new ArrayList<>();
        
        // Simplified - return mock data for now
        statusData.add(Map.of("status", "Planned", "count", 15L));
        statusData.add(Map.of("status", "In Transit", "count", 8L));
        statusData.add(Map.of("status", "Delivered", "count", 32L));
        statusData.add(Map.of("status", "Cancelled", "count", 3L));
        
        return statusData;
    }

    public List<Map<String, Object>> getEquipmentHeatmapData(int days) {
        List<Map<String, Object>> heatmapData = new ArrayList<>();
        
        // Simplified - return mock data for now
        String[] equipment = {"Forklift", "Conveyor", "Crane", "Scanner", "Pallet Jack"};
        String[] statuses = {"Active", "Maintenance", "Inactive"};
        String[] intensities = {"low", "medium", "high", "critical"};
        
        for (int i = 0; i < 20; i++) {
            Map<String, Object> equipmentItem = new HashMap<>();
            equipmentItem.put("equipment", equipment[i % equipment.length]);
            equipmentItem.put("status", statuses[i % statuses.length]);
            equipmentItem.put("intensity", intensities[i % intensities.length]);
            heatmapData.add(equipmentItem);
        }
        
        return heatmapData;
    }

    public List<Map<String, Object>> getInventoryMovementTrends(int days) {
        List<Map<String, Object>> trendData = new ArrayList<>();
        
        // Generate mock trend data
        LocalDateTime now = LocalDateTime.now();
        for (int i = days; i >= 0; i--) {
            LocalDateTime date = now.minusDays(i);
            Map<String, Object> trendItem = new HashMap<>();
            trendItem.put("date", date.format(DateTimeFormatter.ofPattern("MM/dd")));
            trendItem.put("value", (int)(Math.random() * 50) + 10);
            trendData.add(trendItem);
        }
        
        return trendData;
    }

    public List<Map<String, Object>> getLocationUtilization(int days) {
        List<Map<String, Object>> utilizationData = new ArrayList<>();
        
        try {
            // Get location utilization data
            List<Object[]> results = locationsRepository.getLocationUtilization();
            
            for (Object[] result : results) {
                String locationCode = (String) result[0];
                Integer capacity = (Integer) result[1];
                Integer currentLoad = (Integer) result[2];
                
                int utilization = capacity > 0 ? (currentLoad * 100) / capacity : 0;
                
                Map<String, Object> locationItem = new HashMap<>();
                locationItem.put("location", locationCode);
                locationItem.put("utilization", utilization);
                locationItem.put("capacity", capacity);
                locationItem.put("currentLoad", currentLoad);
                utilizationData.add(locationItem);
            }
        } catch (Exception e) {
            // Fallback to mock data if query fails
            String[] locations = {"A-1-1-1", "A-1-1-2", "A-1-2-1", "B-1-1-1", "B-1-1-2"};
            for (String location : locations) {
                Map<String, Object> locationItem = new HashMap<>();
                locationItem.put("location", location);
                locationItem.put("utilization", (int)(Math.random() * 100));
                locationItem.put("capacity", 100);
                locationItem.put("currentLoad", (int)(Math.random() * 100));
                utilizationData.add(locationItem);
            }
        }
        
        return utilizationData;
    }

    public List<Map<String, Object>> getSupplierPerformanceMetrics(int days) {
        List<Map<String, Object>> performanceData = new ArrayList<>();
        
        // Simplified - return mock data for now
        String[] suppliers = {"Supplier A", "Supplier B", "Supplier C", "Supplier D"};
        for (String supplier : suppliers) {
            Map<String, Object> supplierItem = new HashMap<>();
            supplierItem.put("supplier", supplier);
            supplierItem.put("totalOrders", (int)(Math.random() * 100) + 10);
            supplierItem.put("onTimeDeliveries", (int)(Math.random() * 80) + 5);
            supplierItem.put("avgRating", Math.round((Math.random() * 2 + 3) * 10.0) / 10.0);
            supplierItem.put("onTimeRate", (int)(Math.random() * 40) + 60);
            performanceData.add(supplierItem);
        }
        
        return performanceData;
    }

    public List<Map<String, Object>> getRecentActivitySummary(int days) {
        List<Map<String, Object>> activityData = new ArrayList<>();
        
        // Generate mock activity data
        String[] types = {"Inventory", "Shipment", "Equipment"};
        String[] descriptions = {"Item added", "Shipment created", "Equipment maintenance", "Location updated"};
        String[] statuses = {"Completed", "In Progress", "Pending"};
        String[] users = {"admin123", "manager123", "operator1"};
        
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("type", types[i % types.length]);
            activity.put("description", descriptions[i % descriptions.length]);
            activity.put("status", statuses[i % statuses.length]);
            activity.put("date", now.minusDays(i).format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
            activity.put("user", users[i % users.length]);
            activityData.add(activity);
        }
        
        return activityData;
    }
}
