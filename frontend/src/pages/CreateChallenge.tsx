import React, { useState } from 'react'
import mockStore from '../services/mockStore'
import { useNavigate } from 'react-router-dom'

const CreateChallenge: React.FC = () => {
  const [title, setTitle] = useState('')
  const [desc, setDesc] = useState('')
  const navigate = useNavigate()

  const submit = (e: React.FormEvent) => {
    e.preventDefault()
    if (!title) return alert('Título requerido')
    const c = { id: 'c_' + Date.now(), title, description: desc }
    mockStore.addChallenge(c)
    navigate(`/challenges/${c.id}`)
  }

  return (
    <div style={{ padding: 20 }}>
      <h2>Crear reto</h2>
      <form onSubmit={submit}>
        <div style={{ marginBottom: 8 }}>
          <label htmlFor="title">Título</label>
          <input id="title" aria-label="Título" value={title} onChange={e => setTitle(e.target.value)} maxLength={120} />
        </div>
        <div style={{ marginBottom: 8 }}>
          <label htmlFor="desc">Descripción</label>
          <textarea id="desc" aria-label="Descripción" value={desc} onChange={e => setDesc(e.target.value)} maxLength={10000} />
        </div>
        <div style={{ marginTop: 8 }}>
          <button type="submit">Crear</button>
        </div>
      </form>
    </div>
  )
}

export default CreateChallenge
