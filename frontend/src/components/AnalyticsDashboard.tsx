import React, { useState, useEffect } from 'react';
import { useAnalytics } from '../hooks/useAnalytics';
import { useAdaptiveNavigation } from '../contexts/AdaptiveNavigationContext';
import './AnalyticsDashboard.css';

interface DashboardProps {
  className?: string;
}

const AnalyticsDashboard: React.FC<DashboardProps> = ({ className = '' }) => {
  const analytics = useAnalytics();
  const navigation = useAdaptiveNavigation();
  const [activeTab, setActiveTab] = useState<'overview' | 'performance' | 'behavior' | 'health'>('overview');
  const [autoRefresh, setAutoRefresh] = useState(true);

  // Auto-refresh cada 30 segundos
  useEffect(() => {
    if (!autoRefresh) return;

    const interval = setInterval(() => {
      analytics.performHealthCheck();
    }, 30000);

    return () => clearInterval(interval);
  }, [autoRefresh, analytics]);

  const formatDuration = (ms: number): string => {
    if (ms < 1000) return `${ms.toFixed(0)}ms`;
    if (ms < 60000) return `${(ms / 1000).toFixed(1)}s`;
    return `${(ms / 60000).toFixed(1)}m`;
  };

  const getHealthStatusColor = (status: string): string => {
    switch (status) {
      case 'healthy': return '#10b981';
      case 'warning': return '#f59e0b';
      case 'critical': return '#ef4444';
      default: return '#6b7280';
    }
  };

  const renderOverview = () => {
    const stats = analytics.getStats();
    const cacheStats = navigation.cacheStats();
    const predictions = navigation.predictions();

    return (
      <div className="analytics-overview">
        <div className="stats-grid">
          <div className="stat-card">
            <div className="stat-header">
              <h3>Actividad de Sesión</h3>
              <span className={`status-badge ${stats.healthStatus}`}>
                {stats.healthStatus.toUpperCase()}
              </span>
            </div>
            <div className="stat-content">
              <div className="stat-item">
                <span className="stat-label">Eventos Totales:</span>
                <span className="stat-value">{stats.totalEvents}</span>
              </div>
              <div className="stat-item">
                <span className="stat-label">Métricas de Performance:</span>
                <span className="stat-value">{stats.totalMetrics}</span>
              </div>
              <div className="stat-item">
                <span className="stat-label">Duración de Sesión:</span>
                <span className="stat-value">{formatDuration(stats.sessionDuration)}</span>
              </div>
            </div>
          </div>

          <div className="stat-card">
            <div className="stat-header">
              <h3>Cache Inteligente</h3>
              <span className="cache-utilization">
                {cacheStats.utilizationPercent.toFixed(1)}% utilizado
              </span>
            </div>
            <div className="stat-content">
              <div className="stat-item">
                <span className="stat-label">Entradas en Cache:</span>
                <span className="stat-value">{cacheStats.size}</span>
              </div>
              <div className="stat-item">
                <span className="stat-label">Memoria Utilizada:</span>
                <span className="stat-value">{cacheStats.currentSizeMB.toFixed(2)}MB</span>
              </div>
              <div className="stat-item">
                <span className="stat-label">Predicciones Activas:</span>
                <span className="stat-value">{predictions.length}</span>
              </div>
            </div>
          </div>

          <div className="stat-card">
            <div className="stat-header">
              <h3>Navegación Adaptativa</h3>
            </div>
            <div className="stat-content">
              <div className="stat-item">
                <span className="stat-label">Patrones Detectados:</span>
                <span className="stat-value">{navigation.navigationPatterns.length}</span>
              </div>
              <div className="stat-item">
                <span className="stat-label">Recomendaciones:</span>
                <span className="stat-value">{navigation.recommendations.length}</span>
              </div>
              <div className="stat-item">
                <span className="stat-label">Historial de Navegación:</span>
                <span className="stat-value">{navigation.navigationHistory().length}</span>
              </div>
            </div>
          </div>
        </div>

        {predictions.length > 0 && (
          <div className="predictions-section">
            <h3>Predicciones de Navegación</h3>
            <div className="predictions-list">
              {predictions.map((prediction, index) => (
                <div key={index} className="prediction-item">
                  <span className="prediction-route">{prediction.route}</span>
                  <div className="prediction-probability">
                    <div 
                      className="probability-bar"
                      style={{ width: `${prediction.probability * 100}%` }}
                    />
                    <span className="probability-text">
                      {(prediction.probability * 100).toFixed(0)}%
                    </span>
                  </div>
                  {prediction.preloaded && (
                    <span className="preloaded-badge">Precargado</span>
                  )}
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    );
  };

  const renderPerformance = () => {
    const recentMetrics = analytics.metrics.slice(-20);
    const insights = analytics.insights;

    return (
      <div className="analytics-performance">
        <div className="performance-grid">
          <div className="performance-chart">
            <h3>Tiempos de Carga Recientes</h3>
            <div className="chart-container">
              {recentMetrics.map((metric, index) => (
                <div key={index} className="chart-bar">
                  <div 
                    className="bar"
                    style={{ 
                      height: `${Math.min((metric.loadTime / 3000) * 100, 100)}%`,
                      backgroundColor: metric.loadTime > 2000 ? '#ef4444' : 
                                     metric.loadTime > 1000 ? '#f59e0b' : '#10b981'
                    }}
                    title={`${metric.route}: ${metric.loadTime}ms`}
                  />
                  <span className="bar-label">{metric.route.split('/').pop() || '/'}</span>
                </div>
              ))}
            </div>
          </div>

          {insights?.performanceIssues && insights.performanceIssues.length > 0 && (
            <div className="performance-issues">
              <h3>Problemas de Performance</h3>
              <div className="issues-list">
                {insights.performanceIssues.map((issue, index) => (
                  <div key={index} className="issue-item">
                    <span className="issue-route">{issue.route}</span>
                    <span className="issue-time">{formatDuration(issue.avgLoadTime)}</span>
                    <span className="issue-count">{issue.issueCount} problemas</span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {insights?.mostVisitedRoutes && (
          <div className="most-visited">
            <h3>Rutas Más Visitadas</h3>
            <div className="routes-list">
              {insights.mostVisitedRoutes.map((route, index) => (
                <div key={index} className="route-item">
                  <span className="route-path">{route.route}</span>
                  <span className="route-visits">{route.visits} visitas</span>
                  <span className="route-time">{formatDuration(route.avgTime)} promedio</span>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    );
  };

  const renderBehavior = () => {
    const recentEvents = analytics.events.slice(-50);
    const eventsByType = recentEvents.reduce((acc, event) => {
      acc[event.type] = (acc[event.type] || 0) + 1;
      return acc;
    }, {} as Record<string, number>);

    return (
      <div className="analytics-behavior">
        <div className="behavior-summary">
          <h3>Resumen de Comportamiento</h3>
          <div className="event-types">
            {Object.entries(eventsByType).map(([type, count]) => (
              <div key={type} className="event-type">
                <span className="event-label">{type}:</span>
                <span className="event-count">{count}</span>
              </div>
            ))}
          </div>
        </div>

        <div className="recent-events">
          <h3>Eventos Recientes</h3>
          <div className="events-list">
            {recentEvents.slice(-10).reverse().map((event, index) => (
              <div key={index} className={`event-item ${event.type}`}>
                <span className="event-time">
                  {event.timestamp.toLocaleTimeString()}
                </span>
                <span className="event-type-badge">{event.type}</span>
                <span className="event-route">{event.route}</span>
                <span className="event-action">{event.action}</span>
                {event.type === 'error' && (
                  <span className="event-error">{event.data?.message}</span>
                )}
              </div>
            ))}
          </div>
        </div>
      </div>
    );
  };

  const renderHealth = () => {
    const healthChecks = analytics.healthChecks;

    return (
      <div className="analytics-health">
        <div className="health-overview">
          <h3>Estado de Salud del Sistema</h3>
          <div className="health-grid">
            {healthChecks.map((check, index) => (
              <div key={index} className={`health-card ${check.status}`}>
                <div className="health-header">
                  <span className="health-component">{check.component}</span>
                  <span 
                    className="health-status"
                    style={{ color: getHealthStatusColor(check.status) }}
                  >
                    {check.status.toUpperCase()}
                  </span>
                </div>
                <div className="health-details">
                  <div className="health-metric">
                    <span className="metric-name">{check.metric}:</span>
                    <span className="metric-value">
                      {check.value.toFixed(2)}
                      {check.metric.includes('percent') ? '%' : 
                       check.metric.includes('ms') ? 'ms' : ''}
                    </span>
                  </div>
                  <div className="health-threshold">
                    Umbral: {check.threshold}
                    {check.metric.includes('percent') ? '%' : 
                     check.metric.includes('ms') ? 'ms' : ''}
                  </div>
                  <div className="health-timestamp">
                    {check.timestamp.toLocaleTimeString()}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="health-actions">
          <button 
            className="health-button primary"
            onClick={() => analytics.performHealthCheck()}
          >
            Ejecutar Health Check
          </button>
          <button 
            className="health-button secondary"
            onClick={() => setAutoRefresh(!autoRefresh)}
          >
            {autoRefresh ? 'Pausar' : 'Activar'} Auto-refresh
          </button>
          <button 
            className="health-button danger"
            onClick={() => analytics.clearData()}
          >
            Limpiar Datos
          </button>
        </div>
      </div>
    );
  };

  const renderTabContent = () => {
    switch (activeTab) {
      case 'overview': return renderOverview();
      case 'performance': return renderPerformance();
      case 'behavior': return renderBehavior();
      case 'health': return renderHealth();
      default: return renderOverview();
    }
  };

  return (
    <div className={`analytics-dashboard ${className}`}>
      <div className="dashboard-header">
        <h1>Dashboard de Analytics</h1>
        <div className="dashboard-controls">
          <div className="session-info">
            Session: {analytics.sessionId.substring(8, 16)}...
          </div>
          <button 
            className="export-button"
            onClick={() => {
              const data = analytics.exportData();
              const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
              const url = URL.createObjectURL(blob);
              const a = document.createElement('a');
              a.href = url;
              a.download = `impulse-analytics-${new Date().toISOString().split('T')[0]}.json`;
              a.click();
              URL.revokeObjectURL(url);
            }}
          >
            Exportar Datos
          </button>
        </div>
      </div>

      <div className="dashboard-tabs">
        <button 
          className={`tab ${activeTab === 'overview' ? 'active' : ''}`}
          onClick={() => setActiveTab('overview')}
        >
          Resumen
        </button>
        <button 
          className={`tab ${activeTab === 'performance' ? 'active' : ''}`}
          onClick={() => setActiveTab('performance')}
        >
          Performance
        </button>
        <button 
          className={`tab ${activeTab === 'behavior' ? 'active' : ''}`}
          onClick={() => setActiveTab('behavior')}
        >
          Comportamiento
        </button>
        <button 
          className={`tab ${activeTab === 'health' ? 'active' : ''}`}
          onClick={() => setActiveTab('health')}
        >
          Salud del Sistema
        </button>
      </div>

      <div className="dashboard-content">
        {renderTabContent()}
      </div>
    </div>
  );
};

export default AnalyticsDashboard;
