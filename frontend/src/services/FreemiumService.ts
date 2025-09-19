// Sistema Freemium Inteligente - Free tier generoso, Pro benefits claros, upgrade natural
import FreemiumApiClient from './FreemiumApiClient'

// TIERS DE SUBSCRIPTION
export interface SubscriptionTier {
  id: string
  name: string
  description: string
  monthlyPrice: number
  yearlyPrice: number
  yearlyDiscount: number
  features: TierFeature[]
  limits: TierLimits
  benefits: TierBenefit[]
  targetUsers: string[]
  upgradeIncentives: UpgradeIncentive[]
  trialDays: number
}

export interface TierFeature {
  id: string
  name: string
  description: string
  category: 'core' | 'social' | 'coaching' | 'gamification' | 'customization' | 'analytics'
  isUnlimited: boolean
  monthlyQuota?: number
  qualityLevel: 'basic' | 'standard' | 'premium' | 'luxury'
}

export interface TierLimits {
  activeChallenges: number
  monthlyValidations: number
  coachingMessages: number
  videoCalls: number
  customTemplates: number
  premiumThemes: number
  referralBonuses: number
  storageGB: number
  apiCallsPerDay: number
}

export interface TierBenefit {
  type: 'feature' | 'priority' | 'support' | 'customization' | 'social' | 'economy'
  name: string
  description: string
  value: string | number
  isHighlight: boolean
}

export interface UpgradeIncentive {
  trigger: 'limit_reached' | 'feature_interest' | 'time_based' | 'behavior_pattern' | 'social_pressure'
  condition: string
  message: string
  discount?: number
  urgency: 'low' | 'medium' | 'high'
  validityDays: number
}

