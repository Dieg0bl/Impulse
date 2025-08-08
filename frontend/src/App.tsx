import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { AppProvider } from './contexts/AppContext.tsx';
import { NavigationProvider } from './contexts/NavigationContext.tsx';
import { FlagsProvider } from './contexts/FlagsContext';
import AppRouter from './components/AppRouter.tsx';
import './assets/styles.css';

/**
 * Componente principal de la aplicación con providers de contexto
 * y configuración de navegación empresarial.
 */

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <FlagsProvider>
        <AppProvider>
          <NavigationProvider>
            <AppRouter />
          </NavigationProvider>
        </AppProvider>
      </FlagsProvider>
    </BrowserRouter>
  );
};

export default App;
