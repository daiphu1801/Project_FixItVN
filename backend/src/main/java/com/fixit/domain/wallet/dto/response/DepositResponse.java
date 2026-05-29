package com.fixit.domain.wallet.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositResponse {

    private UUID transactionId;

    private UUID workerId;

    private BigDecimal amount;

    private String transactionCode;

    private String status;

    private String transactionType;

    private BigDecimal debtBefore;

    private BigDecimal debtPaidAmount;

    private BigDecimal surplusToAvailable;

    private OffsetDateTime transactionTime;

    private String nextAction;

    /**
     * QR thanh toán trả về ngay khi giao dịch đang Pending.
     *
     * Nếu status = Pending:
     * - Android có thể hiển thị QR ngay.
     *
     * Nếu status = Success / Failed / Cancelled:
     * - qr = null vì không cần thanh toán tiếp.
     */
    private DepositQrResponse qr;
}