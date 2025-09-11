package com.impulse.lean.application.dto.evidence;

import java.math.BigDecimal;

import com.impulse.lean.domain.model.EvidenceType;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

/**
 * IMPULSE LEAN v1 - Evidence Update Request DTO
 * 
 * Request DTO for updating evidence submissions
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
public class EvidenceUpdateRequestDto {

    private EvidenceType type;

    @Size(min = 10, max = 5000, message = "Content must be between 10 and 5000 characters")
    private String content;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private Integer dayNumber;

    // File upload fields
    private String filePath;
    private String mimeType;
    private Long fileSize;
    private String metadata;

    // Validation fields
    @DecimalMin(value = "0.0", inclusive = true, message = "Validation score must be non-negative")
    @DecimalMax(value = "10.0", inclusive = true, message = "Validation score cannot exceed 10")
    private BigDecimal validationScore;

    // Constructor
    public EvidenceUpdateRequestDto() {}

    // Validation methods
    public boolean hasContent() {
        return content != null && !content.trim().isEmpty();
    }

    public boolean hasFileUpdate() {
        return filePath != null && !filePath.trim().isEmpty();
    }

    public boolean hasValidFileSize() {
        if (fileSize == null || type == null) return true;
        return fileSize <= type.getMaxFileSize();
    }

    public boolean hasValidMimeType() {
        if (mimeType == null || type == null) return true;
        
        String[] allowedTypes = type.getAllowedMimeTypes();
        for (String allowedType : allowedTypes) {
            if (mimeType.startsWith(allowedType)) {
                return true;
            }
        }
        return false;
    }

    // Getters and Setters
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

    public BigDecimal getValidationScore() {
        return validationScore;
    }

    public void setValidationScore(BigDecimal validationScore) {
        this.validationScore = validationScore;
    }
}
