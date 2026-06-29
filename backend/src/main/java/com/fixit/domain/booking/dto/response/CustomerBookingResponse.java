package com.fixit.domain.booking.dto.response;

import com.fixit.domain.booking.entity.BookingPaymentMethod;
import com.fixit.domain.booking.entity.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class CustomerBookingResponse {

    private UUID bookingId;
    private Integer serviceId;
    private String address;
    private BigDecimal destinationLat;
    private BigDecimal destinationLng;
    private String issueDescription;
    private BookingPaymentMethod paymentMethod;
    private BookingStatus status;
    private OffsetDateTime createdAt;

    private String serviceName;
    private BigDecimal laborCost;
    private BigDecimal materialCost;
    private String cancellationReason;
    private UUID quotationId; // ID của quotation Pending (khi Surveying) hoặc Accepted
    private String quotationStatus; // "Pending" hoặc "Accepted"
    private String paymentCode;
    private java.util.List<String> doneActions;

    // Thông tin thợ (Sẽ null nếu đơn đang ở trạng thái Pending chưa ghép được thợ)
    private WorkerInfo worker;

    @Data
    @Builder
    public static class WorkerInfo {
        private UUID workerId;
        private String fullName;
        private String avatarUrl;
        private String phoneNumber; // Cần join bảng users để lấy sđt
        private java.math.BigDecimal latitude;
        private java.math.BigDecimal longitude;
    }
}
