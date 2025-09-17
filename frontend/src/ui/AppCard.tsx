import React, { forwardRef } from 'react'
import clsx from 'clsx'

export interface AppCardProps extends React.HTMLAttributes<HTMLDivElement> {
  elevation?: 0 | 1 | 2 | 3
  variant?: 'default' | 'outlined' | 'glass'
  hover?: boolean
  interactive?: boolean
  loading?: boolean
  header?: React.ReactNode
  actions?: React.ReactNode
}

export const AppCard = forwardRef<HTMLDivElement, AppCardProps>(
  ({
    elevation = 1,
    variant = 'default',
    hover = false,
    interactive = false,
    loading = false,
    header,
    actions,
    children,
    className,
    onClick,
    ...cardProps
  }, ref) => {

    const baseStyles: React.CSSProperties = {
      borderRadius: '12px',
      padding: '16px',
      transition: 'all 0.2s ease',
      cursor: interactive ? 'pointer' : 'default',
      position: 'relative',
      backgroundColor: 'rgb(255, 255, 255)',
      boxShadow: elevation > 0 ? '0 2px 8px rgba(0,0,0,0.1)' : 'none',
      border: variant === 'outlined' ? '1px solid #e0e0e0' : 'none',
    }

    return (
      <div
        ref={ref}
        className={clsx('app-card', className)}
        style={baseStyles}
        onClick={interactive ? onClick : undefined}
        {...cardProps}
      >
        {header && <div className="app-card__header">{header}</div>}
        <div className="app-card__content">{children}</div>
        {actions && <div className="app-card__actions">{actions}</div>}
        {loading && <div className="app-card__loading">Loading...</div>}
      </div>
    )
  }
)

AppCard.displayName = 'AppCard'
export default AppCard
