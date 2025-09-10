import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import UserList from './components/UserList';
import BookingForm from './components/BookingForm';
import Challenges from './pages/Challenges';
import ErrorBoundary from './components/ErrorBoundary';
import './assets/styles.css';

const App: React.FC = () => {
  return (
    <ErrorBoundary level="critical">
      <BrowserRouter>
        <Layout>
          <Routes>
            <Route path="/" element={<UserList usuarios={[]} />} />
            <Route path="/users" element={<UserList usuarios={[]} />} />
            <Route path="/booking" element={<BookingForm />} />
            <Route path="/challenges" element={<Challenges />} />
            <Route path="*" element={<div>Página no encontrada</div>} />
          </Routes>
        </Layout>
      </BrowserRouter>
    </ErrorBoundary>
  );
};

export default App;
