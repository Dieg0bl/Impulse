const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080'
const LOGIN_EMAIL = import.meta.env.VITE_DEMO_EMAIL ?? 'admin@impulse.app'
const LOGIN_PASS  = import.meta.env.VITE_DEMO_PASSWORD ?? 'password'

async function ensureToken(): Promise<string> {
  let token = localStorage.getItem('token')
  if (!token) {
    const res = await fetch(`${API_BASE}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: LOGIN_EMAIL, password: LOGIN_PASS })
    })
    if (!res.ok) throw new Error('No se pudo iniciar sesión demo')
    const data = await res.json()
    token = data.token
    localStorage.setItem('token', token)
  }
  return token!
}

export async function getUsuarios() {
  const token = await ensureToken()
  const res = await fetch(`${API_BASE}/api/usuarios`, {
    headers: { Authorization: `Bearer ${token}` }
  })
  if (!res.ok) throw new Error('Error listando usuarios')
  return res.json()
}

export async function crearUsuario(usuario: { nombre: string; email: string }) {
  const token = await ensureToken()
  const res = await fetch(`${API_BASE}/api/usuarios`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    },
    body: JSON.stringify(usuario)
  })
  if (!res.ok) throw new Error('Error creando usuario')
  return res.json()
}
