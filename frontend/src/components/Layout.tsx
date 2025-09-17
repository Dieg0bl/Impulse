import React, { useState } from 'react';
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
  Search,
  Settings
} from 'lucide-react';
import BetaBanner from './BetaBanner';
import Footer from './Footer';
import { Button } from './ui';

// Navigation items
const navigationItems = [
  { name: 'Dashboard', href: '/', icon: Home },
  { name: 'Retos', href: '/challenges', icon: Target },
  { name: 'Evidencias', href: '/evidences', icon: FileText },
  { name: 'Validación', href: '/validator', icon: CheckSquare },
  { name: 'Planes', href: '/pricing', icon: CreditCard },
  { name: 'Cuenta', href: '/account', icon: User },
];

const Layout: React.FC<{ children?: React.ReactNode }> = ({ children }) => {
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const location = useLocation();

  const isActiveRoute = (href: string) => location.pathname === href;

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      {/* Skip link for accessibility */}
      <a href="#main-content" className="skip-link">Saltar al contenido principal</a>

      {/* Navigation */}
      <nav className="bg-white shadow-soft border-b border-gray-200 sticky top-0 z-40" role="navigation" aria-label="Principal">
        <div className="container-app">
          <div className="flex justify-between h-14 md:h-16">
            {/* Logo + Desktop Nav */}
            <div className="flex items-center">
              <div className="flex-shrink-0 flex items-center mr-2 md:mr-4">
                <Link to="/" className="flex items-center space-x-2 group" aria-label="Ir al Dashboard">
                  <div className="w-8 h-8 bg-gradient-to-r from-primary-600 to-primary-700 rounded-lg flex items-center justify-center" aria-hidden="true">
                    <span className="text-white font-bold text-sm">I</span>
                  </div>
                  <span className="text-lg md:text-xl font-bold bg-gradient-to-r from-primary-600 to-primary-800 bg-clip-text text-transparent">
                    Impulse
                  </span>
                </Link>
              </div>

              <div className="hidden md:flex md:space-x-1 lg:space-x-2" role="menubar">
                {navigationItems.map((item) => {
                  const active = isActiveRoute(item.href);
                  const Icon = item.icon;
                  return (
                    <Link
                      key={item.name}
                      to={item.href}
                      role="menuitem"
                      aria-current={active ? 'page' : undefined}
                      className={`relative inline-flex items-center px-3 py-2 rounded-lg text-sm font-medium transition-all duration-200 focus-outline touch-target ${
                        active
                          ? 'text-primary-700 bg-primary-50'
                          : 'text-gray-700 hover:text-primary-600 hover:bg-gray-50'
                      }`}
                    >
                      <Icon className="w-4 h-4 mr-2" aria-hidden="true" />
                      {item.name}
                      {active && (
                        <motion.div
                          className="absolute bottom-0 left-0 right-0 h-0.5 bg-primary-600 rounded-full"
                          layoutId="activeTab"
                          transition={{ type: 'spring', stiffness: 500, damping: 30 }}
                        />
                      )}
                    </Link>
                  );
                })}
              </div>
            </div>

            {/* Right side */}
            <div className="flex items-center space-x-2 md:space-x-4">
              {/* Search (desktop) */}
              <div className="hidden md:block">
                <label className="relative block" aria-label="Buscar">
                  <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" aria-hidden="true" />
                  <input
                    type="text"
                    placeholder="Buscar..."
                    className="pl-10 pr-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent bg-white w-56"
                  />
                </label>
              </div>

              <Button variant="ghost" size="sm" className="relative focus-outline" aria-label="Notificaciones">
                <Bell className="w-5 h-5" aria-hidden="true" />
                <span className="absolute -top-1 -right-1 w-3 h-3 bg-error-500 rounded-full" aria-hidden="true">
                  <span className="w-1.5 h-1.5 bg-white rounded-full block" />
                </span>
              </Button>

              <Button variant="ghost" size="sm" aria-label="Configuración" className="focus-outline">
                <Settings className="w-5 h-5" aria-hidden="true" />
              </Button>

              {/* Mobile menu toggle */}
              <div className="md:hidden">
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={() => setIsMobileMenuOpen(o => !o)}
                  aria-expanded={isMobileMenuOpen}
                  aria-controls="mobile-menu"
                  aria-label={isMobileMenuOpen ? 'Cerrar menú' : 'Abrir menú'}
                  className="focus-outline"
                >
                  {isMobileMenuOpen ? <X className="w-5 h-5" aria-hidden="true" /> : <Menu className="w-5 h-5" aria-hidden="true" />}
                </Button>
              </div>
            </div>
          </div>
        </div>

        {/* Mobile Navigation */}
        <AnimatePresence>
          {isMobileMenuOpen && (
            <motion.div
              id="mobile-menu"
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: 'auto' }}
              exit={{ opacity: 0, height: 0 }}
              transition={{ duration: 0.2 }}
              className="md:hidden border-t border-gray-200 bg-white"
            >
              <div className="px-2 pt-2 pb-3 space-y-1" role="menu">
                {navigationItems.map((item) => {
                  const active = isActiveRoute(item.href);
                  const Icon = item.icon;
                  return (
                    <Link
                      key={item.name}
                      to={item.href}
                      onClick={() => setIsMobileMenuOpen(false)}
                      role="menuitem"
                      aria-current={active ? 'page' : undefined}
                      className={`flex items-center px-3 py-2 rounded-lg text-base font-medium transition-all duration-200 focus-outline touch-target ${
                        active
                          ? 'text-primary-700 bg-primary-50 border-l-4 border-primary-600'
                          : 'text-gray-700 hover:text-primary-600 hover:bg-gray-50'
                      }`}
                    >
                      <Icon className="w-5 h-5 mr-3" aria-hidden="true" />
                      {item.name}
                    </Link>
                  );
                })}

                {/* Mobile Search */}
                <div className="pt-3 pb-2">
                  <label className="relative block" aria-label="Buscar">
                    <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" aria-hidden="true" />
                    <input
                      type="text"
                      placeholder="Buscar..."
                      className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    />
                  </label>
                </div>
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </nav>

      {/* Inline Beta Banner inside main container for less vertical shift */}
      <div className="container-app mt-2" aria-live="polite">
        <BetaBanner />
      </div>

      {/* Main Content */}
      <main id="main-content" className="flex-1 container-app mt-4 mb-12" role="main">
        <motion.div
          initial={{ opacity: 0, y: 16 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.25 }}
        >
          {children}
        </motion.div>
      </main>

      {/* Footer */}
      <Footer />
    </div>
  );
};

export default Layout;
