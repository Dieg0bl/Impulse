import React, { useEffect } from 'react';
import AnalyticsDashboard from '../components/AnalyticsDashboard';
import { useGlobalMonitoring, useTracking } from '../contexts/GlobalMonitoringContext';
import SmartRecommendations from '../components/SmartRecommendations';
import './Analytics.css';

const AnalyticsPage: React.FC = () => {
  const { analytics, getSystemStatus } = useGlobalMonitoring();
  const { trackAction } = useTracking();

  useEffect(() => {
    // Trackear que el usuario accedió a analytics
    trackAction('page_analytics_viewed');
    
    // Trigger health check al cargar la página
    analytics.performHealthCheck();
  }, [trackAction, analytics]);

  const systemStatus = getSystemStatus();

  return (
    <div className="analytics-page">
      {/* Header con estado del sistema */}
      <div className="page-header">
        <div className="status-indicator">
          <div className={`status-dot ${systemStatus}`}></div>
          <span className="status-text">
            Sistema: {systemStatus === 'healthy' ? 'Saludable' : 
                     systemStatus === 'warning' ? 'Advertencias' : 'Crítico'}
          </span>
        </div>
        
        {/* Recomendaciones inteligentes en la parte superior */}
        <SmartRecommendations />
      </div>

      {/* Dashboard principal */}
      <AnalyticsDashboard />
    </div>
  );
};

export default AnalyticsPage;
