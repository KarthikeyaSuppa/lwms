//package com.lwms.backend.dao;
//
//import com.lwms.backend.entities.Role;
//import com.lwms.backend.entities.User;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//import java.util.Set;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//class UserRoleRelationshipTest {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Test
//    void testUserSavedWithRole() {
//        Role viewer = new Role("Viewer", "Read-only access", "{\"reports\":\"read\"}");
//        roleRepository.save(viewer);
//
//        User user = new User();
//        user.setUsername("alice");
//        user.setEmail("alice@example.com");
//        user.setPasswordHash("hashedPassword");
//        user.setRole(viewer);
//        user.setActive(true);
//
//        userRepository.save(user);
//
//        User found = userRepository.findByUsername("alice").orElseThrow();
//        assertThat(found.getRole()).isNotNull();
//        assertThat(found.getRole().getRoleName()).isEqualTo("Viewer");
//    }
//
//    @Test
//    void testRoleHasUsers() {
//        Role manager = new Role("Manager", "Manages operations", "{\"inventory\":\"full\"}");
//        roleRepository.save(manager);
//
//        User user1 = new User();
//        user1.setUsername("bob");
//        user1.setEmail("bob@example.com");
//        user1.setPasswordHash("pass1");
//        user1.setRole(manager);
//        user1.setActive(true);
//
//        User user2 = new User();
//        user2.setUsername("carol");
//        user2.setEmail("carol@example.com");
//        user2.setPasswordHash("pass2");
//        user2.setRole(manager);
//        user2.setActive(true);
//
//        userRepository.saveAll(Set.of(user1, user2));
//
//        Role foundRole = roleRepository.findByRoleName("Manager").orElseThrow();
//        assertThat(foundRole.getUsers()).hasSize(2);
//        assertThat(foundRole.getUsers())
//                .extracting(User::getUsername)
//                .containsExactlyInAnyOrder("bob", "carol");
//    }
//}
