import React from "react";
import { Routes, Route, Navigate, useParams } from "react-router-dom";
import Layout from "./Layout";
import Challenges from "../pages/Challenges";
import Dashboard from "../pages/Dashboard";
import CreateChallenge from "../pages/CreateChallenge";
import ValidatorPanel from "../pages/ValidatorPanel";
import Account from "../pages/Account";
import Plans from "../pages/Plans";
import Login from "../pages/Login";
import Register from "../pages/Register";
import ChallengeDetail from "../pages/ChallengeDetail";
import Validators from "../pages/Validators";
import MarketplaceDashboard from "../pages/MarketplaceDashboard";
import Evidences from "../pages/Evidences";
import EvidenceUpload from "../pages/EvidenceUpload";
import NotFound from "../pages/NotFound";
import PrivateRoute from "./PrivateRoute";
import AdminUserDashboard from "../pages/AdminUserDashboard";
import AdminAdsDashboard from "../pages/AdminAdsDashboard";
import ForgotPassword from "../pages/ForgotPassword";
import ResetPassword from "../pages/ResetPassword";
import VerifyEmail from "../pages/VerifyEmail";

// Wrapper component for EvidenceUpload that gets challengeId from route params
const EvidenceUploadWrapper: React.FC = () => {
  const { challengeId } = useParams<{ challengeId: string }>();
  if (!challengeId) return <Navigate to="/challenges" replace />;
  return <EvidenceUpload challengeId={challengeId} />;
};

const AppRouter: React.FC = () => {
  // PrivateRoute handles auth redirection

  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
  <Route path="/forgot-password" element={<ForgotPassword />} />
  <Route path="/reset-password" element={<ResetPassword />} />
  <Route path="/verify-email" element={<VerifyEmail />} />
        <Route path="/challenges" element={<Challenges />} />
        <Route path="/challenges/:id" element={<ChallengeDetail />} />
        <Route
          path="/challenges/create"
          element={
            <PrivateRoute>
              <CreateChallenge />
            </PrivateRoute>
          }
        />
        <Route
          path="/account"
          element={
            <PrivateRoute>
              <Account />
            </PrivateRoute>
          }
        />
        <Route path="/plans" element={<Plans />} />
        <Route
          path="/validator"
          element={
            <PrivateRoute>
              <ValidatorPanel />
            </PrivateRoute>
          }
        />
        <Route path="/validators" element={<Validators />} />
        <Route
          path="/marketplace"
          element={
            <PrivateRoute>
              <MarketplaceDashboard />
            </PrivateRoute>
          }
        />
        <Route
          path="/evidences"
          element={
            <PrivateRoute>
              <Evidences />
            </PrivateRoute>
          }
        />
        <Route
          path="/evidences/upload/:challengeId"
          element={
            <PrivateRoute>
              <EvidenceUploadWrapper />
            </PrivateRoute>
          }
        />
        <Route
          path="/admin/users"
          element={
            <PrivateRoute>
              <AdminUserDashboard />
            </PrivateRoute>
          }
        />
        <Route
          path="/admin/ads"
          element={
            <PrivateRoute>
              <AdminAdsDashboard />
            </PrivateRoute>
          }
        />
        <Route path="/404" element={<NotFound />} />
        <Route path="*" element={<Navigate to="/404" replace />} />
      </Routes>
    </Layout>
  );
};

export default AppRouter;
