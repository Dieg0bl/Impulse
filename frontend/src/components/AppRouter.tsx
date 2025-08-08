import React, { Suspense, useEffect } from 'react';
import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth.ts';
import { useNavigation } from '../contexts/NavigationContext.tsx';
import LoadingSpinner from './LoadingSpinner.tsx';
import ErrorBoundary from './ErrorBoundary.tsx';
import Header from './Header.tsx';
import BottomNav from './BottomNav.tsx';

// Componentes lazy loading para mejor rendimiento
const Home = React.lazy(() => import('../pages/Home.tsx'));
const Login = React.lazy(() => import('../pages/Login.tsx'));
const Register = React.lazy(() => import('../pages/Register.tsx'));
const Dashboard = React.lazy(() => import('../pages/Dashboard.tsx'));
const MisRetos = React.lazy(() => import('../pages/MisRetos.tsx'));
const CrearReto = React.lazy(() => import('../pages/CrearReto.tsx'));
const RetoDetalle = React.lazy(() => import('../pages/RetoDetalle.tsx'));
const ReportarAvance = React.lazy(() => import('../pages/ReportarAvance.tsx'));
const Perfil = React.lazy(() => import('../pages/Perfil.tsx'));
const Configuracion = React.lazy(() => import('../pages/Configuracion.tsx'));
const Validaciones = React.lazy(() => import('../pages/Validaciones.tsx'));
const Validadores = React.lazy(() => import('../pages/Validadores.tsx'));
const Notificaciones = React.lazy(() => import('../pages/Notificaciones.tsx'));
const Onboarding = React.lazy(() => import('../pages/Onboarding.tsx'));

/**
 * Router empresarial con deep linking, lazy loading y protección de rutas.
 * Implementa navegación segura y gestión avanzada de estado.
 */

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRoles?: string[];
  redirectTo?: string;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ 
  children, 
  requiredRoles = [], 
  redirectTo = '/login' 
}) => {
  const { isAuthenticated, user, loading } = useAuth();
  const { setLoading } = useNavigation();

  useEffect(() => {
    setLoading(loading);
  }, [loading, setLoading]);

  if (loading) {
    return <LoadingSpinner />;
  }

  if (!isAuthenticated) {
    return <Navigate to={redirectTo} replace />;
  }

  // Verificar roles si se especifican
  if (requiredRoles.length > 0 && user) {
    const hasRequiredRole = requiredRoles.some(role => 
      user.rol === role
    );
    
    if (!hasRequiredRole) {
      return <Navigate to="/dashboard" replace />;
    }
  }

  return <>{children}</>;
};

interface PublicRouteProps {
  children: React.ReactNode;
  redirectTo?: string;
}

const PublicRoute: React.FC<PublicRouteProps> = ({ 
  children, 
  redirectTo = '/dashboard' 
}) => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return <LoadingSpinner />;
  }

  if (isAuthenticated) {
    return <Navigate to={redirectTo} replace />;
  }

  return <>{children}</>;
};

const BreadcrumbManager: React.FC = () => {
  const location = useLocation();
  const { updateBreadcrumbs } = useNavigation();
  const { user } = useAuth();

  useEffect(() => {
    const generateBreadcrumbs = () => {
      const pathSegments = location.pathname.split('/').filter(Boolean);
      const breadcrumbs: Array<{
        label: string;
        path: string;
        icon: string;
        isActive: boolean;
      }> = [];

      // Mapeo de rutas a labels
      const routeLabels: Record<string, string> = {
        'dashboard': 'Panel Principal',
        'retos': 'Retos',
        'crear': 'Crear Reto',
        'avance': 'Reportar Avance',
        'perfil': 'Mi Perfil',
        'configuracion': 'Configuración',
        'validaciones': 'Validaciones',
        'validadores': 'Validadores',
        'notificaciones': 'Notificaciones',
        'onboarding': 'Bienvenida'
      };

      // Iconos para las rutas
      const routeIcons: Record<string, string> = {
        'dashboard': '📊',
        'retos': '🎯',
        'crear': '➕',
        'avance': '📈',
        'perfil': '👤',
        'configuracion': '⚙️',
        'validaciones': '✅',
        'validadores': '👥',
        'notificaciones': '🔔',
        'onboarding': '🚀'
      };

      let currentPath = '';
      
      pathSegments.forEach((segment, index) => {
        currentPath += `/${segment}`;
        
        // Verificar si es un ID (número o UUID)
        const isId = /^[0-9a-f-]{8,}$/i.test(segment) || /^\d+$/.test(segment);
        
        if (!isId) {
          const label = routeLabels[segment] || segment.charAt(0).toUpperCase() + segment.slice(1);
          const icon = routeIcons[segment];
          const isActive = currentPath === location.pathname;
          
          breadcrumbs.push({
            label,
            path: currentPath,
            icon,
            isActive
          });
        } else {
          // Para IDs, usar el contexto del segmento anterior
          const prevSegment = pathSegments[index - 1];
          let label = `Detalle`;
          
          if (prevSegment === 'retos') {
            label = `Reto #${segment.substring(0, 8)}`;
          }
          
          breadcrumbs.push({
            label,
            path: currentPath,
            icon: '📋',
            isActive: currentPath === location.pathname
          });
        }
      });

      updateBreadcrumbs(breadcrumbs);
    };

    generateBreadcrumbs();
  }, [location.pathname, updateBreadcrumbs, user]);

  return null;
};

