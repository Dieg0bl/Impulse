// Theme MUI 2025-2026 sincronizado con tokens CSS
import React, { ReactNode, useMemo, useEffect, useState } from 'react'
import { createTheme, ThemeProvider, CssBaseline, alpha } from '@mui/material'

// Helper para leer variables CSS del DOM
const getCSSVar = (varName: string): string => {
  if (typeof window === 'undefined') return ''
  return getComputedStyle(document.documentElement)
    .getPropertyValue(varName)
    .trim()
}

// Helper para convertir RGB a formato MUI
const rgbToMui = (rgbValues: string): string => {
  if (!rgbValues) return '#000000'
  const rgb = rgbValues.split(' ').map(val => parseInt(val.trim(), 10))
  if (rgb.length !== 3) return '#000000'
  return `rgb(${rgb[0]}, ${rgb[1]}, ${rgb[2]})`
}

// Hook para detectar cambios de tema
const useThemeDetection = () => {
  const [isDark, setIsDark] = useState(false)
  const [isFocusMode, setIsFocusMode] = useState(false)
  const [isEcoMode, setIsEcoMode] = useState(false)

  useEffect(() => {
    const updateTheme = () => {
      const html = document.documentElement
      setIsDark(html.getAttribute('data-theme') === 'dark')
      setIsFocusMode(html.getAttribute('data-focus-mode') === 'on')
      setIsEcoMode(html.getAttribute('data-eco-mode') === 'on')
    }

    // Observar cambios en atributos del HTML
    const observer = new MutationObserver(updateTheme)
    observer.observe(document.documentElement, {
      attributes: true,
      attributeFilter: ['data-theme', 'data-focus-mode', 'data-eco-mode']
    })

    updateTheme() // Inicial

    return () => observer.disconnect()
  }, [])

  return { isDark, isFocusMode, isEcoMode }
}

