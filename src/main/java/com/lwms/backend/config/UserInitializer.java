package com.lwms.backend.config;

import com.lwms.backend.dao.RoleRepository;
import com.lwms.backend.dao.UserRepository;
import com.lwms.backend.entities.Role;
import com.lwms.backend.entities.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;

import java.util.Optional;

@Configuration
public class UserInitializer {

private static final Logger logger = LoggerFactory.getLogger(UserInitializer.class);

@Bean
@Order(2)
CommandLineRunner initDefaultUsers(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder) {
return args -> {
logger.info("Seeding default users (idempotent)");

// Find existing roles
Role adminRole = roleRepository.findByRoleName("Admin")
.orElseThrow(() -> new RuntimeException("Admin role not found. Ensure RoleInitializer runs first."));
Role managerRole = roleRepository.findByRoleName("Manager")
.orElseThrow(() -> new RuntimeException("Manager role not found. Ensure RoleInitializer runs first."));

upsertUser(userRepository, encoder,
"admin123", "admin@cognizant.com", "admin", "cogni", "SuperCardboard@123", adminRole);
upsertUser(userRepository, encoder,
"manager123", "manager@cognizant.com", "manager", "cogni", "SuperCardboard@456", managerRole);
};
}

@Transactional
void upsertUser(UserRepository userRepository, PasswordEncoder encoder,
String username, String email, String firstName, String lastName, String rawPassword, Role role) {
Optional<User> byUsername = userRepository.findByUsername(username);
Optional<User> byEmail = userRepository.findByEmail(email);
User u = byUsername.orElseGet(() -> byEmail.orElse(null));
if (u == null) {
logger.info("Creating default user '{}' (role={})", username, role.getRoleName());
u = new User();
u.setUsername(username);
u.setEmail(email);
u.setFirstName(firstName);
u.setLastName(lastName);
u.setPasswordHash(encoder.encode(rawPassword));
u.setRole(role);
u.setActive(true);
u.setLastLogin(LocalDateTime.now());
u = userRepository.saveAndFlush(u);
logger.info("Default user created: id={} username={}", u.getUserId(), u.getUsername());
} else {
logger.info("Ensuring default user '{}' exists and is updated (role={})", username, role.getRoleName());
u.setUsername(username);
u.setEmail(email);
u.setFirstName(firstName);
u.setLastName(lastName);
u.setPasswordHash(encoder.encode(rawPassword));
u.setRole(role);
u.setActive(true);
u = userRepository.saveAndFlush(u);
logger.info("Default user upserted: id={} username={}", u.getUserId(), u.getUsername());
}
}
}
