import React, { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import { useAuth } from '../hooks/useAuth';
import { useLocation, useNavigate } from 'react-router-dom';
import { useNavigationCache } from '../hooks/useIntelligentCache';

// Tipos para navegación adaptativa
interface NavigationContext {
  userRole: string | null;
  lastVisitedRoutes: string[];
  preferredSections: string[];
  adaptiveRecommendations: RouteRecommendation[];
}

interface RouteRecommendation {
  path: string;
  title: string;
  reason: string;
  priority: number;
  category: 'frecuent' | 'role-based' | 'contextual' | 'trending';
}

interface UserNavigationPattern {
  route: string;
  visitCount: number;
  lastVisited: Date;
  timeSpent: number;
  exitRate: number;
}

interface AdaptiveNavigationContextType {
  context: NavigationContext;
  recommendations: RouteRecommendation[];
  navigationPatterns: UserNavigationPattern[];
  adaptRoute: (targetRoute: string) => string;
  recordNavigation: (route: string, timeSpent?: number) => void;
  getOptimalNextRoute: () => string | null;
  isRouteRecommended: (route: string) => boolean;
  updateUserPreferences: (preferences: Partial<NavigationContext>) => void;
  // Métodos del cache inteligente
  cacheStats: () => any;
  predictions: () => any[];
  navigationHistory: () => string[];
  cachePageData: (route: string, dataLoader: () => Promise<any>, priority?: number) => Promise<any>;
}

const AdaptiveNavigationContext = createContext<AdaptiveNavigationContextType | undefined>(undefined);

// Configuración de roles y sus rutas preferenciales
const ROLE_PREFERENCES: Record<string, string[]> = {
  'ADMIN': ['/dashboard', '/usuarios', '/configuracion', '/analytics'],
  'MODERATOR': ['/validaciones', '/reportes', '/usuarios', '/dashboard'],
  'VALIDATOR': ['/validaciones', '/mis-retos', '/dashboard'],
  'USER': ['/dashboard', '/crear-reto', '/mis-retos', '/perfil']
};

// Rutas contextuales basadas en tiempo de día y actividad
const CONTEXTUAL_ROUTES = {
  morning: ['/dashboard', '/crear-reto'],
  afternoon: ['/mis-retos', '/validaciones'],
  evening: ['/perfil', '/configuracion'],
  weekend: ['/explorar', '/social']
};

export const AdaptiveNavigationProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const { user } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const navigationCache = useNavigationCache();
  
  const [context, setContext] = useState<NavigationContext>({
    userRole: null,
    lastVisitedRoutes: [],
    preferredSections: [],
    adaptiveRecommendations: []
  });

  const [navigationPatterns, setNavigationPatterns] = useState<UserNavigationPattern[]>([]);
  const [sessionStartTime, setSessionStartTime] = useState<Date>(new Date());
  const [currentRouteStartTime, setCurrentRouteStartTime] = useState<Date>(new Date());

  // Inicializar contexto del usuario
  useEffect(() => {
    if (user) {
      const storedPatterns = localStorage.getItem(`nav_patterns_${user.id}`);
      const storedContext = localStorage.getItem(`nav_context_${user.id}`);
      
      if (storedPatterns) {
        try {
          const patterns = JSON.parse(storedPatterns).map((p: any) => ({
            ...p,
            lastVisited: new Date(p.lastVisited)
          }));
          setNavigationPatterns(patterns);
        } catch (e) {
          console.warn('Error loading navigation patterns:', e);
        }
      }

      if (storedContext) {
        try {
          setContext(prev => ({ ...prev, ...JSON.parse(storedContext) }));
        } catch (e) {
          console.warn('Error loading navigation context:', e);
        }
      }

      // Establecer contexto inicial basado en rol
      setContext(prev => ({
        ...prev,
        userRole: user.rol,
        preferredSections: ROLE_PREFERENCES[user.rol] || ROLE_PREFERENCES['USER']
      }));
    }
  }, [user]);

  // Registrar navegación y tiempo en ruta
  useEffect(() => {
    const currentRoute = location.pathname;
    const timeSpent = Date.now() - currentRouteStartTime.getTime();
    
    if (timeSpent > 1000) { // Solo registrar si estuvo más de 1 segundo
      recordNavigation(currentRoute, timeSpent);
      navigationCache.recordNavigation(currentRoute); // Integrar con cache
    }
    
    setCurrentRouteStartTime(new Date());
  }, [location.pathname, navigationCache]);

  // Generar recomendaciones basadas en contexto
  useEffect(() => {
    if (user && navigationPatterns.length > 0) {
      const recommendations = generateAdaptiveRecommendations();
      setContext(prev => ({
        ...prev,
        adaptiveRecommendations: recommendations
      }));
    }
  }, [navigationPatterns, user]);

  const recordNavigation = (route: string, timeSpent: number = 0) => {
    if (!user) return;

    setNavigationPatterns(prev => {
      const existing = prev.find(p => p.route === route);
      let updated;

      if (existing) {
        updated = prev.map(p => 
          p.route === route 
            ? {
                ...p,
                visitCount: p.visitCount + 1,
                lastVisited: new Date(),
                timeSpent: (p.timeSpent + timeSpent) / 2 // Promedio de tiempo
              }
            : p
        );
      } else {
        updated = [...prev, {
          route,
          visitCount: 1,
          lastVisited: new Date(),
          timeSpent,
          exitRate: 0
        }];
      }

      // Mantener solo las últimas 50 rutas para performance
      const trimmed = updated.slice(-50);
      
      // Persistir en localStorage
      localStorage.setItem(`nav_patterns_${user.id}`, JSON.stringify(trimmed));
      
      return trimmed;
    });

    // Actualizar últimas rutas visitadas
    setContext(prev => {
      const lastRoutes = [route, ...prev.lastVisitedRoutes.filter(r => r !== route)].slice(0, 10);
      const updatedContext = { ...prev, lastVisitedRoutes: lastRoutes };
      
      if (user) {
        localStorage.setItem(`nav_context_${user.id}`, JSON.stringify(updatedContext));
      }
      
      return updatedContext;
    });
  };

  const generateAdaptiveRecommendations = (): RouteRecommendation[] => {
    if (!user) return [];

    const recommendations: RouteRecommendation[] = [];
    
    // 1. Recomendaciones basadas en frecuencia
    const frequentRoutes = navigationPatterns
      .filter(p => p.visitCount > 2)
      .sort((a, b) => b.visitCount - a.visitCount)
      .slice(0, 3);

    frequentRoutes.forEach((pattern, index) => {
      recommendations.push({
        path: pattern.route,
        title: getRouteTitle(pattern.route),
        reason: `Visitas frecuentes (${pattern.visitCount} veces)`,
        priority: 10 - index,
        category: 'frecuent'
      });
    });

    // 2. Recomendaciones basadas en rol
    const roleRoutes = ROLE_PREFERENCES[user.rol] || [];
    roleRoutes.forEach((route, index) => {
      if (!recommendations.find(r => r.path === route)) {
        recommendations.push({
          path: route,
          title: getRouteTitle(route),
          reason: `Recomendado para ${user.rol}`,
          priority: 8 - index,
          category: 'role-based'
        });
      }
    });

    // 3. Recomendaciones contextuales (hora del día)
    const hour = new Date().getHours();
    const timeContext = hour < 12 ? 'morning' : hour < 18 ? 'afternoon' : 'evening';
    const isWeekend = [0, 6].includes(new Date().getDay());
    
    const contextualRoutes = isWeekend ? CONTEXTUAL_ROUTES.weekend : CONTEXTUAL_ROUTES[timeContext];
    contextualRoutes.forEach((route, index) => {
      if (!recommendations.find(r => r.path === route)) {
        recommendations.push({
          path: route,
          title: getRouteTitle(route),
          reason: `Ideal para ${isWeekend ? 'fin de semana' : timeContext}`,
          priority: 6 - index,
          category: 'contextual'
        });
      }
    });

    return recommendations
      .sort((a, b) => b.priority - a.priority)
      .slice(0, 8); // Máximo 8 recomendaciones
  };

  const getRouteTitle = (route: string): string => {
    const titles: Record<string, string> = {
      '/dashboard': 'Panel Principal',
      '/crear-reto': 'Crear Reto',
      '/mis-retos': 'Mis Retos',
      '/validaciones': 'Validaciones',
      '/perfil': 'Mi Perfil',
      '/configuracion': 'Configuración',
      '/usuarios': 'Gestión de Usuarios',
      '/reportes': 'Reportes',
      '/analytics': 'Analytics',
      '/explorar': 'Explorar Retos',
      '/social': 'Red Social'
    };
    return titles[route] || route;
  };

  const adaptRoute = (targetRoute: string): string => {
    // Si el usuario no tiene permisos para la ruta, redirigir a alternativa
    if (!user) return '/login';
    
    const userRoutes = ROLE_PREFERENCES[user.rol] || ROLE_PREFERENCES['USER'];
    
    if (!userRoutes.includes(targetRoute)) {
      // Buscar ruta alternativa similar
      const alternative = findAlternativeRoute(targetRoute, userRoutes);
      return alternative || userRoutes[0];
    }
    
    return targetRoute;
  };

  const findAlternativeRoute = (targetRoute: string, allowedRoutes: string[]): string | null => {
    // Mapeo de rutas similares por funcionalidad
    const routeMappings: Record<string, string[]> = {
      '/admin': ['/dashboard', '/configuracion'],
      '/usuarios': ['/perfil', '/dashboard'],
      '/analytics': ['/dashboard', '/reportes'],
      '/configuracion-avanzada': ['/configuracion', '/perfil']
    };

    const alternatives = routeMappings[targetRoute];
    if (alternatives) {
      return alternatives.find(alt => allowedRoutes.includes(alt)) || null;
    }

    return null;
  };

  const getOptimalNextRoute = (): string | null => {
    if (context.adaptiveRecommendations.length === 0) return null;
    
    // Filtrar rutas que no ha visitado recientemente
    const unvisitedRecommendations = context.adaptiveRecommendations
      .filter(rec => !context.lastVisitedRoutes.slice(0, 3).includes(rec.path));
    
    return unvisitedRecommendations.length > 0 
      ? unvisitedRecommendations[0].path 
      : context.adaptiveRecommendations[0].path;
  };

  const isRouteRecommended = (route: string): boolean => {
    return context.adaptiveRecommendations.some(rec => rec.path === route);
  };

  const updateUserPreferences = (preferences: Partial<NavigationContext>) => {
    setContext(prev => {
      const updated = { ...prev, ...preferences };
      if (user) {
        localStorage.setItem(`nav_context_${user.id}`, JSON.stringify(updated));
      }
      return updated;
    });
  };

  const value: AdaptiveNavigationContextType = {
    context,
    recommendations: context.adaptiveRecommendations,
    navigationPatterns,
    adaptRoute,
    recordNavigation,
    getOptimalNextRoute,
    isRouteRecommended,
    updateUserPreferences,
    // Métodos del cache inteligente
    cacheStats: () => navigationCache.getStats(),
    predictions: () => navigationCache.getPredictions(),
    navigationHistory: () => navigationCache.getNavigationHistory(),
    cachePageData: navigationCache.cachePageData
  };

  return (
    <AdaptiveNavigationContext.Provider value={value}>
      {children}
    </AdaptiveNavigationContext.Provider>
  );
};

export const useAdaptiveNavigation = (): AdaptiveNavigationContextType => {
  const context = useContext(AdaptiveNavigationContext);
  if (!context) {
    throw new Error('useAdaptiveNavigation must be used within AdaptiveNavigationProvider');
  }
  return context;
};

export default AdaptiveNavigationContext;
