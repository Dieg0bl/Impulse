// Remote Freemium API client with fallback to local static data
import { SUBSCRIPTION_TIERS, UPGRADE_PATHS, UPGRADE_EXPERIENCES, SubscriptionTier, UpgradePath, UpgradeExperience } from './FreemiumService'

const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080'
const FREEMIUM_FALLBACK = import.meta.env.VITE_FREEMIUM_FALLBACK === 'true'

async function fetchJson<T>(url: string, signal?: AbortSignal): Promise<T> {
  const res = await fetch(url, { signal })
  if (!res.ok) throw new Error(`Freemium API error: ${res.status}`)
  return res.json() as Promise<T>
}

export interface SubscriptionStatusDTO {
  currentTier: string
  trialActive: boolean
  trialDaysRemaining: number
  subscriptionStatus: string
  recommendedUpgrade?: string | null
}

export const FreemiumApiClient = {
  async getTiers(signal?: AbortSignal): Promise<SubscriptionTier[]> {
    if (FREEMIUM_FALLBACK) return SUBSCRIPTION_TIERS
    try {
      const data = await fetchJson<SubscriptionTier[]>(`${API_BASE}/api/freemium/tiers`, signal)
      return data
    } catch (e) {
      if (!FREEMIUM_FALLBACK) console.warn('Freemium tiers fallback', e)
      return SUBSCRIPTION_TIERS
    }
  },
  async getUpgradePaths(signal?: AbortSignal): Promise<UpgradePath[]> {
    if (FREEMIUM_FALLBACK) return UPGRADE_PATHS
    try {
      const data = await fetchJson<UpgradePath[]>(`${API_BASE}/api/freemium/upgrade-paths`, signal)
      return data
    } catch (e) {
      if (!FREEMIUM_FALLBACK) console.warn('Freemium upgrade paths fallback', e)
      return UPGRADE_PATHS
    }
  },
  async getUpgradeExperiences(signal?: AbortSignal): Promise<UpgradeExperience[]> {
    if (FREEMIUM_FALLBACK) return UPGRADE_EXPERIENCES
    try {
      const data = await fetchJson<UpgradeExperience[]>(`${API_BASE}/api/freemium/upgrade-experiences`, signal)
      return data
    } catch (e) {
      if (!FREEMIUM_FALLBACK) console.warn('Freemium upgrade experiences fallback', e)
      return UPGRADE_EXPERIENCES
    }
  },
  async getSubscriptionStatus(userId: string, signal?: AbortSignal): Promise<SubscriptionStatusDTO | null> {
    try {
      return await fetchJson<SubscriptionStatusDTO>(`${API_BASE}/api/freemium/subscription-status?userId=${encodeURIComponent(userId)}`, signal)
    } catch (e) {
      console.warn('Freemium subscription status unavailable', e)
      return null
    }
  },
  async upgrade(userId: string, targetTier: string): Promise<boolean> {
    try {
      const res = await fetch(`${API_BASE}/api/freemium/upgrade?userId=${encodeURIComponent(userId)}&targetTier=${encodeURIComponent(targetTier)}`, { method: 'POST' })
      return res.ok && await res.json()
    } catch (e) {
      console.warn('Upgrade failed', e)
      return false
    }
  },
  async startTrial(userId: string, targetTier: string): Promise<boolean> {
    try {
      const res = await fetch(`${API_BASE}/api/freemium/trial?userId=${encodeURIComponent(userId)}&targetTier=${encodeURIComponent(targetTier)}`, { method: 'POST' })
      return res.ok && await res.json()
    } catch (e) {
      console.warn('Start trial failed', e)
      return false
    }
  }
}

export default FreemiumApiClient
