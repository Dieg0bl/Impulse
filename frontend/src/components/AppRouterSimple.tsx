import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth.ts';
import LoadingSpinner from './LoadingSpinner.tsx';
import ErrorBoundary from './ErrorBoundary.tsx';
import Header from './Header.tsx';
import BottomNav from './BottomNav.tsx';

// Importaciones directas temporales
import Home from '../pages/Home.tsx';
import Login from '../pages/Login.tsx';
import Register from '../pages/Register.tsx';
import Dashboard from '../pages/Dashboard.tsx';

/**
 * Router empresarial simplificado para verificar compilación
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

const AppRouter: React.FC = () => {
  const { isAuthenticated } = useAuth();

  return (
    <ErrorBoundary>
      <div className={isAuthenticated ? "app-authenticated" : "app-public"}>
        {isAuthenticated && <Header />}
        <main className={isAuthenticated ? "main-content" : ""}>
          <Routes>
            {/* Rutas básicas */}
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route 
              path="/dashboard" 
              element={
                <ProtectedRoute>
                  <Dashboard />
                </ProtectedRoute>
              } 
            />
            
            {/* Redirección por defecto */}
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
        </main>
        {isAuthenticated && <BottomNav />}
      </div>
    </ErrorBoundary>
  );
};

export default AppRouter;
