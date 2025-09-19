// Sistema de Marketing Viral y Growth - Invitations, social sharing, achievements públicos, PR automático
import { ReferralService } from './ReferralService'

// CAMPAÑAS DE GROWTH
export interface GrowthCampaign {
  id: string
  name: string
  description: string
  type: 'viral_loop' | 'social_proof' | 'content_marketing' | 'influencer' | 'pr_automation' | 'user_generated'
  status: 'draft' | 'active' | 'paused' | 'completed' | 'archived'
  startDate: Date
  endDate?: Date
  targets: GrowthTarget[]
  channels: MarketingChannel[]
  content: CampaignContent[]
  triggers: CampaignTrigger[]
  metrics: CampaignMetrics
  budget?: CampaignBudget
}

export interface GrowthTarget {
  metric: 'new_signups' | 'viral_coefficient' | 'retention_rate' | 'share_rate' | 'conversion_rate'
  currentValue: number
  targetValue: number
  deadline: Date
  priority: 'low' | 'medium' | 'high' | 'critical'
}

export interface MarketingChannel {
  platform: 'instagram' | 'tiktok' | 'youtube' | 'twitter' | 'linkedin' | 'whatsapp' | 'telegram' | 'discord' | 'reddit'
  isActive: boolean
  automationLevel: 'manual' | 'semi_auto' | 'full_auto'
  contentTypes: string[]
  posting_schedule: PostingSchedule
  engagement_strategy: EngagementStrategy
}

export interface PostingSchedule {
  frequency: 'daily' | 'weekly' | 'bi_weekly' | 'monthly' | 'event_based'
  timeSlots: string[] // '09:00', '14:00', '19:00'
  timezone: string
  weekdays: number[] // 0-6
}

export interface EngagementStrategy {
  auto_reply: boolean
  community_management: boolean
  hashtag_strategy: string[]
  mention_strategy: boolean
  collaboration_outreach: boolean
}

export interface CampaignContent {
  id: string
  type: 'image' | 'video' | 'story' | 'carousel' | 'text' | 'testimonial' | 'achievement' | 'ugc'
  title: string
  description: string
  assets: ContentAsset[]
  copy: ContentCopy
  targeting: ContentTargeting
  performance: ContentPerformance
}

export interface ContentAsset {
  type: 'image' | 'video' | 'gif' | 'audio'
  url: string
  alt_text?: string
  duration?: number
  resolution?: string
  file_size?: number
}

export interface ContentCopy {
  headline?: string
  body: string
  call_to_action: string
  hashtags: string[]
  mentions: string[]
  personalization_tokens: string[]
}

export interface ContentTargeting {
  demographics: string[]
  interests: string[]
  behaviors: string[]
  custom_audiences: string[]
  lookalike_audiences: string[]
}

export interface ContentPerformance {
  impressions: number
  clicks: number
  shares: number
  conversions: number
  engagement_rate: number
  ctr: number
  conversion_rate: number
  cost_per_acquisition?: number
}

export interface CampaignTrigger {
  type: 'user_action' | 'milestone' | 'time_based' | 'external_event' | 'performance_based'
  condition: string
  threshold?: number
  action: TriggerAction
  frequency: 'once' | 'recurring'
}

export interface TriggerAction {
  type: 'send_content' | 'create_post' | 'notify_team' | 'adjust_targeting' | 'scale_budget'
  parameters: any
  delay?: number // minutes
}

export interface CampaignMetrics {
  reach: number
  impressions: number
  engagement: number
  clicks: number
  shares: number
  conversions: number
  cost: number
  roi: number
  viral_coefficient: number
  k_factor: number
}

export interface CampaignBudget {
  total_budget: number
  daily_budget: number
  cost_per_click: number
  cost_per_acquisition: number
  remaining_budget: number
  spend_pace: 'normal' | 'accelerated' | 'conservative'
}

// ACHIEVEMENTS PÚBLICOS
export interface PublicAchievement {
  id: string
  userId: string
  achievementType: 'milestone' | 'streak' | 'challenge_completion' | 'community_contribution' | 'viral_growth'
  title: string
  description: string
  category: string
  difficulty: 'easy' | 'medium' | 'hard' | 'legendary'
  rarity: number // 0-1, where 0 is very rare
  achievedDate: Date
  evidenceUrl?: string
  socialSharing: SocialSharingConfig
  publicVisibility: PublicVisibilityConfig
  virality: ViralityMetrics
}

