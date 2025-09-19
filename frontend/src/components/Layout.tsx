import React, { useState, Suspense } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Home,
  Target,
  FileText,
  CheckSquare,
  CreditCard,
  User,
  Menu,
  X,
  Bell,
  Search
} from 'lucide-react';
import BetaBanner from './BetaBanner';
import Footer from './Footer';
import { Button } from './ui';
import { useAuth } from '../providers/AuthProvider';
import { useFlags } from '../contexts/FlagsContext';
import { UserRole } from '../types/enums';
import HealthBanner from './HealthBanner';
import ErrorBoundary from './ErrorBoundary';
import OfflineFallback from './OfflineFallback';
import AddToHomePrompt from './PWAInstallBanner';
import AccessDenied from './AccessDenied';
import CookieConsentBanner from './CookieConsentBanner';
import { useGlobalUIState } from '../contexts/GlobalUIStateContext';

// Ítems de navegación con RBAC y flags (única declaración)
const navigationItems = [
  { name: 'Dashboard', href: '/dashboard', icon: Home, roles: [UserRole.USER,UserRole.ADMIN,UserRole.VALIDATOR,UserRole.MODERATOR,UserRole.SUPPORT,UserRole.BILLING,UserRole.COACH] },
  { name: 'Retos', href: '/retos', icon: Target, roles: [UserRole.USER,UserRole.ADMIN,UserRole.COACH] },
  { name: 'Evidencias', href: '/evidencias', icon: FileText, roles: [UserRole.USER,UserRole.ADMIN,UserRole.VALIDATOR,UserRole.COACH] },
  { name: 'Validación', href: '/validaciones', icon: CheckSquare, roles: [UserRole.VALIDATOR,UserRole.ADMIN] },
  { name: 'Moderación', href: '/moderacion', icon: FileText, roles: [UserRole.MODERATOR,UserRole.ADMIN], flags: ['DSA_AMAR_ON'] },
  { name: 'Planes', href: '/planes', icon: CreditCard, roles: [UserRole.USER,UserRole.ADMIN,UserRole.BILLING], flags: ['BILLING_ON'] },
  { name: 'Facturas', href: '/facturas', icon: CreditCard, roles: [UserRole.USER,UserRole.ADMIN,UserRole.BILLING], flags: ['BILLING_ON'] },
  { name: 'Referidos', href: '/referidos/codigo', icon: User, roles: [UserRole.USER,UserRole.ADMIN], flags: ['REFERRALS_ON'] },
  { name: 'Perfil', href: '/perfil', icon: User, roles: [UserRole.USER,UserRole.ADMIN,UserRole.VALIDATOR,UserRole.MODERATOR,UserRole.SUPPORT,UserRole.BILLING,UserRole.COACH] },
];

function filterNavItems(items: any[], user: any, flags?: Record<string, boolean>) {
  return items.filter(item => {
    if (item.roles && user?.role && !item.roles.includes(user.role)) return false;
    if (item.flags?.some((flag: string) => !flags?.[flag])) return false;
    return true;
  });
}

// Local Header component to avoid referencing an external Header symbol
const Header: React.FC = () => (
  <div className="container-app flex items-center justify-between py-2">
    <Link to="/" className="flex items-center gap-2 font-bold text-xl" style={{ color: 'rgb(var(--color-primary))' }} aria-label="Ir al Dashboard">
      <span style={{ width: 32, height: 32, background: 'linear-gradient(90deg, rgb(var(--color-primary-dark)), rgb(var(--color-primary)))', borderRadius: 8, display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'rgb(var(--text-inverse))', fontWeight: 700, fontSize: 16 }}>I</span>
      <span className="ml-2">Impulse</span>
    </Link>
    <div />
  </div>
);


