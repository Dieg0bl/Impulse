import React, { createContext, useContext, useMemo, useState, useCallback } from 'react'

export type User = { id?: number; username?: string; email?: string; firstName?: string; lastName?: string; bio?: string; phone?: string; location?: string; website?: string; isVerified?: boolean }

type AuthContextShape = {
  user?: User | null
  roles: string[]
  setUser: (u?: User | null) => void
  setRoles: (r: string[]) => void
  updateProfile: (changes: any) => Promise<void>
  signOut: () => void
}

const AuthContext = createContext<AuthContextShape>({ user: null, roles: [], setUser: () => {}, setRoles: () => {}, updateProfile: async () => {}, signOut: () => {} })

export const useAuth = () => useContext(AuthContext)

export const AuthProvider: React.FC<{ children: React.ReactNode; initialUser?: User | null; initialRoles?: string[] }> = ({ children, initialUser = null, initialRoles = [] }) => {
  const [user, setUser] = useState<User | null | undefined>(initialUser)
  const [roles, setRoles] = useState<string[]>(initialRoles)

  const updateProfile = useCallback(async (changes: any) => {
    // Accept either a plain Partial<User> or an API wrapper { success, data }
    let payload = changes
    if (changes && typeof changes === 'object' && ('success' in changes || 'data' in changes)) {
      payload = changes.data ?? changes
    }
    setUser(prev => ({ ...(prev as any || {}), ...(payload || {}) }))
  }, [])

  const signOut = useCallback(() => {
    setUser(null)
    setRoles([])
    try { localStorage.removeItem('auth_roles'); localStorage.removeItem('auth_user') } catch {}
  }, [])

  const setUserWrapped = useCallback((u?: User | null) => {
    setUser(u)
    try { localStorage.setItem('auth_user', JSON.stringify(u)) } catch {}
  }, [])

  const setRolesWrapped = useCallback((r: string[]) => {
    setRoles(r)
    try { localStorage.setItem('auth_roles', JSON.stringify(r)) } catch {}
  }, [])

  const value = useMemo(() => ({ user, roles, setUser: setUserWrapped, setRoles: setRolesWrapped, updateProfile, signOut }), [user, roles, setUserWrapped, setRolesWrapped, updateProfile, signOut])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export default useAuth