export const SUBSCRIPTION_TIERS: SubscriptionTier[] = [
  {
    id: 'free',
    name: 'IMPULSE Free',
    description: 'Todo lo esencial para empezar tu journey',
    monthlyPrice: 0,
    yearlyPrice: 0,
    yearlyDiscount: 0,
    features: [
      {
        id: 'basic_challenges',
        name: 'Retos Básicos',
        description: 'Acceso a retos predefinidos',
        category: 'core',
        isUnlimited: true,
        qualityLevel: 'basic'
      },
      {
        id: 'peer_validation',
        name: 'Validación Peer-to-Peer',
        description: 'Validación entre usuarios',
        category: 'social',
        isUnlimited: false,
        monthlyQuota: 10,
        qualityLevel: 'basic'
      },
      {
        id: 'basic_gamification',
        name: 'Gamificación Básica',
        description: 'Rachas, puntos y badges básicos',
        category: 'gamification',
        isUnlimited: true,
        qualityLevel: 'basic'
      },
      {
        id: 'ai_coaching',
        name: 'Coaching IA',
        description: 'Tips y mensajes motivacionales automáticos',
        category: 'coaching',
        isUnlimited: true,
        qualityLevel: 'basic'
      },
      {
        id: 'community_access',
        name: 'Acceso Comunidad',
        description: 'Participación en la comunidad global',
        category: 'social',
        isUnlimited: true,
        qualityLevel: 'basic'
      }
    ],
    limits: {
      activeChallenges: 3,
      monthlyValidations: 10,
      coachingMessages: 5,
      videoCalls: 0,
      customTemplates: 0,
      premiumThemes: 0,
      referralBonuses: 2,
      storageGB: 1,
      apiCallsPerDay: 100
    },
    benefits: [
      {
        type: 'feature',
        name: 'Sin límite de tiempo',
        description: 'Usa IMPULSE todo el tiempo que quieras',
        value: 'unlimited',
        isHighlight: true
      },
      {
        type: 'economy',
        name: 'Todas las currencies',
        description: 'Gana Motivación, Cred, SLA+ e IMPULSE Coins',
        value: '4_currencies',
        isHighlight: true
      }
    ],
    targetUsers: ['estudiantes', 'usuarios_nuevos', 'usuarios_casuales'],
    upgradeIncentives: [
      {
        trigger: 'limit_reached',
        condition: 'active_challenges >= 3',
        message: '¡Estás súper activo! ¿Qué tal más retos simultáneos?',
        discount: 20,
        urgency: 'medium',
        validityDays: 7
      },
      {
        trigger: 'feature_interest',
        condition: 'clicked_coach_feature',
        message: 'Un coach humano puede acelerar 3x tu progreso',
        discount: 30,
        urgency: 'low',
        validityDays: 14
      }
    ],
    trialDays: 0
  },
  {
    id: 'pro',
    name: 'IMPULSE Pro',
    description: 'Para quienes van en serio con sus objetivos',
    monthlyPrice: 19,
    yearlyPrice: 190, // 2 meses gratis
    yearlyDiscount: 38,
    features: [
      {
        id: 'unlimited_challenges',
        name: 'Retos Ilimitados',
        description: 'Tantos retos como quieras',
        category: 'core',
        isUnlimited: true,
        qualityLevel: 'standard'
      },
      {
        id: 'human_coach',
        name: 'Coach Humano',
        description: 'Coach certificado asignado',
        category: 'coaching',
        isUnlimited: false,
        monthlyQuota: 50,
        qualityLevel: 'premium'
      },
      {
        id: 'priority_validation',
        name: 'Validación Prioritaria',
        description: 'Validaciones en <24h garantizadas',
        category: 'core',
        isUnlimited: true,
        qualityLevel: 'premium'
      },
      {
        id: 'advanced_analytics',
        name: 'Analytics Avanzado',
        description: 'Métricas detalladas de progreso',
        category: 'analytics',
        isUnlimited: true,
        qualityLevel: 'premium'
      },
      {
        id: 'custom_templates',
        name: 'Templates Personalizados',
        description: 'Crea tus propios templates',
        category: 'customization',
        isUnlimited: false,
        monthlyQuota: 10,
        qualityLevel: 'premium'
      },
      {
        id: 'premium_themes',
        name: 'Themes Premium',
        description: 'Colecciones exclusivas de themes',
        category: 'customization',
        isUnlimited: false,
        monthlyQuota: 5,
        qualityLevel: 'premium'
      }
    ],
    limits: {
      activeChallenges: -1, // unlimited
      monthlyValidations: 50,
      coachingMessages: 50,
      videoCalls: 0,
      customTemplates: 10,
      premiumThemes: 5,
      referralBonuses: 10,
      storageGB: 10,
      apiCallsPerDay: 1000
    },
    benefits: [
      {
        type: 'priority',
        name: 'Cola Prioritaria',
        description: 'Validaciones garantizadas en menos de 24h',
        value: '24h_sla',
        isHighlight: true
      },
      {
        type: 'support',
        name: 'Soporte Premium',
        description: 'Respuesta en menos de 12h',
        value: '12h_support',
        isHighlight: true
      },
      {
        type: 'economy',
        name: 'Bonus +25% Currencies',
        description: 'Gana 25% más en todas las monedas',
        value: '1.25x',
        isHighlight: true
      }
    ],
    targetUsers: ['profesionales', 'usuarios_comprometidos', 'estudiantes_serios'],
    upgradeIncentives: [
      {
        trigger: 'feature_interest',
        condition: 'clicked_video_calls',
        message: 'Con Premium obtienes 2 video calls mensuales',
        discount: 25,
        urgency: 'medium',
        validityDays: 10
      },
      {
        trigger: 'behavior_pattern',
        condition: 'high_engagement_7days',
        message: 'Tu dedicación merece herramientas premium',
        discount: 40,
        urgency: 'high',
        validityDays: 5
      }
    ],
    trialDays: 14
  },
  {
    id: 'premium',
    name: 'IMPULSE Premium',
    description: 'La experiencia completa para champions',
    monthlyPrice: 49,
    yearlyPrice: 490, // 2 meses gratis
    yearlyDiscount: 98,
    features: [
      {
        id: 'video_coaching',
        name: 'Video Coaching',
        description: 'Sesiones en vivo con tu coach',
        category: 'coaching',
        isUnlimited: false,
        monthlyQuota: 2,
        qualityLevel: 'luxury'
      },
      {
        id: 'unlimited_messaging',
        name: 'Mensajes Ilimitados',
        description: 'Chat sin límites con tu coach',
        category: 'coaching',
        isUnlimited: true,
        qualityLevel: 'luxury'
      },
      {
        id: 'ai_insights',
        name: 'AI Insights',
        description: 'Análisis IA de patrones y recomendaciones',
        category: 'analytics',
        isUnlimited: true,
        qualityLevel: 'luxury'
      },
      {
        id: 'exclusive_themes',
        name: 'Themes Exclusivos',
        description: 'Colecciones solo para Premium',
        category: 'customization',
        isUnlimited: true,
        qualityLevel: 'luxury'
      },
      {
        id: 'advanced_social',
        name: 'Features Sociales Avanzadas',
        description: 'Leagues privadas, challenges grupales',
        category: 'social',
        isUnlimited: true,
        qualityLevel: 'luxury'
      }
    ],
    limits: {
      activeChallenges: -1,
      monthlyValidations: -1,
      coachingMessages: -1,
      videoCalls: 2,
      customTemplates: -1,
      premiumThemes: -1,
      referralBonuses: -1,
      storageGB: 50,
      apiCallsPerDay: 5000
    },
    benefits: [
      {
        type: 'feature',
        name: 'Video Calls Mensuales',
        description: '2 sesiones de coaching en vivo',
        value: '2_calls',
        isHighlight: true
      },
      {
        type: 'support',
        name: 'Soporte VIP',
        description: 'Línea directa con el equipo',
        value: 'vip_support',
        isHighlight: true
      },
      {
        type: 'economy',
        name: 'Bonus +50% Currencies',
        description: 'Gana 50% más en todas las monedas',
        value: '1.5x',
        isHighlight: true
      },
      {
        type: 'social',
        name: 'Leagues Exclusivas',
        description: 'Acceso a competencias premium',
        value: 'exclusive_leagues',
        isHighlight: true
      }
    ],
    targetUsers: ['executives', 'coaches', 'influencers', 'power_users'],
    upgradeIncentives: [],
    trialDays: 7
  }
]

