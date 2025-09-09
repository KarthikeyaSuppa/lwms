//package com.lwms.backend.dao;
//
//import com.lwms.backend.entities.Role;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//class RoleRepositoryTest {
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Test
//    void testFindByRoleName() {
//        Role role = new Role("Manager", "Manages warehouse operations", "{\"inventory\":\"full\"}");
//        roleRepository.save(role);
//
//        Optional<Role> found = roleRepository.findByRoleName("Manager");
//
//        assertThat(found).isPresent();
//        assertThat(found.get().getDescription()).contains("warehouse operations");
//    }
//}
