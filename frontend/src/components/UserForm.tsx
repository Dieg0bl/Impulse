import React, { useState } from 'react'

export default function UserForm({ onCreate }: { onCreate: (u: {nombre: string; email: string}) => Promise<void> }) {
  const [nombre, setNombre] = useState('')
  const [email, setEmail] = useState('')
  const [loading, setLoading] = useState(false)

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    try {
      await onCreate({ nombre, email })
      setNombre('')
      setEmail('')
    } finally { setLoading(false) }
  }

  return (
    <form onSubmit={submit} className="card" style={{marginTop: '1rem'}}>
      <h3 style={{marginTop:0}}>Añadir usuario</h3>
      <label>Nombre</label>
      <input value={nombre} onChange={e=>setNombre(e.target.value)} placeholder="Nombre" required />
      <label>Email</label>
      <input type="email" value={email} onChange={e=>setEmail(e.target.value)} placeholder="email@dominio.com" required />
      <div style={{marginTop:'.75rem', display:'flex', gap:'.5rem'}}>
        <button className="primary" disabled={loading} type="submit">{loading? 'Creando…':'Crear'}</button>
        <button className="ghost" type="button" onClick={()=>{setNombre(''); setEmail('')}}>Limpiar</button>
      </div>
    </form>
  )
}
