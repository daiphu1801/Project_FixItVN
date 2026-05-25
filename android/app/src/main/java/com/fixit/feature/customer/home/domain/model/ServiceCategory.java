package com.fixit.feature.customer.home.domain.model;

/**
 * Model class representing a service category (e.g., Plumbing, Electricity, etc.)
 */
public class ServiceCategory {
    private final String id;
    private final String name;
    private final String description;
    private final String iconUrl;

    public ServiceCategory(String id, String name, String description, String iconUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconUrl = iconUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIconUrl() {
        return iconUrl;
    }
}