// UPGRADE PATHS NATURALES
export interface UpgradePath {
  fromTier: string
  toTier: string
  triggers: UpgradeTrigger[]
  messaging: UpgradeMessaging
  incentives: UpgradeOffer[]
}

export interface UpgradeTrigger {
  type: 'usage_limit' | 'feature_demand' | 'time_milestone' | 'social_influence' | 'goal_achievement'
  metric: string
  threshold: number | string
  frequency: 'once' | 'recurring'
  priority: number
}

export interface UpgradeMessaging {
  headline: string
  description: string
  benefits: string[]
  socialProof?: string
  urgency?: string
  visualCues: string[]
}

export interface UpgradeOffer {
  type: 'discount' | 'trial_extension' | 'feature_unlock' | 'bonus_currency'
  value: number | string
  duration: number
  conditions: string[]
  expirationDays: number
}

export const UPGRADE_PATHS: UpgradePath[] = [
  {
    fromTier: 'free',
    toTier: 'pro',
    triggers: [
      {
        type: 'usage_limit',
        metric: 'active_challenges',
        threshold: 3,
        frequency: 'recurring',
        priority: 1
      },
      {
        type: 'feature_demand',
        metric: 'coach_interest_clicks',
        threshold: 3,
        frequency: 'once',
        priority: 2
      },
      {
        type: 'time_milestone',
        metric: 'days_active',
        threshold: 7,
        frequency: 'once',
        priority: 3
      }
    ],
    messaging: {
      headline: '¿Listo para acelerar tu progreso?',
      description: 'Con IMPULSE Pro tienes un coach humano que te acompaña personalizada y directamente',
      benefits: [
        'Coach humano certificado asignado',
        'Validaciones garantizadas en menos de 24h',
        'Templates y themes premium',
        '+25% bonus en todas las monedas'
      ],
      socialProof: 'Más de 1,000 usuarios Pro han alcanzado sus objetivos 3x más rápido',
      urgency: 'Solo esta semana: 30% de descuento',
      visualCues: ['coach_avatar', 'speed_indicator', 'success_checkmarks']
    },
    incentives: [
      {
        type: 'discount',
        value: 30,
        duration: 7,
        conditions: ['first_time_user'],
        expirationDays: 7
      },
      {
        type: 'trial_extension',
        value: 21, // 21 días en lugar de 14
        duration: 1,
        conditions: ['high_engagement'],
        expirationDays: 3
      }
    ]
  },
  {
    fromTier: 'pro',
    toTier: 'premium',
    triggers: [
      {
        type: 'feature_demand',
        metric: 'video_call_requests',
        threshold: 2,
        frequency: 'recurring',
        priority: 1
      },
      {
        type: 'goal_achievement',
        metric: 'challenges_completed',
        threshold: 10,
        frequency: 'once',
        priority: 2
      },
      {
        type: 'social_influence',
        metric: 'friends_premium_count',
        threshold: 2,
        frequency: 'once',
        priority: 3
      }
    ],
    messaging: {
      headline: 'Tu dedicación merece la experiencia completa',
      description: 'Premium te da el nivel de acompañamiento que tu progreso merece',
      benefits: [
        '2 video calls mensuales con tu coach',
        'Mensajes ilimitados con respuesta prioritaria',
        'AI insights personalizado',
        'Acceso a leagues exclusivas'
      ],
      socialProof: 'Los usuarios Premium mantienen 95% de consistencia',
      urgency: 'Actualiza ahora y programa tu primera video call esta semana',
      visualCues: ['video_call_preview', 'ai_brain', 'exclusive_badge']
    },
    incentives: [
      {
        type: 'feature_unlock',
        value: 'free_video_call',
        duration: 30,
        conditions: ['upgrade_this_week'],
        expirationDays: 7
      },
      {
        type: 'bonus_currency',
        value: 500, // IMPULSE Coins de bienvenida
        duration: 1,
        conditions: ['immediate_upgrade'],
        expirationDays: 24
      }
    ]
  }
]

