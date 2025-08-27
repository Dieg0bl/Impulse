import React, { useEffect } from 'react';
import { useAppContext } from '../contexts/AppContext.tsx';

const mockPaneles = [
  { id: 'usuarios', nombre: 'Usuarios', descripcion: 'Gestión avanzada de usuarios y roles.' },
  { id: 'logs', nombre: 'Logs', descripcion: 'Auditoría y logs de seguridad.' },
  { id: 'flags', nombre: 'Feature Flags', descripcion: 'Activación de features y experimentos.' },
];

const PanelAdmin: React.FC = () => {
  const { state, navigate } = useAppContext();
  const { currentUser: user } = state;

  useEffect(() => {
    if (!user) {
      navigate('/login');
      return;
    }
    if (user.rol !== 'ADMIN') {
      navigate('/dashboard');
    } else {
      // Log de auditoría
      import('../utils/logger.ts').then(({ logger }) => {
        logger.info('Acceso al PanelAdmin', 'AUDIT', {
          usuario: user.email,
          fecha: new Date().toISOString()
        });
      });
    }
  }, [user, navigate]);

  return (
    <div className="paneladmin-page container mx-auto px-4 py-6">
      <h1 className="text-2xl font-bold mb-4">Panel de Administración Avanzada</h1>
      <ul className="divide-y">
        {mockPaneles.map(p => (
          <li key={p.id} className="py-4">
            <div className="font-medium">{p.nombre}</div>
            <div className="text-xs text-gray-500">{p.descripcion}</div>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default PanelAdmin;
