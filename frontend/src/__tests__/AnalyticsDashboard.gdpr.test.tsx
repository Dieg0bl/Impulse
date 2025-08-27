import { render, screen } from '@testing-library/react';
import AnalyticsDashboard from '../components/AnalyticsDashboard';

describe('AnalyticsDashboard GDPR compliance', () => {
  it('no muestra datos personales identificables', () => {
    render(<AnalyticsDashboard />);
    expect(screen.queryByText(/nombre|email|usuario/i)).not.toBeInTheDocument();
  });
  it('permite exportar y limpiar datos de analítica', () => {
    render(<AnalyticsDashboard />);
    expect(screen.getByRole('button', { name: /exportar/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /limpiar/i })).toBeInTheDocument();
  });
});
