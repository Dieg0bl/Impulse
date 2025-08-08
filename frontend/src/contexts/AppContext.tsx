import React, { createContext, useContext, useReducer, ReactNode } from 'react';

// ====================================== 
// TIPOS Y INTERFACES PRINCIPALES
// ====================================== 

export interface Usuario {
  id: string;
  email: string;
  nombre: string;
  apellidos: string;
  telefono?: string;
  avatar?: string;
  rol: 'USUARIO' | 'VALIDADOR' | 'ADMIN';
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
  fechaRegistro: string;
  ultimoAcceso: string;
  estado: 'ACTIVO' | 'PENDIENTE_VERIFICACION' | 'SUSPENDIDO';
}

export interface Reto {
  id: string;
  usuarioId: string;
  titulo: string;
  descripcion: string;
  categoria: 'SALUD' | 'EDUCACION' | 'TRABAJO' | 'PERSONAL' | 'DEPORTE' | 'HABITOS' | 'OTRO';
  dificultad: 'FACIL' | 'MEDIO' | 'DIFICIL' | 'EXTREMO';
  fechaInicio: string;
  fechaFin: string;
  estado: 'BORRADOR' | 'ACTIVO' | 'PAUSADO' | 'COMPLETADO' | 'FALLIDO' | 'CANCELADO';
  progreso: number; // 0-100
  validadores: string[]; // IDs de usuarios validadores
  reportes: ReporteAvance[];
  recompensas: {
    puntos: number;
    badges: string[];
    consecuencias?: string;
  };
  configuracion: {
    requiereEvidencia: boolean;
    tipoEvidencia: 'TEXTO' | 'FOTO' | 'VIDEO' | 'ARCHIVO';
    frecuenciaReporte: 'DIARIO' | 'SEMANAL' | 'QUINCENAL' | 'MENSUAL';
    validacionMinima: number; // mínimo de validadores que deben aprobar
  };
  fechaCreacion: string;
  fechaActualizacion: string;
}

export interface ReporteAvance {
  id: string;
  retoId: string;
  usuarioId: string;
  descripcion: string;
  evidencia?: {
    tipo: 'TEXTO' | 'FOTO' | 'VIDEO' | 'ARCHIVO';
    contenido: string; // URL o contenido según tipo
    metadata?: any;
  };
  fecha: string;
  validaciones: ValidacionReporte[];
  estado: 'PENDIENTE' | 'APROBADO' | 'RECHAZADO' | 'EN_REVISION';
}

export interface ValidacionReporte {
  id: string;
  reporteId: string;
  validadorId: string;
  estado: 'PENDIENTE' | 'APROBADO' | 'RECHAZADO';
  comentario?: string;
  fecha?: string;
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

// ====================================== 
// ESTADO DE LA APLICACIÓN
// ====================================== 

export interface AppState {
  // Autenticación
  isAuthenticated: boolean;
  currentUser: Usuario | null;
  
  // Navegación
  currentPage: string;
  previousPage: string;
  
  // Datos principales
  retos: Reto[];
  notificaciones: Notificacion[];
  validadores: Usuario[];
  
  // Estados de carga
  loading: {
    auth: boolean;
    retos: boolean;
    validadores: boolean;
    reportes: boolean;
  };
  
  // Errores
  errors: {
    auth?: string;
    retos?: string;
    validadores?: string;
    reportes?: string;
    general?: string;
  };
  
  // Filtros y búsquedas
  filters: {
    retosCategoria?: string;
    retosDificultad?: string;
    retosEstado?: string;
    validadoresBusqueda?: string;
  };
  
  // Modales y UI
  modals: {
    createReto: boolean;
    editProfile: boolean;
    reportProgress: boolean;
    selectValidators: boolean;
    notifications: boolean;
  };
  
  // Datos temporales
  selectedReto: Reto | null;
  selectedReporte: ReporteAvance | null;
}

// ====================================== 
// ACCIONES
// ====================================== 

export type AppAction = 
  // Autenticación
  | { type: 'AUTH_START' }
  | { type: 'AUTH_SUCCESS'; payload: Usuario }
  | { type: 'AUTH_FAILURE'; payload: string }
  | { type: 'LOGOUT' }
  
