package com.impulse.application.freemium;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.time.*;

@Service
public class FreemiumService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    // Simulación de tiers y upgrades (en producción, cargar desde BD o config)

    private static final List<SubscriptionTier> TIERS_CACHE = buildTiers();

    // Ordering definition for tier hierarchy comparisons
    private static final List<String> TIER_ORDER = Arrays.asList("free","pro","premium");
    private static final String STATUS_TRIALING = "TRIALING";
    private static final String STATUS_ACTIVE = "ACTIVE";

    // Constants to reduce duplication (sonar/style concerns)
    private static final String CAT_CORE = "core";
    private static final String CAT_SOCIAL = "social";
    private static final String CAT_COACHING = "coaching";
    private static final String CAT_GAMIFICATION = "gamification";
    private static final String CAT_CUSTOMIZATION = "customization";
    private static final String CAT_ANALYTICS = "analytics";
    private static final String Q_BASIC = "basic";
    private static final String Q_STANDARD = "standard";
    private static final String Q_PREMIUM = "premium";
    private static final String Q_LUXURY = "luxury";
    private static final String B_ECONOMY = "economy";
    private static final String B_FEATURE = "feature";
    private static final String B_PRIORITY = "priority";
    private static final String B_SUPPORT = "support";
    private static final String B_SOCIAL = "social";

    private static List<SubscriptionTier> buildTiers() {
    // FREE
    SubscriptionTier free = new SubscriptionTier();
    free.setId("free"); free.setName("IMPULSE Free"); free.setDescription("Todo lo esencial para empezar tu journey");
    free.setMonthlyPrice(0); free.setYearlyPrice(0); free.setYearlyDiscount(0); free.setTrialDays(0);
    free.setFeatures(Arrays.asList(
        feature("basic_challenges","Retos Básicos","Acceso a retos predefinidos",CAT_CORE,true,null,Q_BASIC),
        feature("peer_validation","Validación Peer-to-Peer","Validación entre usuarios",CAT_SOCIAL,false,10,Q_BASIC),
        feature("basic_gamification","Gamificación Básica","Rachas, puntos y badges básicos",CAT_GAMIFICATION,true,null,Q_BASIC),
        feature("ai_coaching","Coaching IA","Tips y mensajes automáticos",CAT_COACHING,true,null,Q_BASIC),
        feature("community_access","Acceso Comunidad","Participación en comunidad global",CAT_SOCIAL,true,null,Q_BASIC)));
    TierLimits freeLimits = new TierLimits();
    freeLimits.setActiveChallenges(3); freeLimits.setMonthlyValidations(10); freeLimits.setCoachingMessages(5); freeLimits.setVideoCalls(0);
    freeLimits.setCustomTemplates(0); freeLimits.setPremiumThemes(0); freeLimits.setReferralBonuses(2); freeLimits.setStorageGB(1); freeLimits.setApiCallsPerDay(100);
    free.setLimits(freeLimits);
    free.setBenefits(Arrays.asList(
        benefit(B_FEATURE,"Sin límite de tiempo","Usa IMPULSE todo el tiempo que quieras","unlimited",true),
        benefit(B_ECONOMY,"Todas las currencies","Gana Motivación, Cred, SLA+ e IMPULSE Coins","4_currencies",true)));
    free.setTargetUsers(Arrays.asList("estudiantes","usuarios_nuevos","usuarios_casuales"));
    // upgrade incentives (mirror frontend)
    free.setUpgradeIncentives(Arrays.asList(
        incentive("limit_reached","active_challenges >= 3","¡Estás súper activo! ¿Qué tal más retos simultáneos?",20.0,"medium",7),
        incentive("feature_interest","clicked_coach_feature","Un coach humano puede acelerar 3x tu progreso",30.0,"low",14)
    ));

    // PRO
    SubscriptionTier pro = new SubscriptionTier();
    pro.setId("pro"); pro.setName("IMPULSE Pro"); pro.setDescription("Para quienes van en serio con sus objetivos");
    pro.setMonthlyPrice(19); pro.setYearlyPrice(190); pro.setYearlyDiscount(38); pro.setTrialDays(14);
    pro.setFeatures(Arrays.asList(
        feature("unlimited_challenges","Retos Ilimitados","Tantos retos como quieras",CAT_CORE,true,null,Q_STANDARD),
        feature("human_coach","Coach Humano","Coach certificado asignado",CAT_COACHING,false,50,Q_PREMIUM),
        feature("priority_validation","Validación Prioritaria","Validaciones en <24h",CAT_CORE,true,null,Q_PREMIUM),
        feature("advanced_analytics","Analytics Avanzado","Métricas detalladas",CAT_ANALYTICS,true,null,Q_PREMIUM),
        feature("custom_templates","Templates Personalizados","Crea tus propios templates",CAT_CUSTOMIZATION,false,10,Q_PREMIUM),
        feature("premium_themes","Themes Premium","Colecciones exclusivas",CAT_CUSTOMIZATION,false,5,Q_PREMIUM)));
    TierLimits proLimits = new TierLimits();
    proLimits.setActiveChallenges(-1); proLimits.setMonthlyValidations(50); proLimits.setCoachingMessages(50); proLimits.setVideoCalls(0);
    proLimits.setCustomTemplates(10); proLimits.setPremiumThemes(5); proLimits.setReferralBonuses(10); proLimits.setStorageGB(10); proLimits.setApiCallsPerDay(1000);
    pro.setLimits(proLimits);
    pro.setBenefits(Arrays.asList(
        benefit(B_PRIORITY,"Cola Prioritaria","Validaciones <24h","24h_sla",true),
        benefit(B_SUPPORT,"Soporte Premium","Respuesta <12h","12h_support",true),
        benefit(B_ECONOMY,"Bonus +25% Currencies","Gana 25% más","1.25x",true)));
    pro.setTargetUsers(Arrays.asList("profesionales","usuarios_comprometidos","estudiantes_serios"));
    pro.setUpgradeIncentives(Arrays.asList(
        incentive("feature_interest","clicked_video_calls","Con Premium obtienes 2 video calls mensuales",25.0,"medium",10),
        incentive("behavior_pattern","high_engagement_7days","Tu dedicación merece herramientas premium",40.0,"high",5)
    ));

    // PREMIUM
    SubscriptionTier premium = new SubscriptionTier();
    premium.setId("premium"); premium.setName("IMPULSE Premium"); premium.setDescription("La experiencia completa para champions");
    premium.setMonthlyPrice(49); premium.setYearlyPrice(490); premium.setYearlyDiscount(98); premium.setTrialDays(7);
    premium.setFeatures(Arrays.asList(
        feature("video_coaching","Video Coaching","Sesiones en vivo",CAT_COACHING,false,2,Q_LUXURY),
        feature("unlimited_messaging","Mensajes Ilimitados","Chat sin límites",CAT_COACHING,true,null,Q_LUXURY),
        feature("ai_insights","AI Insights","Análisis IA patrones",CAT_ANALYTICS,true,null,Q_LUXURY),
        feature("exclusive_themes","Themes Exclusivos","Colecciones solo Premium",CAT_CUSTOMIZATION,true,null,Q_LUXURY),
        feature("advanced_social","Features Sociales Avanzadas","Leagues privadas, challenges grupales",CAT_SOCIAL,true,null,Q_LUXURY)));
    TierLimits premiumLimits = new TierLimits();
    premiumLimits.setActiveChallenges(-1); premiumLimits.setMonthlyValidations(-1); premiumLimits.setCoachingMessages(-1); premiumLimits.setVideoCalls(2);
    premiumLimits.setCustomTemplates(-1); premiumLimits.setPremiumThemes(-1); premiumLimits.setReferralBonuses(-1); premiumLimits.setStorageGB(50); premiumLimits.setApiCallsPerDay(5000);
    premium.setLimits(premiumLimits);
    premium.setBenefits(Arrays.asList(
        benefit(B_FEATURE,"Video Calls Mensuales","2 sesiones de coaching","2_calls",true),
        benefit(B_SUPPORT,"Soporte VIP","Línea directa","vip_support",true),
        benefit(B_ECONOMY,"Bonus +50% Currencies","Gana 50% más","1.5x",true),
        benefit(B_SOCIAL,"Leagues Exclusivas","Acceso a competencias premium","exclusive_leagues",true)));
    premium.setTargetUsers(Arrays.asList("executives","coaches","influencers","power_users"));
    premium.setUpgradeIncentives(Collections.emptyList());
    return Arrays.asList(free, pro, premium);
    }

    private static TierFeature feature(String id,String name,String desc,String category,boolean unlimited,Integer quota,String quality){
    TierFeature tf = new TierFeature(); tf.setId(id); tf.setName(name); tf.setDescription(desc); tf.setCategory(category); tf.setUnlimited(unlimited); tf.setMonthlyQuota(quota); tf.setQualityLevel(quality); return tf; }
    private static TierBenefit benefit(String type,String name,String desc,String value,boolean highlight){
        TierBenefit b = new TierBenefit(); b.setType(type); b.setName(name); b.setDescription(desc); b.setValue(value); b.setHighlight(highlight); return b; }
    private static UpgradeIncentive incentive(String trigger,String condition,String message,Double discount,String urgency,int validityDays){
        UpgradeIncentive i = new UpgradeIncentive(); i.setTrigger(trigger); i.setCondition(condition); i.setMessage(message); i.setDiscount(discount); i.setUrgency(urgency); i.setValidityDays(validityDays); return i; }

    public List<SubscriptionTier> getAllTiers() { return TIERS_CACHE; }

    public SubscriptionTier getTierById(String id) {
        return getAllTiers().stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
    }

    public List<UpgradePath> getUpgradePaths() { return buildUpgradePaths(); }

    private List<UpgradePath> buildUpgradePaths(){
        UpgradePath freeToPro = new UpgradePath();
        freeToPro.setFromTier("free"); freeToPro.setToTier("pro");
        UpgradeTrigger t1 = new UpgradeTrigger(); t1.setType("usage_limit"); t1.setMetric("active_challenges"); t1.setThreshold("3"); t1.setFrequency("recurring"); t1.setPriority(1);
        UpgradeTrigger t2 = new UpgradeTrigger(); t2.setType("feature_demand"); t2.setMetric("coach_interest_clicks"); t2.setThreshold("3"); t2.setFrequency("once"); t2.setPriority(2);
        UpgradeMessaging m1 = new UpgradeMessaging(); m1.setHeadline("¿Listo para acelerar tu progreso?"); m1.setDescription("Con IMPULSE Pro tienes un coach humano"); m1.setBenefits(Arrays.asList("Coach humano","Validaciones <24h","Templates y themes premium","+25% monedas")); m1.setSocialProof("1,000 usuarios Pro 3x más rápido"); m1.setUrgency("30% descuento esta semana"); m1.setVisualCues(Arrays.asList("coach_avatar","speed_indicator"));
        UpgradeOffer o1 = new UpgradeOffer(); o1.setType("discount"); o1.setValue("30"); o1.setDuration(7); o1.setConditions(Arrays.asList("first_time_user")); o1.setExpirationDays(7);
        freeToPro.setTriggers(Arrays.asList(t1,t2)); freeToPro.setMessaging(m1); freeToPro.setIncentives(Arrays.asList(o1));

        UpgradePath proToPremium = new UpgradePath();
        proToPremium.setFromTier("pro"); proToPremium.setToTier("premium");
        UpgradeTrigger pt1 = new UpgradeTrigger(); pt1.setType("feature_demand"); pt1.setMetric("video_call_requests"); pt1.setThreshold("2"); pt1.setFrequency("recurring"); pt1.setPriority(1);
        UpgradeMessaging m2 = new UpgradeMessaging(); m2.setHeadline("Tu dedicación merece Premium"); m2.setDescription("Premium te da acompañamiento total"); m2.setBenefits(Arrays.asList("2 video calls","Mensajes ilimitados","AI insights","Leagues exclusivas")); m2.setSocialProof("Usuarios Premium 95% consistencia"); m2.setUrgency("Actualiza ahora y agenda call"); m2.setVisualCues(Arrays.asList("video_call_preview","exclusive_badge"));
        UpgradeOffer o2 = new UpgradeOffer(); o2.setType("feature_unlock"); o2.setValue("free_video_call"); o2.setDuration(30); o2.setConditions(Arrays.asList("upgrade_this_week")); o2.setExpirationDays(7);
        proToPremium.setTriggers(Arrays.asList(pt1)); proToPremium.setMessaging(m2); proToPremium.setIncentives(Arrays.asList(o2));
        return Arrays.asList(freeToPro, proToPremium);
    }

    public List<UpgradeExperience> getUpgradeExperiences() {
        UpgradeExperience soft = new UpgradeExperience();
        soft.setStrategy("soft_paywall"); soft.setScenario("user_tries_4th_challenge");
        UpgradeImplementation impl = new UpgradeImplementation(); impl.setDisplayType("modal"); impl.setTiming("immediate"); impl.setFrequency("once"); impl.setDismissible(true); impl.setAlternatives(Arrays.asList("continue_with_existing","complete_one_first"));
        UserExperienceFlow flow = new UserExperienceFlow(); flow.setEntry("¡Wow! Estás súper motivado con 4 retos"); flow.setDemonstration(Arrays.asList("Preview dashboard ilimitado","Highlight prioridad validaciones","Testimonial similar")); flow.setComparison("Free:3 | Pro:Ilimitados + Coach"); flow.setCallToAction("Desbloquea tu potencial por $19/mes"); flow.setFallback("Completa un reto actual primero");
        soft.setImplementation(impl); soft.setUserExperience(flow);
        return Arrays.asList(soft);
    }

    public SubscriptionStatusDTO getSubscriptionStatus(String userId){
        SubscriptionStatusDTO dto = new SubscriptionStatusDTO();
        dto.setCurrentTier("free");
        dto.setSubscriptionStatus("NONE");
        try {
            Long uid = Long.parseLong(userId);
            Subscription current = subscriptionRepository.findTopByUserIdOrderByCreatedAtDesc(uid);
            if(current!=null){
                dto.setSubscriptionStatus(current.getStatus());
                dto.setCurrentTier(current.getPlanId());
                if(STATUS_TRIALING.equals(current.getStatus()) && current.getTrialEnd()!=null){
                    long days = Duration.between(LocalDateTime.now(), current.getTrialEnd()).toDays();
                    dto.setTrialActive(days>0);
                    dto.setTrialDaysRemaining(Math.max(days,0));
                }
            }
        } catch (NumberFormatException ignored) {
            // Si el userId no es numérico simplemente devolvemos el default (usuario anónimo / no registrado)
        }
        String rec = recommendUpgrade(dto.getCurrentTier(), dto.isTrialActive());
        dto.setRecommendedUpgrade(rec);
        return dto;
    }

    private String recommendUpgrade(String tier, boolean trialActive){
        if("free".equals(tier) && !trialActive) return "pro";
        if("pro".equals(tier)) return "premium";
        return null;
    }

    // Lógica básica de upgrade de tier (mock)
    public boolean upgradeTier(String userId, String targetTier) {
        try {
            Long uid = Long.parseLong(userId);
            if(!isValidTier(targetTier)) return false;
            Subscription current = subscriptionRepository.findTopByUserIdOrderByCreatedAtDesc(uid);
            // If no subscription create new active subscription (first upgrade from free state)
            if (current == null) {
                Subscription s = new Subscription();
                s.setUserId(uid);
                s.setPlanId(targetTier);
                s.setPlanName(targetTier);
                s.setStatus(STATUS_ACTIVE);
                s.setCreatedAt(LocalDateTime.now());
                s.setUpdatedAt(LocalDateTime.now());
                subscriptionRepository.save(s);
                return true;
            }
            // Prevent downgrade or same-tier upgrade
            if(!isUpgrade(current.getPlanId(), targetTier)) return false;
            // If trialing same tier => convert to ACTIVE
            if(STATUS_TRIALING.equals(current.getStatus()) && current.getPlanId().equals(targetTier)) {
                current.setStatus(STATUS_ACTIVE);
            } else {
                current.setPlanId(targetTier);
                current.setPlanName(targetTier);
                current.setStatus(STATUS_ACTIVE);
            }
            current.setUpdatedAt(LocalDateTime.now());
            subscriptionRepository.save(current);
            return true;
        } catch (Exception e) {
            return false; // logging pending integration of central logger
        }
    }

    // Lógica básica de activación de trial (mock)
    public boolean startTrial(String userId, String targetTier) {
        try {
            Long uid = Long.parseLong(userId);
            if(!isValidTier(targetTier)) return false;
            SubscriptionTier tierConfig = getTierById(targetTier);
            if(tierConfig == null || tierConfig.getTrialDays() <= 0) return false;
            // Prevent second trial for same tier
            List<Subscription> all = subscriptionRepository.findByUserId(uid);
            boolean alreadyTrialed = all.stream().anyMatch(s -> targetTier.equals(s.getPlanId()) && s.getTrialStart()!=null);
            if(alreadyTrialed) return false;
            // If existing active sub to same or higher tier -> no trial
            Subscription current = subscriptionRepository.findTopByUserIdOrderByCreatedAtDesc(uid);
            if(current != null && (STATUS_ACTIVE.equals(current.getStatus()) || STATUS_TRIALING.equals(current.getStatus())) && !isUpgrade(current.getPlanId(), targetTier)) {
                return false; // cannot trial lower or same
            }
            Subscription trial = new Subscription();
            trial.setUserId(uid);
            trial.setPlanId(targetTier);
            trial.setPlanName(targetTier);
            trial.setStatus(STATUS_TRIALING);
            trial.setTrialStart(LocalDateTime.now());
            trial.setTrialEnd(LocalDateTime.now().plusDays(tierConfig.getTrialDays()));
            trial.setCreatedAt(LocalDateTime.now());
            trial.setUpdatedAt(LocalDateTime.now());
            subscriptionRepository.save(trial);
            return true;
        } catch (Exception e) {
            return false; // logging pending integration of central logger
        }
    }

    private boolean isValidTier(String tier){
        return TIER_ORDER.contains(tier);
    }
    private boolean isUpgrade(String from, String to){
        return TIER_ORDER.indexOf(to) > TIER_ORDER.indexOf(from);
    }
}
