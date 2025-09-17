// Sistema de Cofres Deterministas - Zero RNG, 100% Transparente
export interface DeterministicChest {
  id: string
  name: string
  category: 'milestone' | 'achievement' | 'streak' | 'seasonal'
  unlockCondition: {
    type: 'challenge_completed' | 'streak_days' | 'validations_received' | 'cred_earned' | 'monthly_active'
    threshold: number
    timeframe?: '24h' | '7d' | '30d' | 'all_time'
  }
  rewards: ChestReward[]
  userCanChoose: boolean // True = usuario elige, False = todas las recompensas
  description: string
  icon: string
  rarity: 'common' | 'rare' | 'epic' | 'legendary'
}

export interface ChestReward {
  id: string
  type: 'currency' | 'item' | 'subscription_days' | 'feature_unlock'
  currency?: keyof typeof CURRENCIES
  amount?: number
  itemId?: string
  subscriptionDays?: number
  featureId?: string
  name: string
  description: string
  icon: string
  realCost: number // Coste real en euros para control de presupuesto
}

// Cofres disponibles (logros claros, sin azar)
export const DETERMINISTIC_CHESTS: DeterministicChest[] = [
  {
    id: 'welcome_chest',
    name: 'Cofre de Bienvenida',
    category: 'milestone',
    unlockCondition: {
      type: 'challenge_completed',
      threshold: 1,
      timeframe: 'all_time'
    },
    rewards: [
      {
        id: 'welcome_coins',
        type: 'currency',
        currency: 'IMPULSE_COINS',
        amount: 100,
        name: '100 IMPULSE Coins',
        description: 'Monedas para comenzar tu viaje',
        icon: '🪙',
        realCost: 0
      },
      {
        id: 'welcome_theme',
        type: 'item',
        itemId: 'theme_welcome',
        name: 'Tema de Bienvenida',
        description: 'Tema especial para nuevos usuarios',
        icon: '🎨',
        realCost: 0
      }
    ],
    userCanChoose: true,
    description: 'Tu primer logro merece una recompensa especial',
    icon: '🎁',
    rarity: 'common'
  },
  {
    id: 'consistency_chest',
    name: 'Cofre de Constancia',
    category: 'streak',
    unlockCondition: {
      type: 'streak_days',
      threshold: 7,
      timeframe: '7d'
    },
    rewards: [
      {
        id: 'consistency_pro_trial',
        type: 'subscription_days',
        subscriptionDays: 7,
        name: '7 Días Pro Gratis',
        description: 'Prueba todas las funciones Pro',
        icon: '👑',
        realCost: 3.02 // ~12.99/30 * 7
      },
      {
        id: 'consistency_sla_credits',
        type: 'currency',
        currency: 'SLA_CREDITS',
        amount: 3,
        name: '3 Créditos SLA+',
        description: 'Recordatorios extra para mantener el ritmo',
        icon: '⚡',
        realCost: 1.35 // 3 * 0.45
      },
      {
        id: 'consistency_templates',
        type: 'item',
        itemId: 'templates_starter',
        name: 'Pack Templates Starter',
        description: 'Plantillas básicas para mantener el hábito',
        icon: '📋',
        realCost: 0.50
      }
    ],
    userCanChoose: true,
    description: '7 días seguidos es solo el comienzo. ¡Elige tu recompensa!',
    icon: '🔥',
    rarity: 'rare'
  },
  {
    id: 'social_chest',
    name: 'Cofre Social',
    category: 'achievement',
    unlockCondition: {
      type: 'validations_received',
      threshold: 10,
      timeframe: '30d'
    },
    rewards: [
      {
        id: 'social_cred_boost',
        type: 'currency',
        currency: 'CRED',
        amount: 50,
        name: '+50 Cred Bonus',
        description: 'Reconocimiento por tu actividad social',
        icon: '⭐',
        realCost: 0
      },
      {
        id: 'social_celebration_frames',
        type: 'item',
        itemId: 'celebration_frames_social',
        name: 'Marcos de Celebración Social',
        description: 'Marcos especiales para compartir logros',
        icon: '🖼️',
        realCost: 0.75
      }
    ],
    userCanChoose: true,
    description: 'Tu red de validadores está creciendo. ¡Celebrémoslo!',
    icon: '👥',
    rarity: 'rare'
  },
  {
    id: 'mastery_chest',
    name: 'Cofre de Maestría',
    category: 'achievement',
    unlockCondition: {
      type: 'cred_earned',
      threshold: 500,
      timeframe: 'all_time'
    },
    rewards: [
      {
        id: 'mastery_pro_month',
        type: 'subscription_days',
        subscriptionDays: 30,
        name: '1 Mes Pro Completo',
        description: 'Mes completo de funciones Pro',
        icon: '👑',
        realCost: 12.99
      },
      {
        id: 'mastery_premium_templates',
        type: 'item',
        itemId: 'templates_mastery_bundle',
        name: 'Bundle Templates Maestría',
        description: 'Todas las categorías de templates premium',
        icon: '📚',
        realCost: 8.99
      },
      {
        id: 'mastery_exclusive_theme',
        type: 'item',
        itemId: 'theme_mastery_exclusive',
        name: 'Tema Exclusivo Maestría',
        description: 'Tema único solo para maestros',
        icon: '💎',
        realCost: 4.99
      }
    ],
    userCanChoose: true,
    description: '500 puntos de reputación te convierten en maestro',
    icon: '🏆',
    rarity: 'legendary'
  },
  {
    id: 'monthly_active_chest',
    name: 'Cofre de Actividad Mensual',
    category: 'seasonal',
    unlockCondition: {
      type: 'monthly_active',
      threshold: 20, // 20 días activos en el mes
      timeframe: '30d'
    },
    rewards: [
      {
        id: 'monthly_coins_bonus',
        type: 'currency',
        currency: 'IMPULSE_COINS',
        amount: 200,
        name: '200 IMPULSE Coins Bonus',
        description: 'Bonus por tu dedicación mensual',
        icon: '🪙',
        realCost: 0
      },
      {
        id: 'monthly_storage_upgrade',
        type: 'item',
        itemId: 'storage_monthly_bonus',
        name: '+2GB Storage Temporal',
        description: 'Storage extra válido por 30 días',
        icon: '📦',
        realCost: 0.10
      }
    ],
    userCanChoose: true,
    description: '20 días activos este mes. ¡Eres imparable!',
    icon: '📅',
    rarity: 'epic'
  }
]

