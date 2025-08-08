import * as React from 'react';
import { useAppContext } from '../contexts/AppContext.tsx';

// Importar todas las páginas
import Home from '../pages/Home.tsx';
import Login from '../pages/Login.tsx';
import Register from '../pages/Register.tsx';
import Dashboard from '../pages/Dashboard.tsx';
import Perfil from '../pages/Perfil.tsx';
import CrearReto from '../pages/CrearReto.tsx';
import MisRetos from '../pages/MisRetos.tsx';
import Validadores from '../pages/Validadores.tsx';
import Validaciones from '../pages/Validaciones.tsx';
import Onboarding from '../pages/Onboarding.tsx';
import Notificaciones from '../pages/Notificaciones.tsx';
import Configuracion from '../pages/Configuracion.tsx';
import RetoDetalle from '../pages/RetoDetalle.tsx';
import ReportarAvance from '../pages/ReportarAvance.tsx';

// Componentes de layout
import Header from './Header.tsx';
import BottomNav from './BottomNav.tsx';
import LoadingSpinner from './LoadingSpinner.tsx';
import ErrorBoundary from './ErrorBoundary.tsx';

const AppRouter: React.FC = () => {
  const { state } = useAppContext();
  const { currentPage, isAuthenticated, loading } = state;

  // Mostrar spinner si está cargando la autenticación
  if (loading.auth) {
    return <LoadingSpinner message="Cargando aplicación..." />;
  }

  // Rutas que no requieren autenticación
  const publicPages = ['home', 'login', 'register'];
  const isPublicPage = publicPages.includes(currentPage);

  // Rutas que requieren autenticación
  const requiresAuth = !isPublicPage;

  // Si la página requiere autenticación pero no está autenticado, ir a login
  if (requiresAuth && !isAuthenticated) {
    return <Login />;
  }

  // Función para renderizar la página actual
  const renderCurrentPage = () => {
    switch (currentPage) {
      // Páginas públicas
      case 'home':
        return <Home />;
      case 'login':
        return <Login />;
      case 'register':
        return <Register />;
      
      // Páginas privadas
      case 'dashboard':
        return <Dashboard />;
      case 'perfil':
        return <Perfil />;
      case 'crear-reto':
        return <CrearReto />;
      case 'mis-retos':
        return <MisRetos />;
      case 'validadores':
        return <Validadores />;
      case 'validaciones':
        return <Validaciones />;
      case 'notificaciones':
        return <Notificaciones />;
      case 'configuracion':
        return <Configuracion />;
      case 'onboarding':
        return <Onboarding />;
      case 'reto-detalle':
        return <RetoDetalle />;
      case 'reportar-avance':
        return <ReportarAvance />;
      
      default:
        return isAuthenticated ? <Dashboard /> : <Home />;
    }
  };

  // Layout para páginas autenticadas
  if (isAuthenticated && !isPublicPage) {
    return (
      <ErrorBoundary>
        <div className="app-authenticated">
          <Header />
          <main className="main-content">
            {renderCurrentPage()}
          </main>
          <BottomNav />
        </div>
      </ErrorBoundary>
    );
  }

  // Layout para páginas públicas
  return (
    <ErrorBoundary>
      <div className="app-public">
        {renderCurrentPage()}
      </div>
    </ErrorBoundary>
  );
};

export default AppRouter;
