package com.impulse.freemium;

import java.util.List;

public class UpgradePath {
    private String fromTier;
    private String toTier;
    private List<UpgradeTrigger> triggers;
    private UpgradeMessaging messaging;
    private List<UpgradeOffer> incentives;
    public String getFromTier() { return fromTier; }
    public void setFromTier(String fromTier) { this.fromTier = fromTier; }
    public String getToTier() { return toTier; }
    public void setToTier(String toTier) { this.toTier = toTier; }
    public List<UpgradeTrigger> getTriggers() { return triggers; }
    public void setTriggers(List<UpgradeTrigger> triggers) { this.triggers = triggers; }
    public UpgradeMessaging getMessaging() { return messaging; }
    public void setMessaging(UpgradeMessaging messaging) { this.messaging = messaging; }
    public List<UpgradeOffer> getIncentives() { return incentives; }
    public void setIncentives(List<UpgradeOffer> incentives) { this.incentives = incentives; }
}
