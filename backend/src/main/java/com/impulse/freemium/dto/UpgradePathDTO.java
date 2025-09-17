package com.impulse.freemium.dto;

import java.util.List;

public class UpgradePathDTO {
    private String fromTier;
    private String toTier;
    private List<UpgradeTriggerDTO> triggers;
    private UpgradeMessagingDTO messaging;
    private List<UpgradeOfferDTO> incentives;

    public String getFromTier() { return fromTier; }
    public void setFromTier(String fromTier) { this.fromTier = fromTier; }
    public String getToTier() { return toTier; }
    public void setToTier(String toTier) { this.toTier = toTier; }
    public List<UpgradeTriggerDTO> getTriggers() { return triggers; }
    public void setTriggers(List<UpgradeTriggerDTO> triggers) { this.triggers = triggers; }
    public UpgradeMessagingDTO getMessaging() { return messaging; }
    public void setMessaging(UpgradeMessagingDTO messaging) { this.messaging = messaging; }
    public List<UpgradeOfferDTO> getIncentives() { return incentives; }
    public void setIncentives(List<UpgradeOfferDTO> incentives) { this.incentives = incentives; }
}
