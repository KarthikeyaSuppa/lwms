package com.lwms.backend.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Users", indexes = {
        @Index(name = "idx_users_username", columnList = "username"),
        @Index(name = "idx_users_email", columnList = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @NotBlank
    @Size(max = 64)
    @Column(nullable = false, unique=true, length = 64)
    private String username;

    @NotBlank
    @Email
    @Size(max = 254)
    @Column(nullable = false, unique=true, length = 254)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    // Transient field for form binding (not persisted to database)
    @Transient
    private String password;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastLogin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roleId", referencedColumnName = "roleId", nullable = false)
    @JsonIgnore
    private Role role;

    // A no-argument constructor is required by JPA
    public User() {
    }

    // Constructor with all fields
    public User(Integer userId, String username, String email, String passwordHash, String firstName, String lastName, Boolean isActive, LocalDateTime createdAt, LocalDateTime lastLogin, Role role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
        this.role = role;
    }

    // Getters and Setters for all fields
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}