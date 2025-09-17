package com.impulse.gamification;

import jakarta.persistence.*;

@Entity
@Table(name = "mission_objective")
public class MissionObjective {
    public String getMissionId() { return missionId; }
    @Id
    private String id;
    private String missionId;
    private String action;
    private int targetCount;
    private int currentCount;
    private boolean isCompleted;
    private String description;
    // getters y setters
}
