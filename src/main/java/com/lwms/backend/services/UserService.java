package com.lwms.backend.services;

import com.lwms.backend.entities.User;
import com.lwms.backend.entities.Role;
import com.lwms.backend.dao.UserRepository;
import com.lwms.backend.dao.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user) {
        logger.info("Attempting to register user with username: {}", user.getUsername());

        // Check if username already exists
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            logger.warn("Registration failed: Username '{}' already exists.", user.getUsername());
            throw new RuntimeException("Username '" + user.getUsername() + "' already exists. Please choose a different one.");
        }

        // Check if email already exists
        Optional<User> existingEmail = userRepository.findByEmail(user.getEmail());
        if (existingEmail.isPresent()) {
            logger.warn("Registration failed: Email '{}' already exists.", user.getEmail());
            throw new RuntimeException("Email '" + user.getEmail() + "' already exists. Please use a different email.");
        }

        user.setPasswordHash(passwordEncoder.encode(user.getPassword()));

        Optional<Role> viewerRole = roleRepository.findByRoleName("Viewer");
        if (viewerRole.isPresent()) {
            user.setRole(viewerRole.get());
            logger.debug("Assigned default role 'Viewer' to user '{}'.", user.getUsername());
        } else {
            logger.error("Default 'Viewer' role not found in the database!");
            throw new RuntimeException("Default 'Viewer' role not found!");
        }

        user.setActive(true);
        User savedUser = userRepository.save(user);
        logger.info("User '{}' registered successfully with role '{}'.", savedUser.getUsername(), savedUser.getRole().getRoleName());

        return savedUser;
    }

    public Optional<User> findByUsername(String username) {
        logger.debug("Searching for user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        logger.debug("Searching for user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    public Optional<User> findWithRoleByUsernameOrEmail(String usernameOrEmail) {
        logger.debug("Fetching user with role by usernameOrEmail: {}", usernameOrEmail);
        Optional<User> userOpt = userRepository.findWithRoleByUsername(usernameOrEmail);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findWithRoleByEmail(usernameOrEmail);
        }
        return userOpt;
    }

    public User authenticateUser(String usernameOrEmail, String password) {
        logger.info("Authenticating user: {}", usernameOrEmail);

        Optional<User> userOpt = findWithRoleByUsernameOrEmail(usernameOrEmail);

        if (userOpt.isEmpty()) {
            logger.warn("Authentication failed: No user found with username/email '{}'.", usernameOrEmail);
            throw new RuntimeException("User not found with username/email: " + usernameOrEmail);
        }

        User user = userOpt.get();

        if (!user.getActive()) {
            logger.warn("Authentication failed: User '{}' is inactive.", user.getUsername());
            throw new RuntimeException("User account is deactivated");
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            logger.warn("Authentication failed: Invalid password for user '{}'.", user.getUsername());
            throw new RuntimeException("Invalid password");
        }

        logger.info("User '{}' authenticated successfully.", user.getUsername());
        return user;
    }

    public User updateUser(Integer userId, String email, String firstName, String lastName, String roleName, Boolean active) {
        logger.info("Updating user id={} with fields: email={}, firstName={}, lastName={}, roleName={}, active={}",
                userId, email, firstName, lastName, roleName, active);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (email != null && !email.equals(user.getEmail())) {
            userRepository.findByEmail(email).ifPresent(existing -> {
                if (!existing.getUserId().equals(userId)) {
                    throw new RuntimeException("Email '" + email + "' is already in use by another user.");
                }
            });
            user.setEmail(email);
        }
        if (firstName != null) {
            user.setFirstName(firstName);
        }
        if (lastName != null) {
            user.setLastName(lastName);
        }
        if (active != null) {
            user.setActive(active);
        }
        if (roleName != null && !roleName.isBlank()) {
            Role role = roleRepository.findByRoleName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            user.setRole(role);
        }

        userRepository.save(user);
        // Re-fetch with role eagerly loaded to avoid lazy serialization issues
        return userRepository.findWithRoleByUserId(userId).orElse(user);
    }

    public List<com.lwms.backend.dto.UserSummaryDto> listUsersWithRoles() {
        return userRepository.findAllBy().stream().map(u -> {
            com.lwms.backend.dto.UserSummaryDto dto = new com.lwms.backend.dto.UserSummaryDto();
            dto.setUserId(u.getUserId());
            dto.setUsername(u.getUsername());
            dto.setEmail(u.getEmail());
            dto.setFirstName(u.getFirstName());
            dto.setLastName(u.getLastName());
            dto.setRoleName(u.getRole() != null ? u.getRole().getRoleName() : null);
            dto.setActive(u.getActive());
            dto.setCreatedAt(u.getCreatedAt());
            dto.setLastLogin(u.getLastLogin());
            return dto;
        }).collect(Collectors.toList());
    }

    public User createUser(String firstName, String lastName, String email, String username, String password, String roleName) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username '" + username + "' already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email '" + email + "' already exists");
        }
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        User u = new User();
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setEmail(email);
        u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(password));
        u.setRole(role);
        u.setActive(true);
        return userRepository.save(u);
    }

    public void deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    public void updateLastLoginForUsername(String usernameOrEmail) {
        findWithRoleByUsernameOrEmail(usernameOrEmail).ifPresent(u -> {
            u.setLastLogin(LocalDateTime.now());
            userRepository.save(u);
            logger.info("Updated lastLogin for user '{}'.", u.getUsername());
        });
    }
}
