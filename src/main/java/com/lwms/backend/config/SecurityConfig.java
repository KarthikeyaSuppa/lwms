package com.lwms.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.lwms.backend.services.UserService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * What: Provides a password encoder bean using BCrypt for hashing.
     * Inputs: None (Spring injects where needed).
     * Sends/How: Returned bean is used by the authentication provider to verify passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * What: Builds a DaoAuthenticationProvider wired with our UserDetailsService and PasswordEncoder.
     * Inputs: userDetailsService (to load users), passwordEncoder (to check credentials).
     * Does: Configures the provider and disables hiding of UsernameNotFound exceptions..
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setHideUserNotFoundExceptions(false); // expose UsernameNotFoundException
        return provider;
    }

    /**
     * What: Logout success handler that updates last login on logout and redirects to /login?logout.
     * Inputs: userService for updating last-login; request/response/authentication via Spring at runtime.
     * Does: If the current principal is a UserDetails, update last-login; then redirect.
     */
    @Bean
    public LogoutSuccessHandler updatingLogoutSuccessHandler(UserService userService) {
        return (request, response, authentication) -> {
            if (authentication != null) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof UserDetails userDetails) {
                    userService.updateLastLoginForUsername(userDetails.getUsername());
                }
            }
            response.sendRedirect("/login?logout");
        };
    }

    /**
     * What: AccessDeniedHandler that redirects unauthorized requests to /unauthorized.
     * Inputs: request/response/exception injected by Spring.
     * Does: Swallows the exception and performs a redirect.
     * Sends/How: HTTP 302 redirect to /unauthorized.
     */
    @Bean
    public AccessDeniedHandler redirectAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> response.sendRedirect("/unauthorized");
    }

    /**
     * What: Central HTTP security configuration (CSRF, URL authorization, exception routing, form-login, logout).
     * Inputs: http (to configure the chain), authenticationProvider (for auth), updatingLogoutSuccessHandler (on logout).
     * Does: Defines public endpoints, role-gated routes, denies redirect to /unauthorized, and configures login failure messages.
     * Sends/How: Constructs and returns the SecurityFilterChain consumed by Spring Security at runtime.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, DaoAuthenticationProvider authenticationProvider,
                                                   LogoutSuccessHandler updatingLogoutSuccessHandler) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/login", "/signup", "/lwms/register", "/api/users/register",
                        "/css/**", "/js/**", "/images/**", "/favicon.ico", "/unauthorized").permitAll()
                // Role-based page access
                .requestMatchers(
                        "/settings"
                ).hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(
                        "/reports"
                ).hasAnyRole("ADMIN", "MANAGER", "INVENTORY CONTROLLER")
                .requestMatchers(
                        "/inventory", "/shipments", "/equipment", "/locations", "/categories", "/suppliers",
                        "/maintenance-schedule"
                ).hasAnyRole("ADMIN", "MANAGER", "SUPERVISOR", "INVENTORY CONTROLLER", "OPERATOR")
                // Default: authenticated only
                .anyRequest().authenticated()
            )
            .exceptionHandling(e -> e.accessDeniedHandler(redirectAccessDeniedHandler()))
            .authenticationProvider(authenticationProvider)
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("usernameOrEmail")
                .passwordParameter("password")
                .defaultSuccessUrl("/dashboard", true)
                .failureHandler((request, response, exception) -> {
                    String message;
                    if (exception instanceof UsernameNotFoundException) {
                        String raw = exception.getMessage() == null ? "User not found" : exception.getMessage();
                        if (raw.toLowerCase().startsWith("email not found")) {
                            message = "Wrong Email";
                        } else if (raw.toLowerCase().startsWith("username not found")) {
                            message = "Wrong Username";
                        } else {
                            message = "Wrong Username or Email";
                        }
                    } else if (exception instanceof BadCredentialsException) {
                        message = "Wrong Password";
                    } else {
                        message = "Authentication failed";
                    }
                    String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);
                    response.sendRedirect("/login?errorMessage=" + encoded);
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(updatingLogoutSuccessHandler)
                .permitAll()
            );

        return http.build();
    }
}
