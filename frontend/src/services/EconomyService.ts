// Sistema de Economía Interna IMPULSE - Escalable, Ética y 100% Controlada
export interface Currency {
  id: string
  name: string
  type: 'soft' | 'hard' | 'reputation' | 'service'
  transferable: boolean
  purchasable: boolean
  description: string
}

export interface CurrencyBalance {
  userId: string
  motivation: number      // Moneda blanda - gratis por uso
  impulseCoins: number   // Moneda dura - de pago o stipend Pro
  cred: number           // Reputación - no comprable
  slaCredits: number     // Créditos de servicio - consumibles
  lastUpdated: Date
}

// Monedas del sistema (sin azar, sin cash-out, sin cripto)
export const CURRENCIES: Record<string, Currency> = {
  MOTIVATION: {
    id: 'motivation',
    name: 'Motivación',
    type: 'soft',
    transferable: false,
    purchasable: false,
    description: 'Moneda blanda obtenida por uso diario: check-ins, invitar validador, subir evidencia, validaciones <48h'
  },
  IMPULSE_COINS: {
    id: 'impulseCoins',
    name: 'IMPULSE Coins',
    type: 'hard',
    transferable: false,
    purchasable: true,
    description: 'Moneda dura de pago o stipend Pro. Usos: add-ons no decisivos (SLA+, storage+, themes). Nunca compra validaciones'
  },
  CRED: {
    id: 'cred',
    name: 'Cred',
    type: 'reputation',
    transferable: false,
    purchasable: false,
    description: 'Reputación no comprable. Sube con validaciones reales, feedback útil, rachas. Usos: badges, marcos'
  },
  SLA_CREDITS: {
    id: 'slaCredits',
    name: 'SLA+',
    type: 'service',
    transferable: false,
    purchasable: true,
    description: 'Créditos consumibles para recordatorios extra o segundo canal (email/WA/TG). Tienen coste real'
  }
}

// Fuentes de suministro (earning sources)
export interface EarningRule {
  id: string
  action: string
  currency: keyof typeof CURRENCIES
  amount: number
  maxPerDay?: number
  maxPerWeek?: number
  conditions?: string[]
}

export const EARNING_RULES: EarningRule[] = [
  // MOTIVACIÓN (uso diario)
  { id: 'daily_checkin', action: 'DAILY_CHECKIN', currency: 'MOTIVATION', amount: 10, maxPerDay: 1 },
  { id: 'invite_validator', action: 'INVITE_VALIDATOR', currency: 'MOTIVATION', amount: 25, maxPerDay: 3 },
  { id: 'upload_evidence', action: 'UPLOAD_EVIDENCE', currency: 'MOTIVATION', amount: 15, maxPerDay: 5 },
  { id: 'validation_under_48h', action: 'VALIDATION_RECEIVED_FAST', currency: 'MOTIVATION', amount: 50, maxPerDay: 10 },

  // IMPULSE COINS (hitos deterministas)
  { id: 'first_challenge_completed', action: 'FIRST_CHALLENGE_COMPLETED', currency: 'IMPULSE_COINS', amount: 100, maxPerDay: 1 },
  { id: 'three_validations_48h', action: 'THREE_FAST_VALIDATIONS', currency: 'IMPULSE_COINS', amount: 75, maxPerWeek: 1 },
  { id: 'weekly_streak', action: 'WEEKLY_STREAK', currency: 'IMPULSE_COINS', amount: 50, maxPerWeek: 1 },

  // CRED (reputación por calidad)
  { id: 'validation_approved', action: 'VALIDATION_APPROVED', currency: 'CRED', amount: 10 },
  { id: 'useful_feedback', action: 'USEFUL_FEEDBACK_RECEIVED', currency: 'CRED', amount: 15 },
  { id: 'streak_maintained', action: 'STREAK_MAINTAINED', currency: 'CRED', amount: 5, maxPerDay: 1 },

  // SLA CREDITS (Pro stipend o compra)
  { id: 'pro_monthly_stipend', action: 'PRO_MONTHLY_STIPEND', currency: 'SLA_CREDITS', amount: 5, maxPerDay: 1, conditions: ['HAS_PRO_SUBSCRIPTION'] }
]

// Sumideros (spending sinks)
export interface SpendingItem {
  id: string
  name: string
  category: 'sla' | 'storage' | 'templates' | 'cosmetics' | 'programs'
  currency: keyof typeof CURRENCIES
  cost: number
  description: string
  realCost?: number // Coste real en euros para calcular márgenes
  maxPurchasesPerMonth?: number
}

