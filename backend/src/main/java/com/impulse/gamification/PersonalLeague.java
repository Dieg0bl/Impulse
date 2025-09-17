package com.impulse.gamification;

import jakarta.persistence.*;

@Entity
@Table(name = "personal_league")
public class PersonalLeague {
    @Id
    private String id;
    private String name;
    private String tier;
    private double minValidationRate;
    private int minCredPoints;
    private int minStreakDays;
    private int windowDays;
    private String badgeIcon;
    private String frameColor;
    private double credBonus;
    private int prestigePoints;
    private String description;
    // getters y setters
}
