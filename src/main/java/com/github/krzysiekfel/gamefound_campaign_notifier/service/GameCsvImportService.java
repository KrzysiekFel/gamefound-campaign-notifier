package com.github.krzysiekfel.gamefound_campaign_notifier.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class GameCsvImportService {

    private static final int BATCH_SIZE = 1000;
    private static final String UPSERT_SQL = """                     
              INSERT INTO game (bgg_id, name, year_published, rank)    
              VALUES (?, ?, ?, ?)                                      
              ON CONFLICT (bgg_id) DO UPDATE SET                       
                  name = EXCLUDED.name,                                
                  year_published = EXCLUDED.year_published,            
                  rank = EXCLUDED.rank                                 
              """;

    private final JdbcTemplate jdbcTemplate;

    public GameCsvImportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public GameImportSummary importFromCsv(InputStream inputStream)
            throws IOException {
        int imported = 0;
        int skipped = 0;

        try (Reader reader = new InputStreamReader(inputStream,
                StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .build()
                     .parse(reader)) {

            List<Object[]> batch = new ArrayList<>(BATCH_SIZE);

            for (CSVRecord record : parser) {
                if ("1".equals(record.get("is_expansion"))) {
                    skipped++;
                    continue;
                }

                Integer yearPublished =
                        parseIntOrNull(record.get("yearpublished"));
                Integer rank = parseIntOrNull(record.get("rank"));

                batch.add(new Object[]{
                        Long.parseLong(record.get("id")),
                        record.get("name"),
                        yearPublished,
                        rank
                });
                imported++;

                if (batch.size() == BATCH_SIZE) {
                    upsertBatch(batch);
                    batch.clear();
                }
            }
            if (!batch.isEmpty()) {
                upsertBatch(batch);
            }
        }

        return new GameImportSummary(imported, skipped);
    }

    private void upsertBatch(List<Object[]> batch) {
        jdbcTemplate.batchUpdate(UPSERT_SQL, batch);
    }

    private Integer parseIntOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public record GameImportSummary(int imported, int skipped) {
    }
}