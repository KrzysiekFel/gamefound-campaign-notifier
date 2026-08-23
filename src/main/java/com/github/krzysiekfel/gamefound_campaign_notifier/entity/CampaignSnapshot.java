package com.github.krzysiekfel.gamefound_campaign_notifier.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CampaignSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_name", nullable = false)
    private String gameName;

    @Column(name = "creator_name", nullable = false)
    private String creatorName;

    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public CampaignSnapshot(String gameName, String creatorName, Instant startDate, Instant endDate) {
        this.gameName = gameName;
        this.creatorName = creatorName;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
