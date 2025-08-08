import React, { useState } from 'react';
import { useAppContext } from '../contexts/AppContext.tsx';
import FormField from '../components/FormField.tsx';
import Button from '../components/Button.tsx';

interface ConfiguracionForm {
  notificaciones: boolean;
  privacidad: 'PUBLICO' | 'PRIVADO' | 'SOLO_VALIDADORES';
  idioma: 'es' | 'en';
  tema: 'claro' | 'oscuro';
  frecuenciaRecordatorios: 'diario' | 'semanal' | 'nunca';
}

const Configuracion: React.FC = () => {
  const { navigate } = useAppContext();
  const [config, setConfig] = useState<ConfiguracionForm>({
    notificaciones: true,
    privacidad: 'PUBLICO',
    idioma: 'es',
    tema: 'claro',
    frecuenciaRecordatorios: 'diario'
  });
  const [guardando, setGuardando] = useState(false);

  const handleToggle = (campo: keyof ConfiguracionForm) => {
    setConfig(prev => ({
      ...prev,
      [campo]: campo === 'notificaciones' ? !prev[campo] : prev[campo]
    }));
  };

  const handleSelectChange = (campo: keyof ConfiguracionForm) => (value: string) => {
    setConfig(prev => ({
      ...prev,
      [campo]: value
    }));
  };

  const handleGuardar = async () => {
    setGuardando(true);
    try {
      // Simular guardado
      await new Promise(resolve => setTimeout(resolve, 1000));
      console.log('Configuración guardada:', config);
      // Mostrar mensaje de éxito
    } catch (error) {
      console.error('Error al guardar configuración:', error);
    } finally {
      setGuardando(false);
    }
  };

  const handleEliminarCuenta = async () => {
    if (window.confirm('¿Estás seguro de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.')) {
      try {
        // Lógica para eliminar cuenta
        console.log('Eliminando cuenta...');
      } catch (error) {
        console.error('Error al eliminar cuenta:', error);
      }
    }
  };

  const handleExportarDatos = async () => {
    try {
      // Simular exportación de datos
      const datos = {
        usuario: 'datos_usuario',
        retos: 'datos_retos',
        reportes: 'datos_reportes',
        configuracion: config
      };
      const blob = new Blob([JSON.stringify(datos, null, 2)], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'mis_datos_impulse.json';
      a.click();
      URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Error al exportar datos:', error);
    }
  };

  return (
    <div className="configuracion-page">
      <div className="container mx-auto px-4 py-6">
        <h1 className="text-2xl font-bold mb-6">Configuración</h1>
        
        <div className="space-y-6">
          {/* Sección Notificaciones */}
          <div className="bg-white p-6 rounded-lg border">
            <h2 className="text-lg font-semibold mb-4">Notificaciones</h2>
            
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <div>
                  <label htmlFor="notificaciones" className="font-medium">
                    Recibir notificaciones
                  </label>
                  <p className="text-sm text-gray-600">
                    Activa para recibir alertas sobre tus retos
                  </p>
                </div>
                <input
                  id="notificaciones"
                  type="checkbox"
                  checked={config.notificaciones}
                  onChange={() => handleToggle('notificaciones')}
                  className="w-5 h-5"
                />
              </div>

              <FormField
                id="frecuenciaRecordatorios"
                label="Frecuencia de recordatorios"
                type="text"
                value={config.frecuenciaRecordatorios}
                onChange={handleSelectChange('frecuenciaRecordatorios')}
              />
            </div>
          </div>

          {/* Sección Privacidad */}
          <div className="bg-white p-6 rounded-lg border">
            <h2 className="text-lg font-semibold mb-4">Privacidad</h2>
            
            <div>
              <label htmlFor="privacidad" className="block font-medium mb-2">
                Nivel de privacidad
              </label>
              <select
                id="privacidad"
                value={config.privacidad}
                onChange={(e) => handleSelectChange('privacidad')(e.target.value)}
                className="w-full p-3 border rounded-lg"
              >
                <option value="PUBLICO">Público - Todos pueden ver mis retos</option>
                <option value="SOLO_VALIDADORES">Solo validadores pueden ver mis retos</option>
                <option value="PRIVADO">Privado - Solo yo puedo ver mis retos</option>
              </select>
            </div>
          </div>

          {/* Sección Apariencia */}
          <div className="bg-white p-6 rounded-lg border">
            <h2 className="text-lg font-semibold mb-4">Apariencia</h2>
            
            <div className="space-y-4">
              <div>
                <label htmlFor="idioma" className="block font-medium mb-2">
                  Idioma
                </label>
                <select
                  id="idioma"
                  value={config.idioma}
                  onChange={(e) => handleSelectChange('idioma')(e.target.value)}
                  className="w-full p-3 border rounded-lg"
                >
                  <option value="es">Español</option>
                  <option value="en">English</option>
                </select>
              </div>

              <div>
                <label htmlFor="tema" className="block font-medium mb-2">
                  Tema
                </label>
                <select
                  id="tema"
                  value={config.tema}
                  onChange={(e) => handleSelectChange('tema')(e.target.value)}
                  className="w-full p-3 border rounded-lg"
                >
                  <option value="claro">Claro</option>
                  <option value="oscuro">Oscuro</option>
                </select>
              </div>
            </div>
          </div>

          {/* Sección Datos */}
          <div className="bg-white p-6 rounded-lg border">
            <h2 className="text-lg font-semibold mb-4">Mis Datos</h2>
            
            <div className="space-y-4">
              <Button
                variant="secondary"
                onClick={handleExportarDatos}
              >
                Exportar mis datos
              </Button>
              
              <div className="border-t pt-4">
                <h3 className="font-medium text-red-700 mb-2">Zona de peligro</h3>
                <Button
                  variant="danger"
                  onClick={handleEliminarCuenta}
                >
                  Eliminar cuenta permanentemente
                </Button>
              </div>
            </div>
          </div>

          {/* Botones de acción */}
          <div className="flex gap-4 pt-4">
            <Button
              variant="secondary"
              onClick={() => navigate('dashboard')}
            >
              Cancelar
            </Button>
            <Button
              onClick={handleGuardar}
              disabled={guardando}
            >
              {guardando ? 'Guardando...' : 'Guardar cambios'}
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Configuracion;
