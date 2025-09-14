package com.impulse.application.dto.evidence;

import com.impulse.domain.enums.EvidenceStatus;
import com.impulse.domain.enums.EvidenceType;

/**
 * DTO para actualizar evidencia existente
 */
public class EvidenceUpdateRequestDto {

    private String title;
    private String description;
    private EvidenceType type;
    private EvidenceStatus status;
    private String content;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String mimeType;
    private String metadata;

    public EvidenceUpdateRequestDto() {
    }

    public EvidenceUpdateRequestDto(String title, String description, EvidenceType type) {
        this.title = title;
        this.description = description;
        this.type = type;
    }

    // Getters and Setters
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

    public EvidenceStatus getStatus() {
        return status;
    }

    public void setStatus(EvidenceStatus status) {
        this.status = status;
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
        return "EvidenceUpdateRequestDto{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", content='" + content + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", mimeType='" + mimeType + '\'' +
                ", metadata='" + metadata + '\'' +
                '}';
    }
}
