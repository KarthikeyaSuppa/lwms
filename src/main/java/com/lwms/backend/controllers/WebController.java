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
            model.addAttribute("permissionsJson", user.getRole() != null ? user.getRole().getPermissions() : null);
            model.addAttribute("allowSettings", user.getRole() != null && ("admin".equalsIgnoreCase(user.getRole().getRoleName()) || "manager".equalsIgnoreCase(user.getRole().getRoleName())));
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
                .ifPresent(u -> { model.addAttribute("user", u); model.addAttribute("permissionsJson", u.getRole() != null ? u.getRole().getPermissions() : null); model.addAttribute("allowSettings", u.getRole() != null && ("admin".equalsIgnoreCase(u.getRole().getRoleName()) || "manager".equalsIgnoreCase(u.getRole().getRoleName()))); });
        }
        return "dashboard";
    }

    @GetMapping("/dashboard2")
    public String showDashboard2(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> { model.addAttribute("user", u); model.addAttribute("permissionsJson", u.getRole() != null ? u.getRole().getPermissions() : null); model.addAttribute("allowSettings", u.getRole() != null && ("admin".equalsIgnoreCase(u.getRole().getRoleName()) || "manager".equalsIgnoreCase(u.getRole().getRoleName()))); });
        }
        return "dashboard2";
    }

    /**
     * Handles requests for the settings page using the authenticated principal.
     */
    @GetMapping("/settings")
    public String showSettings(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> { model.addAttribute("user", u); model.addAttribute("permissionsJson", u.getRole() != null ? u.getRole().getPermissions() : null); model.addAttribute("allowSettings", u.getRole() != null && ("admin".equalsIgnoreCase(u.getRole().getRoleName()) || "manager".equalsIgnoreCase(u.getRole().getRoleName()))); });
        }
        // Fallbacks to avoid template errors if user not resolved
        if (!model.containsAttribute("user")) {
            com.lwms.backend.entities.User fallback = new com.lwms.backend.entities.User();
            fallback.setFirstName("");
            fallback.setLastName("");
            fallback.setUsername("");
            fallback.setEmail("");
            fallback.setActive(true);
            model.addAttribute("user", fallback);
        }
        if (!model.containsAttribute("permissionsJson")) {
            model.addAttribute("permissionsJson", null);
        }
        if (!model.containsAttribute("allowSettings")) {
            model.addAttribute("allowSettings", false);
        }
        return "settings";
    }

    @GetMapping("/roles")
    public String showRoles(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> { model.addAttribute("user", u); model.addAttribute("permissionsJson", u.getRole() != null ? u.getRole().getPermissions() : null); model.addAttribute("allowSettings", u.getRole() != null && ("admin".equalsIgnoreCase(u.getRole().getRoleName()) || "manager".equalsIgnoreCase(u.getRole().getRoleName()))); });
        }
        return "roles";
    }

    @GetMapping("/suppliers")
    public String showSuppliers(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> { model.addAttribute("user", u); model.addAttribute("permissionsJson", u.getRole() != null ? u.getRole().getPermissions() : null); model.addAttribute("allowSettings", u.getRole() != null && ("admin".equalsIgnoreCase(u.getRole().getRoleName()) || "manager".equalsIgnoreCase(u.getRole().getRoleName()))); });
        }
        return "suppliers";
    }

    @GetMapping("/categories")
    public String showCategories(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> { model.addAttribute("user", u); model.addAttribute("permissionsJson", u.getRole() != null ? u.getRole().getPermissions() : null); model.addAttribute("allowSettings", u.getRole() != null && ("admin".equalsIgnoreCase(u.getRole().getRoleName()) || "manager".equalsIgnoreCase(u.getRole().getRoleName()))); });
        }
        return "categories";
    }

    @GetMapping("/equipment")
    public String showEquipment(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> { model.addAttribute("user", u); model.addAttribute("permissionsJson", u.getRole() != null ? u.getRole().getPermissions() : null); model.addAttribute("allowSettings", u.getRole() != null && ("admin".equalsIgnoreCase(u.getRole().getRoleName()) || "manager".equalsIgnoreCase(u.getRole().getRoleName()))); });
        }
        return "equipment";
    }

    @GetMapping("/locations")
    public String showLocations(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> { model.addAttribute("user", u); model.addAttribute("permissionsJson", u.getRole() != null ? u.getRole().getPermissions() : null); model.addAttribute("allowSettings", u.getRole() != null && ("admin".equalsIgnoreCase(u.getRole().getRoleName()) || "manager".equalsIgnoreCase(u.getRole().getRoleName()))); });
        }
        return "locations";
    }

    @GetMapping("/inventory")
    public String showInventory(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> { model.addAttribute("user", u); model.addAttribute("permissionsJson", u.getRole() != null ? u.getRole().getPermissions() : null); model.addAttribute("allowSettings", u.getRole() != null && ("admin".equalsIgnoreCase(u.getRole().getRoleName()) || "manager".equalsIgnoreCase(u.getRole().getRoleName()))); });
        }
        return "inventory";
    }

    @GetMapping("/shipments")
    public String showShipments(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> { model.addAttribute("user", u); model.addAttribute("permissionsJson", u.getRole() != null ? u.getRole().getPermissions() : null); model.addAttribute("allowSettings", u.getRole() != null && ("admin".equalsIgnoreCase(u.getRole().getRoleName()) || "manager".equalsIgnoreCase(u.getRole().getRoleName()))); });
        }
        return "shipments";
    }

    @GetMapping("/shipment-items")
    public String showShipmentItems(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> { model.addAttribute("user", u); model.addAttribute("permissionsJson", u.getRole() != null ? u.getRole().getPermissions() : null); model.addAttribute("allowSettings", u.getRole() != null && ("admin".equalsIgnoreCase(u.getRole().getRoleName()) || "manager".equalsIgnoreCase(u.getRole().getRoleName()))); });
        }
        return "shipment-items";
    }

    @GetMapping("/maintenance-schedule")
    public String showMaintenanceSchedule(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> { model.addAttribute("user", u); model.addAttribute("permissionsJson", u.getRole() != null ? u.getRole().getPermissions() : null); model.addAttribute("allowSettings", u.getRole() != null && ("admin".equalsIgnoreCase(u.getRole().getRoleName()) || "manager".equalsIgnoreCase(u.getRole().getRoleName()))); });
        }
        return "maintenance-schedule";
    }

    @GetMapping("/inventory-movements")
    public String showInventoryMovements(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> { model.addAttribute("user", u); model.addAttribute("permissionsJson", u.getRole() != null ? u.getRole().getPermissions() : null); model.addAttribute("allowSettings", u.getRole() != null && ("admin".equalsIgnoreCase(u.getRole().getRoleName()) || "manager".equalsIgnoreCase(u.getRole().getRoleName()))); });
        }
        return "inventory-movements";
    }

    @GetMapping("/reports")
    public String showReports(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> { model.addAttribute("user", u); model.addAttribute("permissionsJson", u.getRole() != null ? u.getRole().getPermissions() : null); model.addAttribute("allowSettings", u.getRole() != null && ("admin".equalsIgnoreCase(u.getRole().getRoleName()) || "manager".equalsIgnoreCase(u.getRole().getRoleName()))); });
        }
        return "reports";
    }
}
