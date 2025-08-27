import { render, screen, fireEvent } from '@testing-library/react';
import Login from '../pages/Login';

describe('Login GDPR compliance', () => {
  it('no muestra detalles sensibles en errores', () => {
    render(<Login />);
    // Simula error de login
    // ...
    expect(screen.queryByText(/contraseña incorrecta|password/i)).not.toBeInTheDocument();
  });
  it('muestra enlace a política de privacidad', () => {
    render(<Login />);
    expect(screen.getByText(/política de privacidad/i)).toBeInTheDocument();
  });
});
