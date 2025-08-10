import { useState, useEffect, useRef, useCallback } from 'react';

// Tipos para Analytics y Monitoreo
interface PerformanceMetric {
  route: string;
  loadTime: number;
  renderTime: number;
  userInteractions: number;
  timestamp: Date;
  sessionId: string;
}

interface UserBehaviorEvent {
  type: 'navigation' | 'interaction' | 'error' | 'performance' | 'system';
  route: string;
  action: string;
  data?: any;
  timestamp: Date;
  sessionId: string;
  userId?: string;
}

interface HealthCheck {
  status: 'healthy' | 'warning' | 'critical';
  component: string;
  metric: string;
  value: number;
  threshold: number;
  timestamp: Date;
}

interface AnalyticsConfig {
  enablePerformanceTracking: boolean;
  enableUserBehavior: boolean;
  enableHealthChecks: boolean;
  batchSize: number;
  flushInterval: number; // ms
  retentionDays: number;
}

interface NavigationInsight {
  mostVisitedRoutes: Array<{ route: string; visits: number; avgTime: number }>;
  userJourneys: Array<{ path: string[]; frequency: number; avgDuration: number }>;
  dropOffPoints: Array<{ route: string; exitRate: number }>;
  performanceIssues: Array<{ route: string; avgLoadTime: number; issueCount: number }>;
}

// Configuración por defecto
const DEFAULT_CONFIG: AnalyticsConfig = {
  enablePerformanceTracking: true,
  enableUserBehavior: true,
  enableHealthChecks: true,
  batchSize: 50,
  flushInterval: 30000, // 30 segundos
  retentionDays: 30
};

