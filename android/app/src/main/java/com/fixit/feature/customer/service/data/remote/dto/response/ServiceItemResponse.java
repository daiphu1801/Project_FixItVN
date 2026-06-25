package com.fixit.feature.customer.service.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

/**
 * DTO for Service Item response from Backend
 */
public class ServiceItemResponse {
    @SerializedName("id")
    private Integer id;

    @SerializedName("itemName")
    private String itemName;

    @SerializedName("suggestedPrice")
    private Long suggestedPrice;

    @SerializedName("serviceCategoryId")
    private Integer serviceCategoryId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getSuggestedPrice() {
        return suggestedPrice;
    }

    public void setSuggestedPrice(Long suggestedPrice) {
        this.suggestedPrice = suggestedPrice;
    }

    public Integer getServiceCategoryId() {
        return serviceCategoryId;
    }

    public void setServiceCategoryId(Integer serviceCategoryId) {
        this.serviceCategoryId = serviceCategoryId;
    }
}
