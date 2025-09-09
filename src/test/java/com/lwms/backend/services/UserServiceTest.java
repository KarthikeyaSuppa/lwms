package com.lwms.backend.services;

import com.lwms.backend.dao.RoleRepository;
import com.lwms.backend.dao.UserRepository;
import com.lwms.backend.entities.Role;
import com.lwms.backend.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Role viewerRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setUsername("johndoe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("Password123!");
        testUser.setPasswordHash("hashedPassword");
        testUser.setActive(true);

        viewerRole = new Role("Viewer", "Read-only access", "{\"reports\":\"read\"}");
    }

    // ✅ Successful registration
    @Test
    void registerUser_Success() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password123!")).thenReturn("encodedPassword");
        when(roleRepository.findByRoleName("Viewer")).thenReturn(Optional.of(viewerRole));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User registeredUser = userService.registerUser(testUser);

        assertNotNull(registeredUser);
        assertEquals("johndoe", registeredUser.getUsername());
        assertEquals("encodedPassword", registeredUser.getPasswordHash());
        assertEquals(viewerRole, registeredUser.getRole());
        assertTrue(registeredUser.getActive());
    }

    // ❌ Username exists
    @Test
    void registerUser_ShouldThrow_WhenUsernameExists() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                userService.registerUser(testUser)
        );

        assertEquals("Username 'johndoe' already exists. Please choose a different one.", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    // ❌ Viewer role missing
    @Test
    void registerUser_ShouldThrow_WhenViewerRoleMissing() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password123!")).thenReturn("encodedPassword");
        when(roleRepository.findByRoleName("Viewer")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                userService.registerUser(testUser)
        );

        assertEquals("Default 'Viewer' role not found!", ex.getMessage());
    }

    // ✅ Authentication with username
    @Test
    void authenticateUser_Success_WithUsername() {
        testUser.setPasswordHash("encodedPassword");

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123!", "encodedPassword")).thenReturn(true);

        User authenticated = userService.authenticateUser("johndoe", "Password123!");

        assertNotNull(authenticated);
        assertEquals("johndoe", authenticated.getUsername());
    }

    // ✅ Authentication with email
    @Test
    void authenticateUser_Success_WithEmail() {
        testUser.setPasswordHash("encodedPassword");

        when(userRepository.findByUsername("john@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123!", "encodedPassword")).thenReturn(true);

        User authenticated = userService.authenticateUser("john@example.com", "Password123!");

        assertNotNull(authenticated);
        assertEquals("john@example.com", authenticated.getEmail());
    }

    // ❌ User not found
    @Test
    void authenticateUser_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("unknown")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                userService.authenticateUser("unknown", "Password123!")
        );

        assertEquals("User not found with username/email: unknown", ex.getMessage());
    }

    // ❌ User inactive
    @Test
    void authenticateUser_ShouldThrow_WhenUserInactive() {
        testUser.setActive(false);
        testUser.setPasswordHash("encodedPassword");

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                userService.authenticateUser("johndoe", "Password123!")
        );

        assertEquals("User account is deactivated", ex.getMessage());
    }

    // ❌ Invalid password
    @Test
    void authenticateUser_ShouldThrow_WhenPasswordInvalid() {
        testUser.setPasswordHash("encodedPassword");

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                userService.authenticateUser("johndoe", "wrongPassword")
        );

        assertEquals("Invalid password", ex.getMessage());
    }
}
