package com.fixit.domain.wallet.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepositCreateRequest {

    @NotNull(message = "Số tiền nạp không được để trống")
    @DecimalMin(value = "50000", message = "Số tiền nạp tối thiểu là 50.000 VNĐ")
    @DecimalMax(value = "50000000", message = "Số tiền nạp tối đa là 50.000.000 VNĐ")
    @Digits(integer = 10, fraction = 0, message = "Số tiền nạp phải là số nguyên VNĐ")
    private BigDecimal amount;
}