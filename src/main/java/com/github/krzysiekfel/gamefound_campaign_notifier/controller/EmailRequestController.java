package com.github.krzysiekfel.gamefound_campaign_notifier.controller;

import com.github.krzysiekfel.gamefound_campaign_notifier.controller.api.EmailRequestsApi;
import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.CreateEmailRequest;
import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.EmailRequestResponse;
import com.github.krzysiekfel.gamefound_campaign_notifier.entity.EmailRequest;
import com.github.krzysiekfel.gamefound_campaign_notifier.mapper.EmailRequestMapper;
import com.github.krzysiekfel.gamefound_campaign_notifier.service.EmailRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
public class EmailRequestController implements EmailRequestsApi {

    private final EmailRequestService emailRequestService;
    private final EmailRequestMapper emailRequestMapper;

    public EmailRequestController(EmailRequestService emailRequestService,
                                  EmailRequestMapper emailRequestMapper) {
        this.emailRequestService = emailRequestService;
        this.emailRequestMapper = emailRequestMapper;
    }

    @Override
    public ResponseEntity<EmailRequestResponse> createEmailRequest(CreateEmailRequest createEmailRequest) {
        EmailRequest emailRequest =
                emailRequestService.requestCampaignShare(createEmailRequest.getProjectUrlName());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(emailRequest.getId())
                .toUri();

        return ResponseEntity.accepted()
                .location(location)
                .body(emailRequestMapper.toEmailRequestResponse(emailRequest));
    }

    @Override
    public ResponseEntity<EmailRequestResponse> getEmailRequest(UUID id) {
        return ResponseEntity.of(emailRequestService.findByIdForCurrentUser(id)
                .map(emailRequestMapper::toEmailRequestResponse));
    }
}
