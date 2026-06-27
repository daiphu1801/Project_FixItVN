package com.fixit.domain.wallet.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Payload SePay gửi vào POST /api/v1/webhooks/sepay
 * Hỗ trợ cả định dạng camelCase (mặc định của SePay) và snake_case.
 */
@Getter
@Setter
@NoArgsConstructor
public class SepayWebhookRequest {

    // Mã giao dịch nội bộ SePay
    private String id;

    private String gateway;

    @JsonAlias({"transaction_date", "transactionDate"})
    private String transactionDate;

    // "in" = tiền vào, "out" = tiền ra
    @JsonAlias({"transfer_type", "transferType", "transaction_type", "transactionType"})
    private String transferType;

    // Số tài khoản nhận
    @JsonAlias({"account_number", "accountNumber"})
    private String accountNumber;

    // Số tiền giao dịch
    @JsonAlias({"transfer_amount", "transferAmount"})
    private BigDecimal transferAmount;

    // Nội dung chuyển khoản — đây là trường khớp với transactionCode
    private String content;

    // Mã tham chiếu của ngân hàng
    @JsonAlias({"reference_code", "referenceCode"})
    private String referenceCode;

    // Số dư tài khoản sau giao dịch
    private BigDecimal accumulated;

    // Mô tả thêm từ SePay
    private String description;

    private String subAccount;

    // Helper getter/setter để đảm bảo tương thích ngược với code cũ sử dụng transactionType
    public String getTransactionType() {
        return transferType;
    }

    public void setTransactionType(String transactionType) {
        this.transferType = transactionType;
    }
}
