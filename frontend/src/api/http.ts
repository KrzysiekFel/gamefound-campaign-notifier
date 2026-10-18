// Przechowywanie tokenu JWT i wspólna obsługa requestów wymagających logowania.

const TOKEN_STORAGE_KEY = "gamefound.auth";

// Wysyłane, gdy backend odrzuci token (401) albo go brakuje – AuthProvider wylogowuje wtedy użytkownika
export const UNAUTHORIZED_EVENT = "gamefound:unauthorized";

interface StoredToken {
  accessToken: string;
  expiresAt: string;
}

export class ApiError extends Error {
  readonly status: number;

  constructor(status: number, message: string) {
    super(message);
    this.name = "ApiError";
    this.status = status;
  }
}

export function saveToken(accessToken: string, expiresAt: string) {
  const stored: StoredToken = { accessToken, expiresAt };
  localStorage.setItem(TOKEN_STORAGE_KEY, JSON.stringify(stored));
}

export function clearToken() {
  localStorage.removeItem(TOKEN_STORAGE_KEY);
}

// Zwraca token tylko, jeśli jeszcze nie wygasł – przeterminowany od razu usuwamy
export function getToken(): string | null {
  const raw = localStorage.getItem(TOKEN_STORAGE_KEY);
  if (!raw) return null;

  try {
    const stored = JSON.parse(raw) as StoredToken;
    if (new Date(stored.expiresAt).getTime() <= Date.now()) {
      clearToken();
      return null;
    }
    return stored.accessToken;
  } catch {
    clearToken();
    return null;
  }
}

// fetch z nagłówkiem Authorization – tylko dla endpointów chronionych.
// Publiczne (kampanie, podpowiedzi) wołamy zwykłym fetch, żeby nieważny token ich nie blokował (backend dałby 401).
export async function authFetch(input: string, init: RequestInit = {}): Promise<Response> {
  const token = getToken();
  if (!token) {
    window.dispatchEvent(new Event(UNAUTHORIZED_EVENT));
    throw new ApiError(401, "Not logged in");
  }

  const headers = new Headers(init.headers);
  headers.set("Authorization", `Bearer ${token}`);
  const response = await fetch(input, { ...init, headers });

  if (response.status === 401) {
    clearToken();
    window.dispatchEvent(new Event(UNAUTHORIZED_EVENT));
  }
  return response;
}

// Zamienia odpowiedź z błędem na ApiError; komunikat bierze z pola "detail" (ProblemDetail z backendu)
export async function toApiError(response: Response, fallbackMessage: string): Promise<ApiError> {
  let message = `${fallbackMessage}: ${response.status} ${response.statusText}`;
  try {
    const problem = (await response.json()) as { detail?: string };
    if (problem.detail) {
      message = problem.detail;
    }
  } catch {
    // brak body (np. 401 z filtra Spring Security) – zostaje komunikat domyślny
  }
  return new ApiError(response.status, message);
}
