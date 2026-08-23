CREATE TABLE favorite_publisher (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    publisher_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_favorite_publisher_user
        FOREIGN KEY (user_id)
        REFERENCES app_user(id),

    CONSTRAINT fk_favorite_publisher_publisher
        FOREIGN KEY (publisher_id)
        REFERENCES publisher(id),

    CONSTRAINT uk_favorite_user_publisher
        UNIQUE (user_id, publisher_id)
);