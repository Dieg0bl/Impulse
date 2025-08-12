// Build-time static defaults. Runtime overrides fetched from backend merge on top (see FlagsProvider).
export const FLAGS = {
  'plans.pro': true,
  'paywall.second_validator': true,
  'notifications.external': true,
  'public.pages': true,
  'library.templates': true,
  'referrals.enabled': true,
  'connect.enabled': false,
  'dac7.enabled': false,
  'share.wins': true,
  'privacy.kill_switch': true,
  'exp.trial_copy_kill': false,
  'teams.enabled': false,
} as const;

export type FlagKey = keyof typeof FLAGS;

export function isFlagEnabled(key: FlagKey){
  return FLAGS[key];
}
