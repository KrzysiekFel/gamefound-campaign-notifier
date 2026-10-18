package com.github.krzysiekfel.gamefound_campaign_notifier.service;

import com.github.krzysiekfel.gamefound_campaign_notifier.entity.User;
import com.github.krzysiekfel.gamefound_campaign_notifier.exception.InvalidCredentialsException;
import com.github.krzysiekfel.gamefound_campaign_notifier.exception.UserAlreadyExistsException;
import com.github.krzysiekfel.gamefound_campaign_notifier.repository.UserRepository;
import com.github.krzysiekfel.gamefound_campaign_notifier.security.AccessToken;
import com.github.krzysiekfel.gamefound_campaign_notifier.security.TokenService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService
            tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Transactional
    public User register(String username, String email, String password) {
        String normalizedEmail = normalizeEmail(email);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new UserAlreadyExistsException("Email is already registered");
        }
        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException("Username is already taken");
        }

        User user = new User(username, normalizedEmail, passwordEncoder.encode(password));
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException("Email or username is already taken");
        }
    }

    @Transactional(readOnly = true)
    public AccessToken login(String email, String password) {
        User user = userRepository.findByEmail(normalizeEmail(email))
                .filter(found -> passwordEncoder.matches(password, found.getPasswordHash()))
                .orElseThrow(InvalidCredentialsException::new);

        return tokenService.generate(user);
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
