import { useMemo } from 'react'
import useLocalStorage from './useLocalStorage'

type RBAC = {
	roles: string[]
	hasRole: (r: string) => boolean
	hasPermission: (perm: string) => boolean
}

// Very small RBAC helper: reads roles from localStorage key 'auth_roles' (array)
export const useRBAC = (fallbackRoles: string[] = []) => {
	const [storedRoles] = useLocalStorage<string[]>('auth_roles', fallbackRoles)

	const api = useMemo<RBAC>(() => ({
		roles: storedRoles || fallbackRoles,
		hasRole: (r: string) => (storedRoles || fallbackRoles).includes(r),
		hasPermission: (_perm: string) => true // app-level permission mapping lives in BE; default allow for dev
	}), [storedRoles, fallbackRoles])

	return api
}

export default useRBAC
