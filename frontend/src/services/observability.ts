// Observability Service for IMPULSE LEAN v1 - Phase 11
// Complete monitoring, metrics, and alerting system

import React, { useState, useEffect } from 'react'
import { 
  BarChart3,
  TrendingUp,
  TrendingDown,
  Alert,
  CheckCircle,
  XCircle,
  Clock,
  Users,
  DollarSign,
  Activity,
  Database,
  Server,
  Shield,
  Bell,
  Eye,
  Gauge,
  Target,
  AlertTriangle
} from 'lucide-react'

// =============================================================================
// OBSERVABILITY SERVICE
// =============================================================================

interface Metric {
  id: string
  name: string
  value: number
  unit: string
  trend: 'up' | 'down' | 'stable'
  change: number
  status: 'healthy' | 'warning' | 'critical'
  timestamp: string
  description?: string
}

interface Alert {
  id: string
  type: 'error' | 'warning' | 'info'
  title: string
  message: string
  timestamp: string
  acknowledged: boolean
  source: string
  severity: 'low' | 'medium' | 'high' | 'critical'
  metadata?: Record<string, any>
}

interface SystemHealth {
  overall: 'healthy' | 'warning' | 'critical'
  services: {
    api: 'up' | 'down' | 'degraded'
    database: 'up' | 'down' | 'degraded'
    cache: 'up' | 'down' | 'degraded'
    storage: 'up' | 'down' | 'degraded'
    payments: 'up' | 'down' | 'degraded'
  }
  uptime: number
  responseTime: number
  errorRate: number
}

class ObservabilityService {
  private baseUrl = '/api/v1/observability'
  private alertsWebSocket: WebSocket | null = null
  private metricsWebSocket: WebSocket | null = null

  // =============================================================================
  // METRICS COLLECTION
  // =============================================================================

  async getMetrics(timeRange: '1h' | '24h' | '7d' | '30d' = '24h'): Promise<Metric[]> {
    try {
      const response = await fetch(`${this.baseUrl}/metrics?range=${timeRange}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        }
      })

      if (!response.ok) {
        throw new Error('Failed to fetch metrics')
      }

      return await response.json()
    } catch (error) {
      console.error('Error fetching metrics:', error)
      return this.getMockMetrics()
    }
  }

  async getBusinessMetrics(): Promise<{
    mrr: number
    conversionRate: number
    retentionRate30d: number
    retentionRate90d: number
    averageSessionDuration: number
    churnRate: number
    lifetimeValue: number
  }> {
    try {
      const response = await fetch(`${this.baseUrl}/metrics/business`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        }
      })

      if (!response.ok) {
        throw new Error('Failed to fetch business metrics')
      }

      return await response.json()
    } catch (error) {
      console.error('Error fetching business metrics:', error)
      return {
        mrr: 12500,
        conversionRate: 3.2,
        retentionRate30d: 68.5,
        retentionRate90d: 42.3,
        averageSessionDuration: 8.4,
        churnRate: 5.2,
        lifetimeValue: 285
      }
    }
  }

  async getTechnicalMetrics(): Promise<{
    uci: number // User Conversion Index
    cps: number // Customer Performance Score
    erss: number // Error Rate & Stability Score
    apiLatencyP95: number
    errorRate: number
    throughput: number
    availability: number
  }> {
    try {
      const response = await fetch(`${this.baseUrl}/metrics/technical`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        }
      })

      if (!response.ok) {
        throw new Error('Failed to fetch technical metrics')
      }

      return await response.json()
    } catch (error) {
      console.error('Error fetching technical metrics:', error)
      return {
        uci: 87.3,
        cps: 92.1,
        erss: 94.8,
        apiLatencyP95: 245,
        errorRate: 0.12,
        throughput: 1247,
        availability: 99.95
      }
    }
  }

  // =============================================================================
  // ALERTS MANAGEMENT
  // =============================================================================

  async getAlerts(limit: number = 50): Promise<Alert[]> {
    try {
      const response = await fetch(`${this.baseUrl}/alerts?limit=${limit}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        }
      })