// EXPERIENCIAS DE UPGRADE SUAVES
export interface UpgradeExperience {
  strategy: 'soft_paywall' | 'feature_preview' | 'social_proof' | 'value_demonstration' | 'gentle_nudge'
  scenario: string
  implementation: UpgradeImplementation
  userExperience: UserExperienceFlow
}

export interface UpgradeImplementation {
  displayType: 'modal' | 'banner' | 'inline' | 'toast' | 'fullscreen'
  timing: 'immediate' | 'delayed' | 'contextual' | 'milestone_based'
  frequency: 'once' | 'daily' | 'weekly' | 'on_action'
  dismissible: boolean
  alternatives: string[]
}

export interface UserExperienceFlow {
  entry: string
  demonstration: string[]
  comparison: string
  callToAction: string
  fallback: string
}

export const UPGRADE_EXPERIENCES: UpgradeExperience[] = [
  {
    strategy: 'soft_paywall',
    scenario: 'user_tries_4th_challenge',
    implementation: {
      displayType: 'modal',
      timing: 'immediate',
      frequency: 'once',
      dismissible: true,
      alternatives: ['continue_with_existing', 'complete_one_first']
    },
    userExperience: {
      entry: '¡Wow! Estás súper motivado con 4 retos simultáneos',
      demonstration: [
        'Mostrar preview de dashboard con retos ilimitados',
        'Highlight de prioridad en validaciones',
        'Testimonial de usuario similar'
      ],
      comparison: 'Free: 3 retos | Pro: Ilimitados + Coach',
      callToAction: 'Desbloquea tu potencial por $19/mes',
      fallback: 'O completa uno de tus retos actuales primero'
    }
  },
  {
    strategy: 'feature_preview',
    scenario: 'user_sees_coach_feature',
    implementation: {
      displayType: 'inline',
      timing: 'contextual',
      frequency: 'weekly',
      dismissible: true,
      alternatives: ['try_ai_tips', 'browse_community']
    },
    userExperience: {
      entry: 'Tu progreso es increíble. ¿Imaginas con un coach humano?',
      demonstration: [
        'Mostrar ejemplo de conversación con coach',
        'Highlight de tiempo de respuesta',
        'Preview de plan personalizado'
      ],
      comparison: 'AI Tips vs Coach Humano Certificado',
      callToAction: 'Prueba 14 días gratis',
      fallback: 'Tal vez más adelante'
    }
  },
  {
    strategy: 'value_demonstration',
    scenario: 'user_reaches_streak_milestone',
    implementation: {
      displayType: 'fullscreen',
      timing: 'milestone_based',
      frequency: 'once',
      dismissible: true,
      alternatives: ['continue_free', 'maybe_later']
    },
    userExperience: {
      entry: '¡7 días de racha! Estás en el top 20% de usuarios',
      demonstration: [
        'Mostrar estadísticas de progreso',
        'Comparar con usuarios Pro similares',
        'Proyección de progreso con Pro'
      ],
      comparison: 'Tu progreso actual vs Proyección con Pro',
      callToAction: 'Acelera tu progreso 3x',
      fallback: 'Continuar con Free (siempre puedes cambiar)'
    }
  }
]

