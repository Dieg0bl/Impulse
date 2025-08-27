import React, { createContext, useContext } from 'react';
import { useConsents } from '../hooks/useConsents';

// Definición del contexto
const ComplianceContext = createContext<any>(null);

export const ComplianceProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
	const consentsHook = useConsents();
	// consentsHook: { consents, loading, error, fetchConsents, update }
	return (
		<ComplianceContext.Provider value={consentsHook}>
			{children}
		</ComplianceContext.Provider>
	);
};

// Hook para consumir el contexto
export function useCompliance() {
	const ctx = useContext(ComplianceContext);
	if (!ctx) throw new Error('useCompliance debe usarse dentro de ComplianceProvider');
	return ctx;
}
