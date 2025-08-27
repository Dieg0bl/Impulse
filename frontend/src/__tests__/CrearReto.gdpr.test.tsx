import { render, screen } from '@testing-library/react';
import CrearReto from '../pages/CrearReto';

describe('CrearReto GDPR compliance', () => {
  it('valida todos los campos antes de enviar', () => {
    render(<CrearReto />);
    expect(screen.getByLabelText(/título/i)).toBeRequired();
    expect(screen.getByLabelText(/descripción/i)).toBeRequired();
  });
  it('no expone IDs internos ni datos sensibles en la UI', () => {
    render(<CrearReto />);
    expect(screen.queryByText(/id:|token|secreto/i)).not.toBeInTheDocument();
  });
});
