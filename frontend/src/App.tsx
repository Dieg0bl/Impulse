import React from 'react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter } from 'react-router-dom';
import { SessionProvider } from './providers/SessionProvider';
import ErrorBoundary from './components/ErrorBoundary';
import AppRouter from './components/AppRouter';
import './assets/styles.css';
import { FlagsProvider } from './contexts/FlagsContext';
import { AppProvider } from './contexts/AppContext';
import { NavigationProvider } from './contexts/NavigationContext';
import { ComplianceProvider } from './contexts/ComplianceContext';
import { A11yHelper } from './components/A11yHelper';
import * as Sentry from './telemetry/sentry';

/**
 * Componente principal de la aplicación con providers de contexto
 * y configuración de navegación empresarial.
 */

const queryClient = new QueryClient();
const App: React.FC = () => {
  return (
    <ErrorBoundary level="critical">
      <SessionProvider>
        <QueryClientProvider client={queryClient}>
          <BrowserRouter>
            <FlagsProvider>
              <AppProvider>
                <ComplianceProvider>
                  <NavigationProvider>
                    <AppRouter />
                  </NavigationProvider>
                </ComplianceProvider>
              </AppProvider>
            </FlagsProvider>
          </BrowserRouter>
        </QueryClientProvider>
      </SessionProvider>
    </ErrorBoundary>
  );
};

export default App;
