import React, { useState } from 'react';
import { useCompliance } from '../contexts/ComplianceContext';
import Button from '../components/Button.tsx';
import { usePerfil } from '../hooks/usePerfil';
import { z } from 'zod';
import Skeleton from '../components/Skeleton';

const schema = z.object({
  nombre: z.string().min(2, 'El nombre es obligatorio'),
  apellidos: z.string().min(2, 'Los apellidos son obligatorios'),
  email: z.string().email('Email no válido'),
});

// Tipado robusto para formData
interface PerfilForm {
  nombre: string;
  apellidos: string;
  email: string;
  consentimientos: any;
}

const Perfil: React.FC = () => {
  const { perfil, loading, error, actualizar } = usePerfil();
  const { consents, update: updateConsent, loading: loadingConsents } = useCompliance();
  const [isEditing, setIsEditing] = useState(false);
  const [success, setSuccess] = useState('');
  const [formData, setFormData] = useState<PerfilForm | null>(perfil ? {
    nombre: perfil.nombre,
    apellidos: perfil.apellidos || '',
    email: perfil.email,
    consentimientos: perfil.consentimientos || {
      marketing: false,
      cookies: true,
      analytics: false,
      comunicaciones: false,
      fechaAceptacion: ''
    }
  } : null);
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});

  React.useEffect(() => {
    if (perfil) {
      setFormData({
        nombre: perfil.nombre,
        apellidos: perfil.apellidos || '',
        email: perfil.email,
        consentimientos: perfil.consentimientos || {
          marketing: false,
          cookies: true,
          analytics: false,
          comunicaciones: false,
          fechaAceptacion: ''
        }
      });
    }
  }, [perfil]);

  const handleSave = async () => {
    if (!formData) return;
    const result = schema.safeParse(formData);
    if (!result.success) {
      const errors: Record<string, string> = {};
      result.error.errors.forEach(e => {
        if (e.path[0]) errors[e.path[0]] = e.message;
      });
      setFormErrors(errors);
      return;
    }
    setFormErrors({});
    try {
      await actualizar({
        ...formData,
        consentimientos: {
          ...formData.consentimientos,
          fechaAceptacion: new Date().toISOString()
        }
      });
      setIsEditing(false);
      setSuccess('Perfil actualizado correctamente');
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      // El hook ya maneja el error
    }
  };

  // Eliminar cuenta: llamada real a solicitud de privacidad
  const handleDeleteAccount = async () => {
    if (!window.confirm('¿Estás seguro de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.')) {
      return;
    }
    try {
      // Lógica real: crear solicitud de borrado de datos personales
      const res = await import('../services/privacyService');
      await res.createPrivacyRequest('DELETE');
      alert('Solicitud de eliminación enviada. Recibirás confirmación por email.');
    } catch (e) {
      alert('Error al solicitar la eliminación de cuenta. Intenta de nuevo.');
    }
  };

  if (loading || !perfil) {
    return <div style={{ maxWidth: 400, margin: '2rem auto' }}><Skeleton height={40} /><Skeleton height={30} /><Skeleton height={30} /><Skeleton height={30} /></div>;
  }
  if (error) {
    return <div className="error-message" role="alert">{error.message}</div>;
  }

  return (
    <div className="perfil-page" aria-label="Perfil de usuario">
      <h1 tabIndex={0}>Mi Perfil</h1>
      <a href="/privacidad" target="_blank" rel="noopener noreferrer" className="enlace-privacidad" style={{float:'right',fontSize:'0.9em'}}>Política de privacidad y derechos</a>
      {success && <div className="success-message" role="status">{success}</div>}
      {!isEditing ? (
        <div className="perfil-info">
          <div><strong>Nombre:</strong> {perfil.nombre}</div>
          <div><strong>Apellidos:</strong> {perfil.apellidos}</div>
          <div><strong>Email:</strong> {perfil.email}</div>
          <div><strong>Consentimientos:</strong>
            {loadingConsents ? <span>Cargando consentimientos...</span> : (
              <ul style={{margin:0,padding:0,listStyle:'none'}}>
                {consents && consents.length > 0 ? consents.map(c => (
                  <li key={c.id}>
                    <label>
                      <input
                        type="checkbox"
                        checked={!!c.value}
                        onChange={e => updateConsent(c.id, e.target.checked)}
                        aria-checked={!!c.value}
                        aria-label={`Consentimiento ${c.id}`}
                      />
                      {c.id}
                    </label>
                  </li>
                )) : <li>No hay consentimientos configurados</li>}
              </ul>
            )}
          </div>
          <Button onClick={() => setIsEditing(true)} aria-label="Editar perfil">Editar</Button>
        </div>
      ) : (
        isEditing && formData && (
          <form className="perfil-edit" onSubmit={e => { e.preventDefault(); handleSave(); }}>
            <div className="form-field">
              <label htmlFor="nombre">Nombre</label>
              <input id="nombre" value={formData.nombre} onChange={e => setFormData({ ...formData, nombre: e.target.value })} aria-required="true" />
              {formErrors.nombre && <div className="field-error" aria-live="polite">{formErrors.nombre}</div>}
            </div>
            <div className="form-field">
              <label htmlFor="apellidos">Apellidos</label>
              <input id="apellidos" value={formData.apellidos} onChange={e => setFormData({ ...formData, apellidos: e.target.value })} aria-required="true" />
              {formErrors.apellidos && <div className="field-error" aria-live="polite">{formErrors.apellidos}</div>}
            </div>
            <div className="form-field">
              <label htmlFor="email">Email</label>
              <input id="email" value={formData.email} onChange={e => setFormData({ ...formData, email: e.target.value })} aria-required="true" />
              {formErrors.email && <div className="field-error" aria-live="polite">{formErrors.email}</div>}
            </div>
            <div className="form-field">
              <label>Consentimientos</label>
              {loadingConsents ? <span>Cargando consentimientos...</span> : (
                <ul style={{margin:0,padding:0,listStyle:'none'}}>
                  {consents && consents.length > 0 ? consents.map(c => (
                    <li key={c.id}>
                      <label>
                        <input
                          type="checkbox"
                          checked={!!c.value}
                          onChange={e => updateConsent(c.id, e.target.checked)}
                          aria-checked={!!c.value}
                          aria-label={`Consentimiento ${c.id}`}
                        />
                        {c.id}
                      </label>
                    </li>
                  )) : <li>No hay consentimientos configurados</li>}
                </ul>
              )}
            </div>
            <Button type="submit">Guardar</Button>
            <Button variant="secondary" onClick={() => setIsEditing(false)}>Cancelar</Button>
          </form>
        )
      )}
      <Button variant="danger" onClick={handleDeleteAccount} aria-label="Eliminar cuenta">Eliminar cuenta</Button>
    </div>
  );
};

export default Perfil;
