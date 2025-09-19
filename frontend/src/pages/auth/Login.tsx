
import React, { useState } from 'react';
import { useAuth } from '../../providers/AuthProvider';
import { useNavigate } from 'react-router-dom';

const Login: React.FC = () => {
	const { login } = useAuth();
		// Predefinir credenciales admin para pruebas
		const [email, setEmail] = useState('admin@impulse.local');
		const [password, setPassword] = useState('admin');
	const [error, setError] = useState<string | null>(null);
	const navigate = useNavigate();

		const handleSubmit = async (e: React.FormEvent) => {
			e.preventDefault();
			try {
				// Si no existe el usuario admin en backend, crearlo antes de login
				if (!email.endsWith('.local')) {
					const res = await fetch(`${import.meta.env.VITE_API_BASE || "http://localhost:8080"}/api/v1/auth/register`, {
						method: 'POST',
						headers: { 'Content-Type': 'application/json' },
						body: JSON.stringify({
							username: 'admin',
							email,
							firstName: 'Admin',
							lastName: 'User',
							password,
							bio: 'Superadmin omnipotente',
							profileImageUrl: '',
						}),
					});
					// Ignorar error si ya existe
				}
				await login(email, password);
				navigate('/');
			} catch (err: any) {
				setError(err.message || 'Error de login');
			}
		};

	return (
		<div style={{ maxWidth: 320, margin: '80px auto', padding: 24, background: 'rgb(var(--surface-0))', borderRadius: 12, boxShadow: '0 2px 12px rgba(0,0,0,0.08)' }}>
			<h2 style={{ marginBottom: 16 }}>Iniciar sesión</h2>
			<form onSubmit={handleSubmit}>
				<label style={{ display: 'block', marginBottom: 8 }}>
					Email
					<input type="email" value={email} onChange={e => setEmail(e.target.value)} style={{ width: '100%', marginTop: 4, marginBottom: 12 }} />
				</label>
				<label style={{ display: 'block', marginBottom: 8 }}>
					Contraseña
					<input type="password" value={password} onChange={e => setPassword(e.target.value)} style={{ width: '100%', marginTop: 4, marginBottom: 12 }} />
				</label>
				{error && <div style={{ color: 'red', marginBottom: 8 }}>{error}</div>}
				<button type="submit" style={{ width: '100%', padding: 8, background: 'rgb(var(--color-primary))', color: '#fff', border: 'none', borderRadius: 6 }}>Entrar</button>
			</form>
			<div style={{ marginTop: 16, fontSize: 12, color: '#888' }}>
				Usa <b>admin@impulse.local</b> / <b>admin</b> para acceso total local.
			</div>
		</div>
	);
};

export default Login;