  // Navegación
  | { type: 'NAVIGATE'; payload: string }
  | { type: 'GO_BACK' }
  
  // Retos
  | { type: 'LOAD_RETOS_START' }
  | { type: 'LOAD_RETOS_SUCCESS'; payload: Reto[] }
  | { type: 'LOAD_RETOS_FAILURE'; payload: string }
  | { type: 'CREATE_RETO_SUCCESS'; payload: Reto }
  | { type: 'UPDATE_RETO_SUCCESS'; payload: Reto }
  | { type: 'DELETE_RETO_SUCCESS'; payload: string }
  | { type: 'SELECT_RETO'; payload: Reto | null }
  
  // Reportes
  | { type: 'ADD_REPORTE_SUCCESS'; payload: { retoId: string; reporte: ReporteAvance } }
  | { type: 'UPDATE_REPORTE_SUCCESS'; payload: ReporteAvance }
  | { type: 'SELECT_REPORTE'; payload: ReporteAvance | null }
  
  // Validadores
  | { type: 'LOAD_VALIDADORES_SUCCESS'; payload: Usuario[] }
  | { type: 'LOAD_VALIDADORES_FAILURE'; payload: string }
  
  // Notificaciones
  | { type: 'LOAD_NOTIFICACIONES_SUCCESS'; payload: Notificacion[] }
  | { type: 'MARK_NOTIFICATION_READ'; payload: string }
  | { type: 'ADD_NOTIFICATION'; payload: Notificacion }
  
  // Modales
  | { type: 'TOGGLE_MODAL'; payload: { modal: keyof AppState['modals']; open?: boolean } }
  
  // Filtros
  | { type: 'SET_FILTER'; payload: { key: keyof AppState['filters']; value: any } }
  | { type: 'CLEAR_FILTERS' }
  
  // Errores
  | { type: 'SET_ERROR'; payload: { key: keyof AppState['errors']; value: string } }
  | { type: 'CLEAR_ERROR'; payload: keyof AppState['errors'] }
  | { type: 'CLEAR_ALL_ERRORS' }
  
