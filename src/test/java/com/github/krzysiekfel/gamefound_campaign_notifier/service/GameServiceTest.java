package com.github.krzysiekfel.gamefound_campaign_notifier.service;

import com.github.krzysiekfel.gamefound_campaign_notifier.entity.Game;
import com.github.krzysiekfel.gamefound_campaign_notifier.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    @Test
    void shouldDelegateToRepositoryAndReturnResult() {
        // GIVEN
        Game game = new Game(174430L, "Gloomhaven", 2017, 4);
        when(gameRepository.findFirstByNameIgnoreCaseOrderByRankAsc("gloomhaven")).thenReturn
                (Optional.of(game));

        // WHEN
        Optional<Game> result = gameService.findByName("gloomhaven");

        // THEN
        assertThat(result).contains(game);
        verify(gameRepository).findFirstByNameIgnoreCaseOrderByRankAsc("gloomhaven");
    }

    @Test
    void shouldReturnEmptyWhenRepositoryFindsNothing() {
        // GIVEN
        when(gameRepository.findFirstByNameIgnoreCaseOrderByRankAsc("unknown")).thenReturn(Optional.empty());

        // WHEN
        Optional<Game> result = gameService.findByName("unknown");

        // THEN
        assertThat(result).isEmpty();
    }
}
