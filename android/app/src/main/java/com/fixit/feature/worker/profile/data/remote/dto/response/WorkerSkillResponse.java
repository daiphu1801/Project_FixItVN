package com.fixit.feature.worker.profile.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class WorkerSkillResponse {

    @SerializedName("serviceId")
    private Integer serviceId;

    @SerializedName("serviceName")
    private String serviceName;

    @SerializedName("basePrice")
    private BigDecimal basePrice;

    public Integer getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }
}