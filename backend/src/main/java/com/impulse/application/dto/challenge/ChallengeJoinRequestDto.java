package com.impulse.application.dto.challenge;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ChallengeJoinRequestDto {

    @NotNull(message = "El ID del reto es obligatorio")
    private Long challengeId;

    @Size(max = 500, message = "El mensaje de motivación no puede exceder 500 caracteres")
    private String motivationMessage;

    private Boolean acceptTerms = false;

    private Boolean joinAsTeam = false;

    @Size(max = 100, message = "El nombre del equipo no puede exceder 100 caracteres")
    private String teamName;

    @Size(max = 300, message = "La descripción del equipo no puede exceder 300 caracteres")
    private String teamDescription;

    // Constructor por defecto
    public ChallengeJoinRequestDto() {
        // Constructor vacío para serialización/deserialización
    }

    // Constructor con parámetros principales
    public ChallengeJoinRequestDto(Long challengeId, String motivationMessage) {
        this.challengeId = challengeId;
        this.motivationMessage = motivationMessage;
        this.acceptTerms = true;
    }

    // Getters and Setters
    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public String getMotivationMessage() {
        return motivationMessage;
    }

    public void setMotivationMessage(String motivationMessage) {
        this.motivationMessage = motivationMessage;
    }

    public Boolean getAcceptTerms() {
        return acceptTerms;
    }

    public void setAcceptTerms(Boolean acceptTerms) {
        this.acceptTerms = acceptTerms;
    }

    public Boolean getJoinAsTeam() {
        return joinAsTeam;
    }

    public void setJoinAsTeam(Boolean joinAsTeam) {
        this.joinAsTeam = joinAsTeam;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamDescription() {
        return teamDescription;
    }

    public void setTeamDescription(String teamDescription) {
        this.teamDescription = teamDescription;
    }

    // Métodos de validación
    public boolean isValidForTeamJoin() {
        return joinAsTeam && teamName != null && !teamName.trim().isEmpty();
    }

    public boolean isTermsAccepted() {
        return acceptTerms != null && acceptTerms;
    }
}
