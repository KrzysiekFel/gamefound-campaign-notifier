CREATE TABLE game (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    bgg_id BIGINT NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    year_published BIGINT NOT NULL,
    rank BIGINT NOT NULL,
    publisher_id BIGINT,

    CONSTRAINT fk_game_publisher
        FOREIGN KEY (publisher_id)
        REFERENCES publisher(id)
);