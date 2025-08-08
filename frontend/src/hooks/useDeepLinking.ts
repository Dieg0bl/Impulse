import { useEffect, useState, useCallback } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { useNavigation } from '../contexts/NavigationContext.tsx';

/**
 * Hook avanzado para gestión de deep linking y parámetros URL.
 * Proporciona funcionalidades empresariales para navegación y estado URL.
 */

interface DeepLinkState {
  currentRoute: string;
  routeParams: Record<string, string | undefined>;
  queryParams: URLSearchParams;
  isValidRoute: boolean;
  routeMetadata: any;
}

interface DeepLinkActions {
  // Navegación con preservación de estado
  navigateWithState: (path: string, state?: any) => void;
  
  // Gestión de parámetros
  updateQueryParam: (key: string, value: string | null) => void;
  updateQueryParams: (params: Record<string, string | null>) => void;
  getQueryParam: (key: string, defaultValue?: string) => string | null;
  
  // Parámetros de ruta
  getRouteParam: (key: string) => string | undefined;
  
  // Construcción de URLs
  buildUrl: (path: string, params?: Record<string, string>, query?: Record<string, string>) => string;
  
  // Historial
  goBackWithFallback: (fallbackPath: string) => void;
  
  // Compartir
  getShareableUrl: () => string;
  copyUrlToClipboard: () => Promise<boolean>;
  
  // Validación
  validateCurrentRoute: () => boolean;
}

export const useDeepLinking = (options?: {
  preserveQueryOnNavigate?: boolean;
  fallbackPath?: string;
}): [DeepLinkState, DeepLinkActions] => {
  const location = useLocation();
  const navigate = useNavigate();
  const params = useParams();
  const { resolveDeepLink, getUrlParam, updateUrlParams } = useNavigation();
  
  const { 
    preserveQueryOnNavigate = false, 
    fallbackPath = '/dashboard' 
  } = options || {};
  
  // Estado del deep link
  const [deepLinkState, setDeepLinkState] = useState<DeepLinkState>({
    currentRoute: location.pathname,
    routeParams: params,
    queryParams: new URLSearchParams(location.search),
    isValidRoute: true,
    routeMetadata: null
  });
  
  // Actualizar estado cuando cambie la ubicación
  useEffect(() => {
    const currentDeepLink = resolveDeepLink(new URLSearchParams(location.search));
    
    setDeepLinkState({
      currentRoute: location.pathname,
      routeParams: params,
      queryParams: new URLSearchParams(location.search),
      isValidRoute: currentDeepLink !== null,
      routeMetadata: currentDeepLink
    });
  }, [location, params, resolveDeepLink]);
  
  // Navegación con preservación de estado
  const navigateWithState = useCallback((path: string, state?: any) => {
    let targetPath = path;
    
    // Preservar query params si está habilitado
    if (preserveQueryOnNavigate && deepLinkState.queryParams.toString()) {
      const separator = path.includes('?') ? '&' : '?';
      targetPath = `${path}${separator}${deepLinkState.queryParams.toString()}`;
    }
    
    navigate(targetPath, { state });
  }, [navigate, preserveQueryOnNavigate, deepLinkState.queryParams]);
  
  // Actualizar un parámetro de query
  const updateQueryParam = useCallback((key: string, value: string | null) => {
    updateUrlParams({ [key]: value });
  }, [updateUrlParams]);
  
  // Actualizar múltiples parámetros de query
  const updateQueryParams = useCallback((params: Record<string, string | null>) => {
    updateUrlParams(params);
  }, [updateUrlParams]);
  
  // Obtener parámetro de query
  const getQueryParam = useCallback((key: string, defaultValue?: string): string | null => {
    return getUrlParam(key) || defaultValue || null;
  }, [getUrlParam]);
  
  // Obtener parámetro de ruta
  const getRouteParam = useCallback((key: string): string | undefined => {
    return deepLinkState.routeParams[key];
  }, [deepLinkState.routeParams]);
  
  // Construir URL completa
  const buildUrl = useCallback((
    path: string, 
    routeParams?: Record<string, string>, 
    queryParams?: Record<string, string>
  ): string => {
    let url = path;
    
    // Reemplazar parámetros de ruta
    if (routeParams) {
      Object.entries(routeParams).forEach(([key, value]) => {
        url = url.replace(`:${key}`, encodeURIComponent(value));
      });
    }
    
    // Agregar parámetros de query
    if (queryParams) {
      const searchParams = new URLSearchParams();
      Object.entries(queryParams).forEach(([key, value]) => {
        if (value !== null && value !== undefined) {
          searchParams.append(key, value);
        }
      });
      
      const queryString = searchParams.toString();
      if (queryString) {
        url += `?${queryString}`;
      }
    }
    
    return url;
  }, []);
  
  // Navegar hacia atrás con fallback
  const goBackWithFallback = useCallback((customFallbackPath?: string) => {
    const targetFallback = customFallbackPath || fallbackPath;
    if (window.history.length > 1) {
      navigate(-1);
    } else {
      navigate(targetFallback);
    }
  }, [navigate, fallbackPath]);
  
  // Obtener URL compartible
  const getShareableUrl = useCallback((): string => {
    return `${window.location.origin}${location.pathname}${location.search}${location.hash}`;
  }, [location]);
  
  // Copiar URL al portapapeles
  const copyUrlToClipboard = useCallback(async (): Promise<boolean> => {
    try {
      const url = getShareableUrl();
      await navigator.clipboard.writeText(url);
      return true;
    } catch (error) {
      console.error('Error al copiar URL al portapapeles:', error);
      return false;
    }
  }, [getShareableUrl]);
  
  // Validar ruta actual
  const validateCurrentRoute = useCallback((): boolean => {
    return deepLinkState.isValidRoute;
  }, [deepLinkState.isValidRoute]);
  
  const actions: DeepLinkActions = {
    navigateWithState,
    updateQueryParam,
    updateQueryParams,
    getQueryParam,
    getRouteParam,
    buildUrl,
    goBackWithFallback,
    getShareableUrl,
    copyUrlToClipboard,
    validateCurrentRoute
  };
  
  return [deepLinkState, actions];
};

/**
 * Hook específico para gestión de parámetros de query
 */
export const useQueryParams = () => {
  const [, actions] = useDeepLinking();
  
  return {
    get: actions.getQueryParam,
    set: actions.updateQueryParam,
    setMultiple: actions.updateQueryParams,
    build: actions.buildUrl
  };
};

/**
 * Hook específico para parámetros de ruta
 */
export const useRouteParams = () => {
  const [state, actions] = useDeepLinking();
  
  return {
    params: state.routeParams,
    get: actions.getRouteParam,
    build: actions.buildUrl
  };
};

/**
 * Hook para compartir URLs
 */
export const useUrlSharing = () => {
  const [, actions] = useDeepLinking();
  
  return {
    getShareableUrl: actions.getShareableUrl,
    copyToClipboard: actions.copyUrlToClipboard,
    share: async (title?: string, text?: string) => {
      const url = actions.getShareableUrl();
      
      if (navigator.share) {
        try {
          await navigator.share({
            title: title || document.title,
            text: text || 'Compartir enlace',
            url
          });
          return true;
        } catch (error) {
          console.error('Error al compartir:', error);
          return false;
        }
      } else {
        // Fallback: copiar al portapapeles
        return await actions.copyUrlToClipboard();
      }
    }
  };
};

export default useDeepLinking;
