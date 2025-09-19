import { useCallback } from 'react'
import { v4 as uuidv4 } from 'uuid'

export const useApi = (baseUrl = '') => {
	const apiCall = useCallback(async function apiCall<T = any>(path: string, options?: RequestInit) : Promise<{ success: boolean; data?: T; status?: number }> {
		const correlation = uuidv4()
		const headers = new Headers(options?.headers || {})
		headers.set('X-Correlation-Id', correlation)
		headers.set('Content-Type', headers.get('Content-Type') || 'application/json')

		try {
			const res = await fetch(baseUrl + path, { ...(options || {}), headers })
			const text = await res.text()
			let data: any = undefined
			try { data = text ? JSON.parse(text) : undefined } catch { data = text }
			return { success: res.ok, data: data as T, status: res.status }
			} catch (err) {
				// preserve error for diagnostics in dev
				// eslint-disable-next-line no-console
				console.debug('apiCall error', err)
				return { success: false, data: undefined, status: 0 }
			}
	}, [baseUrl])

	return { apiCall }
}

export default useApi
