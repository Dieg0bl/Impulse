import React, { useState } from 'react'
import { UserCreateRequestDto } from '../types/dtos'

export default function UserForm({ onCreate }: { onCreate: (u: UserCreateRequestDto) => Promise<void> }) {
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [firstName, setFirstName] = useState('')
  const [lastName, setLastName] = useState('')
  const [password, setPassword] = useState('')
  const [bio, setBio] = useState('')
  const [loading, setLoading] = useState(false)

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    try {
      await onCreate({
        username,
        email,
        firstName,
        lastName,
        password,
        bio: bio || undefined
      })
      setUsername('')
      setEmail('')
      setFirstName('')
      setLastName('')
      setPassword('')
      setBio('')
    } finally { setLoading(false) }
  }

  return (
    <form onSubmit={submit} className="card" style={{marginTop: '1rem'}}>
      <h3 style={{marginTop:0}}>Añadir usuario</h3>
      <label>Username</label>
      <input value={username} onChange={e=>setUsername(e.target.value)} placeholder="Username" required />
      <label>Email</label>
      <input type="email" value={email} onChange={e=>setEmail(e.target.value)} placeholder="email@dominio.com" required />
      <label>Nombre</label>
      <input value={firstName} onChange={e=>setFirstName(e.target.value)} placeholder="Nombre" required />
      <label>Apellido</label>
      <input value={lastName} onChange={e=>setLastName(e.target.value)} placeholder="Apellido" required />
      <label>Contraseña</label>
      <input type="password" value={password} onChange={e=>setPassword(e.target.value)} placeholder="Contraseña" required />
      <label>Bio (opcional)</label>
      <textarea value={bio} onChange={e=>setBio(e.target.value)} placeholder="Descripción del usuario" />
      <div style={{marginTop:'.75rem', display:'flex', gap:'.5rem'}}>
        <button className="primary" disabled={loading} type="submit">{loading? 'Creando…':'Crear'}</button>
        <button className="ghost" type="button" onClick={()=>{
          setUsername('');
          setEmail('');
          setFirstName('');
          setLastName('');
          setPassword('');
          setBio('')
        }}>Limpiar</button>
      </div>
    </form>
  )
}
