package com.impulse.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Entity representing an idempotency token for preventing duplicate operations
 */
@Entity
@Table(name = "idempotency_tokens", indexes = {
    @Index(name = "idx_token", columnList = "token"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_token_user", columnList = "token, user_id"),
    @Index(name = "idx_expires_at", columnList = "expires_at"),
    @Index(name = "idx_operation_type", columnList = "operation_type"),
    @Index(name = "idx_is_used", columnList = "is_used")
})
public class IdempotencyToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "token", nullable = false, unique = true, length = 255)
    private String token;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotBlank
    @Column(name = "operation_type", nullable = false, length = 100)
    private String operationType;

    @Column(name = "operation_data", columnDefinition = "TEXT")
    private String operationData;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "result_data", columnDefinition = "TEXT")
    private String resultData;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Version
    private Long version;

    // Constructors
    public IdempotencyToken() {
        this.createdAt = LocalDateTime.now();
        this.isUsed = false;
    }

    public IdempotencyToken(String token, Long userId, String operationType) {
        this();
        this.token = token;
        this.userId = userId;
        this.operationType = operationType;
        this.expiresAt = LocalDateTime.now().plusHours(24); // Default 24 hours expiry
    }

    public IdempotencyToken(String token, Long userId, String operationType, LocalDateTime expiresAt) {
        this();
        this.token = token;
        this.userId = userId;
        this.operationType = operationType;
        this.expiresAt = expiresAt;
    }

    // Business methods
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !isUsed && !isExpired();
    }

    public void markAsUsed() {
        this.isUsed = true;
        this.usedAt = LocalDateTime.now();
    }

    public void markAsUsed(String resultData, Integer httpStatus) {
        markAsUsed();
        this.resultData = resultData;
        this.httpStatus = httpStatus;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationData() {
        return operationData;
    }

    public void setOperationData(String operationData) {
        this.operationData = operationData;
    }

    public Boolean getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }

    public String getResultData() {
        return resultData;
    }

    public void setResultData(String resultData) {
        this.resultData = resultData;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "IdempotencyToken{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", userId=" + userId +
                ", operationType='" + operationType + '\'' +
                ", isUsed=" + isUsed +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                ", usedAt=" + usedAt +
                '}';
    }
}
