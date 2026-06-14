package com.fixit.feature.customer.workerprofile.domain.model;

import java.math.BigDecimal;

public class PublicWorkerSkill {
    private Integer serviceId;
    private String serviceName;
    private String iconUrl;
    private BigDecimal basePrice;

    public PublicWorkerSkill(Integer serviceId, String serviceName, String iconUrl, BigDecimal basePrice) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.iconUrl = iconUrl;
        this.basePrice = basePrice;
    }

    public Integer getServiceId() { return serviceId; }
    public String getServiceName() { return serviceName; }
    public String getIconUrl() { return iconUrl; }
    public BigDecimal getBasePrice() { return basePrice; }
}
