import { render, screen } from '@testing-library/react';
import Notificaciones from '../pages/Notificaciones';

describe('Notificaciones GDPR compliance', () => {
  it('solo muestra notificaciones relevantes para el usuario', () => {
    render(<Notificaciones />);
    expect(screen.queryByText(/de otro usuario/i)).not.toBeInTheDocument();
  });
  it('permite marcar como leídas o eliminar notificaciones', () => {
    render(<Notificaciones />);
    expect(screen.getByRole('button', { name: /marcar como leída/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /eliminar/i })).toBeInTheDocument();
  });
});
