import React, { useState } from 'react';
import { useAppContext } from '../contexts/AppContext.tsx';
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
    frecuenciaRecordatorios: 'diario',
  });

  const [guardando, setGuardando] = useState(false);

  const handleToggle = (campo: 'notificaciones') => {
    setConfig((prev) => ({ ...prev, [campo]: !prev[campo] }));
  };

  const handleSelectChange =
    <K extends keyof Omit<ConfiguracionForm, 'notificaciones'>>(campo: K) =>
    (value: ConfiguracionForm[K]) => {
      setConfig((prev) => ({ ...prev, [campo]: value }));
    };

  const handleGuardar = async () => {
    setGuardando(true);
    try {
      logger.info('Guardando configuración', 'Configuracion', config);

      const res = await fetch('/api/user/preferences', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(config),
        credentials: 'include',
      });

      if (!res.ok) throw new Error('Error guardando configuración');

      localStorage.setItem('userPreferences', JSON.stringify(config));

      alert('Configuración guardada con éxito');
    } catch (error) {
      logger.error('Error al guardar configuración', 'Configuracion', error);
      alert('Hubo un error al guardar.');
    } finally {
      setGuardando(false);
    }
  };

  const handleExportarDatos = async () => {
    try {
      const service = await import('../services/privacyService');
      await service.createPrivacyRequest('EXPORT');
      alert('Solicitud de exportación enviada.');
    } catch {
      alert('Error al exportar datos.');
    }
  };

  const handleEliminarCuenta = async () => {
    if (!window.confirm('¿Seguro que quieres eliminar tu cuenta?')) return;
    try {
      const service = await import('../services/privacyService');
      await service.createPrivacyRequest('DELETE');
      alert('Solicitud de eliminación enviada.');
    } catch {
      alert('Error al eliminar cuenta.');
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
            {/* Notificaciones */}
            <div className="form-section">
              <h3>🔔 Notificaciones</h3>
              <div className="space-y-4">
                <div className="flex items-center justify-between">
                  <div>
                    <label className="font-medium">Recibir notificaciones</label>
                    <p className="text-sm text-gray-600">
                      Activa para recibir alertas sobre tus retos
                    </p>
                  </div>
                  <input
                    type="checkbox"
                    checked={config.notificaciones}
                    onChange={() => handleToggle('notificaciones')}
                    className="w-5 h-5"
                  />
                </div>

                <div>
                  <label className="block font-medium mb-2">Frecuencia de recordatorios</label>
                  <select
                    value={config.frecuenciaRecordatorios}
                    onChange={(e) =>
                      handleSelectChange('frecuenciaRecordatorios')(
                        e.target.value as ConfiguracionForm['frecuenciaRecordatorios']
                      )
                    }
                    className="w-full p-3 border rounded-lg"
                  >
                    <option value="diario">Diario</option>
                    <option value="semanal">Semanal</option>
                    <option value="nunca">Nunca</option>
                  </select>
                </div>
              </div>
            </div>

            {/* Privacidad */}
            <div className="bg-white p-6 rounded-lg border">
              <h2 className="text-lg font-semibold mb-4">Privacidad</h2>
              <label className="block font-medium mb-2">Nivel de privacidad</label>
              <select
                value={config.privacidad}
                onChange={(e) =>
                  handleSelectChange('privacidad')(
                    e.target.value as ConfiguracionForm['privacidad']
                  )
                }
                className="w-full p-3 border rounded-lg"
              >
                <option value="PUBLICO">Público</option>
                <option value="SOLO_VALIDADORES">Solo validadores</option>
                <option value="PRIVADO">Privado</option>
              </select>
            </div>

            {/* Derechos */}
            <div className="bg-white p-6 rounded-lg border mt-6">
              <h2 className="text-lg font-semibold mb-4">Tus derechos y privacidad</h2>
              <a
                href="/privacidad"
                target="_blank"
                rel="noopener noreferrer"
                style={{ fontSize: '0.9em' }}
              >
                Ver política de privacidad
              </a>
              <div className="flex flex-col gap-2 mt-4">
                <button className="btn btn-outline" onClick={handleExportarDatos}>
                  Solicitar exportación de mis datos
                </button>
                <button
                  className="btn btn-outline btn-danger"
                  onClick={handleEliminarCuenta}
                >
                  Solicitar eliminación de mi cuenta
                </button>
              </div>
            </div>

            {/* Apariencia */}
            <div className="bg-white p-6 rounded-lg border">
              <h2 className="text-lg font-semibold mb-4">Apariencia</h2>
              <div className="space-y-4">
                <div>
                  <label className="block font-medium mb-2">Idioma</label>
                  <select
                    value={config.idioma}
                    onChange={(e) =>
                      handleSelectChange('idioma')(e.target.value as ConfiguracionForm['idioma'])
                    }
                    className="w-full p-3 border rounded-lg"
                  >
                    <option value="es">Español</option>
                    <option value="en">English</option>
                  </select>
                </div>
                <div>
                  <label className="block font-medium mb-2">Tema</label>
                  <select
                    value={config.tema}
                    onChange={(e) =>
                      handleSelectChange('tema')(e.target.value as ConfiguracionForm['tema'])
                    }
                    className="w-full p-3 border rounded-lg"
                  >
                    <option value="claro">Claro</option>
                    <option value="oscuro">Oscuro</option>
                  </select>
                </div>
              </div>
            </div>

            {/* Datos */}
            <div className="bg-white p-6 rounded-lg border">
              <h2 className="text-lg font-semibold mb-4">Mis Datos</h2>
              <Button variant="secondary" onClick={handleExportarDatos}>
                Exportar mis datos
              </Button>
              <div style={{ borderTop: '1px solid #e5e7eb', paddingTop: '1rem' }}>
                <h4 style={{ fontWeight: 500, color: '#dc2626' }}>Zona de peligro</h4>
                <Button variant="danger" onClick={handleEliminarCuenta}>
                  Eliminar cuenta permanentemente
                </Button>
              </div>
            </div>
          </div>

          {/* Acciones */}
          <div className="page-actions" style={{ justifyContent: 'space-between', marginTop: '2rem' }}>
            <Button variant="secondary" onClick={() => navigate('dashboard')}>
              Cancelar
            </Button>
            <Button onClick={handleGuardar} disabled={guardando}>
              {guardando ? 'Guardando...' : 'Guardar cambios'}
            </Button>
          </div>
        </main>
      </div>
    </div>
  );
};

export default Configuracion;