// Servicio principal de freemium
export class FreemiumService {
  // --- Remote backend dataset cache (lazy) ---
  private static remoteInitialized = false
  private static remoteTiers: SubscriptionTier[] | null = null
  private static remotePaths: UpgradePath[] | null = null
  private static remoteExperiences: UpgradeExperience[] | null = null
  private static remoteInitAttemptedAt: number | null = null
  private static lastRemoteError: string | null = null

  private static mapRemoteTier(remote: any): SubscriptionTier {
    return {
      id: remote.id,
      name: remote.name,
      description: remote.description,
      monthlyPrice: remote.monthlyPrice,
      yearlyPrice: remote.yearlyPrice,
      yearlyDiscount: remote.yearlyDiscount,
      features: (remote.features || []).map((f: any) => ({
        id: f.id,
        name: f.name,
        description: f.description,
        category: f.category,
        isUnlimited: typeof f.isUnlimited === 'boolean' ? f.isUnlimited : !!f.unlimited,
        monthlyQuota: f.monthlyQuota ?? undefined,
        qualityLevel: f.qualityLevel
      })),
      limits: remote.limits,
      benefits: remote.benefits,
      targetUsers: remote.targetUsers || [],
      upgradeIncentives: remote.upgradeIncentives || [],
      trialDays: remote.trialDays ?? 0
    }
  }

  private static async ensureRemote(): Promise<void> {
    if (this.remoteInitialized) return
    const now = Date.now()
    if (this.remoteInitAttemptedAt && now - this.remoteInitAttemptedAt < 60_000 && this.lastRemoteError) return
    this.remoteInitAttemptedAt = now
    try {
      const [tiers, paths, experiences] = await Promise.all([
        FreemiumApiClient.getTiers(),
        FreemiumApiClient.getUpgradePaths(),
        FreemiumApiClient.getUpgradeExperiences()
      ])
      if (Array.isArray(tiers) && tiers.length > 0 && tiers[0].id) {
        this.remoteTiers = tiers.map(t => this.mapRemoteTier(t))
      }
      if (Array.isArray(paths) && paths.length > 0) this.remotePaths = paths as any
      if (Array.isArray(experiences) && experiences.length > 0) this.remoteExperiences = experiences as any
      this.lastRemoteError = null
      this.remoteInitialized = true
    } catch (e: any) {
      this.lastRemoteError = e?.message || 'remote freemium error'
    }
  }

  static async getTiers(): Promise<SubscriptionTier[]> {
    await this.ensureRemote()
    return this.remoteTiers || SUBSCRIPTION_TIERS
  }

  static async getUpgradePaths(): Promise<UpgradePath[]> {
    await this.ensureRemote()
    return this.remotePaths || UPGRADE_PATHS
  }

  static async getUpgradeExperiences(): Promise<UpgradeExperience[]> {
    await this.ensureRemote()
    return this.remoteExperiences || UPGRADE_EXPERIENCES
  }
  // DETECCIÓN DE UPGRADE OPPORTUNITIES
  static async checkUpgradeOpportunities(userId: string): Promise<UpgradeOffer[]> {
    const userUsage = await this.getUserUsage(userId)
    const userBehavior = await this.getUserBehavior(userId)
    const currentTier = await this.getCurrentTier(userId)

  const applicablePaths = (await this.getUpgradePaths()).filter(path => path.fromTier === currentTier)
    const opportunities: UpgradeOffer[] = []

    for (const path of applicablePaths) {
      for (const trigger of path.triggers) {
        if (await this.evaluateTrigger(trigger, userUsage, userBehavior)) {
          const offers = this.generateOffers(path, trigger)
          opportunities.push(...offers)
        }
      }
    }

    return opportunities.sort((a, b) => this.calculateOfferScore(b) - this.calculateOfferScore(a))
  }

