package com.github.krzysiekfel.gamefound_campaign_notifier.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
public class FavoritePublisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected FavoritePublisher() {
    }

    public FavoritePublisher(User user, Publisher publisher) {
        this.user = user;
        this.publisher = publisher;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
