// Sistema de Acompañamiento Coaching - Coaches humanos, mensajes premium, video calls
import { EconomyService } from './EconomyService'

// NIVELES DE COACHING
export interface CoachingTier {
  tier: 'free' | 'pro' | 'premium' | 'vip'
  name: string
  description: string
  monthlyPrice: number
  features: CoachingFeature[]
  responseTimeHours: number
  monthlyInteractions: number
  includesVideoCalls: boolean
  personalizedPlan: boolean
  prioritySupport: boolean
}

export interface CoachingFeature {
  id: string
  name: string
  description: string
  type: 'messaging' | 'planning' | 'calls' | 'analysis' | 'priority'
  unlimitedUsage: boolean
  monthlyQuota?: number
}

export const COACHING_TIERS: CoachingTier[] = [
  {
    tier: 'free',
    name: 'Apoyo Básico',
    description: 'Mensajes automáticos motivacionales',
    monthlyPrice: 0,
    features: [
      {
        id: 'auto_messages',
        name: 'Mensajes Automáticos',
        description: 'Tips motivacionales generados por IA',
        type: 'messaging',
        unlimitedUsage: true
      },
      {
        id: 'basic_templates',
        name: 'Templates Básicos',
        description: 'Plantillas de retos predefinidas',
        type: 'planning',
        unlimitedUsage: true
      }
    ],
    responseTimeHours: 48,
    monthlyInteractions: 10,
    includesVideoCalls: false,
    personalizedPlan: false,
    prioritySupport: false
  },
  {
    tier: 'pro',
    name: 'Coach Humano',
    description: 'Acompañamiento humano personalizado',
    monthlyPrice: 29,
    features: [
      {
        id: 'human_coach',
        name: 'Coach Humano Asignado',
        description: 'Coach certificado dedicado',
        type: 'messaging',
        unlimitedUsage: false,
        monthlyQuota: 50
      },
      {
        id: 'custom_plans',
        name: 'Planes Personalizados',
        description: 'Roadmaps adaptados a tu perfil',
        type: 'planning',
        unlimitedUsage: false,
        monthlyQuota: 2
      },
      {
        id: 'priority_response',
        name: 'Respuesta Prioritaria',
        description: 'Respuestas en menos de 24h',
        type: 'priority',
        unlimitedUsage: true
      }
    ],
    responseTimeHours: 24,
    monthlyInteractions: 50,
    includesVideoCalls: false,
    personalizedPlan: true,
    prioritySupport: true
  },
  {
    tier: 'premium',
    name: 'Coach Premium',
    description: 'Coaching intensivo con video calls',
    monthlyPrice: 79,
    features: [
      {
        id: 'video_calls',
        name: 'Video Calls Mensuales',
        description: 'Sesiones de coaching en vivo',
        type: 'calls',
        unlimitedUsage: false,
        monthlyQuota: 2
      },
      {
        id: 'unlimited_messaging',
        name: 'Mensajes Ilimitados',
        description: 'Comunicación sin límites',
        type: 'messaging',
        unlimitedUsage: true
      },
      {
        id: 'progress_analysis',
        name: 'Análisis de Progreso',
        description: 'Reportes detallados semanales',
        type: 'analysis',
        unlimitedUsage: false,
        monthlyQuota: 4
      }
    ],
    responseTimeHours: 12,
    monthlyInteractions: -1, // unlimited
    includesVideoCalls: true,
    personalizedPlan: true,
    prioritySupport: true
  },
  {
    tier: 'vip',
    name: 'Coach VIP',
    description: 'Coaching ejecutivo 24/7',
    monthlyPrice: 199,
    features: [
      {
        id: 'executive_coach',
        name: 'Coach Ejecutivo',
        description: 'Coach senior especializado',
        type: 'messaging',
        unlimitedUsage: true
      },
      {
        id: 'weekly_calls',
        name: 'Calls Semanales',
        description: 'Sesiones semanales garantizadas',
        type: 'calls',
        unlimitedUsage: false,
        monthlyQuota: 4
      },
      {
        id: 'emergency_support',
        name: 'Soporte Emergencia',
        description: 'Acceso 24/7 para crisis',
        type: 'priority',
        unlimitedUsage: true
      }
    ],
    responseTimeHours: 2,
    monthlyInteractions: -1, // unlimited
    includesVideoCalls: true,
    personalizedPlan: true,
    prioritySupport: true
  }
]

