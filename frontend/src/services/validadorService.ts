// Servicio para gestión de validadores
export interface ValidadorInvitacionRequest {
  email: string;
}

export const inviteValidador = async (email: string): Promise<ValidadorInvitacionRequest> => {
  const res = await fetch('/api/validadores/invitar', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email })
  });
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || 'Error invitando validador');
  }
  return res.json();
};

export const deleteValidador = async (validadorId: string): Promise<string> => {
  const res = await fetch(`/api/validadores/${validadorId}`, {
    method: 'DELETE'
  });
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || 'Error eliminando validador');
  }
  return res.text();
};
