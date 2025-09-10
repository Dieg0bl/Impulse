package com.impulse.application.services;

import com.impulse.domain.entities.User;
import com.impulse.domain.entities.User.UserRole;
import com.impulse.domain.entities.User.UserStatus;
import com.impulse.infrastructure.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de usuarios
 */
@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // CRUD Operations
    
    /**
     * Crea un nuevo usuario
     */
    public User createUser(String username, String email, String password) {
        validateUniqueUser(username, email);
        
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(username, email, hashedPassword);
        
        return userRepository.save(user);
    }
    
    /**
     * Crea un usuario con información adicional
     */
    public User createUser(String username, String email, String password, 
                          String firstName, String lastName) {
        User user = createUser(username, email, password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        
        return userRepository.save(user);
    }
    
    /**
     * Obtiene un usuario por ID
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Obtiene un usuario por username
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Obtiene un usuario por email
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Obtiene un usuario por username o email
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsernameOrEmail(String identifier) {
        return userRepository.findByUsernameOrEmail(identifier, identifier);
    }
    
    /**
     * Actualiza la información básica del usuario
     */
    public User updateUser(Long userId, String firstName, String lastName, 
                          String bio, String location, String websiteUrl) {
        User user = getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setBio(bio);
        user.setLocation(location);
        user.setWebsiteUrl(websiteUrl);
        
        return userRepository.save(user);
    }
    
    /**
     * Actualiza el avatar del usuario
     */
    public User updateAvatar(Long userId, String avatarUrl) {
        User user = getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        user.setAvatarUrl(avatarUrl);
        return userRepository.save(user);
    }
    
    /**
     * Cambia la contraseña del usuario
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new RuntimeException("Contraseña actual incorrecta");
        }
        
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    /**
     * Resetea la contraseña del usuario (para admins)
     */
    public void resetPassword(Long userId, String newPassword) {
        User user = getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    // Authentication and Security
    
    /**
     * Verifica el email del usuario
     */
    public User verifyEmail(Long userId) {
        User user = getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        user.verifyEmail();
        return userRepository.save(user);
    }
    
    /**
     * Actualiza el último login
     */
    public User updateLastLogin(Long userId) {
        User user = getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        user.updateLastLogin();
        return userRepository.save(user);
    }
    
    /**
     * Actualiza el último login por username
     */
    public User updateLastLogin(String username) {
        User user = getUserByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        user.updateLastLogin();
        return userRepository.save(user);
    }
    
    // Role Management
    
    /**
     * Asigna un rol al usuario
     */
    public User assignRole(Long userId, UserRole role) {
        User user = getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        user.addRole(role);
        return userRepository.save(user);
    }
    
    /**
     * Remueve un rol del usuario
     */
    public User removeRole(Long userId, UserRole role) {
        User user = getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        user.removeRole(role);
        return userRepository.save(user);
    }
    
    /**
     * Verifica si el usuario tiene un rol específico
     */
    @Transactional(readOnly = true)
    public boolean hasRole(Long userId, UserRole role) {
        User user = getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return user.hasRole(role);
    }
    
    // Status Management
    
    /**
     * Activa un usuario
     */
    public User activateUser(Long userId) {
        User user = getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }
    
    /**
     * Desactiva un usuario
     */
    public User deactivateUser(Long userId) {
        User user = getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        user.setStatus(UserStatus.INACTIVE);
        return userRepository.save(user);
    }
    
    /**
     * Suspende un usuario
     */
    public User suspendUser(Long userId) {
        User user = getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        user.setStatus(UserStatus.SUSPENDED);
        return userRepository.save(user);
    }
    
    /**
     * Banea un usuario
     */
    public User banUser(Long userId) {
        User user = getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        user.setStatus(UserStatus.BANNED);
        return userRepository.save(user);
    }
    
    // Gamification
    
    /**
     * Otorga puntos al usuario
     */
    public User awardPoints(Long userId, int points) {
        User user = getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        user.addPoints(points);
        return userRepository.save(user);
    }
    
    /**
     * Incrementa el streak del usuario
     */
    public User incrementStreak(Long userId) {
        User user = getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        user.incrementStreak();
        return userRepository.save(user);
    }
    
    /**
     * Resetea el streak del usuario
     */
    public User resetStreak(Long userId) {
        User user = getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        user.resetStreak();
        return userRepository.save(user);
    }
    
    /**
     * Incrementa challenges completados
     */
    public User incrementChallengesCompleted(Long userId) {
        User user = getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        user.incrementChallengesCompleted();
        return userRepository.save(user);
    }
    
    /**
     * Incrementa evidencias enviadas
     */
    public User incrementEvidencesSubmitted(Long userId) {
        User user = getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        user.incrementEvidencesSubmitted();
        return userRepository.save(user);
    }
    
    // Preferences
    
    /**
     * Establece una preferencia del usuario
     */
    public User setPreference(Long userId, String key, String value) {
        User user = getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        user.setPreference(key, value);
        return userRepository.save(user);
    }
    
    /**
     * Obtiene una preferencia del usuario
     */
    @Transactional(readOnly = true)
    public String getPreference(Long userId, String key) {
        User user = getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return user.getPreference(key);
    }
    
    /**
     * Obtiene una preferencia con valor por defecto
     */
    @Transactional(readOnly = true)
    public String getPreference(Long userId, String key, String defaultValue) {
        User user = getUserById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return user.getPreference(key, defaultValue);
    }
    
    // Search and Listing
    
    /**
     * Busca usuarios por texto
     */
    @Transactional(readOnly = true)
    public Page<User> searchUsers(String search, Pageable pageable) {
        return userRepository.searchUsers(search, pageable);
    }
    
    /**
     * Obtiene usuarios por status
     */
    @Transactional(readOnly = true)
    public Page<User> getUsersByStatus(UserStatus status, Pageable pageable) {
        return userRepository.findByStatus(status, pageable);
    }
    
    /**
     * Obtiene usuarios por rol
     */
    @Transactional(readOnly = true)
    public Page<User> getUsersByRole(UserRole role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }
    
    /**
     * Obtiene el leaderboard por puntos
     */
    @Transactional(readOnly = true)
    public Page<User> getLeaderboardByPoints(Pageable pageable) {
        return userRepository.findLeaderboardByPoints(pageable);
    }
    
    /**
     * Obtiene el leaderboard por challenges
     */
    @Transactional(readOnly = true)
    public Page<User> getLeaderboardByChallenges(Pageable pageable) {
        return userRepository.findLeaderboardByChallenges(pageable);
    }
    
    /**
     * Obtiene el leaderboard por streak activo
     */
    @Transactional(readOnly = true)
    public Page<User> getActiveStreakLeaderboard(Pageable pageable) {
        return userRepository.findActiveStreakLeaderboard(pageable);
    }
    
    // Statistics
    
    /**
     * Cuenta usuarios por status
     */
    @Transactional(readOnly = true)
    public long countUsersByStatus(UserStatus status) {
        return userRepository.countByStatus(status);
    }
    
    /**
     * Cuenta usuarios por rol
     */
    @Transactional(readOnly = true)
    public long countUsersByRole(UserRole role) {
        return userRepository.countByRole(role);
    }
    
    /**
     * Cuenta nuevos usuarios desde una fecha
     */
    @Transactional(readOnly = true)
    public long countNewUsersFromDate(LocalDateTime date) {
        return userRepository.countNewUsersFromDate(date);
    }
    
    /**
     * Cuenta usuarios activos desde una fecha
     */
    @Transactional(readOnly = true)
    public long countActiveUsersFromDate(LocalDateTime date) {
        return userRepository.countActiveUsersFromDate(date);
    }
    
    /**
     * Obtiene estadísticas de usuarios
     */
    @Transactional(readOnly = true)
    public UserStatistics getUserStatistics() {
        return UserStatistics.builder()
            .totalUsers(userRepository.count())
            .activeUsers(countUsersByStatus(UserStatus.ACTIVE))
            .inactiveUsers(countUsersByStatus(UserStatus.INACTIVE))
            .suspendedUsers(countUsersByStatus(UserStatus.SUSPENDED))
            .bannedUsers(countUsersByStatus(UserStatus.BANNED))
            .verifiedUsers(userRepository.findByEmailVerified(true).size())
            .averagePoints(userRepository.getAveragePointsForActiveUsers())
            .averageLevel(userRepository.getAverageLevelForActiveUsers())
            .maxPoints(userRepository.getMaxPoints())
            .maxLevel(userRepository.getMaxLevel())
            .maxStreak(userRepository.getMaxStreak())
            .build();
    }
    
    // Validation
    
    /**
     * Valida que el usuario sea único
     */
    private void validateUniqueUser(String username, String email) {
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new RuntimeException("El username ya está en uso");
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new RuntimeException("El email ya está registrado");
        }
    }
    
    /**
     * Verifica si un username está disponible
     */
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsernameIgnoreCase(username);
    }
    
    /**
     * Verifica si un email está disponible
     */
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmailIgnoreCase(email);
    }
    
    // Inner class for statistics
    public static class UserStatistics {
        private long totalUsers;
        private long activeUsers;
        private long inactiveUsers;
        private long suspendedUsers;
        private long bannedUsers;
        private long verifiedUsers;
        private Double averagePoints;
        private Double averageLevel;
        private Integer maxPoints;
        private Integer maxLevel;
        private Integer maxStreak;
        
        // Builder pattern
        public static UserStatisticsBuilder builder() {
            return new UserStatisticsBuilder();
        }
        
        public static class UserStatisticsBuilder {
            private UserStatistics stats = new UserStatistics();
            
            public UserStatisticsBuilder totalUsers(long totalUsers) {
                stats.totalUsers = totalUsers;
                return this;
            }
            
            public UserStatisticsBuilder activeUsers(long activeUsers) {
                stats.activeUsers = activeUsers;
                return this;
            }
            
            public UserStatisticsBuilder inactiveUsers(long inactiveUsers) {
                stats.inactiveUsers = inactiveUsers;
                return this;
            }
            
            public UserStatisticsBuilder suspendedUsers(long suspendedUsers) {
                stats.suspendedUsers = suspendedUsers;
                return this;
            }
            
            public UserStatisticsBuilder bannedUsers(long bannedUsers) {
                stats.bannedUsers = bannedUsers;
                return this;
            }
            
            public UserStatisticsBuilder verifiedUsers(long verifiedUsers) {
                stats.verifiedUsers = verifiedUsers;
                return this;
            }
            
            public UserStatisticsBuilder averagePoints(Double averagePoints) {
                stats.averagePoints = averagePoints;
                return this;
            }
            
            public UserStatisticsBuilder averageLevel(Double averageLevel) {
                stats.averageLevel = averageLevel;
                return this;
            }
            
            public UserStatisticsBuilder maxPoints(Integer maxPoints) {
                stats.maxPoints = maxPoints;
                return this;
            }
            
            public UserStatisticsBuilder maxLevel(Integer maxLevel) {
                stats.maxLevel = maxLevel;
                return this;
            }
            
            public UserStatisticsBuilder maxStreak(Integer maxStreak) {
                stats.maxStreak = maxStreak;
                return this;
            }
            
            public UserStatistics build() {
                return stats;
            }
        }
        
        // Getters
        public long getTotalUsers() { return totalUsers; }
        public long getActiveUsers() { return activeUsers; }
        public long getInactiveUsers() { return inactiveUsers; }
        public long getSuspendedUsers() { return suspendedUsers; }
        public long getBannedUsers() { return bannedUsers; }
        public long getVerifiedUsers() { return verifiedUsers; }
        public Double getAveragePoints() { return averagePoints; }
        public Double getAverageLevel() { return averageLevel; }
        public Integer getMaxPoints() { return maxPoints; }
        public Integer getMaxLevel() { return maxLevel; }
        public Integer getMaxStreak() { return maxStreak; }
    }
}
