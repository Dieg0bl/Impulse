package com.impulse.application.evidencevalidation.port;

import com.impulse.domain.evidencevalidation.EvidenceValidation;
import com.impulse.domain.evidencevalidation.EvidenceValidationId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EvidenceValidationRepository {
    EvidenceValidation save(EvidenceValidation evidenceValidation);
    Optional<EvidenceValidation> findById(EvidenceValidationId id);
    List<EvidenceValidation> findByEvidenceId(String evidenceId);
    List<EvidenceValidation> findByValidatorId(String validatorId);
    List<EvidenceValidation> findByStatus(String status);

    // Métodos adicionales para paginación y funcionalidad avanzada
    Page<EvidenceValidation> findByStatus(String status, Pageable pageable);
    Page<EvidenceValidation> findPendingValidations(Pageable pageable);
    Page<EvidenceValidation> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    long countByValidatorId(String validatorId);
    long countByEvidenceId(String evidenceId);
    Double getAverageScoreByEvidence(String evidenceId);
    void deleteById(EvidenceValidationId id);
}
