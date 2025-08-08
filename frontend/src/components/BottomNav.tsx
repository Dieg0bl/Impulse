import * as React from 'react';
import { useAppContext } from '../contexts/AppContext.tsx';

const BottomNav: React.FC = () => {
  const { state, navigate } = useAppContext();
  const { currentPage, currentUser } = state;

  // Solo mostrar navegación inferior en móvil para usuarios autenticados
  if (!currentUser) return null;

  const navItems = [
    {
      id: 'dashboard',
      icon: '🏠',
      label: 'Inicio',
      page: 'dashboard'
    },
    {
      id: 'mis-retos',
      icon: '🎯',
      label: 'Retos',
      page: 'mis-retos'
    },
    {
      id: 'crear-reto',
      icon: '➕',
      label: 'Crear',
      page: 'crear-reto'
    },
    {
      id: 'validaciones',
      icon: '✅',
      label: 'Validar',
      page: 'validaciones'
    },
    {
      id: 'perfil',
      icon: '👤',
      label: 'Perfil',
      page: 'perfil'
    }
  ];

  return (
    <nav className="bottom-nav" role="navigation" aria-label="Navegación inferior">
      <div className="bottom-nav-container">
        {navItems.map((item) => (
          <button
            key={item.id}
            className={`bottom-nav-item ${currentPage === item.page ? 'active' : ''}`}
            onClick={() => navigate(item.page)}
            aria-label={`Ir a ${item.label}`}
            aria-current={currentPage === item.page ? 'page' : undefined}
          >
            <span className="bottom-nav-icon">{item.icon}</span>
            <span className="bottom-nav-label">{item.label}</span>
          </button>
        ))}
      </div>
    </nav>
  );
};

export default BottomNav;