// Crear tema dinámico basado en tokens CSS
const createAppTheme = (isDark: boolean, isFocusMode: boolean, isEcoMode: boolean) => {
  // Leer colores desde variables CSS
  const primaryRgb = getCSSVar('--color-primary')
  const secondaryRgb = getCSSVar('--color-secondary')
  const successRgb = getCSSVar('--color-success')
  const warningRgb = getCSSVar('--color-warning')
  const errorRgb = getCSSVar('--color-error')
  const infoRgb = getCSSVar('--color-info')

  const surface0Rgb = getCSSVar('--surface-0')
  const surface1Rgb = getCSSVar('--surface-1')
  const text1Rgb = getCSSVar('--text-1')
  const text2Rgb = getCSSVar('--text-2')

  // Convertir a formato MUI
  const primary = rgbToMui(primaryRgb)
  const secondary = rgbToMui(secondaryRgb)
  const success = rgbToMui(successRgb)
  const warning = rgbToMui(warningRgb)
  const error = rgbToMui(errorRgb)
  const info = rgbToMui(infoRgb)

  const backgroundDefault = rgbToMui(surface0Rgb)
  const backgroundPaper = rgbToMui(surface1Rgb)
  const textPrimary = rgbToMui(text1Rgb)
  const textSecondary = rgbToMui(text2Rgb)

  // Duraciones de animación basadas en modo
  const transitions = {
    duration: {
      shortest: isEcoMode ? 80 : 150,
      shorter: isEcoMode ? 120 : 200,
      short: isEcoMode ? 160 : 250,
      standard: isEcoMode ? 200 : 300,
      complex: isEcoMode ? 280 : 375,
      enteringScreen: isEcoMode ? 180 : 225,
      leavingScreen: isEcoMode ? 140 : 195,
    },
    easing: {
      easeInOut: 'var(--ease-standard)',
      easeOut: 'var(--ease-decelerated)',
      easeIn: 'var(--ease-emphasized)',
      sharp: 'cubic-bezier(0.4, 0, 0.6, 1)',
    }
  }

  return createTheme({
    palette: {
      mode: isDark ? 'dark' : 'light',
      primary: {
        main: primary,
        light: alpha(primary, 0.7),
        dark: alpha(primary, 0.9),
        contrastText: '#ffffff',
      },
      secondary: {
        main: secondary,
        light: alpha(secondary, 0.7),
        dark: alpha(secondary, 0.9),
        contrastText: '#ffffff',
      },
      success: {
        main: success,
        light: alpha(success, 0.7),
        dark: alpha(success, 0.9),
      },
      warning: {
        main: warning,
        light: alpha(warning, 0.7),
        dark: alpha(warning, 0.9),
      },
      error: {
        main: error,
        light: alpha(error, 0.7),
        dark: alpha(error, 0.9),
      },
      info: {
        main: info,
        light: alpha(info, 0.7),
        dark: alpha(info, 0.9),
      },
      background: {
        default: backgroundDefault,
        paper: backgroundPaper,
      },
      text: {
        primary: textPrimary,
        secondary: textSecondary,
      },
      divider: alpha(textPrimary, 0.12),
    },
    typography: {
      fontFamily: 'var(--font-family-base)',
      h1: {
        fontSize: 'var(--text-4xl)',
        fontWeight: 700,
        lineHeight: 1.167,
        letterSpacing: '-0.01562em',
      },
      h2: {
        fontSize: 'var(--text-3xl)',
        fontWeight: 600,
        lineHeight: 1.2,
        letterSpacing: '-0.00833em',
      },
      h3: {
        fontSize: 'var(--text-2xl)',
        fontWeight: 600,
        lineHeight: 1.3,
      },
      h4: {
        fontSize: 'var(--text-xl)',
        fontWeight: 600,
        lineHeight: 1.4,
        letterSpacing: '0.00735em',
      },
      h5: {
        fontSize: 'var(--text-lg)',
        fontWeight: 500,
        lineHeight: 1.5,
      },
      h6: {
        fontSize: 'var(--text-base)',
        fontWeight: 500,
        lineHeight: 1.6,
        letterSpacing: '0.0075em',
      },
      body1: {
        fontSize: 'var(--text-base)',
        lineHeight: 1.6,
        letterSpacing: '0.00938em',
      },
      body2: {
        fontSize: 'var(--text-sm)',
        lineHeight: 1.5,
        letterSpacing: '0.01071em',
      },
      button: {
        fontSize: 'var(--text-sm)',
        fontWeight: 500,
        lineHeight: 1.4,
        letterSpacing: '0.02857em',
        textTransform: 'none', // Sin mayúsculas automáticas
      },
      caption: {
        fontSize: 'var(--text-xs)',
        lineHeight: 1.4,
        letterSpacing: '0.03333em',
      },
    },
    shape: {
      borderRadius: parseInt(getCSSVar('--radius-md').replace('px', '')) || 8,
    },
    spacing: 4, // Base spacing unit (4px)
    transitions,
    shadows: [
      'none',
      'var(--shadow-xs)',
      'var(--shadow-sm)',
      'var(--shadow-md)',
      'var(--shadow-lg)',
      'var(--shadow-xl)',
      'var(--shadow-2xl)',
      // Más sombras utilizando las variables CSS
      'var(--shadow-lg)',
      'var(--shadow-xl)',
      'var(--shadow-2xl)',
      'var(--shadow-2xl)',
      'var(--shadow-2xl)',
      'var(--shadow-2xl)',
      'var(--shadow-2xl)',
      'var(--shadow-2xl)',
      'var(--shadow-2xl)',
      'var(--shadow-2xl)',
      'var(--shadow-2xl)',
      'var(--shadow-2xl)',
      'var(--shadow-2xl)',
      'var(--shadow-2xl)',
      'var(--shadow-2xl)',
      'var(--shadow-2xl)',
      'var(--shadow-2xl)',
      'var(--shadow-2xl)',
    ],
    components: {
      // Personalización de componentes
      MuiButton: {
        styleOverrides: {
          root: {
            borderRadius: 'var(--radius-md)',
            minHeight: 'var(--control-height-normal)',
            fontWeight: 500,
            fontSize: 'var(--text-sm)',
            transition: `all ${transitions.duration.short}ms ${transitions.easing.easeInOut}`,
            '&:focus-visible': {
              outline: '2px solid currentColor',
              outlineOffset: '2px',
            },
          },
          sizeSmall: {
            minHeight: 'var(--control-height-compact)',
            fontSize: 'var(--text-xs)',
            padding: '6px 12px',
          },
          sizeLarge: {
            minHeight: 'var(--control-height-large)',
            fontSize: 'var(--text-base)',
            padding: '12px 24px',
          },
        },
      },
      MuiTextField: {
        styleOverrides: {
          root: {
            '& .MuiOutlinedInput-root': {
              borderRadius: 'var(--radius-md)',
              minHeight: 'var(--control-height-normal)',
              transition: `all ${transitions.duration.short}ms ${transitions.easing.easeInOut}`,
              '&:focus-within': {
                outline: '2px solid currentColor',
                outlineOffset: '2px',
              },
            },
          },
        },
      },
      MuiCard: {
        styleOverrides: {
          root: {
            borderRadius: 'var(--radius-lg)',
            backgroundColor: backgroundPaper,
            boxShadow: 'var(--shadow-sm)',
            border: `1px solid ${alpha(textPrimary, 0.1)}`,
          },
        },
      },
      MuiPaper: {
        styleOverrides: {
          root: {
            borderRadius: 'var(--radius-md)',
            backgroundColor: backgroundPaper,
          },
          elevation1: {
            boxShadow: 'var(--shadow-sm)',
          },
          elevation2: {
            boxShadow: 'var(--shadow-md)',
          },
          elevation3: {
            boxShadow: 'var(--shadow-lg)',
          },
        },
      },
      MuiDialog: {
        styleOverrides: {
          paper: {
            borderRadius: 'var(--radius-xl)',
            boxShadow: 'var(--shadow-2xl)',
          },
        },
      },
      // Modo Focus: reducir densidad
      ...(isFocusMode && {
        MuiButton: {
          styleOverrides: {
            root: {
              minHeight: 'var(--control-height-large)',
              padding: '12px 24px',
            },
          },
        },
        MuiTextField: {
          styleOverrides: {
            root: {
              '& .MuiOutlinedInput-root': {
                minHeight: 'var(--control-height-large)',
              },
            },
          },
        },
      }),
    },
  })
}

