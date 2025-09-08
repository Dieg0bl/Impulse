package com.impulse.ports.in;

import com.impulse.domain.user.UserStatus;
import com.impulse.infrastructure.web.dto.AuthResponseDto;
import com.impulse.infrastructure.web.dto.UserLoginDto;
import com.impulse.infrastructure.web.dto.UserRegistrationDto;
import com.impulse.infrastructure.web.dto.UserResponseDto;

import java.util.List;
import java.util.Optional;

/**
 * Input port for user management operations
 */
public interface UserManagementPort {
    
    UserResponseDto registerUser(UserRegistrationDto registrationDto);
    
    AuthResponseDto authenticateUser(UserLoginDto loginDto);
    
    Optional<UserResponseDto> getUserById(String id);
    
    Optional<UserResponseDto> getUserByEmail(String email);
    
    Optional<UserResponseDto> getUserByUsername(String username);
    
    List<UserResponseDto> getAllUsers();
    
    List<UserResponseDto> getUsersByStatus(UserStatus status);
    
    List<UserResponseDto> searchUsers(String searchTerm);
    
    UserResponseDto updateUserStatus(String userId, UserStatus status);
    
    void changePassword(String userId, String currentPassword, String newPassword);
    
    void deleteUser(String userId);
    
    long getUserCount();
    
    long getActiveUserCount();
    
    List<UserResponseDto> getRecentUsers(int days);
    
    List<UserResponseDto> getInactiveUsers(int days);
}
