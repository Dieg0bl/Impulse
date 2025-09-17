import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Layout from "./components/Layout";
import UserList from "./components/UserList";
import BookingForm from "./components/BookingForm";
import Challenges from "./pages/Challenges";
import Pricing from "./pages/Pricing";
import ErrorBoundary from "./components/ErrorBoundary";
import { AppThemeProvider, useInitializeTheme } from "./theme/muiTheme";
import PWAInstallBanner from "./components/PWAInstallBanner";
import PWAUpdatePrompt from "./components/PWAUpdatePrompt";
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
          <BrowserRouter>
            <Layout>
              <Routes>
                <Route path="/" element={<UserList usuarios={[]} />} />
                <Route path="/users" element={<UserList usuarios={[]} />} />
                <Route path="/booking" element={<BookingForm />} />
                <Route path="/challenges" element={<Challenges />} />
                <Route path="/pricing" element={<Pricing />} />
                <Route path="*" element={<div>Página no encontrada</div>} />
              </Routes>
            </Layout>

            {/* PWA Components */}
            <PWAInstallBanner />
            <PWAUpdatePrompt />
          </BrowserRouter>
        </ErrorBoundary>
      </ThemeInitializer>
    </AppThemeProvider>
  );
};

export default App;
