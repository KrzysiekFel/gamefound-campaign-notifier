package com.github.krzysiekfel.gamefound_campaign_notifier.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class GameUpsertRepository {

    private static final String UPSERT_SQL = """                                                                                              
              INSERT INTO game (bgg_id, name, year_published, rank)
              VALUES (?, ?, ?, ?)
              ON CONFLICT (bgg_id) DO UPDATE SET
                  name = EXCLUDED.name,
                  year_published = EXCLUDED.year_published,
                  rank = EXCLUDED.rank
              """;

    private final JdbcTemplate jdbcTemplate;

    public GameUpsertRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void upsertAll(List<Object[]> rows) {
        jdbcTemplate.batchUpdate(UPSERT_SQL, rows);
        log.debug("Upserted {} games", rows.size());
    }
}
