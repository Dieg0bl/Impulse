import '@testing-library/jest-dom';
import { ReactNode } from 'react';

export function AppProvider({ children }: Readonly<{ children?: ReactNode }>) {
  return <div data-testid="app-provider">{children}</div>;
}
