import { useQuery, useMutation, useQueryClient } from 'react-query';

export interface Validador {
  id: number;
  nombre: string;
  email: string;
  estado: string;
  confianza: number;
  validacionesRealizadas: number;
}

const fetchValidadores = async (): Promise<Validador[]> => {
  const res = await fetch('/api/validadores');
  if (!res.ok) throw new Error('Error obteniendo validadores');
  return res.json();
};

const invitarValidador = async (email: string): Promise<Validador> => {
  const res = await fetch('/api/validadores/invitar', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email }),
  });
  if (!res.ok) throw new Error('Error invitando validador');
  return res.json();
};

const eliminarValidador = async (id: number): Promise<void> => {
  const res = await fetch(`/api/validadores/${id}`, { method: 'DELETE' });
  if (!res.ok) throw new Error('Error eliminando validador');
};

export function useValidadores() {
  const queryClient = useQueryClient();
  const { data, error, isLoading } = useQuery<Validador[], Error>('validadores', fetchValidadores);

  const invitar = useMutation(invitarValidador, {
    onSuccess: () => queryClient.invalidateQueries('validadores'),
  });

  const eliminar = useMutation(eliminarValidador, {
    onSuccess: () => queryClient.invalidateQueries('validadores'),
  });

  return {
    validadores: data || [],
    loading: isLoading,
    error,
    invitar: invitar.mutateAsync,
    eliminar: eliminar.mutateAsync,
  };
}
