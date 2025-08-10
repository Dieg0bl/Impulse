import React, { createContext, useContext, useState, useCallback, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

/**
 * Context de navegacion empresarial con deep linking.
 */

interface BreadcrumbItem {
  label: string;
  path: string;
  icon?: string;
  isActive: boolean;
}

interface NavigationState {
  currentPath: string;
  previousPath: string | null;
  breadcrumbs: BreadcrumbItem[];
  urlParams: URLSearchParams;
  isLoading: boolean;
  canGoBack: boolean;
  navigationHistory: string[];
}

interface NavigationContextType {
  navigationState: NavigationState;
  navigateTo: (path: string, options?: { replace?: boolean }) => void;
  goBack: () => void;
  updateBreadcrumbs: (items: BreadcrumbItem[]) => void;
  setLoading: (loading: boolean) => void;
  updateUrlParams: (params: Record<string, string | null>) => void;
  getUrlParam: (key: string) => string | null;
  isCurrentPath: (path: string) => boolean;
  resolveDeepLink: (params: URLSearchParams) => string;
}

const NavigationContext = createContext<NavigationContextType | undefined>(undefined);

export const NavigationProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const navigate = useNavigate();
  const location = useLocation();
  
  const [navigationState, setNavigationState] = useState<NavigationState>({
    currentPath: location.pathname,
    previousPath: null,
    breadcrumbs: [],
    urlParams: new URLSearchParams(location.search),
    isLoading: false,
    canGoBack: false,
    navigationHistory: [location.pathname]
  });
  
  useEffect(() => {
    setNavigationState(prev => ({
      ...prev,
      previousPath: prev.currentPath,
      currentPath: location.pathname,
      urlParams: new URLSearchParams(location.search),
      navigationHistory: [...prev.navigationHistory.slice(-10), location.pathname],
      canGoBack: prev.navigationHistory.length > 1
    }));
  }, [location]);
  
  const navigateTo = useCallback((path: string, options: { replace?: boolean } = {}) => {
    if (options.replace) {
      navigate(path, { replace: true });
    } else {
      navigate(path);
    }
  }, [navigate]);
  
  const goBack = useCallback(() => {
    if (navigationState.canGoBack) {
      navigate(-1);
    }
  }, [navigate, navigationState.canGoBack]);
  
  const updateBreadcrumbs = useCallback((items: BreadcrumbItem[]) => {
    setNavigationState(prev => ({
      ...prev,
      breadcrumbs: items
    }));
  }, []);
  
  const setLoading = useCallback((loading: boolean) => {
    setNavigationState(prev => ({ ...prev, isLoading: loading }));
  }, []);
  
  const updateUrlParams = useCallback((params: Record<string, string | null>) => {
    const newParams = new URLSearchParams(navigationState.urlParams);
    
    Object.entries(params).forEach(([key, value]) => {
      if (value === null) {
        newParams.delete(key);
      } else {
        newParams.set(key, value);
      }
    });
    
    const newSearch = newParams.toString();
    const newPath = `${location.pathname}${newSearch ? `?${newSearch}` : ''}`;
    
    navigate(newPath, { replace: true });
  }, [navigate, location.pathname, navigationState.urlParams]);
  
  const getUrlParam = useCallback((key: string): string | null => {
    return navigationState.urlParams.get(key);
  }, [navigationState.urlParams]);

  const isCurrentPath = useCallback((path: string): boolean => {
    return navigationState.currentPath === path;
  }, [navigationState.currentPath]);

  const resolveDeepLink = useCallback((params: URLSearchParams): string => {
    const redirectTo = params.get('redirect');
    if (redirectTo && redirectTo.startsWith('/')) {
      return redirectTo;
    }
    return '/';
  }, []);
  
  const contextValue: NavigationContextType = {
    navigationState,
    navigateTo,
    goBack,
    updateBreadcrumbs,
    setLoading,
    updateUrlParams,
    getUrlParam,
    isCurrentPath,
    resolveDeepLink
  };
  
  return (
    <NavigationContext.Provider value={contextValue}>
      {children}
    </NavigationContext.Provider>
  );
};

export const useNavigation = (): NavigationContextType => {
  const context = useContext(NavigationContext);
  if (context === undefined) {
    throw new Error('useNavigation debe ser usado dentro de NavigationProvider');
  }
  return context;
};

export default NavigationContext;
