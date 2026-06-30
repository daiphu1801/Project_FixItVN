package com.fixit.feature.worker.orders.domain.model;

public class WorkerAssignment {
    private final String assignmentId;
    private final String bookingId;
    private final String serviceName;
    private final String customerName;
    private final String addressPreview;
    private final String issueDescription;
    private final int remainingSeconds;
    private final double finalPrice;
    private final String paymentMethod;

    public WorkerAssignment(
            String assignmentId,
            String bookingId,
            String serviceName,
            String customerName,
            String addressPreview,
            String issueDescription,
            int remainingSeconds,
            double finalPrice,
            String paymentMethod
    ) {
        this.assignmentId = assignmentId;
        this.bookingId = bookingId;
        this.serviceName = serviceName;
        this.customerName = customerName;
        this.addressPreview = addressPreview;
        this.issueDescription = issueDescription;
        this.remainingSeconds = remainingSeconds;
        this.finalPrice = finalPrice;
        this.paymentMethod = paymentMethod;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getAddressPreview() {
        return addressPreview;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}
