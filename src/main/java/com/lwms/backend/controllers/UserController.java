package com.lwms.backend.controllers;

import com.lwms.backend.entities.User;
import com.lwms.backend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/lwms")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, @RequestParam String confirmPassword, Model model) {
        try {
            if (!user.getPassword().equals(confirmPassword)) {
                model.addAttribute("error", "Passwords do not match!");
                model.addAttribute("user", user);
                return "Signup";
            }
            userService.registerUser(user);
            model.addAttribute("success", "Account created successfully! Please login.");
            return "redirect:/lwms/login";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            model.addAttribute("user", user);
            return "Signup";
        }
    }

    @PostMapping("/register/api")
    @ResponseBody
    public ResponseEntity<User> registerUserApi(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }

    @GetMapping("/users")
    @ResponseBody
    public ResponseEntity<java.util.List<com.lwms.backend.dto.UserSummaryDto>> listUsers() {
        return ResponseEntity.ok(userService.listUsersWithRoles());
    }

    @PostMapping("/users")
    @ResponseBody
    public ResponseEntity<com.lwms.backend.dto.UserSummaryDto> createUser(@RequestBody com.lwms.backend.dto.UserCreateRequest request) {
        User created = userService.createUser(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getUsername(),
                request.getPassword(),
                request.getRoleName()
        );
        return ResponseEntity.ok(toSummaryDto(created));
    }

    @PatchMapping("/users/{id}")
    @ResponseBody
    public ResponseEntity<com.lwms.backend.dto.UserSummaryDto> updateUser(@PathVariable("id") Integer userId,
                                           @RequestBody com.lwms.backend.dto.UserUpdateRequest request) {
        User updated = userService.updateUser(
                userId,
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getRoleName(),
                request.getActive()
        );
        return ResponseEntity.ok(toSummaryDto(updated));
    }

    @DeleteMapping("/users/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Integer userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    private com.lwms.backend.dto.UserSummaryDto toSummaryDto(User u) {
        com.lwms.backend.dto.UserSummaryDto dto = new com.lwms.backend.dto.UserSummaryDto();
        dto.setUserId(u.getUserId());
        dto.setUsername(u.getUsername());
        dto.setEmail(u.getEmail());
        dto.setFirstName(u.getFirstName());
        dto.setLastName(u.getLastName());
        dto.setRoleName(u.getRole() != null ? u.getRole().getRoleName() : null);
        dto.setActive(u.getActive());
        dto.setCreatedAt(u.getCreatedAt());
        dto.setLastLogin(u.getLastLogin());
        return dto;
    }
}