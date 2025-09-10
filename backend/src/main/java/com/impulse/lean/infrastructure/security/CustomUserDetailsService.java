package com.impulse.lean.infrastructure.security;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * IMPULSE LEAN v1 - Custom User Details Service
 * 
 * Temporary implementation for Phase 1
 * Will be replaced with proper user repository integration in Phase 2
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Temporary implementation for Phase 1
        // In Phase 2, this will integrate with the User repository
        return User.builder()
                .username(username)
                .password("$2a$12$dummy.password.hash.for.phase.one.implementation")
                .authorities(new ArrayList<>())
                .build();
    }
}
