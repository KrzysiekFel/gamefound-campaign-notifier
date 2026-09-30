package com.github.krzysiekfel.gamefound_campaign_notifier.controller;

import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.GameSuggestionResponse;
import com.github.krzysiekfel.gamefound_campaign_notifier.entity.Game;
import com.github.krzysiekfel.gamefound_campaign_notifier.mapper.GameSuggestionMapper;
import com.github.krzysiekfel.gamefound_campaign_notifier.service.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameSuggestionControllerTest {

    @Mock
    private GameService gameService;

    @Mock
    private GameSuggestionMapper gameSuggestionMapper;

    @InjectMocks
    private GameSuggestionController gameSuggestionController;

    @Test
    void shouldReturnMappedSuggestions() {
        // GIVEN
        Game game = new Game(174430L, "Gloomhaven", 2017, 4);
        GameSuggestionResponse response = new GameSuggestionResponse().name("Gloomhaven").yearPublished(2017);
        when(gameService.findSuggestions("gloom")).thenReturn(List.of(game));
        when(gameSuggestionMapper.toGameSuggestionResponse(game)).thenReturn(response);

        // WHEN
        ResponseEntity<List<GameSuggestionResponse>> result = gameSuggestionController.suggestGames("gloom");

        // THEN
        assertThat(result.getBody()).containsExactly(response);
    }

    @Test
    void shouldReturnEmptyListWhenNoSuggestions() {
        // GIVEN
        when(gameService.findSuggestions("xyz")).thenReturn(List.of());

        // WHEN
        ResponseEntity<List<GameSuggestionResponse>> result = gameSuggestionController.suggestGames("xyz");

        // THEN
        assertThat(result.getBody()).isEmpty();
    }
}
