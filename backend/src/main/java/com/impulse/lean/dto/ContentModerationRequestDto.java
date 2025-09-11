package com.impulse.lean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * IMPULSE LEAN v1 - Content Moderation Request DTO
 * 
 * Request for content moderation operations
 * Includes content identification and moderation parameters
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@Schema(description = "Content moderation request")
public class ContentModerationRequestDto {

    @NotNull(message = "Content ID is required")
    @Schema(description = "Content identifier", example = "content_123")
    private String contentId;

    @NotBlank(message = "Content type is required")
    @Size(max = 50, message = "Content type must not exceed 50 characters")
    @Schema(description = "Type of content", example = "EVIDENCE")
    private String contentType;

    @NotBlank(message = "Content is required")
    @Size(max = 10000, message = "Content must not exceed 10000 characters")
    @Schema(description = "Content to moderate", example = "User submitted evidence description")
    private String content;

    @Schema(description = "Moderation flags to check", example = "[\"SPAM\", \"INAPPROPRIATE\"]")
    private String[] flags;

    @Schema(description = "Priority level", example = "HIGH")
    private String priority;

    @Schema(description = "Moderator ID for manual review", example = "mod_456")
    private String moderatorId;

    @Schema(description = "Additional context for moderation", example = "User reported content")
    private String context;

    @Schema(description = "Auto-moderation enabled", example = "true")
    private Boolean autoModeration = true;

    // Constructors
    public ContentModerationRequestDto() {}

    public ContentModerationRequestDto(String contentId, String contentType, String content) {
        this.contentId = contentId;
        this.contentType = contentType;
        this.content = content;
    }

    // Getters and Setters
    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getFlags() {
        return flags;
    }

    public void setFlags(String[] flags) {
        this.flags = flags;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getModeratorId() {
        return moderatorId;
    }

    public void setModeratorId(String moderatorId) {
        this.moderatorId = moderatorId;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Boolean getAutoModeration() {
        return autoModeration;
    }

    public void setAutoModeration(Boolean autoModeration) {
        this.autoModeration = autoModeration;
    }
}
