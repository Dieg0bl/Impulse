// Sistema de Gamificación Ética - Impulsa validación real, no manipula
import { EconomyService } from './EconomyService'

// RACHAS + FREEZE SYSTEM
export interface UserStreak {
  userId: string
  currentStreak: number
  longestStreak: number
  lastActivityDate: Date
  freezesUsed: number
  freezesAvailable: number
  streakType: 'daily_activity' | 'validation_streak' | 'challenge_completion'
  isActive: boolean
}

export interface StreakFreeze {
  userId: string
  freezeDate: Date
  reason: 'user_requested' | 'validator_delay' | 'system_suggested'
  duration: number // horas
  isActive: boolean
}

// LIGAS PERSONALES (90 días, no comprables)
export interface PersonalLeague {
  id: string
  name: string
  tier: 'bronze' | 'silver' | 'gold' | 'platinum' | 'diamond'
  requirements: {
    minValidationRate: number // % validaciones <48h
    minCredPoints: number
    minStreakDays: number
    windowDays: number // ventana de evaluación (90 días)
  }
  benefits: {
    badgeIcon: string
    frameColor: string
    credBonus: number // multiplicador de Cred
    prestigePoints: number
  }
  description: string
}

export const PERSONAL_LEAGUES: PersonalLeague[] = [
  {
    id: 'bronze',
    name: 'Liga Bronce',
    tier: 'bronze',
    requirements: {
      minValidationRate: 0.60, // 60% validaciones <48h
      minCredPoints: 50,
      minStreakDays: 7,
      windowDays: 90
    },
    benefits: {
      badgeIcon: '🥉',
      frameColor: '#CD7F32',
      credBonus: 1.0,
      prestigePoints: 100
    },
    description: 'Primeros pasos hacia la excelencia'
  },
  {
    id: 'silver',
    name: 'Liga Plata',
    tier: 'silver',
    requirements: {
      minValidationRate: 0.75, // 75% validaciones <48h
      minCredPoints: 200,
      minStreakDays: 14,
      windowDays: 90
    },
    benefits: {
      badgeIcon: '🥈',
      frameColor: '#C0C0C0',
      credBonus: 1.1,
      prestigePoints: 250
    },
    description: 'Consistencia que marca la diferencia'
  },
  {
    id: 'gold',
    name: 'Liga Oro',
    tier: 'gold',
    requirements: {
      minValidationRate: 0.85, // 85% validaciones <48h
      minCredPoints: 500,
      minStreakDays: 21,
      windowDays: 90
    },
    benefits: {
      badgeIcon: '🥇',
      frameColor: '#FFD700',
      credBonus: 1.25,
      prestigePoints: 500
    },
    description: 'Excelencia reconocida por todos'
  },
  {
    id: 'platinum',
    name: 'Liga Platino',
    tier: 'platinum',
    requirements: {
      minValidationRate: 0.90, // 90% validaciones <48h
      minCredPoints: 1000,
      minStreakDays: 30,
      windowDays: 90
    },
    benefits: {
      badgeIcon: '💎',
      frameColor: '#E5E4E2',
      credBonus: 1.5,
      prestigePoints: 1000
    },
    description: 'Élite de validadores comprometidos'
  },
  {
    id: 'diamond',
    name: 'Liga Diamante',
    tier: 'diamond',
    requirements: {
      minValidationRate: 0.95, // 95% validaciones <48h
      minCredPoints: 2000,
      minStreakDays: 45,
      windowDays: 90
    },
    benefits: {
      badgeIcon: '💠',
      frameColor: '#B9F2FF',
      credBonus: 2.0,
      prestigePoints: 2500
    },
    description: 'Leyenda viviente de IMPULSE'
  }
]

// MISIONES DIARIAS/SEMANALES/MENSUALES
export interface Mission {
  id: string
  type: 'daily' | 'weekly' | 'monthly'
  category: 'social' | 'progress' | 'quality' | 'consistency'
  name: string
  description: string
  objectives: MissionObjective[]
  rewards: MissionReward[]
  isActive: boolean
  startDate: Date
  endDate: Date
  difficulty: 'easy' | 'medium' | 'hard' | 'expert'
}

export interface MissionObjective {
  id: string
  action: string
  targetCount: number
  currentCount: number
  isCompleted: boolean
  description: string
}

