import React, { useState, useEffect } from 'react';
import { useAppContext } from '../contexts/AppContext.tsx';
import Button from '../components/Button.tsx';

interface NotificacionLocal {
  id: string;
  tipo: 'RETO_PENDIENTE' | 'VALIDACION_REQUERIDA' | 'REPORTE_APROBADO' | 'REPORTE_RECHAZADO' | 'RETO_COMPLETADO' | 'RETO_FALLIDO';
  titulo: string;
  mensaje: string;
  leida: boolean;
  fecha: string;
  accion?: {
    tipo: 'NAVEGAR' | 'MODAL' | 'EXTERNAL';
    destino: string;
  };
}

const Notificaciones: React.FC = () => {
  const { navigate } = useAppContext();
  const [notificaciones, setNotificaciones] = useState<NotificacionLocal[]>([]);
  const [filtro, setFiltro] = useState<'todas' | 'no-leidas'>('todas');

  useEffect(() => {
    // Simular carga de notificaciones
    const mockNotificaciones: NotificacionLocal[] = [
      {
        id: '1',
        tipo: 'VALIDACION_REQUERIDA',
        titulo: 'Validación Pendiente',
        mensaje: 'El reto "Ejercicio Diario" requiere tu validación',
        leida: false,
        fecha: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
        accion: { tipo: 'NAVEGAR', destino: 'validaciones' }
      },
      {
        id: '2',
        tipo: 'REPORTE_APROBADO',
        titulo: 'Reporte Aprobado',
        mensaje: 'Tu reporte para "Leer 30 minutos" ha sido aprobado',
        leida: false,
        fecha: new Date(Date.now() - 4 * 60 * 60 * 1000).toISOString()
      },
      {
        id: '3',
        tipo: 'RETO_COMPLETADO',
        titulo: '¡Reto Completado!',
        mensaje: 'Has completado exitosamente "Meditar diariamente"',
        leida: true,
        fecha: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString()
      }
    ];
    setNotificaciones(mockNotificaciones);
  }, []);

  const notificacionesFiltradas = notificaciones.filter(notif => 
    filtro === 'todas' || !notif.leida
  );

  const marcarComoLeida = (id: string) => {
    setNotificaciones(prev => 
      prev.map(notif => 
        notif.id === id ? { ...notif, leida: true } : notif
      )
    );
  };

  const marcarTodasComoLeidas = () => {
    setNotificaciones(prev => 
      prev.map(notif => ({ ...notif, leida: true }))
    );
  };

  const handleAccionNotificacion = (notificacion: NotificacionLocal) => {
    marcarComoLeida(notificacion.id);
    if (notificacion.accion?.tipo === 'NAVEGAR') {
      navigate(notificacion.accion.destino);
    }
  };

  const getIconoTipo = (tipo: NotificacionLocal['tipo']) => {
    const iconos = {
      RETO_PENDIENTE: '⏰',
      VALIDACION_REQUERIDA: '✋',
      REPORTE_APROBADO: '✅',
      REPORTE_RECHAZADO: '❌',
      RETO_COMPLETADO: '🎉',
      RETO_FALLIDO: '💔'
    };
    return iconos[tipo];
  };

  const formatearFecha = (fecha: string) => {
    const now = new Date();
    const fechaNotif = new Date(fecha);
    const diffMs = now.getTime() - fechaNotif.getTime();
    const diffHoras = Math.floor(diffMs / (1000 * 60 * 60));
    
    if (diffHoras < 1) return 'Hace unos minutos';
    if (diffHoras < 24) return `Hace ${diffHoras} hora${diffHoras > 1 ? 's' : ''}`;
    const diffDias = Math.floor(diffHoras / 24);
    return `Hace ${diffDias} día${diffDias > 1 ? 's' : ''}`;
  };

  return (
    <div className="notificaciones-page">
      <div className="container mx-auto px-4 py-6">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-2xl font-bold">Notificaciones</h1>
          <div className="flex gap-2">
            <Button
              variant="secondary"
              size="small"
              onClick={marcarTodasComoLeidas}
              disabled={notificaciones.every(n => n.leida)}
            >
              Marcar todas como leídas
            </Button>
          </div>
        </div>

        <div className="flex gap-4 mb-6">
          <Button
            variant={filtro === 'todas' ? 'primary' : 'secondary'}
            size="small"
            onClick={() => setFiltro('todas')}
          >
            Todas ({notificaciones.length})
          </Button>
          <Button
            variant={filtro === 'no-leidas' ? 'primary' : 'secondary'}
            size="small"
            onClick={() => setFiltro('no-leidas')}
          >
            No leídas ({notificaciones.filter(n => !n.leida).length})
          </Button>
        </div>

        <div className="space-y-3">
          {notificacionesFiltradas.length === 0 ? (
            <div className="text-center py-8 text-gray-500">
              {filtro === 'no-leidas' ? 
                'No tienes notificaciones sin leer' : 
                'No tienes notificaciones'
              }
            </div>
          ) : (
            notificacionesFiltradas.map(notificacion => (
              <button
                key={notificacion.id}
                className={`w-full text-left p-4 border rounded-lg cursor-pointer transition-colors ${
                  notificacion.leida 
                    ? 'bg-gray-50 border-gray-200' 
                    : 'bg-blue-50 border-blue-200 shadow-sm'
                }`}
                onClick={() => handleAccionNotificacion(notificacion)}
                aria-label={`Notificación: ${notificacion.titulo}`}
              >
                <div className="flex items-start gap-3">
                  <span className="text-2xl">{getIconoTipo(notificacion.tipo)}</span>
                  <div className="flex-1">
                    <div className="flex justify-between items-start mb-1">
                      <h3 className={`font-medium ${!notificacion.leida ? 'text-blue-900' : ''}`}>
                        {notificacion.titulo}
                      </h3>
                      {!notificacion.leida && (
                        <span className="w-2 h-2 bg-blue-500 rounded-full flex-shrink-0"></span>
                      )}
                    </div>
                    <p className="text-gray-600 text-sm mb-2">{notificacion.mensaje}</p>
                    <span className="text-xs text-gray-400">
                      {formatearFecha(notificacion.fecha)}
                    </span>
                  </div>
                </div>
                {notificacion.accion && (
                  <div className="mt-2 ml-11">
                    <span className="text-xs text-blue-600">Toca para ver más →</span>
                  </div>
                )}
              </button>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default Notificaciones;
