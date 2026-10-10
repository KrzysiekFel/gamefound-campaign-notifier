import type { components } from "./schema";

export type GameSuggestionResponse = components["schemas"]["GameSuggestionResponse"];

export async function suggestGames(query: string, signal?: AbortSignal): Promise<GameSuggestionResponse[]> {
  const params = new URLSearchParams({ query });
  const response = await fetch(`/api/v1/games/suggestions?${params.toString()}`, { signal });

  if (!response.ok) {
    throw new Error(`Failed to fetch game suggestions: ${response.status} ${response.statusText}`);
  }

  return response.json();
}
