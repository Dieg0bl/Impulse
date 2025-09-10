package com.impulse.interfaces.rest.controllers;

import com.impulse.application.services.UserService;
import com.impulse.domain.entities.User;
import com.impulse.domain.entities.User.UserRole;
import com.impulse.domain.entities.User.UserStatus;
import com.impulse.interfaces.rest.dto.UserCreateDto;
import com.impulse.interfaces.rest.dto.UserResponseDto;
import com.impulse.interfaces.rest.dto.UserUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

/**
 * Controlador REST para gestión de usuarios
 */
@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // CRUD Operations
    
    /**
     * Crea un nuevo usuario (registro público)
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateDto userDto) {
        User user = userService.createUser(
            userDto.getUsername(),
            userDto.getEmail(),
            userDto.getPassword(),
            userDto.getFirstName(),
            userDto.getLastName()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(convertToResponseDto(user));
    }
    
    /**
     * Obtiene un usuario por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        
        return user.map(u -> ResponseEntity.ok(convertToResponseDto(u)))
                  .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Obtiene un usuario por username
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.getUserByUsername(username);
        
        return user.map(u -> ResponseEntity.ok(convertToResponseDto(u)))
                  .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Obtiene el perfil del usuario autenticado
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        // TODO: Obtener el ID del usuario autenticado desde el contexto de seguridad
        // Long userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = 1L; // Placeholder
        
        Optional<User> user = userService.getUserById(userId);
        
        return user.map(u -> ResponseEntity.ok(convertToResponseDto(u)))
                  .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Actualiza la información del usuario autenticado
     */
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> updateCurrentUser(@Valid @RequestBody UserUpdateDto userDto) {
        // TODO: Obtener el ID del usuario autenticado
        Long userId = 1L; // Placeholder
        
        User user = userService.updateUser(
            userId,
            userDto.getFirstName(),
            userDto.getLastName(),
            userDto.getBio(),
            userDto.getLocation(),
            userDto.getWebsiteUrl()
        );
        
        return ResponseEntity.ok(convertToResponseDto(user));
    }
    
    /**
     * Actualiza el avatar del usuario autenticado
     */
    @PutMapping("/me/avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> updateAvatar(@RequestBody String avatarUrl) {
        // TODO: Obtener el ID del usuario autenticado
        Long userId = 1L; // Placeholder
        
        User user = userService.updateAvatar(userId, avatarUrl);
        
        return ResponseEntity.ok(convertToResponseDto(user));
    }
    
    /**
     * Cambia la contraseña del usuario autenticado
     */
    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordDto passwordDto) {
        // TODO: Obtener el ID del usuario autenticado
        Long userId = 1L; // Placeholder
        
        userService.changePassword(userId, passwordDto.getCurrentPassword(), passwordDto.getNewPassword());
        
        return ResponseEntity.ok().build();
    }
    
    // Search and Listing
    
    /**
     * Busca usuarios
     */
    @GetMapping("/search")
    public ResponseEntity<Page<UserResponseDto>> searchUsers(
            @RequestParam String query,
            Pageable pageable) {
        
        Page<User> users = userService.searchUsers(query, pageable);
        Page<UserResponseDto> userDtos = users.map(this::convertToResponseDto);
        
        return ResponseEntity.ok(userDtos);
    }
    
    /**
     * Obtiene usuarios por status
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Page<UserResponseDto>> getUsersByStatus(
            @PathVariable UserStatus status,
            Pageable pageable) {
        
        Page<User> users = userService.getUsersByStatus(status, pageable);
        Page<UserResponseDto> userDtos = users.map(this::convertToResponseDto);
        
        return ResponseEntity.ok(userDtos);
    }
    
    /**
     * Obtiene usuarios por rol
     */
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDto>> getUsersByRole(
            @PathVariable UserRole role,
            Pageable pageable) {
        
        Page<User> users = userService.getUsersByRole(role, pageable);
        Page<UserResponseDto> userDtos = users.map(this::convertToResponseDto);
        
        return ResponseEntity.ok(userDtos);
    }
    
    // Leaderboards
    
    /**
     * Obtiene el leaderboard por puntos
     */
    @GetMapping("/leaderboard/points")
    public ResponseEntity<Page<UserResponseDto>> getLeaderboardByPoints(Pageable pageable) {
        Page<User> users = userService.getLeaderboardByPoints(pageable);
        Page<UserResponseDto> userDtos = users.map(this::convertToResponseDto);
        
        return ResponseEntity.ok(userDtos);
    }
    
    /**
     * Obtiene el leaderboard por challenges completados
     */
    @GetMapping("/leaderboard/challenges")
    public ResponseEntity<Page<UserResponseDto>> getLeaderboardByChallenges(Pageable pageable) {
        Page<User> users = userService.getLeaderboardByChallenges(pageable);
        Page<UserResponseDto> userDtos = users.map(this::convertToResponseDto);
        
        return ResponseEntity.ok(userDtos);
    }
    
    /**
     * Obtiene el leaderboard por streak activo
     */
    @GetMapping("/leaderboard/streak")
    public ResponseEntity<Page<UserResponseDto>> getActiveStreakLeaderboard(Pageable pageable) {
        Page<User> users = userService.getActiveStreakLeaderboard(pageable);
        Page<UserResponseDto> userDtos = users.map(this::convertToResponseDto);
        
        return ResponseEntity.ok(userDtos);
    }
    
    // Admin Operations
    
    /**
     * Asigna un rol a un usuario
     */
    @PostMapping("/{id}/roles/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> assignRole(
            @PathVariable Long id,
            @PathVariable UserRole role) {
        
        User user = userService.assignRole(id, role);
        
        return ResponseEntity.ok(convertToResponseDto(user));
    }
    
    /**
     * Remueve un rol de un usuario
     */
    @DeleteMapping("/{id}/roles/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> removeRole(
            @PathVariable Long id,
            @PathVariable UserRole role) {
        
        User user = userService.removeRole(id, role);
        
        return ResponseEntity.ok(convertToResponseDto(user));
    }
    
    /**
     * Activa un usuario
     */
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<UserResponseDto> activateUser(@PathVariable Long id) {
        User user = userService.activateUser(id);
        
        return ResponseEntity.ok(convertToResponseDto(user));
    }
    
    /**
     * Desactiva un usuario
     */
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<UserResponseDto> deactivateUser(@PathVariable Long id) {
        User user = userService.deactivateUser(id);
        
        return ResponseEntity.ok(convertToResponseDto(user));
    }
    
    /**
     * Suspende un usuario
     */
    @PutMapping("/{id}/suspend")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<UserResponseDto> suspendUser(@PathVariable Long id) {
        User user = userService.suspendUser(id);
        
        return ResponseEntity.ok(convertToResponseDto(user));
    }
    
    /**
     * Banea un usuario
     */
    @PutMapping("/{id}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> banUser(@PathVariable Long id) {
        User user = userService.banUser(id);
        
        return ResponseEntity.ok(convertToResponseDto(user));
    }
    
    /**
     * Resetea la contraseña de un usuario
     */
    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> resetPassword(
            @PathVariable Long id,
            @RequestBody String newPassword) {
        
        userService.resetPassword(id, newPassword);
        
        return ResponseEntity.ok().build();
    }
    
    // Gamification
    
    /**
     * Otorga puntos a un usuario
     */
    @PostMapping("/{id}/points")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<UserResponseDto> awardPoints(
            @PathVariable Long id,
            @RequestBody int points) {
        
        User user = userService.awardPoints(id, points);
        
        return ResponseEntity.ok(convertToResponseDto(user));
    }
    
    // Preferences
    
    /**
     * Establece una preferencia del usuario autenticado
     */
    @PutMapping("/me/preferences/{key}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> setPreference(
            @PathVariable String key,
            @RequestBody String value) {
        
        // TODO: Obtener el ID del usuario autenticado
        Long userId = 1L; // Placeholder
        
        userService.setPreference(userId, key, value);
        
        return ResponseEntity.ok().build();
    }
    
    /**
     * Obtiene una preferencia del usuario autenticado
     */
    @GetMapping("/me/preferences/{key}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> getPreference(@PathVariable String key) {
        // TODO: Obtener el ID del usuario autenticado
        Long userId = 1L; // Placeholder
        
        String value = userService.getPreference(userId, key);
        
        if (value != null) {
            return ResponseEntity.ok(value);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Statistics
    
    /**
     * Obtiene estadísticas de usuarios
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<UserService.UserStatistics> getUserStatistics() {
        UserService.UserStatistics stats = userService.getUserStatistics();
        
        return ResponseEntity.ok(stats);
    }
    
    // Validation
    
    /**
     * Verifica disponibilidad de username
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Boolean> checkUsernameAvailability(@PathVariable String username) {
        boolean available = userService.isUsernameAvailable(username);
        
        return ResponseEntity.ok(available);
    }
    
    /**
     * Verifica disponibilidad de email
     */
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Boolean> checkEmailAvailability(@PathVariable String email) {
        boolean available = userService.isEmailAvailable(email);
        
        return ResponseEntity.ok(available);
    }
    
    // Helper methods
    
    /**
     * Convierte User a UserResponseDto
     */
    private UserResponseDto convertToResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setFullName(user.getFullName());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setBio(user.getBio());
        dto.setLocation(user.getLocation());
        dto.setWebsiteUrl(user.getWebsiteUrl());
        dto.setStatus(user.getStatus());
        dto.setEmailVerified(user.getEmailVerified());
        dto.setTotalPoints(user.getTotalPoints());
        dto.setLevel(user.getLevel());
        dto.setCurrentStreak(user.getCurrentStreak());
        dto.setLongestStreak(user.getLongestStreak());
        dto.setChallengesCompleted(user.getChallengesCompleted());
        dto.setEvidencesSubmitted(user.getEvidencesSubmitted());
        dto.setRoles(user.getRoles());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLoginAt(user.getLastLoginAt());
        
        return dto;
    }
    
    // DTOs internos
    
    public static class ChangePasswordDto {
        private String currentPassword;
        private String newPassword;
        
        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
