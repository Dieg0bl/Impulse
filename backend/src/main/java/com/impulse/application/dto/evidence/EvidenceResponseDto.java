package com.impulse.application.dto.evidence;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.impulse.domain.model.Evidence;
import com.impulse.domain.enums.EvidenceStatus;
import com.impulse.domain.enums.EvidenceType;
import com.impulse.domain.model.EvidenceValidation;

/**
 * IMPULSE LEAN v1 - Evidence Response DTO
 *
 * Response DTO for evidence data transfer
 *
 * @author IMPULSE Team
 * @version 1.0.0
 */
public class EvidenceResponseDto {

    private Long id;
    private String uuid;
    private EvidenceType type;
    private String content;
    private String description;
    private Integer dayNumber;
    private String filePath;
    private String mimeType;
    private Long fileSize;
    private String metadata;
    private EvidenceStatus status;
    private BigDecimal validationScore;
    private String rejectionReason;
    private LocalDateTime submittedAt;
    private LocalDateTime validatedAt;

    // Participation and Challenge info
    private String participationUuid;
    private String challengeUuid;
    private String challengeTitle;
    private String userUuid;
    private String userName;

    // Validation info
    private List<EvidenceValidationDto> validations;
    private int validationCount;
    private boolean requiresValidation;
    private BigDecimal averageValidationScore;

    // Constructor
    public EvidenceResponseDto() {
        // Default constructor for framework instantiation
    }

    // Factory method to create from Entity
    public static EvidenceResponseDto fromEntity(Evidence evidence) {
        EvidenceResponseDto dto = new EvidenceResponseDto();
        dto.setId(evidence.getId());
        dto.setType(evidence.getType());
        dto.setContent(evidence.getTitle()); // Usando title como content
        dto.setDescription(evidence.getDescription());
        dto.setFilePath(evidence.getFileUrl());
        dto.setMimeType(evidence.getMimeType());
        dto.setFileSize(evidence.getFileSize());
        dto.setStatus(evidence.getStatus());
        dto.setSubmittedAt(evidence.getSubmissionDate());
        dto.setValidatedAt(evidence.getValidationDeadline());

        // Set user and challenge info
        if (evidence.getUser() != null) {
            dto.setUserUuid(evidence.getUser().getUuid());
            dto.setUserName(evidence.getUser().getUsername());
        }
        if (evidence.getChallenge() != null) {
            dto.setChallengeUuid(evidence.getChallenge().getUuid());
            dto.setChallengeTitle(evidence.getChallenge().getTitle());
        }

        return dto;
    }

    // Helper methods
    public boolean isValidated() {
        return validatedAt != null;
    }

    public boolean isApproved() {
        return status == EvidenceStatus.VALIDATED;
    }

    public boolean isRejected() {
        return status == EvidenceStatus.REJECTED;
    }

    public boolean isPending() {
        return status == EvidenceStatus.PENDING_VALIDATION;
    }

    public boolean isFlagged() {
        return status == EvidenceStatus.SUSPENDED;
    }

    public boolean hasFileAttachment() {
        return filePath != null && !filePath.trim().isEmpty();
    }

    public String getFileExtension() {
        if (filePath == null) return null;
        int lastDot = filePath.lastIndexOf('.');
        return lastDot > 0 ? filePath.substring(lastDot + 1) : null;
    }

    public double getQualityScore() {
        return averageValidationScore != null ? averageValidationScore.doubleValue() : 0.0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public EvidenceType getType() {
        return type;
    }

    public void setType(EvidenceType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(Integer dayNumber) {
        this.dayNumber = dayNumber;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public EvidenceStatus getStatus() {
        return status;
    }

    public void setStatus(EvidenceStatus status) {
        this.status = status;
    }

    public BigDecimal getValidationScore() {
        return validationScore;
    }

    public void setValidationScore(BigDecimal validationScore) {
        this.validationScore = validationScore;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(LocalDateTime validatedAt) {
        this.validatedAt = validatedAt;
    }

    public String getParticipationUuid() {
        return participationUuid;
    }

    public void setParticipationUuid(String participationUuid) {
        this.participationUuid = participationUuid;
    }

    public String getChallengeUuid() {
        return challengeUuid;
    }

    public void setChallengeUuid(String challengeUuid) {
        this.challengeUuid = challengeUuid;
    }

    public String getChallengeTitle() {
        return challengeTitle;
    }

    public void setChallengeTitle(String challengeTitle) {
        this.challengeTitle = challengeTitle;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<EvidenceValidationDto> getValidations() {
        return validations;
    }

    public void setValidations(List<EvidenceValidationDto> validations) {
        this.validations = validations;
    }

    public int getValidationCount() {
        return validationCount;
    }

    public void setValidationCount(int validationCount) {
        this.validationCount = validationCount;
    }

    public boolean isRequiresValidation() {
        return requiresValidation;
    }

    public void setRequiresValidation(boolean requiresValidation) {
        this.requiresValidation = requiresValidation;
    }

    public BigDecimal getAverageValidationScore() {
        return averageValidationScore;
    }

    public void setAverageValidationScore(BigDecimal averageValidationScore) {
        this.averageValidationScore = averageValidationScore;
    }

    // Nested DTO for Evidence Validation
    public static class EvidenceValidationDto {
        private Long id;
        private String validatorName;
        private BigDecimal score;
        private String comments;
        private LocalDateTime validatedAt;

        public static EvidenceValidationDto fromEntity(EvidenceValidation validation) {
            EvidenceValidationDto dto = new EvidenceValidationDto();
            dto.setId(validation.getId());
            dto.setScore(validation.getScore() != null ? BigDecimal.valueOf(validation.getScore()) : null);
            dto.setComments(validation.getFeedback());
            dto.setValidatedAt(validation.getValidatedAt());

            if (validation.getValidator() != null) {
                dto.setValidatorName(validation.getValidator().getUsername());
            }

            return dto;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getValidatorName() { return validatorName; }
        public void setValidatorName(String validatorName) { this.validatorName = validatorName; }

        public BigDecimal getScore() { return score; }
        public void setScore(BigDecimal score) { this.score = score; }

        public String getComments() { return comments; }
        public void setComments(String comments) { this.comments = comments; }

        public LocalDateTime getValidatedAt() { return validatedAt; }
        public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }
    }
}
