package com.github.krzysiekfel.gamefound_campaign_notifier.notification;

public interface EmailSender {
    void send(String to, String subject, String htmlBody);
}
