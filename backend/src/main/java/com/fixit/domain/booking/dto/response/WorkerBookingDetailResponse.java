package com.fixit.domain.booking.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerBookingDetailResponse {
    private UUID bookingId;
    private String serviceName;
    private String customerName;
    private String customerPhone;
    private String customerAvatar;
    private String address;
    private BigDecimal destinationLat;
    private BigDecimal destinationLng;
    private String issueDescription;
    private String scheduledTime;
    private String paymentMethod;
    private BigDecimal finalPrice;
    private String status;
    private String statusText;
    private String nextAction;
    private List<String> doneActions;
}
