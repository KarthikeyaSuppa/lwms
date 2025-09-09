package com.lwms.backend.services;

import com.lwms.backend.entities.User;
import com.lwms.backend.dao.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Try username with role eagerly, then email with role
        User user = userRepository.findWithRoleByUsername(usernameOrEmail)
                .orElseGet(() -> userRepository.findWithRoleByEmail(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException(
                                usernameOrEmail.contains("@")
                                        ? "Email not found: " + usernameOrEmail
                                        : "Username not found: " + usernameOrEmail)));

        String roleName = user.getRole().getRoleName();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleName);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                Collections.singleton(authority)
        );
    }
}