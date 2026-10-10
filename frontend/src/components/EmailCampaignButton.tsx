import { useEffect, useState } from "react";
import { createEmailRequest, getEmailRequest } from "../api/emailRequests";

const POLL_INTERVAL_MS = 2000;
// ~60 s – dłużej niż pełny cykl błędu na backendzie (4 próby z timeoutem 5 s + przerwy 2+6+18 s ≈ 53 s)
const MAX_POLL_ATTEMPTS = 30;

type SendStatus = "idle" | "pending" | "sent" | "failed" | "timeout";

// TODO (logowanie): niezalogowany → przekierowanie na /login?redirect=..., po powrocie ponowne kliknięcie
export function EmailCampaignButton({ projectUrlName }: { projectUrlName: string }) {
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
      } catch {
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

  async function handleClick() {
    setStatus("pending");
    setRequestId(null);
    try {
      const request = await createEmailRequest(projectUrlName);
      setRequestId(request.id ?? null);
    } catch {
      setStatus("failed");
    }
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