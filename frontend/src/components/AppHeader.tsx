import { Link, useLocation } from "react-router-dom";
import { useAuth } from "../auth/authContext";
import { loginPathFor, registerPathFor } from "../auth/redirect";

const AUTH_PAGES = ["/login", "/register"];

export function AppHeader() {
  const { user, status, logout } = useAuth();
  const location = useLocation();
  const isAuthPage = AUTH_PAGES.includes(location.pathname);

  return (
    <header className="app-header">
      <Link to="/" className="app-title">
        Gamefound Campaign Notifier
      </Link>

      <nav className="app-nav">
        {status === "authenticated" && user && (
          <>
            <span>
              Logged in as <strong>{user.username}</strong>
            </span>
            <button type="button" className="link-button" onClick={logout}>
              Log out
            </button>
          </>
        )}
        {status === "anonymous" && !isAuthPage && (
          <>
            <Link to={loginPathFor(location)}>Log in</Link>
            <Link to={registerPathFor(location)}>Register</Link>
          </>
        )}
      </nav>
    </header>
  );
}
