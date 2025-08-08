import React, { useState } from 'react';
import { useAuth } from '../hooks/useAuth.ts';
import { useReto, Reto } from '../hooks/useReto.ts';
import Button from '../components/Button.tsx';
import Modal from '../components/Modal.tsx';
import { logger } from '../utils/logger.ts';
import ViralShare from '../components/ViralShare';

const Dashboard: React.FC = () => {
  const { user } = useAuth();
  const { retos, loading, error, crearReto, reportarAvance } = useReto();
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showReportModal, setShowReportModal] = useState(false);
  const [selectedReto, setSelectedReto] = useState<Reto | null>(null);

  // Estadísticas calculadas
  const retosActivos = retos.filter(r => r.estado === 'ACTIVO').length;
  const retosCompletados = retos.filter(r => r.estado === 'COMPLETADO').length;
  const puntosTotales = retos.reduce((total, reto) => total + reto.recompensas.puntos, 0);
  const progresoPromedio = retos.length > 0 
    ? Math.round(retos.reduce((total, reto) => total + reto.progreso, 0) / retos.length)
    : 0;

  const handleCreateReto = async (formData: any) => {
    try {
      await crearReto({
        titulo: formData.titulo,
        descripcion: formData.descripcion,
        fechaInicio: new Date().toISOString(),
        fechaFin: formData.fechaFin,
        categoria: formData.categoria,
        dificultad: formData.dificultad,
        validadores: formData.validadores,
        estado: 'ACTIVO',
        progreso: 0,
        reportes: [],
        recompensas: {
          puntos: getDificultadPuntos(formData.dificultad),
          badges: []
        }
      });
      setShowCreateModal(false);
    } catch (error) {
      logger.error(
        'Error al crear nuevo reto',
        'Dashboard',
        {
          titulo: formData.titulo,
          dificultad: formData.dificultad,
          error: error instanceof Error ? error.message : String(error)
        }
      );
    }
  };

  const handleReportProgress = async (formData: any) => {
    if (!selectedReto) return;
    
    try {
      await reportarAvance(selectedReto.id, {
        descripcion: formData.descripcion,
        evidencia: {
          tipo: formData.evidenciaTipo,
          contenido: formData.evidenciaContenido
        },
        fecha: new Date().toISOString(),
        validaciones: selectedReto.validadores.map(validadorId => ({
          validadorId,
          estado: 'PENDIENTE'
        }))
      });
      setShowReportModal(false);
      setSelectedReto(null);
    } catch (error) {
      logger.error(
        'Error al reportar avance del reto',
        'Dashboard',
        {
          retoId: selectedReto?.id,
          retoTitulo: selectedReto?.titulo,
          evidenciaTipo: formData.evidenciaTipo,
          error: error instanceof Error ? error.message : String(error)
        }
      );
    }
  };

  const getDificultadPuntos = (dificultad: string) => {
    const puntos = { 'FACIL': 10, 'MEDIO': 25, 'DIFICIL': 50, 'EXTREMO': 100 };
    return puntos[dificultad as keyof typeof puntos] || 10;
  };

  const getDificultadColor = (dificultad: string) => {
    const colores = { 
      'FACIL': 'green', 
      'MEDIO': 'yellow', 
      'DIFICIL': 'orange', 
      'EXTREMO': 'red' 
    };
    return colores[dificultad as keyof typeof colores] || 'gray';
  };

  if (loading && retos.length === 0) {
    return (
      <div className="page-container">
        <div className="page-content">
          <div className="loading-container">
            <output className="spinner" aria-label="Cargando dashboard...">
              🚀 Cargando tu dashboard...
            </output>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">
      <div className="page-content">
        <header className="internal-header">
          <div className="internal-header-content">
            <div className="internal-logo">{/* div en lugar de a href="#" */}
              <span className="internal-logo-icon">⚡</span>
              <span>IMPULSE</span>
            </div>
            <div className="page-actions">
              <Button onClick={() => setShowCreateModal(true)}>
                🎯 Crear Nuevo Reto
              </Button>
            </div>
          </div>
        </header>

        <main className="page-main">
          {/* Welcome section */}
          <div className="content-card">
            <h1>¡Hola, {user?.nombre}! 👋</h1>
            <p style={{ color: 'var(--text-secondary)', marginBottom: 0 }}>
              Aquí tienes el resumen de tus retos y progreso.
            </p>
          </div>

      {/* Estadísticas */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon">🎯</div>
          <div className="stat-content">
            <div className="stat-number">{retosActivos}</div>
            <div className="stat-label">Retos Activos</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">✅</div>
          <div className="stat-content">
            <div className="stat-number">{retosCompletados}</div>
            <div className="stat-label">Completados</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">🏆</div>
          <div className="stat-content">
            <div className="stat-number">{puntosTotales}</div>
            <div className="stat-label">Puntos Totales</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">📊</div>
          <div className="stat-content">
            <div className="stat-number">{progresoPromedio}%</div>
            <div className="stat-label">Progreso Promedio</div>
          </div>
        </div>
      </div>

      <div style={{marginTop:24}}>
  {user?.id && <ViralShare referrerId={Number(user.id)} />}
      </div>

      {/* Lista de Retos */}
      <div className="retos-section">
        <h2 className="section-title">Mis Retos</h2>
        
        {error && (
          <div className="error-message" role="alert">
            ⚠️ {error}
          </div>
        )}

        {retos.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon">🎯</div>
            <h3>¡Aún no tienes retos!</h3>
            <p>Crea tu primer reto y comienza tu viaje hacia el crecimiento personal.</p>
            <Button onClick={() => setShowCreateModal(true)}>
              ✨ Crear Mi Primer Reto
            </Button>
          </div>
        ) : (
          <div className="retos-grid">
            {retos.map((reto) => (
              <div key={reto.id} className={`reto-card reto-${reto.estado.toLowerCase()}`}>
                <div className="reto-header">
                  <h3 className="reto-title">{reto.titulo}</h3>
                  <span className={`reto-dificultad reto-dificultad-${getDificultadColor(reto.dificultad)}`}>
                    {reto.dificultad}
                  </span>
                </div>

                <p className="reto-description">{reto.descripcion}</p>

                <div className="reto-progress">
                  <progress
                    className="progress-bar"
                    value={reto.progreso}
                    max={100}
                    aria-label={`Progreso: ${reto.progreso}%`}
                  >
                    {reto.progreso}%
                  </progress>
                  <span className="progress-text">{reto.progreso}%</span>
                </div>

                <div className="reto-meta">
                  <span className="reto-categoria">📁 {reto.categoria}</span>
                  <span className="reto-fecha">📅 {new Date(reto.fechaFin).toLocaleDateString()}</span>
                </div>

                <div className="reto-actions">
                  {reto.estado === 'ACTIVO' && (
                    <Button 
                      onClick={() => {
                        setSelectedReto(reto);
                        setShowReportModal(true);
                      }}
                    >
                      📝 Reportar Avance
                    </Button>
                  )}
                  <span className="reto-status">
                    {reto.estado === 'ACTIVO' && '🟢 Activo'}
                    {reto.estado === 'COMPLETADO' && '✅ Completado'}
                    {reto.estado === 'PAUSADO' && '⏸️ Pausado'}
                    {reto.estado === 'FALLIDO' && '❌ Fallido'}
                  </span>
                </div>

                {/* Validadores */}
                <div className="reto-validadores">
                  <span className="validadores-label">👥 Validadores:</span>
                  <span className="validadores-count">{reto.validadores.length}</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Modal para crear reto */}
      {showCreateModal && (
        <Modal 
          isOpen={showCreateModal} 
          onClose={() => setShowCreateModal(false)}
          title="🎯 Crear Nuevo Reto"
        >
          <CreateRetoForm onSubmit={handleCreateReto} onCancel={() => setShowCreateModal(false)} />
        </Modal>
      )}

      {/* Modal para reportar avance */}
      {showReportModal && selectedReto && (
        <Modal 
          isOpen={showReportModal} 
          onClose={() => setShowReportModal(false)}
          title={`📝 Reportar Avance: ${selectedReto.titulo}`}
        >
          <ReportProgressForm 
            onSubmit={handleReportProgress} 
            onCancel={() => setShowReportModal(false)} 
          />
        </Modal>
      )}
        </main>
      </div>
    </div>
  );
};

// Componente para formulario de creación de reto
const CreateRetoForm: React.FC<{ onSubmit: (data: any) => void; onCancel: () => void }> = ({ onSubmit, onCancel }) => {
  const [formData, setFormData] = useState({
    titulo: '',
    descripcion: '',
    fechaFin: '',
    categoria: 'PERSONAL',
    dificultad: 'MEDIO',
    validadores: []
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
  };

  return (
    <form onSubmit={handleSubmit} className="reto-form">
      <div className="form-field">
        <label htmlFor="titulo">Título del Reto *</label>
        <input
          id="titulo"
          type="text"
          value={formData.titulo}
          onChange={(e) => setFormData({ ...formData, titulo: e.target.value })}
          placeholder="Ej: Leer 12 libros este año"
          required
          maxLength={100}
        />
      </div>

      <div className="form-field">
        <label htmlFor="descripcion">Descripción</label>
        <textarea
          id="descripcion"
          value={formData.descripcion}
          onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })}
          placeholder="Describe tu reto con más detalle..."
          rows={3}
          maxLength={500}
        />
      </div>

      <div className="form-row">
        <div className="form-field">
          <label htmlFor="categoria">Categoría</label>
          <select
            id="categoria"
            value={formData.categoria}
            onChange={(e) => setFormData({ ...formData, categoria: e.target.value })}
          >
            <option value="PERSONAL">👤 Personal</option>
            <option value="SALUD">💪 Salud</option>
            <option value="EDUCACION">📚 Educación</option>
            <option value="PROFESIONAL">💼 Profesional</option>
            <option value="SOCIAL">👥 Social</option>
          </select>
        </div>

        <div className="form-field">
          <label htmlFor="dificultad">Dificultad</label>
          <select
            id="dificultad"
            value={formData.dificultad}
            onChange={(e) => setFormData({ ...formData, dificultad: e.target.value })}
          >
            <option value="FACIL">🟢 Fácil (10 pts)</option>
            <option value="MEDIO">🟡 Medio (25 pts)</option>
            <option value="DIFICIL">🟠 Difícil (50 pts)</option>
            <option value="EXTREMO">🔴 Extremo (100 pts)</option>
          </select>
        </div>
      </div>

      <div className="form-field">
        <label htmlFor="fechaFin">Fecha límite *</label>
        <input
          id="fechaFin"
          type="date"
          value={formData.fechaFin}
          onChange={(e) => setFormData({ ...formData, fechaFin: e.target.value })}
          min={new Date().toISOString().split('T')[0]}
          required
        />
      </div>

      <div className="form-actions">
        <Button type="button" onClick={onCancel}>
          Cancelar
        </Button>
        <Button type="submit">
          🚀 Crear Reto
        </Button>
      </div>
    </form>
  );
};

// Componente para formulario de reporte de avance
const ReportProgressForm: React.FC<{ onSubmit: (data: any) => void; onCancel: () => void }> = ({ onSubmit, onCancel }) => {
  const [formData, setFormData] = useState({
    descripcion: '',
    evidenciaTipo: 'TEXTO',
    evidenciaContenido: ''
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
  };

  return (
    <form onSubmit={handleSubmit} className="report-form">
      <div className="form-field">
        <label htmlFor="descripcion">Descripción del Avance *</label>
        <textarea
          id="descripcion"
          value={formData.descripcion}
          onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })}
          placeholder="Describe qué has logrado hoy..."
          rows={4}
          required
          maxLength={1000}
        />
      </div>

      <div className="form-field">
        <label htmlFor="evidenciaTipo">Tipo de Evidencia</label>
        <select
          id="evidenciaTipo"
          value={formData.evidenciaTipo}
          onChange={(e) => setFormData({ ...formData, evidenciaTipo: e.target.value })}
        >
          <option value="TEXTO">📝 Solo texto</option>
          <option value="IMAGEN">📸 Imagen</option>
          <option value="VIDEO">🎥 Video</option>
        </select>
      </div>

      {formData.evidenciaTipo !== 'TEXTO' && (
        <div className="form-field">
          <label htmlFor="evidenciaContenido">Evidencia</label>
          <input
            id="evidenciaContenido"
            type="file"
            accept={formData.evidenciaTipo === 'IMAGEN' ? 'image/*' : 'video/*'}
            onChange={(e) => {
              const file = e.target.files?.[0];
              if (file) {
                // En un entorno real, aquí subirías el archivo
                setFormData({ ...formData, evidenciaContenido: file.name });
              }
            }}
          />
        </div>
      )}

      <div className="form-actions">
        <Button type="button" onClick={onCancel}>
          Cancelar
        </Button>
        <Button type="submit">
          📤 Enviar Reporte
        </Button>
      </div>
    </form>
  );
};

export default Dashboard;
