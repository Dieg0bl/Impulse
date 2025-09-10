export type Challenge = { id: string; title: string; description: string }
export type Evidence = { id: string; filename: string; uploadedAt: string }

const challenges: Challenge[] = [
  { id: 'c1', title: '10k steps daily', description: 'Camina 10.000 pasos cada día durante 7 días.' },
  { id: 'c2', title: 'Meditation streak', description: 'Médítalo 10 minutos diarios por 14 días.' },
]

const evidences: Record<string, Evidence[]> = {}

const mockStore = {
  listChallenges: () => Promise.resolve(challenges.slice()),
  getChallenge: (id: string) => challenges.find(c => c.id === id) || null,
  addChallenge: (c: Challenge) => {
    challenges.push(c)
    return c
  },
  addEvidence: (challengeId: string, e: Evidence) => {
    if (!evidences[challengeId]) evidences[challengeId] = []
    evidences[challengeId].push(e)
    return e
  },
  listEvidences: (challengeId: string) => evidences[challengeId] ? Promise.resolve(evidences[challengeId].slice()) : Promise.resolve([]),
}

export default mockStore