// PERFIL DE COACH
export interface CoachProfile {
  id: string
  name: string
  photo: string
  specializations: string[]
  languages: string[]
  timezone: string
  experience: {
    yearsExperience: number
    certificationsCount: number
    totalStudents: number
    averageRating: number
    successRate: number
  }
  availability: {
    isActive: boolean
    maxStudentsPerTier: {
      pro: number
      premium: number
      vip: number
    }
    currentStudentsCount: {
      pro: number
      premium: number
      vip: number
    }
    preferredHours: number[]
    responseTimeGoal: number
  }
  coachingStyle: {
    approach: 'supportive' | 'challenging' | 'analytical' | 'holistic'
    focusAreas: string[]
    methodology: string
    communicationStyle: string
  }
  pricing: {
    tierMultiplier: number // 1.0 = precio base, 1.2 = 20% más caro
    specialtyFees: { [specialty: string]: number }
  }
  bio: string
  testimonials: CoachTestimonial[]
}

export interface CoachTestimonial {
  studentId: string
  studentName: string
  rating: number
  comment: string
  date: Date
  challengeType: string
}

// SESIÓN DE COACHING
export interface CoachingSession {
  id: string
  studentId: string
  coachId: string
  type: 'message' | 'video_call' | 'progress_review' | 'planning'
  scheduledDate?: Date
  startTime?: Date
  endTime?: Date
  duration?: number
  topic: string
  description?: string
  studentGoals: string[]
  coachNotes: string
  actionItems: ActionItem[]
  followUpScheduled?: Date
  satisfactionRating?: number
  status: 'scheduled' | 'in_progress' | 'completed' | 'cancelled' | 'no_show'
  recordingUrl?: string
  sessionSummary?: string
  nextSteps?: string[]
}

export interface ActionItem {
  id: string
  description: string
  dueDate: Date
  priority: 'low' | 'medium' | 'high'
  status: 'pending' | 'in_progress' | 'completed'
  studentNotes?: string
  coachNotes?: string
}

// MENSAJE DE COACHING
export interface CoachingMessage {
  id: string
  sessionId?: string
  studentId: string
  coachId: string
  senderId: string
  senderType: 'student' | 'coach' | 'system'
  content: string
  type: 'text' | 'audio' | 'image' | 'file' | 'quick_response'
  timestamp: Date
  isRead: boolean
  metadata?: {
    messageLength: number
    sentiment: 'positive' | 'neutral' | 'negative' | 'urgent'
    containsGoals: boolean
    containsQuestions: boolean
    replyToMessageId?: string
  }
  urgencyLevel: 'normal' | 'high' | 'emergency'
  responseRequired: boolean
  autoResponseGenerated?: boolean
}

// PLAN PERSONALIZADO
export interface PersonalizedPlan {
  id: string
  studentId: string
  coachId: string
  createdDate: Date
  lastUpdated: Date
  title: string
  description: string
  duration: number // días
  difficulty: 'beginner' | 'intermediate' | 'advanced' | 'expert'
  categories: string[]
  milestones: PlanMilestone[]
  weeklyGoals: WeeklyGoal[]
  adaptiveElements: AdaptiveElement[]
  status: 'draft' | 'active' | 'paused' | 'completed' | 'archived'
  completionRate: number
  studentSatisfaction?: number
}

export interface PlanMilestone {
  id: string
  week: number
  title: string
  description: string
  objectives: string[]
  successCriteria: string[]
  isCompleted: boolean
  completedDate?: Date
  coachComments?: string
  studentReflection?: string
}

export interface WeeklyGoal {
  week: number
  mainFocus: string
  specificGoals: string[]
  challenges: string[]
  supportResources: string[]
  expectedDifficulty: number // 1-10
  actualDifficulty?: number
  weeklyReflection?: string
}

