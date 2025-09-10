import axios from 'axios'
import mockStore from './mockStore'

const BASE = process.env.REACT_APP_API_BASE || process.env.REACT_APP_API_URL || 'http://localhost:8080'

const http = axios.create({ baseURL: BASE, headers: { 'Content-Type': 'application/json' } })

// attach token if present in localStorage
http.interceptors.request.use(cfg => {
  const token = localStorage.getItem('token')
  if (token) cfg.headers = Object.assign(cfg.headers || {}, { Authorization: `Bearer ${token}` })
  return cfg
})

export async function tryListChallenges() {
  try {
  const res = await http.get('/api/v1/challenges')
  return res.data
  } catch (e) {
  console.warn('tryListChallenges failed, falling back to mock', e)
  return mockStore.listChallenges()
  }
}

export async function tryGetChallenge(id: string) {
  try {
  const res = await http.get(`/api/v1/challenges/${id}`)
  return res.data
  } catch (e) {
  console.warn('tryGetChallenge failed, falling back to mock', e)
  return mockStore.getChallenge(id)
  }
}

export async function tryUploadEvidence(challengeId: string, file: File) {
  try {
    // Try to use backend presigned flow
  const pres = await http.post(`/api/v1/challenges/${challengeId}/evidences/presign`, { filename: file.name })
    if (pres?.data?.uploadUrl) {
      // upload to the presigned URL
      await axios.put(pres.data.uploadUrl, file, { headers: { 'Content-Type': file.type || 'application/octet-stream' } })
      // notify backend
  await http.post(`/api/v1/challenges/${challengeId}/evidences`, { filename: file.name })
      return { id: 'real_' + Date.now(), filename: file.name, uploadedAt: new Date().toISOString() }
    }
  } catch (err) {
    console.warn('presigned upload flow failed, falling back to mock', err)
  }
  // fallback: simulate latency and store locally
  await new Promise(r => setTimeout(r, 600))
  return mockStore.addEvidence(challengeId, { id: 'e_' + Date.now(), filename: file.name, uploadedAt: new Date().toISOString() })
}

export default http