export interface MissionReward {
  type: 'currency' | 'chest' | 'item' | 'badge'
  currency?: string
  amount?: number
  chestId?: string
  itemId?: string
  badgeId?: string
  name: string
}

export const DAILY_MISSIONS: Mission[] = [
  {
    id: 'daily_social_invite',
    type: 'daily',
    category: 'social',
    name: 'Conecta con tu Red',
    description: 'Invita al menos 1 validador a un reto',
    objectives: [{
      id: 'invite_validator',
      action: 'INVITE_VALIDATOR',
      targetCount: 1,
      currentCount: 0,
      isCompleted: false,
      description: 'Invitar 1 validador'
    }],
    rewards: [{
      type: 'currency',
      currency: 'motivation',
      amount: 30,
      name: '+30 Motivación'
    }],
    isActive: true,
    startDate: new Date(),
    endDate: new Date(Date.now() + 24 * 60 * 60 * 1000),
    difficulty: 'easy'
  },
  {
    id: 'daily_quality_message',
    type: 'daily',
    category: 'quality',
    name: 'Mensaje Humano',
    description: 'Redacta un mensaje personalizado de al menos 50 caracteres',
    objectives: [{
      id: 'human_message',
      action: 'WRITE_HUMAN_MESSAGE',
      targetCount: 1,
      currentCount: 0,
      isCompleted: false,
      description: 'Escribir mensaje humano (≥50 chars)'
    }],
    rewards: [{
      type: 'currency',
      currency: 'cred',
      amount: 15,
      name: '+15 Cred'
    }],
    isActive: true,
    startDate: new Date(),
    endDate: new Date(Date.now() + 24 * 60 * 60 * 1000),
    difficulty: 'medium'
  },
  {
    id: 'daily_progress_evidence',
    type: 'daily',
    category: 'progress',
    name: 'Evidencia Visual',
    description: 'Sube evidencia visual de tu progreso',
    objectives: [{
      id: 'upload_evidence',
      action: 'UPLOAD_EVIDENCE',
      targetCount: 1,
      currentCount: 0,
      isCompleted: false,
      description: 'Subir 1 evidencia'
    }],
    rewards: [{
      type: 'currency',
      currency: 'motivation',
      amount: 25,
      name: '+25 Motivación'
    }],
    isActive: true,
    startDate: new Date(),
    endDate: new Date(Date.now() + 24 * 60 * 60 * 1000),
    difficulty: 'easy'
  }
]

export const WEEKLY_MISSIONS: Mission[] = [
  {
    id: 'weekly_fast_validation',
    type: 'weekly',
    category: 'consistency',
    name: 'Velocidad de Luz',
    description: 'Consigue 2 avances + 1 validación en menos de 48h',
    objectives: [
      {
        id: 'two_advances',
        action: 'PROGRESS_ADVANCE',
        targetCount: 2,
        currentCount: 0,
        isCompleted: false,
        description: '2 avances en retos'
      },
      {
        id: 'fast_validation',
        action: 'VALIDATION_UNDER_48H',
        targetCount: 1,
        currentCount: 0,
        isCompleted: false,
        description: '1 validación <48h'
      }
    ],
    rewards: [
      {
        type: 'currency',
        currency: 'impulseCoins',
        amount: 100,
        name: '+100 IMPULSE Coins'
      },
      {
        type: 'chest',
        chestId: 'weekly_consistency_chest',
        name: 'Cofre de Consistencia Semanal'
      }
    ],
    isActive: true,
    startDate: new Date(),
    endDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000),
    difficulty: 'medium'
  }
]

export const MONTHLY_MISSIONS: Mission[] = [
  {
    id: 'monthly_program_completion',
    type: 'monthly',
    category: 'progress',
    name: 'Maratón de 30 Días',
    description: 'Completa un programa completo de 30 días',
    objectives: [{
      id: 'complete_30day_program',
      action: 'COMPLETE_30DAY_PROGRAM',
      targetCount: 1,
      currentCount: 0,
      isCompleted: false,
      description: 'Completar programa 30 días'
    }],
    rewards: [
      {
        type: 'currency',
        currency: 'impulseCoins',
        amount: 500,
        name: '+500 IMPULSE Coins'
      },
      {
        type: 'badge',
        badgeId: 'marathon_finisher',
        name: 'Badge Maratonista'
      },
      {
        type: 'chest',
        chestId: 'mastery_chest',
        name: 'Cofre de Maestría'
      }
    ],
    isActive: true,
    startDate: new Date(),
    endDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000),
    difficulty: 'expert'
  }
]