export const SPENDING_ITEMS: SpendingItem[] = [
  // SLA+ (con coste real controlado)
  { id: 'sla_extra_reminder', name: 'Recordatorio Extra', category: 'sla', currency: 'SLA_CREDITS', cost: 1, realCost: 0.15, description: 'Recordatorio adicional por email/WhatsApp' },
  { id: 'sla_second_channel', name: 'Segundo Canal 24h', category: 'sla', currency: 'SLA_CREDITS', cost: 3, realCost: 0.45, description: 'Notificación por segundo canal durante 24h' },

  // Storage+ (por bloques)
  { id: 'storage_1gb', name: 'Storage +1GB', category: 'storage', currency: 'IMPULSE_COINS', cost: 200, realCost: 0.05, description: '+1GB almacenamiento adicional' },
  { id: 'storage_5gb', name: 'Storage +5GB', category: 'storage', currency: 'IMPULSE_COINS', cost: 800, realCost: 0.25, description: '+5GB almacenamiento adicional' },

  // Templates+ (packs por categoría)
  { id: 'templates_study', name: 'Pack Estudio', category: 'templates', currency: 'IMPULSE_COINS', cost: 150, description: 'Plantillas avanzadas para estudio y productividad' },
  { id: 'templates_fitness', name: 'Pack Fitness', category: 'templates', currency: 'IMPULSE_COINS', cost: 150, description: 'Plantillas específicas para fitness y deporte' },
  { id: 'templates_language', name: 'Pack Idiomas', category: 'templates', currency: 'IMPULSE_COINS', cost: 150, description: 'Plantillas para aprendizaje de idiomas' },

  // Cosméticos (themes, marcos)
  { id: 'theme_glass', name: 'Tema Glass', category: 'cosmetics', currency: 'IMPULSE_COINS', cost: 100, description: 'Tema premium con efectos de cristal' },
  { id: 'theme_dark_pro', name: 'Tema Dark Pro', category: 'cosmetics', currency: 'IMPULSE_COINS', cost: 100, description: 'Tema oscuro profesional' },
  { id: 'celebration_frames', name: 'Marcos Celebración', category: 'cosmetics', currency: 'IMPULSE_COINS', cost: 75, description: 'Marcos especiales para logros' },

  // Programas guiados (30 días)
  { id: 'program_30_study', name: 'Programa 30 Días - Estudio', category: 'programs', currency: 'IMPULSE_COINS', cost: 500, description: 'Programa estructurado 30 días con material y seguimiento' },
  { id: 'program_30_fitness', name: 'Programa 30 Días - Fitness', category: 'programs', currency: 'IMPULSE_COINS', cost: 500, description: 'Programa fitness 30 días con rutinas y validación' }
]

// Balance económico automático (tipo "Clash")
export interface EconomicGuardrails {
  monthlyRewardsPool: number // % fijo del margen mensual
  maxRewardsPercentage: number // Máximo 8% de ingresos
  maxBenefitPercentage: number // Máximo 25% del beneficio neto
  userSpendingLimits: {
    monthly: number // 100€ máximo por usuario/mes
    dailyThreshold: number // Confirmación extra >50€
    impulseProtection: number // >X compras en 24h = confirmación
  }
}

export const ECONOMIC_GUARDRAILS: EconomicGuardrails = {
  monthlyRewardsPool: 0,
  maxRewardsPercentage: 0.08, // 8%
  maxBenefitPercentage: 0.25, // 25%
  userSpendingLimits: {
    monthly: 100,
    dailyThreshold: 50,
    impulseProtection: 3
  }
}

// Fórmula de presupuesto automático
export function calculateMonthlyRewardsPool(
  monthlyRevenue: number,
  monthlyNetProfit: number
): number {
  const revenueLimit = monthlyRevenue * ECONOMIC_GUARDRAILS.maxRewardsPercentage
  const profitLimit = monthlyNetProfit * ECONOMIC_GUARDRAILS.maxBenefitPercentage
  return Math.min(revenueLimit, profitLimit)
}

// Sistema de transacciones con trazabilidad
export interface CurrencyTransaction {
  id: string
  userId: string
  type: 'earn' | 'spend' | 'stipend' | 'purchase'
  currency: keyof typeof CURRENCIES
  amount: number
  reason: string
  sourceId?: string // ID del earning rule o spending item
  metadata?: Record<string, any>
  createdAt: Date
  balanceBefore: number
  balanceAfter: number
}

// Servicios de economía
export class EconomyService {
  // Ganar moneda por acción
  static async earnCurrency(
    userId: string,
    action: string,
    metadata?: Record<string, any>
  ): Promise<CurrencyTransaction[]> {
    const applicableRules = EARNING_RULES.filter(rule => rule.action === action)
    const transactions: CurrencyTransaction[] = []

    for (const rule of applicableRules) {
      // Verificar límites diarios/semanales
      if (await this.checkEarningLimits(userId, rule)) {
        const transaction = await this.addCurrency(userId, rule.currency, rule.amount, `Earned: ${action}`, rule.id)
        transactions.push(transaction)
      }
    }

    return transactions
  }

