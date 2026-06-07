package com.fixit.feature.customer.favorite.domain.model;

import java.util.List;

public class FavoriteWorker {
    private final String workerId;
    private final String fullName;
    private final String avatarUrl;
    private final float rating;
    private final List<String> skills;
    private final boolean available;

    public FavoriteWorker(String workerId, String fullName, String avatarUrl, float rating, List<String> skills,
            boolean available) {
        this.workerId = workerId;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.rating = rating;
        this.skills = skills;
        this.available = available;
    }

    public String getWorkerId() {
        return workerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public float getRating() {
        return rating;
    }

    public List<String> getSkills() {
        return skills;
    }

    public boolean isAvailable() {
        return available;
    }
}
