package com.impulse.freemium;

public class UpgradeExperience {
    private String strategy;
    private String scenario;
    private UpgradeImplementation implementation;
    private UserExperienceFlow userExperience;
    public String getStrategy() { return strategy; }
    public void setStrategy(String strategy) { this.strategy = strategy; }
    public String getScenario() { return scenario; }
    public void setScenario(String scenario) { this.scenario = scenario; }
    public UpgradeImplementation getImplementation() { return implementation; }
    public void setImplementation(UpgradeImplementation implementation) { this.implementation = implementation; }
    public UserExperienceFlow getUserExperience() { return userExperience; }
    public void setUserExperience(UserExperienceFlow userExperience) { this.userExperience = userExperience; }
}