const LayoutWrapper: React.FC<{ children: React.ReactNode; isAuthenticated: boolean }> = ({ 
  children, 
  isAuthenticated 
}) => {
  if (isAuthenticated) {
    return (
      <div className="app-authenticated">
        <Header />
        <main className="main-content">
          {children}
        </main>
        <BottomNav />
      </div>
    );
  }

  return (
    <div className="app-public">
      {children}
    </div>
  );
};

const AppRouter: React.FC = () => {
  const { isAuthenticated } = useAuth();

  return (
    <ErrorBoundary>
      <BreadcrumbManager />
      <LayoutWrapper isAuthenticated={isAuthenticated}>
        <Suspense fallback={<LoadingSpinner />}>
          <Routes>
            {/* Rutas públicas */}
            <Route 
              path="/" 
              element={
                <PublicRoute>
                  <Home />
                </PublicRoute>
              } 
            />
            <Route 
              path="/login" 
              element={
                <PublicRoute>
                  <Login />
                </PublicRoute>
              } 
            />
            <Route 
              path="/register" 
              element={
                <PublicRoute>
                  <Register />
                </PublicRoute>
              } 
            />

            {/* Rutas protegidas */}
            <Route 
              path="/dashboard" 
              element={
                <ProtectedRoute>
                  <Dashboard />
                </ProtectedRoute>
              } 
            />

            {/* Onboarding para nuevos usuarios */}
            <Route 
              path="/onboarding" 
              element={
                <ProtectedRoute>
                  <Onboarding />
                </ProtectedRoute>
              } 
            />

            {/* Gestión de retos */}
            <Route 
              path="/retos" 
              element={
                <ProtectedRoute requiredRoles={['USER', 'ADMIN']}>
                  <MisRetos />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="/retos/crear" 
              element={
                <ProtectedRoute requiredRoles={['USER', 'ADMIN']}>
                  <CrearReto />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="/retos/:id" 
              element={
                <ProtectedRoute requiredRoles={['USER', 'ADMIN']}>
                  <RetoDetalle />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="/retos/:id/avance" 
              element={
                <ProtectedRoute requiredRoles={['USER', 'ADMIN']}>
                  <ReportarAvance />
                </ProtectedRoute>
              } 
            />

            {/* Gestión de perfil */}
            <Route 
              path="/perfil" 
              element={
                <ProtectedRoute>
                  <Perfil />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="/configuracion" 
              element={
                <ProtectedRoute>
                  <Configuracion />
                </ProtectedRoute>
              } 
            />

            {/* Notificaciones */}
            <Route 
              path="/notificaciones" 
              element={
                <ProtectedRoute>
                  <Notificaciones />
                </ProtectedRoute>
              } 
            />

            {/* Rutas de administración */}
            <Route 
              path="/validaciones" 
              element={
                <ProtectedRoute requiredRoles={['ADMIN']}>
                  <Validaciones />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="/validadores" 
              element={
                <ProtectedRoute requiredRoles={['ADMIN']}>
                  <Validadores />
                </ProtectedRoute>
              } 
            />

            {/* Redirecciones por defecto */}
            <Route 
              path="*" 
              element={
                isAuthenticated ? (
                  <Navigate to="/dashboard" replace />
                ) : (
                  <Navigate to="/" replace />
                )
              } 
            />
          </Routes>
        </Suspense>
      </LayoutWrapper>
    </ErrorBoundary>
  );
};

export default AppRouter;
