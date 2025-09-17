// ValidatorAvatarStack - Stack de validadores con estados según addenda IMPULSE
import React from 'react'
import { Avatar, Tooltip, Box, Badge, Chip } from '@mui/material'
import { CheckCircle, XCircle, Clock, AlertTriangle } from 'lucide-react'

export type ValidatorState = 'pending' | 'ok' | 'rejected' | 'overdue'

export interface ValidatorItem {
  id: string
  name: string
  image?: string
  state: ValidatorState
  relation?: string
  responseTime?: string
}

export interface ValidatorAvatarStackProps {
  validators: ValidatorItem[]
  maxVisible?: number
  size?: 'small' | 'medium' | 'large'
  showLabels?: boolean
  onValidatorClick?: (validator: ValidatorItem) => void
}

export const ValidatorAvatarStack: React.FC<ValidatorAvatarStackProps> = ({
  validators,
  maxVisible = 5,
  size = 'medium',
  showLabels = false,
  onValidatorClick
}) => {

  const getSizeConfig = () => {
    switch (size) {
      case 'small': return { width: 32, height: 32, fontSize: '0.75rem', overlap: -0.8 }
      case 'large': return { width: 56, height: 56, fontSize: '1.25rem', overlap: -1.5 }
      default: return { width: 40, height: 40, fontSize: '1rem', overlap: -1.2 }
    }
  }

  const config = getSizeConfig()
  const visibleValidators = validators.slice(0, maxVisible)
  const hiddenCount = Math.max(0, validators.length - maxVisible)

  const getStateIcon = (state: ValidatorState) => {
    const iconSize = size === 'small' ? 12 : size === 'large' ? 16 : 14

    switch (state) {
      case 'ok': return <CheckCircle size={iconSize} className="text-success" />
      case 'rejected': return <XCircle size={iconSize} className="text-error" />
      case 'overdue': return <AlertTriangle size={iconSize} className="text-warning" />
      case 'pending': return <Clock size={iconSize} className="text-text-3" />
      default: return null
    }
  }

  const getStateColor = (state: ValidatorState) => {
    switch (state) {
      case 'ok': return 'rgb(var(--color-success))'
      case 'rejected': return 'rgb(var(--color-error))'
      case 'overdue': return 'rgb(var(--color-warning))'
      case 'pending': return 'rgb(var(--text-3))'
      default: return 'rgb(var(--surface-3))'
    }
  }

  const getStateLabel = (state: ValidatorState) => {
    switch (state) {
      case 'ok': return 'Validado'
      case 'rejected': return 'Rechazado'
      case 'overdue': return 'Retrasado'
      case 'pending': return 'Pendiente'
      default: return 'Desconocido'
    }
  }

  const formatTooltipContent = (validator: ValidatorItem) => {
    const parts = [
      validator.name,
      validator.relation ? `(${validator.relation})` : null,
      getStateLabel(validator.state),
      validator.responseTime ? `(${validator.responseTime})` : null
    ].filter(Boolean)
    return parts.join(' ')
  }

  const renderAvatar = (validator: ValidatorItem, index: number) => (
    <Tooltip
      key={validator.id}
      title={formatTooltipContent(validator)}
      arrow
      placement="top"
    >
      <Badge
        overlap="circular"
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
        badgeContent={
          <Box
            sx={{
              width: size === 'small' ? 16 : 20,
              height: size === 'small' ? 16 : 20,
              borderRadius: '50%',
              backgroundColor: getStateColor(validator.state),
              border: '2px solid white',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              boxShadow: 'var(--shadow-sm)'
            }}
          >
            {getStateIcon(validator.state)}
          </Box>
        }
      >
        <Avatar
          src={validator.image}
          alt={validator.name}
          onClick={() => onValidatorClick?.(validator)}
          sx={{
            width: config.width,
            height: config.height,
            ml: index > 0 ? config.overlap : 0,
            border: '2px solid rgba(255, 255, 255, 0.8)',
            cursor: onValidatorClick ? 'pointer' : 'default',
            transition: 'all var(--duration-fast) var(--ease-standard)',
            fontSize: config.fontSize,
            fontWeight: 600,
            backgroundColor: 'rgb(var(--color-primary))',
            color: 'white',

            '&:hover': onValidatorClick ? {
              transform: 'translateY(-2px) scale(1.05)',
              zIndex: 10,
              boxShadow: 'var(--shadow-md)',
            } : {},

            '@media (prefers-reduced-motion: reduce)': {
              transition: 'none',
              '&:hover': { transform: 'none' }
            }
          }}
          className={"validator-avatar validator-avatar--" + validator.state}
        >
          {validator.name.charAt(0).toUpperCase()}
        </Avatar>
      </Badge>
    </Tooltip>
  )

  const renderOverflowIndicator = () => {
    if (hiddenCount <= 0) return null

    return (
      <Tooltip title={`+${hiddenCount} validador(es) más`} arrow placement="top">
        <Avatar
          sx={{
            width: config.width,
            height: config.height,
            ml: config.overlap,
            border: '2px solid rgba(255, 255, 255, 0.8)',
            backgroundColor: 'rgb(var(--surface-2))',
            color: 'rgb(var(--text-2))',
            fontSize: size === 'small' ? '0.65rem' : size === 'large' ? '0.9rem' : '0.75rem',
            fontWeight: 600,
          }}
        >
          +{hiddenCount}
        </Avatar>
      </Tooltip>
    )
  }

  if (validators.length === 0) {
    return (
      <Box className="flex items-center gap-2 text-text-3">
        <Avatar
          sx={{
            width: config.width,
            height: config.height,
            backgroundColor: 'rgb(var(--surface-3))',
            color: 'rgb(var(--text-3))',
          }}
        >
          ?
        </Avatar>
        <span className="text-sm">Sin validadores</span>
      </Box>
    )
  }

  return (
    <Box
      className="validator-avatar-stack"
      sx={{
        display: 'flex',
        alignItems: 'center',
        flexDirection: showLabels ? 'column' : 'row',
        gap: showLabels ? 1 : 0
      }}
    >
      <Box sx={{ display: 'flex', alignItems: 'center' }}>
        {visibleValidators.map((validator, index) => renderAvatar(validator, index))}
        {renderOverflowIndicator()}
      </Box>

      {showLabels && (
        <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap', justifyContent: 'center' }}>
          {validators.map((validator) => (
            <Chip
              key={validator.id}
              label={`${validator.name}: ${getStateLabel(validator.state)}`}
              size="small"
              variant="outlined"
              color={validator.state === 'ok' ? 'success' :
                     validator.state === 'rejected' ? 'error' :
                     validator.state === 'overdue' ? 'warning' : 'default'}
              sx={{ fontSize: '0.75rem' }}
            />
          ))}
        </Box>
      )}
    </Box>
  )
}

export default ValidatorAvatarStack
