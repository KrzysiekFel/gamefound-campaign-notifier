package com.github.krzysiekfel.gamefound_campaign_notifier.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bgg_id")
    private Long bggId;

    @Column(nullable = false)
    private String name;

    protected Publisher() {
    }

    public Publisher(Long bggId, String name) {
        this.bggId = bggId;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public Long getBggId() {
        return bggId;
    }

    public String getName() {
        return name;
    }
}