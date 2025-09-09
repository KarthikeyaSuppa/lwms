package com.lwms.backend.dao;

import com.lwms.backend.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    // This method allows us to find a Role by its name, which is a common requirement for role-based security.
    Optional<Role> findByRoleName(String roleName);
}