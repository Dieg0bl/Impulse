import React from "react";
import { Routes, Route, Navigate, useParams } from "react-router-dom";
import Layout from "./Layout";
import { useAuth } from "../providers/AuthProvider";
import { useFlags } from "../contexts/FlagsContext";
import { UserRole } from "../types/enums";
import PrivateRoute from "./PrivateRoute";
// Importar todas las vistas por carpeta
import Landing from "../pages/public/Landing";
import Dashboard from "../pages/core/Dashboard";
import VDP from "../pages/public/VDP";
import Changelog from "../pages/public/Changelog";
import NotFound from "../pages/public/NotFound";
import Register from "../pages/auth/Register";
import Login from "../pages/auth/Login";
import ValidationDecision from "../pages/core/ValidationDecision";
import ReportCreate from "../pages/moderation/ReportCreate";
import Appeals from "../pages/moderation/Appeals";
import DSARDelete from "../pages/privacy/DSARDelete";
import VisibilityTrail from "../pages/privacy/VisibilityTrail";
import PlansInApp from "../pages/billing/PlansInApp";
import CustomerPortal from "../pages/billing/CustomerPortal";
import Invoices from "../pages/billing/Invoices";
import MyCode from "../pages/referrals/MyCode";
import RedeemCode from "../pages/referrals/RedeemCode";
import Rewards from "../pages/referrals/Rewards";
import NPS from "../pages/feedback/NPS";
import InsightsCenter from "../pages/insights/InsightsCenter";
import Profile from "../pages/profile/Profile";
import Preferences from "../pages/settings/Preferences";
import DeleteAccount from "../pages/settings/DeleteAccount";
import HealthBanner from "../pages/system/HealthBanner";
import AuditLog from "../pages/system/AuditLog";
import Evidences from "../pages/core/Evidences";
import EvidenceDetail from "../pages/core/EvidenceDetail";
import EvidenceUpload from "../pages/core/EvidenceUpload";
import Jobs from "../pages/system/Jobs";
import Webhooks from "../pages/system/Webhooks";
import IPBlocklist from "../pages/system/IPBlocklist";
import Flags from "../pages/system/Flags";
import AddToHomePrompt from "../pages/pwa/AddToHomePrompt";
import OfflineFallback from "../pages/pwa/OfflineFallback";
import AccessDenied from "../pages/pwa/AccessDenied";
import ErrorBoundaryPage from "../pages/pwa/ErrorBoundary";
import ValidationInbox from "../pages/core/ValidationInbox";
import ModerationInbox from "../pages/moderation/ModerationInbox";
import ModerationDetail from "../pages/moderation/ModerationDetail";
import DSARExport from "../pages/privacy/DSARExport";
import Checkout from "../pages/billing/Checkout";
import Dunning from "../pages/billing/Dunning";

// Wrapper para pasar challengeId a EvidenceUpload
const EvidenceUploadWithParams: React.FC = () => {
  const { challengeId } = useParams();
  if (!challengeId || typeof challengeId !== 'string') return <Navigate to="/retos" replace />;
  return <EvidenceUpload challengeId={challengeId} />;
};

// Auxiliares para RBAC/flags
const RoleRoute = ({ roles, children }: { roles: UserRole[]; children: React.ReactNode }) => {
  const { user } = useAuth();
  // Soporta user.role o user.status como fallback
  const userRole = (user as any)?.role || (user as any)?.status;
  if (!user || !roles.includes(userRole)) return <Navigate to="/403" replace />;
  return <>{children}</>;
};


