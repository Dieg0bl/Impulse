import React, { createContext, useContext, ReactNode } from "react";

interface ComplianceContextType {
  gdprConsent: boolean;
  setGdprConsent: (consent: boolean) => void;
}

const ComplianceContext = createContext<ComplianceContextType>({
  gdprConsent: false,
  setGdprConsent: () => {},
});

export const useCompliance = () => useContext(ComplianceContext);

interface ComplianceProviderProps {
  children: ReactNode;
}

export const ComplianceProvider: React.FC<ComplianceProviderProps> = ({ children }) => {
  const [gdprConsent, setGdprConsent] = React.useState(false);

  const value = React.useMemo(() => ({ gdprConsent, setGdprConsent }), [gdprConsent]);

  return <ComplianceContext.Provider value={value}>{children}</ComplianceContext.Provider>;
};
