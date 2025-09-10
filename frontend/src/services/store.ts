// State Management para IMPULSE LEAN v1
// Sistema de estado global con React Context + useReducer

import React, { createContext, useContext, useReducer, useEffect, ReactNode } from 'react'
import { authApi, usersApi, challengesApi, User, Challenge, UserStats, UserSubscription, billingApi } from './api'

// =============================================================================
// TYPES
// =============================================================================

export interface AppState {
  // User state
  user: User | null
  userStats: UserStats | null
  subscription: UserSubscription | null
  isAuthenticated: boolean
  isLoading: boolean
  
  // Challenges state
  challenges: Challenge[]
  featuredChallenges: Challenge[]
  userChallenges: Challenge[]
  challengesLoading: boolean
  challengesError: string | null
  
  // UI state
  notifications: Notification[]
  isOnline: boolean
  theme: 'light' | 'dark'
  
  // Error state
  globalError: string | null
}

export interface Notification {
  id: string
  type: 'success' | 'error' | 'warning' | 'info'
  title: string
  message: string
  timestamp: number
  read: boolean
  actions?: Array<{
    label: string
    action: () => void
  }>
}

// =============================================================================
// ACTIONS
// =============================================================================

export type AppAction = 
  // Auth actions
  | { type: 'AUTH_START' }
  | { type: 'AUTH_SUCCESS'; payload: { user: User; subscription?: UserSubscription } }
  | { type: 'AUTH_FAILURE'; payload: string }
  | { type: 'AUTH_LOGOUT' }
  | { type: 'UPDATE_USER'; payload: Partial<User> }
  | { type: 'UPDATE_USER_STATS'; payload: UserStats }
  | { type: 'UPDATE_SUBSCRIPTION'; payload: UserSubscription | null }
  
  // Challenge actions
  | { type: 'CHALLENGES_LOADING' }
  | { type: 'CHALLENGES_SUCCESS'; payload: Challenge[] }
  | { type: 'CHALLENGES_ERROR'; payload: string }
  | { type: 'FEATURED_CHALLENGES_SUCCESS'; payload: Challenge[] }
  | { type: 'USER_CHALLENGES_SUCCESS'; payload: Challenge[] }
  | { type: 'ADD_CHALLENGE'; payload: Challenge }
  | { type: 'UPDATE_CHALLENGE'; payload: Challenge }
  | { type: 'REMOVE_CHALLENGE'; payload: string }
  | { type: 'JOIN_CHALLENGE'; payload: { challengeUuid: string } }
  | { type: 'LEAVE_CHALLENGE'; payload: { challengeUuid: string } }
  
  // UI actions
  | { type: 'ADD_NOTIFICATION'; payload: Omit<Notification, 'id' | 'timestamp'> }
  | { type: 'REMOVE_NOTIFICATION'; payload: string }
  | { type: 'MARK_NOTIFICATION_READ'; payload: string }
  | { type: 'SET_ONLINE_STATUS'; payload: boolean }
  | { type: 'SET_THEME'; payload: 'light' | 'dark' }
  | { type: 'SET_GLOBAL_ERROR'; payload: string | null }
  | { type: 'CLEAR_ERROR' }

// =============================================================================
// REDUCER
// =============================================================================

const initialState: AppState = {
  user: null,
  userStats: null,
  subscription: null,
  isAuthenticated: false,
  isLoading: false,
  
  challenges: [],
  featuredChallenges: [],
  userChallenges: [],
  challengesLoading: false,
  challengesError: null,
  
  notifications: [],
  isOnline: navigator.onLine,
  theme: 'light',
  
  globalError: null
}

