import React, { useEffect, useState } from 'react';
// Opcional: importa sin extensión si tu TS lo soporta
import Header from '../components/Header';
import Footer from '../components/Footer';
import Button from '../components/Button';
import Modal from '../components/Modal';
import { useAppContext } from '../contexts/AppContext';
import { logger } from '../utils/logger.ts';

interface Evidencia {
  id: string;
  retoId: string;
  usuarioId?: string;
  retoTitulo?: string;
  autorNombre?: string;
  tipoEvidencia: string;
  kind?: string;
  descripcion: string;
  downloadUrl?: string;
  thumbnailUrl?: string;
  contenido?: string;
  estadoValidacion: string;
  fechaReporte?: string;
  fechaValidacion?: string;
  validadorId?: string;
  valorReportado?: number;
  unidadMedida?: string;
  metadatos?: any;
}

const Validaciones: React.FC = () => {
  const { state, navigate } = useAppContext();
  const { currentUser: user } = state;

  const [evidencias, setEvidencias] = useState<Evidencia[]>([]);
  const [evidenciaSeleccionada, setEvidenciaSeleccionada] = useState<Evidencia | null>(null);
  const [modalAbierto, setModalAbierto] = useState(false);
  const [procesando, setProcesando] = useState(false);
  const [loading, setLoading] = useState(true);

  // Redirecciones básicas por usuario/rol
  useEffect(() => {
    if (!user) {
      navigate('/login');
      return;
    }
    if (user.rol !== 'VALIDADOR' && user.rol !== 'ADMIN') {
      navigate('/dashboard');
    }
  }, [user, navigate]);

  // Carga simulada
  useEffect(() => {
    if (!user) return;
    const timer = setTimeout(() => {
      const evidenciasSimuladas: Evidencia[] = [
        {
          id: '1',
          retoId: 'reto-1',
          retoTitulo: '30 días de ejercicio',
          autorNombre: 'Ana García',
          tipoEvidencia: 'FOTO',
          contenido: '/api/evidencias/1/foto.jpg',
          descripcion: 'Día 15 - Rutina de 45 minutos en el gimnasio. Hoy enfoqué en piernas y glúteos.',
          fechaReporte: '2025-08-07T09:30:00Z',
          estadoValidacion: 'PENDIENTE'
        },
        {
          id: '2',
          retoId: 'reto-2',
          retoTitulo: 'Aprender TypeScript',
          autorNombre: 'Carlos Ruiz',
          tipoEvidencia: 'TEXTO',
          contenido: '',
          descripcion: 'Completé el módulo de interfaces y types. Adjunto el código de mi proyecto personal donde implementé todo lo aprendido.',
          fechaReporte: '2025-08-07T14:15:00Z',
          estadoValidacion: 'PENDIENTE'
        },
        {
          id: '3',
          retoId: 'reto-3',
          retoTitulo: 'Cocinar saludable',
          autorNombre: 'María López',
          tipoEvidencia: 'VIDEO',
          contenido: '/api/evidencias/3/video.mp4',
          descripcion: 'Preparando ensalada de quinoa con vegetales orgánicos. Receta completa paso a paso.',
          fechaReporte: '2025-08-07T18:45:00Z',
          estadoValidacion: 'PENDIENTE'
        }
      ];
      setEvidencias(evidenciasSimuladas);
      setLoading(false);
    }, 1000);

    return () => clearTimeout(timer);
  }, [user]);

  const abrirModal = (e: Evidencia) => {
    setEvidenciaSeleccionada(e);
    setModalAbierto(true);
  };

  const cerrarModal = () => {
    setModalAbierto(false);
    setEvidenciaSeleccionada(null);
  };

  const validarEvidencia = async (aprobada: boolean) => {
    if (!evidenciaSeleccionada) return;
    setProcesando(true);
    try {
      // Simula API
      await new Promise((r) => setTimeout(r, 1500));

      // Log de auditoría
      logger.info(
        `Evidencia ${aprobada ? 'aprobada' : 'rechazada'}`,
        'AUDIT',
        {
          evidenciaId: evidenciaSeleccionada.id,
          usuario: user?.email,
          accion: aprobada ? 'APROBAR' : 'RECHAZAR',
          fecha: new Date().toISOString()
        }
      );

      // Elimina de la lista de pendientes
      setEvidencias((prev) => prev.filter((ev) => ev.id !== evidenciaSeleccionada.id));

      alert(`Evidencia ${aprobada ? 'aprobada' : 'rechazada'} exitosamente`);
      cerrarModal();
    } catch (err) {
      logger.error('Error al validar evidencia', 'VALIDATION', err);
      alert('Error al procesar la validación. Inténtalo de nuevo.');
    } finally {
      setProcesando(false);
    }
  };

  const formatearFecha = (fechaISO: string) => {
    const fecha = new Date(fechaISO);
    return new Intl.DateTimeFormat('es-ES', {
      year: 'numeric',
      month: 'short',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      hour12: false,
      timeZone: 'Europe/Madrid',
    }).format(fecha);
  };

  const getTipoIcon = (tipo: Evidencia['tipoEvidencia']) => {
    switch (tipo) {
      case 'FOTO': return '📷';
      case 'VIDEO': return '🎥';
      case 'TEXTO': return '📝';
      default: return '';
    }
  };

  // Si no hay usuario o no tiene permisos, no renderizamos nada (ya redirige el useEffect)
  if (!user || (user.rol !== 'VALIDADOR' && user.rol !== 'ADMIN')) {
    return null;
  }

  const renderContenido = () => {
    if (loading) {
      return (
        <div className="text-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mx-auto"></div>
          <p className="mt-4 text-gray-600">Cargando evidencias pendientes...</p>
        </div>
      );
    }

    if (evidencias.length === 0) {
      return (
        <div className="text-center py-12 bg-gray-50 rounded-lg">
          <div className="text-6xl mb-4">✅</div>
          <h3 className="text-xl font-semibold mb-2">¡Todo al día!</h3>
          <p className="text-gray-600">No hay evidencias pendientes de validación en este momento.</p>
        </div>
      );
    }

    return (
      <div className="space-y-6">
        {evidencias.map((evidencia) => (
          <div key={evidencia.id} className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow">
            <div className="flex justify-between items-start mb-4">
              <div className="flex-1">
                <div className="flex items-center gap-2 mb-2">
                  <span className="text-2xl">{getTipoIcon(evidencia.tipoEvidencia)}</span>
                  <h3 className="font-bold text-lg">{evidencia.retoTitulo}</h3>
                  <span className="px-2 py-1 bg-yellow-100 text-yellow-800 rounded-full text-xs font-medium">
                    PENDIENTE
                  </span>
                </div>
                <p className="text-gray-600 mb-2">
                  <strong>Autor:</strong> {evidencia.autorNombre}
                </p>
                <p className="text-gray-600 mb-4">
                  <strong>Enviado:</strong> {formatearFecha(evidencia.fechaReporte || '')}
                </p>
              </div>
            </div>

            <div className="bg-gray-50 rounded-lg p-4 mb-4">
              <h4 className="font-semibold mb-2">Descripción del usuario:</h4>
              <p className="text-gray-700">{evidencia.descripcion}</p>
            </div>

            {evidencia.tipoEvidencia === 'FOTO' && evidencia.contenido && (
              <div className="mb-4">
                <img
                  src={evidencia.contenido}
                  alt={`Evidencia ${evidencia.id}`}
                  className="w-full h-64 object-cover rounded-lg"
                />
              </div>
            )}

            {evidencia.tipoEvidencia === 'VIDEO' && evidencia.contenido && (
              <div className="mb-4">
                <video
                  src={evidencia.contenido}
                  controls
                  className="w-full h-64 bg-black rounded-lg"
                >
                  <track kind="captions" src="" label="Español" default />
                </video>
              </div>
            )}

            <div className="flex justify-end space-x-3">
              <Button variant="secondary" onClick={() => abrirModal(evidencia)}>
                Ver Detalles
              </Button>
              <Button
                variant="danger"
                onClick={() => {
                  setEvidenciaSeleccionada(evidencia);
                  validarEvidencia(false);
                }}
                disabled={procesando}
              >
                Rechazar
              </Button>
              <Button
                variant="primary"
                onClick={() => {
                  setEvidenciaSeleccionada(evidencia);
                  validarEvidencia(true);
                }}
                disabled={procesando}
              >
                Aprobar
              </Button>
            </div>
          </div>
        ))}
      </div>
    );
  };

  return (
    <div className="page">
      <Header />
      <main className="main-content">
        <div className="container mx-auto px-4 py-8">
          <div className="mb-8">
            <h1 className="text-3xl font-bold mb-2">Panel de Validaciones</h1>
            <p className="text-gray-600">Revisa y valida las evidencias enviadas por los usuarios</p>
          </div>
          {renderContenido()}
        </div>
      </main>

      <Modal isOpen={modalAbierto} onClose={cerrarModal} title="Detalles de Evidencia">
        {evidenciaSeleccionada && (
          <div className="space-y-4">
            <div>
              <h3 className="font-semibold text-lg">{evidenciaSeleccionada.retoTitulo}</h3>
              <p className="text-gray-600">Por: {evidenciaSeleccionada.autorNombre}</p>
              <p className="text-sm text-gray-500">{formatearFecha(evidenciaSeleccionada.fechaReporte || '')}</p>
            </div>

            <div>
              <h4 className="font-semibold mb-2">Tipo de evidencia:</h4>
              <span className="inline-flex items-center gap-1 px-3 py-1 bg-blue-100 text-blue-800 rounded-full">
                {getTipoIcon(evidenciaSeleccionada.tipoEvidencia)} {evidenciaSeleccionada.tipoEvidencia}
              </span>
            </div>

            <div>
              <h4 className="font-semibold mb-2">Descripción:</h4>
              <p className="text-gray-700">{evidenciaSeleccionada.descripcion}</p>
            </div>

            <div className="flex justify-end space-x-3">
              <Button variant="danger" onClick={() => validarEvidencia(false)} disabled={procesando}>
                Rechazar
              </Button>
              <Button variant="primary" onClick={() => validarEvidencia(true)} disabled={procesando}>
                Aprobar
              </Button>
            </div>
          </div>
        )}
      </Modal>

      <Footer />
    </div>
  );
};

export default Validaciones;
