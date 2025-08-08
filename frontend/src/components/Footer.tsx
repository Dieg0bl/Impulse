import * as React from 'react';

const Footer: React.FC = () => {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="footer" role="contentinfo">
      <div className="footer-container">
        {/* Sección principal */}
        <div className="footer-main">
          <div className="footer-brand">
              <img className="footer-logo" src="data:image/svg+xml,%F0%9F%9A%80" alt="Cohete" style={{width:'1.5em',height:'1.5em',display:'inline'}} />
            <span className="footer-title">IMPULSE</span>
            <p className="footer-description">
              Plataforma de gamificación educativa con validación humana real y presión social auténtica.
            </p>
          </div>

          <div className="footer-links">
            <div className="footer-section">
              <h3 className="footer-section-title">Plataforma</h3>
              <ul className="footer-list">
                <li><a href="/login" className="footer-link">Iniciar Sesión</a></li>
                <li><a href="/onboarding" className="footer-link">Crear Cuenta</a></li>
                <li><a href="/dashboard" className="footer-link">Dashboard</a></li>
              </ul>
            </div>

            <div className="footer-section">
              <h3 className="footer-section-title">Soporte</h3>
              <ul className="footer-list">
                <li><a href="mailto:security@impulse.dev" className="footer-link">Seguridad</a></li>
                <li><a href="mailto:dba@impulse.dev" className="footer-link">Soporte Técnico</a></li>
                <li><a href="mailto:legal@impulse.dev" className="footer-link">Legal</a></li>
              </ul>
            </div>

            <div className="footer-section">
              <h3 className="footer-section-title">Legal</h3>
              <ul className="footer-list">
                <li><a href="/politica-privacidad" className="footer-link">Política de Privacidad</a></li>
                <li><a href="/terminos-servicio" className="footer-link">Términos de Servicio</a></li>
                <li><a href="/cookies" className="footer-link">Política de Cookies</a></li>
              </ul>
            </div>
          </div>
        </div>

        {/* Sección de cumplimiento */}
        <div className="footer-compliance">
          <div className="compliance-badges">
            <span className="compliance-badge" title="Cumple con RGPD">
              🛡️ RGPD
            </span>
            <span className="compliance-badge" title="Cumple con ISO 27001">
              🔒 ISO 27001
            </span>
            <span className="compliance-badge" title="Cumple con ENS">
              ⚡ ENS
            </span>
          </div>
          
          <div className="footer-legal-text">
            <p>
              © {currentYear} IMPULSE. Todos los derechos reservados. 
              Plataforma desarrollada bajo los más altos estándares de seguridad y cumplimiento.
            </p>
            <p className="footer-disclaimer">
              Esta plataforma utiliza validación humana real y presión social auténtica. 
              Los usuarios mantienen control total sobre su privacidad y exposición.
            </p>
          </div>
        </div>

        {/* Información técnica (solo en desarrollo) */}
        {window.location.hostname === 'localhost' && (
          <div className="footer-dev-info">
            <small>
              Entorno: Desarrollo | Stack: React + TypeScript + Spring Boot + MySQL | 
              Cumplimiento: RGPD, ISO 27001, ENS
            </small>
          </div>
        )}
      </div>
    </footer>
  );
};

export default Footer;
