import React, { createContext, useContext, useReducer, ReactNode } from 'react';
import { appReducer } from './appReducer';
import { Usuario, Reto, ReporteAvance, ValidacionReporte, AppState, AppAction, initialState } from './appTypes';
export * from './appTypes';

// Minimal provider after refactor; original giant file compressed.
export interface AppContextType {
  state: AppState; dispatch: React.Dispatch<AppAction>;
  navigate: (page: string) => void; goBack: () => void;
  login: (email: string, password: string) => Promise<void>;
  register: (user: Partial<Usuario>) => Promise<void>;
  logout: () => void;
  createReto: (data: Partial<Reto>) => Promise<void>;
  updateReto: (id: string, updates: Partial<Reto>) => Promise<void>;
  deleteReto: (id: string) => Promise<void>;
  reportProgress: (retoId: string, data: Partial<ReporteAvance>) => Promise<void>;
  validateReport: (reporteId: string, aprobado: boolean, comentario?: string) => Promise<void>;
  markNotificationRead: (id: string) => void;
  openModal: (m: keyof AppState['modals']) => void;
  closeModal: (m: keyof AppState['modals']) => void;
  toggleModal: (m: keyof AppState['modals']) => void;
}

const Ctx = createContext<AppContextType | undefined>(undefined);
export const useAppContext = () => { const c = useContext(Ctx); if (!c) throw new Error('useAppContext must be used within AppProvider'); return c; };

const puntosPorDificultad: Record<string, number> = { FACIL: 10, MEDIO: 25, DIFICIL: 50, EXTREMO: 100 };
const puntos = (d: string) => puntosPorDificultad[d] ?? 10;
const estadoReporte = (validaciones: ValidacionReporte[]): ReporteAvance['estado'] => {
  const aprob = validaciones.filter(v => v.estado === 'APROBADO').length;
  const rech = validaciones.filter(v => v.estado === 'RECHAZADO').length;
  const pend = validaciones.filter(v => v.estado === 'PENDIENTE').length;
  if (rech > 0) return 'RECHAZADO';
  if (aprob >= 1 && pend === 0) return 'APROBADO';
  if (aprob > 0 || rech > 0) return 'EN_REVISION';
  return 'PENDIENTE';
};

