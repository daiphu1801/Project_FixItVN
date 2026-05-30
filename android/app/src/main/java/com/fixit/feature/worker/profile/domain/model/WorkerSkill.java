package com.fixit.feature.worker.profile.domain.model;

public class WorkerSkill {

    private final Integer serviceId;
    private final String serviceName;
    private final double basePrice;
    private final String customServiceName;

    public WorkerSkill(Integer serviceId, String serviceName, double basePrice) {
        this(serviceId, serviceName, basePrice, null);
    }

    public WorkerSkill(Integer serviceId, String serviceName, double basePrice, String customServiceName) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.basePrice = basePrice;
        this.customServiceName = customServiceName;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public String getCustomServiceName() {
        return customServiceName;
    }
}