const AppRouter: React.FC = () => {
  // TODO: flags y roles reales, aquí solo ejemplo
  return (
    <Layout>
      <Routes>
  {/* Públicas */}
  <Route path="/" element={<Navigate to="/dashboard" replace />} />
  <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
        <Route path="/vdp" element={<VDP />} />
        <Route path="/changelog" element={<Changelog />} />
  <Route path="/register" element={<Register />} />
  <Route path="/login" element={<Login />} />


        <Route path="/evidencias" element={<PrivateRoute><Evidences /></PrivateRoute>} />
    <Route path="/evidencias/:id" element={<PrivateRoute><EvidenceDetail /></PrivateRoute>} />
    <Route path="/evidencias/upload/:challengeId" element={<PrivateRoute><EvidenceUploadWithParams /></PrivateRoute>} />
        <Route path="/validaciones" element={<PrivateRoute><ValidationInbox /></PrivateRoute>} />
        <Route path="/validacion/:id" element={<PrivateRoute><ValidationDecision /></PrivateRoute>} />
        {/* Moderación (roles) */}
        <Route path="/reportar" element={<PrivateRoute><ReportCreate /></PrivateRoute>} />
        <Route path="/moderacion" element={<RoleRoute roles={[UserRole.ADMIN, UserRole.VALIDATOR]}><ModerationInbox /></RoleRoute>} />
        <Route path="/moderacion/:id" element={<RoleRoute roles={[UserRole.ADMIN, UserRole.VALIDATOR]}><ModerationDetail /></RoleRoute>} />
        <Route path="/apelaciones" element={<RoleRoute roles={[UserRole.ADMIN, UserRole.VALIDATOR]}><Appeals /></RoleRoute>} />
        {/* Privacidad/GDPR */}
        <Route path="/dsar/export" element={<PrivateRoute><DSARExport /></PrivateRoute>} />
        <Route path="/dsar/delete" element={<PrivateRoute><DSARDelete /></PrivateRoute>} />
        <Route path="/visibilidad" element={<PrivateRoute><VisibilityTrail /></PrivateRoute>} />
        {/* Billing/Stripe */}
        <Route path="/planes" element={<PrivateRoute><PlansInApp /></PrivateRoute>} />
        <Route path="/checkout" element={<PrivateRoute><Checkout /></PrivateRoute>} />
        <Route path="/portal" element={<PrivateRoute><CustomerPortal /></PrivateRoute>} />
        <Route path="/facturas" element={<PrivateRoute><Invoices /></PrivateRoute>} />
        <Route path="/dunning" element={<PrivateRoute><Dunning /></PrivateRoute>} />
        {/* Referidos */}
        <Route path="/referidos/codigo" element={<PrivateRoute><MyCode /></PrivateRoute>} />
        <Route path="/referidos/canjear" element={<PrivateRoute><RedeemCode /></PrivateRoute>} />
        <Route path="/referidos/recompensas" element={<PrivateRoute><Rewards /></PrivateRoute>} />
        {/* Feedback/Insights */}
        <Route path="/nps" element={<PrivateRoute><NPS /></PrivateRoute>} />
        <Route path="/insights" element={<PrivateRoute><InsightsCenter /></PrivateRoute>} />
        {/* Perfil y ajustes */}
        <Route path="/perfil" element={<PrivateRoute><Profile /></PrivateRoute>} />
        <Route path="/preferencias" element={<PrivateRoute><Preferences /></PrivateRoute>} />
        <Route path="/borrar-cuenta" element={<PrivateRoute><DeleteAccount /></PrivateRoute>} />
        {/* Sistema/operación (roles) */}
        <Route path="/health" element={<RoleRoute roles={[UserRole.ADMIN]}><HealthBanner /></RoleRoute>} />
        <Route path="/audit" element={<RoleRoute roles={[UserRole.ADMIN]}><AuditLog /></RoleRoute>} />
        <Route path="/jobs" element={<RoleRoute roles={[UserRole.ADMIN]}><Jobs /></RoleRoute>} />
        <Route path="/webhooks" element={<RoleRoute roles={[UserRole.ADMIN]}><Webhooks /></RoleRoute>} />
        <Route path="/ipblock" element={<RoleRoute roles={[UserRole.ADMIN]}><IPBlocklist /></RoleRoute>} />
        <Route path="/flags" element={<RoleRoute roles={[UserRole.ADMIN]}><Flags /></RoleRoute>} />
        {/* PWA/estados */}
        <Route path="/add-to-home" element={<AddToHomePrompt />} />
        <Route path="/offline" element={<OfflineFallback />} />
        <Route path="/403" element={<AccessDenied />} />
        <Route path="/error" element={<ErrorBoundaryPage />} />
        {/* Catch all */}
        <Route path="*" element={<NotFound />} />
      </Routes>
    </Layout>
  );
};

export default AppRouter;
