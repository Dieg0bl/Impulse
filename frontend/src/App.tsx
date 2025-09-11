import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import UserList from './components/UserList';
import BookingForm from './components/BookingForm';
import Challenges from './pages/Challenges';
import Pricing from './pages/Pricing';
import ErrorBoundary from './components/ErrorBoundary';
import './assets/styles.css';

const App: React.FC = () => {
  return (
    <ErrorBoundary>
      <BrowserRouter>
        <Layout>
          <Routes>
            <Route path="/" element={<UserList usuarios={[]} />} />
            <Route path="/users" element={<UserList usuarios={[]} />} />
            <Route path="/booking" element={<BookingForm />} />
            <Route path="/challenges" element={<Challenges />} />
            <Route path="/pricing" element={<Pricing />} />
            <Route path="*" element={<div>Página no encontrada</div>} />
          </Routes>
        </Layout>
      </BrowserRouter>
    </ErrorBoundary>
  );
};

export default App;
