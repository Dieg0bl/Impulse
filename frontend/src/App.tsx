import React from 'react'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import HomePage from './pages/HomePage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'

function App() {
  return (
    <Router>
      <div className="app">
        <header className="app-header">
          <h1>IMPULSE</h1>
          <p>Human Validation Platform</p>
        </header>
        
        <main>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
          </Routes>
        </main>
        
        <footer>
          <p>&copy; 2024 IMPULSE - Human Validation Platform</p>
        </footer>
      </div>
    </Router>
  )
}

export default App
