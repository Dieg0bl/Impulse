// Automated Alert System for IMPULSE LEAN v1 - Phase 11
// Complete alerting system with configurable rules and notifications

import { Alert } from '../services/observability'

interface AlertRule {
  id: string
  name: string
  description: string
  enabled: boolean
  metric: string
  condition: 'greater_than' | 'less_than' | 'equals' | 'not_equals'
  threshold: number
  timeWindow: number // in minutes
  severity: 'low' | 'medium' | 'high' | 'critical'
  cooldown: number // in minutes
  actions: AlertAction[]
  metadata: Record<string, any>
}

interface AlertAction {
  type: 'email' | 'slack' | 'webhook' | 'sms' | 'pagerduty'
  target: string
  template?: string
  enabled: boolean
}

interface AlertingConfig {
  rules: AlertRule[]
  channels: {
    email: {
      enabled: boolean
      smtpHost: string
      smtpPort: number
      username: string
      from: string
      templates: Record<string, string>
    }
    slack: {
      enabled: boolean
      webhookUrl: string
      channel: string
      username: string
    }
    webhook: {
      enabled: boolean
      url: string
      headers: Record<string, string>
    }
    sms: {
      enabled: boolean
      provider: 'twilio' | 'aws_sns'
      apiKey: string
      from: string
    }
    pagerDuty: {
      enabled: boolean
      integrationKey: string
      severity: string
    }
  }
}

class AlertingService {
  private config: AlertingConfig
  private readonly baseUrl = '/api/v1/alerts'
  private activeAlerts: Map<string, Date> = new Map()
  private cooldownTimers: Map<string, NodeJS.Timeout> = new Map()

  constructor(config: AlertingConfig) {
    this.config = config
    this.initializeAlerts()
  }

  // =============================================================================
  // ALERT RULE MANAGEMENT
  // =============================================================================

  async createAlertRule(rule: Omit<AlertRule, 'id'>): Promise<AlertRule> {
    try {
      const response = await fetch(`${this.baseUrl}/rules`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        },
        body: JSON.stringify(rule)
      })

      if (!response.ok) {
        throw new Error('Failed to create alert rule')
      }

