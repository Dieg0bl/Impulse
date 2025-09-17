package com.impulse.infrastructure.services;

import com.impulse.application.dto.common.PaginationRequest;
import com.impulse.application.dto.common.PaginationResponse;
import com.impulse.application.dto.user.UserResponseDto;
import com.impulse.application.dto.user.UserCreateRequestDto;
import com.impulse.application.dto.user.UserUpdateRequestDto;
import com.impulse.application.service.interfaces.UserService;
import com.impulse.domain.enums.UserRole;
import com.impulse.domain.enums.UserStatus;
import com.impulse.domain.user.User;
import com.impulse.infrastructure.persistence.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of UserService interface
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDto createUser(UserCreateRequestDto request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with email " + request.getEmail() + " already exists");
        }

        // Create new user entity
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setProfileImageUrl(request.getProfileImageUrl());
        user.setRole(UserRole.USER); // Default role
        user.setStatus(UserStatus.ACTIVE); // Default status
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);
        return mapToResponseDto(savedUser);
    }

    @Override
    public UserResponseDto updateUser(Long userId, UserUpdateRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Update fields if provided
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }

        User updatedUser = userRepository.save(user);
        return mapToResponseDto(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return mapToResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByUuid(String uuid) {
        // For now, using ID as UUID since the entity has ID
        try {
            Long id = Long.parseLong(uuid);
            return getUserById(id);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid UUID format: " + uuid);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByUsername(String username) {
        // Using email as username for now
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return mapToResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return mapToResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<UserResponseDto> getAllUsers(PaginationRequest request) {
        Sort.Direction direction = "DESC".equalsIgnoreCase(request.getSortDirection()) ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
            request.getPage(),
            request.getSize(),
            Sort.by(direction, request.getSortBy())
        );

        Page<User> userPage = userRepository.findAll(pageable);
        List<UserResponseDto> users = userPage.getContent().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return new PaginationResponse<>(users, userPage.getNumber(), userPage.getSize(), userPage.getTotalElements());
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        // Using email as username
        return userRepository.existsByEmail(username);
    }

    @Override
    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    @Override
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserActive(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return UserStatus.ACTIVE.equals(user.getStatus());
    }

    @Override
    public void updateLastLogin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<UserResponseDto> searchUsers(String query, PaginationRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        // Using a simple approach for now since the complex method doesn't exist
        Page<User> userPage = userRepository.findAll(pageable);

        List<UserResponseDto> users = userPage.getContent().stream()
                .filter(user -> user.getFirstName().toLowerCase().contains(query.toLowerCase()) ||
                               user.getLastName().toLowerCase().contains(query.toLowerCase()) ||
                               user.getEmail().toLowerCase().contains(query.toLowerCase()))
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return new PaginationResponse<>(users, userPage.getNumber(), userPage.getSize(), userPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatsDto getUserStatistics(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        // For now, return basic stats - this can be enhanced with actual queries
        return new UserStatsDto(0L, 0L, 0L, 0L, 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getTopActiveUsers(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "lastLogin"));
        // Using a simple approach since the specific method doesn't exist
        Page<User> topUsers = userRepository.findAll(pageable);

        return topUsers.getContent().stream()
                .filter(user -> UserStatus.ACTIVE.equals(user.getStatus()))
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Maps User entity to UserResponseDto
     */
    private UserResponseDto mapToResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUuid(), // Using ID as UUID for now
                user.getUsername(), // Using email as username
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getProfileImageUrl(),
                null, // bio
                user.getStatus() != null ? user.getStatus().toString() : null,
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLastLogin(),
                UserStatus.ACTIVE.equals(user.getStatus()),
                user.getEmailVerified(),
                0, // challengesCreated
                0, // challengesCompleted
                0  // totalPoints
        );
    }
}

