import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Button from '../components/Button.tsx';
import { useAppContext } from '../contexts/AppContext.tsx';

interface Reto {
  id: string;
  titulo: string;
  descripcion: string;
  categoria: string;
  dificultad: string;
  estado: 'ACTIVO' | 'COMPLETADO' | 'PAUSADO' | 'CANCELADO';
  progreso: number;
  fechaInicio: string;
  fechaFin: string;
  puntos: number;
}

const MisRetos: React.FC = () => {
  const navigate = useNavigate();
  const { state } = useAppContext();
  const { currentUser: user } = state;
  const [retos, setRetos] = useState<Reto[]>([]);
  const [filtro, setFiltro] = useState<string>('TODOS');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!user) {
      navigate('/login');
      return;
    }

    // Simular carga de retos del usuario
    setTimeout(() => {
      const retosSimulados: Reto[] = [
        {
          id: '1',
          titulo: '30 días de ejercicio',
          descripcion: 'Ejercitar 30 minutos diarios durante un mes',
          categoria: 'Fitness',
          dificultad: 'Intermedio',
          estado: 'ACTIVO',
          progreso: 65,
          fechaInicio: '2025-07-15',
          fechaFin: '2025-08-15',
          puntos: 300
        },
        {
          id: '2',
          titulo: 'Aprender TypeScript',
          descripcion: 'Completar curso de TypeScript',
          categoria: 'Tecnología',
          dificultad: 'Difícil',
          estado: 'COMPLETADO',
          progreso: 100,
          fechaInicio: '2025-06-01',
          fechaFin: '2025-07-01',
          puntos: 500
        },
        {
          id: '3',
          titulo: 'Leer 10 libros',
          descripcion: 'Leer 10 libros en 3 meses',
          categoria: 'Educación',
          dificultad: 'Intermedio',
          estado: 'PAUSADO',
          progreso: 40,
          fechaInicio: '2025-05-01',
          fechaFin: '2025-08-01',
          puntos: 400
        }
      ];
      setRetos(retosSimulados);
      setLoading(false);
    }, 1000);
  }, [user, navigate]);

  const retosFiltrados = retos.filter(reto =>
    filtro === 'TODOS' || reto.estado === filtro
  );

    const getEstadoColor = (estado: string) => {
    switch (estado) {
      case 'ACTIVO': return 'bg-green-100 text-green-800';
      case 'COMPLETADO': return 'bg-blue-100 text-blue-800';
      case 'PAUSADO': return 'bg-yellow-100 text-yellow-800';
      case 'CANCELADO': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  };

  if (!user) return null;

  return (
    <div className="page-container">
      <div className="page-content">
        <header className="internal-header">
          <div className="internal-header-content">
            <div className="internal-logo">
              <span className="internal-logo-icon">⚡</span>
              <span>IMPULSE</span>
            </div>
            <div className="page-actions">
              <Button
                variant="primary"
                onClick={() => navigate('/retos/nuevo')}
              >
                + Crear Reto
              </Button>
            </div>
          </div>
        </header>

        <main className="page-main">
          <div className="content-card">
            <h1>🎯 Mis Retos</h1>
            <p style={{ color: 'var(--text-secondary)', marginBottom: 0 }}>
              Gestiona y da seguimiento a todos tus retos personales
            </p>
          </div>

          {/* Stats cards */}
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-value">{retosFiltrados.filter(r => r.estado === 'ACTIVO').length}</div>
              <div className="stat-label">Activos</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">{retosFiltrados.filter(r => r.estado === 'COMPLETADO').length}</div>
              <div className="stat-label">Completados</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">{retosFiltrados.reduce((total, reto) => total + reto.puntos, 0)}</div>
              <div className="stat-label">Puntos Totales</div>
            </div>
          </div>

          {/* Filtros */}
          <div className="content-card">
            <h3>Filtros</h3>
            <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
              {['TODOS', 'ACTIVO', 'COMPLETADO', 'PAUSADO'].map(estado => {
                const isActive = filtro === estado;
                const label = estado === 'TODOS' ? 'Todos' : estado.charAt(0) + estado.slice(1).toLowerCase();
                return (
                  <button
                    key={estado}
                    onClick={() => setFiltro(estado)}
                    className={`status-badge ${isActive ? 'active' : ''}`}
                    style={{
                      background: isActive ? 'var(--gradient-primary)' : 'rgba(102, 126, 234, 0.1)',
                      color: isActive ? 'white' : 'var(--color-primary)',
                      border: 'none',
                      cursor: 'pointer',
                      transition: 'all 0.3s ease'
                    }}
                  >
                    {label}
                  </button>
                );
              })}
            </div>
          </div>

          {/* Lista de retos */}
          {loading ? (
            <div className="text-center py-8">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mx-auto"></div>
              <p className="mt-4 text-gray-600">Cargando tus retos...</p>
            </div>
          ) : retosFiltrados.length === 0 ? (
            <div className="text-center py-12">
              <h3 className="text-xl font-semibold mb-4">
                {filtro === 'TODOS' ? '¡Crea tu primer reto!' : `No tienes retos ${filtro.toLowerCase()}`}
              </h3>
              <p className="text-gray-600 mb-6">
                {filtro === 'TODOS' 
                  ? 'Comienza tu viaje de crecimiento personal creando tu primer reto'
                  : 'Cambia el filtro para ver tus otros retos'
                }
              </p>
              {filtro === 'TODOS' && (
                <Button onClick={() => navigate('/crear-reto')}>
                  ✨ Crear mi primer reto
                </Button>
              )}
            </div>
          ) : (
            <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
              {retosFiltrados.map(reto => (
                <div key={reto.id} className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow">
                  <div className="flex justify-between items-start mb-4">
                    <h3 className="font-bold text-lg">{reto.titulo}</h3>
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${getEstadoColor(reto.estado)}`}>
                      {reto.estado}
                    </span>
                  </div>
                  <p className="text-gray-600 mb-4 line-clamp-2">{reto.descripcion}</p>
                  <div className="mb-4">
                    <div className="flex justify-between text-sm mb-1">
                      <span>Progreso</span>
                      <span>{reto.progreso}%</span>
                    </div>
                    <div className="w-full bg-gray-200 rounded-full h-2">
                      <div
                        className="bg-blue-500 h-2 rounded-full transition-all duration-300"
                        style={{ width: `${reto.progreso}%` }}
                      ></div>
                    </div>
                  </div>
                  <div className="text-sm text-gray-500 mb-4">
                    <p><strong>Categoría:</strong> {reto.categoria}</p>
                    <p><strong>Dificultad:</strong> {reto.dificultad}</p>
                    <p><strong>Puntos:</strong> {reto.puntos}</p>
                    <p><strong>Fin:</strong> {formatDate(reto.fechaFin)}</p>
                  </div>
                  <div className="flex space-x-2">
                    <Button
                      variant="primary"
                      size="small"
                      onClick={() => navigate(`/reto/${reto.id}`)}
                    >
                      Ver Detalles
                    </Button>
                    {reto.estado === 'ACTIVO' && (
                      <Button
                        variant="secondary"
                        size="small"
                        onClick={() => navigate(`/reportar-avance?reto=${reto.id}`)}
                      >
                        Subir Evidencia
                      </Button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

export default MisRetos;