package com.lwms.backend.config;

import com.lwms.backend.dao.RoleRepository;
import com.lwms.backend.dao.UserRepository;
import com.lwms.backend.entities.Role;
import com.lwms.backend.entities.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Configuration
public class RoleInitializer {

	@Bean
	@Transactional
	CommandLineRunner initDefaultUsers(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder) {
		return args -> {
			// Ensure roles exist
			Role adminRole = roleRepository.findByRoleName("Admin").orElseGet(() -> {
				Role r = new Role(); r.setRoleName("Admin"); r.setDescription("Administrator"); r.setPermissions("{\"users\":\"full\",\"reports\":\"full\",\"settings\":\"full\",\"inventory\":\"full\",\"shipments\":\"full\"}");
				return roleRepository.save(r);
			});
			Role managerRole = roleRepository.findByRoleName("Manager").orElseGet(() -> {
				Role r = new Role(); r.setRoleName("Manager"); r.setDescription("Manager"); r.setPermissions("{\"users\":\"limited\",\"reports\":\"full\",\"inventory\":\"full\",\"shipments\":\"full\"}");
				return roleRepository.save(r);
			});

			// Admin user
			if (userRepository.findByUsername("admin123").isEmpty() && userRepository.findByEmail("admin@cognizant.com").isEmpty()) {
				User u = new User();
				u.setUsername("admin123");
				u.setEmail("admin@cognizant.com");
				u.setFirstName("admin");
				u.setLastName("cogni");
				u.setPasswordHash(encoder.encode("SuperCardboard@123"));
				u.setRole(adminRole);
				u.setActive(true);
				userRepository.save(u);
			}

			// Manager user
			if (userRepository.findByUsername("manager123").isEmpty() && userRepository.findByEmail("manager@cognizant.com").isEmpty()) {
				User u = new User();
				u.setUsername("manager123");
				u.setEmail("manager@cognizant.com");
				u.setFirstName("manager");
				u.setLastName("cogni");
				u.setPasswordHash(encoder.encode("SuperCardboard@456"));
				u.setRole(managerRole);
				u.setActive(true);
				userRepository.save(u);
			}
		};
	}
}
