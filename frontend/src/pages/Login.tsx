import React, { useState } from 'react';
import { useAppContext } from '../contexts/AppContext.tsx';
import Button from '../components/Button.tsx';
import { logger } from '../utils/logger.ts';

const Login: React.FC = () => {
  const { state, login, navigate } = useAppContext();
  const { loading, errors } = state;
  
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.email || !formData.password) {
      return;
    }

    try {
      await login(formData.email, formData.password);
    } catch (error) {
      logger.error(
        'Error durante el proceso de login',
        'LoginPage',
        {
          email: formData.email,
          error: error instanceof Error ? error.message : String(error)
        }
      );
    }
  };

  return (
    <div className="auth-page">
      {/* Header simple para páginas de auth */}
      <header className="auth-header">
        <div className="auth-header-content">
          <button
            className="logo"
            onClick={() => navigate('home')}
            style={{ cursor: 'pointer', background: 'none', border: 'none', padding: 0 }}
            aria-label="Ir al inicio"
            tabIndex={0}
            onKeyDown={e => { if (e.key === 'Enter' || e.key === ' ') navigate('home'); }}
          >
            <span className="logo-icon">⚡</span>
            <span className="logo-text">IMPULSE</span>
          </button>
        </div>
      </header>

      <main className="auth-main">
        <div className="auth-container">
          <div className="auth-content">
            {/* Título y descripción */}
            <div className="auth-intro">
              <h1 className="auth-title">¡Bienvenido de vuelta!</h1>
              <p className="auth-subtitle">
                Inicia sesión para continuar con tus retos y validaciones
              </p>
            </div>

            {/* Formulario de login */}
            <form className="auth-form" onSubmit={handleSubmit}>
              {errors.auth && (
                <div className="error-message" role="alert">
                  <span className="error-icon">⚠️</span>
                  {errors.auth}
                </div>
              )}

              <div className="form-field">
                <label htmlFor="email" className="form-label">Email</label>
                <input
                  id="email"
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleInputChange}
                  placeholder="tu@email.com"
                  required
                  autoComplete="email"
                  className="form-input"
                  aria-describedby="email-help"
                />
                <div id="email-help" className="field-help">
                  Usa el email con el que te registraste
                </div>
              </div>

              <div className="form-field">
                <label htmlFor="password" className="form-label">Contraseña</label>
                <input
                  id="password"
                  type="password"
                  name="password"
                  value={formData.password}
                  onChange={handleInputChange}
                  placeholder="Tu contraseña"
                  required
                  autoComplete="current-password"
                  className="form-input"
                />
              </div>

              <Button
                type="submit"
                variant="primary"
                size="large"
                disabled={loading.auth || !formData.email || !formData.password}
                className="auth-submit-btn"
              >
                {loading.auth ? '⏳ Iniciando sesión...' : '🔑 Iniciar Sesión'}
              </Button>

              {/* Enlaces adicionales */}
              <div className="auth-links">
                <button 
                  type="button"
                  className="link-button"
                  onClick={() => navigate('forgot-password')}
                >
                  ¿Olvidaste tu contraseña?
                </button>
              </div>
            </form>

            {/* Link a registro */}
            <div className="auth-alternative">
              <p>¿No tienes cuenta?</p>
              <Button
                variant="secondary"
                onClick={() => navigate('register')}
                aria-label="Crear nueva cuenta"
              >
                Crear cuenta gratis
              </Button>
            </div>
          </div>

          {/* Panel lateral con beneficios */}
          <div className="auth-sidebar">
            <div className="sidebar-content">
              <h2 className="sidebar-title">¿Por qué IMPULSE?</h2>
              
              <div className="benefit-item">
                <span className="benefit-icon">🎯</span>
                <div>
                  <h3>Retos Reales</h3>
                  <p>Objetivos medibles con fechas límite reales</p>
                </div>
              </div>
              
              <div className="benefit-item">
                <span className="benefit-icon">👥</span>
                <div>
                  <h3>Validación Humana</h3>
                  <p>Personas reales verifican tu progreso</p>
                </div>
              </div>
              
              <div className="benefit-item">
                <span className="benefit-icon">🏆</span>
                <div>
                  <h3>Resultados Comprobables</h3>
                  <p>Evidencia real de tus logros</p>
                </div>
              </div>
              
              <div className="benefit-item">
                <span className="benefit-icon">⚖️</span>
                <div>
                  <h3>Consecuencias Reales</h3>
                  <p>Compromiso auténtico con tu futuro</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>

      {/* Footer simple */}
      <footer className="auth-footer">
        <div className="auth-footer-content">
          <p>&copy; 2024 IMPULSE. Convierte tus retos en realidad.</p>
          <div className="footer-links">
            <a href="/privacy" className="auth-footer-link">Privacidad</a>
            <a href="/terms" className="auth-footer-link">Términos</a>
            <a href="mailto:soporte@impulse.dev" className="auth-footer-link">Soporte</a>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Login;
