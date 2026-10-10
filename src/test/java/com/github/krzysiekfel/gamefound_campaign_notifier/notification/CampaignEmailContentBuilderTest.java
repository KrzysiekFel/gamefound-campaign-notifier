package com.github.krzysiekfel.gamefound_campaign_notifier.notification;

import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.CampaignResponse;
import org.junit.jupiter.api.Test;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignEmailContentBuilderTest {

    private final CampaignEmailContentBuilder campaignEmailContentBuilder =
            new CampaignEmailContentBuilder(createTemplateEngine());

    @Test
    void shouldRenderCampaignDataIntoEmail() {
        // GIVEN
        CampaignResponse campaign = new CampaignResponse()
                .projectName("Age of Skorpa")
                .creatorName("Skorpa Games")
                .projectHomeUrl(URI.create("https://gamefound.com/en/projects/skorpa/age-of-skorpa"));

        // WHEN
        EmailContent content = campaignEmailContentBuilder.build(campaign);

        // THEN
        assertThat(content.subject()).isEqualTo("Gamefound campaign: Age of Skorpa");
        assertThat(content.htmlBody())
                .contains("Age of Skorpa")
                .contains("Skorpa Games")
                .contains("https://gamefound.com/en/projects/skorpa/age-of-skorpa")
                .doesNotContain("th:text", "th:if", "th:href", "th:src");
    }

    @Test
    void shouldEscapeHtmlFromCampaignData() {
        // GIVEN
        CampaignResponse campaign = new CampaignResponse()
                .projectName("Safe name")
                .shortDescription("<a href=\"http://phishing.example\">Click me</a>");

        // WHEN
        EmailContent content = campaignEmailContentBuilder.build(campaign);

        // THEN
        assertThat(content.htmlBody())
                .contains("&lt;a href=")
                .doesNotContain("<a href=\"http://phishing.example\">");
    }

    private static ITemplateEngine createTemplateEngine() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }
}