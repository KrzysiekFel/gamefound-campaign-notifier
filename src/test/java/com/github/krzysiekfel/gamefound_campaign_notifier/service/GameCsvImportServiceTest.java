package com.github.krzysiekfel.gamefound_campaign_notifier.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameCsvImportServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private GameCsvImportService gameCsvImportService;

    @BeforeEach
    void setUp() {
        gameCsvImportService = new GameCsvImportService(jdbcTemplate);
    }

    @Test
    void shouldSkipExpansions() throws IOException {
        // GIVEN
        String csv = """                                                                     
                  id,name,yearpublished,rank,is_expansion
                  174430,Gloomhaven,2017,4,0
                  999999,Some Expansion,2018,0,1
                  """;

        // WHEN
        GameCsvImportService.GameImportSummary summary =
                gameCsvImportService.importFromCsv(toInputStream(csv));

        // THEN
        assertThat(summary.imported()).isEqualTo(1);
        assertThat(summary.skipped()).isEqualTo(1);
        verify(jdbcTemplate, times(1)).batchUpdate(anyString(), anyList());
    }

    @Test
    void shouldImportMultipleValidRows() throws IOException {
        // GIVEN
        String csv = """                                                                     
                  id,name,yearpublished,rank,is_expansion
                  174430,Gloomhaven,2017,4,0
                  161936,Pandemic Legacy: Season 1,2015,3,0
                  342942,Ark Nova,2021,2,0
                  """;

        // WHEN
        GameCsvImportService.GameImportSummary summary =
                gameCsvImportService.importFromCsv(toInputStream(csv));

        // THEN
        assertThat(summary.imported()).isEqualTo(3);
        assertThat(summary.skipped()).isEqualTo(0);
    }

    @Test
    void shouldSplitIntoMultipleBatchesWhenExceedingBatchSize() throws IOException {
        // GIVEN
        StringBuilder csv = new StringBuilder("id,name,yearpublished,rank,is_expansion\n");
        for (int i = 0; i < 1001; i++) {
            csv.append(i).append(",Game ").append(i).append(",2020,1,0\n");
        }

        // WHEN
        gameCsvImportService.importFromCsv(toInputStream(csv.toString()));

        // THEN
        verify(jdbcTemplate, times(2)).batchUpdate(anyString(), anyList());
    }

    private ByteArrayInputStream toInputStream(String csv) {
        return new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
    }
}
