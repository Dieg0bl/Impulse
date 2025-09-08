package com.impulse.infrastructure.security;

import com.impulse.domain.user.User;
import com.impulse.ports.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom UserDetailsService implementation
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepositoryPort userRepositoryPort;
    
    @Override
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        // Try to find by email first, then by username
        User user = userRepositoryPort.findByEmail(emailOrUsername)
                .or(() -> userRepositoryPort.findByUsername(emailOrUsername))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + emailOrUsername));
        
        return UserPrincipal.create(user);
    }
    
    public UserDetails loadUserById(String id) {
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        
        return UserPrincipal.create(user);
    }
}