export interface SocialSharingConfig {
  auto_generate_content: boolean
  platforms: string[]
  share_templates: ShareTemplate[]
  include_evidence: boolean
  include_stats: boolean
  custom_message?: string
}

export interface ShareTemplate {
  platform: string
  template: string
  image_template?: string
  video_template?: string
  hashtags: string[]
  mentions: string[]
}

export interface PublicVisibilityConfig {
  show_on_profile: boolean
  show_in_leaderboards: boolean
  show_in_community: boolean
  allow_external_sharing: boolean
  seo_indexable: boolean
}

export interface ViralityMetrics {
  total_shares: number
  unique_viewers: number
  conversion_rate: number
  referrals_generated: number
  engagement_score: number
  viral_reach: number
}

// PR AUTOMÁTICO
export interface PRCampaign {
  id: string
  type: 'user_milestone' | 'company_milestone' | 'product_launch' | 'partnership' | 'community_story' | 'impact_story'
  trigger: PRTrigger
  story: PRStory
  distribution: PRDistribution
  status: 'generated' | 'approved' | 'distributed' | 'published' | 'tracked'
  metrics: PRMetrics
}

export interface PRTrigger {
  condition: string
  threshold?: number
  frequency: 'immediate' | 'daily' | 'weekly' | 'monthly'
  auto_approve: boolean
  requires_review: boolean
}

export interface PRStory {
  headline: string
  summary: string
  full_story: string
  quotes: PRQuote[]
  facts_figures: string[]
  images: string[]
  videos?: string[]
  press_kit_url?: string
}

export interface PRQuote {
  person: string
  title: string
  company?: string
  quote: string
  context: string
}

export interface PRDistribution {
  media_list: MediaContact[]
  influencer_list: InfluencerContact[]
  platforms: string[]
  timing: DistributionTiming
  follow_up_strategy: FollowUpStrategy
}

export interface MediaContact {
  name: string
  outlet: string
  email: string
  beat: string
  relationship_score: number
  last_contact: Date
  response_rate: number
}

export interface InfluencerContact {
  name: string
  platform: string
  handle: string
  followers: number
  engagement_rate: number
  category: string
  collaboration_history: string[]
}

export interface DistributionTiming {
  send_time: Date
  optimal_days: number[]
  time_zone: string
  embargo?: Date
}

export interface FollowUpStrategy {
  auto_follow_up: boolean
  follow_up_intervals: number[] // days
  personalized_messages: boolean
  escalation_rules: string[]
}

export interface PRMetrics {
  emails_sent: number
  open_rate: number
  response_rate: number
  pickups: number
  reach: number
  sentiment_score: number
  mention_value: number
}

