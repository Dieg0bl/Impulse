// Servicio para permisos y validaciones de retos
export interface PermisoDTO {
  tipo: string;
  valor: boolean;
}

export const getRetoPermisos = async (retoId: string): Promise<PermisoDTO> => {
  const res = await fetch(`/api/retos/${retoId}/permisos`);
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || 'Error obteniendo permisos');
  }
  return res.json();
};

export const updateRetoPermisos = async (retoId: string, nuevoPermiso: PermisoDTO): Promise<PermisoDTO> => {
  const res = await fetch(`/api/retos/${retoId}/permisos`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(nuevoPermiso)
  });
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || 'Error actualizando permisos');
  }
  return res.json();
};

export const validateReto = async (retoId: string, validacion: any): Promise<string> => {
  const res = await fetch(`/api/retos/${retoId}/validar`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(validacion)
  });
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || 'Error validando reto');
  }
  return res.text();
};