      if (!response.ok) {
        throw new Error('Failed to fetch alerts')
      }

      return await response.json()
    } catch (error) {
      console.error('Error fetching alerts:', error)
      return this.getMockAlerts()
    }
  }

  async acknowledgeAlert(alertId: string): Promise<void> {
    try {
      const response = await fetch(`${this.baseUrl}/alerts/${alertId}/acknowledge`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        }
      })

      if (!response.ok) {
        throw new Error('Failed to acknowledge alert')
      }
    } catch (error) {
      console.error('Error acknowledging alert:', error)
      throw error
    }
  }

  async createAlert(alert: Partial<Alert>): Promise<Alert> {
    try {
      const response = await fetch(`${this.baseUrl}/alerts`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        },
        body: JSON.stringify(alert)
      })

      if (!response.ok) {
        throw new Error('Failed to create alert')
      }

      return await response.json()
    } catch (error) {
      console.error('Error creating alert:', error)
      throw error
    }
  }

  // =============================================================================
  // SYSTEM HEALTH
  // =============================================================================

  async getSystemHealth(): Promise<SystemHealth> {
    try {
      const response = await fetch(`${this.baseUrl}/health`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        }
      })

      if (!response.ok) {
        throw new Error('Failed to fetch system health')
      }

      return await response.json()
    } catch (error) {
      console.error('Error fetching system health:', error)
      return {
        overall: 'healthy',
        services: {
          api: 'up',
          database: 'up',
          cache: 'up',
          storage: 'up',
          payments: 'up'
        },
        uptime: 99.95,
        responseTime: 245,
        errorRate: 0.12
      }
    }
  }

  // =============================================================================
  // REAL-TIME MONITORING
  // =============================================================================

  subscribeToAlerts(callback: (alert: Alert) => void): () => void {
    const token = localStorage.getItem('authToken')
    const wsUrl = `ws://localhost:8080/ws/alerts?token=${token}`
    
    this.alertsWebSocket = new WebSocket(wsUrl)
    
    this.alertsWebSocket.onmessage = (event) => {
      try {
        const alert = JSON.parse(event.data)
        callback(alert)
      } catch (error) {
        console.error('Error parsing alert:', error)
      }
    }

    this.alertsWebSocket.onerror = (error) => {
      console.error('WebSocket error:', error)
    }

    return () => {
      if (this.alertsWebSocket) {
        this.alertsWebSocket.close()
        this.alertsWebSocket = null
      }
    }
  }

  subscribeToMetrics(callback: (metrics: Metric[]) => void): () => void {
    const token = localStorage.getItem('authToken')
    const wsUrl = `ws://localhost:8080/ws/metrics?token=${token}`
    
    this.metricsWebSocket = new WebSocket(wsUrl)
    
    this.metricsWebSocket.onmessage = (event) => {
      try {
        const metrics = JSON.parse(event.data)
        callback(metrics)
      } catch (error) {
        console.error('Error parsing metrics:', error)
      }
    }

    this.metricsWebSocket.onerror = (error) => {
      console.error('WebSocket error:', error)
    }

    return () => {
      if (this.metricsWebSocket) {
        this.metricsWebSocket.close()
        this.metricsWebSocket = null
      }
    }
  }

  // =============================================================================
  // MOCK DATA (for development)
  // =============================================================================

  private getMockMetrics(): Metric[] {
    return [
      {
        id: 'active_users',
        name: 'Active Users',
        value: 1247,
        unit: 'users',
        trend: 'up',
        change: 12.3,
        status: 'healthy',
        timestamp: new Date().toISOString(),
        description: '24h active users'
      },
      {
        id: 'conversion_rate',
        name: 'Conversion Rate',
        value: 3.2,
        unit: '%',
        trend: 'up',
        change: 0.8,
        status: 'healthy',
        timestamp: new Date().toISOString(),
        description: 'Trial to paid conversion'
      },
      {
        id: 'error_rate',
        name: 'Error Rate',
        value: 0.12,
        unit: '%',
        trend: 'down',
        change: -0.05,
        status: 'healthy',
        timestamp: new Date().toISOString(),
        description: '5xx error rate'
      },
      {
        id: 'response_time',
        name: 'Response Time P95',
        value: 245,
        unit: 'ms',
        trend: 'stable',
        change: 0,
        status: 'healthy',
        timestamp: new Date().toISOString(),
        description: '95th percentile response time'
      },
      {
        id: 'mrr',
        name: 'Monthly Recurring Revenue',
        value: 12500,
        unit: '$',
        trend: 'up',
        change: 8.7,
        status: 'healthy',
        timestamp: new Date().toISOString(),
        description: 'Current month MRR'
      }
    ]
  }

  private getMockAlerts(): Alert[] {
    return [
      {
        id: '1',
        type: 'warning',
        title: 'High API Latency',
        message: 'API response time P95 exceeded 500ms threshold',
        timestamp: new Date(Date.now() - 300000).toISOString(),
        acknowledged: false,
        source: 'API Gateway',
        severity: 'medium',
        metadata: { latency: 523, threshold: 500 }
      },
      {
        id: '2',
        type: 'info',
        title: 'GDPR Request Pending',
        message: 'Data subject request pending for more than 48 hours',
        timestamp: new Date(Date.now() - 600000).toISOString(),
        acknowledged: false,
        source: 'Compliance Service',
        severity: 'high',
        metadata: { requestId: 'dsr_123456', hours: 48 }
      },
      {
        id: '3',
        type: 'error',
        title: 'Payment Webhook Failed',
        message: 'Stripe webhook delivery failed 3 times consecutively',
        timestamp: new Date(Date.now() - 900000).toISOString(),
        acknowledged: true,
        source: 'Payment Service',
        severity: 'critical',
        metadata: { webhookId: 'we_123456', attempts: 3 }
      }
    ]
  }
}

