import React, { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import Button from '../components/Button';
import { useValidadores, Validador as BaseValidador } from '../hooks/useValidadores';

// Extend the backend Validador shape with optional UI-only fields
type Validador = BaseValidador & {
  relacion?: string;
  fechaInvitacion?: string | number;
};

const getErrorMessage = (err: unknown): string =>
  (typeof err === 'object' && err && 'message' in err && typeof (err as any).message === 'string')
    ? (err as { message: string }).message
    : 'Ocurrió un error inesperado.';

const getRelacionEmoji = (relacion?: string) => {
  const emojis: Record<string, string> = {
    FAMILIAR: '👨‍👩‍👧‍👦',
    AMIGO: '👫',
    MENTOR: '🎓',
    COLEGA: '💼',
    OTRO: '👤',
  };
  if (!relacion) return '👤';
  return emojis[relacion] ?? '👤';
};

const getEstadoColor = (estado: string) => {
  const colores = {
    ACTIVO: 'green',
    PENDIENTE: 'yellow',
    INACTIVO: 'gray',
  } as const;
  return (colores as Record<string, string>)[estado] ?? 'gray';
};

const renderConfianzaStars = (confianza?: number) => {
  const n = Math.round(Math.max(0, Math.min(5, confianza ?? 0)));
  return '⭐'.repeat(n) + '☆'.repeat(5 - n);
};

const Validadores: React.FC = () => {
  useAuth();
  const { validadores, loading, error, invitar, eliminar } = useValidadores();
  const [showInviteModal, setShowInviteModal] = useState(false);
  const [inviteError, setInviteError] = useState<string | null>(null);

  const invitarValidador = async (email: string) => {
    try {
      await invitar(email);
      setShowInviteModal(false);
      setInviteError(null);
    } catch (err: unknown) {
      setInviteError(getErrorMessage(err));
    }
  };

  const eliminarValidador = async (validadorId: number) => {
    if (!window.confirm('¿Estás seguro de que quieres eliminar este validador?')) return;
    try {
      await eliminar(validadorId);
    } catch (err: unknown) {
      // Manejo de error mínimo: log para depuración en desarrollo
      // No romper flujo en UI — se puede mejorar con un toast centralizado
      // eslint-disable-next-line no-console
      console.error('Error eliminando validador', err);
    }
  };

  if (loading) {
    return (
      <div className="validadores-loading">
        <div className="spinner">👥 Cargando validadores...</div>
      </div>
    );
  }

  const totalValidadores = validadores.length;
  const totalActivos = validadores.filter(v => v.estado === 'ACTIVO').length;
  const totalValidaciones = validadores.reduce((t, v) => t + (v.validacionesRealizadas ?? 0), 0);
  const sumaConfianza = validadores.reduce((t, v) => t + (v.confianza ?? 0), 0);
  const confianzaPromedio = totalValidadores > 0 ? Math.round((sumaConfianza / totalValidadores) * 10) / 10 : 0;

  return (
    <div className="validadores-page">
      <div className="validadores-container">
        {/* Header */}
        <div className="validadores-header">
          <div className="header-content">
            <h1 className="validadores-title">👥 Mis Validadores</h1>
            <p className="validadores-subtitle">
              Las personas que validan tu progreso y te ayudan a mantener tus compromisos
            </p>
          </div>
          <Button onClick={() => setShowInviteModal(true)}>
            ➕ Invitar Validador
          </Button>
        </div>

        {/* Explicación */}
        <div className="info-section">
          <div className="info-card">
            <h3>🤔 ¿Qué es un validador?</h3>
            <p>
              Los validadores son personas de confianza que verifican tu progreso en los retos.
              Pueden ser familiares, amigos, mentores o colegas que te conocen bien y pueden
              confirmar si realmente has cumplido con tus objetivos.
            </p>
          </div>
          <div className="info-card">
            <h3>🔍 ¿Cómo funciona?</h3>
            <p>
              Cuando reportas avance en un reto, tus validadores reciben una notificación
              para revisar tu reporte. Ellos pueden aprobar, rechazar o solicitar más información.
            </p>
          </div>
        </div>

        {/* Mensajes de error */}
        {error && (
          <div className="error-message" role="alert">
            ⚠️ {String(error)}
          </div>
        )}

        {/* Lista de validadores */}
        {totalValidadores === 0 ? (
          <div className="empty-state">
            <div className="empty-icon">👥</div>
            <h3>¡Aún no tienes validadores!</h3>
            <p>
              Invita a personas de confianza para que validen tu progreso.
              Esto hace que tus compromisos sean más reales y motivadores.
            </p>
            <Button onClick={() => setShowInviteModal(true)}>
              ✨ Invitar Mi Primer Validador
            </Button>
          </div>
        ) : (
          <div className="validadores-grid">
            {validadores.map((validador: Validador) => (
              <div
                key={String(validador.id)}
                className={`validador-card validador-${getEstadoColor(validador.estado)}`}
              >
                <div className="validador-header">
                  <div className="validador-avatar">
                    {getRelacionEmoji(validador.relacion)}
                  </div>
                  <div className="validador-info">
                    <h3 className="validador-nombre">{validador.nombre}</h3>
                    <p className="validador-email">{validador.email}</p>
                  </div>
                  <div className="validador-status">
                    <span className={`status-badge status-${getEstadoColor(validador.estado)}`}>
                      {validador.estado === 'ACTIVO' && '🟢 Activo'}
                      {validador.estado === 'PENDIENTE' && '🟡 Pendiente'}
                      {validador.estado === 'INACTIVO' && '⚫ Inactivo'}
                      {!['ACTIVO', 'PENDIENTE', 'INACTIVO'].includes(validador.estado) && '⚪ Desconocido'}
                    </span>
                  </div>
                </div>

                <div className="validador-details">
                  <div className="detail-item">
                    <span className="detail-label">Relación:</span>
                    <span className="detail-value">
                      {getRelacionEmoji(validador.relacion)} {validador.relacion ?? '—'}
                    </span>
                  </div>
                  <div className="detail-item">
                    <span className="detail-label">Confianza:</span>
                    <span className="detail-value">
                      {renderConfianzaStars(validador.confianza)}
                    </span>
                  </div>
                  <div className="detail-item">
                    <span className="detail-label">Validaciones:</span>
                    <span className="detail-value">
                      {(validador.validacionesRealizadas ?? 0).toString()} realizadas
                    </span>
                  </div>
                  <div className="detail-item">
                    <span className="detail-label">Invitado:</span>
                    <span className="detail-value">
                      {validador.fechaInvitacion
                        ? new Date(validador.fechaInvitacion).toLocaleDateString()
                        : '—'}
                    </span>
                  </div>
                </div>

                <div className="validador-actions">
                  {validador.estado === 'PENDIENTE' && (
                    <Button onClick={() => alert('Reenviar invitación (funcionalidad en desarrollo)')}>
                      📧 Reenviar Invitación
                    </Button>
                  )}
                  <Button
                    onClick={() => eliminarValidador(Number(validador.id))}
                    className="danger-button"
                    aria-label="Eliminar validador"
                  >
                    🗑️ Eliminar
                  </Button>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Estadísticas */}
        {totalValidadores > 0 && (
          <div className="validadores-stats">
            <h3>📊 Estadísticas</h3>
            <div className="stats-grid">
              <div className="stat-item">
                <div className="stat-number">{totalValidadores}</div>
                <div className="stat-label">Total Validadores</div>
              </div>
              <div className="stat-item">
                <div className="stat-number">{totalActivos}</div>
                <div className="stat-label">Activos</div>
              </div>
              <div className="stat-item">
                <div className="stat-number">{totalValidaciones}</div>
                <div className="stat-label">Validaciones Totales</div>
              </div>
              <div className="stat-item">
                <div className="stat-number">{confianzaPromedio}</div>
                <div className="stat-label">Confianza Promedio</div>
              </div>
            </div>
          </div>
        )}

        {/* Modal de invitación */}
        {showInviteModal && (
          <InviteValidadorModal
            onInvite={invitarValidador}
            onClose={() => setShowInviteModal(false)}
            error={inviteError}
          />
        )}
      </div>
    </div>
  );
};

const InviteValidadorModal: React.FC<{
  onInvite: (email: string) => void | Promise<void>;
  onClose: () => void;
  error?: string | null;
}> = ({ onInvite, onClose, error }) => {
  const [email, setEmail] = useState('');
  const [mensaje, setMensaje] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (email) {
      void Promise.resolve(onInvite(email));
    }
  };

  return (
    <dialog className="modal-overlay" open>
      <button
        type="button"
        className="modal-backdrop"
        aria-label="Cerrar modal"
        onClick={onClose}
        style={{ position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.3)', zIndex: 1 }}
        tabIndex={0}
      />
      <form
        className="modal-content"
        onSubmit={handleSubmit}
        style={{ position: 'relative', zIndex: 2 }}
      >
        <div className="form-field">
          <label htmlFor="email">Email del Validador *</label>
          <input
            id="email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="validador@ejemplo.com"
            required
          />
          {error && (
            <div className="error-message" role="alert" style={{ marginTop: 8, color: 'red' }}>
              ⚠️ {error}
            </div>
          )}
        </div>

        <div className="form-field">
          <label htmlFor="mensaje">Mensaje Personal (Opcional)</label>
          <textarea
            id="mensaje"
            value={mensaje}
            onChange={(e) => setMensaje(e.target.value)}
            placeholder="Añade un mensaje personal para tu invitación..."
            rows={3}
            maxLength={500}
          />
        </div>

        <div className="form-info">
          <div className="info-item">
            <h4>🔒 ¿Qué verá el validador?</h4>
            <ul>
              <li>Solo los retos en los que lo incluyas</li>
              <li>Solo la información que compartas en reportes</li>
              <li>Puede aprobar, rechazar o comentar tus avances</li>
            </ul>
          </div>
        </div>

        <div className="form-actions">
          <Button type="button" onClick={onClose}>
            Cancelar
          </Button>
          <Button type="submit">
            📧 Enviar Invitación
          </Button>
        </div>
      </form>
    </dialog>
  );
};

export default Validadores;
