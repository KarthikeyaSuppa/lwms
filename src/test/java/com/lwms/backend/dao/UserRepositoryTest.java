//  package com.lwms.backend.dao;
//
//import com.lwms.backend.entities.Role;
//import com.lwms.backend.entities.User;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//class UserRepositoryTest {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Test
//    void testFindByUsername() {
//        Role role = roleRepository.save(new Role("Viewer", "Read-only access", "{\"reports\":\"read\"}"));
//
//        User user = new User();
//        user.setUsername("johndoe");
//        user.setEmail("john@example.com");
//        user.setPasswordHash("hashedPassword");
//        user.setRole(role);
//        user.setActive(true);
//
//        userRepository.save(user);
//
//        Optional<User> found = userRepository.findByUsername("johndoe");
//
//        assertThat(found).isPresent();
//        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
//    }
//
//    @Test
//    void testFindByEmail() {
//        Role role = roleRepository.save(new Role("Viewer", "Read-only access", "{\"reports\":\"read\"}"));
//
//        User user = new User();
//        user.setUsername("janedoe");
//        user.setEmail("jane@example.com");
//        user.setPasswordHash("hashedPassword");
//        user.setRole(role);
//        user.setActive(true);
//
//        userRepository.save(user);
//
//        Optional<User> found = userRepository.findByEmail("jane@example.com");
//
//        assertThat(found).isPresent();
//        assertThat(found.get().getUsername()).isEqualTo("janedoe");
//    }
//}