// Contexto para gestión del tema
interface AppThemeContextValue {
  isDark: boolean
  isFocusMode: boolean
  isEcoMode: boolean
  toggleTheme: () => void
  toggleFocusMode: () => void
  toggleEcoMode: () => void
}

const AppThemeContext = React.createContext<AppThemeContextValue | null>(null)

export const useAppTheme = (): AppThemeContextValue => {
  const context = React.useContext(AppThemeContext)
  if (!context) {
    throw new Error('useAppTheme must be used within AppThemeProvider')
  }
  return context
}

// Provider principal del tema
interface AppThemeProviderProps {
  children: ReactNode
}

export const AppThemeProvider: React.FC<AppThemeProviderProps> = ({ children }) => {
  const { isDark, isFocusMode, isEcoMode } = useThemeDetection()

  const theme = useMemo(
    () => createAppTheme(isDark, isFocusMode, isEcoMode),
    [isDark, isFocusMode, isEcoMode]
  )

  const toggleTheme = () => {
    const newTheme = isDark ? 'light' : 'dark'
    document.documentElement.setAttribute('data-theme', newTheme)
    localStorage.setItem('theme', newTheme)
  }

  const toggleFocusMode = () => {
    const newMode = isFocusMode ? 'off' : 'on'
    document.documentElement.setAttribute('data-focus-mode', newMode)
    localStorage.setItem('focus-mode', newMode)
  }

  const toggleEcoMode = () => {
    const newMode = isEcoMode ? 'off' : 'on'
    document.documentElement.setAttribute('data-eco-mode', newMode)
    localStorage.setItem('eco-mode', newMode)
  }

  const contextValue: AppThemeContextValue = useMemo(() => ({
    isDark,
    isFocusMode,
    isEcoMode,
    toggleTheme,
    toggleFocusMode,
    toggleEcoMode,
  }), [isDark, isFocusMode, isEcoMode])

  return (
    <AppThemeContext.Provider value={contextValue}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        {children}
      </ThemeProvider>
    </AppThemeContext.Provider>
  )
}

// Hook para inicializar tema desde localStorage
export const useInitializeTheme = () => {
  useEffect(() => {
    // Detectar preferencia del sistema
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches

    // Leer configuración guardada o usar preferencia del sistema
    const savedTheme = localStorage.getItem('theme') || (prefersDark ? 'dark' : 'light')
    const savedFocusMode = localStorage.getItem('focus-mode') || 'off'
    const savedEcoMode = localStorage.getItem('eco-mode') || 'off'

    // Aplicar configuración
    document.documentElement.setAttribute('data-theme', savedTheme)
    document.documentElement.setAttribute('data-focus-mode', savedFocusMode)
    document.documentElement.setAttribute('data-eco-mode', savedEcoMode)

    // Escuchar cambios en la preferencia del sistema
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
    const handleChange = (e: MediaQueryListEvent) => {
      if (!localStorage.getItem('theme')) {
        document.documentElement.setAttribute('data-theme', e.matches ? 'dark' : 'light')
      }
    }

    mediaQuery.addEventListener('change', handleChange)
    return () => mediaQuery.removeEventListener('change', handleChange)
  }, [])
}

export default AppThemeProvider
