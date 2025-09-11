package com.impulse.lean.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.impulse.lean.domain.model.AssignmentStatus;
import com.impulse.lean.domain.model.Evidence;
import com.impulse.lean.domain.model.ValidationPriority;
import com.impulse.lean.domain.model.Validator;
import com.impulse.lean.domain.model.ValidatorAssignment;

/**
 * IMPULSE LEAN v1 - Validator Assignment Repository Interface
 * 
 * Repository for validator assignment domain entity operations
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Repository
public interface ValidatorAssignmentRepository extends JpaRepository<ValidatorAssignment, Long> {

    // Basic lookups
    Optional<ValidatorAssignment> findByUuid(String uuid);
    
    // Validator queries
    List<ValidatorAssignment> findByValidator(Validator validator);
    List<ValidatorAssignment> findByValidatorUuid(String validatorUuid);
    List<ValidatorAssignment> findByValidatorAndStatus(Validator validator, AssignmentStatus status);
    
    @Query("SELECT va FROM ValidatorAssignment va WHERE va.validator.uuid = :validatorUuid " +
           "AND va.status = :status ORDER BY va.assignedAt DESC")
    List<ValidatorAssignment> findByValidatorUuidAndStatus(@Param("validatorUuid") String validatorUuid,
                                                           @Param("status") AssignmentStatus status);

    // Evidence queries
    List<ValidatorAssignment> findByEvidence(Evidence evidence);
    List<ValidatorAssignment> findByEvidenceUuid(String evidenceUuid);
    List<ValidatorAssignment> findByEvidenceAndStatus(Evidence evidence, AssignmentStatus status);
    
    @Query("SELECT va FROM ValidatorAssignment va WHERE va.evidence.uuid = :evidenceUuid " +
           "AND va.status IN :statuses")
    List<ValidatorAssignment> findActiveAssignmentsByEvidence(@Param("evidenceUuid") String evidenceUuid,
                                                              @Param("statuses") List<AssignmentStatus> statuses);

    // Status queries
    List<ValidatorAssignment> findByStatus(AssignmentStatus status);
    
    @Query("SELECT va FROM ValidatorAssignment va WHERE va.status = 'ASSIGNED'")
    List<ValidatorAssignment> findPendingAssignments();

    @Query("SELECT va FROM ValidatorAssignment va WHERE va.status IN ('ASSIGNED', 'ACCEPTED', 'IN_PROGRESS')")
    List<ValidatorAssignment> findActiveAssignments();

    @Query("SELECT va FROM ValidatorAssignment va WHERE va.status = 'COMPLETED'")
    List<ValidatorAssignment> findCompletedAssignments();

    // Priority queries
    List<ValidatorAssignment> findByPriority(ValidationPriority priority);
    
    @Query("SELECT va FROM ValidatorAssignment va WHERE va.priority IN ('HIGH', 'URGENT', 'CRITICAL') " +
           "AND va.status IN ('ASSIGNED', 'ACCEPTED', 'IN_PROGRESS') " +
           "ORDER BY va.priority DESC, va.assignedAt ASC")
    List<ValidatorAssignment> findHighPriorityAssignments();

    @Query("SELECT va FROM ValidatorAssignment va WHERE va.priority = 'CRITICAL' " +
           "AND va.status IN ('ASSIGNED', 'ACCEPTED', 'IN_PROGRESS')")
    List<ValidatorAssignment> findCriticalAssignments();

    // Time-based queries
    @Query("SELECT va FROM ValidatorAssignment va WHERE va.dueDate IS NOT NULL " +
           "AND va.dueDate < CURRENT_TIMESTAMP " +
           "AND va.status IN ('ASSIGNED', 'ACCEPTED', 'IN_PROGRESS')")
    List<ValidatorAssignment> findOverdueAssignments();

    @Query("SELECT va FROM ValidatorAssignment va WHERE va.dueDate IS NOT NULL " +
           "AND va.dueDate BETWEEN CURRENT_TIMESTAMP AND :threshold " +
           "AND va.status IN ('ASSIGNED', 'ACCEPTED', 'IN_PROGRESS')")
    List<ValidatorAssignment> findAssignmentsDueSoon(@Param("threshold") LocalDateTime threshold);

    @Query("SELECT va FROM ValidatorAssignment va WHERE va.assignedAt >= :since")
    List<ValidatorAssignment> findAssignmentsSince(@Param("since") LocalDateTime since);

    @Query("SELECT va FROM ValidatorAssignment va WHERE va.completedAt BETWEEN :start AND :end")
    List<ValidatorAssignment> findCompletedBetween(@Param("start") LocalDateTime start, 
                                                   @Param("end") LocalDateTime end);

    // Validator workload queries
    @Query("SELECT va FROM ValidatorAssignment va WHERE va.validator.uuid = :validatorUuid " +
           "AND va.status IN ('ASSIGNED', 'ACCEPTED', 'IN_PROGRESS')")
    List<ValidatorAssignment> findActiveAssignmentsByValidator(@Param("validatorUuid") String validatorUuid);

    @Query("SELECT COUNT(va) FROM ValidatorAssignment va WHERE va.validator.uuid = :validatorUuid " +
           "AND va.status IN ('ASSIGNED', 'ACCEPTED', 'IN_PROGRESS')")
    long countActiveAssignmentsByValidator(@Param("validatorUuid") String validatorUuid);

    @Query("SELECT va FROM ValidatorAssignment va WHERE va.validator.uuid = :validatorUuid " +
           "AND va.status = 'ASSIGNED'")
    List<ValidatorAssignment> findPendingAssignmentsByValidator(@Param("validatorUuid") String validatorUuid);

    // Assignment combination queries
    @Query("SELECT va FROM ValidatorAssignment va WHERE va.validator.uuid = :validatorUuid " +
           "AND va.evidence.uuid = :evidenceUuid")
    Optional<ValidatorAssignment> findByValidatorAndEvidence(@Param("validatorUuid") String validatorUuid,
                                                             @Param("evidenceUuid") String evidenceUuid);

    @Query("SELECT va FROM ValidatorAssignment va WHERE va.validator.uuid = :validatorUuid " +
           "AND va.evidence.uuid = :evidenceUuid " +
           "AND va.status IN ('ASSIGNED', 'ACCEPTED', 'IN_PROGRESS')")
    Optional<ValidatorAssignment> findActiveAssignmentByValidatorAndEvidence(@Param("validatorUuid") String validatorUuid,
                                                                             @Param("evidenceUuid") String evidenceUuid);

    // Auto-assignment queries
    @Query("SELECT va FROM ValidatorAssignment va WHERE va.autoAssigned = true")
    List<ValidatorAssignment> findAutoAssignedAssignments();

    @Query("SELECT va FROM ValidatorAssignment va WHERE va.autoAssigned = false")
    List<ValidatorAssignment> findManualAssignments();

    // Notification queries
    @Query("SELECT va FROM ValidatorAssignment va WHERE va.notificationSent = false " +
           "AND va.status = 'ASSIGNED'")
    List<ValidatorAssignment> findAssignmentsNeedingNotification();

    @Query("SELECT va FROM ValidatorAssignment va WHERE va.reminderSent = false " +
           "AND va.status IN ('ASSIGNED', 'ACCEPTED') " +
           "AND va.assignedAt < :threshold")
    List<ValidatorAssignment> findAssignmentsNeedingReminder(@Param("threshold") LocalDateTime threshold);

    // Performance metrics
    @Query("SELECT AVG(EXTRACT(HOUR FROM (va.completedAt - va.assignedAt))) " +
           "FROM ValidatorAssignment va WHERE va.completedAt IS NOT NULL")
    Double getAverageCompletionTimeHours();

    @Query("SELECT AVG(EXTRACT(HOUR FROM (va.acceptedAt - va.assignedAt))) " +
           "FROM ValidatorAssignment va WHERE va.acceptedAt IS NOT NULL")
    Double getAverageAcceptanceTimeHours();

    @Query("SELECT va.validator, AVG(EXTRACT(HOUR FROM (va.completedAt - va.assignedAt))) " +
           "FROM ValidatorAssignment va WHERE va.completedAt IS NOT NULL " +
           "GROUP BY va.validator")
    List<Object[]> getAverageCompletionTimeByValidator();

    // Statistics queries
    @Query("SELECT COUNT(va) FROM ValidatorAssignment va WHERE va.status = :status")
    long countByStatus(@Param("status") AssignmentStatus status);

    @Query("SELECT va.status, COUNT(va) FROM ValidatorAssignment va GROUP BY va.status")
    List<Object[]> countAssignmentsByStatus();

    @Query("SELECT va.priority, COUNT(va) FROM ValidatorAssignment va GROUP BY va.priority")
    List<Object[]> countAssignmentsByPriority();

    @Query("SELECT COUNT(va) FROM ValidatorAssignment va WHERE va.autoAssigned = true")
    long countAutoAssigned();

    // Search and filtering
    @Query("SELECT va FROM ValidatorAssignment va WHERE " +
           "LOWER(va.assignmentReason) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(va.notes) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<ValidatorAssignment> searchAssignments(@Param("search") String searchTerm, Pageable pageable);

    // Cleanup queries
    @Query("SELECT va FROM ValidatorAssignment va WHERE va.status = 'COMPLETED' " +
           "AND va.completedAt < :threshold")
    List<ValidatorAssignment> findOldCompletedAssignments(@Param("threshold") LocalDateTime threshold);

    @Query("SELECT va FROM ValidatorAssignment va WHERE va.status IN ('CANCELLED', 'REJECTED') " +
           "AND va.updatedAt < :threshold")
    List<ValidatorAssignment> findOldCancelledAssignments(@Param("threshold") LocalDateTime threshold);

    // Validation workflow support
    @Query("SELECT va FROM ValidatorAssignment va WHERE va.evidence.id IN " +
           "(SELECT e.id FROM Evidence e WHERE e.status = 'PENDING') " +
           "AND va.status = 'COMPLETED'")
    List<ValidatorAssignment> findCompletedAssignmentsForPendingEvidence();

    @Query("SELECT va FROM ValidatorAssignment va WHERE va.validator.uuid = :validatorUuid " +
           "ORDER BY va.assignedAt DESC")
    Page<ValidatorAssignment> findByValidatorUuidOrderByAssignedAt(@Param("validatorUuid") String validatorUuid,
                                                                   Pageable pageable);
}
