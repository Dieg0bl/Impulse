import { useState, useEffect, useCallback, useRef } from 'react';

// Tipos para el cache inteligente
interface CacheEntry<T> {
  data: T;
  timestamp: number;
  accessCount: number;
  lastAccess: number;
  priority: number;
  size: number;
}

interface CacheConfig {
  maxSize: number; // Tamaño máximo en MB
  maxAge: number; // Tiempo de vida en ms
  maxEntries: number; // Número máximo de entradas
  cleanupInterval: number; // Intervalo de limpieza en ms
  preloadThreshold: number; // Umbral para precarga automática
}

interface PredictiveCache {
  route: string;
  probability: number;
  userData?: any;
  preloaded: boolean;
}

// Configuración por defecto del cache
const DEFAULT_CONFIG: CacheConfig = {
  maxSize: 50, // 50MB
  maxAge: 30 * 60 * 1000, // 30 minutos
  maxEntries: 100,
  cleanupInterval: 5 * 60 * 1000, // 5 minutos
  preloadThreshold: 0.7 // 70% de probabilidad
};

// Calculadora de tamaño de datos
const calculateSize = (data: any): number => {
  const str = JSON.stringify(data);
  return new Blob([str]).size / (1024 * 1024); // En MB
};

// Algoritmo LRU con prioridades
class PriorityLRUCache<T> {
  private cache = new Map<string, CacheEntry<T>>();
  private config: CacheConfig;
  private currentSize = 0;

  constructor(config: CacheConfig = DEFAULT_CONFIG) {
    this.config = config;
  }

  set(key: string, data: T, priority: number = 1): void {
    const size = calculateSize(data);
    
    // Verificar si excede el tamaño máximo individual
    if (size > this.config.maxSize * 0.1) {
      console.warn(`Data too large for cache: ${key} (${size.toFixed(2)}MB)`);
      return;
    }

    // Limpiar espacio si es necesario
    this.cleanup();
    
    // Si ya existe, actualizar
    if (this.cache.has(key)) {
      const existing = this.cache.get(key)!;
      this.currentSize -= existing.size;
    }

    const entry: CacheEntry<T> = {
      data,
      timestamp: Date.now(),
      accessCount: 1,
      lastAccess: Date.now(),
      priority,
      size
    };

    this.cache.set(key, entry);
    this.currentSize += size;

    // Verificar límites después de agregar
    this.enforceLimit();
  }

  get(key: string): T | null {
    const entry = this.cache.get(key);
    
    if (!entry) return null;

    // Verificar si ha expirado
    if (Date.now() - entry.timestamp > this.config.maxAge) {
      this.delete(key);
      return null;
    }

    // Actualizar estadísticas de acceso
    entry.accessCount++;
    entry.lastAccess = Date.now();
    
    // Mover al final (LRU)
    this.cache.delete(key);
    this.cache.set(key, entry);

    return entry.data;
  }

  delete(key: string): boolean {
    const entry = this.cache.get(key);
    if (entry) {
      this.currentSize -= entry.size;
      return this.cache.delete(key);
    }
    return false;
  }

  has(key: string): boolean {
    const entry = this.cache.get(key);
    if (!entry) return false;
    
    // Verificar si ha expirado
    if (Date.now() - entry.timestamp > this.config.maxAge) {
      this.delete(key);
      return false;
    }
    
    return true;
  }

  clear(): void {
    this.cache.clear();
    this.currentSize = 0;
  }

  // Limpieza automática basada en LRU y prioridad
  cleanup(): void {
    const now = Date.now();
    const expiredKeys: string[] = [];

    for (const [key, entry] of this.cache.entries()) {
      if (now - entry.timestamp > this.config.maxAge) {
        expiredKeys.push(key);
      }
    }

    expiredKeys.forEach(key => this.delete(key));
  }

  // Estadísticas del cache
  getStats() {
    return {
      size: this.cache.size,
      currentSizeMB: this.currentSize,
      maxSizeMB: this.config.maxSize,
      utilizationPercent: (this.currentSize / this.config.maxSize) * 100
    };
  }

