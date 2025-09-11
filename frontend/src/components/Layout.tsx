import React from 'react'
import { Link } from 'react-router-dom'
import BetaBanner from './BetaBanner'
import Footer from './Footer'

const Layout: React.FC<{ children?: React.ReactNode }> = ({ children }) => {
  return (
    <div className="app">
      <header className="header">
        <h1>Impulse</h1>
        <nav>
          <Link to="/">Dashboard</Link> | <Link to="/challenges">Retos</Link> | <a href="/evidences">Evidencias</a> | <a href="/validator">Validación</a> | <Link to="/pricing">Planes</Link> | <a href="/account">Cuenta</a>
        </nav>
      </header>
      <BetaBanner />
      <main className="main-content">{children}</main>
      <Footer />
    </div>
  )
}

export default Layout
