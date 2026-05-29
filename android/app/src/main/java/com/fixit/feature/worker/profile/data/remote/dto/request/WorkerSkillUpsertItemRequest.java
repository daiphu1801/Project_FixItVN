package com.fixit.feature.worker.profile.data.remote.dto.request;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class WorkerSkillUpsertItemRequest {

    @SerializedName("serviceId")
    private final Integer serviceId;

    @SerializedName("customServiceName")
    private final String customServiceName;

    @SerializedName("basePrice")
    private final BigDecimal basePrice;

    public WorkerSkillUpsertItemRequest(Integer serviceId, BigDecimal basePrice) {
        this(serviceId, null, basePrice);
    }

    public WorkerSkillUpsertItemRequest(Integer serviceId, String customServiceName, BigDecimal basePrice) {
        this.serviceId = serviceId;
        this.customServiceName = customServiceName;
        this.basePrice = basePrice;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public String getCustomServiceName() {
        return customServiceName;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }
}