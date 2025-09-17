package com.impulse.adapters.persistence.validatorassignment.repository;

import com.impulse.adapters.persistence.validatorassignment.entity.ValidatorAssignmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataValidatorAssignmentRepository extends JpaRepository<ValidatorAssignmentJpaEntity, UUID> {

    List<ValidatorAssignmentJpaEntity> findByValidatorIdOrderByAssignedDateDesc(UUID validatorId);

    List<ValidatorAssignmentJpaEntity> findByEvidenceIdOrderByAssignedDateDesc(UUID evidenceId);

    List<ValidatorAssignmentJpaEntity> findByStatus(String status);

    List<ValidatorAssignmentJpaEntity> findByStatusOrderByAssignedDate(String status);

    @Query("SELECT va FROM ValidatorAssignmentJpaEntity va WHERE va.deadline < :now AND va.status = 'PENDING'")
    List<ValidatorAssignmentJpaEntity> findOverdueAssignments(@Param("now") LocalDateTime now);

    @Query("SELECT va FROM ValidatorAssignmentJpaEntity va WHERE va.validatorId = :validatorId AND va.status = :status")
    List<ValidatorAssignmentJpaEntity> findByValidatorIdAndStatus(@Param("validatorId") UUID validatorId, @Param("status") String status);

    @Query("SELECT va FROM ValidatorAssignmentJpaEntity va WHERE va.evidenceId = :evidenceId AND va.status = :status")
    List<ValidatorAssignmentJpaEntity> findByEvidenceIdAndStatus(@Param("evidenceId") UUID evidenceId, @Param("status") String status);

    @Query("SELECT COUNT(va) FROM ValidatorAssignmentJpaEntity va WHERE va.validatorId = :validatorId AND va.status = 'COMPLETED'")
    long countCompletedAssignmentsByValidator(@Param("validatorId") UUID validatorId);

    @Query("SELECT va FROM ValidatorAssignmentJpaEntity va WHERE va.assignedDate BETWEEN :startDate AND :endDate")
    List<ValidatorAssignmentJpaEntity> findByAssignedDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
