package com.github.krzysiekfel.gamefound_campaign_notifier.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bgg_id", nullable = false, unique = true)
    private Long bggId;

    @Column(nullable = false)
    private String name;

    @Column(name = "year_published", nullable = false)
    private Integer yearPublished;

    @Column(nullable = false)
    private Integer rank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    public Long getBggId() {
        return bggId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getYearPublished() {
        return yearPublished;
    }

    public Integer getRank() {
        return rank;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public Game(Long bggId, String name, Integer yearPublished, Integer rank) {
        this.bggId = bggId;
        this.name = name;
        this.yearPublished = yearPublished;
        this.rank = rank;
    }
}