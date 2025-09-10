import React, { createContext, useContext, ReactNode } from 'react';

interface AppContextType {
  theme: string;
  setTheme: (theme: string) => void;
}

const AppContext = createContext<AppContextType>({
  theme: 'light',
  setTheme: () => {}
});

export const useApp = () => useContext(AppContext);

interface AppProviderProps {
  children: ReactNode;
}

export const AppProvider: React.FC<AppProviderProps> = ({ children }) => {
  const [theme, setTheme] = React.useState('light');
  const value = React.useMemo(() => ({ theme, setTheme }), [theme]);

  return (
    <AppContext.Provider value={value}>
      {children}
    </AppContext.Provider>
  );
};
