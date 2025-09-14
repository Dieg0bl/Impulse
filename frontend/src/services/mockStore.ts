import {
  ChallengeResponseDto,
  EvidenceResponseDto
} from '../types/dtos'
import {
  ChallengeCategory,
  ChallengeDifficulty,
  ChallengeStatus
} from '../types/enums'

export type Challenge = ChallengeResponseDto
export type Evidence = EvidenceResponseDto

const challenges: Challenge[] = [
  {
    id: 1,
    uuid: 'c1-uuid',
    title: '10k steps daily',
    description: 'Camina 10.000 pasos cada día durante 7 días.',
    category: ChallengeCategory.FITNESS,
    difficulty: ChallengeDifficulty.BEGINNER,
    status: ChallengeStatus.ACTIVE,
    rewardAmount: 50,
    rewardCurrency: 'USD',
    startDate: new Date().toISOString(),
    endDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(),
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    creatorId: 1,
    creatorUsername: 'admin',
    participantCount: 5,
    maxParticipants: 20,
    isPublic: true
  },
  {
    id: 2,
    uuid: 'c2-uuid',
    title: 'Meditation streak',
    description: 'Médítalo 10 minutos diarios por 14 días.',
    category: ChallengeCategory.HEALTH,
    difficulty: ChallengeDifficulty.INTERMEDIATE,
    status: ChallengeStatus.ACTIVE,
    rewardAmount: 75,
    rewardCurrency: 'USD',
    startDate: new Date().toISOString(),
    endDate: new Date(Date.now() + 14 * 24 * 60 * 60 * 1000).toISOString(),
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    creatorId: 1,
    creatorUsername: 'admin',
    participantCount: 3,
    maxParticipants: 15,
    isPublic: true
  },
]

const evidences: Record<string, Evidence[]> = {}

const mockStore = {
  listChallenges: () => Promise.resolve(challenges.slice()),
  getChallenge: (id: string) => challenges.find(c => c.id.toString() === id) || null,
  addChallenge: (c: Challenge) => {
    challenges.push(c)
    return c
  },
  addEvidence: (challengeId: string, e: Evidence) => {
    if (!evidences[challengeId]) evidences[challengeId] = []
    evidences[challengeId].push(e)
    return e
  },
  listEvidences: (challengeId: string) => evidences[challengeId] ? Promise.resolve(evidences[challengeId].slice()) : Promise.resolve([])
}

export default mockStore
