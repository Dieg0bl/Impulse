import React, { useState } from 'react';
import Button from '../components/Button.tsx';
import FormField from '../components/FormField.tsx';
import { useAppContext } from '../contexts/AppContext.tsx';
import { logger } from '../utils/logger.ts';

interface OnboardingData {
  // Datos personales
  nombre: string;
  apellidos: string;
  email: string;
  password: string;
  confirmPassword: string;
  fechaNacimiento: string;
  
  // Consentimientos RGPD
  consentimientos: {
    marketing: boolean;
    cookies: boolean;
    analytics: boolean;
    comunicaciones: boolean;
  };
  
  // Preferencias
  notificaciones: {
    email: boolean;
    push: boolean;
    sms: boolean;
  };
}

const Onboarding: React.FC = () => {
  const { navigate } = useAppContext();
  const [currentStep, setCurrentStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  
  const [formData, setFormData] = useState<OnboardingData>({
    nombre: '',
    apellidos: '',
    email: '',
    password: '',
    confirmPassword: '',
    fechaNacimiento: '',
    consentimientos: {
      marketing: false,
      cookies: true, // Necesarias para el funcionamiento
      analytics: false,
      comunicaciones: false
    },
    notificaciones: {
      email: true,
      push: false,
      sms: false
    }
  });

  const totalSteps = 4;

  const updateFormData = (field: string, value: any) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const updateConsentimiento = (field: keyof OnboardingData['consentimientos'], value: boolean) => {
    setFormData(prev => ({
      ...prev,
      consentimientos: {
        ...prev.consentimientos,
        [field]: value
      }
    }));
  };

  const updateNotificacion = (field: keyof OnboardingData['notificaciones'], value: boolean) => {
    setFormData(prev => ({
      ...prev,
      notificaciones: {
        ...prev.notificaciones,
        [field]: value
      }
    }));
  };

  const validateStep = (step: number): boolean => {
    switch (step) {
      case 1:
        return !!(formData.nombre && formData.apellidos && formData.email);
      case 2:
        return !!(formData.password && formData.confirmPassword && 
                 formData.password === formData.confirmPassword &&
                 formData.password.length >= 8);
      case 3:
        return formData.consentimientos.cookies; // Obligatorio
      case 4:
        return true; // Preferencias opcionales
      default:
        return false;
    }
  };

  const nextStep = () => {
    if (validateStep(currentStep) && currentStep < totalSteps) {
      setCurrentStep(prev => prev + 1);
      setError('');
    } else {
      setError('Por favor, completa todos los campos requeridos');
    }
  };

  const prevStep = () => {
    if (currentStep > 1) {
      setCurrentStep(prev => prev - 1);
      setError('');
    }
  };

  const handleSubmit = async () => {
    if (!validateStep(currentStep)) {
      setError('Por favor, revisa los datos ingresados');
      return;
    }

    try {
      setLoading(true);
      setError('');

      // Preparar datos para envío
      const registrationData = {
        ...formData,
        consentimientos: {
          ...formData.consentimientos,
          fechaAceptacion: new Date().toISOString()
        }
      };

      const response = await fetch('/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(registrationData)
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Error en el registro');
      }

  await response.json();
      
      // Registro exitoso - redirigir a login o dashboard
      localStorage.setItem('registrationSuccess', 'true');
      navigate('login');

    } catch (err) {
      logger.error(
        'Error durante el proceso de registro',
        'OnboardingPage',
        {
          error: err instanceof Error ? err.message : String(err),
          step: currentStep,
          userId: formData.email,
          timestamp: new Date().toISOString()
        }
      );
      setError(err instanceof Error ? err.message : 'Error en el registro');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="onboarding-page">
      <div className="onboarding-container">
        {/* Header */}
        <div className="onboarding-header">
          <h1 className="onboarding-title">
            <img src="/assets/rocket.svg" alt="Cohete" style={{ width: 32, height: 32, display: 'inline', verticalAlign: 'middle', marginRight: 8 }} />{' '}
            Únete a IMPULSE
          </h1>
          <p className="onboarding-subtitle">
            Crea tu cuenta y comienza tu viaje hacia el crecimiento personal
          </p>
        </div>

        {/* Progreso */}
        <div className="progress-indicator">
          <progress
            className="progress-bar"
            value={currentStep}
            max={totalSteps}
            aria-label={`Paso ${currentStep} de ${totalSteps}`}
          >
            {currentStep}
          </progress>
          <span className="progress-text">Paso {currentStep} de {totalSteps}</span>
        </div>

        {/* Formulario */}
        <div className="onboarding-form">
          {error && (
            <div className="error-message" role="alert">
              ⚠️ {error}
            </div>
          )}

          {/* Paso 1: Datos Personales */}
          {currentStep === 1 && (
            <div className="step-content">
              <h2 className="step-title">👤 Datos Personales</h2>
              <p className="step-description">
                Comencemos con tu información básica
              </p>

              <FormField
                id="nombre"
                label="Nombre"
                type="text"
                value={formData.nombre}
                onChange={(value) => updateFormData('nombre', value)}
                placeholder="Tu nombre"
                required
                maxLength={50}
              />

              <FormField
                id="apellidos"
                label="Apellidos"
                type="text"
                value={formData.apellidos}
                onChange={(value) => updateFormData('apellidos', value)}
                placeholder="Tus apellidos"
                required
                maxLength={100}
              />

              <FormField
                id="email"
                label="Email"
                type="email"
                value={formData.email}
                onChange={(value) => updateFormData('email', value)}
                placeholder="tu@email.com"
                required
              />

              <FormField
                id="fechaNacimiento"
                label="Fecha de Nacimiento"
                type="date"
                value={formData.fechaNacimiento}
                onChange={(value) => updateFormData('fechaNacimiento', value)}
                required
                max={new Date(Date.now() - 13 * 365 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]}
              />
            </div>
          )}

          {/* Paso 2: Contraseña */}
          {currentStep === 2 && (
            <div className="step-content">
              <h2 className="step-title">🔐 Seguridad</h2>
              <p className="step-description">
                Crea una contraseña segura para proteger tu cuenta
              </p>

              <FormField
                id="password"
                label="Contraseña"
                type="password"
                value={formData.password}
                onChange={(value) => updateFormData('password', value)}
                placeholder="Mínimo 8 caracteres"
                required
                minLength={8}
              />

              <FormField
                id="confirmPassword"
                label="Confirmar Contraseña"
                type="password"
                value={formData.confirmPassword}
                onChange={(value) => updateFormData('confirmPassword', value)}
                placeholder="Repite tu contraseña"
                required
                error={formData.confirmPassword && formData.password !== formData.confirmPassword 
                  ? 'Las contraseñas no coinciden' : undefined}
              />

              <div className="password-requirements">
                <h4>Requisitos de contraseña:</h4>
                <ul>
                  <li className={formData.password.length >= 8 ? 'valid' : ''}>
                    ✓ Mínimo 8 caracteres
                  </li>
                  <li className={/[A-Z]/.test(formData.password) ? 'valid' : ''}>
                    ✓ Una letra mayúscula
                  </li>
                  <li className={/[a-z]/.test(formData.password) ? 'valid' : ''}>
                    ✓ Una letra minúscula
                  </li>
                  <li className={/\d/.test(formData.password) ? 'valid' : ''}>
                    ✓ Un número
                  </li>
                </ul>
              </div>
            </div>
          )}

          {/* Paso 3: Consentimientos RGPD */}
          {currentStep === 3 && (
            <div className="step-content">
              <h2 className="step-title">🛡️ Privacidad y Consentimientos</h2>
              <p className="step-description">
                Tu privacidad es importante. Selecciona qué datos podemos usar.
              </p>

              <div className="consent-section">
                <div className="consent-item required">
                  <label className="consent-label">
                    <input
                      type="checkbox"
                      checked={formData.consentimientos.cookies}
                      onChange={(e) => updateConsentimiento('cookies', e.target.checked)}
                      disabled
                    />
                    <span className="consent-text">
                      <strong>Cookies técnicas (Requerido)</strong>
                      <br />
                      Necesarias para el funcionamiento básico de la plataforma
                    </span>
                  </label>
                </div>

                <div className="consent-item">
                  <label className="consent-label">
                    <input
                      type="checkbox"
                      checked={formData.consentimientos.analytics}
                      onChange={(e) => updateConsentimiento('analytics', e.target.checked)}
                    />
                    <span className="consent-text">
                      <strong>Analytics y métricas</strong>
                      <br />
                      Nos ayuda a mejorar la plataforma analizando el uso anónimo
                    </span>
                  </label>
                </div>

                <div className="consent-item">
                  <label className="consent-label">
                    <input
                      type="checkbox"
                      checked={formData.consentimientos.marketing}
                      onChange={(e) => updateConsentimiento('marketing', e.target.checked)}
                    />
                    <span className="consent-text">
                      <strong>Comunicaciones de marketing</strong>
                      <br />
                      Recibir novedades, consejos y ofertas personalizadas
                    </span>
                  </label>
                </div>

                <div className="consent-item">
                  <label className="consent-label">
                    <input
                      type="checkbox"
                      checked={formData.consentimientos.comunicaciones}
                      onChange={(e) => updateConsentimiento('comunicaciones', e.target.checked)}
                    />
                    <span className="consent-text">
                      <strong>Comunicaciones del sistema</strong>
                      <br />
                      Notificaciones importantes sobre tu cuenta y retos
                    </span>
                  </label>
                </div>
              </div>

              <div className="legal-links">
                <p>
                  Al registrarte, aceptas nuestros{' '}
                  <a href="/terminos" target="_blank" rel="noopener noreferrer">
                    Términos de Servicio
                  </a>{' '}
                  y{' '}
                  <a href="/privacidad" target="_blank" rel="noopener noreferrer">
                    Política de Privacidad
                  </a>
                </p>
              </div>
            </div>
          )}

          {/* Paso 4: Preferencias de Notificación */}
          {currentStep === 4 && (
            <div className="step-content">
              <h2 className="step-title">🔔 Preferencias de Notificación</h2>
              <p className="step-description">
                Configura cómo quieres recibir las notificaciones (puedes cambiar esto después)
              </p>

              <div className="notification-section">
                <div className="notification-item">
                  <label className="notification-label">
                    <input
                      type="checkbox"
                      checked={formData.notificaciones.email}
                      onChange={(e) => updateNotificacion('email', e.target.checked)}
                    />
                    <span className="notification-text">
                      <strong>📧 Email</strong>
                      <br />
                      Recibir notificaciones por correo electrónico
                    </span>
                  </label>
                </div>

                <div className="notification-item">
                  <label className="notification-label">
                    <input
                      type="checkbox"
                      checked={formData.notificaciones.push}
                      onChange={(e) => updateNotificacion('push', e.target.checked)}
                    />
                    <span className="notification-text">
                      <strong>🔔 Push</strong>
                      <br />
                      Notificaciones push en el navegador
                    </span>
                  </label>
                </div>

                <div className="notification-item">
                  <label className="notification-label">
                    <input
                      type="checkbox"
                      checked={formData.notificaciones.sms}
                      onChange={(e) => updateNotificacion('sms', e.target.checked)}
                    />
                    <span className="notification-text">
                      <strong>📱 SMS</strong>
                      <br />
                      Mensajes de texto para notificaciones urgentes
                    </span>
                  </label>
                </div>
              </div>
            </div>
          )}

          {/* Navegación */}
          <div className="step-navigation">
            {currentStep > 1 && (
              <Button onClick={prevStep} disabled={loading}>
                ← Anterior
              </Button>
            )}
            
            <div className="nav-spacer"></div>
            
            {currentStep < totalSteps ? (
              <Button onClick={nextStep} disabled={!validateStep(currentStep) || loading}>
                Siguiente →
              </Button>
            ) : (
              <Button onClick={handleSubmit} disabled={loading}>
                {loading ? 'Creando cuenta...' : '🚀 Crear Cuenta'}
              </Button>
            )}
          </div>
        </div>

        {/* Footer */}
        <div className="onboarding-footer">
          <p>
            ¿Ya tienes cuenta?{' '}
            <button 
              type="button" 
              className="login-link" 
              onClick={() => navigate('login')}
            >
              Inicia sesión aquí
            </button>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Onboarding;
