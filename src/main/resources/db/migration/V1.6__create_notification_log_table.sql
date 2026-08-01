CREATE TABLE notification_log (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    campaign_id BIGINT NOT NULL,
    sent_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_notification_log_user
        FOREIGN KEY (user_id)
        REFERENCES app_user(id),

    CONSTRAINT fk_notification_log_campaign
        FOREIGN KEY (campaign_id)
        REFERENCES campaign_snapshot(id)
);