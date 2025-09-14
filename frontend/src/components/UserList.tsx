import { UserResponseDto } from '../types/dtos'

export default function UserList({ usuarios }: { readonly usuarios: UserResponseDto[] }) {
  if (!usuarios.length) return <p>No hay usuarios todavía.</p>
  return (
    <ul>
      {usuarios.map(u => (
        <li key={u.id}>
          <strong>{u.firstName} {u.lastName}</strong> (@{u.username}) — {u.email}
          <span style={{ marginLeft: '10px', fontSize: '0.8em', color: '#666' }}>
            {u.isActive ? 'Activo' : 'Inactivo'} | {u.status}
          </span>
        </li>
      ))}
    </ul>
  )
}
