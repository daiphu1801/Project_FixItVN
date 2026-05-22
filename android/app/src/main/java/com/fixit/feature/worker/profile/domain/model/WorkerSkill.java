package com.fixit.feature.worker.profile.domain.model;

public class WorkerSkill {

    private final int serviceId;
    private final String serviceName;
    private final double basePrice;

    public WorkerSkill(int serviceId, String serviceName, double basePrice) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.basePrice = basePrice;
    }

    public int getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getBasePrice() {
        return basePrice;
    }
}