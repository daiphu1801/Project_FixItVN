package com.fixit.domain.wallet.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawRequest {

    @NotNull(message = "Số tiền rút không được để trống")
    @DecimalMin(value = "50000", message = "Số tiền rút tối thiểu là 50.000 VNĐ")
    @DecimalMax(value = "50000000", message = "Số tiền rút tối đa là 50.000.000 VNĐ")
    @Digits(integer = 10, fraction = 0, message = "Số tiền rút phải là số nguyên VNĐ")
    private BigDecimal amount;

    @NotNull(message = "Tài khoản ngân hàng nhận tiền không được để trống")
    private UUID targetBankAccountId;
}
