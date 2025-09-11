package com.impulse.validation.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.impulse.lean.domain.model.User;
import com.impulse.lean.domain.model.Validator;
import com.impulse.lean.domain.model.ValidatorSpecialty;
import com.impulse.lean.domain.model.ValidatorStatus;

/**
 * IMPULSE LEAN v1 - Validator Repository Interface
 * 
 * Repository for validator domain entity operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Repository
public interface ValidatorRepository extends JpaRepository<Validator, Long> {

    // Basic lookups
    Optional<Validator> findByUuid(String uuid);
    Optional<Validator> findByUser(User user);
    Optional<Validator> findByUserUuid(String userUuid);

    // Status queries
    List<Validator> findByStatus(ValidatorStatus status);
    List<Validator> findByStatusAndAvailable(ValidatorStatus status, Boolean available);
    
    @Query("SELECT v FROM Validator v WHERE v.status = 'ACTIVE'")
    List<Validator> findActiveValidators();

    @Query("SELECT v FROM Validator v WHERE v.status = 'ACTIVE' AND v.available = true")
    List<Validator> findAvailableValidators();

    @Query("SELECT v FROM Validator v WHERE v.status = 'ACTIVE' AND v.available = true " +
           "AND v.currentAssignments < v.maxAssignments")
    List<Validator> findValidatorsAcceptingAssignments();

    // Specialty queries
    @Query("SELECT v FROM Validator v JOIN v.specialties s WHERE s = :specialty")
    List<Validator> findBySpecialty(@Param("specialty") ValidatorSpecialty specialty);

    @Query("SELECT v FROM Validator v JOIN v.specialties s WHERE s = :specialty " +
           "AND v.status = 'ACTIVE' AND v.available = true")
    List<Validator> findAvailableValidatorsBySpecialty(@Param("specialty") ValidatorSpecialty specialty);

    @Query("SELECT v FROM Validator v JOIN v.specialties s WHERE s IN :specialties")
    List<Validator> findBySpecialties(@Param("specialties") List<ValidatorSpecialty> specialties);

    // Workload queries
    @Query("SELECT v FROM Validator v WHERE v.currentAssignments >= v.maxAssignments")
    List<Validator> findOverloadedValidators();

    @Query("SELECT v FROM Validator v WHERE v.currentAssignments = 0 AND v.available = true")
    List<Validator> findUnderutilizedValidators();

    @Query("SELECT v FROM Validator v WHERE v.currentAssignments < :maxAssignments")
    List<Validator> findValidatorsWithCapacity(@Param("maxAssignments") int maxAssignments);

    // Rating and performance queries
    @Query("SELECT v FROM Validator v WHERE v.rating >= :minRating ORDER BY v.rating DESC")
    List<Validator> findByMinRating(@Param("minRating") BigDecimal minRating);

    @Query("SELECT v FROM Validator v WHERE v.totalValidations >= :minValidations")
    List<Validator> findExperiencedValidators(@Param("minValidations") int minValidations);

    @Query("SELECT v FROM Validator v ORDER BY v.rating DESC")
    List<Validator> findTopRatedValidators(Pageable pageable);

    @Query("SELECT v FROM Validator v WHERE v.successfulValidations > 0 " +
           "ORDER BY (CAST(v.successfulValidations AS double) / v.totalValidations) DESC")
    List<Validator> findBySuccessRate(Pageable pageable);

    // Activity queries
    @Query("SELECT v FROM Validator v WHERE v.lastActivityAt >= :since")
    List<Validator> findActiveValidatorsSince(@Param("since") LocalDateTime since);

    @Query("SELECT v FROM Validator v WHERE v.lastActivityAt < :threshold")
    List<Validator> findInactiveValidators(@Param("threshold") LocalDateTime threshold);

    // Certification queries
    @Query("SELECT v FROM Validator v WHERE v.certifiedAt IS NOT NULL " +
           "AND (v.certificationExpiresAt IS NULL OR v.certificationExpiresAt > CURRENT_TIMESTAMP)")
    List<Validator> findCertifiedValidators();

    @Query("SELECT v FROM Validator v WHERE v.certificationExpiresAt IS NOT NULL " +
           "AND v.certificationExpiresAt < CURRENT_TIMESTAMP")
    List<Validator> findExpiredCertifications();

    @Query("SELECT v FROM Validator v WHERE v.certificationExpiresAt IS NOT NULL " +
           "AND v.certificationExpiresAt BETWEEN CURRENT_TIMESTAMP AND :threshold")
    List<Validator> findExpiringCertifications(@Param("threshold") LocalDateTime threshold);

    // Search capabilities
    @Query("SELECT v FROM Validator v WHERE " +
           "LOWER(v.user.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(v.user.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(v.user.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(v.expertise) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Validator> searchValidators(@Param("search") String searchTerm, Pageable pageable);

    @Query("SELECT v FROM Validator v WHERE " +
           "LOWER(v.expertise) LIKE LOWER(CONCAT('%', :domain, '%'))")
    List<Validator> findByExpertiseDomain(@Param("domain") String domain);

    // Statistics queries
    @Query("SELECT COUNT(v) FROM Validator v WHERE v.status = :status")
    long countByStatus(@Param("status") ValidatorStatus status);

    @Query("SELECT s, COUNT(v) FROM Validator v JOIN v.specialties s GROUP BY s")
    List<Object[]> countValidatorsBySpecialty();

    @Query("SELECT COUNT(v) FROM Validator v WHERE v.available = true")
    long countAvailableValidators();

    @Query("SELECT AVG(v.rating) FROM Validator v WHERE v.rating IS NOT NULL")
    BigDecimal getAverageRating();

    @Query("SELECT AVG(v.totalValidations) FROM Validator v")
    Double getAverageValidationsPerValidator();

    // Recommendation queries
    @Query("SELECT v FROM Validator v JOIN v.specialties s WHERE s = :specialty " +
           "AND v.status = 'ACTIVE' AND v.available = true " +
           "AND v.currentAssignments < v.maxAssignments " +
           "ORDER BY v.rating DESC, v.totalValidations DESC")
    List<Validator> findBestValidatorsForSpecialty(@Param("specialty") ValidatorSpecialty specialty, 
                                                   Pageable pageable);

    @Query("SELECT v FROM Validator v WHERE v.status = 'ACTIVE' AND v.available = true " +
           "AND v.currentAssignments < v.maxAssignments " +
           "ORDER BY v.currentAssignments ASC, v.rating DESC")
    List<Validator> findOptimalValidators(Pageable pageable);

    // Time-based queries
    @Query("SELECT v FROM Validator v WHERE v.createdAt >= :since")
    List<Validator> findValidatorsJoinedSince(@Param("since") LocalDateTime since);

    @Query("SELECT v FROM Validator v WHERE v.updatedAt >= :since")
    List<Validator> findRecentlyUpdatedValidators(@Param("since") LocalDateTime since);

    // Response time analysis
    @Query("SELECT v FROM Validator v WHERE v.averageResponseTimeHours IS NOT NULL " +
           "AND v.averageResponseTimeHours <= :maxHours")
    List<Validator> findFastResponseValidators(@Param("maxHours") BigDecimal maxHours);

    @Query("SELECT AVG(v.averageResponseTimeHours) FROM Validator v " +
           "WHERE v.averageResponseTimeHours IS NOT NULL")
    BigDecimal getAverageResponseTime();

    // Bulk operations support
    @Query("SELECT v FROM Validator v WHERE v.uuid IN :uuids")
    List<Validator> findByUuids(@Param("uuids") List<String> uuids);

    @Query("SELECT v FROM Validator v WHERE v.status = 'ACTIVE' " +
           "AND v.lastActivityAt < :threshold")
    List<Validator> findInactiveActiveValidators(@Param("threshold") LocalDateTime threshold);
}
