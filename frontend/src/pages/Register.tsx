import React, { useState } from 'react';
import { useAppContext } from '../contexts/AppContext.tsx';
import Button from '../components/Button.tsx';
import { logger } from '../utils/logger.ts';

const Register: React.FC = () => {
  const { state, register, navigate } = useAppContext();
  const { loading, errors } = state;
  
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    confirmPassword: '',
    nombre: '',
    apellidos: '',
    telefono: '',
  acceptTerms: false,
  inviteCode: ''
  });

  const [formErrors, setFormErrors] = useState<Record<string, string>>({});

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
    
    // Limpiar error del campo cuando el usuario empiece a escribir
    if (formErrors[name]) {
      setFormErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const validateForm = () => {
    const newErrors: Record<string, string> = {};

    if (!formData.email) {
      newErrors.email = 'El email es obligatorio';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'El email no es válido';
    }

    if (!formData.password) {
      newErrors.password = 'La contraseña es obligatoria';
    } else if (formData.password.length < 6) {
      newErrors.password = 'La contraseña debe tener al menos 6 caracteres';
    }

    if (!formData.confirmPassword) {
      newErrors.confirmPassword = 'Confirma tu contraseña';
    } else if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = 'Las contraseñas no coinciden';
    }

    if (!formData.nombre) {
      newErrors.nombre = 'El nombre es obligatorio';
    }

    if (!formData.apellidos) {
      newErrors.apellidos = 'Los apellidos son obligatorios';
    }

    if (!formData.acceptTerms) {
      newErrors.acceptTerms = 'Debes aceptar los términos y condiciones';
    }

    if (formData.inviteCode && formData.inviteCode.length < 6) {
      newErrors.inviteCode = 'Código de invitación demasiado corto';
    }

    setFormErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    try {
      await register({
        email: formData.email,
        nombre: formData.nombre,
        apellidos: formData.apellidos,
        telefono: formData.telefono || undefined,
        inviteCode: formData.inviteCode || undefined
      });
    } catch (error) {
      logger.error('Error en registro', 'AUTH', error);
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
              <h1 className="auth-title">¡Únete a IMPULSE!</h1>
              <p className="auth-subtitle">
                Crea tu cuenta y empieza a convertir tus retos en realidad
              </p>
            </div>

            {/* Formulario de registro */}
            <form className="auth-form" onSubmit={handleSubmit}>
              {errors.auth && (
                <div className="error-message" role="alert">
                  <span className="error-icon">⚠️</span>
                  {errors.auth}
                </div>
              )}

              <div className="form-row">
                <div className="form-field">
                  <label htmlFor="nombre" className="form-label">Nombre *</label>
                  <input
                    id="nombre"
                    type="text"
                    name="nombre"
                    value={formData.nombre}
                    onChange={handleInputChange}
                    placeholder="Tu nombre"
                    required
                    className={`form-input ${formErrors.nombre ? 'error' : ''}`}
                  />
                  {formErrors.nombre && (
                    <div className="field-error">{formErrors.nombre}</div>
                  )}
                </div>

                <div className="form-field">
                  <label htmlFor="apellidos" className="form-label">Apellidos *</label>
                  <input
                    id="apellidos"
                    type="text"
                    name="apellidos"
                    value={formData.apellidos}
                    onChange={handleInputChange}
                    placeholder="Tus apellidos"
                    required
                    className={`form-input ${formErrors.apellidos ? 'error' : ''}`}
                  />
                  {formErrors.apellidos && (
                    <div className="field-error">{formErrors.apellidos}</div>
                  )}
                </div>
              </div>

              <div className="form-field">
                <label htmlFor="email" className="form-label">Email *</label>
                <input
                  id="email"
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleInputChange}
                  placeholder="tu@email.com"
                  required
                  autoComplete="email"
                  className={`form-input ${formErrors.email ? 'error' : ''}`}
                />
                {formErrors.email && (
                  <div className="field-error">{formErrors.email}</div>
                )}
              </div>

              <div className="form-field">
                <label htmlFor="telefono" className="form-label">Teléfono (opcional)</label>
                <input
                  id="telefono"
                  type="tel"
                  name="telefono"
                  value={formData.telefono}
                  onChange={handleInputChange}
                  placeholder="+34 600 000 000"
                  autoComplete="tel"
                  className="form-input"
                />
                <div className="field-help">
                  Para notificaciones importantes por WhatsApp
                </div>
              </div>

              <div className="form-field">
                <label htmlFor="inviteCode" className="form-label">Código de invitación (opcional)</label>
                <input
                  id="inviteCode"
                  type="text"
                  name="inviteCode"
                  value={formData.inviteCode}
                  onChange={handleInputChange}
                  placeholder="Ingresa tu código si tienes uno"
                  className={`form-input ${formErrors.inviteCode ? 'error' : ''}`}
                />
                {formErrors.inviteCode && (
                  <div className="field-error">{formErrors.inviteCode}</div>
                )}
                <div className="field-help">Nos ayuda a reconocer quién te invitó.</div>
              </div>

              <div className="form-row">
                <div className="form-field">
                  <label htmlFor="password" className="form-label">Contraseña *</label>
                  <input
                    id="password"
                    type="password"
                    name="password"
                    value={formData.password}
                    onChange={handleInputChange}
                    placeholder="Mínimo 6 caracteres"
                    required
                    autoComplete="new-password"
                    className={`form-input ${formErrors.password ? 'error' : ''}`}
                  />
                  {formErrors.password && (
                    <div className="field-error">{formErrors.password}</div>
                  )}
                </div>

                <div className="form-field">
                  <label htmlFor="confirmPassword" className="form-label">Confirmar Contraseña *</label>
                  <input
                    id="confirmPassword"
                    type="password"
                    name="confirmPassword"
                    value={formData.confirmPassword}
                    onChange={handleInputChange}
                    placeholder="Repite tu contraseña"
                    required
                    autoComplete="new-password"
                    className={`form-input ${formErrors.confirmPassword ? 'error' : ''}`}
                  />
                  {formErrors.confirmPassword && (
                    <div className="field-error">{formErrors.confirmPassword}</div>
                  )}
                </div>
              </div>

              <div className="form-field checkbox-field">
                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    name="acceptTerms"
                    checked={formData.acceptTerms}
                    onChange={handleInputChange}
                    required
                    className="checkbox-input"
                  />
                  <span className="checkbox-custom"></span>
                  <span className="checkbox-text">
                    Acepto los <a href="/terms" target="_blank">términos y condiciones</a> y la <a href="/privacy" target="_blank">política de privacidad</a> *
                  </span>
                </label>
                {formErrors.acceptTerms && (
                  <div className="field-error">{formErrors.acceptTerms}</div>
                )}
              </div>

              <Button
                type="submit"
                variant="primary"
                size="large"
                disabled={loading.auth}
                className="auth-submit-btn"
              >
                {loading.auth ? '⏳ Creando cuenta...' : '🚀 Crear mi cuenta'}
              </Button>
            </form>

            {/* Link a login */}
            <div className="auth-alternative">
              <p>¿Ya tienes cuenta?</p>
              <Button
                variant="secondary"
                onClick={() => navigate('login')}
                aria-label="Iniciar sesión"
              >
                Iniciar sesión
              </Button>
            </div>
          </div>

          {/* Panel lateral con información */}
          <div className="auth-sidebar">
            <div className="sidebar-content">
              <h2 className="sidebar-title">🎯 ¿Qué puedes lograr con IMPULSE?</h2>
              
              <div className="success-story">
                <h3>📚 María - Estudiante</h3>
                <p>"Logré estudiar 2 horas diarias durante 3 meses. Mis padres validaron mi progreso y ahora tengo la mejor nota de la clase."</p>
              </div>
              
              <div className="success-story">
                <h3>🏃‍♂️ Carlos - Oficinista</h3>
                <p>"Perdí 15kg en 6 meses. Mi hermano verificaba mis entrenamientos diarios. ¡La presión social funciona!"</p>
              </div>
              
              <div className="success-story">
                <h3>💰 Ana - Freelancer</h3>
                <p>"Ahorré 5000€ para mi viaje. Mi novio revisaba mis gastos semanalmente. Sin excusas, sin fallos."</p>
              </div>
              
              <div className="cta-sidebar">
                <h3>🔥 ¿Tu turno?</h3>
                <p>Sea cual sea tu objetivo, IMPULSE te ayuda a cumplirlo con <strong>compromiso real</strong>.</p>
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
            <a href="/privacy" className="footer-link">Privacidad</a>
            <a href="/terms" className="footer-link">Términos</a>
            <a href="mailto:soporte@impulse.dev" className="footer-link">Soporte</a>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Register;
