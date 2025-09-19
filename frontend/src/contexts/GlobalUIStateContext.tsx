import React, { createContext, useContext, useState } from 'react';

type UIState = 'idle' | 'offline' | 'access-denied' | 'error' | 'success';

interface GlobalUIState {
  uiState: UIState | undefined;
  message?: string | undefined;
  setUIState: (s: UIState | undefined, msg?: string) => void;
}

const GlobalUIStateContext = createContext<GlobalUIState | undefined>(undefined);

export const GlobalUIStateProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [uiState, setUiState] = useState<UIState | undefined>(undefined);
  const [message, setMessage] = useState<string | undefined>(undefined);

  const setUIState = (s: UIState | undefined, msg?: string) => {
    setUiState(s);
    setMessage(msg);
  };

  return (
    <GlobalUIStateContext.Provider value={{ uiState, message, setUIState }}>
      {children}
    </GlobalUIStateContext.Provider>
  );
};

export const useGlobalUIState = () => {
  const ctx = useContext(GlobalUIStateContext);
  if (!ctx) {
    // Provide a safe default to avoid crashing components that call this hook without provider
    return { uiState: undefined, message: undefined, setUIState: () => {} };
  }
  return ctx;
};

export default GlobalUIStateContext;
