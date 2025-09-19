import { useState, useEffect } from 'react'

export const useLocalStorage = <T,>(key: string, initialValue: T) => {
  const isBrowser = typeof window !== 'undefined' && typeof window.localStorage !== 'undefined'

  const [state, setState] = useState<T>(() => {
    if (!isBrowser) return initialValue
    try {
      const raw = window.localStorage.getItem(key)
      return raw ? (JSON.parse(raw) as T) : initialValue
    } catch {
      return initialValue
    }
  })

  useEffect(() => {
    if (!isBrowser) return
    try {
      window.localStorage.setItem(key, JSON.stringify(state))
    } catch {
      // ignore quota errors in private mode
    }
  }, [key, state, isBrowser])

  const setLocal = (val: T | ((v: T) => T)) => {
    setState(prev => (typeof val === 'function' ? (val as any)(prev) : val))
  }

  return [state, setLocal] as const
}

export default useLocalStorage