// CAMPAÑAS PREDEFINIDAS
export const VIRAL_CAMPAIGNS: GrowthCampaign[] = [
  {
    id: 'achievement_sharing_campaign',
    name: 'Achievement Viral Loop',
    description: 'Usuarios comparten logros automáticamente generando curiosidad e invitaciones',
    type: 'viral_loop',
    status: 'active',
    startDate: new Date('2025-09-01'),
    targets: [
      {
        metric: 'viral_coefficient',
        currentValue: 0.3,
        targetValue: 0.8,
        deadline: new Date('2025-12-31'),
        priority: 'high'
      },
      {
        metric: 'share_rate',
        currentValue: 0.15,
        targetValue: 0.40,
        deadline: new Date('2025-12-31'),
        priority: 'medium'
      }
    ],
    channels: [
      {
        platform: 'instagram',
        isActive: true,
        automationLevel: 'semi_auto',
        contentTypes: ['story', 'post', 'carousel'],
        posting_schedule: {
          frequency: 'event_based',
          timeSlots: ['09:00', '14:00', '19:00'],
          timezone: 'America/Mexico_City',
          weekdays: [0, 1, 2, 3, 4, 5, 6]
        },
        engagement_strategy: {
          auto_reply: true,
          community_management: true,
          hashtag_strategy: ['#IMPULSEApp', '#Goals', '#Motivation', '#Achievement'],
          mention_strategy: true,
          collaboration_outreach: false
        }
      },
      {
        platform: 'tiktok',
        isActive: true,
        automationLevel: 'semi_auto',
        contentTypes: ['video'],
        posting_schedule: {
          frequency: 'event_based',
          timeSlots: ['18:00', '20:00'],
          timezone: 'America/Mexico_City',
          weekdays: [0, 1, 2, 3, 4, 5, 6]
        },
        engagement_strategy: {
          auto_reply: false,
          community_management: true,
          hashtag_strategy: ['#IMPULSEChallenge', '#MotivationTok', '#GoalGetter'],
          mention_strategy: false,
          collaboration_outreach: true
        }
      }
    ],
    content: [
      {
        id: 'achievement_story_template',
        type: 'story',
        title: 'Achievement Story',
        description: 'Template para compartir logros en stories',
        assets: [
          {
            type: 'image',
            url: '/templates/achievement_story.png',
            alt_text: 'Achievement unlocked template'
          }
        ],
        copy: {
          body: '🎉 ¡Logro desbloqueado en IMPULSE! {achievement_name}\n\n💪 {progress_description}\n\n¿Tú también tienes metas? ¡Únete!',
          call_to_action: 'Descarga IMPULSE gratis',
          hashtags: ['#IMPULSEApp', '#Achievement', '#Goals'],
          mentions: ['@impulse.app'],
          personalization_tokens: ['achievement_name', 'progress_description', 'user_name']
        },
        targeting: {
          demographics: ['18-35', 'spanish_speakers'],
          interests: ['fitness', 'productivity', 'goals', 'motivation'],
          behaviors: ['app_users', 'goal_setters'],
          custom_audiences: ['friends_of_users'],
          lookalike_audiences: ['high_value_users']
        },
        performance: {
          impressions: 0,
          clicks: 0,
          shares: 0,
          conversions: 0,
          engagement_rate: 0,
          ctr: 0,
          conversion_rate: 0
        }
      }
    ],
    triggers: [
      {
        type: 'user_action',
        condition: 'achievement_unlocked',
        action: {
          type: 'send_content',
          parameters: {
            content_id: 'achievement_story_template',
            personalize: true,
            auto_post: false // Usuario decide si compartir
          },
          delay: 5 // 5 minutos después del logro
        },
        frequency: 'recurring'
      },
      {
        type: 'milestone',
        condition: 'user_streak_7_days',
        action: {
          type: 'create_post',
          parameters: {
            type: 'celebration_post',
            include_stats: true,
            include_invitation: true
          }
        },
        frequency: 'once'
      }
    ],
    metrics: {
      reach: 0,
      impressions: 0,
      engagement: 0,
      clicks: 0,
      shares: 0,
      conversions: 0,
      cost: 0,
      roi: 0,
      viral_coefficient: 0,
      k_factor: 0
    }
  }
]

export const PUBLIC_ACHIEVEMENT_TEMPLATES: PublicAchievement[] = [
  {
    id: 'first_30_day_challenge',
    userId: 'template',
    achievementType: 'challenge_completion',
    title: 'Primer Maratón de 30 Días',
    description: 'Completó su primer reto de 30 días consecutivos',
    category: 'consistency',
    difficulty: 'medium',
    rarity: 0.25, // 25% de usuarios lo logran
    achievedDate: new Date(),
    socialSharing: {
      auto_generate_content: true,
      platforms: ['instagram', 'twitter', 'whatsapp'],
      share_templates: [
        {
          platform: 'instagram',
          template: '💪 ¡30 días de disciplina completados con IMPULSE!\n\n🎯 Mi reto: {challenge_name}\n📈 Mi progreso: {progress_stats}\n✨ Lo que aprendí: La consistencia transforma\n\n¿Listos para su próximo reto? 👇',
          hashtags: ['#IMPULSE30Days', '#Discipline', '#Goals', '#Transformation'],
          mentions: ['@impulse.app']
        },
        {
          platform: 'whatsapp',
          template: '🎉 ¡Completé mi primer reto de 30 días con IMPULSE! {challenge_name}\n\nSi yo pude, tú también puedes. ¿Te animas a intentarlo?\n\n{referral_link}',
          hashtags: [],
          mentions: []
        }
      ],
      include_evidence: true,
      include_stats: true
    },
    publicVisibility: {
      show_on_profile: true,
      show_in_leaderboards: true,
      show_in_community: true,
      allow_external_sharing: true,
      seo_indexable: true
    },
    virality: {
      total_shares: 0,
      unique_viewers: 0,
      conversion_rate: 0,
      referrals_generated: 0,
      engagement_score: 0,
      viral_reach: 0
    }
  }
]

