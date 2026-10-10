package com.github.krzysiekfel.gamefound_campaign_notifier.service;

import com.github.krzysiekfel.gamefound_campaign_notifier.repository.GameUpsertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameCsvImportServiceTest {

    @Mock
    private GameUpsertRepository gameUpsertRepository;

    @Captor
    private ArgumentCaptor<List<Object[]>> batchCaptor;

    private GameCsvImportService gameCsvImportService;

    @BeforeEach
    void setUp() {
        gameCsvImportService = new GameCsvImportService(gameUpsertRepository);
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
        gameCsvImportService.importFromCsv(toInputStream(csv));

        // THEN
        verify(gameUpsertRepository).upsertAll(batchCaptor.capture());
        assertThat(batchCaptor.getValue()).hasSize(1);
        assertThat(batchCaptor.getValue().getFirst()[0]).isEqualTo(174430L);
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
        gameCsvImportService.importFromCsv(toInputStream(csv));

        // THEN
        verify(gameUpsertRepository).upsertAll(batchCaptor.capture());
        assertThat(batchCaptor.getValue()).hasSize(3);
    }

    @Test
    void shouldNotCallRepositoryWhenCsvHasNoDataRows() throws IOException {
        // GIVEN
        String csv = "id,name,yearpublished,rank,is_expansion\n";

        // WHEN
        gameCsvImportService.importFromCsv(toInputStream(csv));

        // THEN
        verifyNoInteractions(gameUpsertRepository);
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
        verify(gameUpsertRepository, times(2)).upsertAll(batchCaptor.capture());
        assertThat(batchCaptor.getAllValues())
                .extracting(batch -> batch.size())
                .containsExactly(1000, 1);
    }

    private ByteArrayInputStream toInputStream(String csv) {
        return new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
    }
}
