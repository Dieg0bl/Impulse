// Sistema SLA de Validación Humana - Timeouts éticos y redistribución automática
import { EconomyService } from './EconomyService'
import { GamificationService } from './GamificationService'

// NIVELES DE TIMEOUT CON RECOMPENSAS
export interface ValidationSLA {
  level: 'optimal' | 'standard' | 'delayed' | 'critical'
  timeoutHours: number
  slaReward: number // SLA+ currency amount
  description: string
  userMessage: string
  coachMessage: string
}

export const VALIDATION_SLA_LEVELS: ValidationSLA[] = [
  {
    level: 'optimal',
    timeoutHours: 6,
    slaReward: 50,
    description: 'Validación ultra-rápida que acelera el momentum',
    userMessage: '¡Increíble! Tu validador respondió súper rápido 🚀',
    coachMessage: 'Excelente tiempo de respuesta. Usuario muy motivado.'
  },
  {
    level: 'standard',
    timeoutHours: 24,
    slaReward: 25,
    description: 'Validación dentro del tiempo esperado',
    userMessage: 'Tu validación llegó en tiempo perfecto ✅',
    coachMessage: 'Tiempo de respuesta estándar. Mantener consistencia.'
  },
  {
    level: 'delayed',
    timeoutHours: 48,
    slaReward: 10,
    description: 'Validación tardía que requiere explicación',
    userMessage: 'Tu validador se disculpa por el retraso 📝',
    coachMessage: 'Retraso identificado. Favor explicar o redistribuir.'
  },
  {
    level: 'critical',
    timeoutHours: 72,
    slaReward: 0,
    description: 'Timeout crítico - redistribución automática',
    userMessage: 'Te hemos asignado un nuevo validador para mantener tu momentum 🔄',
    coachMessage: 'Timeout crítico alcanzado. Redistribución automática activada.'
  }
]

// VALIDADOR CON ESTADÍSTICAS SLA
export interface ValidatorProfile {
  id: string
  userId: string
  name: string
  slaStats: {
    averageResponseHours: number
    optimalValidations: number // <6h
    standardValidations: number // 6-24h
    delayedValidations: number // 24-48h
    timeoutValidations: number // >48h
    totalValidations: number
    slaScore: number // 0-100
    currentStreak: number // validaciones consecutivas <24h
    lastActivityDate: Date
  }
  availability: {
    isActive: boolean
    maxDailyValidations: number
    currentDailyValidations: number
    timezone: string
    preferredHours: number[] // 0-23
    autoAssignEnabled: boolean
  }
  specializations: string[]
  languagePreferences: string[]
  reputationScore: number
}

// SOLICITUD DE VALIDACIÓN CON TIMEOUTS
export interface ValidationRequest {
  id: string
  userId: string
  challengeId: string
  evidenceId: string
  requestDate: Date
  assignedValidatorId: string | null
  backupValidatorIds: string[]
  currentSLA: ValidationSLA
  escalationLevel: number
  isRedistributed: boolean
  urgencyBoost: number // multiplicador por retrasos
  status: 'pending' | 'assigned' | 'in_review' | 'completed' | 'timeout'
  completedDate?: Date
  actualResponseHours?: number
}

// POOL DE VALIDADORES DISPONIBLES
export interface ValidatorPool {
  region: string
  timezone: string
  availableValidators: ValidatorProfile[]
  avgResponseTime: number
  capacityUtilization: number
  lastUpdated: Date
}

// REGLAS DE REDISTRIBUCIÓN
export interface RedistributionRule {
  trigger: 'timeout_reached' | 'validator_inactive' | 'capacity_exceeded' | 'user_request'
  timeoutHours: number
  redistributionStrategy: 'nearest_available' | 'best_sla_score' | 'timezone_match' | 'specialization_match'
  compensationRequired: boolean
  compensationAmount?: number
  notificationTemplate: string
}

export const REDISTRIBUTION_RULES: RedistributionRule[] = [
  {
    trigger: 'timeout_reached',
    timeoutHours: 48,
    redistributionStrategy: 'best_sla_score',
    compensationRequired: true,
    compensationAmount: 25, // SLA+ currency al usuario
    notificationTemplate: 'timeout_redistribution'
  },
  {
    trigger: 'validator_inactive',
    timeoutHours: 0, // inmediato
    redistributionStrategy: 'nearest_available',
    compensationRequired: false,
    notificationTemplate: 'validator_inactive'
  },
  {
    trigger: 'capacity_exceeded',
    timeoutHours: 12,
    redistributionStrategy: 'timezone_match',
    compensationRequired: false,
    notificationTemplate: 'capacity_rebalance'
  }
]

