import React, { useState, useEffect } from 'react';
import { useAuth } from '../hooks/useAuth.ts';
import Button from '../components/Button.tsx';

interface Validador {
  id: string;
  nombre: string;
  email: string;
  relacion: 'FAMILIAR' | 'AMIGO' | 'MENTOR' | 'COLEGA' | 'OTRO';
  estado: 'ACTIVO' | 'PENDIENTE' | 'INACTIVO';
  fechaInvitacion: string;
  validacionesRealizadas: number;
  confianza: number; // 1-5
}

const Validadores: React.FC = () => {
  useAuth();
  const [validadores, setValidadores] = useState<Validador[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showInviteModal, setShowInviteModal] = useState(false);

  useEffect(() => {
    obtenerValidadores();
  }, []);

  const obtenerValidadores = async () => {
    try {
      setLoading(true);
      const response = await fetch('/api/validadores', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });

      if (response.ok) {
        const data = await response.json();
        setValidadores(data);
      } else {
        throw new Error('Error obteniendo validadores');
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error obteniendo validadores');
    } finally {
      setLoading(false);
    }
  };

  const invitarValidador = async (email: string, relacion: string) => {
    try {
      const response = await fetch('/api/validadores/invitar', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify({ email, relacion })
      });

      if (response.ok) {
        const nuevoValidador = await response.json();
        setValidadores(prev => [...prev, nuevoValidador]);
        setShowInviteModal(false);
      } else {
        throw new Error('Error enviando invitación');
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error enviando invitación');
    }
  };

  const eliminarValidador = async (validadorId: string) => {
    if (!window.confirm('¿Estás seguro de que quieres eliminar este validador?')) {
      return;
    }

    try {
      const response = await fetch(`/api/validadores/${validadorId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });

      if (response.ok) {
        setValidadores(prev => prev.filter(v => v.id !== validadorId));
      } else {
        throw new Error('Error eliminando validador');
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error eliminando validador');
    }
  };

  const getRelacionEmoji = (relacion: string) => {
    const emojis = {
      'FAMILIAR': '👨‍👩‍👧‍👦',
      'AMIGO': '👫',
      'MENTOR': '🎓',
      'COLEGA': '💼',
      'OTRO': '👤'
    };
    return emojis[relacion as keyof typeof emojis] || '👤';
  };

  const getEstadoColor = (estado: string) => {
    const colores = {
      'ACTIVO': 'green',
      'PENDIENTE': 'yellow',
      'INACTIVO': 'gray'
    };
    return colores[estado as keyof typeof colores] || 'gray';
  };

  const renderConfianzaStars = (confianza: number) => {
    return '⭐'.repeat(confianza) + '☆'.repeat(5 - confianza);
  };

  if (loading) {
    return (
      <div className="validadores-loading">
        <div className="spinner">👥 Cargando validadores...</div>
      </div>
    );
  }

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
            ⚠️ {error}
          </div>
        )}

        {/* Lista de validadores */}
        {validadores.length === 0 ? (
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
            {validadores.map((validador) => (
              <div key={validador.id} className={`validador-card validador-${getEstadoColor(validador.estado)}`}>
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
                    </span>
                  </div>
                </div>

                <div className="validador-details">
                  <div className="detail-item">
                    <span className="detail-label">Relación:</span>
                    <span className="detail-value">
                      {getRelacionEmoji(validador.relacion)} {validador.relacion}
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
                      {validador.validacionesRealizadas} realizadas
                    </span>
                  </div>

                  <div className="detail-item">
                    <span className="detail-label">Invitado:</span>
                    <span className="detail-value">
                      {new Date(validador.fechaInvitacion).toLocaleDateString()}
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
                    onClick={() => eliminarValidador(validador.id)}
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
        {validadores.length > 0 && (
          <div className="validadores-stats">
            <h3>📊 Estadísticas</h3>
            <div className="stats-grid">
              <div className="stat-item">
                <div className="stat-number">{validadores.length}</div>
                <div className="stat-label">Total Validadores</div>
              </div>
              <div className="stat-item">
                <div className="stat-number">
                  {validadores.filter(v => v.estado === 'ACTIVO').length}
                </div>
                <div className="stat-label">Activos</div>
              </div>
              <div className="stat-item">
                <div className="stat-number">
                  {validadores.reduce((total, v) => total + v.validacionesRealizadas, 0)}
                </div>
                <div className="stat-label">Validaciones Totales</div>
              </div>
              <div className="stat-item">
                <div className="stat-number">
                  {validadores.length > 0 
                    ? Math.round(validadores.reduce((total, v) => total + v.confianza, 0) / validadores.length * 10) / 10
                    : 0
                  }
                </div>
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
          />
        )}
      </div>
    </div>
  );
};

// Componente Modal para invitar validador
const InviteValidadorModal: React.FC<{
  onInvite: (email: string, relacion: string) => void;
  onClose: () => void;
}> = ({ onInvite, onClose }) => {
  const [email, setEmail] = useState('');
  const [relacion, setRelacion] = useState('AMIGO');
  const [mensaje, setMensaje] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (email && relacion) {
      onInvite(email, relacion);
    }
  };

  return (
    <dialog
      className="modal-overlay"
      open
    >
      <button
        type="button"
        className="modal-backdrop"
        aria-label="Cerrar modal"
        onClick={onClose}
        onKeyDown={e => {
          if (e.key === 'Enter' || e.key === ' ' || e.key === 'Escape') {
            onClose();
          }
        }}
        style={{
          position: 'fixed',
          inset: 0,
          background: 'rgba(0,0,0,0.3)',
          zIndex: 1
        }}
        tabIndex={0}
      />
      <form
        className="modal-content"
        onSubmit={handleSubmit}
        style={{ position: 'relative', zIndex: 2 }}
      >
        <div className="modal-header">
          <h2>➕ Invitar Validador</h2>
          <button className="modal-close" onClick={onClose}>✕</button>
        </div>

  {/* ...existing code... */}
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
          </div>

          <div className="form-field">
            <label htmlFor="relacion">Relación *</label>
            <select
              id="relacion"
              value={relacion}
              onChange={(e) => setRelacion(e.target.value)}
              required
            >
              <option value="FAMILIAR">👨‍👩‍👧‍👦 Familiar</option>
              <option value="AMIGO">👫 Amigo/a</option>
              <option value="MENTOR">🎓 Mentor</option>
              <option value="COLEGA">💼 Colega</option>
              <option value="OTRO">👤 Otro</option>
            </select>
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
