package com.impulse.adapters.http.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.impulse.application.dto.common.PaginationRequest;
import com.impulse.application.dto.common.PaginationResponse;
import com.impulse.application.dto.user.UserCreateRequestDto;
import com.impulse.application.dto.user.UserResponseDto;
import com.impulse.application.dto.user.UserUpdateRequestDto;
import com.impulse.application.service.interfaces.UserService;

import jakarta.validation.Valid;

/**
 * REST Controller for User operations
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Create a new user
     */
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateRequestDto request) {
        try {
            UserResponseDto user = userService.createUser(request);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new RuntimeException("Error creating user: " + e.getMessage(), e);
        }
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        try {
            UserResponseDto user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get user by UUID
     */
    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<UserResponseDto> getUserByUuid(@PathVariable String uuid) {
        try {
            UserResponseDto user = userService.getUserByUuid(uuid);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get user by username
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username) {
        try {
            UserResponseDto user = userService.getUserByUsername(username);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get user by email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {
        try {
            UserResponseDto user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all users with pagination
     */
    @GetMapping
    public ResponseEntity<PaginationResponse<UserResponseDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        PaginationRequest request = new PaginationRequest(page, size, sortBy, sortDirection);

        PaginationResponse<UserResponseDto> users = userService.getAllUsers(request);
        return ResponseEntity.ok(users);
    }

    /**
     * Update user
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequestDto request) {
        try {
            UserResponseDto user = userService.updateUser(id, request);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete user
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Check if user exists by email
     */
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    /**
     * Check if user exists by username
     */
    @GetMapping("/exists/username/{username}")
    public ResponseEntity<Boolean> existsByUsername(@PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }

    /**
     * Activate user account
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        try {
            userService.activateUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deactivate user account
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        try {
            userService.deactivateUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Check if user is active
     */
    @GetMapping("/{id}/active")
    public ResponseEntity<Boolean> isUserActive(@PathVariable Long id) {
        try {
            boolean isActive = userService.isUserActive(id);
            return ResponseEntity.ok(isActive);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update user last login
     */
    @PutMapping("/{id}/last-login")
    public ResponseEntity<Void> updateLastLogin(@PathVariable Long id) {
        try {
            userService.updateLastLogin(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Search users
     */
    @GetMapping("/search")
    public ResponseEntity<PaginationResponse<UserResponseDto>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PaginationRequest request = new PaginationRequest(page, size, "id", "ASC");

        PaginationResponse<UserResponseDto> users = userService.searchUsers(query, request);
        return ResponseEntity.ok(users);
    }

    /**
     * Get user statistics
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<UserService.UserStatsDto> getUserStatistics(@PathVariable Long id) {
        try {
            UserService.UserStatsDto stats = userService.getUserStatistics(id);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get top active users
     */
    @GetMapping("/top-active")
    public ResponseEntity<List<UserResponseDto>> getTopActiveUsers(
            @RequestParam(defaultValue = "10") int limit) {

        List<UserResponseDto> topUsers = userService.getTopActiveUsers(limit);
        return ResponseEntity.ok(topUsers);
    }
}