  private enforceLimit(): void {
    // Límite por número de entradas y tamaño
    while (this.cache.size > this.config.maxEntries || this.currentSize > this.config.maxSize) {
      // Encontrar la entrada menos importante para eliminar
      let leastImportantKey = '';
      let lowestScore = Infinity;

      for (const [key, entry] of this.cache.entries()) {
        const now = Date.now();
        const age = (now - entry.timestamp) / this.config.maxAge;
        const recency = (now - entry.lastAccess) / (60 * 1000);
        const score = (entry.priority * entry.accessCount) / (age + recency + 1);
        
        if (score < lowestScore) {
          lowestScore = score;
          leastImportantKey = key;
        }
      }

      if (leastImportantKey) {
        this.delete(leastImportantKey);
      } else {
        break;
      }
    }
  }
}

// Hook principal para cache inteligente
export const useIntelligentCache = <T>(
  config: Partial<CacheConfig> = {}
) => {
  const finalConfig = { ...DEFAULT_CONFIG, ...config };
  const cacheRef = useRef(new PriorityLRUCache<T>(finalConfig));
  const [predictiveCache, setPredictiveCache] = useState<PredictiveCache[]>([]);
  const navigationHistoryRef = useRef<string[]>([]);

  // Limpieza periódica
  useEffect(() => {
    const cleanup = () => {
      cacheRef.current.cleanup();
    };

    const interval = setInterval(cleanup, finalConfig.cleanupInterval);
    return () => clearInterval(interval);
  }, [finalConfig.cleanupInterval]);

  // Predicción de navegación
  const predictNextRoutes = useCallback((currentRoute: string): string[] => {
    const history = navigationHistoryRef.current;
    const predictions: { route: string; count: number }[] = [];

    // Buscar patrones en el historial
    for (let i = 0; i < history.length - 1; i++) {
      if (history[i] === currentRoute) {
        const nextRoute = history[i + 1];
        const existing = predictions.find(p => p.route === nextRoute);
        
        if (existing) {
          existing.count++;
        } else {
          predictions.push({ route: nextRoute, count: 1 });
        }
      }
    }

    // Ordenar por frecuencia y retornar top 3
    return predictions
      .sort((a, b) => b.count - a.count)
      .slice(0, 3)
      .map(p => p.route);
  }, []);

  // API principal del cache
  const cache = {
    // Operaciones básicas
    get: (key: string) => cacheRef.current.get(key),
    set: (key: string, data: T, priority: number = 1) => 
      cacheRef.current.set(key, data, priority),
    delete: (key: string) => cacheRef.current.delete(key),
    has: (key: string) => cacheRef.current.has(key),
    clear: () => cacheRef.current.clear(),
    
    // Actualizar historial de navegación
    recordNavigation: (route: string) => {
      navigationHistoryRef.current = [
        ...navigationHistoryRef.current.slice(-19), // Mantener últimas 20
        route
      ];
      
      // Actualizar predicciones
      const predictions = predictNextRoutes(route);
      setPredictiveCache(predictions.map((r, i) => ({
        route: r,
        probability: (3 - i) / 3, // 1.0, 0.67, 0.33
        preloaded: cacheRef.current.has(r)
      })));
    },

    // Estadísticas y debugging
    getStats: () => cacheRef.current.getStats(),
    getPredictions: () => predictiveCache,
    getNavigationHistory: () => navigationHistoryRef.current.slice(-10)
  };

  return cache;
};

// Hook especializado para datos de navegación
export const useNavigationCache = () => {
  const cache = useIntelligentCache<any>({
    maxSize: 20, // 20MB para datos de navegación
    maxAge: 15 * 60 * 1000, // 15 minutos
    maxEntries: 50
  });

  const cachePageData = useCallback(async (
    route: string,
    dataLoader: () => Promise<any>,
    priority: number = 1
  ) => {
    const cacheKey = `page_${route}`;
    
    // Verificar cache primero
    const cachedData = cache.get(cacheKey);
    if (cachedData) {
      return cachedData;
    }

    // Cargar y cachear
    try {
      const data = await dataLoader();
      cache.set(cacheKey, data, priority);
      return data;
    } catch (error) {
      console.error(`Failed to load data for route ${route}:`, error);
      throw error;
    }
  }, [cache]);

  return {
    ...cache,
    cachePageData
  };
};

export default useIntelligentCache;
