import React from 'react'
import { useAuth } from '../providers/AuthProvider'
import mockStore, { Challenge } from '../services/mockStore'

const Account: React.FC = () => {
  const { user, logout } = useAuth()

  return (
    <div style={{ padding: 20 }}>
      <h2>Cuenta</h2>
      {user ? (
        <div>
          <p>Nombre: {user.name}</p>
          <p>Email: {user.email}</p>
          <div style={{ marginTop: 8 }}>
            <button onClick={logout}>Cerrar sesión</button>
            <button style={{ marginLeft: 8 }} onClick={() => {
              const data: { user: any; challenges: Challenge[] } = { user, challenges: [] }
              // for demo, include all challenges
              mockStore.listChallenges().then((ch: any[]) => {
                data.challenges = ch
                const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' })
                const url = URL.createObjectURL(blob)
                const a = document.createElement('a')
                a.href = url
                a.download = 'impulse-data.json'
                a.click()
              })
            }}>Exportar datos (mock)</button>
            <button style={{ marginLeft: 8 }} onClick={() => {
              if (confirm('Borrar cuenta (simulado)?')) {
                // clear auth and any local data
                logout()
                alert('Cuenta borrada (simulado)')
              }
            }}>Borrar cuenta</button>
          </div>
        </div>
      ) : (
        <p>No has iniciado sesión.</p>
      )}
    </div>
  )
}

export default Account
