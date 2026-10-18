package com.github.krzysiekfel.gamefound_campaign_notifier.service;

import com.github.krzysiekfel.gamefound_campaign_notifier.entity.User;
import com.github.krzysiekfel.gamefound_campaign_notifier.exception.UserNotFoundException;
import com.github.krzysiekfel.gamefound_campaign_notifier.repository.UserRepository;
import com.github.krzysiekfel.gamefound_campaign_notifier.security.CurrentUserProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final Long USER_ID = 7L;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnCurrentUser() {
        // GIVEN
        User user = new User("krzysiek", "test.user@example.com", "$2a$10$hash");
        when(currentUserProvider.getCurrentUserId()).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        // WHEN
        User result = userService.getCurrentUser();

        // THEN
        assertThat(result).isSameAs(user);
    }

    @Test
    void shouldFailWhenUserFromTokenNoLongerExists() {
        // GIVEN – token is still valid, but the account was deleted
        when(currentUserProvider.getCurrentUserId()).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User 7 not found");
    }
}
