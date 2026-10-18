import { createContext, useContext } from "react";
import type { UserResponse } from "../api/auth";

// "loading" – mamy token, ale jeszcze sprawdzamy go przez GET /v1/me
export type AuthStatus = "loading" | "authenticated" | "anonymous";

export interface AuthContextValue {
  user: UserResponse | null;
  status: AuthStatus;
  login: (email: string, password: string) => Promise<void>;
  register: (username: string, email: string, password: string) => Promise<void>;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextValue | null>(null);

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used inside <AuthProvider>");
  }
  return context;
}
