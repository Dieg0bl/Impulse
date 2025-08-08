import React, { useState, useEffect } from 'react';
import { useAppContext } from '../contexts/AppContext.tsx';
import Button from '../components/Button.tsx';

interface RetoDetalle {
  id: string;
  titulo: string;
  descripcion: string;
  categoria: string;
  dificultad: 'FACIL' | 'MEDIO' | 'DIFICIL';
  fechaInicio: string;
  fechaFin: string;
  estado: 'BORRADOR' | 'ACTIVO' | 'PAUSADO' | 'COMPLETADO' | 'FALLIDO' | 'CANCELADO';
  progreso: number;
  puntos: number;
  validadores: string[];
  usuarioCreador: {
    id: string;
    nombre: string;
    avatar?: string;
  };
  reportes: Array<{
    id: string;
    fecha: string;
    descripcion: string;
    evidencia?: string;
    estado: 'PENDIENTE' | 'APROBADO' | 'RECHAZADO';
  }>;
}

const RetoDetalle: React.FC = () => {
  const { navigate } = useAppContext();
  const [reto, setReto] = useState<RetoDetalle | null>(null);
  const [loading, setLoading] = useState(true);
  const [mostrarReportes, setMostrarReportes] = useState(false);

  // Simular parámetro de reto ID (en una app real vendría de la URL)
  const retoId = '1';

  useEffect(() => {
    const cargarReto = async () => {
      try {
        // Simular carga de datos del reto
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        const mockReto: RetoDetalle = {
          id: retoId,
          titulo: 'Ejercicio Diario de 30 Minutos',
          descripcion: 'Realizar al menos 30 minutos de ejercicio cardiovascular todos los días durante un mes. Puede incluir correr, caminar rápido, bicicleta o natación.',
          categoria: 'SALUD',
          dificultad: 'MEDIO',
          fechaInicio: '2025-07-01',
          fechaFin: '2025-07-31',
          estado: 'ACTIVO',
          progreso: 65,
          puntos: 500,
          validadores: ['validador1', 'validador2'],
          usuarioCreador: {
            id: 'user1',
            nombre: 'María González',
            avatar: undefined
          },
          reportes: [
            {
              id: '1',
              fecha: '2025-07-15',
              descripcion: 'Completé 35 minutos de caminata rápida en el parque',
              evidencia: 'foto_ejercicio.jpg',
              estado: 'APROBADO'
            },
            {
              id: '2',
              fecha: '2025-07-20',
              descripcion: '40 minutos de bicicleta estática',
              estado: 'PENDIENTE'
            }
          ]
        };
        
        setReto(mockReto);
      } catch (error) {
        console.error('Error al cargar reto:', error);
      } finally {
        setLoading(false);
      }
    };

    cargarReto();
  }, [retoId]);

  const handleUnirseReto = async () => {
    try {
      console.log('Uniéndose al reto...');
      // Lógica para unirse al reto
    } catch (error) {
      console.error('Error al unirse al reto:', error);
    }
  };

  const handleReportarAvance = () => {
    navigate('reportar-avance');
  };

  const getEstadoReporteColor = (estado: 'PENDIENTE' | 'APROBADO' | 'RECHAZADO') => {
    const colores = {
      APROBADO: 'bg-green-100 text-green-800',
      RECHAZADO: 'bg-red-100 text-red-800',
      PENDIENTE: 'bg-yellow-100 text-yellow-800'
    };
    return colores[estado];
  };

  const getEstadoColor = (estado: RetoDetalle['estado']) => {
    const colores = {
      BORRADOR: 'bg-gray-100 text-gray-800',
      ACTIVO: 'bg-green-100 text-green-800',
      PAUSADO: 'bg-yellow-100 text-yellow-800',
      COMPLETADO: 'bg-blue-100 text-blue-800',
      FALLIDO: 'bg-red-100 text-red-800',
      CANCELADO: 'bg-gray-100 text-gray-600'
    };
    return colores[estado];
  };

  const getDificultadColor = (dificultad: RetoDetalle['dificultad']) => {
    const colores = {
      FACIL: 'bg-green-100 text-green-800',
      MEDIO: 'bg-yellow-100 text-yellow-800',
      DIFICIL: 'bg-red-100 text-red-800'
    };
    return colores[dificultad];
  };

  const formatearFecha = (fecha: string) => {
    return new Date(fecha).toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  if (loading) {
    return (
      <div className="reto-detalle-page">
        <div className="container mx-auto px-4 py-6">
          <div className="text-center">Cargando reto...</div>
        </div>
      </div>
    );
  }

  if (!reto) {
    return (
      <div className="reto-detalle-page">
        <div className="container mx-auto px-4 py-6">
          <div className="text-center">
            <h2 className="text-xl font-bold mb-4">Reto no encontrado</h2>
            <Button onClick={() => navigate('dashboard')}>
              Volver al inicio
            </Button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="reto-detalle-page">
      <div className="container mx-auto px-4 py-6">
        {/* Header */}
        <div className="flex items-center gap-4 mb-6">
          <Button
            variant="secondary"
            size="small"
            onClick={() => navigate('mis-retos')}
          >
            ← Volver
          </Button>
          <h1 className="text-2xl font-bold">{reto.titulo}</h1>
        </div>

        {/* Información principal */}
        <div className="bg-white rounded-lg border p-6 mb-6">
          <div className="flex flex-wrap gap-2 mb-4">
            <span className={`px-3 py-1 rounded-full text-sm font-medium ${getEstadoColor(reto.estado)}`}>
              {reto.estado}
            </span>
            <span className={`px-3 py-1 rounded-full text-sm font-medium ${getDificultadColor(reto.dificultad)}`}>
              {reto.dificultad}
            </span>
            <span className="px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800">
              {reto.categoria}
            </span>
          </div>

          <p className="text-gray-700 mb-4">{reto.descripcion}</p>

          <div className="grid md:grid-cols-3 gap-4 mb-4">
            <div>
              <span className="text-sm text-gray-500">Fecha de inicio:</span>
              <p className="font-medium">{formatearFecha(reto.fechaInicio)}</p>
            </div>
            <div>
              <span className="text-sm text-gray-500">Fecha límite:</span>
              <p className="font-medium">{formatearFecha(reto.fechaFin)}</p>
            </div>
            <div>
              <span className="text-sm text-gray-500">Puntos:</span>
              <p className="font-medium">{reto.puntos} pts</p>
            </div>
          </div>

          {/* Barra de progreso */}
          <div className="mb-4">
            <div className="flex justify-between text-sm mb-1">
              <span>Progreso</span>
              <span>{reto.progreso}%</span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2">
              <div
                className="bg-blue-600 h-2 rounded-full transition-all"
                style={{ width: `${reto.progreso}%` }}
              ></div>
            </div>
          </div>

          {/* Creador */}
          <div className="border-t pt-4">
            <span className="text-sm text-gray-500">Creado por:</span>
            <div className="flex items-center gap-2 mt-1">
              <div className="w-8 h-8 bg-gray-300 rounded-full flex items-center justify-center">
                {reto.usuarioCreador.avatar ? (
                  <img 
                    src={reto.usuarioCreador.avatar} 
                    alt={reto.usuarioCreador.nombre}
                    className="w-full h-full rounded-full"
                  />
                ) : (
                  <span className="text-sm font-medium">
                    {reto.usuarioCreador.nombre.charAt(0)}
                  </span>
                )}
              </div>
              <span className="font-medium">{reto.usuarioCreador.nombre}</span>
            </div>
          </div>
        </div>

        {/* Reportes */}
        <div className="bg-white rounded-lg border p-6 mb-6">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-lg font-semibold">Reportes de Avance</h2>
            <Button
              size="small"
              onClick={() => setMostrarReportes(!mostrarReportes)}
            >
              {mostrarReportes ? 'Ocultar' : 'Ver'} reportes ({reto.reportes.length})
            </Button>
          </div>

          {mostrarReportes && (
            <div className="space-y-3">
              {reto.reportes.map(reporte => (
                <div key={reporte.id} className="border rounded-lg p-4">
                  <div className="flex justify-between items-start mb-2">
                    <span className="text-sm text-gray-500">
                      {formatearFecha(reporte.fecha)}
                    </span>
                    <span className={`px-2 py-1 rounded text-xs font-medium ${getEstadoReporteColor(reporte.estado)}`}>
                      {reporte.estado}
                    </span>
                  </div>
                  <p className="text-gray-700">{reporte.descripcion}</p>
                  {reporte.evidencia && (
                    <p className="text-sm text-blue-600 mt-1">📎 {reporte.evidencia}</p>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Acciones */}
        <div className="flex gap-4">
          {reto.estado === 'ACTIVO' && (
            <>
              <Button onClick={handleReportarAvance}>
                Reportar Avance
              </Button>
              <Button variant="secondary" onClick={handleUnirseReto}>
                Unirse al Reto
              </Button>
            </>
          )}
          <Button
            variant="secondary"
            onClick={() => navigate('dashboard')}
          >
            Volver al Dashboard
          </Button>
        </div>
      </div>
    </div>
  );
};

export default RetoDetalle;