// NOTIFICACIONES AUTOMÁTICAS
export interface SLANotification {
  type: 'reminder' | 'escalation' | 'timeout_warning' | 'redistribution'
  recipient: 'validator' | 'user' | 'coach' | 'admin'
  triggerHours: number
  template: string
  urgency: 'low' | 'medium' | 'high' | 'critical'
  includeCompensation: boolean
}

export const SLA_NOTIFICATIONS: SLANotification[] = [
  {
    type: 'reminder',
    recipient: 'validator',
    triggerHours: 12,
    template: 'gentle_reminder_12h',
    urgency: 'low',
    includeCompensation: false
  },
  {
    type: 'escalation',
    recipient: 'validator',
    triggerHours: 24,
    template: 'escalation_24h',
    urgency: 'medium',
    includeCompensation: false
  },
  {
    type: 'timeout_warning',
    recipient: 'validator',
    triggerHours: 36,
    template: 'final_warning_36h',
    urgency: 'high',
    includeCompensation: false
  },
  {
    type: 'redistribution',
    recipient: 'user',
    triggerHours: 48,
    template: 'new_validator_assigned',
    urgency: 'medium',
    includeCompensation: true
  }
]

// COMPENSACIONES POR RETRASOS
export interface SLACompensation {
  type: 'currency' | 'premium_feature' | 'priority_queue' | 'free_upgrade'
  delayHours: number
  description: string
  value: number | string
  isAutomatic: boolean
}

export const SLA_COMPENSATIONS: SLACompensation[] = [
  {
    type: 'currency',
    delayHours: 48,
    description: '+25 SLA+ por timeout de validador',
    value: 25,
    isAutomatic: true
  },
  {
    type: 'priority_queue',
    delayHours: 72,
    description: 'Próximas 3 validaciones en cola prioritaria',
    value: '3_validations',
    isAutomatic: true
  },
  {
    type: 'premium_feature',
    delayHours: 96,
    description: '7 días de features Pro gratuitas',
    value: '7_days_pro',
    isAutomatic: false // requiere aprobación manual
  }
]

// Servicio principal de SLA
export class ValidationSLAService {
  // ASIGNACIÓN INTELIGENTE DE VALIDADORES
  static async assignValidator(validationRequest: ValidationRequest): Promise<ValidatorProfile | null> {
    const userTimezone = await this.getUserTimezone(validationRequest.userId)
    const availableValidators = await this.getAvailableValidators(userTimezone)

    // Filtrar por capacidad disponible
    const capableValidators = availableValidators.filter(v =>
      v.availability.isActive &&
      v.availability.currentDailyValidations < v.availability.maxDailyValidations
    )

    if (capableValidators.length === 0) {
      return null // No hay validadores disponibles
    }

    // Algoritmo de asignación optimizada
    const bestValidator = this.selectBestValidator(capableValidators, validationRequest)

    if (bestValidator) {
      await this.assignValidationToValidator(validationRequest.id, bestValidator.id)
      await this.scheduleTimeoutChecks(validationRequest)
    }

    return bestValidator
  }

  private static selectBestValidator(validators: ValidatorProfile[], request: ValidationRequest): ValidatorProfile {
    return validators
      .map(validator => ({
        validator,
        score: this.calculateValidatorScore(validator, request)
      }))
      .sort((a, b) => b.score - a.score)[0].validator
  }

  private static calculateValidatorScore(validator: ValidatorProfile, request: ValidationRequest): number {
    let score = 0

    // SLA Score (40% del peso)
    score += validator.slaStats.slaScore * 0.4

    // Tiempo de respuesta promedio (30% del peso)
    const responseBonus = Math.max(0, 50 - validator.slaStats.averageResponseHours) * 0.6
    score += responseBonus

    // Streak actual (20% del peso)
    score += Math.min(validator.slaStats.currentStreak * 2, 20)

    // Disponibilidad inmediata (10% del peso)
    const currentHour = new Date().getHours()
    if (validator.availability.preferredHours.includes(currentHour)) {
      score += 10
    }

    return score
  }

