package com.github.krzysiekfel.gamefound_campaign_notifier.controller;

import com.github.krzysiekfel.gamefound_campaign_notifier.dto.generated.UserResponse;
import com.github.krzysiekfel.gamefound_campaign_notifier.entity.User;
import com.github.krzysiekfel.gamefound_campaign_notifier.mapper.UserMapper;
import com.github.krzysiekfel.gamefound_campaign_notifier.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    @Test
    void shouldReturnCurrentUser() {
        // GIVEN
        User user = new User("krzysiek", "test.user@example.com", "$2a$10$hash");
        UserResponse response = new UserResponse()
                .username("krzysiek")
                .role(UserResponse.RoleEnum.USER);
        when(userService.getCurrentUser()).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(response);

        // WHEN
        ResponseEntity<UserResponse> result = userController.getCurrentUser();

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }
}
