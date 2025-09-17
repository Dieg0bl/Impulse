// ValidationDecisionCard - UI para validador según addenda IMPULSE
import React, { useState } from 'react'
import { Card, CardContent, CardActions, Typography, TextField, Box, Chip, Avatar } from '@mui/material'
import { AppButton } from './AppButton'
import { GlassSurface } from './GlassSurface'
import { CheckCircle, XCircle, Calendar, User, MessageSquare } from 'lucide-react'
import { motion } from 'framer-motion'

export interface EvidenceData {
  id: string
  challengeTitle: string
  userComment: string
  attachments: Array<{
    type: 'image' | 'video' | 'document'
    url: string
    name: string
  }>
  submittedAt: Date
  deadline?: Date
  challengeCreator: {
    name: string
    image?: string
  }
}

export interface ValidationDecisionCardProps {
  evidence: EvidenceData
  onValidate: (feedback?: string) => void
  onReject: (reason: string) => void
  loading?: boolean
  className?: string
}

export const ValidationDecisionCard: React.FC<ValidationDecisionCardProps> = ({
  evidence,
  onValidate,
  onReject,
  loading = false,
  className
}) => {
  const [decision, setDecision] = useState<'validate' | 'reject' | null>(null)
  const [feedback, setFeedback] = useState('')
  const [rejectReason, setRejectReason] = useState('')
  const [showForm, setShowForm] = useState(false)

  const handleValidateClick = () => {
    setDecision('validate')
    setShowForm(true)
  }

  const handleRejectClick = () => {
    setDecision('reject')
    setShowForm(true)
  }

  const handleCancel = () => {
    setDecision(null)
    setShowForm(false)
    setFeedback('')
    setRejectReason('')
  }

  const handleConfirm = () => {
    if (decision === 'validate') {
      onValidate(feedback.trim() || undefined)
    } else if (decision === 'reject' && rejectReason.trim()) {
      onReject(rejectReason.trim())
    }
  }

  const isConfirmDisabled = () => {
    if (decision === 'reject') return !rejectReason.trim()
    return false
  }

  const formatTimeAgo = (date: Date) => {
    const now = new Date()
    const diffMs = now.getTime() - date.getTime()
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
    const diffDays = Math.floor(diffHours / 24)

  if (diffDays > 0) return `hace ${diffDays} día${diffDays > 1 ? 's' : ''}`
  if (diffHours > 0) return `hace ${diffHours} hora${diffHours > 1 ? 's' : ''}`
  return 'hace unos minutos'
  }

  const renderAttachments = () => {
    if (evidence.attachments.length === 0) return null

    return (
      <Box sx={{ mt: 2 }}>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 1, fontWeight: 500 }}>
          Evidencias adjuntas:
        </Typography>
        <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
          {evidence.attachments.map((attachment, index) => (
            <Chip
              key={index}
              label={attachment.name}
              variant="outlined"
              size="small"
              onClick={() => window.open(attachment.url, '_blank')}
              sx={{ cursor: 'pointer' }}
            />
          ))}
        </Box>
      </Box>
    )
  }

  const renderDeadlineInfo = () => {
    if (!evidence.deadline) return null

    const now = new Date()
    const isOverdue = evidence.deadline < now
    const hoursLeft = Math.floor((evidence.deadline.getTime() - now.getTime()) / (1000 * 60 * 60))

    return (
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mt: 1 }}>
        <Calendar size={16} className={isOverdue ? 'text-error' : 'text-warning'} />
        <Typography
          variant="caption"
          color={isOverdue ? 'error' : 'warning.main'}
          sx={{ fontWeight: 500 }}
        >
          {isOverdue
            ? 'Validación vencida'
            : hoursLeft > 24
              ? `${Math.floor(hoursLeft / 24)} días restantes`
              : `${hoursLeft} horas restantes`
          }
        </Typography>
      </Box>
    )
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className={className}
    >
      <GlassSurface
        elevation={2}
        variant="prominent"
        sx={{
          maxWidth: 600,
          mx: 'auto',
          overflow: 'visible'
        }}
      >
        <CardContent sx={{ p: 3 }}>
          {/* Header con info del usuario */}
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
            <Avatar
              src={evidence.challengeCreator.image}
              alt={evidence.challengeCreator.name}
              sx={{ width: 48, height: 48 }}
            >
              {evidence.challengeCreator.name.charAt(0)}
            </Avatar>
            <Box sx={{ flex: 1 }}>
              <Typography variant="h6" sx={{ fontWeight: 600, color: 'text.primary' }}>
                {evidence.challengeTitle}
              </Typography>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <User size={14} className="text-text-3" />
                <Typography variant="body2" color="text.secondary">
                  {evidence.challengeCreator.name}  {formatTimeAgo(evidence.submittedAt)}
                </Typography>
              </Box>
              {renderDeadlineInfo()}
            </Box>
          </Box>

          {/* Comentario del usuario */}
          {evidence.userComment && (
            <Box sx={{ mb: 2, p: 2, backgroundColor: 'rgba(var(--surface-2), 0.5)', borderRadius: 'var(--radius-md)' }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                <MessageSquare size={16} className="text-text-3" />
                <Typography variant="body2" color="text.secondary" sx={{ fontWeight: 500 }}>
                  Comentario del usuario:
                </Typography>
              </Box>
              <Typography variant="body1" sx={{ fontStyle: 'italic' }}>
                "{evidence.userComment}"
              </Typography>
            </Box>
          )}

          {/* Attachments */}
          {renderAttachments()}

          {/* Formulario de decisión */}
          {showForm && (
            <motion.div
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: 'auto' }}
              exit={{ opacity: 0, height: 0 }}
              transition={{ duration: 0.3 }}
            >
              <Box sx={{ mt: 3, p: 2, backgroundColor: 'rgba(var(--surface-2), 0.3)', borderRadius: 'var(--radius-md)' }}>
                {decision === 'validate' && (
                  <>
                    <Typography variant="body2" sx={{ mb: 2, fontWeight: 500, color: 'success.main' }}>
                       Validar evidencia (opcional: añade un mensaje de felicitación)
                    </Typography>
                    <TextField
                      fullWidth
                      multiline
                      rows={3}
                      placeholder="¡Excelente trabajo! Has cumplido el objetivo..."
                      value={feedback}
                      onChange={(e) => setFeedback(e.target.value)}
                      variant="outlined"
                      size="small"
                      sx={{ mb: 2 }}
                    />
                  </>
                )}

                {decision === 'reject' && (
                  <>
                    <Typography variant="body2" sx={{ mb: 2, fontWeight: 500, color: 'error.main' }}>
                       Rechazar evidencia (motivo obligatorio)
                    </Typography>
                    <TextField
                      fullWidth
                      multiline
                      rows={3}
                      placeholder="Explica por qué no cumple con el objetivo del reto..."
                      value={rejectReason}
                      onChange={(e) => setRejectReason(e.target.value)}
                      variant="outlined"
                      size="small"
                      required
                      error={rejectReason.trim() === ''}
                      helperText={rejectReason.trim() === '' ? 'El motivo es obligatorio para rechazar' : ''}
                      sx={{ mb: 2 }}
                    />
                  </>
                )}

                <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
                  <AppButton
                    variant="outlined"
                    onClick={handleCancel}
                    disabled={loading}
                  >
                    Cancelar
                  </AppButton>
                  <AppButton
                    variant="contained"
                    color={decision === 'validate' ? 'success' : 'error'}
                    onClick={handleConfirm}
                    disabled={isConfirmDisabled() || loading}
                    loading={loading}
                  >
                    {decision === 'validate' ? 'Validar' : 'Rechazar'}
                  </AppButton>
                </Box>
              </Box>
            </motion.div>
          )}
        </CardContent>

        {/* Botones de acción */}
        {!showForm && (
          <CardActions sx={{ px: 3, pb: 3, gap: 2 }}>
            <AppButton
              variant="contained"
              color="success"
              size="large"
              onClick={handleValidateClick}
              disabled={loading}
              icon={<CheckCircle size={18} />}
              sx={{ flex: 1 }}
            >
              Validar
            </AppButton>
            <AppButton
              variant="outlined"
              color="error"
              size="large"
              onClick={handleRejectClick}
              disabled={loading}
              icon={<XCircle size={18} />}
              sx={{ flex: 1 }}
            >
              Rechazar
            </AppButton>
          </CardActions>
        )}
      </GlassSurface>
    </motion.div>
  )
}

export default ValidationDecisionCard
