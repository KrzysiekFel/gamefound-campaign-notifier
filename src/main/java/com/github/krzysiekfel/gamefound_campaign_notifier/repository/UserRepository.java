package com.github.krzysiekfel.gamefound_campaign_notifier.repository;

import com.github.krzysiekfel.gamefound_campaign_notifier.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findByEmail(String email);
}
