package com.impulse.interfaces.rest.controller;

import com.impulse.application.service.UserService;
import com.impulse.application.service.ChallengeService;
import com.impulse.application.service.CoachService;
import com.impulse.domain.model.User;
import com.impulse.domain.model.Challenge;
import com.impulse.domain.model.Coach;
import com.impulse.domain.model.enums.UserStatus;
import com.impulse.domain.model.enums.UserRole;
import com.impulse.interfaces.rest.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para funciones de administración
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ChallengeService challengeService;
    
    @Autowired
    private CoachService coachService;

    // ===== GESTIÓN DE USUARIOS =====

    /**
     * Obtener todos los usuarios con filtros
     */
    @GetMapping("/users")
    public ResponseEntity<PageResponseDto<UserResponseDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String search) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<User> userPage;
        
        if (search != null && !search.trim().isEmpty()) {
            userPage = userService.searchUsers(search, pageable);
        } else if (status != null && role != null) {
            userPage = userService.getUsersByStatusAndRole(
                UserStatus.valueOf(status), UserRole.valueOf(role), pageable);
        } else if (status != null) {
            userPage = userService.getUsersByStatus(UserStatus.valueOf(status), pageable);
        } else if (role != null) {
            userPage = userService.getUsersByRole(UserRole.valueOf(role), pageable);
        } else {
            userPage = userService.getAllUsers(pageable);
        }
        
        PageResponseDto<UserResponseDto> response = convertUserPageToResponse(userPage, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener usuario por ID
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
            .map(this::convertUserToResponseDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Actualizar estado de usuario
     */
    @PutMapping("/users/{id}/status")
    public ResponseEntity<UserResponseDto> updateUserStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        
        try {
            UserStatus userStatus = UserStatus.valueOf(status.toUpperCase());
            User updatedUser = userService.updateUserStatus(id, userStatus);
            UserResponseDto responseDto = convertUserToResponseDto(updatedUser);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Actualizar rol de usuario
     */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserResponseDto> updateUserRole(
            @PathVariable Long id,
            @RequestParam String role) {
        
        try {
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            User updatedUser = userService.updateUserRole(id, userRole);
            UserResponseDto responseDto = convertUserToResponseDto(updatedUser);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Suspender usuario
     */
    @PostMapping("/users/{id}/suspend")
    public ResponseEntity<UserResponseDto> suspendUser(
            @PathVariable Long id,
            @RequestParam String reason,
            @RequestParam(required = false) Integer days) {
        
        try {
            User suspendedUser = userService.suspendUser(id, reason, days);
            UserResponseDto responseDto = convertUserToResponseDto(suspendedUser);
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Reactivar usuario
     */
    @PostMapping("/users/{id}/reactivate")
    public ResponseEntity<UserResponseDto> reactivateUser(@PathVariable Long id) {
        
        try {
            User reactivatedUser = userService.reactivateUser(id);
            UserResponseDto responseDto = convertUserToResponseDto(reactivatedUser);
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Eliminar usuario
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ===== GESTIÓN DE CHALLENGES =====

    /**
     * Obtener todos los challenges para administración
     */
    @GetMapping("/challenges")
    public ResponseEntity<PageResponseDto<ChallengeResponseDto>> getAllChallenges(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Challenge> challengePage = challengeService.getAllChallengesForAdmin(pageable);
        PageResponseDto<ChallengeResponseDto> response = convertChallengePageToResponse(challengePage, page, size);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Aprobar challenge
     */
    @PostMapping("/challenges/{id}/approve")
    public ResponseEntity<ChallengeResponseDto> approveChallenge(@PathVariable Long id) {
        
        try {
            Challenge approvedChallenge = challengeService.approveChallenge(id);
            ChallengeResponseDto responseDto = convertChallengeToResponseDto(approvedChallenge);
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Rechazar challenge
     */
    @PostMapping("/challenges/{id}/reject")
    public ResponseEntity<ChallengeResponseDto> rejectChallenge(
            @PathVariable Long id,
            @RequestParam String reason) {
        
        try {
            Challenge rejectedChallenge = challengeService.rejectChallenge(id, reason);
            ChallengeResponseDto responseDto = convertChallengeToResponseDto(rejectedChallenge);
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Destacar challenge
     */
    @PostMapping("/challenges/{id}/feature")
    public ResponseEntity<ChallengeResponseDto> featureChallenge(@PathVariable Long id) {
        
        try {
            Challenge featuredChallenge = challengeService.featureChallenge(id);
            ChallengeResponseDto responseDto = convertChallengeToResponseDto(featuredChallenge);
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ===== GESTIÓN DE COACHES =====

    /**
     * Obtener coaches pendientes de aprobación
     */
    @GetMapping("/coaches/pending")
    public ResponseEntity<PageResponseDto<Object>> getPendingCoaches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        
        // Esta funcionalidad se implementaría en CoachService
        // Page<Coach> pendingCoaches = coachService.getPendingCoaches(pageable);
        
        // Por ahora devolvemos respuesta vacía
        PageResponseDto<Object> response = new PageResponseDto<>();
        return ResponseEntity.ok(response);
    }

    /**
     * Aprobar coach
     */
    @PostMapping("/coaches/{id}/approve")
    public ResponseEntity<Object> approveCoach(@PathVariable Long id) {
        
        try {
            Coach approvedCoach = coachService.approveCoach(id);
            return ResponseEntity.ok(approvedCoach);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Rechazar coach
     */
    @PostMapping("/coaches/{id}/reject")
    public ResponseEntity<Object> rejectCoach(
            @PathVariable Long id,
            @RequestParam String reason) {
        
        try {
            Coach rejectedCoach = coachService.rejectCoach(id, reason);
            return ResponseEntity.ok(rejectedCoach);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ===== ESTADÍSTICAS Y REPORTES =====

    /**
     * Obtener estadísticas del dashboard
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        
        // Obtener estadísticas globales
        Object userStats = userService.getGlobalStatistics();
        // Object challengeStats = challengeService.getGlobalStatistics();
        // Object coachStats = coachService.getGlobalStatistics();
        
        Map<String, Object> dashboardStats = Map.of(
            "users", userStats,
            "timestamp", LocalDateTime.now()
            // "challenges", challengeStats,
            // "coaches", coachStats
        );
        
        return ResponseEntity.ok(dashboardStats);
    }

    /**
     * Obtener tendencias de usuarios
     */
    @GetMapping("/analytics/user-trends")
    public ResponseEntity<List<Object[]>> getUserTrends(
            @RequestParam(defaultValue = "30") int days) {
        
        List<Object[]> trends = userService.getUserRegistrationTrends(days);
        return ResponseEntity.ok(trends);
    }

    /**
     * Obtener reporte de actividad
     */
    @GetMapping("/reports/activity")
    public ResponseEntity<Object> getActivityReport(
            @RequestParam(defaultValue = "7") int days) {
        
        Object activityReport = userService.getActivityReport(days);
        return ResponseEntity.ok(activityReport);
    }

    // ===== CONFIGURACIÓN DEL SISTEMA =====

    /**
     * Obtener configuración del sistema
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getSystemConfig() {
        
        // Aquí se implementaría la lógica para obtener configuración
        Map<String, Object> config = Map.of(
            "maintenanceMode", false,
            "registrationEnabled", true,
            "challengeCreationEnabled", true,
            "version", "1.0.0"
        );
        
        return ResponseEntity.ok(config);
    }

    /**
     * Actualizar configuración del sistema
     */
    @PutMapping("/config")
    public ResponseEntity<Map<String, Object>> updateSystemConfig(
            @RequestBody Map<String, Object> config) {
        
        // Aquí se implementaría la lógica para actualizar configuración
        return ResponseEntity.ok(config);
    }

    // ===== MANTENIMIENTO =====

    /**
     * Limpiar datos antiguos
     */
    @PostMapping("/maintenance/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldData(
            @RequestParam(defaultValue = "90") int daysOld) {
        
        try {
            // Implementar limpieza de datos antiguos
            int deletedUsers = userService.cleanupInactiveUsers(daysOld);
            // int deletedChallenges = challengeService.cleanupOldChallenges(daysOld);
            
            Map<String, Object> result = Map.of(
                "deletedUsers", deletedUsers,
                // "deletedChallenges", deletedChallenges,
                "cleanupDate", LocalDateTime.now()
            );
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ===== MÉTODOS DE UTILIDAD =====

    /**
     * Convertir User a UserResponseDto
     */
    private UserResponseDto convertUserToResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setStatus(user.getStatus().toString());
        dto.setRole(user.getRole().toString());
        dto.setLevel(user.getLevel());
        dto.setTotalPoints(user.getTotalPoints());
        dto.setStreakDays(user.getStreakDays());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLoginAt(user.getLastLoginAt());
        dto.setEmailVerified(user.getEmailVerified());
        dto.setProfilePublic(user.getProfilePublic());
        
        return dto;
    }

    /**
     * Convertir Challenge a ChallengeResponseDto
     */
    private ChallengeResponseDto convertChallengeToResponseDto(Challenge challenge) {
        ChallengeResponseDto dto = new ChallengeResponseDto();
        dto.setId(challenge.getId());
        dto.setTitle(challenge.getTitle());
        dto.setDescription(challenge.getDescription());
        dto.setCategory(challenge.getCategory().toString());
        dto.setDifficulty(challenge.getDifficulty().toString());
        dto.setStatus(challenge.getStatus().toString());
        dto.setPoints(challenge.getPoints());
        dto.setCreatedAt(challenge.getCreatedAt());
        dto.setUpdatedAt(challenge.getUpdatedAt());
        
        return dto;
    }

    /**
     * Convertir Page<User> a PageResponseDto<UserResponseDto>
     */
    private PageResponseDto<UserResponseDto> convertUserPageToResponse(Page<User> userPage, int page, int size) {
        List<UserResponseDto> content = userPage.getContent().stream()
            .map(this::convertUserToResponseDto)
            .collect(Collectors.toList());
        
        return new PageResponseDto<>(
            content,
            page,
            size,
            userPage.getTotalElements(),
            userPage.getTotalPages()
        );
    }

    /**
     * Convertir Page<Challenge> a PageResponseDto<ChallengeResponseDto>
     */
    private PageResponseDto<ChallengeResponseDto> convertChallengePageToResponse(Page<Challenge> challengePage, int page, int size) {
        List<ChallengeResponseDto> content = challengePage.getContent().stream()
            .map(this::convertChallengeToResponseDto)
            .collect(Collectors.toList());
        
        return new PageResponseDto<>(
            content,
            page,
            size,
            challengePage.getTotalElements(),
            challengePage.getTotalPages()
        );
    }
}