function appReducer(state: AppState, action: AppAction): AppState {
  switch (action.type) {
    // Auth cases
    case 'AUTH_START':
      return {
        ...state,
        isLoading: true,
        globalError: null
      }
      
    case 'AUTH_SUCCESS':
      return {
        ...state,
        user: action.payload.user,
        subscription: action.payload.subscription || null,
        isAuthenticated: true,
        isLoading: false,
        globalError: null
      }
      
    case 'AUTH_FAILURE':
      return {
        ...state,
        user: null,
        subscription: null,
        isAuthenticated: false,
        isLoading: false,
        globalError: action.payload
      }
      
    case 'AUTH_LOGOUT':
      return {
        ...initialState,
        isOnline: state.isOnline,
        theme: state.theme
      }
      
    case 'UPDATE_USER':
      return {
        ...state,
        user: state.user ? { ...state.user, ...action.payload } : null
      }
      
    case 'UPDATE_USER_STATS':
      return {
        ...state,
        userStats: action.payload
      }
      
    case 'UPDATE_SUBSCRIPTION':
      return {
        ...state,
        subscription: action.payload
      }
    
    // Challenge cases
    case 'CHALLENGES_LOADING':
      return {
        ...state,
        challengesLoading: true,
        challengesError: null
      }
      
    case 'CHALLENGES_SUCCESS':
      return {
        ...state,
        challenges: action.payload,
        challengesLoading: false,
        challengesError: null
      }
      
    case 'CHALLENGES_ERROR':
      return {
        ...state,
        challengesLoading: false,
        challengesError: action.payload
      }
      
    case 'FEATURED_CHALLENGES_SUCCESS':
      return {
        ...state,
        featuredChallenges: action.payload
      }
      
    case 'USER_CHALLENGES_SUCCESS':
      return {
        ...state,
        userChallenges: action.payload
      }
      
    case 'ADD_CHALLENGE':
      return {
        ...state,
        challenges: [action.payload, ...state.challenges]
      }
      
    case 'UPDATE_CHALLENGE':
      return {
        ...state,
        challenges: state.challenges.map(challenge =>
          challenge.uuid === action.payload.uuid ? action.payload : challenge
        ),
        featuredChallenges: state.featuredChallenges.map(challenge =>
          challenge.uuid === action.payload.uuid ? action.payload : challenge
        ),
        userChallenges: state.userChallenges.map(challenge =>
          challenge.uuid === action.payload.uuid ? action.payload : challenge
        )
      }
      
    case 'REMOVE_CHALLENGE':
      return {
        ...state,
        challenges: state.challenges.filter(challenge => challenge.uuid !== action.payload),
        featuredChallenges: state.featuredChallenges.filter(challenge => challenge.uuid !== action.payload),
        userChallenges: state.userChallenges.filter(challenge => challenge.uuid !== action.payload)
      }
      
    case 'JOIN_CHALLENGE':
      return {
        ...state,
        challenges: state.challenges.map(challenge =>
          challenge.uuid === action.payload.challengeUuid
            ? { ...challenge, isUserParticipating: true, participantCount: challenge.participantCount + 1 }
            : challenge
        )
      }
      
    case 'LEAVE_CHALLENGE':
      return {
        ...state,
        challenges: state.challenges.map(challenge =>
          challenge.uuid === action.payload.challengeUuid
            ? { ...challenge, isUserParticipating: false, participantCount: challenge.participantCount - 1 }
            : challenge
        )
      }
    
    // UI cases
    case 'ADD_NOTIFICATION':
      const notification: Notification = {
        ...action.payload,
        id: Math.random().toString(36).substr(2, 9),
        timestamp: Date.now(),
        read: false
      }
      return {
        ...state,
        notifications: [notification, ...state.notifications]
      }
      
    case 'REMOVE_NOTIFICATION':
      return {
        ...state,
        notifications: state.notifications.filter(n => n.id !== action.payload)
      }
      
    case 'MARK_NOTIFICATION_READ':
      return {
        ...state,
        notifications: state.notifications.map(n =>
          n.id === action.payload ? { ...n, read: true } : n
        )
      }
      
    case 'SET_ONLINE_STATUS':
      return {
        ...state,
        isOnline: action.payload
      }
      
    case 'SET_THEME':
      return {
        ...state,
        theme: action.payload
      }
      
    case 'SET_GLOBAL_ERROR':
      return {
        ...state,
        globalError: action.payload
      }
      
    case 'CLEAR_ERROR':
      return {
        ...state,
        globalError: null,
        challengesError: null
      }
      
    default:
      return state
  }
}

