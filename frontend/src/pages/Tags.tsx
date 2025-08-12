import React, { useState } from 'react';
import Button from '../components/Button';

const mockTags = [
  { id: '1', nombre: 'productividad' },
  { id: '2', nombre: 'salud' },
  { id: '3', nombre: 'aprendizaje' },
];

const Tags: React.FC = () => {
  const [tags, setTags] = useState(mockTags);
  const [nuevo, setNuevo] = useState(false);
  const [nombre, setNombre] = useState('');

  const crearTag = () => {
    setTags([
      ...tags,
      { id: String(Date.now()), nombre },
    ]);
    setNuevo(false);
    setNombre('');
  };

  return (
    <div className="tags-page container mx-auto px-4 py-6">
      <h1 className="text-2xl font-bold mb-4">Gestión de Etiquetas</h1>
      <Button onClick={() => setNuevo(true)}>Nuevo tag</Button>
      {nuevo && (
        <div className="my-4 p-4 border rounded">
          <input
            className="block mb-2 border px-2 py-1"
            placeholder="Nombre"
            value={nombre}
            onChange={e => setNombre(e.target.value)}
          />
          <Button onClick={crearTag}>Crear</Button>
        </div>
      )}
      <ul className="divide-y mt-6">
        {tags.map(t => (
          <li key={t.id} className="py-4">
            <div className="font-medium">{t.nombre}</div>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Tags;
