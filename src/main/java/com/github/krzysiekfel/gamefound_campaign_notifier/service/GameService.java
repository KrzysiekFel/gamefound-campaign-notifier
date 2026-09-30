package com.github.krzysiekfel.gamefound_campaign_notifier.service;

import com.github.krzysiekfel.gamefound_campaign_notifier.entity.Game;
import com.github.krzysiekfel.gamefound_campaign_notifier.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    private static final int MIN_SUGGESTION_QUERY_LENGTH = 2;

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Optional<Game> findByName(String name) {
        return gameRepository.findFirstByNameIgnoreCaseOrderByRankAsc(name);
    }

    public List<Game> findSuggestions(String query) {
        if (query == null || query.trim().length() < MIN_SUGGESTION_QUERY_LENGTH) {
            return List.of();
        }
        return gameRepository.findTop10ByNameContainingIgnoreCaseOrderByNameAsc(query.trim());
    }
}
