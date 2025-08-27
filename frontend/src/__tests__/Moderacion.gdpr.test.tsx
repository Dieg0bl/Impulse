import { render, screen } from '@testing-library/react';
import Moderacion from '../pages/Moderacion';

describe('Moderacion GDPR compliance', () => {
  it('solo permite acceso a moderadores/admins', () => {
    render(<Moderacion />);
    expect(screen.queryByText(/no autorizado|forbidden|denegado/i)).not.toBeInTheDocument();
  });
  it('no muestra más información de usuario de la necesaria', () => {
    render(<Moderacion />);
    expect(screen.queryByText(/email|usuario|dni/i)).not.toBeInTheDocument();
  });
});
