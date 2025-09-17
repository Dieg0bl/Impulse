package com.impulse.freemium.dto;

import com.impulse.freemium.*;
import java.util.stream.Collectors;

public class FreemiumMapper {
    private FreemiumMapper() {}
    public static SubscriptionTierDTO toDTO(SubscriptionTier tier){
        SubscriptionTierDTO dto = new SubscriptionTierDTO();
        dto.setId(tier.getId());
        dto.setName(tier.getName());
        dto.setDescription(tier.getDescription());
        dto.setMonthlyPrice(tier.getMonthlyPrice());
        dto.setYearlyPrice(tier.getYearlyPrice());
        dto.setYearlyDiscount(tier.getYearlyDiscount());
        dto.setFeatures(tier.getFeatures().stream().map(FreemiumMapper::feature).collect(Collectors.toList()));
        dto.setLimits(limits(tier.getLimits()));
        dto.setBenefits(tier.getBenefits().stream().map(FreemiumMapper::benefit).collect(Collectors.toList()));
        dto.setTargetUsers(tier.getTargetUsers());
        if(tier.getUpgradeIncentives()!=null)
            dto.setUpgradeIncentives(tier.getUpgradeIncentives().stream().map(FreemiumMapper::incentive).collect(Collectors.toList()));
        dto.setTrialDays(tier.getTrialDays());
        return dto;
    }

    public static UpgradePathDTO toDTO(UpgradePath p){
        UpgradePathDTO dto = new UpgradePathDTO();
        dto.setFromTier(p.getFromTier());
        dto.setToTier(p.getToTier());
        if(p.getTriggers()!=null)
            dto.setTriggers(p.getTriggers().stream().map(FreemiumMapper::trigger).collect(Collectors.toList()));
        if(p.getMessaging()!=null) dto.setMessaging(messaging(p.getMessaging()));
        if(p.getIncentives()!=null) dto.setIncentives(p.getIncentives().stream().map(FreemiumMapper::offer).collect(Collectors.toList()));
        return dto;
    }

    public static UpgradeExperienceDTO toDTO(UpgradeExperience e){
        UpgradeExperienceDTO dto = new UpgradeExperienceDTO();
        dto.setStrategy(e.getStrategy());
        dto.setScenario(e.getScenario());
        if(e.getImplementation()!=null) dto.setImplementation(implementation(e.getImplementation()));
        if(e.getUserExperience()!=null) dto.setUserExperience(userExperience(e.getUserExperience()));
        return dto;
    }

    private static TierFeatureDTO feature(TierFeature f){
        TierFeatureDTO dto = new TierFeatureDTO();
        dto.setId(f.getId()); dto.setName(f.getName()); dto.setDescription(f.getDescription()); dto.setCategory(f.getCategory()); dto.setUnlimited(f.isUnlimited()); dto.setMonthlyQuota(f.getMonthlyQuota()); dto.setQualityLevel(f.getQualityLevel());
        return dto;
    }
    private static TierLimitsDTO limits(TierLimits l){
        TierLimitsDTO dto = new TierLimitsDTO();
        dto.setActiveChallenges(l.getActiveChallenges()); dto.setMonthlyValidations(l.getMonthlyValidations()); dto.setCoachingMessages(l.getCoachingMessages()); dto.setVideoCalls(l.getVideoCalls()); dto.setCustomTemplates(l.getCustomTemplates()); dto.setPremiumThemes(l.getPremiumThemes()); dto.setReferralBonuses(l.getReferralBonuses()); dto.setStorageGB(l.getStorageGB()); dto.setApiCallsPerDay(l.getApiCallsPerDay());
        return dto;
    }
    private static TierBenefitDTO benefit(TierBenefit b){
        TierBenefitDTO dto = new TierBenefitDTO();
        dto.setType(b.getType()); dto.setName(b.getName()); dto.setDescription(b.getDescription()); dto.setValue(String.valueOf(b.getValue())); dto.setHighlight(b.isHighlight());
        return dto;
    }
    private static UpgradeIncentiveDTO incentive(UpgradeIncentive i){
        UpgradeIncentiveDTO dto = new UpgradeIncentiveDTO();
        dto.setTrigger(i.getTrigger()); dto.setCondition(i.getCondition()); dto.setMessage(i.getMessage()); dto.setDiscount(i.getDiscount()); dto.setUrgency(i.getUrgency()); dto.setValidityDays(i.getValidityDays());
        return dto;
    }
    private static UpgradeTriggerDTO trigger(UpgradeTrigger t){
        UpgradeTriggerDTO dto = new UpgradeTriggerDTO();
        dto.setType(t.getType()); dto.setMetric(t.getMetric()); dto.setThreshold(t.getThreshold()); dto.setFrequency(t.getFrequency()); dto.setPriority(t.getPriority());
        return dto;
    }
    private static UpgradeMessagingDTO messaging(UpgradeMessaging m){
        UpgradeMessagingDTO dto = new UpgradeMessagingDTO();
        dto.setHeadline(m.getHeadline()); dto.setDescription(m.getDescription()); dto.setBenefits(m.getBenefits()); dto.setSocialProof(m.getSocialProof()); dto.setUrgency(m.getUrgency()); dto.setVisualCues(m.getVisualCues());
        return dto;
    }
    private static UpgradeOfferDTO offer(UpgradeOffer o){
        UpgradeOfferDTO dto = new UpgradeOfferDTO();
        dto.setType(o.getType()); dto.setValue(o.getValue()); dto.setDuration(o.getDuration()); dto.setConditions(o.getConditions()); dto.setExpirationDays(o.getExpirationDays());
        return dto;
    }
    private static UpgradeImplementationDTO implementation(UpgradeImplementation impl){
        UpgradeImplementationDTO dto = new UpgradeImplementationDTO();
        dto.setDisplayType(impl.getDisplayType()); dto.setTiming(impl.getTiming()); dto.setFrequency(impl.getFrequency()); dto.setDismissible(impl.isDismissible()); dto.setAlternatives(impl.getAlternatives());
        return dto;
    }
    private static UserExperienceFlowDTO userExperience(UserExperienceFlow u){
        UserExperienceFlowDTO dto = new UserExperienceFlowDTO();
        dto.setEntry(u.getEntry()); dto.setDemonstration(u.getDemonstration()); dto.setComparison(u.getComparison()); dto.setCallToAction(u.getCallToAction()); dto.setFallback(u.getFallback());
        return dto;
    }
}
