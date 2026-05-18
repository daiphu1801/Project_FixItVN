package com.fixit.domain.worker.entity;

import com.fixit.domain.service.entity.Category;
import com.fixit.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private Integer experienceYears;

    private Double latitude;

    private Double longitude;

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private Integer totalJobs;

    @Column(nullable = false)
    private Boolean available;

    @Column(nullable = false)
    private Boolean verified;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (rating == null) {
            rating = 0.0;
        }
        if (totalJobs == null) {
            totalJobs = 0;
        }
        if (available == null) {
            available = true;
        }
        if (verified == null) {
            verified = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