  // MONITOREO DE TIMEOUTS
  static async checkTimeouts(): Promise<void> {
    const pendingValidations = await this.getPendingValidations()
    const now = new Date()

    for (const validation of pendingValidations) {
      const hoursElapsed = (now.getTime() - validation.requestDate.getTime()) / (1000 * 60 * 60)

      // Verificar si necesita escalación
      const newSLA = this.getSLALevel(hoursElapsed)
      if (newSLA.level !== validation.currentSLA.level) {
        await this.escalateValidation(validation, newSLA)
      }

      // Verificar si necesita redistribución
      if (hoursElapsed >= 48 && !validation.isRedistributed) {
        await this.redistributeValidation(validation)
      }

      // Verificar si necesita compensación
      if (hoursElapsed >= 48) {
        await this.applyUserCompensation(validation, hoursElapsed)
      }
    }
  }

  private static getSLALevel(hoursElapsed: number): ValidationSLA {
    if (hoursElapsed <= 6) return VALIDATION_SLA_LEVELS[0] // optimal
    if (hoursElapsed <= 24) return VALIDATION_SLA_LEVELS[1] // standard
    if (hoursElapsed <= 48) return VALIDATION_SLA_LEVELS[2] // delayed
    return VALIDATION_SLA_LEVELS[3] // critical
  }

  private static async escalateValidation(validation: ValidationRequest, newSLA: ValidationSLA): Promise<void> {
    validation.currentSLA = newSLA
    validation.escalationLevel += 1

    // Enviar notificación según el nivel
    const notification = SLA_NOTIFICATIONS.find(n =>
      n.triggerHours <= (new Date().getTime() - validation.requestDate.getTime()) / (1000 * 60 * 60)
    )

    if (notification) {
      await this.sendSLANotification(validation, notification)
    }

    await this.updateValidationRequest(validation)
  }

  // REDISTRIBUCIÓN AUTOMÁTICA
  static async redistributeValidation(validation: ValidationRequest): Promise<void> {
    const rule = REDISTRIBUTION_RULES.find(r => r.trigger === 'timeout_reached')
    if (!rule) return

    // Buscar nuevo validador
    const newValidator = await this.findAlternativeValidator(validation)

    if (newValidator) {
      // Transferir la validación
      validation.assignedValidatorId = newValidator.id
      validation.isRedistributed = true
      validation.escalationLevel += 1

      // Compensar al usuario
      if (rule.compensationRequired && rule.compensationAmount) {
        await EconomyService.earnCurrency(validation.userId, 'SLA_COMPENSATION')
        await this.logCompensation(validation.userId, 'timeout_redistribution', rule.compensationAmount)
      }

      // Notificar a ambas partes
      await this.notifyRedistribution(validation, newValidator)

      // Penalizar al validador original (reducir SLA score)
      await this.penalizeValidator(validation.assignedValidatorId!, 'timeout')

      await this.updateValidationRequest(validation)
    }
  }

  private static async findAlternativeValidator(validation: ValidationRequest): Promise<ValidatorProfile | null> {
    const userTimezone = await this.getUserTimezone(validation.userId)
    const availableValidators = await this.getAvailableValidators(userTimezone)

    // Excluir el validador original y los de backup ya intentados
    const excludeIds = [validation.assignedValidatorId, ...validation.backupValidatorIds]
    const candidates = availableValidators.filter(v => !excludeIds.includes(v.id))

    if (candidates.length === 0) return null

    // Priorizar validadores con mejor SLA score
    return candidates.sort((a, b) => b.slaStats.slaScore - a.slaStats.slaScore)[0]
  }

  // COMPENSACIONES AUTOMÁTICAS
  private static async applyUserCompensation(validation: ValidationRequest, hoursDelayed: number): Promise<void> {
    const applicableCompensations = SLA_COMPENSATIONS.filter(c =>
      hoursDelayed >= c.delayHours
    ).sort((a, b) => b.delayHours - a.delayHours) // Mayor compensación primero

    for (const compensation of applicableCompensations) {
      if (await this.hasReceivedCompensation(validation.userId, validation.id, compensation.type)) {
        continue // Ya se aplicó esta compensación
      }

      if (compensation.isAutomatic) {
        await this.grantCompensation(validation.userId, compensation)
        await this.logCompensation(validation.userId, compensation.type, compensation.value)
      } else {
        await this.queueManualCompensation(validation.userId, compensation)
      }
    }
  }

