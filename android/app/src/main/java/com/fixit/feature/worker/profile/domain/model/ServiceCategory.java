package com.fixit.feature.worker.profile.domain.model;

public class ServiceCategory {

    private final int id;
    private final String serviceName;

    public ServiceCategory(int id, String serviceName) {
        this.id = id;
        this.serviceName = serviceName;
    }

    public int getId() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }
}
