// Servicio para gestión de validadores
export const inviteValidador = async (email: string) => {
  const res = await fetch('/api/validadores/invitar', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email })
  });
  if (!res.ok) throw new Error('Error invitando validador');
  return res.json();
};

export const deleteValidador = async (validadorId: string) => {
  const res = await fetch(`/api/validadores/${validadorId}`, {
    method: 'DELETE'
  });
  if (!res.ok) throw new Error('Error eliminando validador');
  return res.json();
};