// Presupuesto automático de cofres
export interface ChestBudgetControl {
  monthlyPool: number // Pool mensual total para cofres
  usedThisMonth: number // Ya gastado este mes
  remainingBudget: number // Presupuesto restante
  nextResetDate: Date // Cuándo se resetea el presupuesto
  maxCostPerChest: number // Coste máximo por cofre individual
  emergencyThreshold: number // Umbral de emergencia para pausar cofres
}

export class DeterministicChestService {
  // Verificar si usuario puede desbloquear cofres
  static async checkUnlockableChests(userId: string): Promise<DeterministicChest[]> {
    const unlockableChests: DeterministicChest[] = []
    const userStats = await this.getUserStats(userId)
    const alreadyUnlocked = await this.getUserUnlockedChests(userId)

    for (const chest of DETERMINISTIC_CHESTS) {
      // Skip si ya fue desbloqueado
      if (alreadyUnlocked.includes(chest.id)) continue

      // Verificar condición de desbloqueo
      if (await this.checkUnlockCondition(chest.unlockCondition, userStats)) {
        // Verificar presupuesto disponible
        if (await this.canAffordChest(chest)) {
          unlockableChests.push(chest)
        }
      }
    }

    return unlockableChests
  }

  // Usuario elige recompensa de cofre
  static async claimChestReward(
    userId: string,
    chestId: string,
    selectedRewardId: string
  ): Promise<{ success: boolean; error?: string; transaction?: any }> {
    const chest = DETERMINISTIC_CHESTS.find(c => c.id === chestId)
    if (!chest) return { success: false, error: 'Chest not found' }

    const reward = chest.rewards.find(r => r.id === selectedRewardId)
    if (!reward) return { success: false, error: 'Reward not found' }

    // Verificar que el cofre está disponible
    const unlockableChests = await this.checkUnlockableChests(userId)
    if (!unlockableChests.find(c => c.id === chestId)) {
      return { success: false, error: 'Chest not available' }
    }

    // Verificar presupuesto final
    if (!(await this.canAffordReward(reward))) {
      return { success: false, error: 'Budget exceeded' }
    }

    // Procesar recompensa
    const transaction = await this.processChestReward(userId, chest, reward)

    // Marcar cofre como reclamado
    await this.markChestAsClaimed(userId, chestId, selectedRewardId)

    // Actualizar presupuesto usado
    await this.updateUsedBudget(reward.realCost)

    return { success: true, transaction }
  }

