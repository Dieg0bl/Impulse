import * as React from 'react';
import { useAppContext } from '../contexts/AppContext.tsx';

const Home: React.FC = () => {
  const { navigate } = useAppContext();

  return (
    <div className="App">
      {/* Skip to main content para accesibilidad */}
      <a href="#main-content" className="skip-to-main">
        Saltar al contenido principal
      </a>
      
      {/* Header con navegación mejorada */}
      <header className="header">
        <div className="header-content">
          <div className="logo">
            <span className="logo-icon">⚡</span>
            <span className="logo-text">IMPULSE</span>
          </div>
          <nav className="nav-menu" role="navigation" aria-label="Navegación principal">
            <a href="#funciones" className="nav-link interactive-element">Funciones</a>
            <a href="#testimonios" className="nav-link interactive-element">Testimonios</a>
            <a href="#precios" className="nav-link interactive-element">Precios</a>
            <button 
              className="btn btn-primary" 
              onClick={() => navigate('login')}
              aria-label="Acceder a la aplicación"
            >
              Entrar
            </button>
          </nav>
        </div>
      </header>

      {/* Contenido principal */}
      <main id="main-content">
        {/* Hero Section con mejor contraste */}
        <header className="hero hero-section">
          <div className="hero-content">
            <h1 className="hero-title">
              Convierte tus <span className="text-gradient">RETOS</span> en 
              <span className="text-gradient"> REALIDAD</span>
            </h1>
            <p className="hero-subtitle">
              🎯 <strong>PRESIÓN SOCIAL REAL</strong> • 👥 <strong>VALIDACIÓN HUMANA AUTÉNTICA</strong> • 🚀 <strong>RESULTADOS MEDIBLES</strong>
            </p>
            <p className="hero-description">
              No más excusas. No más promesas vacías. IMPULSE utiliza la presión social real 
              y validadores humanos para asegurar que cumplas tus compromisos.
            </p>
            <div className="hero-buttons">
              <button
                className="btn btn-primary btn-large"
                onClick={() => navigate('register')}
                aria-label="Comenzar mi primer reto ahora"
              >
                🚀 Comenzar Ahora
              </button>
              <button 
                className="btn btn-secondary btn-large"
                onClick={() => navigate('login')}
                aria-label="Ya tengo cuenta, iniciar sesión"
              >
                👤 Ya tengo cuenta
              </button>
            </div>
          </div>
        </header>

        {/* Features Section */}
        <section id="funciones" className="features">
          <div className="container">
            <h2 className="section-title">¿Cómo funciona IMPULSE?</h2>
            <div className="features-grid">
              <article className="feature-card">
                <span className="feature-icon" role="img" aria-label="Objetivo">🎯</span>
                <h3>Define tu RETO</h3>
                <p>
                  Establece objetivos claros, medibles y con fecha límite.
                  Desde ejercicio diario hasta aprender un idioma.
                </p>
              </article>
              <article className="feature-card">
                <span className="feature-icon" role="img" aria-label="Personas">👥</span>
                <h3>Elige VALIDADORES</h3>
                <p>
                  Selecciona personas de confianza que verificarán tu progreso.
                  Familiares, amigos o mentores que te conocen bien.
                </p>
              </article>
              <article className="feature-card">
                <span className="feature-icon" role="img" aria-label="Evidencia">📸</span>
                <h3>Reporta EVIDENCIAS</h3>
                <p>
                  Sube fotos, videos o textos que demuestren tu avance.
                  Transparencia total en tu progreso.
                </p>
              </article>
              <article className="feature-card">
                <span className="feature-icon" role="img" aria-label="Validación">✅</span>
                <h3>Recibe VALIDACIÓN</h3>
                <p>
                  Tus validadores aprueban o rechazan tu progreso.
                  Presión social real para mantenerte comprometido.
                </p>
              </article>
              <article className="feature-card">
                <span className="feature-icon" role="img" aria-label="Recompensa">🏆</span>
                <h3>Obtén RECOMPENSAS</h3>
                <p>
                  Gana puntos, badges y reconocimiento real.
                  Celebra tus logros con evidencia verificada.
                </p>
              </article>
              <article className="feature-card">
                <span className="feature-icon" role="img" aria-label="Consecuencias">⚖️</span>
                <h3>Asume CONSECUENCIAS</h3>
                <p>
                  Define qué pasa si no cumples.
                  Compromiso real con resultados reales.
                </p>
              </article>
            </div>
          </div>
        </section>

        {/* Por qué IMPULSE Section */}
        <section className="why-section">
          <div className="container">
            <div className="why-content">
              <h2 className="why-title">¿Por qué IMPULSE funciona para CUALQUIER objetivo?</h2>
              <p className="why-subtitle">
                IMPULSE no es otra app de productividad que te abandona. Es un sistema completo de compromiso social que aprovecha el poder más fuerte de la naturaleza humana: **la validación social**.
              </p>
              <div className="why-highlight">
                <p><strong>🧠 PSICOLOGÍA PROBADA:</strong></p>
                <p>Cuando otras personas que respetas están pendientes de tu progreso, tu cerebro activa mecanismos de compromiso 10 veces más poderosos que la simple fuerza de voluntad.</p>
              </div>
              <p className="why-subtitle">
                **Sea cual sea tu objetivo**, IMPULSE se adapta:
              </p>
              <div className="why-benefits">
                <h3>🏃‍♂️ ¿Quieres hacer ejercicio? → Sube foto del gimnasio cada día</h3>
                <p className="why-subtitle">🎓 <strong>¿Estudiar para un examen?</strong> → Reporta horas de estudio con evidencia</p>
                <p className="why-subtitle">💰 <strong>¿Ahorrar dinero?</strong> → Comparte screenshots de tu cuenta de ahorros</p>
                <p className="why-subtitle">🚭 <strong>¿Dejar un vicio?</strong> → Videos diarios confirmando tu progreso</p>
                <p className="why-subtitle">📚 <strong>¿Leer más libros?</strong> → Fotos de páginas leídas o resúmenes</p>
                <p className="why-subtitle">🎨 <strong>¿Aprender una habilidad?</strong> → Evidencia de tu práctica diaria</p>
              </div>
              <div className="why-highlight">
                <p><strong>💎 EL SECRETO:</strong></p>
                <p>No es la app lo que te hace cumplir. Son las PERSONAS REALES que van a ver si cumples o no. Esa presión social auténtica es lo que convierte promesas en realidad.</p>
              </div>
              <div className="why-final">
                <h3>🎯 IMPULSE = Compromiso Social + Validación Humana + Evidencia Real</h3>
                <p>Funcionamos porque utilizamos los principios más fundamentales del comportamiento humano.</p>
                <p><em>"Si lo haces solo, es fácil rendirse. Si alguien está pendiente, es imposible fallar."</em></p>
              </div>
            </div>
          </div>
        </section>

        {/* CTA Section */}
        <section className="cta-section">
          <div className="container">
            <h2 className="cta-title">¿Listo para convertir tus metas en logros REALES?</h2>
            <p className="cta-description">
              Únete a miles de personas que ya están cumpliendo sus objetivos con 
              el poder de la validación social auténtica.
            </p>
            <div className="hero-buttons">
              <button
                className="btn btn-primary btn-large"
                onClick={() => navigate('register')}
                aria-label="Crear mi cuenta gratis"
              >
                🚀 Crear mi cuenta GRATIS
              </button>
              <button
                className="btn btn-secondary btn-large"
                onClick={() => navigate('login')}
                aria-label="Ya tengo cuenta"
              >
                👤 Ya tengo cuenta
              </button>
            </div>
          </div>
        </section>
      </main>

      {/* Footer */}
      <footer className="footer">
        <div className="container">
          <div className="footer-content">
            <div className="footer-section">
              <h4>IMPULSE</h4>
              <p>Convierte tus retos en realidad</p>
            </div>
            <div className="footer-section">
              <h4>Producto</h4>
              <ul>
                <li><a href="#funciones" className="footer-link">Funciones</a></li>
                <li><a href="#precios" className="footer-link">Precios</a></li>
                <li><button className="footer-link" onClick={() => navigate('login')}>Iniciar Sesión</button></li>
              </ul>
            </div>
            <div className="footer-section">
              <h4>Soporte</h4>
              <ul>
                <li><a href="mailto:soporte@impulse.dev" className="footer-link">📧 Soporte</a></li>
                <li><a href="tel:+34900000000" className="footer-link">📞 Teléfono</a></li>
              </ul>
            </div>
            <div className="footer-section">
              <h4>Legal</h4>
              <ul>
                <li><a href="/privacy" className="footer-link">Privacidad</a></li>
                <li><a href="/terms" className="footer-link">Términos</a></li>
              </ul>
            </div>
          </div>
          <div className="footer-bottom">
            <p>&copy; 2024 IMPULSE. Todos los derechos reservados.</p>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Home;
