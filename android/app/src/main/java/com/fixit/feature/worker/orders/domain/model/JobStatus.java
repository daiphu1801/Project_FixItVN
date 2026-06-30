package com.fixit.feature.worker.orders.domain.model;

public enum JobStatus {
    ACCEPTED(1, "Bắt đầu di chuyển"),
    ARRIVING(2, "Đã đến nơi"),
    SURVEYING(3, "Bắt đầu sửa chữa"),
    REPAIRING(4, "Xác nhận hoàn thành"),
    WAITING_APPROVAL(5, "Chờ khách nghiệm thu"),
    COMPLETED(5, "Đã hoàn thành");

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
