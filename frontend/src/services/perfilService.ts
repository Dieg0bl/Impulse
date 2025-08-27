export interface Consentimientos {
  marketing: boolean;
  cookies: boolean;
  analytics: boolean;
  comunicaciones: boolean;
  fechaAceptacion: string;
}

export interface Perfil {
  id: number;
  nombre: string;
  apellidos?: string;
  email: string;
  fotoUrl?: string;
  consentimientos?: Consentimientos;
  estado?: string;
}

export async function fetchPerfil(): Promise<Perfil> {
  const res = await fetch('/api/usuarios/perfil');
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || 'Error obteniendo perfil');
  }
  return res.json();
}

export async function updatePerfil(perfil: Partial<Perfil>): Promise<Perfil> {
  const res = await fetch('/api/usuarios/perfil', {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(perfil),
  });
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || 'Error actualizando perfil');
  }
  return res.json();
}
