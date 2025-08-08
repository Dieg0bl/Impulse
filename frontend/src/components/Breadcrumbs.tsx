import React from 'react';
import { useNavigation } from '../contexts/NavigationContext.tsx';
import './Breadcrumbs.css';

/**
 * Componente empresarial de breadcrumbs con navegación inteligente.
 * Proporciona navegación contextual y orientación visual para usuarios.
 */

interface BreadcrumbsProps {
  separator?: string;
  maxItems?: number;
  showHome?: boolean;
  className?: string;
  variant?: 'default' | 'compact' | 'minimal';
}

export const Breadcrumbs: React.FC<BreadcrumbsProps> = ({
  separator = '/',
  maxItems = 5,
  showHome = true,
  className = '',
  variant = 'default'
}) => {
  const { navigationState, navigateTo, isCurrentPath } = useNavigation();
  const { breadcrumbs } = navigationState;

  // Agregar home si está habilitado y no está presente
  const allBreadcrumbs = showHome && !breadcrumbs.some(item => item.path === '/')
    ? [{ label: 'Inicio', path: '/', icon: '🏠', isActive: isCurrentPath('/') }, ...breadcrumbs]
    : breadcrumbs;

  // Limitar cantidad de items si es necesario
  const displayBreadcrumbs = maxItems && allBreadcrumbs.length > maxItems
    ? [
        ...allBreadcrumbs.slice(0, 1), // Primer item
        { label: '...', path: '', icon: '', isActive: false }, // Separador
        ...allBreadcrumbs.slice(-maxItems + 2) // Últimos items
      ]
    : allBreadcrumbs;

  const handleBreadcrumbClick = (path: string, event: React.MouseEvent) => {
    event.preventDefault();
    if (path && path !== '...') {
      navigateTo(path);
    }
  };

  const renderBreadcrumbItem = (item: any, index: number) => {
    const isLast = index === displayBreadcrumbs.length - 1;
    const isClickable = item.path && item.path !== '...' && !item.isActive;

    return (
      <React.Fragment key={`${item.path}-${index}`}>
        <li className={`breadcrumb-item ${item.isActive ? 'active' : ''} ${isClickable ? 'clickable' : ''}`}>
          {isClickable ? (
            <button
              type="button"
              className="breadcrumb-link"
              onClick={(e) => handleBreadcrumbClick(item.path, e)}
              aria-label={`Navegar a ${item.label}`}
            >
              {item.icon && (
                <span className="breadcrumb-icon" aria-hidden="true">
                  {item.icon}
                </span>
              )}
              <span className="breadcrumb-text">{item.label}</span>
            </button>
          ) : (
            <span className="breadcrumb-current">
              {item.icon && (
                <span className="breadcrumb-icon" aria-hidden="true">
                  {item.icon}
                </span>
              )}
              <span className="breadcrumb-text">{item.label}</span>
            </span>
          )}
        </li>
        
        {!isLast && (
          <li className="breadcrumb-separator" aria-hidden="true">
            <span>{separator}</span>
          </li>
        )}
      </React.Fragment>
    );
  };

  if (!displayBreadcrumbs.length) {
    return null;
  }

  return (
    <nav
      className={`breadcrumbs breadcrumbs--${variant} ${className}`}
      aria-label="Navegación por migas de pan"
      role="navigation"
    >
      <ol className="breadcrumb-list">
        {displayBreadcrumbs.map(renderBreadcrumbItem)}
      </ol>
    </nav>
  );
};

export default Breadcrumbs;
