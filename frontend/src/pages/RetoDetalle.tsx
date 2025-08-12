import React, { useState, useEffect } from 'react';
import { useAppContext } from '../contexts/AppContext.tsx';
import Button from '../components/Button.tsx';
import { logger } from '../utils/logger.ts';
import { useReto, Reto, ReporteAvance } from '../hooks/useReto';
import Skeleton from '../components/Skeleton';

import { z } from 'zod';

const retoSchema = z.object({
  titulo: z.string().min(3, 'El título es obligatorio'),
  descripcion: z.string().min(5, 'La descripción es obligatoria'),
  categoria: z.string(),
  dificultad: z.string(),
});



const RetoDetalle: React.FC = () => {
  const { navigate } = useAppContext();
  const [mostrarReportes, setMostrarReportes] = useState(false);
  const { retos, loading, error, actualizarReto } = useReto();

  // Obtener retoId de la URL o props
  const retoId = '1'; // Reemplazar por obtención real
  const reto: Reto | undefined = retos.find((r: any) => r.id === retoId);

  useEffect(() => {
    if (reto) {
      setMostrarReportes(reto.reportes.length > 0);
    }
  }, [reto]);


  const handleUnirseReto = async () => {
    try {
      logger.info('Usuario iniciando unión al reto', 'RETO_DETALLE', { retoId });
      // Aquí lógica real para unirse al reto
    } catch (error) {
      logger.error('Error al unirse al reto', 'RETO_DETALLE', { 
        retoId, 
        error: error instanceof Error ? error.message : String(error) 
      });
    }
  };

  const handleReportarAvance = () => {
    // Aquí lógica para abrir modal o navegar a página de reporte de avance
    logger.info('Reportar avance clicado', 'RETO_DETALLE', { retoId });
  };

  const handleActualizarReto = async (datos: any) => {
    const result = retoSchema.safeParse(datos);
    if (!result.success) {
      // feedback visual de errores
      return;
    }
    try {
      await actualizarReto(retoId, datos); // Optimistic update
    } catch (err) {
      // error ya manejado en hook
    }
  };

  if (loading) {
    return <div style={{ maxWidth: 600, margin: '2rem auto' }}><Skeleton height={40} /><Skeleton height={40} /><Skeleton height={40} /></div>;
  }
  if (error) {
    return <div className="error-message" role="alert">{error}</div>;
  }
  if (!reto) {
    return <div className="empty-state" role="status">Reto no encontrado.</div>;
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
            <span className={`px-3 py-1 rounded-full text-sm font-medium ${getDificultadColor(reto.dificultad)}`}>{reto.dificultad}</span>
            <span className="px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800">
              {reto.categoria}
            </span>
          </div>
          <p className="text-gray-700 mb-4">{reto.descripcion}</p>
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
              {reto.reportes.map((reporte: ReporteAvance) => {
                // Estado principal: el de la primera validación, o 'PENDIENTE' si no hay
                const estado = reporte.validaciones?.[0]?.estado || 'PENDIENTE';
                // Evidencia: mostrar texto, imagen o video
                let evidenciaNode: React.ReactNode = null;
                if (reporte.evidencia) {
                  if (reporte.evidencia.tipo === 'IMAGEN' && reporte.evidencia.url) {
                    evidenciaNode = <img src={reporte.evidencia.url} alt="Evidencia" className="max-w-xs mt-1" />;
                  } else if (reporte.evidencia.tipo === 'VIDEO' && reporte.evidencia.url) {
                    evidenciaNode = <video src={reporte.evidencia.url} controls className="max-w-xs mt-1" />;
                  } else {
                    evidenciaNode = <span className="text-blue-600 mt-1">📎 {reporte.evidencia.contenido}</span>;
                  }
                }
                return (
                  <div key={reporte.id} className="border rounded-lg p-4">
                    <div className="flex justify-between items-start mb-2">
                      <span className="text-sm text-gray-500">
                        {formatearFecha(reporte.fecha)}
                      </span>
                      <span className={`px-2 py-1 rounded text-xs font-medium ${getEstadoReporteColor(estado)}`}>
                        {estado}
                      </span>
                    </div>
                    <p className="text-gray-700">{reporte.descripcion}</p>
                    {evidenciaNode && (
                      <div>{evidenciaNode}</div>
                    )}
                  </div>
                );
              })}
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
}

// Helpers para colores y formato
const getEstadoColor = (estado: string) => {
  const colores: Record<string, string> = {
    'ACTIVO': 'bg-green-200 text-green-800',
    'COMPLETADO': 'bg-blue-200 text-blue-800',
    'FALLIDO': 'bg-red-200 text-red-800',
    'PAUSADO': 'bg-yellow-200 text-yellow-800',
  };
  return colores[estado] || 'bg-gray-200 text-gray-800';
};
const getDificultadColor = (dif: string) => {
  const colores: Record<string, string> = {
    'FACIL': 'bg-green-100 text-green-700',
    'MEDIO': 'bg-yellow-100 text-yellow-700',
    'DIFICIL': 'bg-orange-100 text-orange-700',
    'EXTREMO': 'bg-red-100 text-red-700',
  };
  return colores[dif] || 'bg-gray-100 text-gray-700';
};
const formatearFecha = (fecha: string) => new Date(fecha).toLocaleDateString('es-ES');
const getEstadoReporteColor = (estado: string) => {
  const colores: Record<string, string> = {
    'PENDIENTE': 'bg-yellow-100 text-yellow-800',
    'APROBADO': 'bg-green-100 text-green-800',
    'RECHAZADO': 'bg-red-100 text-red-800',
  };
  return colores[estado] || 'bg-gray-100 text-gray-800';
};

export default RetoDetalle;