  private static async grantCompensation(userId: string, compensation: SLACompensation): Promise<void> {
    switch (compensation.type) {
      case 'currency':
        await EconomyService.earnCurrency(userId, 'SLA_COMPENSATION')
        break
      case 'priority_queue':
        await this.grantPriorityQueue(userId, 3)
        break
      case 'premium_feature':
        await this.grantTemporaryPro(userId, 7)
        break
    }
  }

  // ESTADÍSTICAS Y MÉTRICAS SLA
  static async updateValidatorSLAStats(validatorId: string, responseHours: number): Promise<void> {
    const validator = await this.getValidatorProfile(validatorId)
    if (!validator) return

    const stats = validator.slaStats
    stats.totalValidations += 1

    // Clasificar la respuesta
    if (responseHours <= 6) {
      stats.optimalValidations += 1
      stats.currentStreak += 1
    } else if (responseHours <= 24) {
      stats.standardValidations += 1
      stats.currentStreak += 1
    } else if (responseHours <= 48) {
      stats.delayedValidations += 1
      stats.currentStreak = 0
    } else {
      stats.timeoutValidations += 1
      stats.currentStreak = 0
    }

    // Recalcular promedio
    stats.averageResponseHours = (
      (stats.averageResponseHours * (stats.totalValidations - 1)) + responseHours
    ) / stats.totalValidations

    // Recalcular SLA score
    stats.slaScore = this.calculateSLAScore(stats)
    stats.lastActivityDate = new Date()

    await this.updateValidatorProfile(validator)
  }

  private static calculateSLAScore(stats: any): number {
    const total = stats.totalValidations
    if (total === 0) return 100

    const optimalRate = stats.optimalValidations / total
    const standardRate = stats.standardValidations / total
    const delayedRate = stats.delayedValidations / total
    const timeoutRate = stats.timeoutValidations / total

    // Fórmula ponderada: óptimo=100pts, estándar=80pts, tardío=40pts, timeout=0pts
    return Math.round(
      (optimalRate * 100) +
      (standardRate * 80) +
      (delayedRate * 40) +
      (timeoutRate * 0)
    )
  }

  // Métodos auxiliares mock para implementación futura
  private static async getUserTimezone(_userId: string): Promise<string> {
    return 'America/Mexico_City'
  }

  private static async getAvailableValidators(_timezone: string): Promise<ValidatorProfile[]> {
    return []
  }

  private static async assignValidationToValidator(_validationId: string, _validatorId: string): Promise<void> {
    // Implementar asignación
  }

  private static async scheduleTimeoutChecks(_validation: ValidationRequest): Promise<void> {
    // Programar checks automáticos
  }

  private static async getPendingValidations(): Promise<ValidationRequest[]> {
    return []
  }

  private static async updateValidationRequest(_validation: ValidationRequest): Promise<void> {
    // Actualizar en BD
  }

  private static async sendSLANotification(_validation: ValidationRequest, _notification: SLANotification): Promise<void> {
    // Enviar notificación
  }

  private static async notifyRedistribution(_validation: ValidationRequest, _newValidator: ValidatorProfile): Promise<void> {
    // Notificar redistribución
  }

  private static async penalizeValidator(_validatorId: string, _reason: string): Promise<void> {
    // Aplicar penalización al SLA score
  }

  private static async hasReceivedCompensation(_userId: string, _validationId: string, _type: string): Promise<boolean> {
    return false
  }

  private static async logCompensation(_userId: string, _type: string, _value: any): Promise<void> {
    // Log de compensación
  }

  private static async queueManualCompensation(_userId: string, _compensation: SLACompensation): Promise<void> {
    // Cola para revisión manual
  }

  private static async grantPriorityQueue(_userId: string, _validations: number): Promise<void> {
    // Otorgar cola prioritaria
  }

  private static async grantTemporaryPro(_userId: string, _days: number): Promise<void> {
    // Otorgar Pro temporal
  }

  private static async getValidatorProfile(_validatorId: string): Promise<ValidatorProfile | null> {
    return null
  }

  private static async updateValidatorProfile(_validator: ValidatorProfile): Promise<void> {
    // Actualizar perfil de validador
  }
}

export default ValidationSLAService
