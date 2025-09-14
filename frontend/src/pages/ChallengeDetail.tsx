import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import mockStore, { Evidence } from '../services/mockStore'
import EvidenceUpload from './EvidenceUpload'

const ChallengeDetail: React.FC = () => {
  const { id } = useParams()
  const [challenge, setChallenge] = useState<any | null>(null)

  useEffect(() => {
    if (!id) return
    const found = mockStore.getChallenge(id)
    setChallenge(found)
  }, [id])

  const [evidences, setEvidences] = useState<Evidence[]>([])

  const loadEvidences = () => {
    if (!id) return
    mockStore.listEvidences(id).then(setEvidences)
  }

  React.useEffect(() => {
    loadEvidences()
  }, [id])

  if (!challenge) return <div style={{ padding: 20 }}>Reto no encontrado</div>

  return (
    <div style={{ padding: 20 }}>
      <h2>{challenge.title}</h2>
      <p>{challenge.description}</p>
      <h3>Enviar evidencia</h3>
  <EvidenceUpload challengeId={challenge.id} onUploaded={() => loadEvidences()} />
      <h3>Evidencias</h3>
      <ul>
        {evidences.map(ev => (
          <li key={ev.id}>{ev.filename} — {new Date(ev.submittedAt).toLocaleString()}</li>
        ))}
      </ul>
    </div>
  )
}

export default ChallengeDetail
