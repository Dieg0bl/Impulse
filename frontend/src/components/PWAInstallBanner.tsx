// PWAInstallBanner - Banner de instalación PWA según UI/UX 2025-2026
import React, { useState } from 'react'
import { Box, Typography, IconButton, Slide } from '@mui/material'
import { AppButton } from '../ui/AppButton'
import { GlassSurface } from '../ui/GlassSurface'
import { Download, X, Smartphone, Monitor, Tablet } from 'lucide-react'
import { usePWA } from '../hooks/usePWA'

export interface PWAInstallBannerProps {
  onInstalled?: () => void
  className?: string
}

export const PWAInstallBanner: React.FC<PWAInstallBannerProps> = ({
  onInstalled,
  className
}) => {
  const { isInstallable, isInstalled, showInstallPrompt } = usePWA()
  const [dismissed, setDismissed] = useState(false)
  const [installing, setInstalling] = useState(false)

  // Don't show if not installable, already installed, or dismissed
  if (!isInstallable || isInstalled || dismissed) {
    return null
  }

  const handleInstall = async () => {
    setInstalling(true)
    try {
      await showInstallPrompt()
      onInstalled?.()
    } catch (error) {
      console.error('Error installing PWA:', error)
    } finally {
      setInstalling(false)
    }
  }

  const handleDismiss = () => {
    setDismissed(true)
  }

  const getDeviceIcon = () => {
    const width = window.innerWidth
    if (width < 768) return <Smartphone size={20} />
    if (width < 1024) return <Tablet size={20} />
    return <Monitor size={20} />
  }

  const getInstallText = () => {
    const width = window.innerWidth
    if (width < 768) return 'Instalar en tu móvil'
    if (width < 1024) return 'Instalar en tu tablet'
    return 'Instalar en tu escritorio'
  }

  return (
    <Slide direction="up" in={true} mountOnEnter unmountOnExit>
      <Box
        className={className}
        sx={{
          position: 'fixed',
          bottom: 16,
          left: 16,
          right: 16,
          zIndex: 1300,
          maxWidth: 400,
          mx: 'auto'
        }}
      >
        <GlassSurface
          elevation={2}
          sx={{
            p: 2,
            background: 'linear-gradient(135deg, rgb(var(--grad-start) / 0.95) 0%, rgb(var(--grad-end) / 0.95) 100%)',
            color: 'white',
            position: 'relative'
          }}
        >
          <IconButton
            onClick={handleDismiss}
            size="small"
            sx={{
              position: 'absolute',
              top: 8,
              right: 8,
              color: 'white',
              '&:hover': {
                backgroundColor: 'rgba(255, 255, 255, 0.1)'
              }
            }}
          >
            <X size={16} />
          </IconButton>

          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
            {getDeviceIcon()}
            <Box sx={{ flex: 1 }}>
              <Typography variant="subtitle1" sx={{ fontWeight: 600, color: 'white' }}>
                {getInstallText()}
              </Typography>
              <Typography variant="body2" sx={{ color: 'rgba(255, 255, 255, 0.9)', fontSize: '0.875rem' }}>
                Acceso rápido, notificaciones y funciona sin conexión
              </Typography>
            </Box>
          </Box>

          <Box sx={{ display: 'flex', gap: 1 }}>
            <AppButton
              variant="contained"
              size="compact"
              onClick={handleInstall}
              loading={installing}
              disabled={installing}
              icon={<Download size={16} />}
              sx={{
                backgroundColor: 'rgba(255, 255, 255, 0.2)',
                color: 'white',
                border: '1px solid rgba(255, 255, 255, 0.3)',
                '&:hover': {
                  backgroundColor: 'rgba(255, 255, 255, 0.3)',
                  borderColor: 'rgba(255, 255, 255, 0.5)',
                },
                '&:disabled': {
                  backgroundColor: 'rgba(255, 255, 255, 0.1)',
                  color: 'rgba(255, 255, 255, 0.7)',
                }
              }}
            >
              Instalar
            </AppButton>

            <AppButton
              variant="text"
              size="compact"
              onClick={handleDismiss}
              sx={{
                color: 'rgba(255, 255, 255, 0.8)',
                '&:hover': {
                  color: 'white',
                  backgroundColor: 'rgba(255, 255, 255, 0.1)',
                }
              }}
            >
              Ahora no
            </AppButton>
          </Box>

          {/* Beneficios de la instalación */}
          <Box sx={{ mt: 2, pt: 2, borderTop: '1px solid rgba(255, 255, 255, 0.2)' }}>
            <Typography variant="caption" sx={{ color: 'rgba(255, 255, 255, 0.8)', fontSize: '0.75rem' }}>
              ✓ Acceso directo desde tu pantalla de inicio
              <br />
              ✓ Notificaciones de validaciones instantáneas
              <br />
              ✓ Funciona sin conexión a internet
            </Typography>
          </Box>
        </GlassSurface>
      </Box>
    </Slide>
  )
}

export default PWAInstallBanner
