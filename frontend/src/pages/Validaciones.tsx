import React, { useState, useEffect } from 'react';
import Header from '../components/Header.tsx';
import Footer from '../components/Footer.tsx';
import Button from '../components/Button.tsx';
import Modal from '../components/Modal.tsx';
import { useAppContext } from '../contexts/AppContext.tsx';

interface Evidencia {
  id: string;
  retoId: string;
  retoTitulo: string;
  autorNombre: string;
  tipo: 'FOTO' | 'VIDEO' | 'TEXTO';
  contenido: string;
  descripcion: string;
  fechaSubida: string;
  estado: 'PENDIENTE' | 'APROBADA' | 'RECHAZADA';
}

const Validaciones: React.FC = () => {
  const { state, navigate } = useAppContext();
  const { currentUser: user } = state;
  const [evidencias, setEvidencias] = useState<Evidencia[]>([]);
  const [evidenciaSeleccionada, setEvidenciaSeleccionada] = useState<Evidencia | null>(null);
  const [modalAbierto, setModalAbierto] = useState(false);
  const [comentario, setComentario] = useState('');
  const [procesando, setProcesando] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!user) {
      navigate('/login');
      return;
    }

    // Verificar si el usuario puede validar (rol VALIDADOR o ADMIN)
    if (user.rol !== 'VALIDADOR' && user.rol !== 'ADMIN') {
      navigate('/dashboard');
      return;
    }

    // Simular carga de evidencias pendientes
    setTimeout(() => {
      const evidenciasSimuladas: Evidencia[] = [
        {
          id: '1',
          retoId: 'reto-1',
          retoTitulo: '30 días de ejercicio',
          autorNombre: 'Ana García',
          tipo: 'FOTO',
          contenido: '/api/evidencias/1/foto.jpg',
          descripcion: 'Día 15 - Rutina de 45 minutos en el gimnasio. Hoy enfoqué en piernas y glúteos.',
          fechaSubida: '2025-08-07T09:30:00Z',
          estado: 'PENDIENTE'
        },
        {
          id: '2',
          retoId: 'reto-2',
          retoTitulo: 'Aprender TypeScript',
          autorNombre: 'Carlos Ruiz',
          tipo: 'TEXTO',
          contenido: '',
          descripcion: 'Completé el módulo de interfaces y types. Adjunto el código de mi proyecto personal donde implementé todo lo aprendido.',
          fechaSubida: '2025-08-07T14:15:00Z',
          estado: 'PENDIENTE'
        },
        {
          id: '3',
          retoId: 'reto-3',
          retoTitulo: 'Cocinar saludable',
          autorNombre: 'María López',
          tipo: 'VIDEO',
          contenido: '/api/evidencias/3/video.mp4',
          descripcion: 'Preparando ensalada de quinoa con vegetales orgánicos. Receta completa paso a paso.',
          fechaSubida: '2025-08-07T18:45:00Z',
          estado: 'PENDIENTE'
        }
      ];
      setEvidencias(evidenciasSimuladas);
      setLoading(false);
    }, 1000);
  }, [user, navigate]);

  const abrirModal = (evidencia: Evidencia) => {
    setEvidenciaSeleccionada(evidencia);
    setModalAbierto(true);
    setComentario('');
  };

  const cerrarModal = () => {
    setModalAbierto(false);
    setEvidenciaSeleccionada(null);
    setComentario('');
  };

  const validarEvidencia = async (aprobada: boolean) => {
    if (!evidenciaSeleccionada) return;

    setProcesando(true);

    try {
      // Simular llamada a la API
      await new Promise(resolve => setTimeout(resolve, 1500));

      // Actualizar estado local
      setEvidencias(prev => prev.map(ev => 
        ev.id === evidenciaSeleccionada.id 
          ? { ...ev, estado: aprobada ? 'APROBADA' : 'RECHAZADA' }
          : ev
      ));

      // Remover de la lista (ya que ya no está pendiente)
      setEvidencias(prev => prev.filter(ev => ev.id !== evidenciaSeleccionada.id));

      alert(`Evidencia ${aprobada ? 'aprobada' : 'rechazada'} exitosamente`);
      cerrarModal();

    } catch (error) {
      console.error('Error al validar evidencia:', error);
      alert('Error al procesar la validación. Inténtalo de nuevo.');
    } finally {
      setProcesando(false);
    }
  };

  const formatearFecha = (fechaISO: string) => {
    const fecha = new Date(fechaISO);
    return fecha.toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getTipoIcon = (tipo: string) => {
    switch (tipo) {
      case 'FOTO': return '';
      case 'VIDEO': return '';
      case 'TEXTO': return '';
      default: return '';
    }
  };

  if (!user || (user.rol !== 'VALIDADOR' && user.rol !== 'ADMIN')) {
    return null;
  }

  return (
    <div className="page">
      <Header />
      <main className="main-content">
        <div className="container mx-auto px-4 py-8">
          <div className="mb-8">
            <h1 className="text-3xl font-bold mb-2">Panel de Validaciones</h1>
            <p className="text-gray-600">Revisa y valida las evidencias enviadas por los usuarios</p>
          </div>

          {loading ? (
            <div className="text-center py-12">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mx-auto"></div>
              <p className="mt-4 text-gray-600">Cargando evidencias pendientes...</p>
            </div>
          ) : evidencias.length === 0 ? (
            <div className="text-center py-12 bg-gray-50 rounded-lg">
              <div className="text-6xl mb-4"></div>
              <h3 className="text-xl font-semibold mb-2">¡Todo al día!</h3>
              <p className="text-gray-600">No hay evidencias pendientes de validación en este momento.</p>
            </div>
          ) : (
            <div className="space-y-6">
              {evidencias.map(evidencia => (
                <div key={evidencia.id} className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow">
                  <div className="flex justify-between items-start mb-4">
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-2">
                        <span className="text-2xl">{getTipoIcon(evidencia.tipo)}</span>
                        <h3 className="font-bold text-lg">{evidencia.retoTitulo}</h3>
                        <span className="px-2 py-1 bg-yellow-100 text-yellow-800 rounded-full text-xs font-medium">
                          PENDIENTE
                        </span>
                      </div>
                      <p className="text-gray-600 mb-2">
                        <strong>Autor:</strong> {evidencia.autorNombre}
                      </p>
                      <p className="text-gray-600 mb-4">
                        <strong>Enviado:</strong> {formatearFecha(evidencia.fechaSubida)}
                      </p>
                    </div>
                  </div>

                  <div className="bg-gray-50 rounded-lg p-4 mb-4">
                    <h4 className="font-semibold mb-2">Descripción del usuario:</h4>
                    <p className="text-gray-700">{evidencia.descripcion}</p>
                  </div>

                  {evidencia.tipo === 'FOTO' && (
                    <div className="mb-4">
                      <div className="w-full h-48 bg-gray-200 rounded-lg flex items-center justify-center">
                        <span className="text-gray-500"> Imagen: {evidencia.contenido}</span>
                      </div>
                    </div>
                  )}

                  {evidencia.tipo === 'VIDEO' && (
                    <div className="mb-4">
                      <div className="w-full h-48 bg-gray-200 rounded-lg flex items-center justify-center">
                        <span className="text-gray-500"> Video: {evidencia.contenido}</span>
                      </div>
                    </div>
                  )}

                  <div className="flex justify-end space-x-3">
                    <Button 
                      variant="secondary"
                      onClick={() => abrirModal(evidencia)}
                    >
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
          )}
        </div>
      </main>

      {/* Modal de detalles */}
      <Modal
        isOpen={modalAbierto}
        onClose={cerrarModal}
        title="Detalles de Evidencia"
        size="lg"
      >
        {evidenciaSeleccionada && (
          <div className="space-y-4">
            <div>
              <h3 className="font-semibold text-lg">{evidenciaSeleccionada.retoTitulo}</h3>
              <p className="text-gray-600">Por: {evidenciaSeleccionada.autorNombre}</p>
              <p className="text-sm text-gray-500">
                {formatearFecha(evidenciaSeleccionada.fechaSubida)}
              </p>
            </div>

            <div>
              <h4 className="font-semibold mb-2">Tipo de evidencia:</h4>
              <span className="inline-flex items-center gap-1 px-3 py-1 bg-blue-100 text-blue-800 rounded-full">
                {getTipoIcon(evidenciaSeleccionada.tipo)} {evidenciaSeleccionada.tipo}
              </span>
            </div>

            <div>
              <h4 className="font-semibold mb-2">Descripción:</h4>
              <p className="text-gray-700 bg-gray-50 p-3 rounded-lg">
                {evidenciaSeleccionada.descripcion}
              </p>
            </div>

            {evidenciaSeleccionada.contenido && (
              <div>
                <h4 className="font-semibold mb-2">Contenido:</h4>
                <div className="bg-gray-100 p-4 rounded-lg text-center">
                  <p className="text-gray-600">{evidenciaSeleccionada.contenido}</p>
                </div>
              </div>
            )}

            <div>
              <h4 className="font-semibold mb-2">Comentario de validación:</h4>
              <textarea
                value={comentario}
                onChange={(e) => setComentario(e.target.value)}
                placeholder="Añade un comentario para el usuario..."
                className="w-full p-3 border rounded-lg resize-none"
                rows={3}
              />
            </div>

            <div className="flex justify-end space-x-3 pt-4">
              <Button variant="secondary" onClick={cerrarModal}>
                Cancelar
              </Button>
              <Button 
                variant="danger"
                onClick={() => validarEvidencia(false)}
                disabled={procesando}
                loading={procesando}
              >
                 Rechazar
              </Button>
              <Button 
                variant="primary"
                onClick={() => validarEvidencia(true)}
                disabled={procesando}
                loading={procesando}
              >
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
