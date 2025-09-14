package com.impulse.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

import com.impulse.domain.enums.EvidenceType;
import com.impulse.domain.enums.EvidenceStatus;

@Entity
@Table(name = "evidence")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EvidenceType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EvidenceStatus status;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "submission_date")
    private LocalDateTime submissionDate;

    @Column(name = "validation_deadline")
    private LocalDateTime validationDeadline;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @OneToOne(mappedBy = "evidence", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Validation validation;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        submissionDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public User getUser() { return user; }
    public Challenge getChallenge() { return challenge; }
    public Validation getValidation() { return validation; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setType(EvidenceType type) { this.type = type; }
    public void setStatus(EvidenceStatus status) { this.status = status; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public void setSubmissionDate(LocalDateTime submissionDate) { this.submissionDate = submissionDate; }
    public void setValidationDeadline(LocalDateTime validationDeadline) { this.validationDeadline = validationDeadline; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setUser(User user) { this.user = user; }
    public void setChallenge(Challenge challenge) { this.challenge = challenge; }
    public void setValidation(Validation validation) { this.validation = validation; }
}
