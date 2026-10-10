package com.github.krzysiekfel.gamefound_campaign_notifier.mapper;

import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.GameSuggestionResponse;
import com.github.krzysiekfel.gamefound_campaign_notifier.entity.Game;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameSuggestionMapper {

    GameSuggestionResponse toGameSuggestionResponse(Game game);
}
