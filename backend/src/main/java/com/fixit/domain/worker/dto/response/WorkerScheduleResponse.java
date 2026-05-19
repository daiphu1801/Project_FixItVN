package com.fixit.domain.worker.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkerScheduleResponse {

    private LocalDate date;
    private Integer totalItems;
    private Boolean empty;
    private List<ScheduleItem> items;

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ScheduleItem {
        private UUID bookingId;
        private String serviceName;
        private String customerName;
        private String address;
        private String status;
        private String statusText;
        private OffsetDateTime scheduledTime;
        private BigDecimal finalPrice;
        private String paymentMethod;
        private String issueDescription;
    }
}