import type { components } from "./schema";

export type CreateEmailRequest = components["schemas"]["CreateEmailRequest"];
export type EmailRequestResponse = components["schemas"]["EmailRequestResponse"];

export async function createEmailRequest(projectUrlName: string): Promise<EmailRequestResponse> {
  const body: CreateEmailRequest = { projectUrlName };

  const response = await fetch("/api/v1/email-requests", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });

  if (!response.ok) {
    throw new Error(`Failed to request email: ${response.status} ${response.statusText}`);
  }

  return response.json();
}

export async function getEmailRequest(id: string): Promise<EmailRequestResponse> {
  const response = await fetch(`/api/v1/email-requests/${id}`);

  if (!response.ok) {
    throw new Error(`Failed to fetch email request status: ${response.status} ${response.statusText}`);
  }

  return response.json();
}
