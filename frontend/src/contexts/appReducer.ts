// Reducer extracted from AppContext to reduce file size and improve modularity
import { AppState, AppAction, initialState } from './appTypes';

export function appReducer(state: AppState, action: AppAction): AppState {
  switch (action.type) {
    case 'AUTH_START':
      return { ...state, loading: { ...state.loading, auth: true }, errors: { ...state.errors, auth: undefined } };
    case 'AUTH_SUCCESS':
      return { ...state, isAuthenticated: true, currentUser: action.payload, loading: { ...state.loading, auth: false }, errors: { ...state.errors, auth: undefined } };
    case 'AUTH_FAILURE':
      return { ...state, isAuthenticated: false, currentUser: null, loading: { ...state.loading, auth: false }, errors: { ...state.errors, auth: action.payload } };
    case 'LOGOUT':
      return { ...initialState, currentPage: 'home' };
    case 'NAVIGATE':
      return { ...state, previousPage: state.currentPage, currentPage: action.payload };
    case 'GO_BACK':
      return { ...state, currentPage: state.previousPage, previousPage: state.currentPage };
    case 'LOAD_RETOS_START':
      return { ...state, loading: { ...state.loading, retos: true }, errors: { ...state.errors, retos: undefined } };
    case 'LOAD_RETOS_SUCCESS':
      return { ...state, retos: action.payload, loading: { ...state.loading, retos: false } };
    case 'LOAD_RETOS_FAILURE':
      return { ...state, loading: { ...state.loading, retos: false }, errors: { ...state.errors, retos: action.payload } };
    case 'CREATE_RETO_SUCCESS':
      return { ...state, retos: [...state.retos, action.payload] };
    case 'UPDATE_RETO_SUCCESS':
      return { ...state, retos: state.retos.map(r => (r.id === action.payload.id ? action.payload : r)), selectedReto: state.selectedReto?.id === action.payload.id ? action.payload : state.selectedReto };
    case 'DELETE_RETO_SUCCESS':
      return { ...state, retos: state.retos.filter(r => r.id !== action.payload), selectedReto: state.selectedReto?.id === action.payload ? null : state.selectedReto };
    case 'SELECT_RETO':
      return { ...state, selectedReto: action.payload };
    case 'ADD_REPORTE_SUCCESS':
      return { ...state, retos: state.retos.map(r => (r.id === action.payload.retoId ? { ...r, reportes: [...r.reportes, action.payload.reporte] } : r)) };
    case 'UPDATE_REPORTE_SUCCESS':
      return { ...state, retos: state.retos.map(r => ({ ...r, reportes: r.reportes.map(rep => (rep.id === action.payload.id ? action.payload : rep)) })), selectedReporte: state.selectedReporte?.id === action.payload.id ? action.payload : state.selectedReporte };
    case 'SELECT_REPORTE':
      return { ...state, selectedReporte: action.payload };
    case 'LOAD_VALIDADORES_SUCCESS':
      return { ...state, validadores: action.payload, loading: { ...state.loading, validadores: false } };
    case 'LOAD_VALIDADORES_FAILURE':
      return { ...state, loading: { ...state.loading, validadores: false }, errors: { ...state.errors, validadores: action.payload } };
    case 'LOAD_NOTIFICACIONES_SUCCESS':
      return { ...state, notificaciones: action.payload };
    case 'MARK_NOTIFICATION_READ':
      return { ...state, notificaciones: state.notificaciones.map(n => (n.id === action.payload ? { ...n, leida: true } : n)) };
    case 'ADD_NOTIFICATION':
      return { ...state, notificaciones: [action.payload, ...state.notificaciones] };
    case 'TOGGLE_MODAL':
      return { ...state, modals: { ...state.modals, [action.payload.modal]: action.payload.open ?? !state.modals[action.payload.modal] } };
    case 'SET_FILTER':
      return { ...state, filters: { ...state.filters, [action.payload.key]: action.payload.value } };
    case 'CLEAR_FILTERS':
      return { ...state, filters: {} };
    case 'SET_ERROR':
      return { ...state, errors: { ...state.errors, [action.payload.key]: action.payload.value } };
    case 'CLEAR_ERROR':
      return { ...state, errors: { ...state.errors, [action.payload]: undefined } };
    case 'CLEAR_ALL_ERRORS':
      return { ...state, errors: {} };
    case 'UPDATE_PROFILE_SUCCESS':
      return { ...state, currentUser: state.currentUser ? { ...state.currentUser, ...action.payload } : null };
    default:
      return state;
  }
}
