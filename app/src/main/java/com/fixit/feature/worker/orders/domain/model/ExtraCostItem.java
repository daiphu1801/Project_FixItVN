package com.fixit.feature.worker.orders.domain.model;

public class ExtraCostItem {
    public String name;
    public int quantity;
    public long unitPrice;

    public ExtraCostItem(String name, int quantity, long unitPrice) {
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public long getTotal() {
        return (long) quantity * unitPrice;
    }
}
