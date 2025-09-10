import React, { useState } from 'react'
import { tryUploadEvidence } from '../services/api'


const EvidenceUpload: React.FC<{ challengeId: string, onUploaded?: () => void }> = ({ challengeId, onUploaded }) => {
  const [file, setFile] = useState<File | null>(null)
  const [message, setMessage] = useState('')

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!file) return setMessage('Seleccione un archivo')
  setMessage('Subiendo...')
    try {
      // call api wrapper which will fallback to mock
      await tryUploadEvidence(challengeId, file)
      setMessage('Evidencia enviada (mock/real)')
      if (onUploaded) onUploaded()
    } catch (err: any) {
      console.warn('upload error', err)
      setMessage('Error al subir (simulado)')
    } finally {
      setFile(null)
    }
  }

  return (
    <form onSubmit={submit}>
      <input type="file" onChange={e => setFile(e.target?.files?.[0] ?? null)} />
      <div style={{ marginTop: 8 }}>
        <button type="submit">Enviar evidencia</button>
      </div>
      {message && <div style={{ marginTop: 8 }}>{message}</div>}
    </form>
  )
}

export default EvidenceUpload
