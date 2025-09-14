package com.impulse.application.dto.evidence;

import com.impulse.domain.enums.EvidenceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para crear nueva evidencia
 */
public class EvidenceCreateRequestDto {

    @NotNull(message = "El ID del usuario no puede ser nulo")
    private Long userId;

    @NotNull(message = "El ID del challenge no puede ser nulo")
    private Long challengeId;

    @NotBlank(message = "El título no puede estar vacío")
    private String title;

    private String description;

    @NotNull(message = "El tipo de evidencia no puede ser nulo")
    private EvidenceType type;

    private String content;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String mimeType;
    private String metadata;

    public EvidenceCreateRequestDto() {
    }

    public EvidenceCreateRequestDto(Long userId, Long challengeId, String title, String description, EvidenceType type) {
        this.userId = userId;
        this.challengeId = challengeId;
        this.title = title;
        this.description = description;
        this.type = type;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "EvidenceCreateRequestDto{" +
                "userId=" + userId +
                ", challengeId=" + challengeId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", mimeType='" + mimeType + '\'' +
                ", metadata='" + metadata + '\'' +
                '}';
    }
}
