package com.fixit.feature.worker.job.domain.model;

public class WorkerJobSummary {
    private final String workerName;
    private final String serviceArea;
    private final int todayOrders;
    private final float rating;
    private final double debtBalance;

    public WorkerJobSummary(String workerName, String serviceArea, int todayOrders,
                            float rating, double debtBalance) {
        this.workerName = workerName;
        this.serviceArea = serviceArea;
        this.todayOrders = todayOrders;
        this.rating = rating;
        this.debtBalance = debtBalance;
    }

    public String getWorkerName() {
        return workerName;
    }

    public String getServiceArea() {
        return serviceArea;
    }

    public int getTodayOrders() {
        return todayOrders;
    }

    public float getRating() {
        return rating;
    }

    public double getDebtBalance() {
        return debtBalance;
    }
}
