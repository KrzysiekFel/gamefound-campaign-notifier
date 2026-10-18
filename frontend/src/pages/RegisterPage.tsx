import { useState, type FormEvent } from "react";
import { Link, Navigate, useNavigate, useSearchParams } from "react-router-dom";
import { ApiError } from "../api/http";
import { useAuth } from "../auth/authContext";
import { safeRedirectPath } from "../auth/redirect";

export function RegisterPage() {
  const { register, status } = useAuth();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const redirectTo = safeRedirectPath(searchParams.get("redirect"));

  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  if (status === "authenticated" && !isSubmitting) {
    return <Navigate to={redirectTo} replace />;
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setIsSubmitting(true);
    try {
      await register(username.trim(), email, password);
      navigate(redirectTo, { replace: true });
    } catch (err) {
      setError(registrationErrorMessage(err));
      setIsSubmitting(false);
    }
  }

  return (
    <div className="auth-page">
      <h1>Create account</h1>
      <p className="auth-intro">Campaign emails will be sent to the address you register with.</p>

      {/* Ograniczenia pól takie same jak w openapi.yaml (RegisterRequest) – przeglądarka sprawdzi je przed wysłaniem */}
      <form className="auth-form" onSubmit={handleSubmit}>
        <label>
          Username
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            minLength={3}
            maxLength={50}
            pattern="[a-zA-Z0-9_.\-]+"
            title="3–50 characters: letters, digits, _ . -"
            autoComplete="username"
            autoFocus
          />
          <span className="form-hint">3–50 characters: letters, digits, _ . -</span>
        </label>
        <label>
          Email
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            maxLength={255}
            autoComplete="email"
          />
        </label>
        <label>
          Password
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            minLength={8}
            maxLength={72}
            autoComplete="new-password"
          />
          <span className="form-hint">At least 8 characters</span>
        </label>

        {error && <p className="form-error">{error}</p>}

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Creating account…" : "Create account"}
        </button>
      </form>

      <p className="auth-switch">
        Already have an account?{" "}
        <Link to={`/login?redirect=${encodeURIComponent(redirectTo)}`}>Log in</Link>
      </p>
    </div>
  );
}

function registrationErrorMessage(error: unknown): string {
  if (error instanceof ApiError) {
    if (error.status === 409) return error.message; // "Email is already registered" / "Username is already taken"
    if (error.status === 400) return "Please check the form fields.";
  }
  return "Could not create the account. Please try again.";
}