// Servicio principal de marketing viral
export class ViralMarketingService {
  // GESTIÓN DE CAMPAÑAS
  static async createViralCampaign(config: Partial<GrowthCampaign>): Promise<GrowthCampaign> {
    const campaign: GrowthCampaign = {
      id: this.generateCampaignId(),
      name: config.name || 'Nueva Campaña',
      description: config.description || '',
      type: config.type || 'viral_loop',
      status: 'draft',
      startDate: config.startDate || new Date(),
      endDate: config.endDate,
      targets: config.targets || [],
      channels: config.channels || [],
      content: config.content || [],
      triggers: config.triggers || [],
      metrics: {
        reach: 0,
        impressions: 0,
        engagement: 0,
        clicks: 0,
        shares: 0,
        conversions: 0,
        cost: 0,
        roi: 0,
        viral_coefficient: 0,
        k_factor: 0
      },
      budget: config.budget
    }

    await this.saveCampaign(campaign)
    return campaign
  }

  static async activateCampaign(campaignId: string): Promise<boolean> {
    const campaign = await this.getCampaign(campaignId)
    if (!campaign) return false

    campaign.status = 'active'

    // Programar triggers automáticos
    for (const trigger of campaign.triggers) {
      await this.scheduleTrigger(campaignId, trigger)
    }

    // Activar canales
    for (const channel of campaign.channels) {
      if (channel.isActive) {
        await this.activateChannel(campaignId, channel)
      }
    }

    await this.updateCampaign(campaign)
    return true
  }

  // CONTENT GENERATION AUTOMÁTICO
  static async generateViralContent(userId: string, trigger: string, context: any): Promise<CampaignContent | null> {
    const campaign = await this.getActiveCampaignByTrigger(trigger)
    if (!campaign) return null

    const contentTemplate = campaign.content.find(c => c.type === context.contentType)
    if (!contentTemplate) return null

    const personalizedContent = await this.personalizeContent(contentTemplate, userId, context)

    // Track content generation
    await this.trackContentGeneration(userId, personalizedContent.id, trigger)

    return personalizedContent
  }

  private static async personalizeContent(template: CampaignContent, userId: string, context: any): Promise<CampaignContent> {
    const userProfile = await this.getUserProfile(userId)
    const personalizedCopy = { ...template.copy }

    // Personalizar texto
    personalizedCopy.body = this.replaceTokens(template.copy.body, {
      user_name: userProfile.name,
      achievement_name: context.achievementName,
      progress_description: context.progressDescription,
      challenge_name: context.challengeName,
      referral_link: await ReferralService.generateReferralLink(userId)
    })

    return {
      ...template,
      copy: personalizedCopy
    }
  }

  private static replaceTokens(text: string, tokens: any): string {
    let result = text
    for (const [key, value] of Object.entries(tokens)) {
      result = result.replace(new RegExp(`{${key}}`, 'g'), String(value))
    }
    return result
  }

  // ACHIEVEMENTS PÚBLICOS
  static async createPublicAchievement(userId: string, achievementData: any): Promise<PublicAchievement> {
    const achievement: PublicAchievement = {
      id: this.generateAchievementId(),
      userId,
      achievementType: achievementData.type,
      title: achievementData.title,
      description: achievementData.description,
      category: achievementData.category,
      difficulty: achievementData.difficulty,
      rarity: await this.calculateRarity(achievementData.type),
      achievedDate: new Date(),
      evidenceUrl: achievementData.evidenceUrl,
      socialSharing: {
        auto_generate_content: true,
        platforms: ['instagram', 'twitter', 'whatsapp'],
        share_templates: await this.getShareTemplates(achievementData.type),
        include_evidence: true,
        include_stats: true
      },
      publicVisibility: {
        show_on_profile: true,
        show_in_leaderboards: true,
        show_in_community: true,
        allow_external_sharing: true,
        seo_indexable: true
      },
      virality: {
        total_shares: 0,
        unique_viewers: 0,
        conversion_rate: 0,
        referrals_generated: 0,
        engagement_score: 0,
        viral_reach: 0
      }
    }

    await this.savePublicAchievement(achievement)

    // Trigger viral content generation
    await this.triggerViralSharing(achievement)

    return achievement
  }

