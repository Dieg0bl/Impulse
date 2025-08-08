import React, { useState, useEffect } from 'react';
import Header from '../components/Header.tsx';
import Footer from '../components/Footer.tsx';
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
  const { state, navigate } = useAppContext();
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
      case 'ACTIVO': return 'text-green-600 bg-green-100';
      case 'COMPLETADO': return 'text-blue-600 bg-blue-100';
      case 'PAUSADO': return 'text-yellow-600 bg-yellow-100';
      case 'CANCELADO': return 'text-red-600 bg-red-100';
      default: return 'text-gray-600 bg-gray-100';
    }
  };

  if (!user) return null;

  return (
    <div className="page">
      <Header />
      <main className="main-content">
        <div className="container mx-auto px-4 py-8">
          <div className="flex justify-between items-center mb-8">
            <h1 className="text-3xl font-bold">Mis Retos</h1>
            <Button 
              variant="primary" 
              onClick={() => navigate('/retos/nuevo')}
            >
              + Crear Reto
            </Button>
          </div>

          {/* Filtros */}
          <div className="mb-6">
            <div className="flex space-x-2">
              {['TODOS', 'ACTIVO', 'COMPLETADO', 'PAUSADO'].map(estado => (
                <button
                  key={estado}
                  onClick={() => setFiltro(estado)}
                  className={`px-4 py-2 rounded-lg transition-colors ${
                    filtro === estado
                      ? 'bg-blue-500 text-white'
                      : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                  }`}
                >
                  {estado === 'TODOS' ? 'Todos' : estado.charAt(0) + estado.slice(1).toLowerCase()}
                </button>
              ))}
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
              <h3 className="text-xl font-semibold mb-4">No tienes retos {filtro.toLowerCase()}</h3>
              <p className="text-gray-600 mb-6">¡Crea tu primer reto y comienza tu journey!</p>
              <Button 
                variant="primary" 
                onClick={() => navigate('/retos/nuevo')}
              >
                Crear Mi Primer Reto
              </Button>
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
                    <p><strong>Fin:</strong> {new Date(reto.fechaFin).toLocaleDateString()}</p>
                  </div>

                  <div className="flex space-x-2">
                    <Button 
                      variant="primary" 
                      size="sm"
                      onClick={() => navigate(`/retos/${reto.id}`)}
                    >
                      Ver Detalles
                    </Button>
                    {reto.estado === 'ACTIVO' && (
                      <Button 
                        variant="secondary" 
                        size="sm"
                        onClick={() => navigate(`/retos/${reto.id}/evidencia`)}
                      >
                        Subir Evidencia
                      </Button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* Estadísticas resumidas */}
          {!loading && retos.length > 0 && (
            <div className="mt-12 bg-gray-50 rounded-lg p-6">
              <h3 className="text-lg font-semibold mb-4">Estadísticas</h3>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-center">
                <div>
                  <div className="text-2xl font-bold text-blue-600">{retos.length}</div>
                  <div className="text-sm text-gray-600">Total Retos</div>
                </div>
                <div>
                  <div className="text-2xl font-bold text-green-600">
                    {retos.filter(r => r.estado === 'COMPLETADO').length}
                  </div>
                  <div className="text-sm text-gray-600">Completados</div>
                </div>
                <div>
                  <div className="text-2xl font-bold text-yellow-600">
                    {retos.filter(r => r.estado === 'ACTIVO').length}
                  </div>
                  <div className="text-sm text-gray-600">Activos</div>
                </div>
                <div>
                  <div className="text-2xl font-bold text-purple-600">
                    {retos.reduce((total, reto) => total + reto.puntos, 0)}
                  </div>
                  <div className="text-sm text-gray-600">Puntos Totales</div>
                </div>
              </div>
            </div>
          )}
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default MisRetos;
