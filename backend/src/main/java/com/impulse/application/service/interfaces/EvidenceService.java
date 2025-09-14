package com.impulse.application.service.interfaces;

import com.impulse.application.dto.evidence.*;
import com.impulse.application.dto.common.PaginationRequest;
import com.impulse.application.dto.common.PaginationResponse;
import com.impulse.domain.enums.EvidenceStatus;

import java.util.List;

/**
 * Interface for Evidence Service operations
 */
public interface EvidenceService {

    /**
     * Create new evidence
     */
    EvidenceResponseDto createEvidence(Long userId, EvidenceCreateRequestDto request);

    /**
     * Update existing evidence
     */
    EvidenceResponseDto updateEvidence(Long evidenceId, Long userId, EvidenceUpdateRequestDto request);

    /**
     * Get evidence by ID
     */
    EvidenceResponseDto getEvidenceById(Long id);

    /**
     * Get evidence by UUID
     */
    EvidenceResponseDto getEvidenceByUuid(String uuid);

    /**
     * Get all evidence for a user
     */
    PaginationResponse<EvidenceResponseDto> getEvidenceByUser(Long userId, PaginationRequest request);

    /**
     * Get all evidence for a challenge
     */
    PaginationResponse<EvidenceResponseDto> getEvidenceByChallenge(Long challengeId, PaginationRequest request);

    /**
     * Get evidence by status
     */
    PaginationResponse<EvidenceResponseDto> getEvidenceByStatus(EvidenceStatus status, PaginationRequest request);

    /**
     * Get evidence pending validation
     */
    List<EvidenceResponseDto> getEvidencePendingValidation();

    /**
     * Validate evidence
     */
    EvidenceResponseDto validateEvidence(Long evidenceId, Long validatorId, EvidenceValidationRequestDto request);

    /**
     * Approve evidence
     */
    EvidenceResponseDto approveEvidence(Long evidenceId, Long validatorId, String comments);

    /**
     * Reject evidence
     */
    EvidenceResponseDto rejectEvidence(Long evidenceId, Long validatorId, String reason);

    /**
     * Flag evidence for review
     */
    EvidenceResponseDto flagEvidence(Long evidenceId, Long userId, String reason);

    /**
     * Delete evidence
     */
    void deleteEvidence(Long evidenceId, Long userId);

    /**
     * Check if user can edit evidence
     */
    boolean canUserEditEvidence(Long userId, Long evidenceId);

    /**
     * Check if evidence needs validation
     */
    boolean needsValidation(Long evidenceId);

    /**
     * Get evidence statistics for user
     */
    EvidenceStatsDto getEvidenceStatistics(Long userId);

    /**
     * Get evidence statistics for challenge
     */
    EvidenceStatsDto getChallengeEvidenceStatistics(Long challengeId);

    /**
     * Search evidence by content
     */
    PaginationResponse<EvidenceResponseDto> searchEvidence(String query, PaginationRequest request);

    // Inner class for statistics
    class EvidenceStatsDto {
        private Long totalCount;
        private Long approvedCount;
        private Long rejectedCount;
        private Long pendingCount;
        private Double approvalRate;

        // Constructors, getters and setters
        public EvidenceStatsDto() {}

        public EvidenceStatsDto(Long totalCount, Long approvedCount, Long rejectedCount, Long pendingCount) {
            this.totalCount = totalCount;
            this.approvedCount = approvedCount;
            this.rejectedCount = rejectedCount;
            this.pendingCount = pendingCount;
            this.approvalRate = totalCount > 0 ? (approvedCount.doubleValue() / totalCount.doubleValue()) * 100 : 0.0;
        }

        // Getters and setters
        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }

        public Long getApprovedCount() { return approvedCount; }
        public void setApprovedCount(Long approvedCount) { this.approvedCount = approvedCount; }

        public Long getRejectedCount() { return rejectedCount; }
        public void setRejectedCount(Long rejectedCount) { this.rejectedCount = rejectedCount; }

        public Long getPendingCount() { return pendingCount; }
        public void setPendingCount(Long pendingCount) { this.pendingCount = pendingCount; }

        public Double getApprovalRate() { return approvalRate; }
        public void setApprovalRate(Double approvalRate) { this.approvalRate = approvalRate; }
    }
}
