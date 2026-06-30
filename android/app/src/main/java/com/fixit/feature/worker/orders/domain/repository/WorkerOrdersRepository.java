package com.fixit.feature.worker.orders.domain.repository;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.orders.domain.model.ExtraCostItem;
import com.fixit.feature.worker.orders.domain.model.JobStatus;
import com.fixit.feature.worker.orders.domain.model.WorkerOrder;

import java.util.List;

public interface WorkerOrdersRepository {
    void getOrders(ResultCallback<List<WorkerOrder>> callback);

    void filterOrders(String status, ResultCallback<List<WorkerOrder>> callback);

    void getOrderById(String orderId, ResultCallback<WorkerOrder> callback);

    JobStatus getInitialStatus(String orderStatus);

    void advanceStatus(String orderId, JobStatus currentStatus, ResultCallback<JobStatus> callback);

    void saveExtraCosts(List<ExtraCostItem> items);

    List<ExtraCostItem> getExtraCosts();

    long calculateTotalExtra();

    String generatePaymentQrUrl(String orderId, long amount);
}
