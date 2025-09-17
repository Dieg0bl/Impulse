// AppButton - Botón accesible con micro-interacciones 2025-2026
import React, { forwardRef } from 'react'
import clsx from 'clsx'

export interface AppButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  size?: 'compact' | 'normal' | 'large'
  loading?: boolean
  icon?: React.ReactNode
  iconPosition?: 'start' | 'end'
  color?: 'primary' | 'secondary' | 'success' | 'warning' | 'error' | 'info'
  variant?: 'contained' | 'outlined' | 'text'
  fullWidth?: boolean
  sx?: object
}

export const AppButton = forwardRef<HTMLButtonElement, AppButtonProps>(
  ({
    size = 'normal',
    loading = false,
    icon,
    iconPosition = 'start',
    children,
    className,
    disabled,
    color = 'primary',
    variant = 'contained',
    style,
    fullWidth = false,
    sx,
    ...buttonProps
  }, ref) => {

    const getSizeConfig = () => {
      switch (size) {
        case 'compact': return { height: 32, fontSize: 12, padding: 12, iconSize: 14 }
        case 'large': return { height: 48, fontSize: 16, padding: 24, iconSize: 20 }
        default: return { height: 40, fontSize: 14, padding: 16, iconSize: 16 }
      }
    }

    const getColorConfig = () => {
      const colors = {
        primary: { bg: '#3b82f6', text: '#ffffff', hover: '#2563eb' },
        secondary: { bg: '#6b7280', text: '#ffffff', hover: '#4b5563' },
        success: { bg: '#10b981', text: '#ffffff', hover: '#059669' },
        warning: { bg: '#f59e0b', text: '#ffffff', hover: '#d97706' },
        error: { bg: '#ef4444', text: '#ffffff', hover: '#dc2626' },
        info: { bg: '#3b82f6', text: '#ffffff', hover: '#2563eb' }
      }
      return colors[color] || colors.primary
    }

    const config = getSizeConfig()
    const colorConfig = getColorConfig()

    const getVariantStyles = (): React.CSSProperties => {
      switch (variant) {
        case 'outlined':
          return {
            backgroundColor: 'transparent',
            color: colorConfig.bg,
            border: `1px solid ${colorConfig.bg}`,
          }
        case 'text':
          return {
            backgroundColor: 'transparent',
            color: colorConfig.bg,
            border: 'none',
          }
        default:
          return {
            backgroundColor: colorConfig.bg,
            color: colorConfig.text,
            border: 'none',
          }
      }
    }

    const variantStyles = getVariantStyles()

    const modernStyles: React.CSSProperties = {
      minHeight: config.height,
      fontSize: config.fontSize,
      padding: `0 ${config.padding}px`,
      borderRadius: 8,
      fontWeight: 500,
      cursor: disabled || loading ? 'not-allowed' : 'pointer',
      display: 'inline-flex',
      alignItems: 'center',
      justifyContent: 'center',
      gap: 8,
      outline: 'none',
      transition: 'all 0.2s ease',
      opacity: disabled || loading ? 0.6 : 1,
      width: fullWidth ? '100%' : 'auto',
      ...variantStyles,
      ...style,
      ...(sx ?? {}),
    }

    const renderSpinner = () => (
      <div
        style={{
          width: config.iconSize,
          height: config.iconSize,
          border: `2px solid transparent`,
          borderTop: `2px solid currentColor`,
          borderRadius: '50%',
          animation: 'spin 1s linear infinite'
        }}
      />
    )

    const renderContent = () => {
      if (loading) {
        return (
          <>
            {renderSpinner()}
            {children && <span>{children}</span>}
          </>
        )
      }
      if (icon && children) {
        const iconElement = <span style={{ display: 'inline-flex', alignItems: 'center' }}>{icon}</span>
        return iconPosition === 'start' ? <>{iconElement}<span>{children}</span></> : <><span>{children}</span>{iconElement}</>
      }
      return icon || children
    }

    return (
      <button
        ref={ref}
        disabled={disabled || loading}
        className={clsx('app-button', { 'app-button--loading': loading }, className)}
        style={modernStyles}
        aria-disabled={disabled || loading}
        aria-busy={loading}
        {...buttonProps}
      >
        {renderContent()}
      </button>
    )
  }
)

AppButton.displayName = 'AppButton'
export default AppButton