  private static async triggerViralSharing(achievement: PublicAchievement): Promise<void> {
    if (!achievement.socialSharing.auto_generate_content) return

    for (const template of achievement.socialSharing.share_templates) {
      const shareContent = await this.generateShareableContent(achievement, template)
      await this.presentSharingOption(achievement.userId, shareContent)
    }
  }

  private static async generateShareableContent(achievement: PublicAchievement, template: ShareTemplate): Promise<any> {
    const userProfile = await this.getUserProfile(achievement.userId)

    const content = this.replaceTokens(template.template, {
      user_name: userProfile.name,
      achievement_title: achievement.title,
      achievement_description: achievement.description,
      referral_link: await ReferralService.generateReferralLink(achievement.userId)
    })

    return {
      platform: template.platform,
      text: content,
      hashtags: template.hashtags,
      mentions: template.mentions,
      achievementId: achievement.id
    }
  }

  // PR AUTOMÁTICO
  static async generatePRCampaign(trigger: string, data: any): Promise<PRCampaign> {
    const prCampaign: PRCampaign = {
      id: this.generatePRId(),
      type: trigger as any,
      trigger: {
        condition: trigger,
        frequency: 'immediate',
        auto_approve: false,
        requires_review: true
      },
      story: await this.generatePRStory(trigger, data),
      distribution: await this.getPRDistributionList(trigger),
      status: 'generated',
      metrics: {
        emails_sent: 0,
        open_rate: 0,
        response_rate: 0,
        pickups: 0,
        reach: 0,
        sentiment_score: 0,
        mention_value: 0
      }
    }

    await this.savePRCampaign(prCampaign)

    if (prCampaign.trigger.auto_approve) {
      await this.distributePRCampaign(prCampaign.id)
    } else {
      await this.queueForReview(prCampaign.id)
    }

    return prCampaign
  }

  private static async generatePRStory(trigger: string, data: any): Promise<PRStory> {
    const templates = await this.getPRTemplates(trigger)
    const template = templates[0] // Use first template for now

    return {
      headline: this.replaceTokens(template.headline, data),
      summary: this.replaceTokens(template.summary, data),
      full_story: this.replaceTokens(template.full_story, data),
      quotes: template.quotes.map((q: any) => ({
        ...q,
        quote: this.replaceTokens(q.quote, data)
      })),
      facts_figures: template.facts_figures.map((f: string) => this.replaceTokens(f, data)),
      images: data.images || [],
      videos: data.videos || []
    }
  }

  // MÉTRICAS Y OPTIMIZACIÓN
  static async updateCampaignMetrics(campaignId: string, metrics: Partial<CampaignMetrics>): Promise<void> {
    const campaign = await this.getCampaign(campaignId)
    if (!campaign) return

    campaign.metrics = { ...campaign.metrics, ...metrics }

    // Calcular viral coefficient
    if (metrics.conversions && metrics.shares) {
      campaign.metrics.viral_coefficient = metrics.conversions / metrics.shares
    }

    // Calcular K-factor
      if (metrics.conversions && campaign.metrics.impressions) {
        campaign.metrics.k_factor = (metrics.conversions / campaign.metrics.impressions) *
                                    (campaign.metrics.shares / Math.max(1, metrics.conversions))
      }

    await this.updateCampaign(campaign)

    // Trigger optimizations if needed
    await this.optimizeCampaign(campaign)
  }

