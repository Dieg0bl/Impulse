package com.impulse.gamification;

import jakarta.persistence.*;

@Entity
@Table(name = "mission_reward")
public class MissionReward {
    public String getMissionId() { return missionId; }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String missionId;
    private String type;
    private String currency;
    private Double amount;
    private String chestId;
    private String itemId;
    private String badgeId;
    private String name;
    // getters y setters
}