  private static async evaluateTrigger(trigger: UpgradeTrigger, usage: any, behavior: any): Promise<boolean> {
    switch (trigger.type) {
      case 'usage_limit':
        return usage[trigger.metric] >= trigger.threshold
      case 'feature_demand':
        return behavior[trigger.metric] >= trigger.threshold
      case 'time_milestone':
        return behavior[trigger.metric] >= trigger.threshold
      case 'social_influence':
        return behavior[trigger.metric] >= trigger.threshold
      case 'goal_achievement':
        return behavior[trigger.metric] >= trigger.threshold
      default:
        return false
    }
  }

  private static generateOffers(path: UpgradePath, trigger: UpgradeTrigger): UpgradeOffer[] {
    return path.incentives.map(incentive => ({
      ...incentive,
      // Personalize based on trigger
      value: this.personalizeOfferValue(incentive, trigger),
      expirationDays: this.calculateUrgency(trigger)
    }))
  }

  private static personalizeOfferValue(incentive: UpgradeOffer, trigger: UpgradeTrigger): number | string {
    if (trigger.priority === 1 && incentive.type === 'discount') {
      return Math.min((incentive.value as number) + 10, 50) // Boost discount for high priority
    }
    return incentive.value
  }

  private static calculateUrgency(trigger: UpgradeTrigger): number {
    if (trigger.priority === 1) return 3
    if (trigger.priority === 2) return 7
    return 14
  }

  private static calculateOfferScore(offer: UpgradeOffer): number {
    let score = 0

    if (offer.type === 'discount') score += (offer.value as number) / 2
    if (offer.type === 'trial_extension') score += (offer.value as number)
    if (offer.type === 'feature_unlock') score += 30
    if (offer.type === 'bonus_currency') score += 20

    // Urgency bonus
    score += Math.max(0, 14 - offer.expirationDays)

    return score
  }

  // EXPERIENCIAS DE UPGRADE CONTEXTUALES
  static async showUpgradeExperience(userId: string, scenario: string): Promise<UpgradeExperience | null> {
    const relevantExperience = (await this.getUpgradeExperiences()).find(exp => exp.scenario === scenario)
    if (!relevantExperience) return null

    const hasShownRecently = await this.hasShownExperience(userId, scenario)
    if (hasShownRecently && relevantExperience.implementation.frequency === 'once') {
      return null
    }

    await this.trackExperienceShown(userId, scenario)
    return relevantExperience
  }

  // TRIAL MANAGEMENT
  static async startTrial(userId: string, targetTier: string): Promise<boolean> {
    const currentTier = await this.getCurrentTier(userId)
    const tierConfig = (await this.getTiers()).find(t => t.id === targetTier)

    if (!tierConfig || currentTier === targetTier) return false

    const trialDuration = tierConfig.trialDays
    if (trialDuration === 0) return false

    const hasUsedTrial = await this.hasUsedTrial(userId, targetTier)
    if (hasUsedTrial) return false

    await this.grantTrialAccess(userId, targetTier, trialDuration)
    await this.scheduleTrialReminders(userId, targetTier, trialDuration)

    // Activar features del tier inmediatamente
    await this.activateTierFeatures(userId, targetTier)

    return true
  }

  static async extendTrial(userId: string, additionalDays: number, reason: string): Promise<boolean> {
    const trialInfo = await this.getTrialInfo(userId)
    if (!trialInfo?.isActive) return false

    await this.updateTrialDuration(userId, additionalDays)
    await this.logTrialExtension(userId, additionalDays, reason)

    return true
  }

  // CONVERSION OPTIMIZATION
  static async optimizeConversion(userId: string): Promise<void> {
    const userProfile = await this.getUserProfile(userId)
    const usagePatterns = await this.getUserUsage(userId)
    const conversionScore = this.calculateConversionScore(userProfile, usagePatterns)

    if (conversionScore > 0.7) {
      await this.activateHighIntentFlow(userId)
    } else if (conversionScore > 0.4) {
      await this.activateNurtureFlow(userId)
    } else {
      await this.activateEngagementFlow(userId)
    }
  }

  private static calculateConversionScore(_profile: any, usage: any): number {
    let score = 0

    // Engagement indicators
    if (usage.daysActive >= 7) score += 0.2
    if (usage.featuresUsed >= 5) score += 0.2
    if (usage.socialInteractions >= 3) score += 0.1

    // Intent indicators
    if (usage.premiumFeatureClicks >= 3) score += 0.3
    if (usage.upgradePageViews >= 2) score += 0.2

    return Math.min(score, 1.0)
  }