// =============================================================================
// CONTEXT
// =============================================================================

interface AppContextType {
  state: AppState
  dispatch: React.Dispatch<AppAction>
  
  // Auth actions
  login: (email: string, password: string) => Promise<void>
  logout: () => Promise<void>
  register: (userData: any) => Promise<void>
  updateProfile: (profileData: any) => Promise<void>
  
  // Challenge actions
  loadChallenges: (filters?: any) => Promise<void>
  loadFeaturedChallenges: () => Promise<void>
  loadUserChallenges: () => Promise<void>
  createChallenge: (challengeData: any) => Promise<Challenge>
  joinChallenge: (challengeUuid: string) => Promise<void>
  leaveChallenge: (challengeUuid: string) => Promise<void>
  
  // Notification actions
  addNotification: (notification: Omit<Notification, 'id' | 'timestamp'>) => void
  removeNotification: (id: string) => void
  
  // Utility actions
  clearErrors: () => void
}

const AppContext = createContext<AppContextType | undefined>(undefined)

// =============================================================================
// PROVIDER COMPONENT
// =============================================================================

interface AppProviderProps {
  children: ReactNode
}

export function AppProvider({ children }: AppProviderProps) {
  const [state, dispatch] = useReducer(appReducer, initialState)

  // Initialize app on mount
  useEffect(() => {
    initializeApp()
    setupEventListeners()
  }, [])

  // Auto-refresh user stats periodically
  useEffect(() => {
    if (state.isAuthenticated && state.user) {
      const interval = setInterval(() => {
        loadUserStats()
      }, 5 * 60 * 1000) // Every 5 minutes

      return () => clearInterval(interval)
    }
  }, [state.isAuthenticated, state.user])

  // Initialize app
  const initializeApp = async () => {
    try {
      dispatch({ type: 'AUTH_START' })
      
      // Check if user is already authenticated
      const token = localStorage.getItem('auth_token')
      if (token) {
        const user = await authApi.getCurrentUser()
        
        // Load subscription if billing is enabled
        let subscription = null
        try {
          subscription = await billingApi.getCurrentSubscription()
        } catch (error) {
          // Billing might not be enabled or user has no subscription
          console.log('No subscription found or billing disabled')
        }
        
        dispatch({ 
          type: 'AUTH_SUCCESS', 
          payload: { user, subscription } 
        })
        
        // Load initial data
        await Promise.all([
          loadUserStats(),
          loadFeaturedChallenges()
        ])
      } else {
        dispatch({ type: 'AUTH_FAILURE', payload: 'No authentication token found' })
      }
    } catch (error) {
      dispatch({ 
        type: 'AUTH_FAILURE', 
        payload: error instanceof Error ? error.message : 'Failed to initialize app' 
      })
    }
  }

  // Setup event listeners
  const setupEventListeners = () => {
    // Online/offline status
    const handleOnline = () => dispatch({ type: 'SET_ONLINE_STATUS', payload: true })
    const handleOffline = () => dispatch({ type: 'SET_ONLINE_STATUS', payload: false })
    
    window.addEventListener('online', handleOnline)
    window.addEventListener('offline', handleOffline)
    
    // Theme preference
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
    const handleThemeChange = (e: MediaQueryListEvent) => {
      dispatch({ type: 'SET_THEME', payload: e.matches ? 'dark' : 'light' })
    }
    
    mediaQuery.addEventListener('change', handleThemeChange)
    dispatch({ type: 'SET_THEME', payload: mediaQuery.matches ? 'dark' : 'light' })
    
    return () => {
      window.removeEventListener('online', handleOnline)
      window.removeEventListener('offline', handleOffline)
      mediaQuery.removeEventListener('change', handleThemeChange)
    }
  }

  // Auth actions
  const login = async (email: string, password: string) => {
    try {
      dispatch({ type: 'AUTH_START' })
      
      const response = await authApi.login({ email, password })
      
      // Load subscription if billing is enabled
      let subscription = null
      try {
        subscription = await billingApi.getCurrentSubscription()
      } catch (error) {
        // Billing might not be enabled
        console.log('No subscription found or billing disabled')
      }
      
      dispatch({ 
        type: 'AUTH_SUCCESS', 
        payload: { user: response.user, subscription } 
      })
      
      addNotification({
        type: 'success',
        title: 'Welcome back!',
        message: `Successfully logged in as ${response.user.username}`,
        read: false
      })
      
      // Load initial data
      await Promise.all([
        loadUserStats(),
        loadFeaturedChallenges()
      ])
      
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Login failed'
      dispatch({ type: 'AUTH_FAILURE', payload: message })
      
      addNotification({
        type: 'error',
        title: 'Login Failed',
        message,
        read: false
      })
    }
  }

  const logout = async () => {
    try {
      await authApi.logout()
    } catch (error) {
      console.warn('Logout API call failed:', error)
    } finally {
      dispatch({ type: 'AUTH_LOGOUT' })
      addNotification({
        type: 'info',
        title: 'Logged out',
        message: 'You have been successfully logged out',
        read: false
      })
    }
  }

  const register = async (userData: any) => {
    try {
      dispatch({ type: 'AUTH_START' })
      
      await authApi.register(userData)
      
      addNotification({
        type: 'success',
        title: 'Registration Successful',
        message: 'Please check your email to verify your account',
        read: false
      })
      
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Registration failed'
      dispatch({ type: 'AUTH_FAILURE', payload: message })
      
      addNotification({
        type: 'error',
        title: 'Registration Failed',
        message,
        read: false
      })
    }
  }

  const updateProfile = async (profileData: any) => {
    try {
      const updatedUser = await usersApi.updateProfile(profileData)
      dispatch({ type: 'UPDATE_USER', payload: updatedUser })
      
      addNotification({
        type: 'success',
        title: 'Profile Updated',
        message: 'Your profile has been successfully updated',
        read: false
      })
      
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Profile update failed'
      addNotification({
        type: 'error',
        title: 'Update Failed',
        message,
        read: false
      })
    }
  }

  // Data loading actions
  const loadUserStats = async () => {
    if (!state.user) return
    
    try {
      const stats = await usersApi.getStats(state.user.uuid)
      dispatch({ type: 'UPDATE_USER_STATS', payload: stats })
    } catch (error) {
      console.warn('Failed to load user stats:', error)
    }
  }

  const loadChallenges = async (filters: any = {}) => {
    try {
      dispatch({ type: 'CHALLENGES_LOADING' })
      
      const response = await challengesApi.getChallenges(filters)
      dispatch({ type: 'CHALLENGES_SUCCESS', payload: response.content })
      
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Failed to load challenges'
      dispatch({ type: 'CHALLENGES_ERROR', payload: message })
    }
  }

  const loadFeaturedChallenges = async () => {
    try {
      const challenges = await challengesApi.getFeaturedChallenges()
      dispatch({ type: 'FEATURED_CHALLENGES_SUCCESS', payload: challenges })
    } catch (error) {
      console.warn('Failed to load featured challenges:', error)
    }
  }

  const loadUserChallenges = async () => {
    if (!state.user) return
    
    try {
      const response = await challengesApi.getChallenges({ creatorUuid: state.user.uuid })
      dispatch({ type: 'USER_CHALLENGES_SUCCESS', payload: response.content })
    } catch (error) {
      console.warn('Failed to load user challenges:', error)
    }
  }

  const createChallenge = async (challengeData: any): Promise<Challenge> => {
    try {
      const challenge = await challengesApi.createChallenge(challengeData)
      dispatch({ type: 'ADD_CHALLENGE', payload: challenge })
      
      addNotification({
        type: 'success',
        title: 'Challenge Created',
        message: `"${challenge.title}" has been successfully created`,
        read: false
      })
      
      return challenge
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Failed to create challenge'
      addNotification({
        type: 'error',
        title: 'Creation Failed',
        message,
        read: false
      })
      throw error
    }
  }

  const joinChallenge = async (challengeUuid: string) => {
    try {
      await challengesApi.joinChallenge(challengeUuid)
      dispatch({ type: 'JOIN_CHALLENGE', payload: { challengeUuid } })
      
      addNotification({
        type: 'success',
        title: 'Challenge Joined',
        message: 'You have successfully joined the challenge',
        read: false
      })
      
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Failed to join challenge'
      addNotification({
        type: 'error',
        title: 'Join Failed',
        message,
        read: false
      })
    }
  }

  const leaveChallenge = async (challengeUuid: string) => {
    try {
      await challengesApi.leaveChallenge(challengeUuid)
      dispatch({ type: 'LEAVE_CHALLENGE', payload: { challengeUuid } })
      
      addNotification({
        type: 'info',
        title: 'Challenge Left',
        message: 'You have left the challenge',
        read: false
      })
      
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Failed to leave challenge'
      addNotification({
        type: 'error',
        title: 'Leave Failed',
        message,
        read: false
      })
    }
  }

  // Notification actions
  const addNotification = (notification: Omit<Notification, 'id' | 'timestamp'>) => {
    dispatch({ type: 'ADD_NOTIFICATION', payload: notification })
    
    // Auto-remove notification after 5 seconds
    setTimeout(() => {
      removeNotification(notification.id || '')
    }, 5000)
  }

  const removeNotification = (id: string) => {
    dispatch({ type: 'REMOVE_NOTIFICATION', payload: id })
  }

  // Utility actions
  const clearErrors = () => {
    dispatch({ type: 'CLEAR_ERROR' })
  }

  const contextValue: AppContextType = {
    state,
    dispatch,
    login,
    logout,
    register,
    updateProfile,
    loadChallenges,
    loadFeaturedChallenges,
    loadUserChallenges,
    createChallenge,
    joinChallenge,
    leaveChallenge,
    addNotification,
    removeNotification,
    clearErrors
  }

  return (
    <AppContext.Provider value={contextValue}>
      {children}
    </AppContext.Provider>
  )
}

