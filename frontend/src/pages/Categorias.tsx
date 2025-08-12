import React, { useState } from 'react';
import Button from '../components/Button';

const mockCategorias = [
  { id: '1', nombre: 'Salud', descripcion: 'Retos de salud y bienestar.' },
  { id: '2', nombre: 'Educación', descripcion: 'Retos educativos y de aprendizaje.' },
  { id: '3', nombre: 'Personal', descripcion: 'Retos personales y de desarrollo.' },
];

const Categorias: React.FC = () => {
  const [categorias, setCategorias] = useState(mockCategorias);
  const [nueva, setNueva] = useState(false);
  const [nombre, setNombre] = useState('');
  const [descripcion, setDescripcion] = useState('');

  const crearCategoria = () => {
    setCategorias([
      ...categorias,
      { id: String(Date.now()), nombre, descripcion },
    ]);
    setNueva(false);
    setNombre('');
    setDescripcion('');
  };

  return (
    <div className="categorias-page container mx-auto px-4 py-6">
      <h1 className="text-2xl font-bold mb-4">Gestión de Categorías</h1>
      <Button onClick={() => setNueva(true)}>Nueva categoría</Button>
      {nueva && (
        <div className="my-4 p-4 border rounded">
          <input
            className="block mb-2 border px-2 py-1"
            placeholder="Nombre"
            value={nombre}
            onChange={e => setNombre(e.target.value)}
          />
          <input
            className="block mb-2 border px-2 py-1"
            placeholder="Descripción"
            value={descripcion}
            onChange={e => setDescripcion(e.target.value)}
          />
          <Button onClick={crearCategoria}>Crear</Button>
        </div>
      )}
      <ul className="divide-y mt-6">
        {categorias.map(c => (
          <li key={c.id} className="py-4">
            <div className="font-medium">{c.nombre}</div>
            <div className="text-xs text-gray-500">{c.descripcion}</div>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Categorias;
