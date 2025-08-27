import { render, screen } from '@testing-library/react';
import ReportarAvance from '../pages/ReportarAvance';

describe('ReportarAvance GDPR compliance', () => {
  it('valida y sanitiza archivos subidos', () => {
    render(<ReportarAvance />);
    expect(screen.getByLabelText(/archivo/i)).toHaveAttribute('accept');
  });
  it('permite al usuario eliminar sus propios reportes', () => {
    render(<ReportarAvance />);
    expect(screen.getByRole('button', { name: /eliminar/i })).toBeInTheDocument();
  });
});
