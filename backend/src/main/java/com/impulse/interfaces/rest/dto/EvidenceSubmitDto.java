package com.impulse.interfaces.rest.dto;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO para envío de evidencia
 */
public class EvidenceSubmitDto {
    
    @NotNull(message = "Challenge ID es requerido")
    private Long challengeId;
    
    @NotBlank(message = "Descripción es requerida")
    @Size(min = 20, max = 2000, message = "Descripción debe tener entre 20 y 2000 caracteres")
    private String description;
    
    @NotBlank(message = "Tipo de evidencia es requerido")
    private String evidenceType;
    
    private String submissionUrl;
    private String repositoryUrl;
    private String deploymentUrl;
    
    @Size(max = 1000, message = "Notas adicionales no pueden exceder 1000 caracteres")
    private String additionalNotes;
    
    private Boolean isPublic = true;
    private Boolean allowComments = true;
    
    // Para archivos adjuntos
    private MultipartFile[] attachments;
    
    // Constructors
    public EvidenceSubmitDto() {}
    
    public EvidenceSubmitDto(Long challengeId, String description, String evidenceType) {
        this.challengeId = challengeId;
        this.description = description;
        this.evidenceType = evidenceType;
    }
    
    // Getters and Setters
    public Long getChallengeId() { return challengeId; }
    public void setChallengeId(Long challengeId) { this.challengeId = challengeId; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getEvidenceType() { return evidenceType; }
    public void setEvidenceType(String evidenceType) { this.evidenceType = evidenceType; }
    
    public String getSubmissionUrl() { return submissionUrl; }
    public void setSubmissionUrl(String submissionUrl) { this.submissionUrl = submissionUrl; }
    
    public String getRepositoryUrl() { return repositoryUrl; }
    public void setRepositoryUrl(String repositoryUrl) { this.repositoryUrl = repositoryUrl; }
    
    public String getDeploymentUrl() { return deploymentUrl; }
    public void setDeploymentUrl(String deploymentUrl) { this.deploymentUrl = deploymentUrl; }
    
    public String getAdditionalNotes() { return additionalNotes; }
    public void setAdditionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes; }
    
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
    
    public Boolean getAllowComments() { return allowComments; }
    public void setAllowComments(Boolean allowComments) { this.allowComments = allowComments; }
    
    public MultipartFile[] getAttachments() { return attachments; }
    public void setAttachments(MultipartFile[] attachments) { this.attachments = attachments; }
}
