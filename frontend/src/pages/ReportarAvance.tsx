import React, { useState } from 'react';
import { useAppContext } from '../contexts/AppContext.tsx';
import FormField from '../components/FormField.tsx';
import Button from '../components/Button.tsx';
import { logger } from '../utils/logger.ts';

interface ReporteForm {
  descripcion: string;
  fecha: string;
  evidencia?: File | null;
  tipoEvidencia: 'foto' | 'video' | 'documento' | 'ninguna';
  notas: string;
}

const ReportarAvance: React.FC = () => {
  const { navigate } = useAppContext();
  const [formData, setFormData] = useState<ReporteForm>({
    descripcion: '',
    fecha: new Date().toISOString().split('T')[0],
    evidencia: null,
    tipoEvidencia: 'ninguna',
    notas: ''
  });
  const [enviando, setEnviando] = useState(false);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);

  // Simular datos del reto actual
  const retoActual = {
    id: '1',
    titulo: 'Ejercicio Diario de 30 Minutos',
    categoria: 'SALUD'
  };

  const handleInputChange = (field: keyof ReporteForm) => (value: string) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      // Validación de tipo y tamaño (máx 10MB)
      const maxSize = 10 * 1024 * 1024; // 10MB
      const allowedTypes = [
        'image/jpeg', 'image/png', 'image/gif',
        'video/mp4', 'video/webm', 'video/ogg',
        'application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'text/plain'
      ];
      if (!allowedTypes.includes(file.type)) {
        alert('Tipo de archivo no permitido.');
        return;
      }
      if (file.size > maxSize) {
        alert('El archivo supera el tamaño máximo permitido (10MB).');
        return;
      }
      setFormData(prev => ({ ...prev, evidencia: file }));
      // Crear preview para imágenes
      if (file.type.startsWith('image/')) {
        const url = URL.createObjectURL(file);
        setPreviewUrl(url);
      }
    }
  };

  const handleEliminarEvidencia = () => {
    setFormData(prev => ({ ...prev, evidencia: null }));
    if (previewUrl) {
      URL.revokeObjectURL(previewUrl);
      setPreviewUrl(null);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setEnviando(true);

    try {
      // Crear reporte de progreso
      logger.info('Iniciando envío de reporte de progreso', 'REPORTE_AVANCE', {
        retoId: retoActual.id,
        tipoEvidencia: formData.tipoEvidencia,
        hasEvidencia: !!formData.evidencia
      });

      // Upload de archivo si existe
      if (formData.evidencia) {
        logger.info('Subiendo evidencia de progreso', 'REPORTE_AVANCE', {
          fileName: formData.evidencia.name,
          fileSize: formData.evidencia.size,
          fileType: formData.evidencia.type
        });
      }

      // Simular delay de envío
      await new Promise(resolve => setTimeout(resolve, 2000));

      // Redirigir al detalle del reto o dashboard
      navigate('reto-detalle');
    } catch (error) {
      logger.error('Error al enviar reporte', 'REPORTE_AVANCE', error);
    } finally {
      setEnviando(false);
    }
  };

  const esFormularioValido = formData.descripcion.trim() && formData.fecha;

  return (
    <div className="reportar-avance-page">
      <div className="container mx-auto px-4 py-6">
        {/* Header */}
        <div className="flex items-center gap-4 mb-6">
          <Button
            variant="secondary"
            size="small"
            onClick={() => navigate('reto-detalle')}
          >
            ← Volver
          </Button>
          <div>
            <h1 className="text-2xl font-bold">Reportar Avance</h1>
            <p className="text-gray-600">{retoActual.titulo}</p>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Información básica */}
          <div className="bg-white rounded-lg border p-6">
            <h2 className="text-lg font-semibold mb-4">Información del Avance</h2>
            
            <div className="space-y-4">
              <FormField
                id="fecha"
                label="Fecha del avance"
                type="date"
                value={formData.fecha}
                onChange={handleInputChange('fecha')}
                max={new Date().toISOString().split('T')[0]}
                required
              />

              <div>
                <label htmlFor="descripcion" className="block text-sm font-medium mb-2">
                  Descripción del avance *
                </label>
                <textarea
                  id="descripcion"
                  value={formData.descripcion}
                  onChange={(e) => handleInputChange('descripcion')(e.target.value)}
                  placeholder="Describe qué hiciste para avanzar en tu reto..."
                  className="w-full p-3 border rounded-lg"
                  rows={4}
                  required
                />
              </div>

              <div>
                <label htmlFor="notas" className="block text-sm font-medium mb-2">
                  Notas adicionales
                </label>
                <textarea
                  id="notas"
                  value={formData.notas}
                  onChange={(e) => handleInputChange('notas')(e.target.value)}
                  placeholder="Cualquier información adicional, dificultades encontradas, sensaciones, etc."
                  className="w-full p-3 border rounded-lg"
                  rows={3}
                />
              </div>
            </div>
          </div>

          {/* Evidencia */}
          <div className="bg-white rounded-lg border p-6">
            <h2 className="text-lg font-semibold mb-4">Evidencia (Opcional)</h2>
            
            <div className="space-y-4">
              <div>
                <label htmlFor="tipoEvidencia" className="block text-sm font-medium mb-2">
                  Tipo de evidencia
                </label>
                <select
                  id="tipoEvidencia"
                  value={formData.tipoEvidencia}
                  onChange={(e) => handleInputChange('tipoEvidencia')(e.target.value)}
                  className="w-full p-3 border rounded-lg"
                >
                  <option value="ninguna">Sin evidencia</option>
                  <option value="foto">Fotografía</option>
                  <option value="video">Video</option>
                  <option value="documento">Documento</option>
                </select>
              </div>

              {formData.tipoEvidencia !== 'ninguna' && (() => {
                let acceptType = '';
                if (formData.tipoEvidencia === 'foto') {
                  acceptType = 'image/*';
                } else if (formData.tipoEvidencia === 'video') {
                  acceptType = 'video/*';
                } else {
                  acceptType = '.pdf,.doc,.docx,.txt';
                }
                return (
                  <div>
                    <label htmlFor="evidencia" className="block text-sm font-medium mb-2">
                      Subir archivo
                    </label>
                    <input
                      id="evidencia"
                      type="file"
                      onChange={handleFileChange}
                      accept={acceptType}
                      className="w-full p-3 border rounded-lg"
                    />
                    {formData.evidencia && (
                      <div className="mt-3 p-3 bg-gray-50 rounded-lg">
                        <div className="flex justify-between items-center">
                          <span className="text-sm">
                            📎 {formData.evidencia.name} ({Math.round(formData.evidencia.size / 1024)} KB)
                          </span>
                          <Button
                            type="button"
                            variant="danger"
                            size="small"
                            onClick={handleEliminarEvidencia}
                          >
                            Eliminar
                          </Button>
                        </div>
                        {previewUrl && (
                          <div className="mt-3">
                            <img
                              src={previewUrl}
                              alt="Preview"
                              className="max-w-full h-32 object-cover rounded border"
                            />
                          </div>
                        )}
                      </div>
                    )}
                  </div>
                );
              })()}
              )
            </div>
          </div>

          {/* Consejos */}
          <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
            <h3 className="font-medium text-blue-900 mb-2">💡 Consejos para un buen reporte</h3>
            <ul className="text-sm text-blue-800 space-y-1">
              <li>• Sé específico sobre lo que hiciste</li>
              <li>• Incluye detalles como duración, intensidad o métricas</li>
              <li>• Si tuviste dificultades, menciónalas</li>
              <li>• La evidencia ayuda a los validadores a aprobar tu avance</li>
            </ul>
          </div>

          {/* Botones de acción */}
          <div className="flex gap-4 pt-4">
            <Button
              type="button"
              variant="secondary"
              onClick={() => navigate('reto-detalle')}
              disabled={enviando}
            >
              Cancelar
            </Button>
            <Button
              type="submit"
              disabled={!esFormularioValido || enviando}
            >
              {enviando ? 'Enviando reporte...' : 'Enviar reporte'}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ReportarAvance;
