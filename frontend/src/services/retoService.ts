// Servicio para permisos y validaciones de retos
export const getRetoPermisos = async (retoId: string) => {
  const res = await fetch(`/api/retos/${retoId}/permisos`);
  if (!res.ok) throw new Error('Error obteniendo permisos');
  return res.json();
};

export const updateRetoPermisos = async (retoId: string, nuevoPermiso: any) => {
  const res = await fetch(`/api/retos/${retoId}/permisos`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(nuevoPermiso)
  });
  if (!res.ok) throw new Error('Error actualizando permisos');
  return res.json();
};

export const validateReto = async (retoId: string, validacion: any) => {
  const res = await fetch(`/api/retos/${retoId}/validar`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(validacion)
  });
  if (!res.ok) throw new Error('Error validando reto');
  return res.json();
};
