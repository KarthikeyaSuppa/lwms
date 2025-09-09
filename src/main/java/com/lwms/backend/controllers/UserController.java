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
            // Check if passwords match
            if (!user.getPassword().equals(confirmPassword)) {
                model.addAttribute("error", "Passwords do not match!");
                model.addAttribute("user", user);
                return "Signup"; // 
            }
            
            // Register the user
            userService.registerUser(user);
            model.addAttribute("success", "Account created successfully! Please login.");
            return "redirect:/lwms/login"; 
            
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            model.addAttribute("user", user);
            return "Signup"; 
        }
    }

    // Keep the REST API endpoint for AJAX calls if needed
    @PostMapping("/register/api")
    @ResponseBody
    public ResponseEntity<User> registerUserApi(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }
}