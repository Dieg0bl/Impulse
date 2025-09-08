package com.impulse.ports.out;

import com.impulse.domain.user.User;
import com.impulse.domain.user.UserStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Output port for user persistence operations
 */
public interface UserRepositoryPort {
    
    User save(User user);
    
    Optional<User> findById(String id);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByUsername(String username);
    
    List<User> findByStatus(UserStatus status);
    
    List<User> findAll();
    
    void deleteById(String id);
    
    long count();
    
    List<User> findRecentUsers(LocalDateTime since);
    
    List<User> findInactiveUsers(LocalDateTime before);
    
    List<User> searchUsers(String searchTerm);
    
    long countByStatus(UserStatus status);
}
