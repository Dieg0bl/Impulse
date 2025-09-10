package com.impulse.lean.domain.repository;

import com.impulse.lean.domain.model.User;
import com.impulse.lean.domain.model.UserRole;
import com.impulse.lean.domain.model.UserStatus;
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
 * IMPULSE LEAN v1 - User Repository Interface
 * 
 * Repository for user domain entity operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Basic lookups
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUuid(String uuid);
    
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Status and role queries
    List<User> findByStatus(UserStatus status);
    List<User> findByRole(UserRole role);
    
    @Query("SELECT u FROM User u WHERE u.status = :status AND u.role = :role")
    List<User> findByStatusAndRole(@Param("status") UserStatus status, @Param("role") UserRole role);

    // Active users
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE'")
    List<User> findActiveUsers();

    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE' AND u.role = :role")
    List<User> findActiveUsersByRole(@Param("role") UserRole role);

    // Pagination support
    Page<User> findByStatusOrderByCreatedAtDesc(UserStatus status, Pageable pageable);
    Page<User> findByRoleOrderByCreatedAtDesc(UserRole role, Pageable pageable);

    // Time-based queries
    @Query("SELECT u FROM User u WHERE u.createdAt >= :since")
    List<User> findUsersCreatedAfter(@Param("since") LocalDateTime since);

    @Query("SELECT u FROM User u WHERE u.lastLoginAt >= :since")
    List<User> findUsersActiveAfter(@Param("since") LocalDateTime since);

    // Search capabilities
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<User> searchUsers(@Param("search") String searchTerm, Pageable pageable);

    // Statistics queries
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
    long countByStatus(@Param("status") UserStatus status);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") UserRole role);

    @Query("SELECT u.role, COUNT(u) FROM User u GROUP BY u.role")
    List<Object[]> countUsersByRole();

    @Query("SELECT u.status, COUNT(u) FROM User u GROUP BY u.status")
    List<Object[]> countUsersByStatus();

    // Moderator and admin queries
    @Query("SELECT u FROM User u WHERE u.role IN ('ADMIN', 'MODERATOR') AND u.status = 'ACTIVE'")
    List<User> findActiveModerators();

    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN' AND u.status = 'ACTIVE'")
    List<User> findActiveAdmins();

    // Email verification
    @Query("SELECT u FROM User u WHERE u.emailVerified = false AND u.createdAt < :before")
    List<User> findUnverifiedUsersOlderThan(@Param("before") LocalDateTime before);

    // Custom business queries
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE' AND SIZE(u.challengeParticipations) > 0")
    List<User> findActiveUsersWithChallenges();

    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE' " +
           "ORDER BY SIZE(u.challengeParticipations) DESC")
    List<User> findMostActiveChallengeParticipants(Pageable pageable);
}
