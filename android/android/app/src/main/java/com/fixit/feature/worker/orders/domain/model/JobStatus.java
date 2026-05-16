package com.fixit.feature.worker.orders.domain.model;

public enum JobStatus {
    ACCEPTED(1, "Bat dau di chuyen"),
    ARRIVING(2, "Da den noi"),
    SURVEYING(3, "Bat dau sua chua"),
    REPAIRING(4, "Xac nhan hoan thanh"),
    COMPLETED(5, "Da hoan thanh");

    private final int step;
    private final String nextActionText;

    JobStatus(int step, String nextActionText) {
        this.step = step;
        this.nextActionText = nextActionText;
    }

    public int getStep() {
        return step;
    }

    public String getNextActionText() {
        return nextActionText;
    }
}
