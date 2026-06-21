package com.fixit.domain.wallet.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Payload SePay gửi vào POST /api/v1/webhooks/sepay
 * Tham khảo: https://docs.sepay.vn/webhook.html
 */
@Getter
@Setter
@NoArgsConstructor
public class SepayWebhookRequest {

    // Mã giao dịch nội bộ SePay
    private String id;

    // "in" = tiền vào, "out" = tiền ra
    private String gateway;

    @JsonProperty("transaction_date")
    private String transactionDate;

    // "in" = tiền vào
    @JsonProperty("transaction_type")
    private String transactionType;

    @JsonProperty("transfer_type")
    private String transferType;

    // Số tài khoản nhận
    @JsonProperty("account_number")
    private String accountNumber;

    // Mã tham chiếu giao dịch từ ngân hàng
    @JsonProperty("transfer_amount")
    private BigDecimal transferAmount;

    // Nội dung chuyển khoản — đây là trường khớp với transactionCode
    private String content;

    // Mã tham chiếu của ngân hàng
    @JsonProperty("reference_code")
    private String referenceCode;

    // Số dư tài khoản sau giao dịch
    @JsonProperty("accumulated")
    private BigDecimal accumulated;

    // Mô tả thêm từ SePay
    private String description;

    private String subAccount;
}