      const newRule = await response.json()
      this.config.rules.push(newRule)
      return newRule
    } catch (error) {
      console.error('Error creating alert rule:', error)
      throw error
    }
  }

  async updateAlertRule(ruleId: string, updates: Partial<AlertRule>): Promise<AlertRule> {
    try {
      const response = await fetch(`${this.baseUrl}/rules/${ruleId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        },
        body: JSON.stringify(updates)
      })

      if (!response.ok) {
        throw new Error('Failed to update alert rule')
      }

      const updatedRule = await response.json()
      const ruleIndex = this.config.rules.findIndex(r => r.id === ruleId)
      if (ruleIndex !== -1) {
        this.config.rules[ruleIndex] = updatedRule
      }
      return updatedRule
    } catch (error) {
      console.error('Error updating alert rule:', error)
      throw error
    }
  }

  async deleteAlertRule(ruleId: string): Promise<void> {
    try {
      const response = await fetch(`${this.baseUrl}/rules/${ruleId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        }
      })

      if (!response.ok) {
        throw new Error('Failed to delete alert rule')
      }

      this.config.rules = this.config.rules.filter(r => r.id !== ruleId)
      
      // Clear any cooldown timers for this rule
      if (this.cooldownTimers.has(ruleId)) {
        clearTimeout(this.cooldownTimers.get(ruleId)!)
        this.cooldownTimers.delete(ruleId)
      }
    } catch (error) {
      console.error('Error deleting alert rule:', error)
      throw error
    }
  }

  // =============================================================================
  // ALERT EVALUATION
  // =============================================================================

  async evaluateMetric(metricName: string, value: number, timestamp: Date): Promise<void> {
    const relevantRules = this.config.rules.filter(
      rule => rule.enabled && rule.metric === metricName
    )

    for (const rule of relevantRules) {
      await this.evaluateRule(rule, value, timestamp)
    }
  }

  private async evaluateRule(rule: AlertRule, value: number, timestamp: Date): Promise<void> {
    const triggered = this.checkCondition(rule.condition, value, rule.threshold)
    
    if (triggered) {
      // Check if we're in cooldown period
      if (this.isInCooldown(rule.id)) {
        return
      }

      // Create and send alert
      const alert: Omit<Alert, 'id'> = {
        type: this.severityToType(rule.severity),
        title: rule.name,
        message: this.generateAlertMessage(rule, value),
        timestamp: timestamp.toISOString(),
        acknowledged: false,
        source: 'Automated Alert System',
        severity: rule.severity,
        metadata: {
          ruleId: rule.id,
          metricName: rule.metric,
          value,
          threshold: rule.threshold,
          condition: rule.condition,
          ...rule.metadata
        }
      }

      await this.sendAlert(alert, rule.actions)
      this.startCooldown(rule.id, rule.cooldown)
    }
  }

  private checkCondition(condition: string, value: number, threshold: number): boolean {
    switch (condition) {
      case 'greater_than':
        return value > threshold
      case 'less_than':
        return value < threshold
      case 'equals':
        return value === threshold
      case 'not_equals':
        return value !== threshold
      default:
        return false
    }
  }

  private isInCooldown(ruleId: string): boolean {
    return this.cooldownTimers.has(ruleId)
  }

  private startCooldown(ruleId: string, cooldownMinutes: number): void {
    const timeout = setTimeout(() => {
      this.cooldownTimers.delete(ruleId)
    }, cooldownMinutes * 60 * 1000)

    this.cooldownTimers.set(ruleId, timeout)
  }

  private severityToType(severity: string): 'error' | 'warning' | 'info' {
    switch (severity) {
      case 'critical':
      case 'high':
        return 'error'
      case 'medium':
        return 'warning'
      default:
        return 'info'
    }
  }

  private generateAlertMessage(rule: AlertRule, value: number): string {
    return `${rule.description}\nCurrent value: ${value}\nThreshold: ${rule.threshold}\nCondition: ${rule.condition}`
  }

  // =============================================================================
  // ALERT DELIVERY
  // =============================================================================

  private async sendAlert(alert: Omit<Alert, 'id'>, actions: AlertAction[]): Promise<void> {
    for (const action of actions.filter(a => a.enabled)) {
      try {
        switch (action.type) {
          case 'email':
            await this.sendEmailAlert(alert, action)
            break
          case 'slack':
            await this.sendSlackAlert(alert, action)
            break
          case 'webhook':
            await this.sendWebhookAlert(alert, action)
            break
          case 'sms':
            await this.sendSMSAlert(alert, action)
            break
          case 'pagerduty':
            await this.sendPagerDutyAlert(alert, action)
            break
        }
      } catch (error) {
        console.error(`Failed to send ${action.type} alert:`, error)
      }
    }
  }

  private async sendEmailAlert(alert: Omit<Alert, 'id'>, action: AlertAction): Promise<void> {
    if (!this.config.channels.email.enabled) return

    const response = await fetch(`${this.baseUrl}/send/email`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('authToken')}`
      },
      body: JSON.stringify({
        to: action.target,
        subject: `IMPULSE Alert: ${alert.title}`,
        body: this.formatEmailBody(alert),
        template: action.template
      })
    })

    if (!response.ok) {
      throw new Error('Failed to send email alert')
    }
  }

  private async sendSlackAlert(alert: Omit<Alert, 'id'>, action: AlertAction): Promise<void> {
    if (!this.config.channels.slack.enabled) return

    const slackMessage = {
      channel: action.target,
      username: this.config.channels.slack.username,
      text: `🚨 *${alert.title}*`,
      attachments: [
        {
          color: this.getSeverityColor(alert.severity),
          fields: [
            {
              title: 'Message',
              value: alert.message,
              short: false
            },
            {
              title: 'Severity',
              value: alert.severity.toUpperCase(),
              short: true
            },
            {
              title: 'Source',
              value: alert.source,
              short: true
            },
            {
              title: 'Time',
              value: new Date(alert.timestamp).toLocaleString(),
              short: false
            }
          ]
        }
      ]
    }

    const response = await fetch(this.config.channels.slack.webhookUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(slackMessage)
    })

    if (!response.ok) {
      throw new Error('Failed to send Slack alert')
    }
  }

  private async sendWebhookAlert(alert: Omit<Alert, 'id'>, action: AlertAction): Promise<void> {
    if (!this.config.channels.webhook.enabled) return

    const response = await fetch(action.target, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...this.config.channels.webhook.headers
      },
      body: JSON.stringify({
        alert,
        timestamp: new Date().toISOString(),
        source: 'impulse-alerting-system'
      })
    })

    if (!response.ok) {
      throw new Error('Failed to send webhook alert')
    }
  }

  private async sendSMSAlert(alert: Omit<Alert, 'id'>, action: AlertAction): Promise<void> {
    if (!this.config.channels.sms.enabled) return

    const response = await fetch(`${this.baseUrl}/send/sms`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('authToken')}`
      },
      body: JSON.stringify({
        to: action.target,
        message: `IMPULSE Alert: ${alert.title}\n${alert.message}`,
        provider: this.config.channels.sms.provider
      })
    })

    if (!response.ok) {
      throw new Error('Failed to send SMS alert')
    }
  }

  private async sendPagerDutyAlert(alert: Omit<Alert, 'id'>, action: AlertAction): Promise<void> {
    if (!this.config.channels.pagerDuty.enabled) return

    const pagerDutyEvent = {
      routing_key: this.config.channels.pagerDuty.integrationKey,
      event_action: 'trigger',
      payload: {
        summary: alert.title,
        source: alert.source,
        severity: alert.severity,
        timestamp: alert.timestamp,
        custom_details: alert.metadata
      }
    }

    const response = await fetch('https://events.pagerduty.com/v2/enqueue', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(pagerDutyEvent)
    })

    if (!response.ok) {
      throw new Error('Failed to send PagerDuty alert')
    }
  }

  // =============================================================================
  // HELPER METHODS
  // =============================================================================

  private formatEmailBody(alert: Omit<Alert, 'id'>): string {
    return `
Alert: ${alert.title}

Message: ${alert.message}

Severity: ${alert.severity.toUpperCase()}
Source: ${alert.source}
Time: ${new Date(alert.timestamp).toLocaleString()}

Metadata:
${JSON.stringify(alert.metadata, null, 2)}

---
IMPULSE Monitoring System
    `.trim()
  }

  private getSeverityColor(severity: string): string {
    switch (severity) {
      case 'critical':
        return 'danger'
      case 'high':
        return 'warning'
      case 'medium':
        return 'warning'
      case 'low':
        return 'good'
      default:
        return 'good'
    }
  }

  private initializeAlerts(): void {
    // Set up default alert rules if none exist
    if (this.config.rules.length === 0) {
      this.setupDefaultAlertRules()
    }
  }

  private setupDefaultAlertRules(): void {
    const defaultRules: AlertRule[] = [
      {
        id: 'high_error_rate',
        name: 'High Error Rate',
        description: 'API error rate exceeds 5%',
        enabled: true,
        metric: 'error_rate',
        condition: 'greater_than',
        threshold: 5,
        timeWindow: 5,
        severity: 'critical',
        cooldown: 15,
        actions: [
          {
            type: 'email',
            target: 'alerts@impulse-lean.com',
            enabled: true
          },
          {
            type: 'slack',
            target: '#alerts',
            enabled: true
          }
        ],
        metadata: {}
      },
      {
        id: 'high_response_time',
        name: 'High Response Time',
        description: 'API response time P95 exceeds 500ms',
        enabled: true,
        metric: 'response_time_p95',
        condition: 'greater_than',
        threshold: 500,
        timeWindow: 10,
        severity: 'high',
        cooldown: 10,
        actions: [
          {
            type: 'email',
            target: 'performance@impulse-lean.com',
            enabled: true
          }
        ],
        metadata: {}
      },
      {
        id: 'gdpr_request_overdue',
        name: 'GDPR Request Overdue',
        description: 'GDPR data subject request pending for more than 72 hours',
        enabled: true,
        metric: 'gdpr_requests_overdue',
        condition: 'greater_than',
        threshold: 0,
        timeWindow: 60,
        severity: 'critical',
        cooldown: 60,
        actions: [
          {
            type: 'email',
            target: 'legal@impulse-lean.com',
            enabled: true
          },
          {
            type: 'pagerduty',
            target: 'legal-team',
            enabled: true
          }
        ],
        metadata: {}
      },
      {
        id: 'stripe_webhook_failure',
        name: 'Stripe Webhook Failure',
        description: 'Stripe webhook delivery failed multiple times',
        enabled: true,
        metric: 'stripe_webhook_failures',
        condition: 'greater_than',
        threshold: 2,
        timeWindow: 30,
        severity: 'high',
        cooldown: 30,
        actions: [
          {
            type: 'email',
            target: 'finance@impulse-lean.com',
            enabled: true
          },
          {
            type: 'slack',
            target: '#finance',
            enabled: true
          }
        ],
        metadata: {}
      },
      {
        id: 'high_fraud_score',
        name: 'High Fraud Score',
        description: 'Multiple transactions with high fraud scores detected',
        enabled: true,
        metric: 'fraud_score_alerts',
        condition: 'greater_than',
        threshold: 5,
        timeWindow: 60,
        severity: 'critical',
        cooldown: 30,
        actions: [
          {
            type: 'email',
            target: 'security@impulse-lean.com',
            enabled: true
          },
          {
            type: 'pagerduty',
            target: 'security-team',
            enabled: true
          }
        ],
        metadata: {}
      }
    ]

    this.config.rules.push(...defaultRules)
  }

  // =============================================================================
  // PUBLIC API
  // =============================================================================

  getAlertRules(): AlertRule[] {
    return this.config.rules
  }

  getAlertingConfig(): AlertingConfig {
    return this.config
  }

  async updateConfig(config: Partial<AlertingConfig>): Promise<void> {
    this.config = { ...this.config, ...config }
    
    // Persist config to backend
    try {
      await fetch(`${this.baseUrl}/config`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        },
        body: JSON.stringify(this.config)
      })
    } catch (error) {
      console.error('Failed to update alerting config:', error)
      throw error
    }
  }

  async testAlert(action: AlertAction): Promise<void> {
    const testAlert: Omit<Alert, 'id'> = {
      type: 'info',
      title: 'Test Alert',
      message: 'This is a test alert from IMPULSE monitoring system',
      timestamp: new Date().toISOString(),
      acknowledged: false,
      source: 'Test System',
      severity: 'low',
      metadata: { test: true }
    }

    await this.sendAlert(testAlert, [action])
  }

  cleanup(): void {
    // Clear all cooldown timers
    for (const timer of this.cooldownTimers.values()) {
      clearTimeout(timer)
    }
    this.cooldownTimers.clear()
  }
}

// =============================================================================
// EXPORT
// =============================================================================

export { AlertingService, type AlertRule, type AlertAction, type AlertingConfig }
