import React, { useState } from 'react';
import { useAuth } from '../hooks/useAuth.ts';
import Button from '../components/Button.tsx';

const Perfil: React.FC = () => {
  const { user, updateUser, logout } = useAuth();
  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  
  const [formData, setFormData] = useState({
    nombre: user?.nombre || '',
    apellidos: user?.apellidos || '',
    email: user?.email || '',
    consentimientos: user?.consentimientos || {
      marketing: false,
      cookies: true,
      analytics: false,
      comunicaciones: false,
      fechaAceptacion: ''
    }
  });

  const handleSave = async () => {
    try {
      setLoading(true);
      setError('');
      
      const response = await fetch('/api/usuarios/perfil', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify({
          ...formData,
          consentimientos: {
            ...formData.consentimientos,
            fechaAceptacion: new Date().toISOString()
          }
        })
      });

      if (!response.ok) {
        throw new Error('Error actualizando perfil');
      }

      const updatedUser = await response.json();
      updateUser(updatedUser);
      setIsEditing(false);
      setSuccess('Perfil actualizado correctamente');
      
      setTimeout(() => setSuccess(''), 3000);
      
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error actualizando perfil');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteAccount = async () => {
    if (!window.confirm('¿Estás seguro de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.')) {
      return;
    }

    try {
      setLoading(true);
      const response = await fetch('/api/usuarios/eliminar', {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });

      if (response.ok) {
        logout();
        window.location.href = '/';
      } else {
        throw new Error('Error eliminando cuenta');
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error eliminando cuenta');
    } finally {
      setLoading(false);
    }
  };

  if (!user) {
    return <div>Cargando perfil...</div>;
  }

  return (
    <div className="perfil-page">
      <div className="perfil-container">
        {/* Header */}
        <div className="perfil-header">
          <h1 className="perfil-title">👤 Mi Perfil</h1>
          <p className="perfil-subtitle">
            Gestiona tu información personal y preferencias
          </p>
        </div>

        {/* Mensajes */}
        {error && (
          <div className="error-message" role="alert">
            ⚠️ {error}
          </div>
        )}
        
        {success && (
          <div className="success-message" role="alert">
            ✅ {success}
          </div>
        )}

        {/* Información básica */}
        <div className="perfil-section">
          <div className="section-header">
            <h2 className="section-title">📋 Información Personal</h2>
            {!isEditing && (
              <Button onClick={() => setIsEditing(true)}>
                ✏️ Editar
              </Button>
            )}
          </div>

          <div className="form-grid">
            <div className="form-field">
              <label htmlFor="perfil-nombre">Nombre</label>
              {isEditing ? (
                <input
                  id="perfil-nombre"
                  type="text"
                  value={formData.nombre}
                  onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
                />
              ) : (
                <span className="field-value">{user.nombre}</span>
              )}
            </div>

            <div className="form-field">
              <label htmlFor="perfil-apellidos">Apellidos</label>
              {isEditing ? (
                <input
                  id="perfil-apellidos"
                  type="text"
                  value={formData.apellidos}
                  onChange={(e) => setFormData({ ...formData, apellidos: e.target.value })}
                />
              ) : (
                <span className="field-value">{user.apellidos}</span>
              )}
            </div>

            <div className="form-field">
              <label htmlFor="perfil-email">Email</label>
              {isEditing ? (
                <input
                  id="perfil-email"
                  type="email"
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                />
              ) : (
                <span className="field-value">{user.email}</span>
              )}
            </div>

            <div className="form-field">
              <span className="field-label">Estado</span>
              <span className={`status-badge status-${user.estado.toLowerCase()}`}>
                {user.estado === 'ACTIVO' && '🟢 Activo'}
                {user.estado === 'PENDIENTE' && '🟡 Pendiente'}
                {user.estado === 'SUSPENDIDO' && '🔴 Suspendido'}
                {user.estado === 'BAJA' && '⚫ Baja'}
              </span>
            </div>
          </div>

          {isEditing && (
            <div className="edit-actions">
              <Button onClick={() => setIsEditing(false)} disabled={loading}>
                Cancelar
              </Button>
              <Button onClick={handleSave} disabled={loading}>
                {loading ? 'Guardando...' : '💾 Guardar'}
              </Button>
            </div>
          )}
        </div>

        {/* Consentimientos RGPD */}
        <div className="perfil-section">
          <div className="section-header">
            <h2 className="section-title">🛡️ Privacidad y Consentimientos</h2>
          </div>

          <div className="consent-list">
            <div className="consent-item">
              <label className="consent-label">
                <input
                  type="checkbox"
                  checked={formData.consentimientos.cookies}
                  disabled
                  aria-label="Cookies técnicas (Requerido)"
                />
                <span className="consent-text">
                  <strong>Cookies técnicas</strong> (Requerido)
                  <br />
                  <small>Necesarias para el funcionamiento de la plataforma</small>
                </span>
              </label>
            </div>

            <div className="consent-item">
              <label className="consent-label">
                <input
                  type="checkbox"
                  checked={formData.consentimientos.analytics}
                  onChange={(e) => setFormData({
                    ...formData,
                    consentimientos: {
                      ...formData.consentimientos,
                      analytics: e.target.checked
                    }
                  })}
                  disabled={!isEditing}
                  aria-label="Analytics y métricas"
                />
                <span className="consent-text">
                  <strong>Analytics y métricas</strong>
                  <br />
                  <small>Nos ayuda a mejorar la plataforma</small>
                </span>
              </label>
            </div>

            <div className="consent-item">
              <label className="consent-label">
                <input
                  type="checkbox"
                  checked={formData.consentimientos.marketing}
                  onChange={(e) => setFormData({
                    ...formData,
                    consentimientos: {
                      ...formData.consentimientos,
                      marketing: e.target.checked
                    }
                  })}
                  disabled={!isEditing}
                  aria-label="Comunicaciones de marketing"
                />
                <span className="consent-text">
                  <strong>Comunicaciones de marketing</strong>
                  <br />
                  <small>Recibir novedades y ofertas personalizadas</small>
                </span>
              </label>
            </div>

            <div className="consent-item">
              <label className="consent-label">
                <input
                  type="checkbox"
                  checked={formData.consentimientos.comunicaciones}
                  onChange={(e) => setFormData({
                    ...formData,
                    consentimientos: {
                      ...formData.consentimientos,
                      comunicaciones: e.target.checked
                    }
                  })}
                  disabled={!isEditing}
                  aria-label="Comunicaciones del sistema"
                />
                <span className="consent-text">
                  <strong>Comunicaciones del sistema</strong>
                  <br />
                  <small>Notificaciones importantes sobre tu cuenta</small>
                </span>
              </label>
            </div>
          </div>

          {formData.consentimientos.fechaAceptacion && (
            <div className="consent-info">
              <small>
                Última actualización: {new Date(formData.consentimientos.fechaAceptacion).toLocaleDateString()}
              </small>
            </div>
          )}
        </div>

        {/* Seguridad */}
        <div className="perfil-section">
          <div className="section-header">
            <h2 className="section-title">🔐 Seguridad</h2>
          </div>

          <div className="security-options">
            <div className="security-item">
              <div className="security-info">
                <h3>Cambiar Contraseña</h3>
                <p>Actualiza tu contraseña regularmente para mantener tu cuenta segura</p>
              </div>
              <Button onClick={() => alert('Funcionalidad en desarrollo')}>
                🔑 Cambiar
              </Button>
            </div>

            <div className="security-item">
              <div className="security-info">
                <h3>Autenticación de Dos Factores</h3>
                <p>Añade una capa extra de seguridad a tu cuenta</p>
              </div>
              <Button onClick={() => alert('Funcionalidad en desarrollo')}>
                🔐 Configurar
              </Button>
            </div>

            <div className="security-item">
              <div className="security-info">
                <h3>Descargar Mis Datos</h3>
                <p>Descarga una copia de todos tus datos (RGPD)</p>
              </div>
              <Button onClick={() => alert('Funcionalidad en desarrollo')}>
                📥 Descargar
              </Button>
            </div>
          </div>
        </div>

        {/* Zona de peligro */}
        <div className="perfil-section danger-zone">
          <div className="section-header">
            <h2 className="section-title">⚠️ Zona de Peligro</h2>
          </div>

          <div className="danger-content">
            <div className="danger-item">
              <div className="danger-info">
                <h3>Eliminar Cuenta</h3>
                <p>
                  Esta acción eliminará permanentemente tu cuenta y todos tus datos. 
                  No se puede deshacer.
                </p>
              </div>
              <Button 
                onClick={handleDeleteAccount}
                disabled={loading}
                className="danger-button"
              >
                🗑️ Eliminar Cuenta
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Perfil;
