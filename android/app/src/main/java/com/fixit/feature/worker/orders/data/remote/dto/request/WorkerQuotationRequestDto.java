package com.fixit.feature.worker.orders.data.remote.dto.request;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class WorkerQuotationRequestDto {
    @SerializedName("laborCost")
    private BigDecimal laborCost;

    @SerializedName("materialCost")
    private BigDecimal materialCost;

    public WorkerQuotationRequestDto(BigDecimal laborCost, BigDecimal materialCost) {
        this.laborCost = laborCost;
        this.materialCost = materialCost;
    }

    public BigDecimal getLaborCost() { return laborCost; }
    public void setLaborCost(BigDecimal laborCost) { this.laborCost = laborCost; }

    public BigDecimal getMaterialCost() { return materialCost; }
    public void setMaterialCost(BigDecimal materialCost) { this.materialCost = materialCost; }
}