export interface AdaptiveElement {
  type: 'difficulty_adjustment' | 'timeline_extension' | 'focus_shift' | 'additional_support'
  trigger: string
  condition: string
  action: string
  appliedDate?: Date
  isActive: boolean
}

// ESCALADO DE COACHES
export interface CoachEscalation {
  reason: 'no_response' | 'student_request' | 'complexity_increase' | 'specialty_needed' | 'performance_issue'
  fromCoachId: string
  toCoachId: string
  studentId: string
  escalationDate: Date
  resolution: string
  timeToResolve?: number
  studentSatisfaction?: number
}

// Servicio principal de coaching
export class CoachingService {
  // ASIGNACIÓN DE COACHES
  static async assignCoach(studentId: string, tier: string, preferences?: any): Promise<CoachProfile | null> {
    const studentProfile = await this.getStudentProfile(studentId)
    const availableCoaches = await this.getAvailableCoaches(tier as any)

    if (availableCoaches.length === 0) {
      // Escalar a tier superior si no hay disponibilidad
      await this.handleNoAvailability(studentId, tier)
      return null
    }

    const bestMatch = this.findBestCoachMatch(studentProfile, availableCoaches, preferences)

    if (bestMatch) {
      await this.createCoachingRelationship(studentId, bestMatch.id, tier)
      await this.scheduleOnboardingSession(studentId, bestMatch.id)
    }

    return bestMatch
  }

  private static findBestCoachMatch(student: any, coaches: CoachProfile[], preferences?: any): CoachProfile {
    return coaches
      .map(coach => ({
        coach,
        score: this.calculateCoachMatchScore(student, coach, preferences)
      }))
      .sort((a, b) => b.score - a.score)[0].coach
  }

  private static calculateCoachMatchScore(student: any, coach: CoachProfile, preferences?: any): number {
    let score = 0

    // Experiencia (30%)
    score += Math.min(coach.experience.averageRating * 20, 100) * 0.3

    // Especialización (25%)
    if (student.preferredCategories?.some((cat: string) => coach.specializations.includes(cat))) {
      score += 25
    }

    // Timezone match (20%)
    if (student.timezone === coach.timezone) {
      score += 20
    }

    // Idioma (15%)
    if (student.language && coach.languages.includes(student.language)) {
      score += 15
    }

    // Disponibilidad (10%)
    const tier = student.currentTier || 'pro'
    const availability = coach.availability.maxStudentsPerTier[tier as keyof typeof coach.availability.maxStudentsPerTier] -
                        coach.availability.currentStudentsCount[tier as keyof typeof coach.availability.currentStudentsCount]
    score += Math.min(availability * 2, 10)

    // Preferencias específicas si se proporcionan
    if (preferences?.coachingStyle && coach.coachingStyle.approach === preferences.coachingStyle) {
      score += 10
    }

    return score
  }

  // SESIONES DE COACHING
  static async scheduleCoachingSession(studentId: string, type: string, preferredTime?: Date): Promise<CoachingSession | null> {
    const coachingRelation = await this.getCoachingRelationship(studentId)
    if (!coachingRelation) return null

    const coach = await this.getCoachProfile(coachingRelation.coachId)
    if (!coach) return null

    // Verificar límites del tier
    const tierLimits = await this.getTierLimits(studentId)
    const usageThisMonth = await this.getMonthlyUsage(studentId, type)

    if (usageThisMonth >= tierLimits[type]) {
      await this.suggestUpgrade(studentId, type)
      return null
    }

    const session: CoachingSession = {
      id: this.generateSessionId(),
      studentId,
      coachId: coachingRelation.coachId,
      type: type as any,
      scheduledDate: preferredTime || this.suggestNextAvailableSlot(coach),
      topic: 'Sesión de coaching programada',
      studentGoals: [],
      coachNotes: '',
      actionItems: [],
      status: 'scheduled'
    }

    await this.saveSession(session)
    await this.notifyBothParties(session)

    return session
  }

