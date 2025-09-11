package com.impulse.lean.application.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.impulse.lean.application.dto.common.ApiResponse;
import com.impulse.lean.application.dto.common.PaginationRequest;
import com.impulse.lean.application.dto.common.PaginationResponse;
import com.impulse.lean.application.dto.user.UserResponseDto;
import com.impulse.lean.domain.model.User;
import com.impulse.lean.domain.model.UserRole;
import com.impulse.lean.domain.model.UserStatus;
import com.impulse.lean.domain.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * IMPULSE LEAN v1 - User REST Controller
 * 
 * Handles HTTP requests for user operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User management operations")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Retrieve the authenticated user's profile")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User profile retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<ApiResponse<UserResponseDto>> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401)
                .body(ApiResponse.error("User not authenticated"));
        }

        Optional<User> userOpt = userRepository.findByUsername(userDetails.getUsername());
        
        if (userOpt.isPresent()) {
            UserResponseDto dto = UserResponseDto.from(userOpt.get());
            return ResponseEntity.ok(ApiResponse.success(dto));
        } else {
            return ResponseEntity.status(401)
                .body(ApiResponse.error("User not found"));
        }
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get user by UUID", description = "Retrieve a specific user by UUID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserByUuid(
            @Parameter(description = "User UUID") @PathVariable String uuid) {

        Optional<User> userOpt = userRepository.findByUuid(uuid);
        
        if (userOpt.isPresent()) {
            // Hide sensitive data for public endpoint
            UserResponseDto dto = UserResponseDto.fromWithoutSensitiveData(userOpt.get());
            return ResponseEntity.ok(ApiResponse.success(dto));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a paginated list of users (Admin/Moderator only)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<PaginationResponse<UserResponseDto>>> getAllUsers(
            @Valid @ModelAttribute PaginationRequest request,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) UserStatus status) {

        try {
            Sort sort = Sort.by(
                request.getSafeSortDirection().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
                request.getSafeSortBy() != null ? request.getSafeSortBy() : "createdAt"
            );

            Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
            Page<User> userPage;

            if (request.hasSearch()) {
                userPage = userRepository.searchUsers(request.getSafeSearch(), pageable);
            } else if (status != null) {
                userPage = userRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
            } else if (role != null) {
                userPage = userRepository.findByRoleOrderByCreatedAtDesc(role, pageable);
            } else {
                userPage = userRepository.findAll(pageable);
            }

            // Convert to DTOs
            List<UserResponseDto> userDtos = userPage.getContent().stream()
                .map(UserResponseDto::from)
                .collect(Collectors.toList());

            PaginationResponse<UserResponseDto> response = new PaginationResponse<>(
                userDtos,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements()
            );

            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (DataAccessException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve users: " + e.getMessage()));
        }
    }

    @GetMapping("/active")
    @Operation(summary = "Get active users", description = "Retrieve all active users")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getActiveUsers() {
        try {
            List<User> activeUsers = userRepository.findActiveUsers();
            
            List<UserResponseDto> userDtos = activeUsers.stream()
                .map(UserResponseDto::fromWithoutSensitiveData)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(userDtos));
        } catch (DataAccessException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve active users: " + e.getMessage()));
        }
    }

    @GetMapping("/moderators")
    @Operation(summary = "Get moderators", description = "Retrieve all active moderators and admins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getModerators() {
        try {
            List<User> moderators = userRepository.findActiveModerators();
            
            List<UserResponseDto> userDtos = moderators.stream()
                .map(UserResponseDto::fromWithoutSensitiveData)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(userDtos));
        } catch (DataAccessException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve moderators: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by username, name, or email")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<PaginationResponse<UserResponseDto>>> searchUsers(
            @RequestParam String query,
            @Valid @ModelAttribute PaginationRequest request) {
        
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
            Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
            
            Page<User> userPage = userRepository.searchUsers(query, pageable);
            
            List<UserResponseDto> userDtos = userPage.getContent().stream()
                .map(UserResponseDto::from)
                .collect(Collectors.toList());

            PaginationResponse<UserResponseDto> response = new PaginationResponse<>(
                userDtos,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements()
            );

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (DataAccessException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to search users: " + e.getMessage()));
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "Get user statistics", description = "Retrieve user statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getUserStats() {
        try {
            // Simple statistics
            long totalUsers = userRepository.count();
            long activeUsers = userRepository.countByStatus(UserStatus.ACTIVE);
            long pendingUsers = userRepository.countByStatus(UserStatus.PENDING);
            
            var stats = new Object() {
                public final long total = totalUsers;
                public final long active = activeUsers;
                public final long pending = pendingUsers;
                public final long inactive = totalUsers - activeUsers - pendingUsers;
            };

            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (DataAccessException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve user statistics: " + e.getMessage()));
        }
    }

    @GetMapping("/top-participants")
    @Operation(summary = "Get top challenge participants", description = "Retrieve users with most challenge participations")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getTopParticipants(
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            Pageable pageable = PageRequest.of(0, limit);
            List<User> topUsers = userRepository.findMostActiveChallengeParticipants(pageable);
            
            List<UserResponseDto> userDtos = topUsers.stream()
                .map(UserResponseDto::fromWithoutSensitiveData)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(userDtos));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve top participants: " + e.getMessage()));
        }
    }

    @PutMapping("/{uuid}/status")
    @Operation(summary = "Update user status", description = "Update user status (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUserStatus(
            @Parameter(description = "User UUID") @PathVariable String uuid,
            @RequestParam UserStatus status) {

        try {
            Optional<User> userOpt = userRepository.findByUuid(uuid);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            user.setStatus(status);
            user = userRepository.save(user);

            UserResponseDto dto = UserResponseDto.from(user);
            return ResponseEntity.ok(ApiResponse.success("User status updated successfully", dto));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update user status: " + e.getMessage()));
        }
    }

    @PutMapping("/{uuid}/role")
    @Operation(summary = "Update user role", description = "Update user role (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUserRole(
            @Parameter(description = "User UUID") @PathVariable String uuid,
            @RequestParam UserRole role) {

        try {
            Optional<User> userOpt = userRepository.findByUuid(uuid);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            user.setRole(role);
            user = userRepository.save(user);

            UserResponseDto dto = UserResponseDto.from(user);
            return ResponseEntity.ok(ApiResponse.success("User role updated successfully", dto));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update user role: " + e.getMessage()));
        }
    }
}
