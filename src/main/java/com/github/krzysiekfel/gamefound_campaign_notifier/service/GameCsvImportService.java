package com.github.krzysiekfel.gamefound_campaign_notifier.service;

import com.github.krzysiekfel.gamefound_campaign_notifier.repository.GameUpsertRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class GameCsvImportService {  // TODO: for future: Accepted and the async by email

    private static final int BATCH_SIZE = 1000;
    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .build();

    private final GameUpsertRepository gameUpsertRepository;

    public GameCsvImportService(GameUpsertRepository gameUpsertRepository) {
        this.gameUpsertRepository = gameUpsertRepository;
    }

    public void importFromCsv(InputStream inputStream) throws IOException {
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             CSVParser parser = CSV_FORMAT.parse(reader)) {

            Iterator<Object[]> rows = parser.stream()
                    .filter(record -> !isExpansion(record))
                    .map(this::toRow)
                    .iterator();

            while (rows.hasNext()) {
                List<Object[]> batch = nextBatch(rows);
                gameUpsertRepository.upsertAll(batch);
            }
        }
    }

    private List<Object[]> nextBatch(Iterator<Object[]> rows) {
        List<Object[]> batch = new ArrayList<>(BATCH_SIZE);
        while (rows.hasNext() && batch.size() < BATCH_SIZE) {
            batch.add(rows.next());
        }
        return batch;
    }

    private boolean isExpansion(CSVRecord record) {
        return "1".equals(record.get("is_expansion"));
    }

    private Object[] toRow(CSVRecord record) {
        return new Object[]{
                Long.parseLong(record.get("id")),
                record.get("name"),
                parseIntOrNull(record.get("yearpublished")),
                parseIntOrNull(record.get("rank"))
        };
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
}