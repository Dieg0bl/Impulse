package com.impulse.infrastructure.persistence.repositories;

import com.impulse.domain.entities.User;
import com.impulse.domain.entities.User.UserStatus;
import com.impulse.domain.entities.User.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para User
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Búsquedas básicas
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    // Verificaciones de existencia
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String email);
    
    // Búsquedas por status
    List<User> findByStatus(UserStatus status);
    Page<User> findByStatus(UserStatus status, Pageable pageable);
    
    // Búsquedas por roles
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role")
    List<User> findByRole(@Param("role") UserRole role);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role")
    Page<User> findByRole(@Param("role") UserRole role, Pageable pageable);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r IN :roles")
    List<User> findByRolesIn(@Param("roles") List<UserRole> roles);
    
    // Búsquedas por email verificado
    List<User> findByEmailVerified(Boolean emailVerified);
    Page<User> findByEmailVerifiedAndStatus(Boolean emailVerified, UserStatus status, Pageable pageable);
    
    // Búsquedas por fecha
    List<User> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<User> findByLastLoginAtBetween(LocalDateTime start, LocalDateTime end);
    List<User> findByLastLoginAtIsNull(); // Usuarios que nunca han hecho login
    
    // Búsquedas por actividad
    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :date AND u.status = :status")
    List<User> findInactiveUsers(@Param("date") LocalDateTime date, @Param("status") UserStatus status);
    
    // Búsquedas por gamificación
    List<User> findByLevelGreaterThanEqual(Integer level);
    List<User> findByTotalPointsGreaterThanEqual(Integer points);
    
    @Query("SELECT u FROM User u ORDER BY u.totalPoints DESC")
    Page<User> findTopUsersByPoints(Pageable pageable);
    
    @Query("SELECT u FROM User u ORDER BY u.level DESC, u.totalPoints DESC")
    Page<User> findTopUsersByLevel(Pageable pageable);
    
    @Query("SELECT u FROM User u ORDER BY u.longestStreak DESC")
    Page<User> findTopUsersByStreak(Pageable pageable);
    
    // Estadísticas
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
    long countByStatus(@Param("status") UserStatus status);
    
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r = :role")
    long countByRole(@Param("role") UserRole role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :date")
    long countNewUsersFromDate(@Param("date") LocalDateTime date);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.lastLoginAt >= :date")
    long countActiveUsersFromDate(@Param("date") LocalDateTime date);
    
    // Búsqueda de texto
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);
    
    // Usuarios con achievements específicos
    @Query("SELECT u FROM User u WHERE u.challengesCompleted >= :minChallenges")
    List<User> findUsersWithMinChallenges(@Param("minChallenges") Integer minChallenges);
    
    @Query("SELECT u FROM User u WHERE u.evidencesSubmitted >= :minEvidences")
    List<User> findUsersWithMinEvidences(@Param("minEvidences") Integer minEvidences);
    
    // Ranking y leaderboards
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE' ORDER BY u.totalPoints DESC, u.level DESC")
    Page<User> findLeaderboardByPoints(Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE' ORDER BY u.challengesCompleted DESC, u.totalPoints DESC")
    Page<User> findLeaderboardByChallenges(Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE' AND u.currentStreak > 0 ORDER BY u.currentStreak DESC")
    Page<User> findActiveStreakLeaderboard(Pageable pageable);
    
    // Métricas avanzadas
    @Query("SELECT AVG(u.totalPoints) FROM User u WHERE u.status = 'ACTIVE'")
    Double getAveragePointsForActiveUsers();
    
    @Query("SELECT AVG(u.level) FROM User u WHERE u.status = 'ACTIVE'")
    Double getAverageLevelForActiveUsers();
    
    @Query("SELECT MAX(u.totalPoints) FROM User u")
    Integer getMaxPoints();
    
    @Query("SELECT MAX(u.level) FROM User u")
    Integer getMaxLevel();
    
    @Query("SELECT MAX(u.longestStreak) FROM User u")
    Integer getMaxStreak();
    
    // Usuarios por periodo
    @Query("SELECT DATE(u.createdAt) as date, COUNT(u) as count FROM User u " +
           "WHERE u.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(u.createdAt) ORDER BY date")
    List<Object[]> getUserRegistrationsByDay(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    // Usuarios con preferencias específicas
    @Query("SELECT u FROM User u JOIN u.preferences p WHERE KEY(p) = :key AND VALUE(p) = :value")
    List<User> findByPreference(@Param("key") String key, @Param("value") String value);
}
