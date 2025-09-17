package com.impulse.freemium.dto;

public class TierBenefitDTO {
    private String type;
    private String name;
    private String description;
    private String value;
    private boolean highlight;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public boolean isHighlight() { return highlight; }
    public void setHighlight(boolean highlight) { this.highlight = highlight; }
}
