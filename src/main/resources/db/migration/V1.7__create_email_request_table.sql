CREATE TABLE email_request (
    id               UUID PRIMARY KEY,
    type             VARCHAR(30)   NOT NULL,
    status           VARCHAR(20)   NOT NULL,
    recipient        VARCHAR(255)  NOT NULL,
    user_id          BIGINT,
    project_url_name VARCHAR(255),
    error_message    VARCHAR(1000),
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    sent_at          TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_email_request_user
        FOREIGN KEY (user_id)
        REFERENCES app_user(id)
);

CREATE INDEX idx_email_request_status_created_at ON email_request (status, created_at);