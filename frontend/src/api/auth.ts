import { authFetch, toApiError } from "./http";
import type { components } from "./schema";

export type UserResponse = components["schemas"]["UserResponse"];
export type LoginRequest = components["schemas"]["LoginRequest"];
export type LoginResponse = components["schemas"]["LoginResponse"];
export type RegisterRequest = components["schemas"]["RegisterRequest"];

export async function register(body: RegisterRequest): Promise<UserResponse> {
  const response = await fetch("/api/v1/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });

  if (!response.ok) {
    throw await toApiError(response, "Registration failed");
  }

  return response.json();
}

export async function login(body: LoginRequest): Promise<LoginResponse> {
  const response = await fetch("/api/v1/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });

  if (!response.ok) {
    throw await toApiError(response, "Login failed");
  }

  return response.json();
}

export async function getCurrentUser(): Promise<UserResponse> {
  const response = await authFetch("/api/v1/me");

  if (!response.ok) {
    throw await toApiError(response, "Failed to fetch current user");
  }

  return response.json();
}
