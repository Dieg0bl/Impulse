// Observability Dashboard for IMPULSE LEAN v1 - Phase 11
// Complete monitoring dashboard with real-time metrics and alerts

import React, { useState, useEffect } from 'react'
import {
  BarChart3,
  TrendingUp,
  TrendingDown,
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
  AlertTriangle,
  Wifi,
  WifiOff,
  Zap,
  Calendar
} from 'lucide-react'
import { useObservability, useRealTimeMetrics, Metric, Alert, SystemHealth } from '../services/observability'

interface ObservabilityDashboardProps {
  className?: string
}

const ObservabilityDashboard: React.FC<ObservabilityDashboardProps> = ({ className = '' }) => {
  const { getMetrics, getBusinessMetrics, getTechnicalMetrics, acknowledgeAlert } = useObservability()
  const { metrics: realTimeMetrics, alerts, systemHealth, setAlerts } = useRealTimeMetrics()
  
  const [activeTab, setActiveTab] = useState<'overview' | 'business' | 'technical' | 'alerts' | 'health'>('overview')
  const [timeRange, setTimeRange] = useState<'1h' | '24h' | '7d' | '30d'>('24h')
  const [metrics, setMetrics] = useState<Metric[]>([])
  const [businessMetrics, setBusinessMetrics] = useState<any>(null)
  const [technicalMetrics, setTechnicalMetrics] = useState<any>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    loadData()
  }, [timeRange])

  const loadData = async () => {
    setIsLoading(true)
    try {
      const [metricsData, businessData, technicalData] = await Promise.all([
        getMetrics(timeRange),
        getBusinessMetrics(),
        getTechnicalMetrics()
      ])

      setMetrics(metricsData)
      setBusinessMetrics(businessData)
      setTechnicalMetrics(technicalData)
    } catch (error) {
      console.error('Failed to load dashboard data:', error)
    } finally {
      setIsLoading(false)
    }
  }

  const handleAcknowledgeAlert = async (alertId: string) => {
    try {
      await acknowledgeAlert(alertId)
      setAlerts(prev => prev.map(alert => 
        alert.id === alertId ? { ...alert, acknowledged: true } : alert
      ))
    } catch (error) {
      console.error('Failed to acknowledge alert:', error)
    }
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'healthy':
      case 'up':
        return 'text-green-600 bg-green-100'
      case 'warning':
      case 'degraded':
        return 'text-yellow-600 bg-yellow-100'
      case 'critical':
      case 'down':
        return 'text-red-600 bg-red-100'
      default:
        return 'text-gray-600 bg-gray-100'
    }
  }

  const getTrendIcon = (trend: string) => {
    switch (trend) {
      case 'up':
        return <TrendingUp className="w-4 h-4 text-green-600" />
      case 'down':
        return <TrendingDown className="w-4 h-4 text-red-600" />
      default:
        return <div className="w-4 h-4" />
    }
  }

  const getAlertIcon = (type: string) => {
    switch (type) {
      case 'error':
        return <XCircle className="w-5 h-5 text-red-500" />
      case 'warning':
        return <AlertTriangle className="w-5 h-5 text-yellow-500" />
      case 'info':
        return <CheckCircle className="w-5 h-5 text-blue-500" />
      default:
        return <Bell className="w-5 h-5 text-gray-500" />
    }
  }

  const formatValue = (value: number, unit: string) => {
    if (unit === '$') {
      return new Intl.NumberFormat('en-US', { 
        style: 'currency', 
        currency: 'USD' 
      }).format(value)
    }
    if (unit === '%') {
      return `${value.toFixed(1)}%`
    }
    if (unit === 'ms') {
      return `${value}ms`
    }
    return `${value.toLocaleString()} ${unit}`
  }

  return (
    <div className={`max-w-7xl mx-auto py-8 px-4 ${className}`}>
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Observability Dashboard</h1>
        <p className="text-gray-600">
          Real-time monitoring, metrics, and system health for IMPULSE LEAN v1
        </p>
      </div>

      {/* Time Range Selector */}
      <div className="mb-6">
        <div className="flex items-center space-x-4">
          <span className="text-sm font-medium text-gray-700">Time Range:</span>
          <div className="flex space-x-1">
            {(['1h', '24h', '7d', '30d'] as const).map((range) => (
              <button
                key={range}
                onClick={() => setTimeRange(range)}
                className={`px-3 py-1 text-xs font-medium rounded-md ${
                  timeRange === range
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                }`}
              >
                {range}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* Tab Navigation */}
      <div className="border-b border-gray-200 mb-6">
        <nav className="-mb-px flex space-x-8">
          {[
            { id: 'overview', label: 'Overview', icon: Eye },
            { id: 'business', label: 'Business', icon: Target },
            { id: 'technical', label: 'Technical', icon: Gauge },
            { id: 'alerts', label: 'Alerts', icon: Bell },
            { id: 'health', label: 'System Health', icon: Activity }
          ].map(({ id, label, icon: Icon }) => (
            <button
              key={id}
              onClick={() => setActiveTab(id as any)}
              className={`flex items-center py-2 px-1 border-b-2 font-medium text-sm ${
                activeTab === id
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
            >
              <Icon className="w-4 h-4 mr-2" />
              {label}
              {id === 'alerts' && alerts.filter(a => !a.acknowledged).length > 0 && (
                <span className="ml-2 px-2 py-0.5 text-xs bg-red-100 text-red-800 rounded-full">
                  {alerts.filter(a => !a.acknowledged).length}
                </span>
              )}
            </button>
          ))}
        </nav>
      </div>

      {/* Tab Content */}
      {activeTab === 'overview' && (
        <OverviewTab 
          metrics={metrics}
          systemHealth={systemHealth}
          alerts={alerts}
          isLoading={isLoading}
          formatValue={formatValue}
          getTrendIcon={getTrendIcon}
          getStatusColor={getStatusColor}
        />
      )}

      {activeTab === 'business' && (
        <BusinessTab 
          metrics={businessMetrics}
          isLoading={isLoading}
          formatValue={formatValue}
        />
      )}

      {activeTab === 'technical' && (
        <TechnicalTab 
          metrics={technicalMetrics}
          isLoading={isLoading}
          formatValue={formatValue}
        />
      )}

      {activeTab === 'alerts' && (
        <AlertsTab 
          alerts={alerts}
          onAcknowledge={handleAcknowledgeAlert}
          getAlertIcon={getAlertIcon}
        />
      )}

      {activeTab === 'health' && (
        <SystemHealthTab 
          health={systemHealth}
          getStatusColor={getStatusColor}
        />
      )}
    </div>
  )
}

// =============================================================================
// TAB COMPONENTS
// =============================================================================

interface OverviewTabProps {
  metrics: Metric[]
  systemHealth: SystemHealth | null
  alerts: Alert[]
  isLoading: boolean
  formatValue: (value: number, unit: string) => string
  getTrendIcon: (trend: string) => JSX.Element
  getStatusColor: (status: string) => string
}

const OverviewTab: React.FC<OverviewTabProps> = ({
  metrics,
  systemHealth,
  alerts,
  isLoading,
  formatValue,
  getTrendIcon,
  getStatusColor
}) => {
  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* Key Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {metrics.slice(0, 4).map((metric) => (
          <div key={metric.id} className="bg-white shadow rounded-lg p-6">
            <div className="flex items-center justify-between">
              <div className="flex-1">
                <p className="text-sm font-medium text-gray-500 mb-1">
                  {metric.name}
                </p>
                <p className="text-2xl font-semibold text-gray-900">
                  {formatValue(metric.value, metric.unit)}
                </p>
                {metric.change !== 0 && (
                  <div className="flex items-center mt-2">
                    {getTrendIcon(metric.trend)}
                    <span className={`text-sm ml-1 ${
                      metric.trend === 'up' ? 'text-green-600' : 
                      metric.trend === 'down' ? 'text-red-600' : 'text-gray-600'
                    }`}>
                      {Math.abs(metric.change)}%
                    </span>
                  </div>
                )}
              </div>
              <div className={`px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(metric.status)}`}>
                {metric.status}
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* System Status */}
      {systemHealth && (
        <div className="bg-white shadow rounded-lg p-6">
          <h3 className="text-lg font-medium text-gray-900 mb-4">System Status</h3>
          <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
            {Object.entries(systemHealth.services).map(([service, status]) => (
              <div key={service} className="text-center">
                <div className={`w-3 h-3 rounded-full mx-auto mb-2 ${
                  status === 'up' ? 'bg-green-500' : 
                  status === 'degraded' ? 'bg-yellow-500' : 'bg-red-500'
                }`} />
                <p className="text-sm font-medium text-gray-900 capitalize">{service}</p>
                <p className={`text-xs ${getStatusColor(status).split(' ')[0]}`}>
                  {status}
                </p>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Recent Alerts */}
      <div className="bg-white shadow rounded-lg p-6">
        <h3 className="text-lg font-medium text-gray-900 mb-4">Recent Alerts</h3>
        <div className="space-y-3">
          {alerts.slice(0, 5).map((alert) => (
            <AlertRow key={alert.id} alert={alert} />
          ))}
          {alerts.length === 0 && (
            <p className="text-center text-gray-500 py-4">No recent alerts</p>
          )}
        </div>
      </div>
    </div>
  )
}

interface BusinessTabProps {
  metrics: any
  isLoading: boolean
  formatValue: (value: number, unit: string) => string
}

const BusinessTab: React.FC<BusinessTabProps> = ({ metrics, isLoading, formatValue }) => {
  if (isLoading || !metrics) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  const businessKPIs = [
    { name: 'Monthly Recurring Revenue', value: metrics.mrr, unit: '$', icon: DollarSign },
    { name: 'Conversion Rate', value: metrics.conversionRate, unit: '%', icon: Target },
    { name: '30-Day Retention', value: metrics.retentionRate30d, unit: '%', icon: Users },
    { name: '90-Day Retention', value: metrics.retentionRate90d, unit: '%', icon: Shield },
    { name: 'Avg Session Duration', value: metrics.averageSessionDuration, unit: 'min', icon: Clock },
    { name: 'Churn Rate', value: metrics.churnRate, unit: '%', icon: TrendingDown },
    { name: 'Customer Lifetime Value', value: metrics.lifetimeValue, unit: '$', icon: Gauge },
  ]

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {businessKPIs.map((kpi) => (
          <div key={kpi.name} className="bg-white shadow rounded-lg p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <kpi.icon className="h-8 w-8 text-blue-600" />
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-500 truncate">
                    {kpi.name}
                  </dt>
                  <dd className="text-2xl font-semibold text-gray-900">
                    {formatValue(kpi.value, kpi.unit)}
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

interface TechnicalTabProps {
  metrics: any
  isLoading: boolean
  formatValue: (value: number, unit: string) => string
}

const TechnicalTab: React.FC<TechnicalTabProps> = ({ metrics, isLoading, formatValue }) => {
  if (isLoading || !metrics) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  const technicalKPIs = [
    { name: 'User Conversion Index', value: metrics.uci, unit: '%', icon: Users },
    { name: 'Customer Performance Score', value: metrics.cps, unit: '%', icon: BarChart3 },
    { name: 'Error Rate & Stability Score', value: metrics.erss, unit: '%', icon: Shield },
    { name: 'API Latency P95', value: metrics.apiLatencyP95, unit: 'ms', icon: Zap },
    { name: 'Error Rate', value: metrics.errorRate, unit: '%', icon: AlertTriangle },
    { name: 'Throughput', value: metrics.throughput, unit: 'req/min', icon: Activity },
    { name: 'Availability', value: metrics.availability, unit: '%', icon: Wifi },
  ]

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {technicalKPIs.map((kpi) => (
          <div key={kpi.name} className="bg-white shadow rounded-lg p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <kpi.icon className="h-8 w-8 text-blue-600" />
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-500 truncate">
                    {kpi.name}
                  </dt>
                  <dd className="text-2xl font-semibold text-gray-900">
                    {formatValue(kpi.value, kpi.unit)}
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

interface AlertsTabProps {
  alerts: Alert[]
  onAcknowledge: (alertId: string) => void
  getAlertIcon: (type: string) => JSX.Element
}

const AlertsTab: React.FC<AlertsTabProps> = ({ alerts, onAcknowledge, getAlertIcon }) => {
  return (
    <div className="space-y-4">
      {alerts.map((alert) => (
        <div key={alert.id} className={`bg-white shadow rounded-lg p-6 ${
          !alert.acknowledged ? 'border-l-4 border-red-500' : ''
        }`}>
          <div className="flex items-start space-x-4">
            <div className="flex-shrink-0">
              {getAlertIcon(alert.type)}
            </div>
            <div className="flex-1 min-w-0">
              <div className="flex items-center justify-between">
                <h4 className="text-sm font-medium text-gray-900">{alert.title}</h4>
                <div className="flex items-center space-x-2">
                  <span className={`px-2 py-1 text-xs font-medium rounded-full ${
                    alert.severity === 'critical' ? 'bg-red-100 text-red-800' :
                    alert.severity === 'high' ? 'bg-orange-100 text-orange-800' :
                    alert.severity === 'medium' ? 'bg-yellow-100 text-yellow-800' :
                    'bg-gray-100 text-gray-800'
                  }`}>
                    {alert.severity}
                  </span>
                  {!alert.acknowledged && (
                    <button
                      onClick={() => onAcknowledge(alert.id)}
                      className="text-xs text-blue-600 hover:text-blue-800"
                    >
                      Acknowledge
                    </button>
                  )}
                </div>
              </div>
              <p className="text-sm text-gray-600 mt-1">{alert.message}</p>
              <div className="flex items-center justify-between mt-2">
                <span className="text-xs text-gray-500">
                  {new Date(alert.timestamp).toLocaleString()}
                </span>
                <span className="text-xs text-gray-500">
                  Source: {alert.source}
                </span>
              </div>
            </div>
          </div>
        </div>
      ))}
      {alerts.length === 0 && (
        <div className="text-center py-12">
          <CheckCircle className="mx-auto h-12 w-12 text-green-500" />
          <h3 className="mt-2 text-sm font-medium text-gray-900">No alerts</h3>
          <p className="mt-1 text-sm text-gray-500">All systems are running smoothly.</p>
        </div>
      )}
    </div>
  )
}

interface SystemHealthTabProps {
  health: SystemHealth | null
  getStatusColor: (status: string) => string
}

const SystemHealthTab: React.FC<SystemHealthTabProps> = ({ health, getStatusColor }) => {
  if (!health) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  const services = [
    { name: 'API Gateway', status: health.services.api, icon: Server },
    { name: 'Database', status: health.services.database, icon: Database },
    { name: 'Cache Layer', status: health.services.cache, icon: Zap },
    { name: 'File Storage', status: health.services.storage, icon: Server },
    { name: 'Payment Service', status: health.services.payments, icon: DollarSign },
  ]

  return (
    <div className="space-y-6">
      {/* Overall Health */}
      <div className="bg-white shadow rounded-lg p-6">
        <div className="flex items-center justify-between">
          <h3 className="text-lg font-medium text-gray-900">Overall System Health</h3>
          <span className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(health.overall)}`}>
            {health.overall}
          </span>
        </div>
        <div className="mt-4 grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <p className="text-sm font-medium text-gray-500">Uptime</p>
            <p className="text-2xl font-semibold text-gray-900">{health.uptime}%</p>
          </div>
          <div>
            <p className="text-sm font-medium text-gray-500">Response Time</p>
            <p className="text-2xl font-semibold text-gray-900">{health.responseTime}ms</p>
          </div>
          <div>
            <p className="text-sm font-medium text-gray-500">Error Rate</p>
            <p className="text-2xl font-semibold text-gray-900">{health.errorRate}%</p>
          </div>
        </div>
      </div>

      {/* Service Status */}
      <div className="bg-white shadow rounded-lg p-6">
        <h3 className="text-lg font-medium text-gray-900 mb-4">Service Status</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {services.map((service) => (
            <div key={service.name} className="flex items-center space-x-3 p-4 border border-gray-200 rounded-lg">
              <service.icon className="h-8 w-8 text-gray-600" />
              <div className="flex-1">
                <p className="text-sm font-medium text-gray-900">{service.name}</p>
                <span className={`inline-flex px-2 py-1 text-xs font-medium rounded-full ${getStatusColor(service.status)}`}>
                  {service.status === 'up' ? (
                    <><Wifi className="w-3 h-3 mr-1" /> Online</>
                  ) : service.status === 'degraded' ? (
                    <><WifiOff className="w-3 h-3 mr-1" /> Degraded</>
                  ) : (
                    <><XCircle className="w-3 h-3 mr-1" /> Offline</>
                  )}
                </span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

// Helper component for alert rows
const AlertRow: React.FC<{ alert: Alert }> = ({ alert }) => (
  <div className="flex items-center space-x-3 p-3 bg-gray-50 rounded-lg">
    <div className={`w-2 h-2 rounded-full ${
      alert.type === 'error' ? 'bg-red-500' :
      alert.type === 'warning' ? 'bg-yellow-500' : 'bg-blue-500'
    }`} />
    <div className="flex-1 min-w-0">
      <p className="text-sm font-medium text-gray-900 truncate">{alert.title}</p>
      <p className="text-xs text-gray-500">{new Date(alert.timestamp).toLocaleString()}</p>
    </div>
    {!alert.acknowledged && (
      <span className="px-2 py-1 text-xs bg-red-100 text-red-800 rounded-full">
        New
      </span>
    )}
  </div>
)

export default ObservabilityDashboard
