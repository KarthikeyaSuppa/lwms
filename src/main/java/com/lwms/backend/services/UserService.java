package com.lwms.backend.services;

import com.lwms.backend.entities.User;
import com.lwms.backend.entities.Role;
import com.lwms.backend.dao.UserRepository;
import com.lwms.backend.dao.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
}
