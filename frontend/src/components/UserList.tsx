import React from 'react'

type User = { id: number; nombre: string; email: string }

export default function UserList({ usuarios }: { usuarios: User[] }) {
  if (!usuarios.length) return <p>No hay usuarios todavía.</p>
  return (
    <ul>
      {usuarios.map(u => (
        <li key={u.id}><strong>{u.nombre}</strong> — {u.email}</li>
      ))}
    </ul>
  )
}