// Generador de Session ID único
const generateSessionId = (): string => {
  return `session_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
};

// Almacenamiento local con límites
class LocalAnalyticsStorage {
  private maxEntries: number;
  private keyPrefix: string;

  constructor(maxEntries: number = 1000, keyPrefix: string = 'impulse_analytics') {
    this.maxEntries = maxEntries;
    this.keyPrefix = keyPrefix;
  }

  store(key: string, data: any[]): void {
    try {
      // Mantener solo las entradas más recientes
      const limited = data.slice(-this.maxEntries);
      localStorage.setItem(`${this.keyPrefix}_${key}`, JSON.stringify(limited));
    } catch (error) {
      console.warn('Analytics storage failed:', error);
      this.clearOldData();
    }
  }

  retrieve(key: string): any[] {
    try {
      const data = localStorage.getItem(`${this.keyPrefix}_${key}`);
      return data ? JSON.parse(data) : [];
    } catch (error) {
      console.warn('Analytics retrieval failed:', error);
      return [];
    }
  }

  clearOldData(): void {
    const keys = Object.keys(localStorage);
    const analyticsKeys = keys.filter(key => key.startsWith(this.keyPrefix));
    
    // Mantener solo las más recientes
    if (analyticsKeys.length > 10) {
      analyticsKeys.slice(0, analyticsKeys.length - 10).forEach(key => {
        localStorage.removeItem(key);
      });
    }
  }
}

// Hook principal para Analytics y Monitoreo
export const useAnalytics = (config: Partial<AnalyticsConfig> = {}) => {
  const finalConfig = { ...DEFAULT_CONFIG, ...config };
  const sessionId = useRef(generateSessionId());
  const storage = useRef(new LocalAnalyticsStorage());
  
  const [metrics, setMetrics] = useState<PerformanceMetric[]>([]);
  const [events, setEvents] = useState<UserBehaviorEvent[]>([]);
  const [healthChecks, setHealthChecks] = useState<HealthCheck[]>([]);
  const [insights, setInsights] = useState<NavigationInsight | null>(null);

  const performanceObserver = useRef<PerformanceObserver | null>(null);
  const eventBuffer = useRef<UserBehaviorEvent[]>([]);
  const metricsBuffer = useRef<PerformanceMetric[]>([]);

  // Inicialización del monitoreo de performance
  useEffect(() => {
    if (!finalConfig.enablePerformanceTracking) return;

    // Observer para Navigation Timing
    if ('PerformanceObserver' in window) {
      performanceObserver.current = new PerformanceObserver((list) => {
        const entries = list.getEntries();
        entries.forEach((entry) => {
          if (entry.entryType === 'navigation') {
            const navEntry = entry as PerformanceNavigationTiming;
            const metric: PerformanceMetric = {
              route: window.location.pathname,
              loadTime: navEntry.loadEventEnd - navEntry.loadEventStart,
              renderTime: navEntry.domContentLoadedEventEnd - navEntry.domContentLoadedEventStart,
              userInteractions: 0,
              timestamp: new Date(),
              sessionId: sessionId.current
            };
            addMetric(metric);
          }
        });
      });

      performanceObserver.current.observe({ entryTypes: ['navigation'] });
    }

    return () => {
      if (performanceObserver.current) {
        performanceObserver.current.disconnect();
      }
    };
  }, [finalConfig.enablePerformanceTracking]);

  // Flush periódico de datos
  useEffect(() => {
    const flushData = () => {
      if (eventBuffer.current.length > 0) {
        storage.current.store('events', [
          ...storage.current.retrieve('events'),
          ...eventBuffer.current
        ]);
        eventBuffer.current = [];
      }

      if (metricsBuffer.current.length > 0) {
        storage.current.store('metrics', [
          ...storage.current.retrieve('metrics'),
          ...metricsBuffer.current
        ]);
        metricsBuffer.current = [];
      }
    };

    const interval = setInterval(flushData, finalConfig.flushInterval);
    return () => {
      clearInterval(interval);
      flushData(); // Flush final
    };
  }, [finalConfig.flushInterval]);

  // Cargar datos existentes
  useEffect(() => {
    const storedMetrics = storage.current.retrieve('metrics');
    const storedEvents = storage.current.retrieve('events');
    
    setMetrics(storedMetrics);
    setEvents(storedEvents);
  }, []);

  // Funciones de tracking
  const addMetric = useCallback((metric: PerformanceMetric) => {
    metricsBuffer.current.push(metric);
    setMetrics(prev => [...prev.slice(-99), metric]); // Mantener últimas 100
  }, []);

  const trackEvent = useCallback((
    type: UserBehaviorEvent['type'],
    route: string,
    action: string,
    data?: any,
    userId?: string
  ) => {
    if (!finalConfig.enableUserBehavior) return;

    const event: UserBehaviorEvent = {
      type,
      route,
      action,
      data,
      timestamp: new Date(),
      sessionId: sessionId.current,
      userId
    };

    eventBuffer.current.push(event);
    setEvents(prev => [...prev.slice(-199), event]); // Mantener últimas 200
  }, [finalConfig.enableUserBehavior]);

  const trackNavigation = useCallback((fromRoute: string, toRoute: string, duration: number) => {
    trackEvent('navigation', toRoute, 'route_change', {
      fromRoute,
      duration,
      timestamp: new Date()
    });
  }, [trackEvent]);

  const trackError = useCallback((error: Error, route: string, context?: any) => {
    trackEvent('error', route, 'javascript_error', {
      message: error.message,
      stack: error.stack,
      context
    });
  }, [trackEvent]);

  const trackPerformance = useCallback((route: string, loadTime: number, renderTime?: number) => {
    const metric: PerformanceMetric = {
      route,
      loadTime,
      renderTime: renderTime || 0,
      userInteractions: 0,
      timestamp: new Date(),
      sessionId: sessionId.current
    };
    addMetric(metric);
  }, [addMetric]);

  // Health Checks
  const performHealthCheck = useCallback(() => {
    if (!finalConfig.enableHealthChecks) return;

    const checks: HealthCheck[] = [];
    const now = new Date();

    // Check 1: Memory Usage
    if ('memory' in performance) {
      const memory = (performance as any).memory;
      const memoryUsage = (memory.usedJSHeapSize / memory.jsHeapSizeLimit) * 100;
      checks.push({
        status: memoryUsage > 80 ? 'critical' : memoryUsage > 60 ? 'warning' : 'healthy',
        component: 'memory',
        metric: 'heap_usage_percent',
        value: memoryUsage,
        threshold: 80,
        timestamp: now
      });
    }

    // Check 2: Performance Metrics
    const recentMetrics = metrics.slice(-10);
    if (recentMetrics.length > 0) {
      const avgLoadTime = recentMetrics.reduce((sum, m) => sum + m.loadTime, 0) / recentMetrics.length;
      checks.push({
        status: avgLoadTime > 3000 ? 'critical' : avgLoadTime > 1500 ? 'warning' : 'healthy',
        component: 'navigation',
        metric: 'avg_load_time_ms',
        value: avgLoadTime,
        threshold: 3000,
        timestamp: now
      });
    }

    // Check 3: Error Rate
    const recentEvents = events.slice(-100);
    const errorEvents = recentEvents.filter(e => e.type === 'error');
    const errorRate = recentEvents.length > 0 ? (errorEvents.length / recentEvents.length) * 100 : 0;
    checks.push({
      status: errorRate > 5 ? 'critical' : errorRate > 2 ? 'warning' : 'healthy',
      component: 'stability',
      metric: 'error_rate_percent',
      value: errorRate,
      threshold: 5,
      timestamp: now
    });

    setHealthChecks(checks);
    return checks;
  }, [metrics, events, finalConfig.enableHealthChecks]);

  // Generar insights
  const generateInsights = useCallback((): NavigationInsight => {
    const routeStats = new Map<string, { visits: number; totalTime: number; exits: number }>();
    const journeys = new Map<string, { frequency: number; totalDuration: number }>();

    // Procesar eventos de navegación
    events.filter(e => e.type === 'navigation').forEach(event => {
      const route = event.route;
      const stats = routeStats.get(route) || { visits: 0, totalTime: 0, exits: 0 };
      stats.visits++;
      if (event.data?.duration) {
        stats.totalTime += event.data.duration;
      }
      routeStats.set(route, stats);
    });

    // Rutas más visitadas
    const mostVisitedRoutes = Array.from(routeStats.entries())
      .map(([route, stats]) => ({
        route,
        visits: stats.visits,
        avgTime: stats.visits > 0 ? stats.totalTime / stats.visits : 0
      }))
      .sort((a, b) => b.visits - a.visits)
      .slice(0, 10);

    // Puntos de abandono (simplificado)
    const dropOffPoints = Array.from(routeStats.entries())
      .map(([route, stats]) => ({
        route,
        exitRate: stats.visits > 0 ? (stats.exits / stats.visits) * 100 : 0
      }))
      .filter(point => point.exitRate > 20)
      .sort((a, b) => b.exitRate - a.exitRate)
      .slice(0, 5);

    // Problemas de performance
    const performanceIssues = Array.from(
      metrics.reduce((map, metric) => {
        const existing = map.get(metric.route) || { totalTime: 0, count: 0, issues: 0 };
        existing.totalTime += metric.loadTime;
        existing.count++;
        if (metric.loadTime > 2000) existing.issues++;
        map.set(metric.route, existing);
        return map;
      }, new Map())
    ).map(([route, data]) => ({
      route,
      avgLoadTime: data.count > 0 ? data.totalTime / data.count : 0,
      issueCount: data.issues
    }))
    .filter(issue => issue.issueCount > 0)
    .sort((a, b) => b.issueCount - a.issueCount)
    .slice(0, 5);

    return {
      mostVisitedRoutes,
      userJourneys: [], // Se puede implementar análisis más complejo
      dropOffPoints,
      performanceIssues
    };
  }, [events, metrics]);

  // Actualizar insights periódicamente
  useEffect(() => {
    if (events.length > 0 || metrics.length > 0) {
      const newInsights = generateInsights();
      setInsights(newInsights);
    }
  }, [events.length, metrics.length, generateInsights]);

  // API principal
  return {
    // Datos
    metrics,
    events,
    healthChecks,
    insights,
    sessionId: sessionId.current,

    // Tracking
    trackEvent,
    trackNavigation,
    trackError,
    trackPerformance,

    // Monitoreo
    performHealthCheck,
    generateInsights,

    // Utilidades
    clearData: () => {
      setMetrics([]);
      setEvents([]);
      setHealthChecks([]);
      storage.current.clearOldData();
    },
    
    exportData: () => ({
      metrics,
      events,
      healthChecks,
      insights,
      sessionId: sessionId.current,
      exportedAt: new Date()
    }),

    getStats: () => ({
      totalEvents: events.length,
      totalMetrics: metrics.length,
      sessionDuration: Date.now() - parseInt(sessionId.current.split('_')[1]),
      healthStatus: healthChecks.length > 0 
        ? healthChecks.some(h => h.status === 'critical') ? 'critical'
        : healthChecks.some(h => h.status === 'warning') ? 'warning'
        : 'healthy'
        : 'unknown'
    })
  };
};

export default useAnalytics;