const Layout: React.FC<{ children?: React.ReactNode }> = ({ children }) => {
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const location = useLocation();
  const { user, logout } = useAuth();
  const { flags } = useFlags();
  const navItems = filterNavItems(navigationItems, user, flags);
  const isActiveRoute = (href: string) => location.pathname === href;
  const { uiState, message } = useGlobalUIState?.() || { uiState: undefined, message: undefined };

  // RBAC: acceso denegado global
  if (uiState === 'access-denied') {
    return (
      <div style={{ minHeight: '100vh', background: 'rgb(var(--surface-1))', color: 'rgb(var(--text-1))', display: 'flex', flexDirection: 'column' }}>
        <Header />
        <main className="flex-1 flex flex-col items-stretch">
          <AccessDenied />
        </main>
        <Footer />
      </div>
    );
  }

  // Estado offline global
  if (uiState === 'offline' || (typeof window !== 'undefined' && !window.navigator.onLine)) {
    return (
      <div style={{ minHeight: '100vh', background: 'rgb(var(--surface-1))', color: 'rgb(var(--text-1))', display: 'flex', flexDirection: 'column' }}>
        <Header />
        <main className="flex-1 flex flex-col items-stretch">
          <OfflineFallback />
        </main>
        <Footer />
      </div>
    );
  }

  return (
    <div style={{ minHeight: '100vh', background: 'rgb(var(--surface-1))', color: 'rgb(var(--text-1))', display: 'flex', flexDirection: 'column' }}>
      {/* Skip link for accessibility */}
      <a href="#main-content" className="skip-link">Saltar al contenido principal</a>
      <BetaBanner />
      {/* Header y navegación */}
      <nav className="app-header" role="navigation" aria-label="Principal">
        <div className="container-app flex items-center justify-between py-2">
          {/* Logo */}
          <Link to="/" className="flex items-center gap-2 font-bold text-xl" style={{ color: 'rgb(var(--color-primary))' }} aria-label="Ir al Dashboard">
            <span style={{ width: 32, height: 32, background: 'linear-gradient(90deg, rgb(var(--color-primary-dark)), rgb(var(--color-primary)))', borderRadius: 8, display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'rgb(var(--text-inverse))', fontWeight: 700, fontSize: 16 }}>I</span>
            <span className="ml-2">Impulse</span>
          </Link>
          {/* Navegación principal: horizontal en md+, hamburguesa en móvil */}
          <nav className="main-nav hidden md:flex items-center gap-4" aria-label="Navegación principal">
            {navItems.map((item: any) => {
              const active = isActiveRoute(item.href);
              const Icon = item.icon;
              return (
                <Link
                  key={item.name}
                  to={item.href}
                  className={`flex items-center gap-1 px-2 py-1 rounded-md font-medium transition-colors ${active ? 'bg-primary-100 text-primary-700' : 'text-primary-700 hover:bg-primary-50'}`}
                >
                  <Icon className="w-4 h-4" />
                  {item.name}
                </Link>
              );
            })}
          </nav>
          {/* Acciones y menú usuario */}
          <div className="flex items-center gap-2">
            <div className="hidden md:block">
              <label className="relative block" aria-label="Buscar">
                <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2" aria-hidden="true" style={{ color: 'rgb(var(--text-3))' }} />
                <input
                  type="text"
                  placeholder={'Buscar...'}
                  className="pl-10 pr-4 py-2 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent w-44"
                  style={{ border: '1px solid rgb(var(--surface-3))', background: 'rgb(var(--surface-0))', color: 'rgb(var(--text-1))' }}
                />
              </label>
            </div>
            <Button variant="ghost" size="sm" aria-label="Notificaciones">
              <Bell className="w-5 h-5" />
            </Button>
            {/* Menú de usuario */}
            <div className="relative group">
              <Button variant="ghost" size="sm" aria-label="Cuenta" className="flex items-center gap-2">
                <User className="w-6 h-6" />
                <span className="hidden md:inline text-sm font-medium sr-only">Cuenta</span>
              </Button>
              {/* Dropdown */}
              <div style={{ position: 'absolute', right: 0, marginTop: 8, width: 176, background: 'rgb(var(--surface-0))', border: '1px solid rgb(var(--surface-3))', borderRadius: 12, boxShadow: '0 4px 24px rgba(0,0,0,0.08)', opacity: 0, pointerEvents: 'none', transition: 'opacity 0.2s', zIndex: 50 }}
                className="shadow-lg opacity-0 group-hover:opacity-100 group-focus-within:opacity-100 group-hover:pointer-events-auto group-focus-within:pointer-events-auto">
                <Link to="/perfil" className="block px-4 py-2" style={{ color: 'rgb(var(--text-1))' }}>Mi perfil</Link>
                <Link to="/preferencias" className="block px-4 py-2" style={{ color: 'rgb(var(--text-1))' }}>Preferencias</Link>
                {/* Accesos condicionales por rol ocultos si no existe user.role */}
                <button className="w-full text-left px-4 py-2" style={{ color: 'rgb(var(--color-error))' }} onClick={logout}>Cerrar sesión</button>
              </div>
            </div>
            {/* Menú hamburguesa solo en móvil */}
            <div className="md:hidden">
              <Button
                variant="ghost"
                size="sm"
                onClick={() => setIsMobileMenuOpen(o => !o)}
                aria-expanded={isMobileMenuOpen}
                aria-controls="mobile-menu"
                aria-label={isMobileMenuOpen ? 'Cerrar menú' : 'Abrir menú'}
              >
                {isMobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
              </Button>
            </div>
          </div>
        </div>
        {/* Menú móvil: solo visible en md- */}
        <AnimatePresence>
          {isMobileMenuOpen && (
            <motion.div
              id="mobile-menu"
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: 'auto' }}
              exit={{ opacity: 0, height: 0 }}
              transition={{ duration: 0.2 }}
              style={{ borderTop: '1px solid rgb(var(--surface-3))', background: 'rgb(var(--surface-0))' }}
              className="md:hidden"
            >
              <div className="px-2 pt-2 pb-3 space-y-1" role="menu">
                {navItems.map((item: typeof navigationItems[number]) => {
                  const active = isActiveRoute(item.href);
                  const Icon = item.icon;
                  return (
                    <Link
                      key={item.name}
                      to={item.href}
                      onClick={() => setIsMobileMenuOpen(false)}
                      className={`flex items-center gap-2 px-3 py-2 rounded-lg font-medium transition-colors ${active ? 'bg-primary-100 text-primary-700' : 'text-primary-700 hover:bg-primary-50'}`}
                    >
                      <Icon className="w-5 h-5" />
                      {item.name}
                    </Link>
                  );
                })}
                {/* Buscador móvil */}
                <div className="pt-3 pb-2">
                  <label className="relative block" aria-label="Buscar">
                    <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2" aria-hidden="true" style={{ color: 'rgb(var(--text-3))' }} />
                    <input
                      type="text"
                      placeholder="Buscar..."
                      className="w-full pl-10 pr-4 py-2 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                      style={{ border: '1px solid rgb(var(--surface-3))', background: 'rgb(var(--surface-0))', color: 'rgb(var(--text-1))' }}
                    />
                  </label>
                </div>
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </nav>
      {/* HealthBanner: muestra estado de sistema, solo si feature flag activo */}
      {flags.healthBanner && <HealthBanner />}
      {/* Mensajería global homogénea */}
      {message && (
        <div className={`ui-message ui-message--${uiState}`}>{message}</div>
      )}
      {/* Main Content con ErrorBoundary y Suspense */}
      <main id="main-content" className="flex-1 container-app mt-4 mb-12" role="main">
        <ErrorBoundary>
          <Suspense fallback={<div className="ui-loading">Cargando…</div>}>
            <motion.div
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.25 }}
            >
              {children}
            </motion.div>
          </Suspense>
        </ErrorBoundary>
        {/* Prompt para instalar PWA */}
        <AddToHomePrompt />
      </main>
      {/* Footer */}
      <Footer />
      <CookieConsentBanner />
    </div>
  );
};

export default Layout;
