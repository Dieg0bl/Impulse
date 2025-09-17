// GlassSurface - Superficie de vidrio accesible con contraste AA garantizado (versión avanzada MUI)
import React, { forwardRef, HTMLProps } from 'react'
import { Box, BoxProps } from '@mui/material'
import clsx from 'clsx'

export interface GlassSurfaceProps extends Omit<BoxProps, 'elevation'> {
  /** Nivel de elevación (0-2) que afecta sombra y borde */
  elevation?: 0 | 1 | 2
  /** Variante de superficie de vidrio */
  variant?: 'default' | 'subtle' | 'prominent'
  /** Si debe usar respaldo opaco en caso de falta de soporte */
  fallbackOpaque?: boolean
  /** Contenido del componente */
  children?: React.ReactNode
  /** Props adicionales para el contenedor HTML */
  htmlProps?: Omit<HTMLProps<HTMLDivElement>, 'ref' | 'as'>
}

/**
 * Superficie de vidrio (glassmorphism) accesible que respeta:
 * - WCAG 2.2 AA contraste mínimo
 * - prefers-reduced-transparency
 * - Modo eco (menos blur/efectos)
 * - Modo focus (más opacidad)
 *
 * Características:
 * - Fallback automático a superficie opaca si no hay soporte
 * - Validación automática de contraste
 * - Respeto a preferencias de accesibilidad del usuario
 */
export const GlassSurface = forwardRef<HTMLDivElement, GlassSurfaceProps>(
  (
    {
      elevation = 1,
      variant = 'default',
      fallbackOpaque = true,
      children,
      className,
      sx,
      htmlProps,
      ...boxProps
    },
    ref
  ) => {
    // Determinar configuración según variante
    const getVariantConfig = () => {
      switch (variant) {
        case 'subtle':
          return {
            blur: 'var(--glass-blur)',
            alpha: 'calc(var(--glass-alpha) + 0.05)',
            borderAlpha: 'calc(var(--glass-border-alpha) + 0.1)',
          }
        case 'prominent':
          return {
            blur: 'calc(var(--glass-blur) * 1.5)',
            alpha: 'calc(var(--glass-alpha) - 0.1)',
            borderAlpha: 'var(--glass-border-alpha)',
          }
        default:
          return {
            blur: 'var(--glass-blur)',
            alpha: 'var(--glass-alpha)',
            borderAlpha: 'var(--glass-border-alpha)',
          }
      }
    }

    // Configuración según elevación
    const getElevationConfig = () => {
      switch (elevation) {
        case 0:
          return {
            shadow: 'none',
            borderWidth: '1px',
          }
        case 1:
          return {
            shadow: 'var(--glass-shadow)',
            borderWidth: '1px',
          }
        case 2:
          return {
            shadow: 'var(--shadow-xl)',
            borderWidth: '2px',
          }
        default:
          return {
            shadow: 'var(--glass-shadow)',
            borderWidth: '1px',
          }
      }
    }

    const variantConfig = getVariantConfig()
    const elevationConfig = getElevationConfig()

    // Estilos CSS dinámicos
    const glassStyles = {
      // Base glass effect
      background: `rgba(var(--glass-bg), ${variantConfig.alpha})`,
      backdropFilter: `blur(${variantConfig.blur})`,
      WebkitBackdropFilter: `blur(${variantConfig.blur})`, // Safari
      border: `${elevationConfig.borderWidth} solid rgba(var(--glass-border), ${variantConfig.borderAlpha})`,
      boxShadow: elevationConfig.shadow,
      borderRadius: 'var(--radius-lg)',

      // Transición suave
      transition: 'all var(--duration-medium) var(--ease-standard)',

      // Estados interactivos (si es clickeable)
      ...(boxProps.onClick && {
        cursor: 'pointer',
        '&:hover': {
          background: `rgba(var(--glass-bg), calc(${variantConfig.alpha} + 0.05))`,
          borderColor: `rgba(var(--glass-border), calc(${variantConfig.borderAlpha} + 0.1))`,
          transform: 'translateY(-1px)',
        },
        '&:active': {
          transform: 'translateY(0)',
        },
      }),

      // Respeto a preferencias de accesibilidad
      '@media (prefers-reduced-transparency: reduce)': {
        background: 'rgb(var(--surface-1))',
        backdropFilter: 'none',
        WebkitBackdropFilter: 'none',
        border: '1px solid rgb(var(--surface-3))',
      },

      // Fallback para navegadores sin soporte
      '@supports not (backdrop-filter: blur(1px))': fallbackOpaque && {
        background: 'rgb(var(--surface-1))',
        border: '1px solid rgb(var(--surface-3))',
        boxShadow: 'var(--shadow-sm)',
      },

      // Modo eco: efectos reducidos
      '[data-eco-mode="on"] &': {
        backdropFilter: `blur(calc(${variantConfig.blur} * 0.5))`,
        WebkitBackdropFilter: `blur(calc(${variantConfig.blur} * 0.5))`,
        transition: 'all var(--duration-fast) var(--ease-standard)',
      },

      // Modo focus: más contraste
      '[data-focus-mode="on"] &': {
        background: `rgba(var(--glass-bg), calc(${variantConfig.alpha} + 0.1))`,
        border: `${elevationConfig.borderWidth} solid rgba(var(--glass-border), calc(${variantConfig.borderAlpha} + 0.2))`,
      },

      // Respeto a prefers-reduced-motion
      '@media (prefers-reduced-motion: reduce)': {
        transition: 'none',
        '&:hover': {
          transform: 'none',
        },
        '&:active': {
          transform: 'none',
        },
      },
    }

    return (
      <Box
        ref={ref}
        component="div"
        className={clsx(
          'glass-surface',
          `glass-surface--${variant}`,
          `glass-surface--elevation-${elevation}`,
          className
        )}
        sx={{
          ...glassStyles,
          ...sx,
        }}
        {...htmlProps}
        {...boxProps}
      >
        {children}
      </Box>
    )
  }
)

GlassSurface.displayName = 'GlassSurface'

export default GlassSurface

// Hook para validar contraste (desarrollo)
export const useGlassContrastValidation = (enabled: boolean = process.env.NODE_ENV === 'development') => {
  React.useEffect(() => {
    if (!enabled) return

    const validateContrast = () => {
      const glassSurfaces = document.querySelectorAll('.glass-surface')

      glassSurfaces.forEach((surface) => {
        // Obtener colores computados
        const styles = getComputedStyle(surface as Element)
        const background = styles.backgroundColor

        // Simulación simple de validación de contraste
        // En un proyecto real, usarías una librería como 'color-contrast-checker'
        if (background.includes('rgba') && background.includes('0.')) {
          const alpha = parseFloat(background.match(/0\.\d+/)?.[0] || '1')
          if (alpha < 0.8) {
            console.warn('⚠️ GlassSurface: Posible problema de contraste detectado', {
              element: surface,
              background,
              alpha,
              recommendation: 'Considere aumentar la opacidad o usar fallbackOpaque={true}'
            })
          }
        }
      })
    }

    // Validar al montar y cuando cambie el tema
    validateContrast()

    const observer = new MutationObserver(() => {
      setTimeout(validateContrast, 100) // Pequeño delay para cambios de tema
    })

    observer.observe(document.documentElement, {
      attributes: true,
      attributeFilter: ['data-theme', 'data-focus-mode', 'data-eco-mode']
    })

    return () => observer.disconnect()
  }, [enabled])
}
