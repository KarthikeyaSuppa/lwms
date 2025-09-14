package com.lwms.backend.controllers;

import com.lwms.backend.entities.User;
import com.lwms.backend.services.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller class to handle web page requests and serve HTML templates.
 */
@CrossOrigin
@Controller
public class WebController {

    private final UserService userService;

    public WebController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handles requests for the login page.
     * @return The name of the login view (login.html).
     */
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "errorMessage", required = false) String errorMessage,
                                Model model) {
        if (errorMessage != null) {
            model.addAttribute("error", errorMessage);
        }
        return "login";
    }

    /**
     * Handles login form submission.
     * @param usernameOrEmail The username or email entered by user.
     * @param password The password entered by user.
     * @param model The model to add attributes to.
     * @return The dashboard page on success, or login page with error.
     */
    @PostMapping("/login")
    public String handleLogin(@RequestParam String usernameOrEmail, 
                             @RequestParam String password, 
                             Model model) {
        try {
            User user = userService.authenticateUser(usernameOrEmail, password);
            model.addAttribute("user", user);
            return "dashboard";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    /**
     * Handles requests for the signup page.
     * @param model The model to add attributes to.
     * @return The name of the signup view (signup.html).
     */
    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    /**
     * Handles requests for the dashboard page using the authenticated principal.
     */
    @GetMapping("/dashboard")
    public String showDashboard(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> model.addAttribute("user", u));
        }
        return "dashboard";
    }

    /**
     * Handles requests for the settings page using the authenticated principal.
     */
    @GetMapping("/settings")
    public String showSettings(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> model.addAttribute("user", u));
        }
        return "settings";
    }

    @GetMapping("/suppliers")
    public String showSuppliers(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> model.addAttribute("user", u));
        }
        return "suppliers";
    }

    @GetMapping("/categories")
    public String showCategories(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> model.addAttribute("user", u));
        }
        return "categories";
    }

    @GetMapping("/equipment")
    public String showEquipment(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> model.addAttribute("user", u));
        }
        return "equipment";
    }

    @GetMapping("/locations")
    public String showLocations(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> model.addAttribute("user", u));
        }
        return "locations";
    }

    @GetMapping("/inventory")
    public String showInventory(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> model.addAttribute("user", u));
        }
        return "inventory";
    }

    @GetMapping("/shipments")
    public String showShipments(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> model.addAttribute("user", u));
        }
        return "shipments";
    }
}
