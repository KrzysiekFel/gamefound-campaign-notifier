package com.github.krzysiekfel.gamefound_campaign_notifier.repository;

import com.github.krzysiekfel.gamefound_campaign_notifier.entity.EmailRequest;
import com.github.krzysiekfel.gamefound_campaign_notifier.enums.EmailRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmailRequestRepository extends JpaRepository<EmailRequest, UUID> {

    List<EmailRequest> findByStatusAndCreatedAtBefore(EmailRequestStatus status,
                                                      Instant createdBefore);

    Optional<EmailRequest> findByIdAndUserId(UUID id, Long userId);
}
