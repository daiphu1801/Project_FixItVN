package com.fixit.feature.auth.domain.model;

public class Session {
    private final String accessToken;
    private final String refreshToken;
    private final User user;

    public Session(String accessToken, String refreshToken, User user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public User getUser() {
        return user;
    }
}
