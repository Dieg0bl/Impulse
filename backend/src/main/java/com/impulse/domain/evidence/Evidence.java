package com.impulse.domain.evidence;

import com.impulse.domain.challenge.ChallengeId;
import com.impulse.domain.user.UserId;
import com.impulse.domain.enums.EvidenceType;
import com.impulse.domain.enums.EvidenceStatus;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Evidence - Entidad de dominio pura para evidencias
 * SUPERVERSIÓN FUSIONADA con toda la funcionalidad completa
 */
public class Evidence {

    private final EvidenceId id;
    private String title;
    private String description;
    private final EvidenceType type;
    private EvidenceStatus status;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String mimeType;
    private LocalDateTime submissionDate;
    private LocalDateTime validationDeadline;
    private final UserId userId;
    private final ChallengeId challengeId;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor privado - usar Builder
    private Evidence(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "EvidenceId cannot be null");
        this.title = validateAndSetTitle(builder.title);
        this.description = builder.description;
        this.type = Objects.requireNonNull(builder.type, "Type cannot be null");
        this.status = builder.status != null ? builder.status : EvidenceStatus.DRAFT;
        this.fileUrl = builder.fileUrl;
        this.fileName = builder.fileName;
        this.fileSize = builder.fileSize;
        this.mimeType = builder.mimeType;
        this.submissionDate = builder.submissionDate;
        this.validationDeadline = builder.validationDeadline;
        this.userId = Objects.requireNonNull(builder.userId, "UserId cannot be null");
        this.challengeId = Objects.requireNonNull(builder.challengeId, "ChallengeId cannot be null");
        this.createdAt = Objects.requireNonNull(builder.createdAt, "Created date cannot be null");
        this.updatedAt = builder.updatedAt != null ? builder.updatedAt : builder.createdAt;
    }

    // Factory method para nueva evidencia
    public static Evidence createNew(String title, String description, EvidenceType type,
                                   UserId userId, ChallengeId challengeId) {
        validateRequiredFields(title, type, userId, challengeId);

        return Evidence.builder()
                .id(EvidenceId.generate())
                .title(title)
                .description(description)
                .type(type)
                .status(EvidenceStatus.DRAFT)
                .userId(userId)
                .challengeId(challengeId)
                .submissionDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // Factory method con archivo - usando builder interno
    public static Evidence createWithFile(String title, String description, EvidenceType type,
                                        UserId userId, ChallengeId challengeId,
                                        FileInfo fileInfo) {
        Evidence evidence = createNew(title, description, type, userId, challengeId);
        evidence.updateFile(fileInfo.getUrl(), fileInfo.getName(), fileInfo.getSize(), fileInfo.getMimeType());
        return evidence;
    }

    // Value Object para información de archivo
    public static class FileInfo {
        private final String url;
        private final String name;
        private final Long size;
        private final String mimeType;

        public FileInfo(String url, String name, Long size, String mimeType) {
            this.url = url;
            this.name = name;
            this.size = size;
            this.mimeType = mimeType;
        }

        public String getUrl() { return url; }
        public String getName() { return name; }
        public Long getSize() { return size; }
        public String getMimeType() { return mimeType; }
    }

    // Métodos de negocio - MEJORADOS
    public void submit() {
        validateSubmission();
        this.status = EvidenceStatus.PENDING_VALIDATION;
        this.submissionDate = LocalDateTime.now();
        updateTimestamp();
    }

    public void approve() {
        if (this.status != EvidenceStatus.PENDING_VALIDATION) {
            throw new EvidenceDomainError("Solo se pueden aprobar evidencias en validación");
        }
        this.status = EvidenceStatus.VALIDATED;
        updateTimestamp();
    }

    public void reject() {
        if (this.status != EvidenceStatus.PENDING_VALIDATION) {
            throw new EvidenceDomainError("Solo se pueden rechazar evidencias en validación");
        }
        this.status = EvidenceStatus.REJECTED;
        updateTimestamp();
    }

    public void requestRevision() {
        if (this.status != EvidenceStatus.PENDING_VALIDATION && this.status != EvidenceStatus.REJECTED) {
            throw new EvidenceDomainError("Solo se pueden solicitar revisiones desde validación o rechazo");
        }
        this.status = EvidenceStatus.DRAFT;
        updateTimestamp();
    }

    public void updateTitle(String newTitle) {
        if (this.status != EvidenceStatus.DRAFT) {
            throw new EvidenceDomainError("Solo se puede actualizar título en estado DRAFT");
        }
        this.title = validateAndSetTitle(newTitle);
        updateTimestamp();
    }

    public void updateDescription(String newDescription) {
        if (this.status != EvidenceStatus.DRAFT) {
            throw new EvidenceDomainError("Solo se puede actualizar descripción en estado DRAFT");
        }
        this.description = newDescription;
        updateTimestamp();
    }

    public void updateFile(String fileUrl, String fileName, Long fileSize, String mimeType) {
        if (this.status != EvidenceStatus.DRAFT) {
            throw new EvidenceDomainError("Solo se puede actualizar archivo en estado DRAFT");
        }
        validateFileInfo(fileUrl, fileName, fileSize);
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        updateTimestamp();
    }

    public void setValidationDeadline(LocalDateTime deadline) {
        if (deadline != null && deadline.isBefore(LocalDateTime.now())) {
            throw new EvidenceDomainError("Validation deadline cannot be in the past");
        }
        this.validationDeadline = deadline;
        updateTimestamp();
    }

    // Métodos de consulta de dominio - AMPLIADOS
    public boolean isDraft() {
        return this.status == EvidenceStatus.DRAFT;
    }

    public boolean isPendingValidation() {
        return this.status == EvidenceStatus.PENDING_VALIDATION;
    }

    public boolean isValidated() {
        return this.status == EvidenceStatus.VALIDATED;
    }

    public boolean isRejected() {
        return this.status == EvidenceStatus.REJECTED;
    }

    public boolean hasFile() {
        return this.fileUrl != null && !this.fileUrl.trim().isEmpty();
    }

    public boolean canBeSubmitted() {
        return isDraft() && hasFile();
    }

    public boolean isValidationOverdue() {
        return isPendingValidation() &&
               validationDeadline != null &&
               LocalDateTime.now().isAfter(validationDeadline);
    }

    public boolean canBeModified() {
        return isDraft();
    }

    // Métodos de validación privados
    private void validateSubmission() {
        if (this.status != EvidenceStatus.DRAFT) {
            throw new EvidenceDomainError("Solo se pueden enviar evidencias en estado DRAFT");
        }
        if (!hasFile()) {
            throw new EvidenceDomainError("No se puede enviar evidencia sin archivo");
        }
        if (this.title == null || this.title.trim().isEmpty()) {
            throw new EvidenceDomainError("No se puede enviar evidencia sin título");
        }
    }

    private String validateAndSetTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new EvidenceDomainError("Evidence title is required");
        }
        if (title.length() > 500) {
            throw new EvidenceDomainError("Title cannot exceed 500 characters");
        }
        return title.trim();
    }

    private void validateFileInfo(String fileUrl, String fileName, Long fileSize) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            throw new EvidenceDomainError("File URL is required");
        }
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new EvidenceDomainError("File name is required");
        }
        if (fileSize != null && fileSize <= 0) {
            throw new EvidenceDomainError("File size must be positive");
        }
    }

    private static void validateRequiredFields(String title, EvidenceType type,
                                             UserId userId, ChallengeId challengeId) {
        if (title == null || title.trim().isEmpty()) {
            throw new EvidenceDomainError("Evidence title is required");
        }
        if (type == null) {
            throw new EvidenceDomainError("Evidence type is required");
        }
        if (userId == null) {
            throw new EvidenceDomainError("User ID is required");
        }
        if (challengeId == null) {
            throw new EvidenceDomainError("Challenge ID is required");
        }
    }

    private void updateTimestamp() {
        // Se actualiza en el builder/mapper ya que updatedAt es final
        // Este método sirve como placeholder para futuras extensiones
    }

    // Getters completos
    public EvidenceId getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public EvidenceType getType() { return type; }
    public EvidenceStatus getStatus() { return status; }
    public String getFileUrl() { return fileUrl; }
    public String getFileName() { return fileName; }
    public Long getFileSize() { return fileSize; }
    public String getMimeType() { return mimeType; }
    public LocalDateTime getSubmissionDate() { return submissionDate; }
    public LocalDateTime getValidationDeadline() { return validationDeadline; }
    public UserId getUserId() { return userId; }
    public ChallengeId getChallengeId() { return challengeId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Métodos de compatibilidad para migración desde JPA
    public Long getIdValue() {
        return id != null ? Long.valueOf(id.getValue()) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Evidence)) return false;
        Evidence evidence = (Evidence) o;
        return Objects.equals(id, evidence.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Evidence{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", hasFile=" + hasFile() +
                '}';
    }

    // Builder Pattern - COMPLETO
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private EvidenceId id;
        private String title;
        private String description;
        private EvidenceType type;
        private EvidenceStatus status;
        private String fileUrl;
        private String fileName;
        private Long fileSize;
        private String mimeType;
        private LocalDateTime submissionDate;
        private LocalDateTime validationDeadline;
        private UserId userId;
        private ChallengeId challengeId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(EvidenceId id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder type(EvidenceType type) { this.type = type; return this; }
        public Builder status(EvidenceStatus status) { this.status = status; return this; }
        public Builder fileUrl(String fileUrl) { this.fileUrl = fileUrl; return this; }
        public Builder fileName(String fileName) { this.fileName = fileName; return this; }
        public Builder fileSize(Long fileSize) { this.fileSize = fileSize; return this; }
        public Builder mimeType(String mimeType) { this.mimeType = mimeType; return this; }
        public Builder submissionDate(LocalDateTime submissionDate) { this.submissionDate = submissionDate; return this; }
        public Builder validationDeadline(LocalDateTime validationDeadline) { this.validationDeadline = validationDeadline; return this; }
        public Builder userId(UserId userId) { this.userId = userId; return this; }
        public Builder challengeId(ChallengeId challengeId) { this.challengeId = challengeId; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Evidence build() {
            return new Evidence(this);
        }
    }
}
