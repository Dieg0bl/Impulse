// Servicio de Métricas y Analytics Business para IMPULSE
// KPIs, cohort analysis, LTV prediction, engagement, revenue tracking

export interface BusinessKPI {
  name: string
  description: string
  value: number | string | null
  trend: 'up' | 'down' | 'stable' | null
  unit?: string
  lastUpdated: Date
}

export interface CohortAnalysis {
  cohortName: string
  users: string[]
  startDate: Date
  retentionRates: number[] // % por día/semana/mes
  conversionRates: number[]
  avgLTV: number
}

export interface LTVPrediction {
  userId: string
  predictedLTV: number
  confidence: number // 0-1
  factors: string[]
}

export interface EngagementMetric {
  metric: string
  value: number
  period: string
  trend: 'up' | 'down' | 'stable'
}

export interface RevenueMetric {
  totalRevenue: number
  arpu: number // ingreso promedio por usuario
  mrr: number // monthly recurring revenue
  arr: number // annual recurring revenue
  currency: string
  period: string
}

export class AnalyticsService {
  // KPIs principales
  static async getBusinessKPIs(): Promise<BusinessKPI[]> {
    return [
      {
        name: 'Active Users',
        description: 'Usuarios activos mensuales',
        value: 12000,
        trend: 'up',
        unit: 'users',
        lastUpdated: new Date()
      },
      {
        name: 'Churn Rate',
        description: 'Porcentaje de usuarios que abandonan',
        value: 0.045,
        trend: 'down',
        unit: '%',
        lastUpdated: new Date()
      },
      {
        name: 'Conversion Rate',
        description: 'Porcentaje de free → premium',
        value: 0.12,
        trend: 'up',
        unit: '%',
        lastUpdated: new Date()
      },
      {
        name: 'LTV',
        description: 'Valor de vida promedio usuario',
        value: 48.5,
        trend: 'up',
        unit: 'USD',
        lastUpdated: new Date()
      }
    ]
  }

  // Cohort analysis
  static async getCohortAnalysis(period: 'monthly' | 'weekly'): Promise<CohortAnalysis[]> {
    return [
      {
        cohortName: 'Enero 2025',
        users: ['user1', 'user2'],
        startDate: new Date('2025-01-01'),
        retentionRates: [1, 0.7, 0.5, 0.4],
        conversionRates: [0.1, 0.15, 0.2, 0.22],
        avgLTV: 35.2
      },
      {
        cohortName: 'Febrero 2025',
        users: ['user3', 'user4'],
        startDate: new Date('2025-02-01'),
        retentionRates: [1, 0.68, 0.52, 0.41],
        conversionRates: [0.09, 0.13, 0.19, 0.21],
        avgLTV: 37.8
      }
    ]
  }

  // Predicción de LTV
  static async predictLTV(userId: string): Promise<LTVPrediction> {
    return {
      userId,
      predictedLTV: 52.3,
      confidence: 0.87,
      factors: ['engagement_high', 'premium_user', 'low_churn_risk']
    }
  }

  // Engagement metrics
  static async getEngagementMetrics(period: 'daily' | 'weekly' | 'monthly'): Promise<EngagementMetric[]> {
    return [
      {
        metric: 'Session Length',
        value: 38,
        period,
        trend: 'up'
      },
      {
        metric: 'Sessions per User',
        value: 4.2,
        period,
        trend: 'stable'
      },
      {
        metric: 'Feature Usage Rate',
        value: 0.78,
        period,
        trend: 'up'
      }
    ]
  }

  // Revenue tracking
  static async getRevenueMetrics(period: 'monthly' | 'annual'): Promise<RevenueMetric> {
    return {
      totalRevenue: 18500,
      arpu: 1.54,
      mrr: 18500,
      arr: 222000,
      currency: 'USD',
      period
    }
  }

  // Mock para tracking eventos custom
  static async trackEvent(event: string, data: any): Promise<void> {
    // Aquí se enviaría a un sistema de analytics real (Mixpanel, Amplitude, etc)
    console.log(`[Analytics] Event: ${event}`, data)
  }
}

export default AnalyticsService