  // Perfil
  | { type: 'UPDATE_PROFILE_SUCCESS'; payload: Partial<Usuario> };

// ====================================== 
// ESTADO INICIAL
// ====================================== 

const initialState: AppState = {
  isAuthenticated: false,
  currentUser: null,
  currentPage: 'home',
  previousPage: 'home',
  retos: [],
  notificaciones: [],
  validadores: [],
  loading: {
    auth: false,
    retos: false,
    validadores: false,
    reportes: false,
  },
  errors: {},
  filters: {},
  modals: {
    createReto: false,
    editProfile: false,
    reportProgress: false,
    selectValidators: false,
    notifications: false,
  },
  selectedReto: null,
  selectedReporte: null,
};

// ====================================== 
// REDUCER
// ====================================== 

function appReducer(state: AppState, action: AppAction): AppState {
  switch (action.type) {
    // Autenticación
    case 'AUTH_START':
      return {
        ...state,
        loading: { ...state.loading, auth: true },
        errors: { ...state.errors, auth: undefined }
      };
      
    case 'AUTH_SUCCESS':
      return {
        ...state,
        isAuthenticated: true,
        currentUser: action.payload,
        loading: { ...state.loading, auth: false },
        errors: { ...state.errors, auth: undefined }
      };
      
    case 'AUTH_FAILURE':
      return {
        ...state,
        isAuthenticated: false,
        currentUser: null,
        loading: { ...state.loading, auth: false },
        errors: { ...state.errors, auth: action.payload }
      };
      
    case 'LOGOUT':
      return {
        ...initialState,
        currentPage: 'home'
      };
      
    // Navegación
    case 'NAVIGATE':
      return {
        ...state,
        previousPage: state.currentPage,
        currentPage: action.payload
      };
      
    case 'GO_BACK':
      return {
        ...state,
        currentPage: state.previousPage,
        previousPage: state.currentPage
      };
      
    // Retos
    case 'LOAD_RETOS_START':
      return {
        ...state,
        loading: { ...state.loading, retos: true },
        errors: { ...state.errors, retos: undefined }
      };
      
    case 'LOAD_RETOS_SUCCESS':
      return {
        ...state,
        retos: action.payload,
        loading: { ...state.loading, retos: false }
      };
      
    case 'LOAD_RETOS_FAILURE':
      return {
        ...state,
        loading: { ...state.loading, retos: false },
        errors: { ...state.errors, retos: action.payload }
      };
      
    case 'CREATE_RETO_SUCCESS':
      return {
        ...state,
        retos: [...state.retos, action.payload]
      };
      
    case 'UPDATE_RETO_SUCCESS':
      return {
        ...state,
        retos: state.retos.map(reto => 
          reto.id === action.payload.id ? action.payload : reto
        ),
        selectedReto: state.selectedReto?.id === action.payload.id ? action.payload : state.selectedReto
      };
      
    case 'DELETE_RETO_SUCCESS':
      return {
        ...state,
        retos: state.retos.filter(reto => reto.id !== action.payload),
        selectedReto: state.selectedReto?.id === action.payload ? null : state.selectedReto
      };
      
    case 'SELECT_RETO':
      return {
        ...state,
        selectedReto: action.payload
      };
      
    // Reportes
    case 'ADD_REPORTE_SUCCESS':
      return {
        ...state,
        retos: state.retos.map(reto => 
          reto.id === action.payload.retoId 
            ? { ...reto, reportes: [...reto.reportes, action.payload.reporte] }
            : reto
        )
      };
      
    case 'UPDATE_REPORTE_SUCCESS':
      return {
        ...state,
        retos: state.retos.map(reto => ({
          ...reto,
          reportes: reto.reportes.map(reporte =>
            reporte.id === action.payload.id ? action.payload : reporte
          )
        })),
        selectedReporte: state.selectedReporte?.id === action.payload.id ? action.payload : state.selectedReporte
      };
      
    case 'SELECT_REPORTE':
      return {
        ...state,
        selectedReporte: action.payload
      };
      
    // Validadores
    case 'LOAD_VALIDADORES_SUCCESS':
      return {
        ...state,
        validadores: action.payload,
        loading: { ...state.loading, validadores: false }
      };
      
    case 'LOAD_VALIDADORES_FAILURE':
      return {
        ...state,
        loading: { ...state.loading, validadores: false },
        errors: { ...state.errors, validadores: action.payload }
      };
      
    // Notificaciones
    case 'LOAD_NOTIFICACIONES_SUCCESS':
      return {
        ...state,
        notificaciones: action.payload
      };
      
    case 'MARK_NOTIFICATION_READ':
      return {
        ...state,
        notificaciones: state.notificaciones.map(notif =>
          notif.id === action.payload ? { ...notif, leida: true } : notif
        )
      };
      
    case 'ADD_NOTIFICATION':
      return {
        ...state,
        notificaciones: [action.payload, ...state.notificaciones]
      };
      
    // Modales
    case 'TOGGLE_MODAL':
      return {
        ...state,
        modals: {
          ...state.modals,
          [action.payload.modal]: action.payload.open ?? !state.modals[action.payload.modal]
        }
      };
      
    // Filtros
    case 'SET_FILTER':
      return {
        ...state,
        filters: {
          ...state.filters,
          [action.payload.key]: action.payload.value
        }
      };
      
    case 'CLEAR_FILTERS':
      return {
        ...state,
        filters: {}
      };
      
    // Errores
    case 'SET_ERROR':
      return {
        ...state,
        errors: {
          ...state.errors,
          [action.payload.key]: action.payload.value
        }
      };
      
    case 'CLEAR_ERROR':
      return {
        ...state,
        errors: {
          ...state.errors,
          [action.payload]: undefined
        }
      };
      
    case 'CLEAR_ALL_ERRORS':
      return {
        ...state,
        errors: {}
      };
      
    // Perfil
    case 'UPDATE_PROFILE_SUCCESS':
      return {
        ...state,
        currentUser: state.currentUser ? { ...state.currentUser, ...action.payload } : null
      };
      
    default:
      return state;
  }
}

// ====================================== 
// CONTEXTO Y PROVIDER
// ====================================== 

export interface AppContextType {
  state: AppState;
  dispatch: React.Dispatch<AppAction>;
  