// EVENTOS TEMÁTICOS (bonos suaves)
export interface ThematicEvent {
  id: string
  name: string
  description: string
  theme: string
  startDate: Date
  endDate: Date
  bonusMultiplier: number
  applicableCurrencies: string[]
  eventMissions: Mission[]
  specialRewards: string[]
  isActive: boolean
}

export const THEMATIC_EVENTS: ThematicEvent[] = [
  {
    id: 'week_48h_challenge',
    name: 'Semana 48h',
    description: 'Una semana enfocada en validaciones ultra-rápidas',
    theme: 'speed_validation',
    startDate: new Date(),
    endDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000),
    bonusMultiplier: 1.5,
    applicableCurrencies: ['motivation', 'cred'],
    eventMissions: [], // Se poblarían con misiones específicas del evento
    specialRewards: ['theme_speed_rush', 'badge_lightning_validator'],
    isActive: true
  },
  {
    id: 'back_to_study_september',
    name: 'Back to Study',
    description: 'Septiembre de vuelta a los estudios con motivación extra',
    theme: 'education_focus',
    startDate: new Date('2025-09-01'),
    endDate: new Date('2025-09-30'),
    bonusMultiplier: 1.25,
    applicableCurrencies: ['motivation'],
    eventMissions: [],
    specialRewards: ['templates_study_premium', 'theme_academic'],
    isActive: true
  }
]

// Servicio principal de gamificación
export class GamificationService {
  // RACHAS Y FREEZE
  static async updateUserStreak(userId: string, activityType: string): Promise<UserStreak> {
    const currentStreak = await this.getUserStreak(userId)
    const now = new Date()
    const lastActivity = currentStreak?.lastActivityDate

    // Verificar si la racha continúa (menos de 24h desde última actividad)
    const hoursSinceLastActivity = lastActivity ?
      (now.getTime() - lastActivity.getTime()) / (1000 * 60 * 60) : 25

    if (hoursSinceLastActivity <= 24) {
      // Continuar racha
      currentStreak.currentStreak += 1
      currentStreak.lastActivityDate = now
      if (currentStreak.currentStreak > currentStreak.longestStreak) {
        currentStreak.longestStreak = currentStreak.currentStreak
      }
    } else if (hoursSinceLastActivity <= 48 && currentStreak.freezesAvailable > 0) {
      // Sugerir freeze si el retraso fue por validador/coach
      await this.suggestStreakFreeze(userId, 'validator_delay')
    } else {
      // Resetear racha
      currentStreak.currentStreak = 1
      currentStreak.lastActivityDate = now
    }

    // Recompensar rachas importantes
    if (currentStreak.currentStreak % 7 === 0) {
      await EconomyService.earnCurrency(userId, 'WEEKLY_STREAK')
    }

    await this.saveUserStreak(currentStreak)
    return currentStreak
  }

  static async useStreakFreeze(userId: string, reason: string): Promise<boolean> {
    const userStreak = await this.getUserStreak(userId)

    if (userStreak.freezesAvailable <= 0) return false

    const freeze: StreakFreeze = {
      userId,
      freezeDate: new Date(),
      reason: reason as any,
      duration: 24,
      isActive: true
    }

    userStreak.freezesAvailable -= 1
    await this.saveStreakFreeze(freeze)
    await this.saveUserStreak(userStreak)

    return true
  }

  // LIGAS PERSONALES
  static async updateUserLeague(userId: string): Promise<PersonalLeague | null> {
    const userStats = await this.getUserStats90Days(userId)
    const currentLeague = await this.getUserCurrentLeague(userId)

    for (const league of PERSONAL_LEAGUES.reverse()) { // Empezar por la más alta
      if (this.meetsLeagueRequirements(userStats, league)) {
        if (!currentLeague || league.tier !== currentLeague.tier) {
          await this.promoteUserToLeague(userId, league)
          // Recompensa por ascenso
          await EconomyService.earnCurrency(userId, `LEAGUE_PROMOTION_${league.tier.toUpperCase()}`)
        }
        return league
      }
    }

    return null
  }

