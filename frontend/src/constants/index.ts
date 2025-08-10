/**
 * Constantes centralizadas para IMPULSE
 * Elimina magic numbers y valores hardcodeados
 */

// === API ENDPOINTS ===
export const API_ENDPOINTS = {
  BASE_URL: '/api',
  AUTH: {
    LOGIN: '/auth/login',
    REGISTER: '/auth/register',
    LOGOUT: '/auth/logout',
    REFRESH: '/auth/refresh',
    PROFILE: '/auth/profile'
  },
  RETOS: {
    BASE: '/retos',
    SECURE: '/retos/secure',
    USER_RETOS: '/retos/user',
    CREATE: '/retos/create',
    UPDATE: '/retos/update',
    DELETE: '/retos/delete',
    PROGRESS: '/retos/progress'
  },
  VALIDACIONES: {
    BASE: '/validaciones',
    PENDING: '/validaciones/pending',
    APPROVE: '/validaciones/approve',
    REJECT: '/validaciones/reject'
  },
  LOGS: '/logs'
} as const;

// === RESPONSE MESSAGES ===
export const RESPONSE_MESSAGES = {
  SUCCESS: 'success',
  ERROR: 'error',
  WARNING: 'warning',
  INFO: 'info'
} as const;

export const API_RESPONSE_KEYS = {
  SUCCESS_KEY: 'success',
  MESSAGE_KEY: 'message',
  DATA_KEY: 'data',
  ERROR_KEY: 'error'
} as const;

// === UI CONSTANTS ===
export const UI_CONSTANTS = {
  LOADING_DELAY: 300,
  DEBOUNCE_DELAY: 500,
  ANIMATION_DURATION: 200,
  TOAST_DURATION: 3000,
  MAX_FILE_SIZE: 5 * 1024 * 1024, // 5MB
  PAGINATION_SIZE: 10,
  MAX_RETRIES: 3
} as const;

// === VALIDATION RULES ===
export const VALIDATION_RULES = {
  PASSWORD: {
    MIN_LENGTH: 8,
    MAX_LENGTH: 128,
    REQUIRE_UPPERCASE: true,
    REQUIRE_LOWERCASE: true,
    REQUIRE_NUMBERS: true,
    REQUIRE_SPECIAL: true
  },
  USERNAME: {
    MIN_LENGTH: 3,
    MAX_LENGTH: 50,
    PATTERN: /^[a-zA-Z0-9_]+$/
  },
  EMAIL: {
    PATTERN: /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  },
  RETO: {
    TITLE_MIN_LENGTH: 5,
    TITLE_MAX_LENGTH: 100,
    DESCRIPTION_MIN_LENGTH: 20,
    DESCRIPTION_MAX_LENGTH: 1000
  }
} as const;

// === STATUS CODES ===
export const HTTP_STATUS = {
  OK: 200,
  CREATED: 201,
  NO_CONTENT: 204,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  CONFLICT: 409,
  INTERNAL_SERVER_ERROR: 500,
  SERVICE_UNAVAILABLE: 503
} as const;

// === LOCAL STORAGE KEYS ===
export const STORAGE_KEYS = {
  USER: 'user',
  TOKEN: 'token',
  REFRESH_TOKEN: 'refreshToken',
  PREFERENCES: 'userPreferences',
  THEME: 'theme',
  LANGUAGE: 'language',
  NAVIGATION_STATE: 'navigationState',
  CACHE_PREFIX: 'impulse_cache_'
} as const;

// === SECURITY CONSTANTS ===
export const SECURITY = {
  TOKEN_EXPIRY_BUFFER: 5 * 60 * 1000, // 5 minutos
  MAX_LOGIN_ATTEMPTS: 5,
  LOCKOUT_DURATION: 15 * 60 * 1000, // 15 minutos
  SESSION_TIMEOUT: 30 * 60 * 1000, // 30 minutos
  CSRF_HEADER: 'X-CSRF-Token'
} as const;