  // Control de presupuesto automático (guardrail crítico)
  static async updateMonthlyChestBudget(
    monthlyRevenue: number,
    monthlyNetProfit: number
  ): Promise<ChestBudgetControl> {
    // Usar la fórmula del guardrail económico
    const totalRewardsPool = calculateMonthlyRewardsPool(monthlyRevenue, monthlyNetProfit)

    // 40% del pool total para cofres, 60% para otros rewards
    const chestBudget = totalRewardsPool * 0.40

    const budgetControl: ChestBudgetControl = {
      monthlyPool: chestBudget,
      usedThisMonth: await this.getCurrentMonthUsage(),
      remainingBudget: chestBudget - await this.getCurrentMonthUsage(),
      nextResetDate: this.getNextMonthFirstDay(),
      maxCostPerChest: chestBudget * 0.15, // Máximo 15% del budget en un solo cofre
      emergencyThreshold: chestBudget * 0.85 // Parar cofres al 85% del budget
    }

    return budgetControl
  }

  // Verificaciones privadas
  private static async checkUnlockCondition(
    condition: DeterministicChest['unlockCondition'],
    userStats: any
  ): Promise<boolean> {
    switch (condition.type) {
      case 'challenge_completed':
        return userStats.challengesCompleted >= condition.threshold
      case 'streak_days':
        return userStats.currentStreak >= condition.threshold
      case 'validations_received':
        return userStats.validationsReceived >= condition.threshold
      case 'cred_earned':
        return userStats.totalCred >= condition.threshold
      case 'monthly_active':
        return userStats.activeDaysThisMonth >= condition.threshold
      default:
        return false
    }
  }

  private static async canAffordChest(chest: DeterministicChest): Promise<boolean> {
    const budgetControl = await this.getCurrentBudgetControl()
    const maxRewardCost = Math.max(...chest.rewards.map(r => r.realCost))

    return budgetControl.remainingBudget >= maxRewardCost &&
           budgetControl.usedThisMonth < budgetControl.emergencyThreshold
  }

  private static async canAffordReward(reward: ChestReward): Promise<boolean> {
    const budgetControl = await this.getCurrentBudgetControl()
    return budgetControl.remainingBudget >= reward.realCost
  }

  private static async processChestReward(
    userId: string,
    chest: DeterministicChest,
    reward: ChestReward
  ): Promise<any> {
    switch (reward.type) {
      case 'currency':
        if (reward.currency && reward.amount) {
          return await EconomyService.earnCurrency(userId, `CHEST_REWARD_${chest.id}`)
        }
        break
      case 'subscription_days':
        if (reward.subscriptionDays) {
          return await this.grantSubscriptionDays(userId, reward.subscriptionDays)
        }
        break
      case 'item':
        if (reward.itemId) {
          return await this.grantItem(userId, reward.itemId)
        }
        break
      case 'feature_unlock':
        if (reward.featureId) {
          return await this.unlockFeature(userId, reward.featureId)
        }
        break
    }
    return null
  }

  // Mocks para implementación futura
  private static async getUserStats(userId: string): Promise<any> {
    // Implementar estadísticas reales del usuario
    return {
      challengesCompleted: 0,
      currentStreak: 0,
      validationsReceived: 0,
      totalCred: 0,
      activeDaysThisMonth: 0
    }
  }

  private static async getUserUnlockedChests(userId: string): Promise<string[]> {
    // Retornar IDs de cofres ya desbloqueados
    return []
  }

  private static async getCurrentBudgetControl(): Promise<ChestBudgetControl> {
    // Implementar control de presupuesto real
    return {
      monthlyPool: 1000,
      usedThisMonth: 0,
      remainingBudget: 1000,
      nextResetDate: new Date(),
      maxCostPerChest: 150,
      emergencyThreshold: 850
    }
  }

  private static async getCurrentMonthUsage(): Promise<number> {
    return 0
  }

  private static getNextMonthFirstDay(): Date {
    const now = new Date()
    return new Date(now.getFullYear(), now.getMonth() + 1, 1)
  }

  private static async markChestAsClaimed(userId: string, chestId: string, rewardId: string): Promise<void> {
    // Implementar en BD
  }

  private static async updateUsedBudget(cost: number): Promise<void> {
    // Actualizar presupuesto usado
  }

  private static async grantSubscriptionDays(userId: string, days: number): Promise<any> {
    // Implementar concesión de días Pro
    return null
  }

  private static async grantItem(userId: string, itemId: string): Promise<any> {
    // Implementar concesión de items
    return null
  }

  private static async unlockFeature(userId: string, featureId: string): Promise<any> {
    // Implementar desbloqueo de funciones
    return null
  }
}

export default DeterministicChestService