  private static meetsLeagueRequirements(userStats: any, league: PersonalLeague): boolean {
    return userStats.validationRate >= league.requirements.minValidationRate &&
           userStats.credPoints >= league.requirements.minCredPoints &&
           userStats.longestStreak >= league.requirements.minStreakDays
  }

  // MISIONES
  static async updateMissionProgress(userId: string, action: string, count: number = 1): Promise<void> {
    const activeMissions = await this.getUserActiveMissions(userId)

    for (const mission of activeMissions) {
      for (const objective of mission.objectives) {
        if (objective.action === action && !objective.isCompleted) {
          objective.currentCount = Math.min(objective.currentCount + count, objective.targetCount)

          if (objective.currentCount >= objective.targetCount) {
            objective.isCompleted = true
            await this.checkMissionCompletion(userId, mission)
          }
        }
      }
    }

    await this.saveUserMissions(userId, activeMissions)
  }

  private static async checkMissionCompletion(userId: string, mission: Mission): Promise<void> {
    const allCompleted = mission.objectives.every(obj => obj.isCompleted)

    if (allCompleted) {
      // Otorgar recompensas
      for (const reward of mission.rewards) {
        await this.grantMissionReward(userId, reward)
      }

      // Marcar misión como completada
      await this.completeMission(userId, mission.id)
    }
  }

  private static async grantMissionReward(userId: string, reward: MissionReward): Promise<void> {
    switch (reward.type) {
      case 'currency':
        if (reward.currency && reward.amount) {
          await EconomyService.earnCurrency(userId, `MISSION_REWARD`)
        }
        break
      case 'chest':
        if (reward.chestId) {
          await this.unlockChest(userId, reward.chestId)
        }
        break
      // Otros tipos de recompensas...
    }
  }

  // EVENTOS TEMÁTICOS
  static async getActiveEvents(): Promise<ThematicEvent[]> {
    const now = new Date()
    return THEMATIC_EVENTS.filter(event =>
      event.isActive &&
      event.startDate <= now &&
      event.endDate >= now
    )
  }

  static async applyEventBonus(userId: string, currency: string, baseAmount: number): Promise<number> {
    const activeEvents = await this.getActiveEvents()
    let totalMultiplier = 1.0

    for (const event of activeEvents) {
      if (event.applicableCurrencies.includes(currency)) {
        totalMultiplier *= event.bonusMultiplier
      }
    }

    return Math.floor(baseAmount * totalMultiplier)
  }

  // Métodos privados mock para implementación futura
  private static async getUserStreak(userId: string): Promise<UserStreak> {
    return {
      userId,
      currentStreak: 0,
      longestStreak: 0,
      lastActivityDate: new Date(),
      freezesUsed: 0,
      freezesAvailable: 1, // 1 free per month, Pro gets +1
      streakType: 'daily_activity',
      isActive: true
    }
  }

  private static async suggestStreakFreeze(_userId: string, _reason: string): Promise<void> {
    // Implementar sugerencia de freeze al usuario
  }

  private static async saveUserStreak(_streak: UserStreak): Promise<void> {
    // Guardar en BD
  }

  private static async saveStreakFreeze(_freeze: StreakFreeze): Promise<void> {
    // Guardar freeze en BD
  }

  private static async getUserStats90Days(_userId: string): Promise<any> {
    return {
      validationRate: 0.8,
      credPoints: 300,
      longestStreak: 15
    }
  }

  private static async getUserCurrentLeague(_userId: string): Promise<PersonalLeague | null> {
    return null
  }

  private static async promoteUserToLeague(_userId: string, _league: PersonalLeague): Promise<void> {
    // Implementar promoción
  }

  private static async getUserActiveMissions(_userId: string): Promise<Mission[]> {
    return []
  }

  private static async saveUserMissions(_userId: string, _missions: Mission[]): Promise<void> {
    // Guardar misiones
  }

  private static async completeMission(_userId: string, _missionId: string): Promise<void> {
    // Marcar como completada
  }

  private static async unlockChest(_userId: string, _chestId: string): Promise<void> {
    // Desbloquear cofre
  }
}

export default GamificationService
