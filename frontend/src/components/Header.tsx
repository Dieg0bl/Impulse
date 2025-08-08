import * as React from 'react';
import { useAppContext } from '../contexts/AppContext.tsx';

const Header: React.FC = () => {
  const { state, navigate, logout, openModal } = useAppContext();
  const { currentUser, notificaciones, currentPage } = state;
  
  const notificacionesNoLeidas = notificaciones.filter(n => !n.leida).length;
  
  const handleNotificationClick = () => {
    openModal('notifications');
  };
  
  const handleProfileClick = () => {
    navigate('perfil');
  };
  
  const handleLogoClick = () => {
    navigate('dashboard');
  };

  return (
    <header className="header authenticated-header">
      <div className="header-content">
        {/* Logo y navegación principal */}
        <div className="header-left">
          <div className="logo" onClick={handleLogoClick} style={{ cursor: 'pointer' }}>
            <span className="logo-icon">⚡</span>
            <span className="logo-text">IMPULSE</span>
          </div>
          
          <nav className="main-nav" role="navigation" aria-label="Navegación principal">
            <button 
              className={`nav-btn ${currentPage === 'dashboard' ? 'active' : ''}`}
              onClick={() => navigate('dashboard')}
              aria-label="Ir al dashboard"
            >
              🏠 Dashboard
            </button>
            <button 
              className={`nav-btn ${currentPage === 'mis-retos' ? 'active' : ''}`}
              onClick={() => navigate('mis-retos')}
              aria-label="Ver mis retos"
            >
              🎯 Mis Retos
            </button>
            <button 
              className={`nav-btn ${currentPage === 'validaciones' ? 'active' : ''}`}
              onClick={() => navigate('validaciones')}
              aria-label="Ver validaciones pendientes"
            >
              ✅ Validaciones
            </button>
          </nav>
        </div>
        
        {/* Acciones del usuario */}
        <div className="header-right">
          {/* Botón de crear reto */}
          <button 
            className="btn btn-primary btn-create"
            onClick={() => navigate('crear-reto')}
            aria-label="Crear nuevo reto"
          >
            ➕ Crear Reto
          </button>
          
          {/* Notificaciones */}
          <button 
            className="notification-btn"
            onClick={handleNotificationClick}
            aria-label={`Notificaciones ${notificacionesNoLeidas > 0 ? `(${notificacionesNoLeidas} sin leer)` : ''}`}
          >
            🔔
            {notificacionesNoLeidas > 0 && (
              <span className="notification-badge">{notificacionesNoLeidas}</span>
            )}
          </button>
          
          {/* Menú de usuario */}
          <div className="user-menu">
            <button 
              className="user-avatar"
              onClick={handleProfileClick}
              aria-label="Ver perfil de usuario"
            >
              {currentUser?.avatar ? (
                <img src={currentUser.avatar} alt="Avatar del usuario" />
              ) : (
                <div className="avatar-placeholder">
                  {currentUser?.nombre?.charAt(0).toUpperCase()}
                </div>
              )}
            </button>
            
            <div className="user-dropdown">
              <div className="user-info">
                <span className="user-name">{currentUser?.nombre} {currentUser?.apellidos}</span>
                <span className="user-email">{currentUser?.email}</span>
              </div>
              
              <div className="dropdown-menu">
                <button 
                  className="dropdown-item"
                  onClick={() => navigate('perfil')}
                >
                  👤 Mi Perfil
                </button>
                <button 
                  className="dropdown-item"
                  onClick={() => navigate('configuracion')}
                >
                  ⚙️ Configuración
                </button>
                <hr className="dropdown-divider" />
                <button 
                  className="dropdown-item logout-btn"
                  onClick={logout}
                >
                  🚪 Cerrar Sesión
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;