  // Gastar moneda por compra
  static async spendCurrency(
    userId: string,
    itemId: string,
    quantity: number = 1
  ): Promise<{ success: boolean; transaction?: CurrencyTransaction; error?: string }> {
    const item = SPENDING_ITEMS.find(i => i.id === itemId)
    if (!item) return { success: false, error: 'Item not found' }

    const totalCost = item.cost * quantity
    const userBalance = await this.getUserBalance(userId)

    if (userBalance[item.currency] < totalCost) {
      return { success: false, error: 'Insufficient balance' }
    }

    // Verificar límites de gasto
    if (!(await this.checkSpendingLimits(userId, item, quantity))) {
      return { success: false, error: 'Spending limit exceeded' }
    }

    const transaction = await this.subtractCurrency(
      userId,
      item.currency,
      totalCost,
      `Purchased: ${item.name} x${quantity}`,
      item.id
    )

    return { success: true, transaction }
  }

  // Stipend mensual Pro
  static async processMonthlyStipend(userId: string): Promise<CurrencyTransaction[]> {
    const hasProSubscription = await this.userHasProSubscription(userId)
    if (!hasProSubscription) return []

    // Stipend de IMPULSE Coins para Pro
    const coinsStipend = await this.addCurrency(
      userId,
      'IMPULSE_COINS',
      300, // 300 coins mensuales
      'Pro Monthly Stipend - Coins',
      'pro_monthly_stipend'
    )

    // Cupón SLA+ para Pro
    const slaStipend = await this.addCurrency(
      userId,
      'SLA_CREDITS',
      5, // 5 créditos SLA mensuales
      'Pro Monthly Stipend - SLA+',
      'pro_monthly_stipend'
    )

    return [coinsStipend, slaStipend]
  }

  private static async checkEarningLimits(userId: string, rule: EarningRule): Promise<boolean> {
    // Implementar verificación de límites diarios/semanales
    // Por ahora retorna true, pero aquí iría la lógica real
    return true
  }

  private static async checkSpendingLimits(userId: string, item: SpendingItem, quantity: number): Promise<boolean> {
    // Verificar límites mensuales por usuario
    const monthlySpending = await this.getUserMonthlySpending(userId)
    const itemCost = (item.realCost || 0) * quantity

    return (monthlySpending + itemCost) <= ECONOMIC_GUARDRAILS.userSpendingLimits.monthly
  }

  private static async addCurrency(
    userId: string,
    currency: keyof typeof CURRENCIES,
    amount: number,
    reason: string,
    sourceId?: string
  ): Promise<CurrencyTransaction> {
    // Implementación real de base de datos
    const balance = await this.getUserBalance(userId)
    const newBalance = balance[currency] + amount

    const transaction: CurrencyTransaction = {
      id: crypto.randomUUID(),
      userId,
      type: 'earn',
      currency,
      amount,
      reason,
      sourceId,
      createdAt: new Date(),
      balanceBefore: balance[currency],
      balanceAfter: newBalance
    }

    // Actualizar balance en BD
    await this.updateUserBalance(userId, currency, newBalance)

    return transaction
  }

  private static async subtractCurrency(
    userId: string,
    currency: keyof typeof CURRENCIES,
    amount: number,
    reason: string,
    sourceId?: string
  ): Promise<CurrencyTransaction> {
    const balance = await this.getUserBalance(userId)
    const newBalance = balance[currency] - amount

    const transaction: CurrencyTransaction = {
      id: crypto.randomUUID(),
      userId,
      type: 'spend',
      currency,
      amount: -amount,
      reason,
      sourceId,
      createdAt: new Date(),
      balanceBefore: balance[currency],
      balanceAfter: newBalance
    }

    await this.updateUserBalance(userId, currency, newBalance)

    return transaction
  }

  private static async getUserBalance(userId: string): Promise<CurrencyBalance> {
    // Implementación real - por ahora mock
    return {
      userId,
      motivation: 0,
      impulseCoins: 0,
      cred: 0,
      slaCredits: 0,
      lastUpdated: new Date()
    }
  }

  private static async updateUserBalance(userId: string, currency: keyof typeof CURRENCIES, newAmount: number): Promise<void> {
    // Implementación real de BD
  }

  private static async userHasProSubscription(userId: string): Promise<boolean> {
    // Verificar suscripción Pro activa
    return false // Mock
  }

  private static async getUserMonthlySpending(userId: string): Promise<number> {
    // Calcular gasto mensual en euros reales
    return 0 // Mock
  }
}

export default EconomyService
