package com.github.krzysiekfel.gamefound_campaign_notifier.mapper;

import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.UserResponse;
import com.github.krzysiekfel.gamefound_campaign_notifier.entity.User;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toUserResponse(User user);

    default OffsetDateTime map(Instant value) {
        return value == null ? null : value.atOffset(ZoneOffset.UTC);
    }
}
