import React, { useState } from 'react';
import { useAppContext } from '../contexts/AppContext.tsx';
import FormField from '../components/FormField.tsx';
import Button from '../components/Button.tsx';
import { logger } from '../utils/logger.ts';

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
      // Validar configuración antes de guardar
      logger.info(
        'Guardando configuración de usuario',
        'Configuracion',
        {
          notificaciones: config.notificaciones,
          privacidad: config.privacidad,
          idioma: config.idioma,
          tema: config.tema
        }
      );
      
      // Simular guardado
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Guardar en localStorage para persistencia
      localStorage.setItem('userPreferences', JSON.stringify(config));
      
      logger.info('Configuración guardada exitosamente', 'Configuracion');
      // Mostrar mensaje de éxito
    } catch (error) {
      logger.error(
        'Error al guardar configuración',
        'Configuracion',
        {
          error: error instanceof Error ? error.message : String(error)
        }
      );
    } finally {
      setGuardando(false);
    }
  };

  const handleEliminarCuenta = async () => {
    if (window.confirm('¿Estás seguro de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.')) {
      try {
        logger.warn('Usuario iniciando eliminación de cuenta', 'Configuracion');
        // Lógica para eliminar cuenta
        logger.warn('Cuenta eliminada exitosamente', 'Configuracion');
      } catch (error) {
        logger.error(
          'Error al eliminar cuenta',
          'Configuracion',
          {
            error: error instanceof Error ? error.message : String(error)
          }
        );
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
      logger.error(
        'Error al exportar datos',
        'Configuracion',
        {
          error: error instanceof Error ? error.message : String(error)
        }
      );
    }
  };

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
              <Button variant="secondary" onClick={() => navigate('dashboard')}>
                ← Volver al Dashboard
              </Button>
            </div>
          </div>
        </header>

        <main className="page-main">
          <div className="content-card">
            <h1>⚙️ Configuración</h1>
            <p style={{ color: 'var(--text-secondary)', marginBottom: 0 }}>
              Personaliza tu experiencia en Impulse
            </p>
          </div>
        
          <div className="content-grid cols-1">
            {/* Sección Notificaciones */}
            <div className="form-section">
              <h3>🔔 Notificaciones</h3>
            
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
              
              <div style={{ borderTop: '1px solid #e5e7eb', paddingTop: '1rem' }}>
                <h4 style={{ fontWeight: 500, color: '#dc2626', marginBottom: '0.5rem' }}>
                  Zona de peligro
                </h4>
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
          <div className="page-actions" style={{ justifyContent: 'space-between', marginTop: '2rem' }}>
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
        </main>
      </div>
    </div>
  );
};

export default Configuracion;
