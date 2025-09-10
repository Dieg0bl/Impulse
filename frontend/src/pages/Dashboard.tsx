import React, { useEffect, useState } from 'react'
import { tryListChallenges } from '../services/api'

const Dashboard: React.FC = () => {
  const [challenges, setChallenges] = useState<any[]>([])
  useEffect(() => {
    tryListChallenges().then(setChallenges)
  }, [])

  return (
    <div>
      <h2>Dashboard</h2>
      <button className="btn">Nuevo reto</button>
      <section>
        <h3>Retos activos</h3>
        {challenges.length === 0 ? <p>No hay retos</p> : (
          <ul>
            {challenges.map(c => (
              <li key={c.id}>{c.title} — {c.description}</li>
            ))}
          </ul>
        )}
      </section>
    </div>
  )
}

export default Dashboard
