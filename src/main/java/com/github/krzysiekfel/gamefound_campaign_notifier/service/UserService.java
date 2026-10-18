package com.github.krzysiekfel.gamefound_campaign_notifier.service;

import com.github.krzysiekfel.gamefound_campaign_notifier.entity.User;
import com.github.krzysiekfel.gamefound_campaign_notifier.exception.UserNotFoundException;
import com.github.krzysiekfel.gamefound_campaign_notifier.repository.UserRepository;
import com.github.krzysiekfel.gamefound_campaign_notifier.security.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;

    public UserService(UserRepository userRepository, CurrentUserProvider currentUserProvider) {
        this.userRepository = userRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Long userId = currentUserProvider.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
