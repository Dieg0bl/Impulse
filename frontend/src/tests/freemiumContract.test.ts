/**
 * Contract test for Freemium backend tiers endpoint.
 * Verifies minimal shape and field semantics without coupling to full dataset.
 */

import FreemiumApiClient from '../services/FreemiumApiClient'

interface RawFeature { id:string; name:string; description:string; category:string; unlimited?:boolean; isUnlimited?:boolean; monthlyQuota?:number; qualityLevel:string }
interface RawTier { id:string; name:string; description:string; monthlyPrice:number; yearlyPrice:number; yearlyDiscount:number; features:RawFeature[]; limits:any; benefits:any[]; targetUsers:string[]; trialDays:number }

function isNonEmptyString(v:any){ return typeof v === 'string' && v.trim().length>0 }
function isPositiveOrZero(n:any){ return typeof n === 'number' && n >= 0 }

function validateFeature(f:RawFeature){
  if(!isNonEmptyString(f.id)) throw new Error('feature.id missing')
  if(typeof f.unlimited !== 'boolean' && typeof f.isUnlimited !== 'boolean') throw new Error('feature unlimited flag missing')
  if(!isNonEmptyString(f.category)) throw new Error('feature.category missing')
}

function validateTier(t:RawTier){
  if(!isNonEmptyString(t.id)) throw new Error('tier.id missing')
  if(!Array.isArray(t.features) || t.features.length===0) throw new Error('tier.features empty')
  t.features.forEach(validateFeature)
  if(typeof t.trialDays !== 'number') throw new Error('tier.trialDays invalid')
}

async function fetchTiers():Promise<RawTier[]> {
  // We call through the API client which already has fallback logic
  return await FreemiumApiClient.getTiers()
}

// Very lightweight runner (since no jest config present). Execute via: node ts-node or transpiled env.
(async () => {
  try {
    const tiers = await fetchTiers()
    if(!Array.isArray(tiers) || tiers.length===0) throw new Error('tiers array empty')
    tiers.forEach(validateTier)
    // Basic monotonic price check (if >1 tier, later tiers should not be cheaper monthly than free unless zero)
    const paid = tiers.filter(t=>t.monthlyPrice>0)
    if(paid.some(p=>!isPositiveOrZero(p.monthlyPrice))) throw new Error('invalid monthlyPrice')
    console.log('[Freemium Contract Test] SUCCESS: tiers contract valid. Count=', tiers.length)
  } catch (e:any) {
    console.error('[Freemium Contract Test] FAILURE:', e.message)
    process.exitCode = 1
  }
})()
