// Centralized type definitions and initial state for AppContext (extracted to reduce file size)
export interface Usuario {
  id: string;
  email: string;
  nombre: string;
  apellidos: string;
  telefono?: string;
  avatar?: string;
  rol: 'USUARIO' | 'VALIDADOR' | 'ADMIN'; // Unificado con backend (roles)
  estado: 'ACTIVO' | 'INACTIVO' | 'SUSPENDIDO' | 'ELIMINADO' | 'PENDIENTE_VERIFICACION';
  fechaNacimiento?: string;
  consentimientoAceptado?: boolean;
  fechaRegistro: string;
  ultimoAcceso: string;
  configuracion: {
    notificaciones: boolean;
    privacidad: 'PUBLICO' | 'PRIVADO' | 'SOLO_VALIDADORES';
    idioma: 'es' | 'en';
  };
  estadisticas: {
    retosCompletados: number;
    puntosTotales: number;
    badges: string[];
    racha: number;
  };
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
  updatedBy?: string;
  inviteCode?: string; // código con el que se registró (growth tracking)
}

export interface ValidacionReporte {
  id: string;
  reporteId: string;
  validadorId: string;
  estado: 'PENDIENTE' | 'APROBADO' | 'RECHAZADO';
  comentario?: string;
  fecha?: string;
}

export interface ReporteAvance {
  id: string;
  retoId: string;
  usuarioId: string;
  descripcion: string;
  evidencia?: {
    tipo: 'TEXTO' | 'FOTO' | 'VIDEO' | 'ARCHIVO';
    contenido: string;
    metadata?: any;
  };
  fecha: string;
  validaciones: ValidacionReporte[];
  estado: 'PENDIENTE' | 'APROBADO' | 'RECHAZADO' | 'EN_REVISION';
}

export interface Reto {
  id: string;
  idCreador: string;
  idCategoria?: string;
  titulo: string;
  descripcion: string;
  categoria?: string; // Puede mapearse a idCategoria
  dificultad: string;
  fechaInicio: string;
  fechaFin: string;
  tipoValidacion?: string;
  esPublico?: boolean;
  requiereEvidencia: boolean;
  tipoEvidencia: string;
  frecuenciaReporte: string;
  metaObjetivo?: string;
  unidadMedida?: string;
  valorObjetivo?: number;
  estado: string;
  progreso: number;
  validadores: string[];
  reportes: ReporteAvance[];
  recompensas: {
    puntos: number;
    badges: string[];
    consecuencias?: string;
  };
  configuracion: {
    requiereEvidencia: boolean;
  tipoEvidencia: string;
  frecuenciaReporte: string;
    validacionMinima: number;
  };
  publicSlug?: string;
  slaHorasValidacion?: number;
  tipoConsecuencia?: string;
  esPlantilla?: boolean;
  visibility?: string;
  fechaCreacion: string;
  fechaActualizacion: string;
  updatedAt?: string;
}

export interface Notificacion {
  id: string;
  usuarioId: string;
  tipo: 'RETO_PENDIENTE' | 'VALIDACION_REQUERIDA' | 'REPORTE_APROBADO' | 'REPORTE_RECHAZADO' | 'RETO_COMPLETADO' | 'RETO_FALLIDO';
  titulo: string;
  mensaje: string;
  leida: boolean;
  fecha: string;
  accion?: {
    tipo: 'NAVEGAR' | 'MODAL' | 'EXTERNAL';
    destino: string;
  };
}

export interface AppState {
  isAuthenticated: boolean;
  currentUser: Usuario | null;
  currentPage: string;
  previousPage: string;
  retos: Reto[];
  notificaciones: Notificacion[];
  validadores: Usuario[];
  loading: { auth: boolean; retos: boolean; validadores: boolean; reportes: boolean };
  errors: { auth?: string; retos?: string; validadores?: string; reportes?: string; general?: string };
  filters: { retosCategoria?: string; retosDificultad?: string; retosEstado?: string; validadoresBusqueda?: string };
  modals: { createReto: boolean; editProfile: boolean; reportProgress: boolean; selectValidators: boolean; notifications: boolean };
  selectedReto: Reto | null;
  selectedReporte: ReporteAvance | null;
}

export type AppAction =
  | { type: 'AUTH_START' }
  | { type: 'AUTH_SUCCESS'; payload: Usuario }
  | { type: 'AUTH_FAILURE'; payload: string }
  | { type: 'LOGOUT' }
  | { type: 'NAVIGATE'; payload: string }
  | { type: 'GO_BACK' }
  | { type: 'LOAD_RETOS_START' }
  | { type: 'LOAD_RETOS_SUCCESS'; payload: Reto[] }
  | { type: 'LOAD_RETOS_FAILURE'; payload: string }
  | { type: 'CREATE_RETO_SUCCESS'; payload: Reto }
  | { type: 'UPDATE_RETO_SUCCESS'; payload: Reto }
  | { type: 'DELETE_RETO_SUCCESS'; payload: string }
  | { type: 'SELECT_RETO'; payload: Reto | null }
  | { type: 'ADD_REPORTE_SUCCESS'; payload: { retoId: string; reporte: ReporteAvance } }
  | { type: 'UPDATE_REPORTE_SUCCESS'; payload: ReporteAvance }
  | { type: 'SELECT_REPORTE'; payload: ReporteAvance | null }
  | { type: 'LOAD_VALIDADORES_SUCCESS'; payload: Usuario[] }
  | { type: 'LOAD_VALIDADORES_FAILURE'; payload: string }
  | { type: 'LOAD_NOTIFICACIONES_SUCCESS'; payload: Notificacion[] }
  | { type: 'MARK_NOTIFICATION_READ'; payload: string }
  | { type: 'ADD_NOTIFICATION'; payload: Notificacion }
  | { type: 'TOGGLE_MODAL'; payload: { modal: keyof AppState['modals']; open?: boolean } }
  | { type: 'SET_FILTER'; payload: { key: keyof AppState['filters']; value: any } }
  | { type: 'CLEAR_FILTERS' }
  | { type: 'SET_ERROR'; payload: { key: keyof AppState['errors']; value: string } }
  | { type: 'CLEAR_ERROR'; payload: keyof AppState['errors'] }
  | { type: 'CLEAR_ALL_ERRORS' }
  | { type: 'UPDATE_PROFILE_SUCCESS'; payload: Partial<Usuario> };

export const initialState: AppState = {
  isAuthenticated: false,
  currentUser: null,
  currentPage: 'home',
  previousPage: 'home',
  retos: [],
  notificaciones: [],
  validadores: [],
  loading: { auth: false, retos: false, validadores: false, reportes: false },
  errors: {},
  filters: {},
  modals: { createReto: false, editProfile: false, reportProgress: false, selectValidators: false, notifications: false },
  selectedReto: null,
  selectedReporte: null,
};