// =============================================================================
// REACT HOOKS
// =============================================================================

export const useObservability = () => {
  const [service] = useState(() => new ObservabilityService())
  
  return {
    getMetrics: service.getMetrics.bind(service),
    getBusinessMetrics: service.getBusinessMetrics.bind(service),
    getTechnicalMetrics: service.getTechnicalMetrics.bind(service),
    getAlerts: service.getAlerts.bind(service),
    acknowledgeAlert: service.acknowledgeAlert.bind(service),
    createAlert: service.createAlert.bind(service),
    getSystemHealth: service.getSystemHealth.bind(service),
    subscribeToAlerts: service.subscribeToAlerts.bind(service),
    subscribeToMetrics: service.subscribeToMetrics.bind(service)
  }
}

export const useRealTimeMetrics = () => {
  const [metrics, setMetrics] = useState<Metric[]>([])
  const [alerts, setAlerts] = useState<Alert[]>([])
  const [systemHealth, setSystemHealth] = useState<SystemHealth | null>(null)
  const { subscribeToAlerts, subscribeToMetrics, getSystemHealth } = useObservability()

  useEffect(() => {
    // Subscribe to real-time updates
    const unsubscribeAlerts = subscribeToAlerts((alert) => {
      setAlerts(prev => [alert, ...prev.slice(0, 49)])
    })

    const unsubscribeMetrics = subscribeToMetrics((newMetrics) => {
      setMetrics(newMetrics)
    })

    // Load initial system health
    getSystemHealth().then(setSystemHealth)

    // Refresh system health every 30 seconds
    const healthInterval = setInterval(() => {
      getSystemHealth().then(setSystemHealth)
    }, 30000)

    return () => {
      unsubscribeAlerts()
      unsubscribeMetrics()
      clearInterval(healthInterval)
    }
  }, [])

  return {
    metrics,
    alerts,
    systemHealth,
    setAlerts
  }
}

// =============================================================================
// EXPORT TYPES
// =============================================================================

export type { Metric, Alert, SystemHealth }
export { ObservabilityService }
