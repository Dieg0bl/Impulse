// Build-time static defaults. Runtime overrides fetched from backend merge on top (see FlagsProvider).
export const FLAGS = {
  'pmf.surveys': true,
  'activation.quickwin': true,
  'activation.onboarding': true,
  'activation.habit': true,
  'experiments.core': true,
  'growth.viral_share': true,
  'growth.referrals': true,
  'monetization.paywall': true,
  'lifecycle.guardrails': true,
  'support.csat': true,
  'support.nps': true,
  'press.live_counter': true,
  'economics.nsm': true,
  'economics.counters': true,
  'economics.advanced': true,
  'anti_fraud.referrals': true,
  'launch.banner': false,
  'validators.enabled': true,
  'validators.metrics': true,
  'communication.email': true,
} as const;

export type FlagKey = keyof typeof FLAGS;

export function isFlagEnabled(key: FlagKey){
  return FLAGS[key];
}
