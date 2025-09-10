import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from './Layout';
import Challenges from '../pages/Challenges';
import Dashboard from '../pages/Dashboard';
import CreateChallenge from '../pages/CreateChallenge';
import ValidatorPanel from '../pages/ValidatorPanel';
import Account from '../pages/Account';
import Plans from '../pages/Plans';
import Login from '../pages/Login';
import Register from '../pages/Register';
import ChallengeDetail from '../pages/ChallengeDetail';
import PrivateRoute from './PrivateRoute'

const AppRouter: React.FC = () => {
  // PrivateRoute handles auth redirection

  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/challenges" element={<Challenges />} />
        <Route path="/challenges/:id" element={<ChallengeDetail />} />
  <Route path="/challenges/create" element={<PrivateRoute><CreateChallenge /></PrivateRoute>} />
  <Route path="/account" element={<PrivateRoute><Account /></PrivateRoute>} />
        <Route path="/plans" element={<Plans />} />
  <Route path="/validator" element={<PrivateRoute><ValidatorPanel /></PrivateRoute>} />
        <Route path="*" element={<Navigate to='/' replace />} />
      </Routes>
    </Layout>
  );
};

export default AppRouter;