  private static async activateHighIntentFlow(userId: string): Promise<void> {
    await this.showPersonalizedOffer(userId, 'high_intent')
    await this.enableConciergeSupport(userId)
  }

  private static async activateNurtureFlow(userId: string): Promise<void> {
    await this.scheduleValueDemonstrations(userId)
    await this.enableSocialProofCampaign(userId)
  }

  private static async activateEngagementFlow(userId: string): Promise<void> {
    await this.suggestHighValueFeatures(userId)
    await this.enableGamificationBoosts(userId)
  }

  // RETENCIÓN POST-UPGRADE
  static async optimizeRetention(userId: string): Promise<void> {
    const currentTier = await this.getCurrentTier(userId)
    const usageData = await this.getTierFeatureUsage(userId)

  const underutilizedFeatures = await this.identifyUnderutilizedFeatures(currentTier, usageData)

    if (underutilizedFeatures.length > 0) {
      await this.createFeatureAdoptionPlan(userId, underutilizedFeatures)
    }

    await this.scheduleSuccessCheck(userId)
  }

  private static async identifyUnderutilizedFeatures(tier: string, usage: any): Promise<string[]> {
    const tierConfig = (await this.getTiers()).find(t => t.id === tier)
    if (!tierConfig) return []
    return tierConfig.features
      .filter(feature => usage[feature.id] < (feature.monthlyQuota || 1) * 0.3)
      .map(feature => feature.id)
  }

  // Métodos auxiliares mock
  private static async getUserUsage(_userId: string): Promise<any> {
    return {
      active_challenges: 2,
      coach_interest_clicks: 1,
      days_active: 5,
      featuresUsed: 4,
      socialInteractions: 2,
      premiumFeatureClicks: 1,
      upgradePageViews: 0
    }
  }

  private static async getUserBehavior(_userId: string): Promise<any> {
    return {
      coach_interest_clicks: 1,
      days_active: 5,
      video_call_requests: 0,
      challenges_completed: 2,
      friends_premium_count: 0
    }
  }

  // Public accessor for the current tier. Some services need to read the current tier.
  static async getCurrentTier(_userId: string): Promise<string> {
    return 'free'
  }

  private static async hasShownExperience(_userId: string, _scenario: string): Promise<boolean> {
    return false
  }

  private static async trackExperienceShown(_userId: string, _scenario: string): Promise<void> {
    // Track experience shown
  }

  private static async hasUsedTrial(_userId: string, _tier: string): Promise<boolean> {
    return false
  }

  private static async grantTrialAccess(_userId: string, _tier: string, _days: number): Promise<void> {
    // Grant trial access
  }

  private static async scheduleTrialReminders(_userId: string, _tier: string, _duration: number): Promise<void> {
    // Schedule trial reminders
  }

  private static async activateTierFeatures(_userId: string, _tier: string): Promise<void> {
    // Activate tier features
  }

  private static async getTrialInfo(_userId: string): Promise<any> {
    return null
  }

  private static async updateTrialDuration(_userId: string, _days: number): Promise<void> {
    // Update trial duration
  }

  private static async logTrialExtension(_userId: string, _days: number, _reason: string): Promise<void> {
    // Log trial extension
  }

  private static async getUserProfile(_userId: string): Promise<any> {
    return {}
  }

  private static async showPersonalizedOffer(_userId: string, _type: string): Promise<void> {
    // Show personalized offer
  }

  private static async enableConciergeSupport(_userId: string): Promise<void> {
    // Enable concierge support
  }

  private static async scheduleValueDemonstrations(_userId: string): Promise<void> {
    // Schedule value demonstrations
  }

  private static async enableSocialProofCampaign(_userId: string): Promise<void> {
    // Enable social proof campaign
  }

  private static async suggestHighValueFeatures(_userId: string): Promise<void> {
    // Suggest high value features
  }

  private static async enableGamificationBoosts(_userId: string): Promise<void> {
    // Enable gamification boosts
  }

  private static async getTierFeatureUsage(_userId: string): Promise<any> {
    return {}
  }

  private static async createFeatureAdoptionPlan(_userId: string, _features: string[]): Promise<void> {
    // Create feature adoption plan
  }

  private static async scheduleSuccessCheck(_userId: string): Promise<void> {
    // Schedule success check
  }
}

export default FreemiumService
