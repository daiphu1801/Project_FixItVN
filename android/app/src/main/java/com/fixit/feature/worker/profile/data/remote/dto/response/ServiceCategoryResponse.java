package com.fixit.feature.worker.profile.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

public class ServiceCategoryResponse {

    @SerializedName("id")
    private Integer id;

    @SerializedName("serviceName")
    private String serviceName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
