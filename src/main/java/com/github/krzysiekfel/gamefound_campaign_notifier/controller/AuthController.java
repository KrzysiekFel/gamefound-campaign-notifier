package com.github.krzysiekfel.gamefound_campaign_notifier.controller;

import com.github.krzysiekfel.gamefound_campaign_notifier.controller.api.AuthApi;
import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.RegisterRequest;
import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.UserResponse;
import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.LoginRequest;
import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.LoginResponse;
import com.github.krzysiekfel.gamefound_campaign_notifier.entity.User;
import com.github.krzysiekfel.gamefound_campaign_notifier.mapper.UserMapper;
import com.github.krzysiekfel.gamefound_campaign_notifier.security.AccessToken;
import com.github.krzysiekfel.gamefound_campaign_notifier.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneOffset;

@RestController
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final UserMapper userMapper;

    public AuthController(AuthService authService, UserMapper userMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
    }

    @Override
    public ResponseEntity<UserResponse> register(RegisterRequest registerRequest) {
        User user = authService.register(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                registerRequest.getPassword());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userMapper.toUserResponse(user));
    }

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        AccessToken token = authService.login(loginRequest.getEmail(), loginRequest.getPassword());

        LoginResponse response = new LoginResponse()
                .accessToken(token.value())
                .tokenType("Bearer")
                .expiresAt(token.expiresAt().atOffset(ZoneOffset.UTC));

        return ResponseEntity.ok(response);
    }
}
