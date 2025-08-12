import React, { useState } from 'react';
import Button from '../components/Button';

const mockKeys = [
  { id: '1', nombre: 'Integración Zapier', creada: '2025-08-10', scope: 'read:retos', activa: true },
  { id: '2', nombre: 'App móvil', creada: '2025-08-12', scope: 'full', activa: false },
];

const ApiKeys: React.FC = () => {
  const [keys, setKeys] = useState(mockKeys);
  const [nueva, setNueva] = useState(false);
  const [nombre, setNombre] = useState('');
  const [scope, setScope] = useState('read:retos');

  const crearKey = () => {
    setKeys([
      ...keys,
      { id: String(Date.now()), nombre, creada: new Date().toISOString(), scope, activa: true },
    ]);
    setNueva(false);
    setNombre('');
    setScope('read:retos');
  };

  const revocarKey = (id: string) => {
    setKeys(keys.map(k => k.id === id ? { ...k, activa: false } : k));
  };

  return (
    <div className="apikeys-page container mx-auto px-4 py-6">
      <h1 className="text-2xl font-bold mb-4">API Keys</h1>
      <Button onClick={() => setNueva(true)}>Nueva API Key</Button>
      {nueva && (
        <div className="my-4 p-4 border rounded">
          <input
            className="block mb-2 border px-2 py-1"
            placeholder="Nombre"
            value={nombre}
            onChange={e => setNombre(e.target.value)}
          />
          <select className="block mb-2 border px-2 py-1" value={scope} onChange={e => setScope(e.target.value)}>
            <option value="read:retos">Solo lectura</option>
            <option value="full">Acceso completo</option>
          </select>
          <Button onClick={crearKey}>Crear</Button>
        </div>
      )}
      <ul className="divide-y mt-6">
        {keys.map(k => (
          <li key={k.id} className="py-4 flex justify-between items-center">
            <div>
              <div className="font-medium">{k.nombre}</div>
              <div className="text-xs text-gray-500">{new Date(k.creada).toLocaleString()} — {k.scope}</div>
            </div>
            <span className={`px-2 py-1 rounded text-xs font-medium ${k.activa ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>{k.activa ? 'Activa' : 'Revocada'}</span>
            {k.activa && <Button size="small" onClick={() => revocarKey(k.id)}>Revocar</Button>}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ApiKeys;
