import { useEffect, useState } from "react";
import { searchCampaigns, type CampaignResponse, type SearchField } from "../api/campaigns";
import { SearchForm } from "../components/SearchForm";
import { CampaignList } from "../components/CampaignList";

export function CampaignsPage() {
  const [campaigns, setCampaigns] = useState<CampaignResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  function load(field?: SearchField, value?: string) {
    setIsLoading(true);
    setError(null);
    searchCampaigns({ field, value })
      .then(setCampaigns)
      .catch((e: Error) => setError(e.message))
      .finally(() => setIsLoading(false));
  }

  useEffect(() => {
    load();
  }, []);

  return (
    <div className="campaigns-page">
      <h1>Active Gamefound Campaigns</h1>
      <p className="intro">
        Find active crowdfunding campaigns by searching for a creator, a favorite game (this
        matches campaigns from that game's creator), or your BoardGameGeek username (matches
        campaigns from the creators of every game on your BGG list, not just the ones you own).
      </p>
      <SearchForm onSearch={load} />
      <CampaignList campaigns={campaigns} isLoading={isLoading} error={error} />
      <footer className="page-footer">
        <a href="https://boardgamegeek.com" target="_blank" rel="noreferrer">
          <img src="/bgg-logo.jpg" alt="Powered by BoardGameGeek" />
        </a>
      </footer>
    </div>
  );
}
