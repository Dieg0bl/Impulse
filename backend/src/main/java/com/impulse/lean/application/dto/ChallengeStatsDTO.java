package com.impulse.lean.application.dto;

public class ChallengeStatsDTO {

    private final long totalParticipants;
    private final long activeParticipants;
    private final long completedParticipants;
    private final long abandonedParticipants;
    private final long totalEvidence;
    private final long pendingEvidence;
    private final long approvedEvidence;
    private final double completionRate;

    public ChallengeStatsDTO(long totalParticipants, long activeParticipants, 
                           long completedParticipants, long abandonedParticipants,
                           long totalEvidence, long pendingEvidence, long approvedEvidence,
                           double completionRate) {
        this.totalParticipants = totalParticipants;
        this.activeParticipants = activeParticipants;
        this.completedParticipants = completedParticipants;
        this.abandonedParticipants = abandonedParticipants;
        this.totalEvidence = totalEvidence;
        this.pendingEvidence = pendingEvidence;
        this.approvedEvidence = approvedEvidence;
        this.completionRate = completionRate;
    }

    // Getters
    public long getTotalParticipants() {
        return totalParticipants;
    }

    public long getActiveParticipants() {
        return activeParticipants;
    }

    public long getCompletedParticipants() {
        return completedParticipants;
    }

    public long getAbandonedParticipants() {
        return abandonedParticipants;
    }

    public long getTotalEvidence() {
        return totalEvidence;
    }

    public long getPendingEvidence() {
        return pendingEvidence;
    }

    public long getApprovedEvidence() {
        return approvedEvidence;
    }

    public double getCompletionRate() {
        return completionRate;
    }
}
