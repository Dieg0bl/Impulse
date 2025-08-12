import React from 'react';

const mockPaneles = [
  { id: 'flags', nombre: 'Feature Flags', descripcion: 'Activa/desactiva features experimentales.' },
  { id: 'roles', nombre: 'Gestión de Roles', descripcion: 'Administra roles y permisos.' },
  { id: 'metricas', nombre: 'Métricas', descripcion: 'Panel de métricas y stats.' },
];

const Admin: React.FC = () => {
  return (
    <div className="admin-page container mx-auto px-4 py-6">
      <h1 className="text-2xl font-bold mb-4">Panel de Administración</h1>
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

export default Admin;