// === ROLES Y PERMISOS ===
export const ROLES = {
  ADMIN: 'ADMIN',
  USER: 'USER',
  VALIDATOR: 'VALIDATOR',
  MODERATOR: 'MODERATOR'
} as const;

export const PERMISSIONS = {
  CREATE_RETO: 'CREATE_RETO',
  EDIT_RETO: 'EDIT_RETO',
  DELETE_RETO: 'DELETE_RETO',
  VALIDATE_RETO: 'VALIDATE_RETO',
  VIEW_ANALYTICS: 'VIEW_ANALYTICS',
  MANAGE_USERS: 'MANAGE_USERS'
} as const;

// === ESTADO DE RETOS ===
export const RETO_STATUS = {
  DRAFT: 'DRAFT',
  ACTIVE: 'ACTIVE',
  COMPLETED: 'COMPLETED',
  CANCELLED: 'CANCELLED',
  PENDING_VALIDATION: 'PENDING_VALIDATION'
} as const;

// === TIPOS DE RETO ===
export const RETO_TYPES = {
  INDIVIDUAL: 'INDIVIDUAL',
  GROUP: 'GROUP',
  COMPETITION: 'COMPETITION',
  COLLABORATIVE: 'COLLABORATIVE'
} as const;

// === CONFIGURACIÓN DE TEMA ===
export const THEME = {
  COLORS: {
    PRIMARY: '#007bff',
    SECONDARY: '#6c757d',
    SUCCESS: '#28a745',
    DANGER: '#dc3545',
    WARNING: '#ffc107',
    INFO: '#17a2b8',
    LIGHT: '#f8f9fa',
    DARK: '#343a40'
  },
  BREAKPOINTS: {
    XS: '(max-width: 575.98px)',
    SM: '(min-width: 576px) and (max-width: 767.98px)',
    MD: '(min-width: 768px) and (max-width: 991.98px)',
    LG: '(min-width: 992px) and (max-width: 1199.98px)',
    XL: '(min-width: 1200px)'
  }
} as const;

// === CONFIGURACIÓN DE NAVEGACIÓN ===
export const NAVIGATION = {
  ROUTES: {
    HOME: '/',
    DASHBOARD: '/dashboard',
    PERFIL: '/perfil',
    RETOS: '/retos',
    MIS_RETOS: '/mis-retos',
    CREAR_RETO: '/crear-reto',
    VALIDACIONES: '/validaciones',
    ANALYTICS: '/analytics',
    CONFIGURACION: '/configuracion',
    LOGIN: '/login',
    REGISTER: '/register'
  },
  BREADCRUMB_LABELS: {
    HOME: 'Inicio',
    DASHBOARD: 'Panel de Control',
    PERFIL: 'Mi Perfil',
    RETOS: 'Retos',
    MIS_RETOS: 'Mis Retos',
    CREAR_RETO: 'Crear Reto',
    VALIDACIONES: 'Validaciones',
    ANALYTICS: 'Analíticas',
    CONFIGURACION: 'Configuración'
  }
} as const;

// === MENSAJES DE ERROR ESTÁNDAR ===
export const ERROR_MESSAGES = {
  GENERIC: 'Ha ocurrido un error inesperado',
  NETWORK: 'Error de conexión. Verifica tu internet',
  UNAUTHORIZED: 'No tienes permisos para esta acción',
  VALIDATION: 'Los datos ingresados no son válidos',
  NOT_FOUND: 'El recurso solicitado no existe',
  SERVER_ERROR: 'Error del servidor. Intenta más tarde',
  SESSION_EXPIRED: 'Tu sesión ha expirado. Inicia sesión nuevamente'
} as const;

// === MENSAJES DE ÉXITO ===
export const SUCCESS_MESSAGES = {
  RETO_CREATED: 'Reto creado exitosamente',
  RETO_UPDATED: 'Reto actualizado exitosamente',
  RETO_DELETED: 'Reto eliminado exitosamente',
  PROFILE_UPDATED: 'Perfil actualizado exitosamente',
  PROGRESS_REPORTED: 'Progreso reportado exitosamente',
  VALIDATION_COMPLETED: 'Validación completada exitosamente'
} as const;
