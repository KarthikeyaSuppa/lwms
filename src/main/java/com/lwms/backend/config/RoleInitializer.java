package com.lwms.backend.config;

import com.lwms.backend.dao.RoleRepository;
import com.lwms.backend.entities.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

@Configuration
public class RoleInitializer {

private static final Logger logger = LoggerFactory.getLogger(RoleInitializer.class);

@Bean
@Order(1)
CommandLineRunner initDefaultRoles(RoleRepository roleRepository) {
return args -> {
logger.info("Seeding default roles (idempotent)");

List<Role> defaultRoles = Arrays.asList(
new Role("Admin", "Full system access including users, inventory, shipments, and reports.", 
"{\"users\": \"full\", \"inventory\": \"full\", \"shipments\": \"full\", \"reports\": \"full\", \"settings\": \"full\"}"),
new Role("Manager", "Manages warehouse operations, inventory levels, and reporting.", 
"{\"users\": \"limited\", \"inventory\": \"full\", \"shipments\": \"full\", \"reports\": \"full\"}"),
new Role("Supervisor", "Supervises daily tasks, assigns work, and handles exceptions.", 
"{\"inventory\": \"read_write\", \"shipments\": \"read_write\", \"reports\": \"read\"}"),
new Role("Inventory Controller", "Maintains stock levels, cycle counts, and adjustments.", 
"{\"inventory\": \"read_write\", \"reports\": \"read\"}"),
new Role("Operator", "Executes picking, packing, loading, unloading, and scanning.", 
"{\"inventory\": \"read\", \"shipments\": \"read\"}"),
new Role("Viewer", "Read-only access for monitoring warehouse activities.", 
"{\"inventory\": \"read\", \"shipments\": \"read\", \"reports\": \"read\"}")
);

for (Role role : defaultRoles) {
roleRepository.findByRoleName(role.getRoleName()).orElseGet(() -> {
logger.info("Creating role: {}", role.getRoleName());
return roleRepository.saveAndFlush(role);
});
}

logger.info("Default roles seeding completed");
};
}
}
