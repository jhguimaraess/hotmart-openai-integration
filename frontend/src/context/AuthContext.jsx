import { createContext, useContext, useEffect, useState } from "react";

import { getAuth, removeAuth, saveAuth } from "../services/authStorage";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(() => getAuth());

  function login(tokenResponse) {
    const storedAuth = saveAuth(tokenResponse);

    setAuth(storedAuth);
  }

  function logout() {
    removeAuth();

    setAuth(null);
  }

  useEffect(() => {
    if (!auth?.expiresAt) {
      return;
    }

    const remainingTime = auth.expiresAt - Date.now();

    if (remainingTime <= 0) {
      logout();
      return;
    }

    const timeout = setTimeout(logout, remainingTime);

    return () => {
      clearTimeout(timeout);
    };
  }, [auth]);

  useEffect(() => {
    function handleUnauthorized() {
      setAuth(null);
    }

    window.addEventListener("auth:unauthorized", handleUnauthorized);

    return () => {
      window.removeEventListener("auth:unauthorized", handleUnauthorized);
    };
  }, []);

  return (
    <AuthContext.Provider
      value={{
        auth,
        isAuthenticated: Boolean(auth?.accessToken),
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used inside AuthProvider");
  }

  return context;
}
