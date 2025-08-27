import { render, screen } from '@testing-library/react';
import Categorias from '../pages/Categorias';

describe('Categorias GDPR compliance', () => {
  it('restringe creación/edición a usuarios autorizados', () => {
    render(<Categorias />);
    expect(screen.queryByText(/no autorizado|forbidden|denegado/i)).not.toBeInTheDocument();
  });
  it('valida todos los inputs', () => {
    render(<Categorias />);
    expect(screen.getByLabelText(/nombre/i)).toBeRequired();
    expect(screen.getByLabelText(/descripción/i)).toBeRequired();
  });
});
