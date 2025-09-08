import React from 'react'

function HomePage() {
  return (
    <div className="page">
      <h2>Welcome to IMPULSE</h2>
      <p>The minimalist platform for human validation and challenge completion.</p>
      
      <div className="actions">
        <a href="/register" className="btn btn-primary">Get Started</a>
        <a href="/login" className="btn btn-secondary">Login</a>
      </div>
      
      <section className="features">
        <h3>Core Features</h3>
        <ul>
          <li>Create validation challenges</li>
          <li>Submit evidence of completion</li>
          <li>Human validation process</li>
          <li>Minimal, focused experience</li>
        </ul>
      </section>
    </div>
  )
}

export default HomePage
