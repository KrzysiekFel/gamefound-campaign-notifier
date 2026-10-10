package com.github.krzysiekfel.gamefound_campaign_notifier.notification;

import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.CampaignResponse;
import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class CampaignEmailContentBuilder {

    private static final String TEMPLATE = "email/campaign-share";

    private final ITemplateEngine templateEngine;

    public CampaignEmailContentBuilder(ITemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public EmailContent build(CampaignResponse campaign) {
        Context context = new Context();
        context.setVariable("campaign", campaign);

        String subject = "Gamefound campaign: " + campaign.getProjectName();
        String htmlBody = templateEngine.process(TEMPLATE, context);

        return new EmailContent(subject, htmlBody);
    }
}