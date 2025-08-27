import { render, screen } from '@testing-library/react';
import Validaciones from '../pages/Validaciones';

describe('Validaciones GDPR compliance', () => {
  it('enmascara información identificativa si no es necesaria', () => {
    render(<Validaciones />);
    expect(screen.queryByText(/email|usuario|dni/i)).not.toBeInTheDocument();
  });
  it('registra acciones de validación para auditoría', () => {
    render(<Validaciones />);
    expect(screen.getByText(/auditoría|log/i)).toBeInTheDocument();
  });
});
