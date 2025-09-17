package com.impulse.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "coaching_feature")
public class CoachingFeature {
    @Id
    private String id;
    private String name;
    private String description;
    private String type;
    private boolean unlimitedUsage;
    private Integer monthlyQuota;
    private String tier;
    // getters y setters
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return this.id;
    }
}
