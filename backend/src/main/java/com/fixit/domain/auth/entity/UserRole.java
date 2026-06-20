package com.fixit.domain.auth.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UserRole {
    Customer,
    Worker,
    Admin;

    @JsonCreator
    public static UserRole fromString(String value) {
        if (value == null) return null;
        for (UserRole role : UserRole.values()) {
            if (role.name().equalsIgnoreCase(value.trim())) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + value);
    }
}
