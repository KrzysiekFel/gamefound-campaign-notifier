package com.github.krzysiekfel.gamefound_campaign_notifier.entity;

import com.github.krzysiekfel.gamefound_campaign_notifier.enums.EmailRequestStatus;
import com.github.krzysiekfel.gamefound_campaign_notifier.enums.EmailRequestType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailRequest {
    private static final int ERROR_MESSAGE_MAX_LENGTH = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EmailRequestType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmailRequestStatus status;

    @Column(nullable = false)
    private String recipient;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "project_url_name")
    private String projectUrlName;

    @Column(name = "error_message", length = ERROR_MESSAGE_MAX_LENGTH)
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    private EmailRequest(EmailRequestType type, Long userId, String recipient, String projectUrlName) {
        this.type = type;
        this.status = EmailRequestStatus.PENDING;
        this.userId = userId;
        this.recipient = recipient;
        this.projectUrlName = projectUrlName;
    }

    public static EmailRequest campaignShare(Long userId, String recipient, String projectUrlName) {
        return new EmailRequest(EmailRequestType.CAMPAIGN_SHARE, userId, recipient, projectUrlName);
    }

    public boolean isPending() {
        return status == EmailRequestStatus.PENDING;
    }

    public void markSent() {
        this.status = EmailRequestStatus.SENT;
        this.sentAt = Instant.now();
        this.errorMessage = null;
    }

    public void markFailed(String reason) {
        this.status = EmailRequestStatus.FAILED;
        this.errorMessage = reason != null && reason.length() > ERROR_MESSAGE_MAX_LENGTH
                ? reason.substring(0, ERROR_MESSAGE_MAX_LENGTH)
                : reason;
    }
}
