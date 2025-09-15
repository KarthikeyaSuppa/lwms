package com.lwms.backend.entities;

import jakarta.persistence.*;
import java.util.Set;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleId;

    @NotBlank
    @Size(max = 50)
    @Column(unique = true, nullable = false, length = 50)
    private String roleName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "JSON")
    private String permissions;

    /**
     * This establishes the inverse side of the relationship.
     * `mappedBy` is crucial: it tells Hibernate that the `role` field in the `User` class
     * is the owner of the relationship, preventing Hibernate from trying to create
     * an unnecessary foreign key column in the "Roles" table.
     */
    @OneToMany(mappedBy = "role")
    private Set<User> users;
    
    // A no-argument constructor is required by JPA
    public Role() {
    }

    // A constructor with all fields for convenience
    public Role(String roleName, String description, String permissions) {
        this.roleName = roleName;
        this.description = description;
        this.permissions = permissions;
    }

    // Getters and Setters for all fields
    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}