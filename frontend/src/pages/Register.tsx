import React, { useState } from 'react';
import Button from '../components/Button';
import { useRegister } from '../hooks/useRegister';

const Register: React.FC = () => {
  const { register, loading, error } = useRegister();
  const [success, setSuccess] = useState(false);
  const navigate = (route: string) => window.location.assign(`/${route}`);
  
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    confirmPassword: '',
    nombre: '',
    apellidos: '',
    telefono: '',
    acceptTerms: false
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

    setFormErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateForm()) return;
    try {
      await register(formData.email, formData.password, formData.nombre);
      setSuccess(true);
    } catch (err) {
      // El error ya se maneja en el hook
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
              {error && (
                <div className="error-message" role="alert">
                  <span className="error-icon">⚠️</span>
                  {error}
                </div>
              )}
              {success && (
                <div className="success-message" role="status">
                  <span className="success-icon">✅</span>
                  ¡Registro exitoso! Revisa tu correo para verificar tu cuenta.
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
              </div>

              <div className="form-field">
                <label htmlFor="password" className="form-label">Contraseña</label>
                <input
                  id="password"
                  type="password"
                  name="password"
                  value={formData.password}
                  onChange={handleInputChange}
                  required
                  className="form-input"
                  aria-describedby="password-help"
                />
                <div id="password-help" className="field-help">
                  Mínimo 6 caracteres
                </div>
              </div>

              <div className="form-field">
                <label htmlFor="confirmPassword" className="form-label">Confirmar contraseña</label>
                <input
                  id="confirmPassword"
                  type="password"
                  name="confirmPassword"
                  value={formData.confirmPassword}
                  onChange={handleInputChange}
                  required
                  className="form-input"
                  aria-describedby="confirmPassword-help"
                />
                <div id="confirmPassword-help" className="field-help">
                  Repite la contraseña
                </div>
              </div>

              <div className="form-field">
                <label htmlFor="nombre" className="form-label">Nombre</label>
                <input
                  id="nombre"
                  type="text"
                  name="nombre"
                  value={formData.nombre}
                  onChange={handleInputChange}
                  required
                  className="form-input"
                />
              </div>

              <div className="form-field">
                <label htmlFor="apellidos" className="form-label">Apellidos</label>
                <input
                  id="apellidos"
                  type="text"
                  name="apellidos"
                  value={formData.apellidos}
                  onChange={handleInputChange}
                  required
                  className="form-input"
                />
              </div>

              <div className="form-field">
                <label htmlFor="telefono" className="form-label">Teléfono</label>
                <input
                  id="telefono"
                  type="tel"
                  name="telefono"
                  value={formData.telefono}
                  onChange={handleInputChange}
                  className="form-input"
                />
              </div>

              <div className="form-field">
                <label className="form-label">
                  <input
                    type="checkbox"
                    name="acceptTerms"
                    checked={formData.acceptTerms}
                    onChange={handleInputChange}
                    required
                    aria-required="true"
                  />
                  {' '}Acepto los <a href="/terminos" target="_blank" rel="noopener noreferrer">términos y condiciones</a> y la <a href="/privacidad" target="_blank" rel="noopener noreferrer">política de privacidad</a>
                </label>
                {formErrors.acceptTerms && <div className="field-error" aria-live="polite">{formErrors.acceptTerms}</div>}
              </div>

              <Button type="submit" disabled={loading}>Registrarse</Button>
              {success && <div className="success-message" role="status">¡Registro exitoso! Ahora puedes iniciar sesión.</div>}
              {error && <div className="error-message" role="alert">{error}</div>}
            </form>
          </div>
        </div>
      </main>
    </div>
  );
};

export default Register;
