import React from 'react';

const AccessDenied: React.FC = () => {
  return (
    <div className="access-denied container-app p-8 text-center">
      <h1 className="text-2xl font-bold">Acceso denegado</h1>
      <p className="mt-2 text-sm">No tienes permisos para ver esta página. Contacta al administrador si crees que es un error.</p>
    </div>
  );
};

export default AccessDenied;
