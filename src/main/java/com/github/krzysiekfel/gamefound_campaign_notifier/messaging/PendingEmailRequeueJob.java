package com.github.krzysiekfel.gamefound_campaign_notifier.messaging;

import com.github.krzysiekfel.gamefound_campaign_notifier.entity.EmailRequest;
import com.github.krzysiekfel.gamefound_campaign_notifier.enums.EmailRequestStatus;
import com.github.krzysiekfel.gamefound_campaign_notifier.repository.EmailRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class PendingEmailRequeueJob {

    private static final Duration STUCK_THRESHOLD = Duration.ofMinutes(5);

    private final EmailRequestRepository emailRequestRepository;
    private final EmailRequestPublisher emailRequestPublisher;

    public PendingEmailRequeueJob(EmailRequestRepository emailRequestRepository,
                                  EmailRequestPublisher emailRequestPublisher) {
        this.emailRequestRepository = emailRequestRepository;
        this.emailRequestPublisher = emailRequestPublisher;
    }

    @Scheduled(initialDelay = 1, fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    public void requeueStuckRequests() {
        Instant createdBefore = Instant.now().minus(STUCK_THRESHOLD);
        List<EmailRequest> stuckRequests =
                emailRequestRepository.findByStatusAndCreatedAtBefore(EmailRequestStatus.PENDING, createdBefore);

        if (stuckRequests.isEmpty()) {
            return;
        }

        log.warn("Found {} email requests stuck in PENDING - republishing", stuckRequests.size());
        stuckRequests.forEach(request -> emailRequestPublisher.publish(request.getId()));
    }
}
