package com.fixit.feature.worker.profile.presentation;

public class SpecializationItem {
    private int id;
    private String name;
    private boolean isSelected;
    private Double basePrice;

    public SpecializationItem(int id, String name, boolean isSelected, Double basePrice) {
        this.id = id;
        this.name = name;
        this.isSelected = isSelected;
        this.basePrice = basePrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
}
