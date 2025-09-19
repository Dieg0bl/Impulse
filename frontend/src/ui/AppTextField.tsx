// AppTextField - Campo accesible con validación 2025-2026
import React, { forwardRef, useState, useId } from 'react'
import { TextField, TextFieldProps, InputAdornment, Chip } from '@mui/material'
import { AlertCircle, CheckCircle, Info, Eye, EyeOff } from 'lucide-react'
import clsx from 'clsx'

export interface AppTextFieldProps extends Omit<TextFieldProps, 'size' | 'variant'> {
  size?: 'compact' | 'normal' | 'large'
  validationState?: 'success' | 'error' | 'warning' | 'info'
  validationMessage?: string
  showValidationIcon?: boolean
  characterLimit?: number
  maxLength?: number
  passwordToggle?: boolean
  showOptionalLabel?: boolean
}

export const AppTextField = forwardRef<HTMLInputElement, AppTextFieldProps>(
  ({
    size = 'normal',
    validationState,
    validationMessage,
    showValidationIcon = true,
    characterLimit,
  maxLength,
    passwordToggle = false,
    showOptionalLabel = false,
    label,
    required,
    value = '',
    type,
    helperText,
    className,
    sx,
    ...textFieldProps
  }, ref) => {

    const [showPassword, setShowPassword] = useState(false)
    const [focused, setFocused] = useState(false)
    const fieldId = useId()
    const helperTextId = useId()

    const getSizeConfig = () => {
      switch (size) {
        case 'compact': return { muiSize: 'small' as const, height: 'var(--control-height-compact)' }
        case 'large': return { muiSize: 'medium' as const, height: 'var(--control-height-large)' }
        default: return { muiSize: 'medium' as const, height: 'var(--control-height-normal)' }
      }
    }

    const config = getSizeConfig()
    const currentLength = String(value).length
    const isPasswordField = type === 'password' || passwordToggle
    const inputType = isPasswordField && showPassword ? 'text' : type

    const getValidationIcon = () => {
      if (!showValidationIcon || !validationState) return null

      const iconProps = { size: 16, className: 'validation-icon' }
      switch (validationState) {
        case 'success': return <CheckCircle {...iconProps} className="text-success" />
        case 'error': return <AlertCircle {...iconProps} className="text-error" />
        case 'warning': return <AlertCircle {...iconProps} className="text-warning" />
        case 'info': return <Info {...iconProps} className="text-info" />
        default: return null
      }
    }

    const getPasswordToggle = () => {
      if (!passwordToggle && !isPasswordField) return null

      return (
        <button
          type="button"
          onClick={() => setShowPassword(!showPassword)}
          className="p-1 rounded-sm hover:bg-surface-2 focus:bg-surface-2 focus:outline-none focus:ring-2 focus:ring-primary transition-colors"
          aria-label={showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'}
          tabIndex={-1}
        >
          {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
        </button>
      )
    }

    const renderLabel = () => {
      if (!label) return label

      return (
        <span>
          {label}
          {required && <span className="text-error ml-1" aria-label="obligatorio">*</span>}
          {showOptionalLabel && !required && (
            <Chip
              label="opcional"
              size="small"
              variant="outlined"
              sx={{ ml: 1, height: 18, fontSize: '0.75rem' }}
            />
          )}
        </span>
      )
    }

    const renderHelperText = () => {
      const elements = []

      if (validationMessage) {
        elements.push(
          <span key="validation" className="validation-message text-error">
            {validationMessage}
          </span>
        )
      }

      if (helperText) {
        elements.push(
          <span key="helper" className="helper-text">
            {helperText}
          </span>
        )
      }

      if (characterLimit) {
        elements.push(
          <span
            key="counter"
            className={clsx('character-counter text-sm', {
              'text-error': currentLength > characterLimit,
              'text-warning': currentLength > characterLimit * 0.8,
              'text-text-3': currentLength <= characterLimit * 0.8
            })}
            aria-live="polite"
          >
            {currentLength}/{characterLimit}
          </span>
        )
      }

      return elements.length > 0 ? (
        <div className="flex justify-between items-start mt-1 gap-2">
          <div className="flex-1">
            {elements.filter(el => el.key !== 'counter')}
          </div>
          {elements.find(el => el.key === 'counter')}
        </div>
      ) : null
    }

    const modernStyles = {
      minHeight: config.height,
      '& .MuiOutlinedInput-root': {
        borderRadius: 'var(--radius-md)',
        transition: 'all var(--duration-fast) var(--ease-standard)',
        '&:hover': {
          transform: focused ? 'none' : 'translateY(-1px)',
          boxShadow: focused ? 'none' : 'var(--shadow-sm)',
        },
        '&.Mui-focused': {
          transform: 'translateY(-1px)',
          boxShadow: 'var(--shadow-md)',
        },
        '&.Mui-error': {
          borderColor: 'rgb(var(--color-error))',
        }
      },
      '& .MuiInputLabel-root': {
        fontWeight: 500,
        '&.Mui-focused': {
          color: 'rgb(var(--color-primary))',
        }
      },
      '@media (prefers-reduced-motion: reduce)': {
        '& .MuiOutlinedInput-root': {
          transition: 'none',
          '&:hover': { transform: 'none' },
          '&.Mui-focused': { transform: 'none' }
        }
      },
      ...sx,
    }

    const endAdornments = []
    if (getValidationIcon()) endAdornments.push(getValidationIcon())
    if (getPasswordToggle()) endAdornments.push(getPasswordToggle())

    return (
      <TextField
        ref={ref}
        id={fieldId}
        size={config.muiSize}
        variant="outlined"
        label={renderLabel()}
        type={inputType}
        value={value}
        required={required}
        error={validationState === 'error'}
        helperText={renderHelperText()}
        className={clsx('app-textfield',
          validationState && `app-textfield--${validationState}`,
          { 'app-textfield--focused': focused },
          className
        )}
        sx={modernStyles}
        onFocus={(e) => {
          setFocused(true)
          textFieldProps.onFocus?.(e)
        }}
        onBlur={(e) => {
          setFocused(false)
          textFieldProps.onBlur?.(e)
        }}
        InputProps={{
          endAdornment: endAdornments.length > 0 ? (
            <InputAdornment position="end" className="flex gap-1">
              {endAdornments}
            </InputAdornment>
          ) : undefined,
          'aria-describedby': [
            validationMessage ? `${fieldId}-validation` : null,
            helperText ? `${fieldId}-helper` : null,
            characterLimit ? `${fieldId}-counter` : null
          ].filter(Boolean).join(' '),
          ...textFieldProps.InputProps,
        }}
        inputProps={{
          maxLength: characterLimit ?? maxLength,
          'aria-invalid': validationState === 'error',
          'aria-required': required,
          ...textFieldProps.inputProps,
        }}
        {...textFieldProps}
      />
    )
  }
)

AppTextField.displayName = 'AppTextField'
export default AppTextField
