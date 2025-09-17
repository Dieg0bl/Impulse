package com.impulse.domain.coaching;

import java.util.Objects;

/**
 * CoachingFeature - Entidad de dominio para características de coaching
 */
public class CoachingFeature {
    private final String id;
    private final String name;
    private final String description;
    private final String type;
    private final boolean unlimitedUsage;
    private final Integer monthlyQuota;
    private final String tier;

    private CoachingFeature(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "ID cannot be null");
        this.name = Objects.requireNonNull(builder.name, "Name cannot be null");
        this.description = builder.description;
        this.type = Objects.requireNonNull(builder.type, "Type cannot be null");
        this.unlimitedUsage = builder.unlimitedUsage;
        this.monthlyQuota = builder.monthlyQuota;
        this.tier = Objects.requireNonNull(builder.tier, "Tier cannot be null");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static CoachingFeature create(String id, String name, String description, 
                                       String type, boolean unlimitedUsage, 
                                       Integer monthlyQuota, String tier) {
        return builder()
                .id(id)
                .name(name)
                .description(description)
                .type(type)
                .unlimitedUsage(unlimitedUsage)
                .monthlyQuota(monthlyQuota)
                .tier(tier)
                .build();
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public boolean isUnlimitedUsage() { return unlimitedUsage; }
    public Integer getMonthlyQuota() { return monthlyQuota; }
    public String getTier() { return tier; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoachingFeature that = (CoachingFeature) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class Builder {
        private String id;
        private String name;
        private String description;
        private String type;
        private boolean unlimitedUsage;
        private Integer monthlyQuota;
        private String tier;

        public Builder id(String id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder type(String type) { this.type = type; return this; }
        public Builder unlimitedUsage(boolean unlimitedUsage) { this.unlimitedUsage = unlimitedUsage; return this; }
        public Builder monthlyQuota(Integer monthlyQuota) { this.monthlyQuota = monthlyQuota; return this; }
        public Builder tier(String tier) { this.tier = tier; return this; }

        public CoachingFeature build() {
            return new CoachingFeature(this);
        }
    }
}
