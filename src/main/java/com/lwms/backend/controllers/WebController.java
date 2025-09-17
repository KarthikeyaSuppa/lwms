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
 * MVC Web controller responsible for serving Thymeleaf views (HTML pages).
 * Connects UI routes to underlying services.
 * Uses UserService to resolve the authenticated user and role-based permissions.
 * Relies on Spring Security for authentication context via AuthenticationPrincipal.
 */
@CrossOrigin
@Controller
public class WebController {

    private final UserService userService;

    /**
     * Constructs the controller with required dependencies.
     * @param userService Service providing user authentication and user+role lookups.
     */
    public WebController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Serves the login page.
     * Route: GET /login
     * Inputs: optional query param "errorMessage" to show authentication errors.
     * Output: Renders "login" view with error message if present.
     * @param errorMessage Optional error text (set by failure handler or redirect).
     * @param model Spring MVC model for view attributes.
     * @return The "login" template.
     */
    @GetMapping("/login")
    public String showLoginPage(
        @RequestParam(value = "errorMessage", required = false) String errorMessage,
        Model model
    ) {
        if (errorMessage != null) {
            model.addAttribute("error", errorMessage);
        }
        return "login";
    }

    /**
     * Handles login form submission (programmatic authentication path).
     * Route: POST /login
     * Inputs: "usernameOrEmail", "password" from form.
     * Behavior: Delegates to UserService.authenticateUser.
     * On success: Adds user + permissions to model, returns "dashboard".
     * On failure: Adds error message to model, returns "login".
     * Note: Also compatible with Spring Security formLogin configuration.
     * @param usernameOrEmail Username or email supplied by user.
     * @param password Plain-text password supplied by user.
     * @param model Spring MVC model for view attributes.
     * @return "dashboard" when authenticated, else "login".
     */
    @PostMapping("/login")
    public String handleLogin(
        @RequestParam String usernameOrEmail, 
        @RequestParam String password, 
        Model model
    ) {
        try {
            User user = userService.authenticateUser(usernameOrEmail, password);
            model.addAttribute("user", user);
            model.addAttribute(
                "permissionsJson",
                user.getRole() != null ? user.getRole().getPermissions() : null
            );
            model.addAttribute(
                "allowSettings",
                user.getRole() != null &&
                ("admin".equalsIgnoreCase(user.getRole().getRoleName()) ||
                 "manager".equalsIgnoreCase(user.getRole().getRoleName()))
            );
            return "dashboard";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    /**
     * Serves the signup page.
     * Route: GET /signup
     * Behavior: Adds empty User to model for form binding.
     * @param model Spring MVC model for view attributes.
     * @return The "signup" template.
     */
    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    /**
     * Serves the main dashboard page.
     * Route: GET /dashboard
     * Behavior: Resolves authenticated principal and loads full user with role,
     * exposes role permissions and settings visibility to the view.
     * @param principal Authenticated Spring Security principal (may be null).
     * @param model Spring MVC model for view attributes.
     * @return The "dashboard" template.
     */
    @GetMapping("/dashboard")
    public String showDashboard(
        @AuthenticationPrincipal UserDetails principal,
        Model model
    ) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> {
                    model.addAttribute("user", u);
                    model.addAttribute(
                        "permissionsJson",
                        u.getRole() != null ? u.getRole().getPermissions() : null
                    );
                    model.addAttribute(
                        "allowSettings",
                        u.getRole() != null &&
                        ("admin".equalsIgnoreCase(u.getRole().getRoleName()) ||
                         "manager".equalsIgnoreCase(u.getRole().getRoleName()))
                    );
                });
        }
        return "dashboard";
    }

    /**
     * Serves an alternate dashboard layout.
     * Route: GET /dashboard2
     * Behavior: Same user/permissions resolution as /dashboard, different template.
     * @param principal Authenticated principal.
     * @param model View model.
     * @return The "dashboard2" template.
     */
    @GetMapping("/dashboard2")
    public String showDashboard2(
        @AuthenticationPrincipal UserDetails principal,
        Model model
    ) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> {
                    model.addAttribute("user", u);
                    model.addAttribute(
                        "permissionsJson",
                        u.getRole() != null ? u.getRole().getPermissions() : null
                    );
                    model.addAttribute(
                        "allowSettings",
                        u.getRole() != null &&
                        ("admin".equalsIgnoreCase(u.getRole().getRoleName()) ||
                         "manager".equalsIgnoreCase(u.getRole().getRoleName()))
                    );
                });
        }
        return "dashboard2";
    }

    /**
     * Serves the settings page for authorized roles.
     * Route: GET /settings
     * Behavior: Attempts to load authenticated user and permissions. Provides fallbacks
     * to avoid template errors if principal is not available.
     * @param principal Authenticated principal (may be null).
     * @param model View model with user and permission context.
     * @return The "settings" template.
     */
    @GetMapping("/settings")
    public String showSettings(
        @AuthenticationPrincipal UserDetails principal,
        Model model
    ) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> {
                    model.addAttribute("user", u);
                    model.addAttribute(
                        "permissionsJson",
                        u.getRole() != null ? u.getRole().getPermissions() : null
                    );
                    model.addAttribute(
                        "allowSettings",
                        u.getRole() != null &&
                        ("admin".equalsIgnoreCase(u.getRole().getRoleName()) ||
                         "manager".equalsIgnoreCase(u.getRole().getRoleName()))
                    );
                });
        }
        // Fallbacks to avoid template errors if user not resolved
        if (!model.containsAttribute("user")) {
            User fallback = new User();
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

    /**
     * Serves the roles management page.
     * Route: GET /roles
     * @param principal Authenticated principal.
     * @param model View model with user/permissions.
     * @return The "roles" template.
     */
    @GetMapping("/roles")
    public String showRoles(
        @AuthenticationPrincipal UserDetails principal,
        Model model
    ) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> {
                    model.addAttribute("user", u);
                    model.addAttribute(
                        "permissionsJson",
                        u.getRole() != null ? u.getRole().getPermissions() : null
                    );
                    model.addAttribute(
                        "allowSettings",
                        u.getRole() != null &&
                        ("admin".equalsIgnoreCase(u.getRole().getRoleName()) ||
                         "manager".equalsIgnoreCase(u.getRole().getRoleName()))
                    );
                });
        }
        return "roles";
    }

    /**
     * Serves the suppliers page.
     * Route: GET /suppliers
     * @param principal Authenticated principal.
     * @param model View model with user/permissions.
     * @return The "suppliers" template.
     */
    @GetMapping("/suppliers")
    public String showSuppliers(
        @AuthenticationPrincipal UserDetails principal,
        Model model
    ) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> {
                    model.addAttribute("user", u);
                    model.addAttribute(
                        "permissionsJson",
                        u.getRole() != null ? u.getRole().getPermissions() : null
                    );
                    model.addAttribute(
                        "allowSettings",
                        u.getRole() != null &&
                        ("admin".equalsIgnoreCase(u.getRole().getRoleName()) ||
                         "manager".equalsIgnoreCase(u.getRole().getRoleName()))
                    );
                });
        }
        return "suppliers";
    }

    /**
     * Serves the categories page.
     * Route: GET /categories
     * @param principal Authenticated principal.
     * @param model View model with user/permissions.
     * @return The "categories" template.
     */
    @GetMapping("/categories")
    public String showCategories(
        @AuthenticationPrincipal UserDetails principal,
        Model model
    ) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> {
                    model.addAttribute("user", u);
                    model.addAttribute(
                        "permissionsJson",
                        u.getRole() != null ? u.getRole().getPermissions() : null
                    );
                    model.addAttribute(
                        "allowSettings",
                        u.getRole() != null &&
                        ("admin".equalsIgnoreCase(u.getRole().getRoleName()) ||
                         "manager".equalsIgnoreCase(u.getRole().getRoleName()))
                    );
                });
        }
        return "categories";
    }

    /**
     * Serves the equipment page.
     * Route: GET /equipment
     * @param principal Authenticated principal.
     * @param model View model with user/permissions.
     * @return The "equipment" template.
     */
    @GetMapping("/equipment")
    public String showEquipment(
        @AuthenticationPrincipal UserDetails principal,
        Model model
    ) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> {
                    model.addAttribute("user", u);
                    model.addAttribute(
                        "permissionsJson",
                        u.getRole() != null ? u.getRole().getPermissions() : null
                    );
                    model.addAttribute(
                        "allowSettings",
                        u.getRole() != null &&
                        ("admin".equalsIgnoreCase(u.getRole().getRoleName()) ||
                         "manager".equalsIgnoreCase(u.getRole().getRoleName()))
                    );
                });
        }
        return "equipment";
    }

    /**
     * Serves the locations page.
     * Route: GET /locations
     * @param principal Authenticated principal.
     * @param model View model with user/permissions.
     * @return The "locations" template.
     */
    @GetMapping("/locations")
    public String showLocations(
        @AuthenticationPrincipal UserDetails principal,
        Model model
    ) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> {
                    model.addAttribute("user", u);
                    model.addAttribute(
                        "permissionsJson",
                        u.getRole() != null ? u.getRole().getPermissions() : null
                    );
                    model.addAttribute(
                        "allowSettings",
                        u.getRole() != null &&
                        ("admin".equalsIgnoreCase(u.getRole().getRoleName()) ||
                         "manager".equalsIgnoreCase(u.getRole().getRoleName()))
                    );
                });
        }
        return "locations";
    }

    /**
     * Serves the inventory page.
     * Route: GET /inventory
     * @param principal Authenticated principal.
     * @param model View model with user/permissions.
     * @return The "inventory" template.
     */
    @GetMapping("/inventory")
    public String showInventory(
        @AuthenticationPrincipal UserDetails principal,
        Model model
    ) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> {
                    model.addAttribute("user", u);
                    model.addAttribute(
                        "permissionsJson",
                        u.getRole() != null ? u.getRole().getPermissions() : null
                    );
                    model.addAttribute(
                        "allowSettings",
                        u.getRole() != null &&
                        ("admin".equalsIgnoreCase(u.getRole().getRoleName()) ||
                         "manager".equalsIgnoreCase(u.getRole().getRoleName()))
                    );
                });
        }
        return "inventory";
    }

    /**
     * Serves the shipments page.
     * Route: GET /shipments
     * @param principal Authenticated principal.
     * @param model View model with user/permissions.
     * @return The "shipments" template.
     */
    @GetMapping("/shipments")
    public String showShipments(
        @AuthenticationPrincipal UserDetails principal,
        Model model
    ) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> {
                    model.addAttribute("user", u);
                    model.addAttribute(
                        "permissionsJson",
                        u.getRole() != null ? u.getRole().getPermissions() : null
                    );
                    model.addAttribute(
                        "allowSettings",
                        u.getRole() != null &&
                        ("admin".equalsIgnoreCase(u.getRole().getRoleName()) ||
                         "manager".equalsIgnoreCase(u.getRole().getRoleName()))
                    );
                });
        }
        return "shipments";
    }


    /**
     * Serves the maintenance schedule page.
     * Route: GET /maintenance-schedule
     * @param principal Authenticated principal.
     * @param model View model with user/permissions.
     * @return The "maintenance-schedule" template.
     */
    @GetMapping("/maintenance-schedule")
    public String showMaintenanceSchedule(
        @AuthenticationPrincipal UserDetails principal,
        Model model
    ) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> {
                    model.addAttribute("user", u);
                    model.addAttribute(
                        "permissionsJson",
                        u.getRole() != null ? u.getRole().getPermissions() : null
                    );
                    model.addAttribute(
                        "allowSettings",
                        u.getRole() != null &&
                        ("admin".equalsIgnoreCase(u.getRole().getRoleName()) ||
                         "manager".equalsIgnoreCase(u.getRole().getRoleName()))
                    );
                });
        }
        return "maintenance-schedule";
    }

    /**
     * Serves the inventory movements page.
     * Route: GET /inventory-movements
     * @param principal Authenticated principal.
     * @param model View model with user/permissions.
     * @return The "inventory-movements" template.
     */
    @GetMapping("/inventory-movements")
    public String showInventoryMovements(
        @AuthenticationPrincipal UserDetails principal,
        Model model
    ) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> {
                    model.addAttribute("user", u);
                    model.addAttribute(
                        "permissionsJson",
                        u.getRole() != null ? u.getRole().getPermissions() : null
                    );
                    model.addAttribute(
                        "allowSettings",
                        u.getRole() != null &&
                        ("admin".equalsIgnoreCase(u.getRole().getRoleName()) ||
                         "manager".equalsIgnoreCase(u.getRole().getRoleName()))
                    );
                });
        }
        return "inventory-movements";
    }

    /**
     * Serves the reports page.
     * Route: GET /reports
     * @param principal Authenticated principal.
     * @param model View model with user/permissions.
     * @return The "reports" template.
     */
    @GetMapping("/reports")
    public String showReports(
        @AuthenticationPrincipal UserDetails principal,
        Model model
    ) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> {
                    model.addAttribute("user", u);
                    model.addAttribute(
                        "permissionsJson",
                        u.getRole() != null ? u.getRole().getPermissions() : null
                    );
                    model.addAttribute(
                        "allowSettings",
                        u.getRole() != null &&
                        ("admin".equalsIgnoreCase(u.getRole().getRoleName()) ||
                         "manager".equalsIgnoreCase(u.getRole().getRoleName()))
                    );
                });
        }
        return "reports";
    }
}