// =============================================================================
// HOOK
// =============================================================================

export function useApp() {
  const context = useContext(AppContext)
  if (context === undefined) {
    throw new Error('useApp must be used within an AppProvider')
  }
  return context
}

// =============================================================================
// UTILITY HOOKS
// =============================================================================

// Hook para notificaciones
export function useNotifications() {
  const { state, removeNotification, addNotification, dispatch } = useApp()
  
  const markAsRead = (id: string) => {
    dispatch({ type: 'MARK_NOTIFICATION_READ', payload: id })
  }
  
  const unreadCount = state.notifications.filter(n => !n.read).length
  
  return {
    notifications: state.notifications,
    unreadCount,
    addNotification,
    removeNotification,
    markAsRead
  }
}

// Hook para estado de autenticación
export function useAuth() {
  const { state, login, logout, register, updateProfile } = useApp()
  
  return {
    user: state.user,
    isAuthenticated: state.isAuthenticated,
    isLoading: state.isLoading,
    userStats: state.userStats,
    subscription: state.subscription,
    login,
    logout,
    register,
    updateProfile
  }
}

// Hook para challenges
export function useChallenges() {
  const { 
    state, 
    loadChallenges, 
    loadFeaturedChallenges, 
    loadUserChallenges,
    createChallenge,
    joinChallenge,
    leaveChallenge 
  } = useApp()
  
  return {
    challenges: state.challenges,
    featuredChallenges: state.featuredChallenges,
    userChallenges: state.userChallenges,
    isLoading: state.challengesLoading,
    error: state.challengesError,
    loadChallenges,
    loadFeaturedChallenges,
    loadUserChallenges,
    createChallenge,
    joinChallenge,
    leaveChallenge
  }
}
