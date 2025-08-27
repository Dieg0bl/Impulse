import { render, screen, fireEvent } from '@testing-library/react';
import Register from '../pages/Register';

describe('Register GDPR compliance', () => {
  it('requiere aceptar términos y política de privacidad', () => {
    render(<Register />);
    expect(screen.getByLabelText(/acepto/i)).toBeRequired();
    expect(screen.getByText(/política de privacidad/i)).toBeInTheDocument();
  });
  it('no almacena contraseña en localStorage', () => {
    render(<Register />);
    fireEvent.change(screen.getByLabelText(/contraseña/i), { target: { value: '123456' } });
    expect(localStorage.getItem('password')).toBeNull();
  });
});
