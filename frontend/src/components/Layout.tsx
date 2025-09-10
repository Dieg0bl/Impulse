import React from 'react'

const Layout: React.FC<{ children?: React.ReactNode }> = ({ children }) => {
		return (
			<div className="app">
				<header className="header">
					<h1>Impulse</h1>
					<nav>
						<a href="/">Dashboard</a> | <a href="/challenges">Retos</a> | <a href="/evidences">Evidencias</a> | <a href="/validator">Validación</a> | <a href="/plans">Planes</a> | <a href="/account">Cuenta</a>
					</nav>
				</header>
				<main className="main-content">{children}</main>
			</div>
		)
}

export default Layout
