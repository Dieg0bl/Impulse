// usePWA - Hook para gestión de PWA según UI/UX 2025-2026
import { useState, useEffect } from 'react'
import { useRegisterSW } from 'virtual:pwa-register/react'

export interface PWAInstallPrompt {
  prompt: () => Promise<void>
  userChoice: Promise<{ outcome: 'accepted' | 'dismissed' }>
}

export interface UsePWAReturn {
  // Installation
  isInstallable: boolean
  isInstalled: boolean
  showInstallPrompt: () => Promise<void>

  // Updates
  needRefresh: boolean
  updateAvailable: boolean
  updateSW: () => Promise<void>

  // State
  isOffline: boolean

  // Events
  onInstalled?: () => void
  onUpdateReady?: () => void
}

export const usePWA = (): UsePWAReturn => {
  const [installPrompt, setInstallPrompt] = useState<PWAInstallPrompt | null>(null)
  const [isInstalled, setIsInstalled] = useState(false)
  const [isOffline, setIsOffline] = useState(!navigator.onLine)

  // Service Worker registration for updates
  const {
    needRefresh,
    updateServiceWorker,
    offlineReady
  } = useRegisterSW({
    onRegistered(registration) {
      console.log('PWA: Service Worker registered', registration)
    },
    onRegisterError(error) {
      console.error('PWA: Service Worker registration failed', error)
    },
    onNeedRefresh() {
      console.log('PWA: New content available')
    },
    onOfflineReady() {
      console.log('PWA: App ready to work offline')
    }
  })

  // Install prompt handling
  useEffect(() => {
    const handleInstallPrompt = (e: Event) => {
      e.preventDefault()
      setInstallPrompt(e as any)
    }

    const handleAppInstalled = () => {
      setIsInstalled(true)
      setInstallPrompt(null)
      console.log('PWA: App installed')
    }

    window.addEventListener('beforeinstallprompt', handleInstallPrompt)
    window.addEventListener('appinstalled', handleAppInstalled)

    // Check if already installed
    if (window.matchMedia('(display-mode: standalone)').matches) {
      setIsInstalled(true)
    }

    return () => {
      window.removeEventListener('beforeinstallprompt', handleInstallPrompt)
      window.removeEventListener('appinstalled', handleAppInstalled)
    }
  }, [])

  // Network status
  useEffect(() => {
    const handleOnline = () => setIsOffline(false)
    const handleOffline = () => setIsOffline(true)

    window.addEventListener('online', handleOnline)
    window.addEventListener('offline', handleOffline)

    return () => {
      window.removeEventListener('online', handleOnline)
      window.removeEventListener('offline', handleOffline)
    }
  }, [])

  const showInstallPrompt = async (): Promise<void> => {
    if (!installPrompt) {
      throw new Error('Install prompt not available')
    }

    try {
      await installPrompt.prompt()
      const choiceResult = await installPrompt.userChoice

      if (choiceResult.outcome === 'accepted') {
        console.log('PWA: User accepted install prompt')
      } else {
        console.log('PWA: User dismissed install prompt')
      }

      setInstallPrompt(null)
    } catch (error) {
      console.error('PWA: Error showing install prompt', error)
      throw error
    }
  }

  const updateSW = async (): Promise<void> => {
    try {
      await updateServiceWorker(true)
    } catch (error) {
      console.error('PWA: Error updating service worker', error)
      throw error
    }
  }

  return {
    // Installation
    isInstallable: !!installPrompt,
    isInstalled,
    showInstallPrompt,

    // Updates
    needRefresh: Boolean(needRefresh),
    updateAvailable: Boolean(needRefresh),
    updateSW,

    // State
    isOffline,
  }
}

export default usePWA
