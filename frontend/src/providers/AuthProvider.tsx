import React, { createContext, useContext, useEffect, useState } from 'react'
import { UserResponseDto, LoginRequestDto, RegisterRequestDto } from '../types/dtos'

type AuthContextType = {
  user: UserResponseDto | null
  token: string | null
  login: (email: string, password?: string) => Promise<void>
  logout: () => void
  register: (name: string, email: string, password?: string) => Promise<void>
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export const useAuth = () => {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<UserResponseDto | null>(() => {
    const raw = localStorage.getItem('impulse_user')
    return raw ? JSON.parse(raw) as UserResponseDto : null
  })
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('token'))

  useEffect(() => {
    if (user) localStorage.setItem('impulse_user', JSON.stringify(user))
    else localStorage.removeItem('impulse_user')
  }, [user])

  useEffect(() => {
    if (token) localStorage.setItem('token', token)
    else localStorage.removeItem('token')
  }, [token])

  const login = async (email: string, password: string = 'password') => {
    // call backend auth
    const res = await fetch(`${import.meta.env.VITE_API_BASE || 'http://localhost:8080'}/api/v1/auth/login`, {
      method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ email, password })
    })
    const data = await res.json()
    if (!res.ok) throw new Error(data?.message || 'Login failed')
    const tokenResp = data?.data?.token || data?.token
    const userResp = data?.data?.user || data?.user
    setToken(tokenResp)
    setUser(userResp)
  }

  const register = async (name: string, email: string, password: string = 'password') => {
    const res = await fetch(`${import.meta.env.VITE_API_BASE || 'http://localhost:8080'}/api/v1/auth/register`, {
      method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({
        username: name,
        email,
        password,
        firstName: name.split(' ')[0] || name,
        lastName: name.split(' ').slice(1).join(' ') || ''
      })
    })
    const data = await res.json()
    if (!res.ok) throw new Error(data?.message || 'Registration failed')
    const tokenResp = data?.data?.token || data?.token
    const userResp = data?.data?.user || data?.user
    setToken(tokenResp)
    setUser(userResp)
  }

  const logout = () => {
    setUser(null)
    setToken(null)
  }

  const value = React.useMemo(() => ({ user, token, login, logout, register }), [user, token])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export default AuthProvider
