package com.impulse.application;

import com.impulse.domain.user.User;
import com.impulse.domain.user.UserStatus;
import com.impulse.infrastructure.security.jwt.JwtTokenProvider;
import com.impulse.infrastructure.web.dto.AuthResponseDto;
import com.impulse.infrastructure.web.dto.UserLoginDto;
import com.impulse.infrastructure.web.dto.UserRegistrationDto;
import com.impulse.infrastructure.web.dto.UserResponseDto;
import com.impulse.infrastructure.web.mapper.UserMapper;
import com.impulse.ports.in.UserManagementPort;
import com.impulse.ports.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Application service implementing user management use cases
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserManagementServiceNew implements UserManagementPort {
    
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        log.info("Attempting to register user with email: {}", registrationDto.getEmail());
        
        // Validate password confirmation
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and confirmation do not match");
        }
        
        // Check if user already exists
        if (userRepositoryPort.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }
        
        if (userRepositoryPort.existsByUsername(registrationDto.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        
        // Create new user
        User user = new User(
            registrationDto.getEmail(),
            registrationDto.getUsername(),
            passwordEncoder.encode(registrationDto.getPassword())
        );
        
        User savedUser = userRepositoryPort.save(user);
        
        log.info("Successfully registered user: {}", savedUser.getEmail());
        return userMapper.toResponseDto(savedUser);
    }

    @Override
    public AuthResponseDto authenticateUser(UserLoginDto loginDto) {
        log.info("Attempting to authenticate user: {}", loginDto.getEmailOrUsername());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginDto.getEmailOrUsername(),
                    loginDto.getPassword()
                )
            );
            
            String token = jwtTokenProvider.generateToken(authentication);
            
            // Update last login time
            User user = userRepositoryPort.findByEmail(loginDto.getEmailOrUsername())
                    .or(() -> userRepositoryPort.findByUsername(loginDto.getEmailOrUsername()))
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            user.recordLogin();
            userRepositoryPort.save(user);
            
            UserResponseDto userDto = userMapper.toResponseDto(user);
            
            log.info("Successfully authenticated user: {}", user.getEmail());
            return new AuthResponseDto(token, userDto);
            
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user: {}", loginDto.getEmailOrUsername());
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    @Override
    public Optional<UserResponseDto> getUserById(String id) {
        return userRepositoryPort.findById(id)
                .map(userMapper::toResponseDto);
    }

    @Override
    public Optional<UserResponseDto> getUserByEmail(String email) {
        return userRepositoryPort.findByEmail(email)
                .map(userMapper::toResponseDto);
    }

    @Override
    public Optional<UserResponseDto> getUserByUsername(String username) {
        return userRepositoryPort.findByUsername(username)
                .map(userMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepositoryPort.findAll()
                .stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsersByStatus(UserStatus status) {
        return userRepositoryPort.findByStatus(status)
                .stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> searchUsers(String searchTerm) {
        return userRepositoryPort.searchUsers(searchTerm)
                .stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto updateUserStatus(String userId, UserStatus status) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (status == UserStatus.ACTIVE) {
            user.activate();
        } else if (status == UserStatus.SUSPENDED) {
            user.suspend();
        }
        
        User updatedUser = userRepositoryPort.save(user);
        
        log.info("Updated user status for {}: {}", user.getEmail(), status);
        return userMapper.toResponseDto(updatedUser);
    }

    @Override
    public void changePassword(String userId, String currentPassword, String newPassword) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        // Validate new password
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters long");
        }
        
        user.changePassword(passwordEncoder.encode(newPassword));
        userRepositoryPort.save(user);
        
        log.info("Password changed for user: {}", user.getEmail());
    }

    @Override
    public void deleteUser(String userId) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        userRepositoryPort.deleteById(userId);
        
        log.info("Deleted user: {}", user.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUserCount() {
        return userRepositoryPort.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveUserCount() {
        return userRepositoryPort.countByStatus(UserStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getRecentUsers(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return userRepositoryPort.findRecentUsers(since)
                .stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getInactiveUsers(int days) {
        LocalDateTime before = LocalDateTime.now().minusDays(days);
        return userRepositoryPort.findInactiveUsers(before)
                .stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
