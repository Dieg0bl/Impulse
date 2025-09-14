package com.impulse.infrastructure.persistence.repositories;

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

import com.impulse.domain.model.User;
import com.impulse.domain.model.Validator;
import com.impulse.domain.enums.ValidatorSpecialty;
import com.impulse.domain.enums.ValidatorStatus;

/**
 * Repository interface for Validator entity operations
 */
@Repository
public interface ValidatorRepository extends JpaRepository<Validator, Long> {

    /**
     * Find validator by user
     */
    Optional<Validator> findByUser(User user);

    /**
     * Find validator by user ID
     */
    Optional<Validator> findByUserId(Long userId);

    /**
     * Find validators by status
     */
    List<Validator> findByStatus(ValidatorStatus status);

    /**
     * Find active validators
     */
    @Query("SELECT v FROM Validator v WHERE v.status = 'ACTIVE' AND v.isAvailable = true")
    List<Validator> findActiveValidators();

    /**
     * Find validators by specialty
     */
    @Query("SELECT v FROM Validator v WHERE :specialty MEMBER OF v.specialties AND v.status = 'ACTIVE'")
    List<Validator> findBySpecialty(@Param("specialty") ValidatorSpecialty specialty);

    /**
     * Find validators with minimum rating
     */
    @Query("SELECT v FROM Validator v WHERE v.averageRating >= :minRating AND v.status = 'ACTIVE'")
    List<Validator> findByMinimumRating(@Param("minRating") BigDecimal minRating);

    /**
     * Find available validators for assignment
     */
    @Query("SELECT v FROM Validator v WHERE v.isAvailable = true AND v.status = 'ACTIVE' " +
           "AND v.currentCapacity < v.maxCapacity")
    List<Validator> findAvailableValidators();

    /**
     * Find validators by certification level
     */
    @Query("SELECT v FROM Validator v WHERE v.certificationLevel = :level AND v.status = 'ACTIVE'")
    List<Validator> findByCertificationLevel(@Param("level") String level);

    /**
     * Count active validators
     */
    @Query("SELECT COUNT(v) FROM Validator v WHERE v.status = 'ACTIVE'")
    Long countActiveValidators();

    /**
     * Find validators with pagination
     */
    Page<Validator> findByStatus(ValidatorStatus status, Pageable pageable);

    /**
     * Search validators by name or email
     */
    @Query("SELECT v FROM Validator v WHERE " +
           "LOWER(v.user.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(v.user.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Validator> searchValidators(@Param("query") String query, Pageable pageable);

    /**
     * Find top rated validators
     */
    @Query("SELECT v FROM Validator v WHERE v.averageRating IS NOT NULL " +
           "ORDER BY v.averageRating DESC")
    List<Validator> findTopRatedValidators(Pageable pageable);

    /**
     * Find validators by creation date range
     */
    @Query("SELECT v FROM Validator v WHERE v.createdAt BETWEEN :startDate AND :endDate")
    List<Validator> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    /**
     * Update validator availability
     */
    @Query("UPDATE Validator v SET v.isAvailable = :available WHERE v.id = :validatorId")
    void updateAvailability(@Param("validatorId") Long validatorId, @Param("available") Boolean available);

    /**
     * Get validator statistics
     */
    @Query("SELECT COUNT(va) FROM ValidatorAssignment va WHERE va.validator.id = :validatorId")
    Long getValidatorAssignmentCount(@Param("validatorId") Long validatorId);
}
