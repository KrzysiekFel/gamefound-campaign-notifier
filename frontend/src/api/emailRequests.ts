import { authFetch, toApiError } from "./http";
import type { components } from "./schema";

export type CreateEmailRequest = components["schemas"]["CreateEmailRequest"];
export type EmailRequestResponse = components["schemas"]["EmailRequestResponse"];

export async function createEmailRequest(projectUrlName: string): Promise<EmailRequestResponse> {
  const body: CreateEmailRequest = { projectUrlName };

  const response = await authFetch("/api/v1/email-requests", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });

  if (!response.ok) {
    throw await toApiError(response, "Failed to request email");
  }

  return response.json();
}

export async function getEmailRequest(id: string): Promise<EmailRequestResponse> {
  const response = await authFetch(`/api/v1/email-requests/${id}`);

  if (!response.ok) {
    throw await toApiError(response, "Failed to fetch email request status");
  }

  return response.json();
}
