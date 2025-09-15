import React, { createContext, useContext, ReactNode } from "react";

interface FlagsContextType {
  flags: Record<string, boolean>;
}

const FlagsContext = createContext<FlagsContextType>({
  flags: {},
});

export const useFlags = () => useContext(FlagsContext);

interface FlagsProviderProps {
  children: ReactNode;
}

export const FlagsProvider: React.FC<FlagsProviderProps> = ({ children }) => {
  const flags = {
    enableNewFeatures: true,
    enableBetaFeatures: false,
  };

  const value = React.useMemo(() => ({ flags }), []);

  return <FlagsContext.Provider value={value}>{children}</FlagsContext.Provider>;
};
