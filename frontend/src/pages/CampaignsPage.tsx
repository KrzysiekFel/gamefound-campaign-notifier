import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { searchCampaigns, type CampaignResponse, type SearchField, type SearchParams } from "../api/campaigns";
import { SearchForm } from "../components/SearchForm";
import { CampaignList } from "../components/CampaignList";

const SEARCH_FIELDS: SearchField[] = ["publisherName", "gameName", "bggUsername"];

// Kryteria wyszukiwania trzymamy w URL (?gameName=...), żeby po zalogowaniu wrócić do tych samych wyników
function readSearch(searchParams: URLSearchParams): SearchParams {
  for (const field of SEARCH_FIELDS) {
    const value = searchParams.get(field);
    if (value) {
      return { field, value };
    }
  }
  return {};
}

export function CampaignsPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const { field, value } = readSearch(searchParams);

  const [campaigns, setCampaigns] = useState<CampaignResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;
    setIsLoading(true);
    setError(null);
    searchCampaigns({ field, value })
      .then((result) => {
        if (!cancelled) setCampaigns(result);
      })
      .catch((e: Error) => {
        if (!cancelled) setError(e.message);
      })
      .finally(() => {
        if (!cancelled) setIsLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [field, value]);

  function handleSearch(newField?: SearchField, newValue?: string) {
    setSearchParams(newField && newValue ? { [newField]: newValue } : {});
  }

  return (
    <div className="campaigns-page">
      <h1>Active Gamefound Campaigns</h1>
      <p className="intro">
        Find active crowdfunding campaigns by searching for a creator, a favorite game (this
        matches campaigns from that game's creator), or your BoardGameGeek username (matches
        campaigns from the creators of every game on your BGG list, not just the ones you own).
      </p>
      {/* key: przy zmianie URL (np. przycisk Wstecz) formularz startuje od nowa z wartościami z URL */}
      <SearchForm
        key={`${field ?? ""}:${value ?? ""}`}
        initialField={field}
        initialValue={value}
        onSearch={handleSearch}
      />
      <CampaignList campaigns={campaigns} isLoading={isLoading} error={error} />
      <footer className="page-footer">
        <a href="https://boardgamegeek.com" target="_blank" rel="noreferrer">
          <img src="/bgg-logo.jpg" alt="Powered by BoardGameGeek" />
        </a>
      </footer>
    </div>
  );
}