  private static async optimizeCampaign(campaign: GrowthCampaign): Promise<void> {
    // Auto-optimization based on performance
    if (campaign.metrics.viral_coefficient < 0.5 && campaign.metrics.impressions > 1000) {
      await this.suggestContentImprovement(campaign.id)
    }

      // Use conversions-derived rate check (conversions / impressions) to avoid referencing a possibly
      // non-existent 'conversion_rate' property on CampaignMetrics.
      const conversions = campaign.metrics.conversions ?? 0
      const impressions = campaign.metrics.impressions ?? 1
      const conversionRate = conversions / impressions
      if (conversionRate < 0.02) {
        await this.adjustTargeting(campaign.id)
      }
  }

  static async getViralMetrics(timeframe: string): Promise<any> {
    return {
      total_campaigns: await this.getActiveCampaignsCount(),
      average_viral_coefficient: await this.getAverageViralCoefficient(timeframe),
      top_performing_content: await this.getTopPerformingContent(timeframe),
      growth_rate: await this.getGrowthRate(timeframe),
      acquisition_channels: await this.getAcquisitionChannels(timeframe)
    }
  }

  // Métodos auxiliares mock
  private static generateCampaignId(): string {
    return 'camp_' + Date.now() + '_' + Math.random().toString(36).slice(2, 11)
  }

  private static generateAchievementId(): string {
    return 'ach_' + Date.now() + '_' + Math.random().toString(36).slice(2, 11)
  }

  private static generatePRId(): string {
    return 'pr_' + Date.now() + '_' + Math.random().toString(36).slice(2, 11)
  }

  private static async saveCampaign(_campaign: GrowthCampaign): Promise<void> {
    // Save campaign
  }

  private static async getCampaign(_id: string): Promise<GrowthCampaign | null> {
    return null
  }

  private static async updateCampaign(_campaign: GrowthCampaign): Promise<void> {
    // Update campaign
  }

  private static async scheduleTrigger(_campaignId: string, _trigger: CampaignTrigger): Promise<void> {
    // Schedule trigger
  }

  private static async activateChannel(_campaignId: string, _channel: MarketingChannel): Promise<void> {
    // Activate channel
  }

  private static async getActiveCampaignByTrigger(_trigger: string): Promise<GrowthCampaign | null> {
    return null
  }

  private static async trackContentGeneration(_userId: string, _contentId: string, _trigger: string): Promise<void> {
    // Track content generation
  }

  private static async getUserProfile(_userId: string): Promise<any> {
    return { name: 'Usuario' }
  }

  private static async calculateRarity(_type: string): Promise<number> {
    return 0.5 // 50% default rarity
  }

  private static async getShareTemplates(_type: string): Promise<ShareTemplate[]> {
    return []
  }

  private static async savePublicAchievement(_achievement: PublicAchievement): Promise<void> {
    // Save achievement
  }

  private static async presentSharingOption(_userId: string, _content: any): Promise<void> {
    // Present sharing option to user
  }

  private static async getPRTemplates(_trigger: string): Promise<any[]> {
    return []
  }

  private static async getPRDistributionList(_trigger: string): Promise<PRDistribution> {
    return {
      media_list: [],
      influencer_list: [],
      platforms: [],
      timing: {
        send_time: new Date(),
        optimal_days: [1, 2, 3],
        time_zone: 'America/Mexico_City'
      },
      follow_up_strategy: {
        auto_follow_up: false,
        follow_up_intervals: [3, 7, 14],
        personalized_messages: true,
        escalation_rules: []
      }
    }
  }

  private static async savePRCampaign(_campaign: PRCampaign): Promise<void> {
    // Save PR campaign
  }

  private static async distributePRCampaign(_id: string): Promise<void> {
    // Distribute PR campaign
  }

  private static async queueForReview(_id: string): Promise<void> {
    // Queue for review
  }

  private static async suggestContentImprovement(_campaignId: string): Promise<void> {
    // Suggest improvements
  }

  private static async adjustTargeting(_campaignId: string): Promise<void> {
    // Adjust targeting
  }

  private static async getActiveCampaignsCount(): Promise<number> {
    return 0
  }

  private static async getAverageViralCoefficient(_timeframe: string): Promise<number> {
    return 0
  }

  private static async getTopPerformingContent(_timeframe: string): Promise<any[]> {
    return []
  }

  private static async getGrowthRate(_timeframe: string): Promise<number> {
    return 0
  }

  private static async getAcquisitionChannels(_timeframe: string): Promise<any[]> {
    return []
  }
}

export default ViralMarketingService
