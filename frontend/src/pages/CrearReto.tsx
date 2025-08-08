import React, { useState } from 'react';
import { useAppContext } from '../contexts/AppContext.tsx';
import FormField from '../components/FormField.tsx';
import Button from '../components/Button.tsx';
import { logger } from '../utils/logger.ts';

interface RetoForm {
  titulo: string;
  descripcion: string;
  categoria: string;
  dificultad: 'FACIL' | 'MEDIO' | 'DIFICIL';
  puntos: number;
  fechaLimite: string;
  requisitos: string;
}

const CrearReto: React.FC = () => {
  const { navigate } = useAppContext();
  const [formData, setFormData] = useState<RetoForm>({
    titulo: '',
    descripcion: '',
    categoria: '',
    dificultad: 'MEDIO',
    puntos: 100,
    fechaLimite: '',
    requisitos: ''
  });
  const [loading, setLoading] = useState(false);

  const handleInputChange = (field: keyof RetoForm) => (value: string) => {
    setFormData(prev => ({
      ...prev,
      [field]: field === 'puntos' ? parseInt(value) || 0 : value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      // Validar datos del formulario
      if (formData.titulo.length < 5) {
        throw new Error('El título debe tener al menos 5 caracteres');
      }
      
      if (formData.descripcion.length < 20) {
        throw new Error('La descripción debe tener al menos 20 caracteres');
      }

      logger.info(
        'Iniciando creación de nuevo reto',
        'CrearReto',
        {
          titulo: formData.titulo,
          categoria: formData.categoria,
          dificultad: formData.dificultad
        }
      );
      
      // Simular llamada a API
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      logger.info(
        'Reto creado exitosamente',
        'CrearReto',
        { titulo: formData.titulo }
      );
      
      // Navegar a mis retos después de crear
      navigate('mis-retos');
    } catch (error) {
      logger.error(
        'Error al crear reto',
        'CrearReto',
        {
          titulo: formData.titulo,
          error: error instanceof Error ? error.message : String(error)
        }
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="crear-reto-page">
      <div className="container mx-auto px-4 py-6">
        <h1 className="text-2xl font-bold mb-6">Crear Nuevo Reto</h1>
        
        <form onSubmit={handleSubmit} className="space-y-4">
          <FormField
            id="titulo"
            label="Título del Reto"
            type="text"
            value={formData.titulo}
            onChange={handleInputChange('titulo')}
            placeholder="Escribe un título atractivo"
            required
          />

          <div>
            <label htmlFor="descripcion" className="block text-sm font-medium mb-2">
              Descripción
            </label>
            <textarea
              id="descripcion"
              value={formData.descripcion}
              onChange={(e) => handleInputChange('descripcion')(e.target.value)}
              placeholder="Describe el reto en detalle..."
              className="w-full p-3 border rounded-lg"
              rows={4}
              required
            />
          </div>

          <FormField
            id="categoria"
            label="Categoría"
            type="text"
            value={formData.categoria}
            onChange={handleInputChange('categoria')}
            placeholder="Ej: SALUD, EDUCACION, DEPORTE"
            required
          />

          <div>
            <label htmlFor="dificultad" className="block text-sm font-medium mb-2">
              Dificultad
            </label>
            <select
              id="dificultad"
              value={formData.dificultad}
              onChange={(e) => handleInputChange('dificultad')(e.target.value)}
              className="w-full p-3 border rounded-lg"
            >
              <option value="FACIL">Fácil</option>
              <option value="MEDIO">Medio</option>
              <option value="DIFICIL">Difícil</option>
            </select>
          </div>

          <FormField
            id="puntos"
            label="Puntos de Recompensa"
            type="number"
            value={formData.puntos.toString()}
            onChange={handleInputChange('puntos')}
            min="10"
            max="1000"
            required
          />

          <FormField
            id="fechaLimite"
            label="Fecha Límite"
            type="date"
            value={formData.fechaLimite}
            onChange={handleInputChange('fechaLimite')}
            required
          />

          <div>
            <label htmlFor="requisitos" className="block text-sm font-medium mb-2">
              Requisitos
            </label>
            <textarea
              id="requisitos"
              value={formData.requisitos}
              onChange={(e) => handleInputChange('requisitos')(e.target.value)}
              placeholder="Especifica los requisitos para completar el reto..."
              className="w-full p-3 border rounded-lg"
              rows={3}
            />
          </div>

          <div className="flex gap-4 pt-4">
            <Button
              type="button"
              variant="secondary"
              onClick={() => navigate('dashboard')}
            >
              Cancelar
            </Button>
            <Button
              type="submit"
              disabled={loading || !formData.titulo || !formData.descripcion}
            >
              {loading ? 'Creando...' : 'Crear Reto'}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CrearReto;
