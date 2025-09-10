package com.impulse.lean.infrastructure.security;

import com.impulse.lean.domain.model.User;
import com.impulse.lean.domain.model.UserRole;
import com.impulse.lean.domain.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

/**
 * IMPULSE LEAN v1 - Custom UserDetailsService Implementation
 * 
 * Integrates domain User entity with Spring Security
 * Provides authentication and authorization support
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Check if user is active
        if (!user.isActive()) {
            throw new UsernameNotFoundException("User is not active: " + username);
        }

        return new CustomUserPrincipal(user);
    }

    /**
     * Custom UserDetails implementation that wraps our domain User entity
     */
    public static class CustomUserPrincipal implements UserDetails {
        
        private final User user;

        public CustomUserPrincipal(User user) {
            this.user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
            );
        }

        @Override
        public String getPassword() {
            return user.getPasswordHash();
        }

        @Override
        public String getUsername() {
            return user.getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true; // Could be enhanced with user expiration logic
        }

        @Override
        public boolean isAccountNonLocked() {
            return !user.isLocked();
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true; // Could be enhanced with password expiration logic
        }

        @Override
        public boolean isEnabled() {
            return user.isActive();
        }

        // Expose domain user for additional information
        public User getUser() {
            return user;
        }

        public Long getUserId() {
            return user.getId();
        }

        public UserRole getUserRole() {
            return user.getRole();
        }
    }
}
