package com.fixit.feature.customer.service.domain.model;

/**
 * Domain model for a specific service item (e.g., "Repair sink" under "Plumbing")
 */
public class ServiceItem {
    private Integer id;
    private String name;
    private Long price;
    private Integer categoryId;

    public ServiceItem(Integer id, String name, Long price, Integer categoryId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.categoryId = categoryId;
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

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
}
