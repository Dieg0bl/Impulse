// ModerationReportButton - Botón de reporte DSA según addenda IMPULSE
import React, { useState } from 'react'
import { Dialog, DialogTitle, DialogContent, DialogActions, TextField, FormControl, InputLabel, Select, MenuItem, Typography, Alert } from '@mui/material'
import { AppButton } from './AppButton'
import { Flag, AlertTriangle, Shield } from 'lucide-react'

export interface ReportReason {
  id: string
  label: string
  description: string
  category: 'illegal' | 'harmful' | 'inappropriate'
}

export interface ModerationReportButtonProps {
  contentType: 'challenge' | 'evidence' | 'comment' | 'profile'
  contentId: string
  contentAuthor?: string
  onReport: (reason: string, details: string, category: string) => void
  variant?: 'text' | 'outlined' | 'contained'
  size?: 'small' | 'medium' | 'large'
  showIcon?: boolean
  className?: string
}

const REPORT_REASONS: ReportReason[] = [
  {
    id: 'spam',
    label: 'Spam o contenido no deseado',
    description: 'Contenido repetitivo, promocional no autorizado o irrelevante',
    category: 'inappropriate'
  },
  {
    id: 'harassment',
    label: 'Acoso o intimidación',
    description: 'Comportamiento dirigido a intimidar, acosar o amenazar',
    category: 'harmful'
  },
  {
    id: 'hate_speech',
    label: 'Discurso de odio',
    description: 'Contenido que promueve odio hacia grupos por características protegidas',
    category: 'illegal'
  },
  {
    id: 'misinformation',
    label: 'Desinformación',
    description: 'Información falsa que puede causar daño',
    category: 'harmful'
  },
  {
    id: 'inappropriate_content',
    label: 'Contenido inapropiado',
    description: 'Contenido sexual, violento o no apto para la plataforma',
    category: 'inappropriate'
  },
  {
    id: 'privacy_violation',
    label: 'Violación de privacidad',
    description: 'Compartir información personal sin consentimiento',
    category: 'illegal'
  },
  {
    id: 'copyright',
    label: 'Infracción de derechos de autor',
    description: 'Uso no autorizado de contenido protegido',
    category: 'illegal'
  },
  {
    id: 'other',
    label: 'Otro motivo',
    description: 'Otros motivos no listados arriba',
    category: 'inappropriate'
  }
]

export const ModerationReportButton: React.FC<ModerationReportButtonProps> = ({
  contentType,
  contentId,
  contentAuthor,
  onReport,
  variant = 'text',
  size = 'medium',
  showIcon = true,
  className
}) => {
  const [open, setOpen] = useState(false)
  const [selectedReason, setSelectedReason] = useState('')
  const [details, setDetails] = useState('')
  const [loading, setLoading] = useState(false)

  const handleOpen = () => {
    setOpen(true)
  }

  const handleClose = () => {
    setOpen(false)
    setSelectedReason('')
    setDetails('')
  }

  const handleSubmit = async () => {
    if (!selectedReason) return

    setLoading(true)
    try {
      const reason = REPORT_REASONS.find(r => r.id === selectedReason)
      await onReport(selectedReason, details.trim(), reason?.category || 'inappropriate')
      handleClose()
    } catch (error) {
      console.error('Error al enviar reporte:', error)
    } finally {
      setLoading(false)
    }
  }

  const selectedReasonData = REPORT_REASONS.find(r => r.id === selectedReason)

  const getContentTypeLabel = () => {
    switch (contentType) {
      case 'challenge': return 'reto'
      case 'evidence': return 'evidencia'
      case 'comment': return 'comentario'
      case 'profile': return 'perfil'
      default: return 'contenido'
    }
  }

  return (
    <>
      <AppButton
        variant={variant}
        size={size}
        onClick={handleOpen}
        icon={showIcon ? <Flag size={16} /> : undefined}
        className={className}
        sx={{ 
          color: 'text.secondary',
          '&:hover': { 
            color: 'warning.main',
            backgroundColor: 'rgba(var(--color-warning), 0.1)' 
          }
        }}
      >
        Reportar {variant === 'text' ? '' : 'contenido'}
      </AppButton>

      <Dialog 
        open={open} 
        onClose={handleClose}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <Flag size={20} className="text-warning" />
          Reportar {getContentTypeLabel()}
        </DialogTitle>
        
        <DialogContent sx={{ py: 2 }}>
          <Alert severity="info" sx={{ mb: 3 }}>
            <Typography variant="body2">
              <strong>Digital Services Act (DSA) - Derecho a reportar:</strong><br />
              Tienes derecho a reportar contenido que consideres ilegal o dañino. 
              Tu reporte será revisado y recibirás una respuesta sobre las medidas tomadas.
            </Typography>
          </Alert>

          {contentAuthor && (
            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
              Reportando {getContentTypeLabel()} de <strong>{contentAuthor}</strong>
            </Typography>
          )}

          <FormControl fullWidth sx={{ mb: 3 }}>
            <InputLabel>¿Por qué reportas este contenido? *</InputLabel>
            <Select
              value={selectedReason}
              onChange={(e) => setSelectedReason(e.target.value)}
              label="¿Por qué reportas este contenido? *"
              required
            >
              {REPORT_REASONS.map((reason) => (
                <MenuItem key={reason.id} value={reason.id}>
                  <Box>
                    <Typography variant="body2" sx={{ fontWeight: 500 }}>
                      {reason.label}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      {reason.description}
                    </Typography>
                  </Box>
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          {selectedReasonData && (
            <Alert 
              severity={
                selectedReasonData.category === 'illegal' ? 'error' :
                selectedReasonData.category === 'harmful' ? 'warning' : 'info'
              } 
              sx={{ mb: 3 }}
              icon={selectedReasonData.category === 'illegal' ? <AlertTriangle /> : undefined}
            >
              <Typography variant="body2">
                <strong>Categoría: {
                  selectedReasonData.category === 'illegal' ? 'Contenido ilegal' :
                  selectedReasonData.category === 'harmful' ? 'Contenido dañino' :
                  'Contenido inapropiado'
                }</strong><br />
                {selectedReasonData.description}
              </Typography>
            </Alert>
          )}

          <TextField
            fullWidth
            multiline
            rows={4}
            label="Detalles adicionales (opcional)"
            placeholder="Proporciona más contexto sobre por qué reportas este contenido..."
            value={details}
            onChange={(e) => setDetails(e.target.value)}
            helperText="Cuantos más detalles proporciones, mejor podremos evaluar tu reporte"
          />
        </DialogContent>

        <DialogActions sx={{ p: 3, gap: 2 }}>
          <AppButton
            variant="outlined"
            onClick={handleClose}
            disabled={loading}
          >
            Cancelar
          </AppButton>
          <AppButton
            variant="contained"
            color="warning"
            onClick={handleSubmit}
            disabled={!selectedReason || loading}
            loading={loading}
            icon={<Shield size={16} />}
          >
            Enviar reporte
          </AppButton>
        </DialogActions>
      </Dialog>
    </>
  )
}

export default ModerationReportButton
