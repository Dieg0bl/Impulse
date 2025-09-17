package com.impulse.freemium.dto;

public class UpgradeExperienceDTO {
    private String strategy;
    private String scenario;
    private UpgradeImplementationDTO implementation;
    private UserExperienceFlowDTO userExperience;

    public String getStrategy() { return strategy; }
    public void setStrategy(String strategy) { this.strategy = strategy; }
    public String getScenario() { return scenario; }
    public void setScenario(String scenario) { this.scenario = scenario; }
    public UpgradeImplementationDTO getImplementation() { return implementation; }
    public void setImplementation(UpgradeImplementationDTO implementation) { this.implementation = implementation; }
    public UserExperienceFlowDTO getUserExperience() { return userExperience; }
    public void setUserExperience(UserExperienceFlowDTO userExperience) { this.userExperience = userExperience; }
}
