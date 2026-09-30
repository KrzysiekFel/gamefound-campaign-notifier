import type { CampaignResponse } from "../api/campaigns";

function formatDate(value?: string) {
  if (!value) return "-";
  return new Date(value).toLocaleDateString();
}

export function CampaignCard({ campaign }: { campaign: CampaignResponse }) {
  const goal = campaign.campaignGoal ?? 0;
  const gathered = campaign.fundsGathered ?? 0;
  const progress = goal > 0 ? Math.min(100, Math.round((gathered / goal) * 100)) : 0;

  return (
    <article className="campaign-card">
      {campaign.projectImageUrl && (
        <img src={campaign.projectImageUrl} alt={campaign.projectName ?? ""} loading="lazy" />
      )}
      <div className="campaign-card-body">
        <h3>{campaign.projectName}</h3>
        <p className="creator">{campaign.creatorName}</p>

        <div className="progress-bar" title={`${progress}%`}>
          <div className="progress-bar-fill" style={{ width: `${progress}%` }} />
        </div>
        <p className="funding">
          {gathered.toLocaleString()} / {goal.toLocaleString()} {campaign.currencyShortName} ·{" "}
          {campaign.backerCount ?? 0} backers
        </p>

        <p className="dates">
          {formatDate(campaign.campaignStartDate)} – {formatDate(campaign.campaignEndDate)}
        </p>

        {campaign.projectHomeUrl && (
          <a href={campaign.projectHomeUrl} target="_blank" rel="noreferrer">
            View on Gamefound
          </a>
        )}
      </div>
    </article>
  );
}
