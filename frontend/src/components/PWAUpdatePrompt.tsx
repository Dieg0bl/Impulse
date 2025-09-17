// PWAUpdatePrompt - Prompt de actualización PWA según UI/UX 2025-2026
import React, { useState } from 'react'
import { Snackbar, Alert, AlertTitle, IconButton } from '@mui/material'
import { AppButton } from '../ui/AppButton'
import { RefreshCw, X } from 'lucide-react'
import { usePWA } from '../hooks/usePWA'

export interface PWAUpdatePromptProps {
  onUpdated?: () => void
  className?: string
}

export const PWAUpdatePrompt: React.FC<PWAUpdatePromptProps> = ({
  onUpdated,
  className
}) => {
  const { needRefresh, updateSW } = usePWA()
  const [updating, setUpdating] = useState(false)
  const [dismissed, setDismissed] = useState(false)

  if (!needRefresh || dismissed) {
    return null
  }

  const handleUpdate = async () => {
    setUpdating(true)
    try {
      await updateSW()
      onUpdated?.()
    } catch (error) {
      console.error('Error updating PWA:', error)
    } finally {
      setUpdating(false)
    }
  }

  const handleDismiss = () => {
    setDismissed(true)
  }

  return (
    <Snackbar
      open={true}
      anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
      className={className}
      sx={{ mt: 2 }}
    >
      <Alert
        severity="info"
        variant="filled"
        sx={{
          background: 'linear-gradient(135deg, rgb(var(--grad-start)) 0%, rgb(var(--grad-end)) 100%)',
          color: 'white',
          minWidth: 320,
          '& .MuiAlert-icon': {
            color: 'white'
          }
        }}
        action={
          <IconButton
            onClick={handleDismiss}
            size="small"
            sx={{
              color: 'white',
              '&:hover': {
                backgroundColor: 'rgba(255, 255, 255, 0.1)'
              }
            }}
          >
            <X size={16} />
          </IconButton>
        }
      >
        <AlertTitle sx={{ color: 'white', fontWeight: 600 }}>
          Nueva versión disponible
        </AlertTitle>

        <div style={{ marginBottom: '12px', fontSize: '0.875rem' }}>
          Hay mejoras y correcciones disponibles. ¡Actualiza para obtener la mejor experiencia!
        </div>

        <div style={{ display: 'flex', gap: '8px', marginTop: '8px' }}>
          <AppButton
            variant="contained"
            size="compact"
            onClick={handleUpdate}
            loading={updating}
            disabled={updating}
            icon={<RefreshCw size={14} />}
            sx={{
              backgroundColor: 'rgba(255, 255, 255, 0.2)',
              color: 'white',
              border: '1px solid rgba(255, 255, 255, 0.3)',
              fontSize: '0.75rem',
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
            {updating ? 'Actualizando...' : 'Actualizar'}
          </AppButton>

          <AppButton
            variant="text"
            size="compact"
            onClick={handleDismiss}
            sx={{
              color: 'rgba(255, 255, 255, 0.8)',
              fontSize: '0.75rem',
              '&:hover': {
                color: 'white',
                backgroundColor: 'rgba(255, 255, 255, 0.1)',
              }
            }}
          >
            Más tarde
          </AppButton>
        </div>
      </Alert>
    </Snackbar>
  )
}

export default PWAUpdatePrompt