  // Helpers de navegación
  navigate: (page: string) => void;
  goBack: () => void;
  
  // Helpers de autenticación
  login: (email: string, password: string) => Promise<void>;
  register: (userData: Partial<Usuario>) => Promise<void>;
  logout: () => void;
  
  // Helpers de retos
  createReto: (retoData: Partial<Reto>) => Promise<void>;
  updateReto: (retoId: string, updates: Partial<Reto>) => Promise<void>;
  deleteReto: (retoId: string) => Promise<void>;
  reportProgress: (retoId: string, reporteData: Partial<ReporteAvance>) => Promise<void>;
  
  // Helpers de validación
  validateReport: (reporteId: string, aprobado: boolean, comentario?: string) => Promise<void>;
  
  // Helpers de notificaciones
  markNotificationRead: (notificationId: string) => void;
  
  // Helpers de modales
  openModal: (modal: keyof AppState['modals']) => void;
  closeModal: (modal: keyof AppState['modals']) => void;
  toggleModal: (modal: keyof AppState['modals']) => void;
}

const AppContext = createContext<AppContextType | undefined>(undefined);

export const useAppContext = () => {
  const context = useContext(AppContext);
  if (context === undefined) {
    throw new Error('useAppContext must be used within an AppProvider');
  }
  return context;
};

// ====================================== 
// PROVIDER COMPONENT
// ====================================== 

interface AppProviderProps {
  children: ReactNode;
}

export const AppProvider: React.FC<AppProviderProps> = ({ children }) => {
  const [state, dispatch] = useReducer(appReducer, initialState);
  
  // ====================================== 
  // HELPERS DE NAVEGACIÓN
  // ====================================== 
  
  const navigate = (page: string) => {
    dispatch({ type: 'NAVIGATE', payload: page });
  };
  
  const goBack = () => {
    dispatch({ type: 'GO_BACK' });
  };
  
  // ====================================== 
  // HELPERS DE AUTENTICACIÓN
  // ====================================== 
  
  const login = async (email: string, password: string) => {
    dispatch({ type: 'AUTH_START' });
    
    try {
      // Simulación de API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Mock user data
      const mockUser: Usuario = {
        id: '1',
        email,
        nombre: 'Usuario',
        apellidos: 'Demo',
        rol: 'USUARIO',
        configuracion: {
          notificaciones: true,
          privacidad: 'PRIVADO',
          idioma: 'es'
        },
        estadisticas: {
          retosCompletados: 5,
          puntosTotales: 150,
          badges: ['PRINCIPIANTE', 'CONSTANTE'],
          racha: 7
        },
        fechaRegistro: new Date().toISOString(),
        ultimoAcceso: new Date().toISOString(),
        estado: 'ACTIVO'
      };
      
      dispatch({ type: 'AUTH_SUCCESS', payload: mockUser });
      navigate('dashboard');
      
    } catch (error) {
      dispatch({ type: 'AUTH_FAILURE', payload: 'Error de autenticación' });
    }
  };
  
  const register = async (userData: Partial<Usuario>) => {
    dispatch({ type: 'AUTH_START' });
    
    try {
      // Simulación de API call
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      const newUser: Usuario = {
        id: Date.now().toString(),
        email: userData.email!,
        nombre: userData.nombre!,
        apellidos: userData.apellidos!,
        telefono: userData.telefono,
        rol: 'USUARIO',
        configuracion: {
          notificaciones: true,
          privacidad: 'PRIVADO',
          idioma: 'es'
        },
        estadisticas: {
          retosCompletados: 0,
          puntosTotales: 0,
          badges: [],
          racha: 0
        },
        fechaRegistro: new Date().toISOString(),
        ultimoAcceso: new Date().toISOString(),
        estado: 'PENDIENTE_VERIFICACION'
      };
      
      dispatch({ type: 'AUTH_SUCCESS', payload: newUser });
      navigate('onboarding');
      
    } catch (error) {
      dispatch({ type: 'AUTH_FAILURE', payload: 'Error en el registro' });
    }
  };
  
  const logout = () => {
    dispatch({ type: 'LOGOUT' });
    navigate('home');
  };
  
  // ====================================== 
  // HELPERS DE RETOS
  // ====================================== 
  
  const createReto = async (retoData: Partial<Reto>) => {
    try {
      // Simulación de API call
      await new Promise(resolve => setTimeout(resolve, 800));
      
      const newReto: Reto = {
        id: Date.now().toString(),
        usuarioId: state.currentUser!.id,
        titulo: retoData.titulo!,
        descripcion: retoData.descripcion!,
        categoria: retoData.categoria || 'PERSONAL',
        dificultad: retoData.dificultad || 'MEDIO',
        fechaInicio: retoData.fechaInicio || new Date().toISOString(),
        fechaFin: retoData.fechaFin!,
        estado: 'ACTIVO',
        progreso: 0,
        validadores: retoData.validadores || [],
        reportes: [],
        recompensas: {
          puntos: getDificultadPuntos(retoData.dificultad || 'MEDIO'),
          badges: []
        },
        configuracion: {
          requiereEvidencia: true,
          tipoEvidencia: 'FOTO',
          frecuenciaReporte: 'SEMANAL',
          validacionMinima: 1
        },
        fechaCreacion: new Date().toISOString(),
        fechaActualizacion: new Date().toISOString()
      };
      
      dispatch({ type: 'CREATE_RETO_SUCCESS', payload: newReto });
      
    } catch (error) {
      dispatch({ type: 'SET_ERROR', payload: { key: 'retos', value: 'Error creando reto' } });
    }
  };
  
  const updateReto = async (retoId: string, updates: Partial<Reto>) => {
    try {
      const retoExistente = state.retos.find(r => r.id === retoId);
      if (!retoExistente) throw new Error('Reto no encontrado');
      
      const retoActualizado: Reto = {
        ...retoExistente,
        ...updates,
        fechaActualizacion: new Date().toISOString()
      };
      
      dispatch({ type: 'UPDATE_RETO_SUCCESS', payload: retoActualizado });
      
    } catch (error) {
      dispatch({ type: 'SET_ERROR', payload: { key: 'retos', value: 'Error actualizando reto' } });
    }
  };
  
  const deleteReto = async (retoId: string) => {
    try {
      await new Promise(resolve => setTimeout(resolve, 500));
      dispatch({ type: 'DELETE_RETO_SUCCESS', payload: retoId });
    } catch (error) {
      dispatch({ type: 'SET_ERROR', payload: { key: 'retos', value: 'Error eliminando reto' } });
    }
  };
  
  const reportProgress = async (retoId: string, reporteData: Partial<ReporteAvance>) => {
    try {
      const reto = state.retos.find(r => r.id === retoId);
      if (!reto) throw new Error('Reto no encontrado');
      
      const newReporte: ReporteAvance = {
        id: Date.now().toString(),
        retoId,
        usuarioId: state.currentUser!.id,
        descripcion: reporteData.descripcion!,
        evidencia: reporteData.evidencia,
        fecha: new Date().toISOString(),
        validaciones: reto.validadores.map(validadorId => ({
          id: `${Date.now()}-${validadorId}`,
          reporteId: Date.now().toString(),
          validadorId,
          estado: 'PENDIENTE' as const
        })),
        estado: 'PENDIENTE'
      };
      
      dispatch({ type: 'ADD_REPORTE_SUCCESS', payload: { retoId, reporte: newReporte } });
      
    } catch (error) {
      dispatch({ type: 'SET_ERROR', payload: { key: 'reportes', value: 'Error reportando progreso' } });
    }
  };
  
  // ====================================== 
  // HELPERS DE VALIDACIÓN
  // ====================================== 
  
  const validateReport = async (reporteId: string, aprobado: boolean, comentario?: string) => {
    try {
      // Encontrar el reporte en los retos
      let reporteEncontrado: ReporteAvance | null = null;
      
      for (const reto of state.retos) {
        const reporte = reto.reportes.find(r => r.id === reporteId);
        if (reporte) {
          reporteEncontrado = reporte;
          break;
        }
      }
      
      if (!reporteEncontrado) throw new Error('Reporte no encontrado');
      
      const validacionActualizada = reporteEncontrado.validaciones.map(v => 
        v.validadorId === state.currentUser!.id
          ? { 
              ...v, 
              estado: aprobado ? 'APROBADO' as const : 'RECHAZADO' as const,
              comentario,
              fecha: new Date().toISOString()
            }
          : v
      );
      
      const reporteActualizado: ReporteAvance = {
        ...reporteEncontrado,
        validaciones: validacionActualizada,
        estado: determinarEstadoReporte(validacionActualizada)
      };
      
      dispatch({ type: 'UPDATE_REPORTE_SUCCESS', payload: reporteActualizado });
      
    } catch (error) {
      dispatch({ type: 'SET_ERROR', payload: { key: 'reportes', value: 'Error validando reporte' } });
    }
  };
  
  // ====================================== 
  // HELPERS DE NOTIFICACIONES
  // ====================================== 
  
  const markNotificationRead = (notificationId: string) => {
    dispatch({ type: 'MARK_NOTIFICATION_READ', payload: notificationId });
  };
  
  // ====================================== 
  // HELPERS DE MODALES
  // ====================================== 
  
  const openModal = (modal: keyof AppState['modals']) => {
    dispatch({ type: 'TOGGLE_MODAL', payload: { modal, open: true } });
  };
  
  const closeModal = (modal: keyof AppState['modals']) => {
    dispatch({ type: 'TOGGLE_MODAL', payload: { modal, open: false } });
  };
  
  const toggleModal = (modal: keyof AppState['modals']) => {
    dispatch({ type: 'TOGGLE_MODAL', payload: { modal } });
  };
  
  // ====================================== 
  // HELPERS UTILITARIOS
  // ====================================== 
  
  const getDificultadPuntos = (dificultad: string) => {
    const puntos = { 'FACIL': 10, 'MEDIO': 25, 'DIFICIL': 50, 'EXTREMO': 100 };
    return puntos[dificultad as keyof typeof puntos] || 10;
  };
  
  const determinarEstadoReporte = (validaciones: ValidacionReporte[]): 'PENDIENTE' | 'APROBADO' | 'RECHAZADO' | 'EN_REVISION' => {
    const aprobadas = validaciones.filter(v => v.estado === 'APROBADO').length;
    const rechazadas = validaciones.filter(v => v.estado === 'RECHAZADO').length;
    const pendientes = validaciones.filter(v => v.estado === 'PENDIENTE').length;
    
    if (rechazadas > 0) return 'RECHAZADO';
    if (aprobadas >= 1 && pendientes === 0) return 'APROBADO'; // Al menos 1 aprobación y ninguna pendiente
    if (aprobadas > 0 || rechazadas > 0) return 'EN_REVISION';
    return 'PENDIENTE';
  };
  
  // ====================================== 
  // VALOR DEL CONTEXTO
  // ====================================== 
  
  const contextValue: AppContextType = {
    state,
    dispatch,
    navigate,
    goBack,
    login,
    register,
    logout,
    createReto,
    updateReto,
    deleteReto,
    reportProgress,
    validateReport,
    markNotificationRead,
    openModal,
    closeModal,
    toggleModal,
  };
  
  return (
    <AppContext.Provider value={contextValue}>
      {children}
    </AppContext.Provider>
  );
};
