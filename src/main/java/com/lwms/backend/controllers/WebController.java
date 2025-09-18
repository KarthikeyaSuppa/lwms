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
 * Does: Map page routes to templates and load user/permission context.
 * Input: Authenticated principal and request parameters.
 * Output: View names with model attributes.
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
     * Output: "dashboard" on success, "login" with error on failure.
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
     * Output: Renders "signup" view with empty User for form binding.
     */
    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    /**
     * Serves the main dashboard page.
     * Route: GET /dashboard
     * Does: Resolves authenticated user/role, exposes permissions and settings visibility.
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
            // Fallbacks if user lookup failed but principal exists
            if (!model.containsAttribute("allowSettings")) {
                boolean isAdminOrManager = principal.getAuthorities() != null && principal.getAuthorities().stream()
                    .anyMatch(a -> {
                        String auth = a.getAuthority();
                        return "ROLE_ADMIN".equalsIgnoreCase(auth) || "ROLE_MANAGER".equalsIgnoreCase(auth);
                    });
                model.addAttribute("allowSettings", isAdminOrManager);
            }
        }
        // Ensure safe defaults to avoid template removing elements due to missing model attrs
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
        return "dashboard";
    }

    /**
     * Serves an alternate dashboard layout.
     * Route: GET /dashboard2
     * Does: Same user/permissions resolution as /dashboard, different template.
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
            if (!model.containsAttribute("allowSettings")) {
                boolean isAdminOrManager = principal.getAuthorities() != null && principal.getAuthorities().stream()
                    .anyMatch(a -> {
                        String auth = a.getAuthority();
                        return "ROLE_ADMIN".equalsIgnoreCase(auth) || "ROLE_MANAGER".equalsIgnoreCase(auth);
                    });
                model.addAttribute("allowSettings", isAdminOrManager);
            }
        }
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
        return "dashboard2";
    }

    /**
     * Serves the settings page for authorized roles.
     * Route: GET /settings
     * Does: Loads user and permissions; provides safe defaults when absent.
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
     * Serves the reports page.
     * Route: GET /reports
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

    /**
     * Serves the unauthorized page.
     * Route: GET /unauthorized
     */
    @GetMapping("/unauthorized")
    public String showUnauthorized(
        @AuthenticationPrincipal UserDetails principal,
        Model model
    ) {
        if (principal != null) {
            userService.findWithRoleByUsernameOrEmail(principal.getUsername())
                .ifPresent(u -> model.addAttribute("user", u));
        }
        if (!model.containsAttribute("user")) {
            User fallback = new User();
            fallback.setFirstName("");
            fallback.setLastName("");
            fallback.setUsername("");
            fallback.setEmail("");
            fallback.setActive(true);
            model.addAttribute("user", fallback);
        }
        return "unauthorized";
    }
}