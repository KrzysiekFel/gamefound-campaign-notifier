package com.github.krzysiekfel.gamefound_campaign_notifier.controller;

import com.github.krzysiekfel.gamefound_campaign_notifier.controller.api.GamesApi;
import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.GameSuggestionResponse;
import com.github.krzysiekfel.gamefound_campaign_notifier.mapper.GameSuggestionMapper;
import com.github.krzysiekfel.gamefound_campaign_notifier.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GameSuggestionController implements GamesApi {

    private final GameService gameService;
    private final GameSuggestionMapper gameSuggestionMapper;

    public GameSuggestionController(GameService gameService, GameSuggestionMapper gameSuggestionMapper) {
        this.gameService = gameService;
        this.gameSuggestionMapper = gameSuggestionMapper;
    }

    @Override
    public ResponseEntity<List<GameSuggestionResponse>> suggestGames(String query) {
        List<GameSuggestionResponse> suggestions = gameService.findSuggestions(query).stream()
                .map(gameSuggestionMapper::toGameSuggestionResponse)
                .toList();
        return ResponseEntity.ok(suggestions);
    }
}
