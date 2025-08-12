import React from 'react';

const mockPaneles = [
  { id: 'usuarios', nombre: 'Usuarios', descripcion: 'Gestión avanzada de usuarios y roles.' },
  { id: 'logs', nombre: 'Logs', descripcion: 'Auditoría y logs de seguridad.' },
  { id: 'flags', nombre: 'Feature Flags', descripcion: 'Activación de features y experimentos.' },
];

const PanelAdmin: React.FC = () => {
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
