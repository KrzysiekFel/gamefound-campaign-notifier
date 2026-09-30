import type { components } from "./schema";

export type CampaignResponse = components["schemas"]["CampaignResponse"];

export type SearchField = "publisherName" | "gameName" | "bggUsername";

export interface SearchParams {
  field?: SearchField;
  value?: string;
}

export async function searchCampaigns({ field, value }: SearchParams): Promise<CampaignResponse[]> {
  const query = new URLSearchParams();
  if (field && value) {
    query.set(field, value);
  }

  const response = await fetch(`/api/v1/campaigns?${query.toString()}`);

  if (!response.ok) {
    throw new Error(`Failed to fetch campaigns: ${response.status} ${response.statusText}`);
  }

  return response.json();
}
