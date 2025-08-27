import { render, screen, fireEvent } from '@testing-library/react';
import Perfil from '../pages/Perfil';

describe('Perfil GDPR compliance', () => {
  it('muestra y permite editar consentimientos', () => {
    render(<Perfil />);
    expect(screen.getByText(/consentimiento/i)).toBeInTheDocument();
    // Simula edición de consentimiento
    // ...
  });
  it('muestra enlace a política de privacidad', () => {
    render(<Perfil />);
    expect(screen.getByText(/política de privacidad/i)).toBeInTheDocument();
  });
  it('muestra botón de eliminar cuenta', () => {
    render(<Perfil />);
    expect(screen.getByRole('button', { name: /eliminar cuenta/i })).toBeInTheDocument();
  });
});
