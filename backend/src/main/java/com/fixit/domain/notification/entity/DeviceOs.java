package com.fixit.domain.notification.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum DeviceOs {
    Android("Android"),
    iOS("iOS"),
    Web("Web");

    private final String dbValue;

    DeviceOs(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    @JsonCreator
    public static DeviceOs fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (DeviceOs deviceOs : values()) {
            if (deviceOs.name().equalsIgnoreCase(value) || deviceOs.dbValue.equalsIgnoreCase(value)) {
                return deviceOs;
            }
        }
        throw new IllegalArgumentException("Unknown device OS: " + value);
    }

    public static DeviceOs fromDbValue(String dbValue) {
        for (DeviceOs deviceOs : values()) {
            if (deviceOs.dbValue.equals(dbValue)) {
                return deviceOs;
            }
        }
        throw new IllegalArgumentException("Unknown device OS: " + dbValue);
    }
}
