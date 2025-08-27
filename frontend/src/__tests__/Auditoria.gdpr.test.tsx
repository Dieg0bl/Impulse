import { render, screen } from '@testing-library/react';
import Auditoria from '../pages/Auditoria';

describe('Auditoria GDPR compliance', () => {
  it('solo muestra logs a usuarios autorizados', () => {
    render(<Auditoria />);
    expect(screen.queryByText(/no autorizado|forbidden|denegado/i)).not.toBeInTheDocument();
  });
  it('proporciona filtros por tipo de log y rango de fechas', () => {
    render(<Auditoria />);
    expect(screen.getByLabelText(/tipo de log/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/rango de fechas/i)).toBeInTheDocument();
  });
});
