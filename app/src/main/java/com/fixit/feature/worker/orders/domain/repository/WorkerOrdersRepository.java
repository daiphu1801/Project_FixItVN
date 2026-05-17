package com.fixit.feature.worker.orders.domain.repository;

import com.fixit.feature.worker.orders.domain.model.ExtraCostItem;
import com.fixit.feature.worker.orders.domain.model.JobStatus;
import com.fixit.feature.worker.orders.domain.model.WorkerOrder;

import java.util.List;

public interface WorkerOrdersRepository {
    List<WorkerOrder> getOrders();

    List<WorkerOrder> filterOrders(String status);

    WorkerOrder getOrderById(String orderId);

    JobStatus getInitialStatus(String orderStatus);

    JobStatus advanceStatus(JobStatus currentStatus);

    void saveExtraCosts(List<ExtraCostItem> items);

    List<ExtraCostItem> getExtraCosts();

    long calculateTotalExtra();

    String generatePaymentQrUrl(String orderId, long amount);
}