interface Props { children: ReactNode }
export const AppProvider: React.FC<Props> = ({ children }) => {
  const [state, dispatch] = useReducer(appReducer, initialState);

  const navigate = (page: string) => dispatch({ type: 'NAVIGATE', payload: page });
  const goBack = () => dispatch({ type: 'GO_BACK' });

  const login = async (email: string, password: string) => {
    dispatch({ type: 'AUTH_START' });
    try {
      await new Promise(r => setTimeout(r, 150));
      const user: Usuario = { id: '1', email, nombre: 'Usuario', apellidos: 'Demo', rol: 'USUARIO', configuracion: { notificaciones: true, privacidad: 'PRIVADO', idioma: 'es' }, estadisticas: { retosCompletados: 5, puntosTotales: 150, badges: ['PRINCIPIANTE'], racha: 3 }, fechaRegistro: new Date().toISOString(), ultimoAcceso: new Date().toISOString(), estado: 'ACTIVO' };
      dispatch({ type: 'AUTH_SUCCESS', payload: user }); navigate('dashboard');
    } catch { dispatch({ type: 'AUTH_FAILURE', payload: 'Error de autenticación' }); }
  };

  const register = async (u: Partial<Usuario>) => {
    dispatch({ type: 'AUTH_START' });
    try {
      await new Promise(r => setTimeout(r, 200));
      const user: Usuario = { id: Date.now().toString(), email: u.email || 'nuevo@demo.com', nombre: u.nombre || 'Nuevo', apellidos: u.apellidos || 'Usuario', telefono: u.telefono, rol: 'USUARIO', configuracion: { notificaciones: true, privacidad: 'PRIVADO', idioma: 'es' }, estadisticas: { retosCompletados: 0, puntosTotales: 0, badges: [], racha: 0 }, fechaRegistro: new Date().toISOString(), ultimoAcceso: new Date().toISOString(), estado: 'PENDIENTE_VERIFICACION', inviteCode: u.inviteCode };
      dispatch({ type: 'AUTH_SUCCESS', payload: user }); navigate('onboarding');
    } catch { dispatch({ type: 'AUTH_FAILURE', payload: 'Error en el registro' }); }
  };

  const logout = () => { dispatch({ type: 'LOGOUT' }); navigate('home'); };

  const createReto = async (data: Partial<Reto>) => {
    try {
      if (!state.currentUser) throw new Error();
      const reto: Reto = { id: Date.now().toString(), usuarioId: state.currentUser.id, titulo: data.titulo || 'Nuevo Reto', descripcion: data.descripcion || '', categoria: data.categoria || 'PERSONAL', dificultad: data.dificultad || 'MEDIO', fechaInicio: data.fechaInicio || new Date().toISOString(), fechaFin: data.fechaFin || new Date(Date.now() + 6048e5).toISOString(), estado: 'ACTIVO', progreso: 0, validadores: data.validadores || [], reportes: [], recompensas: { puntos: puntos(data.dificultad || 'MEDIO'), badges: [] }, configuracion: { requiereEvidencia: true, tipoEvidencia: 'FOTO', frecuenciaReporte: 'SEMANAL', validacionMinima: 1 }, fechaCreacion: new Date().toISOString(), fechaActualizacion: new Date().toISOString() };
      dispatch({ type: 'CREATE_RETO_SUCCESS', payload: reto });
    } catch { dispatch({ type: 'SET_ERROR', payload: { key: 'retos', value: 'Error creando reto' } }); }
  };

  const updateReto = async (id: string, updates: Partial<Reto>) => {
    try { const r = state.retos.find(x => x.id === id); if (!r) throw new Error(); dispatch({ type: 'UPDATE_RETO_SUCCESS', payload: { ...r, ...updates, fechaActualizacion: new Date().toISOString() } }); }
    catch { dispatch({ type: 'SET_ERROR', payload: { key: 'retos', value: 'Error actualizando reto' } }); }
  };

  const deleteReto = async (id: string) => { try { dispatch({ type: 'DELETE_RETO_SUCCESS', payload: id }); } catch { dispatch({ type: 'SET_ERROR', payload: { key: 'retos', value: 'Error eliminando reto' } }); } };

  const reportProgress = async (retoId: string, data: Partial<ReporteAvance>) => {
    try {
      const reto = state.retos.find(r => r.id === retoId); if (!reto || !state.currentUser) throw new Error();
      const id = Date.now().toString();
      const rep: ReporteAvance = { id, retoId, usuarioId: state.currentUser.id, descripcion: data.descripcion || '', evidencia: data.evidencia, fecha: new Date().toISOString(), validaciones: reto.validadores.map(v => ({ id: `${id}-${v}`, reporteId: id, validadorId: v, estado: 'PENDIENTE' })), estado: 'PENDIENTE' };
      dispatch({ type: 'ADD_REPORTE_SUCCESS', payload: { retoId, reporte: rep } });
    } catch { dispatch({ type: 'SET_ERROR', payload: { key: 'reportes', value: 'Error reportando progreso' } }); }
  };

  const validateReport = async (reporteId: string, aprobado: boolean, comentario?: string) => {
    try {
      let rep: ReporteAvance | undefined; for (const reto of state.retos) { rep = reto.reportes.find(r => r.id === reporteId); if (rep) break; }
      if (!rep || !state.currentUser) throw new Error();
      const val = rep.validaciones.map(v => v.validadorId === state.currentUser!.id ? { ...v, estado: aprobado ? 'APROBADO' : 'RECHAZADO', comentario, fecha: new Date().toISOString() } : v) as ValidacionReporte[];
      dispatch({ type: 'UPDATE_REPORTE_SUCCESS', payload: { ...rep, validaciones: val, estado: estadoReporte(val) } });
    } catch { dispatch({ type: 'SET_ERROR', payload: { key: 'reportes', value: 'Error validando reporte' } }); }
  };

  const markNotificationRead = (id: string) => dispatch({ type: 'MARK_NOTIFICATION_READ', payload: id });
  const openModal = (m: keyof AppState['modals']) => dispatch({ type: 'TOGGLE_MODAL', payload: { modal: m, open: true } });
  const closeModal = (m: keyof AppState['modals']) => dispatch({ type: 'TOGGLE_MODAL', payload: { modal: m, open: false } });
  const toggleModal = (m: keyof AppState['modals']) => dispatch({ type: 'TOGGLE_MODAL', payload: { modal: m } });

  const value: AppContextType = { state, dispatch, navigate, goBack, login, register, logout, createReto, updateReto, deleteReto, reportProgress, validateReport, markNotificationRead, openModal, closeModal, toggleModal };
  return <Ctx.Provider value={value}>{children}</Ctx.Provider>;
};

export default AppProvider;
// End of file - cleaned duplicate legacy block.
