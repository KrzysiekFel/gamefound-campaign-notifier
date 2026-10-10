import type { CampaignResponse } from "../api/campaigns";
import { CampaignCard } from "./CampaignCard";

interface CampaignListProps {
  campaigns: CampaignResponse[];
  isLoading: boolean;
  error: string | null;
}

export function CampaignList({ campaigns, isLoading, error }: CampaignListProps) {
  if (isLoading) {
    return <p className="status">Loading...</p>;
  }

  if (error) {
    return <p className="status status-error">Error: {error}</p>;
  }

  if (campaigns.length === 0) {
    return <p className="status">No campaigns match your criteria.</p>;
  }

  return (
    <div className="campaign-list">
      {campaigns.map((campaign, index) => (
        <CampaignCard key={campaign.projectHomeUrl ?? index} campaign={campaign} />
      ))}
    </div>
  );
}
