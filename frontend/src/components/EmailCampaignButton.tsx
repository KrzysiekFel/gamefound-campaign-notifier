import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { createEmailRequest, getEmailRequest } from "../api/emailRequests";
import { ApiError } from "../api/http";
import { useAuth } from "../auth/authContext";
import { loginPathFor } from "../auth/redirect";

const POLL_INTERVAL_MS = 2000;
// ~60 s – dłużej niż pełny cykl błędu na backendzie (4 próby z timeoutem 5 s + przerwy 2+6+18 s ≈ 53 s)
const MAX_POLL_ATTEMPTS = 30;

type SendStatus = "idle" | "pending" | "sent" | "failed" | "timeout";

function isUnauthorized(error: unknown) {
  return error instanceof ApiError && error.status === 401;
}

export function EmailCampaignButton({ projectUrlName }: { projectUrlName: string }) {
  const { status: authStatus } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [status, setStatus] = useState<SendStatus>("idle");
  const [requestId, setRequestId] = useState<string | null>(null);

  useEffect(() => {
    if (!requestId) return;
    const id = requestId;

    let cancelled = false;
    let attempts = 0;
    let timeoutId: ReturnType<typeof setTimeout>;

    async function poll() {
      attempts += 1;
      try {
        const request = await getEmailRequest(id);
        if (cancelled) return;
        if (request.status === "SENT") {
          setStatus("sent");
          return;
        }
        if (request.status === "FAILED") {
          setStatus("failed");
          return;
        }
      } catch (error) {
        // wylogowany w trakcie (token wygasł) – nie ma sensu dalej pytać
        if (isUnauthorized(error)) {
          setStatus("idle");
          return;
        }
        // chwilowy błąd sieci – spróbujemy przy następnym sprawdzeniu
      }

      if (cancelled) return;
      if (attempts < MAX_POLL_ATTEMPTS) {
        timeoutId = setTimeout(poll, POLL_INTERVAL_MS);
      } else {
        setStatus("timeout");
      }
    }

    timeoutId = setTimeout(poll, POLL_INTERVAL_MS);

    return () => {
      cancelled = true;
      clearTimeout(timeoutId);
    };
  }, [requestId]);

  // Po zalogowaniu wracamy na tę samą stronę (z kryteriami wyszukiwania) – użytkownik klika jeszcze raz
  function goToLogin() {
    navigate(loginPathFor(location));
  }

  async function handleClick() {
    setStatus("pending");
    setRequestId(null);
    try {
      const request = await createEmailRequest(projectUrlName);
      setRequestId(request.id ?? null);
    } catch (error) {
      if (isUnauthorized(error)) {
        setStatus("idle");
        goToLogin();
        return;
      }
      setStatus("failed");
    }
  }

  if (authStatus !== "authenticated") {
    return (
      <button type="button" className="email-button" onClick={goToLogin} disabled={authStatus === "loading"}>
        Log in to get this by email
      </button>
    );
  }

  if (status === "sent") {
    return <p className="email-status">✓ Sent to your email</p>;
  }

  if (status === "timeout") {
    return <p className="email-status">Still sending – check your inbox in a moment</p>;
  }

  if (status === "failed") {
    return (
      <p className="email-status email-status-error">
        Could not send the email.{" "}
        <button type="button" className="email-retry" onClick={handleClick}>
          Try again
        </button>
      </p>
    );
  }

  return (
    <button type="button" className="email-button" onClick={handleClick} disabled={status === "pending"}>
      {status === "pending" ? "Sending…" : "Email me this campaign"}
    </button>
  );
}
