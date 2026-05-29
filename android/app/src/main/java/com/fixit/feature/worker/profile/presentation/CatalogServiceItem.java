package com.fixit.feature.worker.profile.presentation;

public class CatalogServiceItem {
    private final Integer serviceId;
    private final Integer categoryId;
    private final String categoryName;
    private final String serviceName;
    private final double suggestedPrice;
    private boolean isSelected;
    private Double customPrice;
    private boolean isCustom;

    public CatalogServiceItem(Integer serviceId, Integer categoryId, String categoryName, String serviceName, double suggestedPrice) {
        this.serviceId = serviceId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.serviceName = serviceName;
        this.suggestedPrice = suggestedPrice;
        this.isSelected = false;
        this.customPrice = null;
        this.isCustom = false;
    }

    public CatalogServiceItem(Integer serviceId, Integer categoryId, String categoryName, String serviceName, double suggestedPrice, boolean isCustom) {
        this.serviceId = serviceId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.serviceName = serviceName;
        this.suggestedPrice = suggestedPrice;
        this.isSelected = false;
        this.customPrice = null;
        this.isCustom = isCustom;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getSuggestedPrice() {
        return suggestedPrice;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Double getCustomPrice() {
        return customPrice;
    }

    public void setCustomPrice(Double customPrice) {
        this.customPrice = customPrice;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }
}
