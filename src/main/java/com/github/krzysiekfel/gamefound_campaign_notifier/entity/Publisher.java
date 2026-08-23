package com.github.krzysiekfel.gamefound_campaign_notifier.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bgg_id", nullable = false, unique = true)
    private Long bggId;

    @Column(nullable = false)
    private String name;

    public Publisher(Long bggId, String name) {
        this.bggId = bggId;
        this.name = name;
    }
}