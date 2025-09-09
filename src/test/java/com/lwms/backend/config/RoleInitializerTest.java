//package com.lwms.backend.config;
//
//import com.lwms.backend.dao.RoleRepository;
//import com.lwms.backend.entities.Role;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.Optional;
//
//import static org.mockito.Mockito.*;
//
//class RoleInitializerTest {
//
//    @Mock
//    private RoleRepository roleRepository;
//
//    @InjectMocks
//    private RoleInitializer roleInitializer;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void initRoles_CreatesMissingRoles() {
//        // Simulate all roles are missing
//        when(roleRepository.findByRoleName(anyString())).thenReturn(Optional.empty());
//
//        roleInitializer.initRoles();
//
//        // Expect save() called 6 times (Admin, Manager, Supervisor, Inventory Controller, Operator, Viewer)
//        verify(roleRepository, times(6)).save(any(Role.class));
//    }
//
//    @Test
//    void initRoles_SkipsExistingRoles() {
//        // Simulate "Admin" exists, others missing
//        when(roleRepository.findByRoleName("Admin")).thenReturn(Optional.of(new Role("Admin", "desc", "{}")));
//        when(roleRepository.findByRoleName(argThat(r -> !r.equals("Admin")))).thenReturn(Optional.empty());
//
//        roleInitializer.initRoles();
//
//        // Save called only 5 times, since Admin already exists
//        verify(roleRepository, times(5)).save(any(Role.class));
//        verify(roleRepository, never()).save(argThat(role -> role.getRoleName().equals("Admin")));
//    }
//
//    @Test
//    void initRoles_Idempotent_WhenCalledTwice() {
//        // Simulate no roles exist initially
//        when(roleRepository.findByRoleName(anyString())).thenReturn(Optional.empty());
//
//        roleInitializer.initRoles();
//        roleInitializer.initRoles(); // run twice
//
//        // Even if called twice, save should be 12 total (6 + 6), because we mocked repository as always empty
//        verify(roleRepository, times(12)).save(any(Role.class));
//    }
//}
