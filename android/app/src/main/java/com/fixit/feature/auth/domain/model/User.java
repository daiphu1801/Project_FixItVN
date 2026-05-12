package com.fixit.feature.auth.domain.model;

public class User {
    private final String id;
    private final String phone;
    private final String fullName;
    private final UserRole role;

    public User(String id, String phone, String fullName, UserRole role) {
        this.id = id;
        this.phone = phone;
        this.fullName = fullName;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getFullName() {
        return fullName;
    }

    public UserRole getRole() {
        return role;
    }
}
