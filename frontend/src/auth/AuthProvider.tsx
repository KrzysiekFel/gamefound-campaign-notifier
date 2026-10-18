import { useCallback, useEffect, useMemo, useState, type ReactNode } from "react";
import { getCurrentUser, login as apiLogin, register as apiRegister, type UserResponse } from "../api/auth";
import { ApiError, clearToken, getToken, saveToken, UNAUTHORIZED_EVENT } from "../api/http";
import { AuthContext, type AuthContextValue, type AuthStatus } from "./authContext";

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserResponse | null>(null);
  const [status, setStatus] = useState<AuthStatus>(() => (getToken() ? "loading" : "anonymous"));

  const loadCurrentUser = useCallback(async () => {
    try {
      setUser(await getCurrentUser());
      setStatus("authenticated");
    } catch (error) {
      // 401 – token nieważny (authFetch już go usunął); inny błąd – traktujemy jak niezalogowanego do odświeżenia strony
      if (!(error instanceof ApiError && error.status === 401)) {
        console.error(error);
      }
      setUser(null);
      setStatus("anonymous");
    }
  }, []);

  // Po odświeżeniu strony mamy tylko token z localStorage – /v1/me mówi, czy działa i kim jest użytkownik
  useEffect(() => {
    if (getToken()) {
      void loadCurrentUser();
    }
  }, [loadCurrentUser]);

  // Dowolny request z odrzuconym tokenem (np. wygasł w trakcie) wylogowuje użytkownika w całej aplikacji
  useEffect(() => {
    function handleUnauthorized() {
      setUser(null);
      setStatus("anonymous");
    }
    window.addEventListener(UNAUTHORIZED_EVENT, handleUnauthorized);
    return () => window.removeEventListener(UNAUTHORIZED_EVENT, handleUnauthorized);
  }, []);

  const login = useCallback(
    async (email: string, password: string) => {
      const response = await apiLogin({ email, password });
      saveToken(response.accessToken, response.expiresAt);
      await loadCurrentUser();
    },
    [loadCurrentUser],
  );

  const register = useCallback(
    async (username: string, email: string, password: string) => {
      await apiRegister({ username, email, password });
      // od razu logujemy – użytkownik nie musi wpisywać danych drugi raz
      await login(email, password);
    },
    [login],
  );

  const logout = useCallback(() => {
    clearToken();
    setUser(null);
    setStatus("anonymous");
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({ user, status, login, register, logout }),
    [user, status, login, register, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
