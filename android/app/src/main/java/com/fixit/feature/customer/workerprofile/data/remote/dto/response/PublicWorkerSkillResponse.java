package com.fixit.feature.customer.workerprofile.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class PublicWorkerSkillResponse {
    @SerializedName("serviceId")
    private Integer serviceId;

    @SerializedName("serviceName")
    private String serviceName;

    @SerializedName("iconUrl")
    private String iconUrl;

    @SerializedName("basePrice")
    private BigDecimal basePrice;

    // Getters and Setters
    public Integer getServiceId() { return serviceId; }
    public void setServiceId(Integer serviceId) { this.serviceId = serviceId; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
}
