package com.impulse.application;

import com.impulse.domain.user.User;
import com.impulse.ports.in.UserManagementPort;
import com.impulse.ports.out.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

/**
 * User management application service
 * Implements business logic for user operations
 */
@Service
public class UserManagementService implements UserManagementPort {
    
    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserManagementService(UserRepositoryPort userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public User createUser(String email, String username, String password) {
        // Validate input
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email already exists");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("User with username already exists");
        }
        
        // Create and save user
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(UUID.randomUUID(), email, username, hashedPassword);
        return userRepository.save(user);
    }
    
    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
    
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
    
    @Override
    public User authenticate(String email, String password) {
        User user = findByEmail(email);
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return user;
    }
    
    @Override
    public void updateLastLogin(UUID userId) {
        User user = findById(userId);
        user.updateLastLogin();
        userRepository.save(user);
    }
    
    @Override
    public void deactivateUser(UUID userId) {
        User user = findById(userId);
        user.deactivate();
        userRepository.save(user);
    }
}
