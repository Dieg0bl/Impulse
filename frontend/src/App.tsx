import React from "react";
import { BrowserRouter } from "react-router-dom";
import ErrorBoundary from "./components/ErrorBoundary";
import { AppThemeProvider, useInitializeTheme } from "./theme/muiTheme";
import PWAInstallBanner from "./components/PWAInstallBanner";
import PWAUpdatePrompt from "./components/PWAUpdatePrompt";
import AppRouter from "./components/AppRouter";
import { AuthProvider } from "./providers/AuthProvider";
import CookieConsentBanner from "./components/CookieConsentBanner";
import "./assets/styles.css";

// Componente para inicializar tema
const ThemeInitializer: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  useInitializeTheme();
  return <>{children}</>;
};


const App: React.FC = () => {
  return (
    <AppThemeProvider>
      <ThemeInitializer>
        <ErrorBoundary>
          <AuthProvider>
            <BrowserRouter>
              <AppRouter />
              <PWAInstallBanner />
              <PWAUpdatePrompt />
              <CookieConsentBanner />
            </BrowserRouter>
          </AuthProvider>
        </ErrorBoundary>
      </ThemeInitializer>
    </AppThemeProvider>
  );
};

export default App;
