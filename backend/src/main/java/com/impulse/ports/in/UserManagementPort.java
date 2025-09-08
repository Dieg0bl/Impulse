package com.impulse.ports.in;

import com.impulse.domain.user.User;
import java.util.UUID;

/**
 * Input port for user management operations
 */
public interface UserManagementPort {
    
    User createUser(String email, String username, String password);
    
    User findById(UUID id);
    
    User findByEmail(String email);
    
    User authenticate(String email, String password);
    
    void updateLastLogin(UUID userId);
    
    void deactivateUser(UUID userId);
}
