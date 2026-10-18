package com.github.krzysiekfel.gamefound_campaign_notifier.controller;

import com.github.krzysiekfel.gamefound_campaign_notifier.controller.api.UsersApi;
import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.UserResponse;
import com.github.krzysiekfel.gamefound_campaign_notifier.mapper.UserMapper;
import com.github.krzysiekfel.gamefound_campaign_notifier.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController implements UsersApi {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Override
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userMapper.toUserResponse(userService.getCurrentUser()));
    }
}
