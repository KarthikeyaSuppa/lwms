//package com.lwms.backend.services;
//
//import com.lwms.backend.dao.UserRepository;
//import com.lwms.backend.entities.Role;
//import com.lwms.backend.entities.User;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class CustomUserDetailsServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private CustomUserDetailsService customUserDetailsService;
//
//    private User testUser;
//    private Role viewerRole;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        viewerRole = new Role("Viewer", "Read-only access", "{\"reports\":\"read\"}");
//
//        testUser = new User();
//        testUser.setUsername("johndoe");
//        testUser.setPasswordHash("encodedPassword");
//        testUser.setRole(viewerRole);
//    }
//
//    @Test
//    void loadUserByUsername_Success() {
//        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));
//
//        UserDetails userDetails = customUserDetailsService.loadUserByUsername("johndoe");
//
//        assertNotNull(userDetails);
//        assertEquals("johndoe", userDetails.getUsername());
//        assertEquals("encodedPassword", userDetails.getPassword());
//
//        // Verify authority has ROLE_ prefix
//        assertTrue(userDetails.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_Viewer")));
//    }
//
//    @Test
//    void loadUserByUsername_Fails_WhenUserNotFound() {
//        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
//
//        assertThrows(UsernameNotFoundException.class, () ->
//                customUserDetailsService.loadUserByUsername("unknown")
//        );
//    }
//
//    @Test
//    void loadUserByUsername_AssignsCorrectRoleAuthority() {
//        Role adminRole = new Role("Admin", "Full access", "{\"users\":\"full\"}");
//        testUser.setRole(adminRole);
//
//        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));
//
//        UserDetails userDetails = customUserDetailsService.loadUserByUsername("johndoe");
//
//        assertTrue(userDetails.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_Admin")));
//    }
//}
