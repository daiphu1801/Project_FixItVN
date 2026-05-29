package com.fixit.feature.worker.profile.presentation;

public class SpecializationItem {
    private Integer id;
    private String name;
    private boolean isSelected;
    private Double basePrice;
    private String customServiceName;

    public SpecializationItem(Integer id, String name, boolean isSelected, Double basePrice) {
        this.id = id;
        this.name = name;
        this.isSelected = isSelected;
        this.basePrice = basePrice;
    }

    public SpecializationItem(Integer id, String name, boolean isSelected, Double basePrice, String customServiceName) {
        this.id = id;
        this.name = name;
        this.isSelected = isSelected;
        this.basePrice = basePrice;
        this.customServiceName = customServiceName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public String getCustomServiceName() {
        return customServiceName;
    }

    public void setCustomServiceName(String customServiceName) {
        this.customServiceName = customServiceName;
    }
}
