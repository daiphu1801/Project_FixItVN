package com.fixit.domain.worker.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkerHistoryResponse {

    private Integer page;
    private Integer size;
    private Long totalItems;
    private Integer totalPages;
    private Boolean empty;
    private String statusFilter;
    private List<HistoryItem> items;

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class HistoryItem {
        private UUID bookingId;
        private String serviceName;
        private String customerName;
        private String address;
        private String status;
        private String statusText;
        private String scheduledTime;
        private String finishedAt;
        private BigDecimal finalPrice;
        private String paymentMethod;
        private String issueDescription;
    }
}