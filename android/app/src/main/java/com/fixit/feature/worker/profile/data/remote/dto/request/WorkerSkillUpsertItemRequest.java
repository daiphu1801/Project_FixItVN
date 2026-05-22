package com.fixit.feature.worker.profile.data.remote.dto.request;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class WorkerSkillUpsertItemRequest {

    @SerializedName("serviceId")
    private final int serviceId;

    @SerializedName("basePrice")
    private final BigDecimal basePrice;

    public WorkerSkillUpsertItemRequest(int serviceId, BigDecimal basePrice) {
        this.serviceId = serviceId;
        this.basePrice = basePrice;
    }
}