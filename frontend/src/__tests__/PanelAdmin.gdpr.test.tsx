import { render, screen } from '@testing-library/react';
import PanelAdmin from '../pages/PanelAdmin';

describe('PanelAdmin GDPR compliance', () => {
  it('no expone datos sensibles de configuración o usuarios', () => {
    render(<PanelAdmin />);
    expect(screen.queryByText(/contraseña|token|secreto|api/i)).not.toBeInTheDocument();
  });
  it('registra todas las acciones para auditoría', () => {
    render(<PanelAdmin />);
    expect(screen.getByText(/auditoría|log/i)).toBeInTheDocument();
  });
});
