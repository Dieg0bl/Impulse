import { render, screen } from '@testing-library/react';
import Configuracion from '../pages/Configuracion';

describe('Configuracion GDPR compliance', () => {
  it('permite exportar y eliminar datos del usuario', () => {
    render(<Configuracion />);
    expect(screen.getByRole('button', { name: /exportar/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /eliminar/i })).toBeInTheDocument();
  });
  it('explica claramente cada ajuste', () => {
    render(<Configuracion />);
    expect(screen.getByText(/preferencias/i)).toBeInTheDocument();
    expect(screen.getByText(/privacidad/i)).toBeInTheDocument();
  });
});
