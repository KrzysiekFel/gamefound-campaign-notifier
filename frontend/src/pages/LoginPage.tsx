import { useState, type FormEvent } from "react";
import { Link, Navigate, useNavigate, useSearchParams } from "react-router-dom";
import { ApiError } from "../api/http";
import { useAuth } from "../auth/authContext";
import { safeRedirectPath } from "../auth/redirect";

export function LoginPage() {
  const { login, status } = useAuth();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const redirectTo = safeRedirectPath(searchParams.get("redirect"));

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // zalogowany użytkownik nie ma tu czego szukać
  if (status === "authenticated" && !isSubmitting) {
    return <Navigate to={redirectTo} replace />;
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setIsSubmitting(true);
    try {
      await login(email, password);
      navigate(redirectTo, { replace: true });
    } catch (err) {
      setError(
        err instanceof ApiError && err.status === 401
          ? "Invalid email or password"
          : "Could not log in. Please try again.",
      );
      setIsSubmitting(false);
    }
  }

  return (
    <div className="auth-page">
      <h1>Log in</h1>
      <p className="auth-intro">Log in to get campaigns sent to your email.</p>

      <form className="auth-form" onSubmit={handleSubmit}>
        <label>
          Email
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            maxLength={255}
            autoComplete="email"
            autoFocus
          />
        </label>
        <label>
          Password
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            maxLength={72}
            autoComplete="current-password"
          />
        </label>

        {error && <p className="form-error">{error}</p>}

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Logging in…" : "Log in"}
        </button>
      </form>

      <p className="auth-switch">
        No account yet?{" "}
        <Link to={`/register?redirect=${encodeURIComponent(redirectTo)}`}>Register</Link>
      </p>
    </div>
  );
}
