interface PathLocation {
  pathname: string;
  search: string;
}

// Dopuszczamy tylko ścieżki wewnątrz aplikacji – ochrona przed open redirect
// (np. /login?redirect=https://evil.com albo //evil.com odesłałyby użytkownika na obcą stronę)
export function safeRedirectPath(value: string | null): string {
  if (!value || !value.startsWith("/") || value.startsWith("//") || value.startsWith("/\\")) {
    return "/";
  }
  return value;
}

// Link do logowania, który po zalogowaniu wraca w to samo miejsce (razem z kryteriami wyszukiwania)
export function loginPathFor(location: PathLocation): string {
  return `/login?redirect=${encodeURIComponent(location.pathname + location.search)}`;
}

export function registerPathFor(location: PathLocation): string {
  return `/register?redirect=${encodeURIComponent(location.pathname + location.search)}`;
}
