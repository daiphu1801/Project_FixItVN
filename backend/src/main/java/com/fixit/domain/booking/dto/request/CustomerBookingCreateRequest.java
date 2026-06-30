package com.fixit.domain.booking.dto.request;

import com.fixit.domain.booking.entity.BookingPaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerBookingCreateRequest {

    @NotNull(message = "Service ID không được để trống")
    private Integer serviceId;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @NotNull(message = "Tọa độ vĩ độ không được để trống")
    private BigDecimal destinationLat;

    @NotNull(message = "Tọa độ kinh độ không được để trống")
    private BigDecimal destinationLng;

    private String issueDescription;

    private BookingPaymentMethod paymentMethod = BookingPaymentMethod.CASH;
}
