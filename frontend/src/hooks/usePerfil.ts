import { useQuery, useMutation, useQueryClient } from 'react-query';
import { fetchPerfil, updatePerfil, Perfil } from '../services/perfilService';

export function usePerfil() {
  const queryClient = useQueryClient();
  const { data, error, isLoading } = useQuery<Perfil, Error>('perfil', fetchPerfil);

  const actualizar = useMutation(updatePerfil, {
    onSuccess: () => queryClient.invalidateQueries('perfil'),
  });

  return {
    perfil: data,
    loading: isLoading,
    error,
    actualizar: actualizar.mutateAsync,
  };
}
