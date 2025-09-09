package com.lwms.backend.config;

import com.lwms.backend.dao.RoleRepository;
import com.lwms.backend.entities.Role;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Ensures required roles exist in the database at application startup.
 * - Single Responsibility: Only manages role initialization.
 * - Open/Closed: Can add more roles without changing core logic.
 * - Dependency Inversion: Relies on RoleRepository abstraction, not implementation.
 */
@Component
public class RoleInitializer {

    private static final Logger logger = LoggerFactory.getLogger(RoleInitializer.class);

    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void initRoles() {
        logger.info("Starting role initialization...");

        List<Role> predefinedRoles = getPredefinedRoles();

        predefinedRoles.forEach(role -> 
            roleRepository.findByRoleName(role.getRoleName())
                .ifPresentOrElse(
                    existingRole -> logger.debug("Role '{}' already exists, skipping.", existingRole.getRoleName()),
                    () -> {
                        roleRepository.save(role);
                        logger.info("Role '{}' created successfully.", role.getRoleName());
                    }
                )
        );

        logger.info("Role initialization complete.");
    }

    private List<Role> getPredefinedRoles() {
        return Arrays.asList(
                new Role("Admin",
                        "Full system access including users, inventory, shipments, and reports.",
                        "{\"users\":\"full\",\"reports\":\"full\",\"settings\":\"full\",\"inventory\":\"full\",\"shipments\":\"full\"}"),
                new Role("Manager",
                        "Manages warehouse operations, inventory levels, and reporting.",
                        "{\"users\":\"limited\",\"reports\":\"full\",\"inventory\":\"full\",\"shipments\":\"full\"}"),
                new Role("Supervisor",
                        "Supervises daily tasks, assigns work, and handles exceptions.",
                        "{\"reports\":\"read\",\"inventory\":\"read_write\",\"shipments\":\"read_write\"}"),
                new Role("Inventory Controller",
                        "Maintains stock levels, cycle counts, and adjustments.",
                        "{\"reports\":\"read\",\"inventory\":\"read_write\"}"),
                new Role("Operator",
                        "Executes picking, packing, loading, unloading, and scanning.",
                        "{\"inventory\":\"read\",\"shipments\":\"read\"}"),
                new Role("Viewer",
                        "Read-only access for monitoring warehouse activities.",
                        "{\"reports\":\"read\",\"inventory\":\"read\",\"shipments\":\"read\"}")
        );
    }
}