  // MENSAJERÍA PREMIUM
  static async sendCoachingMessage(senderId: string, receiverId: string, content: string, urgency: string = 'normal'): Promise<CoachingMessage> {
    const message: CoachingMessage = {
      id: this.generateMessageId(),
      studentId: senderId.startsWith('student_') ? senderId : receiverId,
      coachId: senderId.startsWith('coach_') ? senderId : receiverId,
      senderId,
      senderType: senderId.startsWith('student_') ? 'student' : 'coach',
      content,
      type: 'text',
      timestamp: new Date(),
      isRead: false,
      urgencyLevel: urgency as any,
      responseRequired: urgency !== 'normal',
      metadata: {
        messageLength: content.length,
        sentiment: await this.analyzeSentiment(content),
        containsGoals: this.containsGoals(content),
        containsQuestions: this.containsQuestions(content)
      }
    }

    await this.saveMessage(message)

    // Notificar al receptor según urgencia
    if (urgency === 'emergency') {
      await this.sendEmergencyNotification(receiverId, message)
    } else if (urgency === 'high') {
      await this.sendPriorityNotification(receiverId, message)
    }

    // Auto-respuesta inmediata si el coach no está disponible
    if (message.senderType === 'student') {
      await this.handleStudentMessage(message)
    }

    return message
  }

  private static async handleStudentMessage(message: CoachingMessage): Promise<void> {
    const coach = await this.getCoachProfile(message.coachId)
    if (!coach?.availability.isActive) {
      // Generar respuesta automática mientras el coach responde
      await this.generateAutoResponse(message)
    }

    // Actualizar tiempo de respuesta esperado
    await this.updateResponseExpectation(message)
  }

  // PLANES PERSONALIZADOS
  static async createPersonalizedPlan(studentId: string, coachId: string, requirements: any): Promise<PersonalizedPlan> {
    const studentProfile = await this.getStudentProfile(studentId)
    const coachExpertise = await this.getCoachProfile(coachId)

    const plan: PersonalizedPlan = {
      id: this.generatePlanId(),
      studentId,
      coachId,
      createdDate: new Date(),
      lastUpdated: new Date(),
      title: `Plan personalizado para ${studentProfile.name}`,
      description: requirements.description || 'Plan adaptado a tus objetivos específicos',
      duration: requirements.durationDays || 30,
      difficulty: requirements.difficulty || 'intermediate',
      categories: requirements.categories || [],
      milestones: this.generateMilestones(requirements),
      weeklyGoals: this.generateWeeklyGoals(requirements),
      adaptiveElements: this.generateAdaptiveElements(requirements),
      status: 'draft',
      completionRate: 0
    }

    await this.savePlan(plan)
    await this.notifyPlanCreated(studentId, coachId, plan)

    return plan
  }

  // ESCALADO Y SOPORTE
  static async escalateToSeniorCoach(studentId: string, reason: string): Promise<boolean> {
    const currentRelation = await this.getCoachingRelationship(studentId)
    if (!currentRelation) return false

    const seniorCoaches = await this.getSeniorCoaches(currentRelation.coachTier)

    if (seniorCoaches.length === 0) {
      // Escalado a tier superior gratis por 1 semana
      await this.grantTemporaryUpgrade(studentId, 'premium', 7)
      return true
    }

    const bestSeniorCoach = seniorCoaches[0] // Lógica de selección

    const escalation: CoachEscalation = {
      reason: reason as any,
      fromCoachId: currentRelation.coachId,
      toCoachId: bestSeniorCoach.id,
      studentId,
      escalationDate: new Date(),
      resolution: 'Escalado a coach senior'
    }

    await this.executeEscalation(escalation)
    return true
  }

  // UPGRADE PATHS NATURALES
  static async suggestUpgrade(studentId: string, context: string): Promise<void> {
    const currentTier = await this.getCurrentTier(studentId)
    const usage = await this.getUsageStats(studentId)

    const suggestedTier = this.calculateOptimalTier(currentTier, usage, context)

    if (suggestedTier !== currentTier) {
      const benefits = this.getTierBenefits(suggestedTier)
      await this.sendUpgradeSuggestion(studentId, suggestedTier, benefits, context)
    }
  }

