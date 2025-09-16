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

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Configures the password encoder bean. We use BCrypt for secure password hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setHideUserNotFoundExceptions(false); // expose UsernameNotFoundException
        return provider;
    }

    @Bean
    public LogoutSuccessHandler updatingLogoutSuccessHandler(com.lwms.backend.services.UserService userService) {
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
     * Configures the security filter chain for the application.
     * This is where we define which URLs are public and how login/logout works.
     *
     * @param http The HttpSecurity object to configure.
     * @return The SecurityFilterChain bean.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, DaoAuthenticationProvider authenticationProvider,
                                                   LogoutSuccessHandler updatingLogoutSuccessHandler) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/login", "/signup", "/lwms/register", "/api/users/register",
                        "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                .requestMatchers("/settings").hasAnyRole("Admin", "Manager")
                .anyRequest().authenticated()
            )
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
