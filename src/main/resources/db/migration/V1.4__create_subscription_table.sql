CREATE TABLE subscription (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    publisher_id BIGINT NOT NULL,
    frequency VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL ,

    CONSTRAINT fk_subscription_user
        FOREIGN KEY (user_id)
        REFERENCES app_user(id),

    CONSTRAINT fk_subscription_publisher
        FOREIGN KEY (publisher_id)
        REFERENCES publisher(id),

    CONSTRAINT uk_subscription_user_publisher
        UNIQUE (user_id, publisher_id)
)