  private static calculateOptimalTier(currentTier: string, usage: any, context: string): string {
    // Lógica para sugerir tier óptimo basado en uso real
    if (usage.monthlyMessages > 40 && currentTier === 'free') return 'pro'
    if (usage.needsVideoCalls && currentTier === 'pro') return 'premium'
    if (usage.emergencySupport && currentTier !== 'vip') return 'vip'

    return currentTier
  }

  // Métodos auxiliares mock para implementación futura
  private static async getStudentProfile(_studentId: string): Promise<any> {
    return {
      name: 'Usuario',
      timezone: 'America/Mexico_City',
      language: 'es',
      preferredCategories: ['fitness', 'mindfulness']
    }
  }

  private static async getAvailableCoaches(_tier: string): Promise<CoachProfile[]> {
    return []
  }

  private static async handleNoAvailability(_studentId: string, _tier: string): Promise<void> {
    // Manejar falta de disponibilidad
  }

  private static async createCoachingRelationship(_studentId: string, _coachId: string, _tier: string): Promise<void> {
    // Crear relación coach-estudiante
  }

  private static async scheduleOnboardingSession(_studentId: string, _coachId: string): Promise<void> {
    // Programar sesión inicial
  }

  private static generateSessionId(): string {
    return `session_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
  }

  private static generateMessageId(): string {
    return `msg_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
  }

  private static generatePlanId(): string {
    return `plan_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
  }

  private static async analyzeSentiment(_content: string): Promise<'positive' | 'neutral' | 'negative' | 'urgent'> {
    return 'neutral' // Implementar análisis de sentimiento
  }

  private static containsGoals(_content: string): boolean {
    return /\b(objetivo|meta|quiero|deseo|planear)\b/i.test(_content)
  }

  private static containsQuestions(_content: string): boolean {
    return /[¿?]/.test(_content)
  }

  // Más métodos mock...
  private static async getCoachingRelationship(_studentId: string): Promise<any> { return null }
  private static async getCoachProfile(_coachId: string): Promise<CoachProfile | null> { return null }
  private static async getTierLimits(_studentId: string): Promise<any> { return {} }
  private static async getMonthlyUsage(_studentId: string, _type: string): Promise<number> { return 0 }
  private static suggestNextAvailableSlot(_coach: CoachProfile): Date { return new Date() }
  private static async saveSession(_session: CoachingSession): Promise<void> {}
  private static async notifyBothParties(_session: CoachingSession): Promise<void> {}
  private static async saveMessage(_message: CoachingMessage): Promise<void> {}
  private static async sendEmergencyNotification(_receiverId: string, _message: CoachingMessage): Promise<void> {}
  private static async sendPriorityNotification(_receiverId: string, _message: CoachingMessage): Promise<void> {}
  private static async generateAutoResponse(_message: CoachingMessage): Promise<void> {}
  private static async updateResponseExpectation(_message: CoachingMessage): Promise<void> {}
  private static generateMilestones(_requirements: any): PlanMilestone[] { return [] }
  private static generateWeeklyGoals(_requirements: any): WeeklyGoal[] { return [] }
  private static generateAdaptiveElements(_requirements: any): AdaptiveElement[] { return [] }
  private static async savePlan(_plan: PersonalizedPlan): Promise<void> {}
  private static async notifyPlanCreated(_studentId: string, _coachId: string, _plan: PersonalizedPlan): Promise<void> {}
  private static async getSeniorCoaches(_currentTier: string): Promise<CoachProfile[]> { return [] }
  private static async grantTemporaryUpgrade(_studentId: string, _tier: string, _days: number): Promise<void> {}
  private static async executeEscalation(_escalation: CoachEscalation): Promise<void> {}
  private static async getCurrentTier(_studentId: string): Promise<string> { return 'free' }
  private static async getUsageStats(_studentId: string): Promise<any> { return {} }
  private static getTierBenefits(_tier: string): string[] { return [] }
  private static async sendUpgradeSuggestion(_studentId: string, _tier: string, _benefits: string[], _context: string): Promise<void> {}
}

export default CoachingService
