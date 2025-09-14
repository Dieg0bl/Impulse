package com.impulse.infrastructure.persistence.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.impulse.domain.model.ValidatorAssignment;
import com.impulse.domain.model.Validator;
import com.impulse.domain.model.Evidence;
import com.impulse.domain.model.AssignmentStatus;

/**
 * Repository interface for ValidatorAssignment entity operations
 */
@Repository
public interface ValidatorAssignmentRepository extends JpaRepository<ValidatorAssignment, Long> {

    /**
     * Find assignments by validator
     */
    List<ValidatorAssignment> findByValidator(Validator validator);

    /**
     * Find assignments by validator ID
     */
    List<ValidatorAssignment> findByValidatorId(Long validatorId);

    /**
     * Find assignments by evidence
     */
    List<ValidatorAssignment> findByEvidence(Evidence evidence);

    /**
     * Find assignments by evidence ID
     */
    List<ValidatorAssignment> findByEvidenceId(Long evidenceId);

    /**
     * Find assignments by status
     */
    List<ValidatorAssignment> findByStatus(AssignmentStatus status);

    /**
     * Find pending assignments for validator
     */
    @Query("SELECT va FROM ValidatorAssignment va WHERE va.validator.id = :validatorId " +
           "AND va.status = 'PENDING' ORDER BY va.deadline ASC")
    List<ValidatorAssignment> findPendingAssignmentsByValidator(@Param("validatorId") Long validatorId);

    /**
     * Find overdue assignments
     */
    @Query("SELECT va FROM ValidatorAssignment va WHERE va.deadline < :currentTime " +
           "AND va.status IN ('PENDING', 'IN_PROGRESS')")
    List<ValidatorAssignment> findOverdueAssignments(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find assignments by status and validator
     */
    List<ValidatorAssignment> findByStatusAndValidator(AssignmentStatus status, Validator validator);

    /**
     * Find assignments by priority range
     */
    @Query("SELECT va FROM ValidatorAssignment va WHERE va.priority BETWEEN :minPriority AND :maxPriority " +
           "ORDER BY va.priority DESC, va.deadline ASC")
    List<ValidatorAssignment> findByPriorityRange(@Param("minPriority") Integer minPriority,
                                                  @Param("maxPriority") Integer maxPriority);

    /**
     * Find high priority assignments
     */
    @Query("SELECT va FROM ValidatorAssignment va WHERE va.priority >= 8 " +
           "AND va.status IN ('PENDING', 'IN_PROGRESS') ORDER BY va.priority DESC")
    List<ValidatorAssignment> findHighPriorityAssignments();

    /**
     * Check if validator is already assigned to evidence
     */
    @Query("SELECT COUNT(va) > 0 FROM ValidatorAssignment va WHERE va.validator.id = :validatorId " +
           "AND va.evidence.id = :evidenceId")
    Boolean isValidatorAssignedToEvidence(@Param("validatorId") Long validatorId,
                                         @Param("evidenceId") Long evidenceId);

    /**
     * Find assignments with pagination
     */
    Page<ValidatorAssignment> findByStatus(AssignmentStatus status, Pageable pageable);

    /**
     * Find assignments by validator with pagination
     */
    Page<ValidatorAssignment> findByValidator(Validator validator, Pageable pageable);

    /**
     * Count assignments by status for validator
     */
    @Query("SELECT COUNT(va) FROM ValidatorAssignment va WHERE va.validator.id = :validatorId " +
           "AND va.status = :status")
    Long countByValidatorAndStatus(@Param("validatorId") Long validatorId,
                                   @Param("status") AssignmentStatus status);

    /**
     * Find assignments created in date range
     */
    @Query("SELECT va FROM ValidatorAssignment va WHERE va.assignedAt BETWEEN :startDate AND :endDate")
    List<ValidatorAssignment> findByAssignedAtBetween(@Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);

    /**
     * Update assignment status
     */
    @Query("UPDATE ValidatorAssignment va SET va.status = :status, va.updatedAt = :updateTime " +
           "WHERE va.id = :assignmentId")
    void updateStatus(@Param("assignmentId") Long assignmentId,
                     @Param("status") AssignmentStatus status,
                     @Param("updateTime") LocalDateTime updateTime);

    /**
     * Find active assignments count for validator
     */
    @Query("SELECT COUNT(va) FROM ValidatorAssignment va WHERE va.validator.id = :validatorId " +
           "AND va.status IN ('PENDING', 'IN_PROGRESS')")
    Long countActiveAssignmentsByValidator(@Param("validatorId") Long validatorId);

    /**
     * Find assignments due soon
     */
    @Query("SELECT va FROM ValidatorAssignment va WHERE va.deadline BETWEEN :now AND :soonTime " +
           "AND va.status IN ('PENDING', 'IN_PROGRESS')")
    List<ValidatorAssignment> findAssignmentsDueSoon(@Param("now") LocalDateTime now,
                                                     @Param("soonTime") LocalDateTime soonTime);
}
