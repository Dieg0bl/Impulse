import React, { createContext, useContext, useEffect, ReactNode } from 'react';
import { useAuth } from '../hooks/useAuth';
import { useLocation } from 'react-router-dom';
import { useAnalytics } from '../hooks/useAnalytics';
import { useAdaptiveNavigation } from './AdaptiveNavigationContext';

// Tipos para el monitoreo global
interface GlobalMonitoringContext {
  analytics: ReturnType<typeof useAnalytics>;
  isMonitoringActive: boolean;
  startMonitoring: () => void;
  stopMonitoring: () => void;
  getSystemStatus: () => 'healthy' | 'warning' | 'critical';
}

const GlobalMonitoringContext = createContext<GlobalMonitoringContext | undefined>(undefined);

// Proveedor de monitoreo global que integra todo el sistema
export const GlobalMonitoringProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const { user } = useAuth();
  const location = useLocation();
  const analytics = useAnalytics({
    enablePerformanceTracking: true,
    enableUserBehavior: true,
    enableHealthChecks: true,
    batchSize: 50,
    flushInterval: 30000,
    retentionDays: 30
  });
  const navigation = useAdaptiveNavigation();

  const [isMonitoringActive, setIsMonitoringActive] = React.useState(true);
  const previousLocation = React.useRef(location.pathname);
  const routeStartTime = React.useRef(Date.now());

  // Tracking automático de navegación
  useEffect(() => {
    if (!isMonitoringActive || !user) return;

    const currentRoute = location.pathname;
    const previousRoute = previousLocation.current;
    const duration = Date.now() - routeStartTime.current;

    // Solo trackear si cambió la ruta y el usuario estuvo al menos 1 segundo
    if (currentRoute !== previousRoute && duration > 1000) {
      analytics.trackNavigation(previousRoute, currentRoute, duration);
      
      // Trackear performance de la página anterior
      if (duration > 0) {
        analytics.trackPerformance(previousRoute, duration);
      }
    }

    // Actualizar referencias
    previousLocation.current = currentRoute;
    routeStartTime.current = Date.now();
  }, [location.pathname, user, isMonitoringActive, analytics]);

  // Monitoreo automático de errores JavaScript
  useEffect(() => {
    if (!isMonitoringActive) return;

    const handleError = (event: ErrorEvent) => {
      analytics.trackError(
        new Error(event.message),
        location.pathname,
        {
          filename: event.filename,
          lineno: event.lineno,
          colno: event.colno,
          userAgent: navigator.userAgent
        }
      );
    };

    const handleUnhandledRejection = (event: PromiseRejectionEvent) => {
      analytics.trackError(
        new Error(event.reason?.message || 'Unhandled Promise Rejection'),
        location.pathname,
        {
          reason: event.reason,
          type: 'unhandledRejection'
        }
      );
    };

    window.addEventListener('error', handleError);
    window.addEventListener('unhandledrejection', handleUnhandledRejection);

    return () => {
      window.removeEventListener('error', handleError);
      window.removeEventListener('unhandledrejection', handleUnhandledRejection);
    };
  }, [isMonitoringActive, analytics, location.pathname]);

  // Health checks automáticos cada 60 segundos
  useEffect(() => {
    if (!isMonitoringActive) return;

    const healthCheckInterval = setInterval(() => {
      analytics.performHealthCheck();
    }, 60000);

    // Health check inicial después de 5 segundos
    const initialTimeout = setTimeout(() => {
      analytics.performHealthCheck();
    }, 5000);

    return () => {
      clearInterval(healthCheckInterval);
      clearTimeout(initialTimeout);
    };
  }, [isMonitoringActive, analytics]);

  // Tracking de interacciones del usuario
  useEffect(() => {
    if (!isMonitoringActive || !user) return;

    const trackInteraction = (eventType: string) => (event: Event) => {
      const target = event.target as HTMLElement;
      const elementInfo = {
        tagName: target.tagName,
        className: target.className,
        id: target.id,
        textContent: target.textContent?.substring(0, 50)
      };

      analytics.trackEvent(
        'interaction',
        location.pathname,
        eventType,
        elementInfo,
        user.id?.toString()
      );
    };

    // Eventos que queremos trackear
    const eventTypes = ['click', 'submit', 'change', 'focus'];
    const listeners: Array<{ type: string; listener: EventListener }> = [];

    eventTypes.forEach(eventType => {
      const listener = trackInteraction(eventType);
      document.addEventListener(eventType, listener, true);
      listeners.push({ type: eventType, listener });
    });

    return () => {
      listeners.forEach(({ type, listener }) => {
        document.removeEventListener(type, listener, true);
      });
    };
  }, [isMonitoringActive, user, analytics, location.pathname]);

  // Funciones de control
  const startMonitoring = () => {
    setIsMonitoringActive(true);
    analytics.trackEvent('system', location.pathname, 'monitoring_started', {}, user?.id?.toString());
  };

  const stopMonitoring = () => {
    setIsMonitoringActive(false);
    analytics.trackEvent('system', location.pathname, 'monitoring_stopped', {}, user?.id?.toString());
  };

  const getSystemStatus = (): 'healthy' | 'warning' | 'critical' => {
    const healthChecks = analytics.healthChecks;
    
    if (healthChecks.length === 0) return 'healthy';
    
    const hasCritical = healthChecks.some(check => check.status === 'critical');
    const hasWarning = healthChecks.some(check => check.status === 'warning');
    
    if (hasCritical) return 'critical';
    if (hasWarning) return 'warning';
    return 'healthy';
  };

  const value: GlobalMonitoringContext = {
    analytics,
    isMonitoringActive,
    startMonitoring,
    stopMonitoring,
    getSystemStatus
  };

  return (
    <GlobalMonitoringContext.Provider value={value}>
      {children}
    </GlobalMonitoringContext.Provider>
  );
};

export const useGlobalMonitoring = (): GlobalMonitoringContext => {
  const context = useContext(GlobalMonitoringContext);
  if (!context) {
    throw new Error('useGlobalMonitoring must be used within GlobalMonitoringProvider');
  }
  return context;
};

// Hook para tracking manual simplificado
export const useTracking = () => {
  const { analytics } = useGlobalMonitoring();
  const { user } = useAuth();
  const location = useLocation();

  return {
    trackAction: (action: string, data?: any) => {
      analytics.trackEvent('interaction', location.pathname, action, data, user?.id?.toString());
    },
    
    trackError: (error: Error, context?: any) => {
      analytics.trackError(error, location.pathname, context);
    },
    
    trackPerformance: (metric: string, value: number, unit: string = 'ms') => {
      analytics.trackEvent('performance', location.pathname, metric, { value, unit }, user?.id?.toString());
    },
    
    trackCustomEvent: (type: string, action: string, data?: any) => {
      analytics.trackEvent(type as any, location.pathname, action, data, user?.id?.toString());
    }
  };
};

export default GlobalMonitoringContext;
