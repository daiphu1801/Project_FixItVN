package com.fixit.domain.wallet.controller;

import com.fixit.domain.wallet.dto.request.SepayWebhookRequest;
import com.fixit.domain.wallet.service.WorkerWalletService;
import com.fixit.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Webhook endpoint nhận callback từ SePay khi có giao dịch ngân hàng vào
 *
 * Để cấu hình:
 * 1. Vào SePay dashboard → Webhook → điền URL: https://your-domain/api/v1/webhooks/sepay
 * 2. Chọn event: Giao dịch mới
 * 3. Copy API key từ SePay → dán vào application.properties:
 *    fixit.webhook.sepay-api-key=YOUR_SEPAY_KEY
 *
 * SePay sẽ gửi header: Authorization: Apikey YOUR_SEPAY_KEY
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
public class DepositWebHookController {

    private final WorkerWalletService workerWalletService;

    @Value("${fixit.webhook.sepay-api-key:}")
    private String sepayApiKey;

    @PostMapping("/sepay")
    public ResponseEntity<ApiResponse<Void>> handleSepayWebhook(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody SepayWebhookRequest request
    ) {
        // Xác thực API key từ SePay
        if (!isValidApiKey(authHeader)) {
            log.warn("Webhook SePay bị từ chối: API key không hợp lệ. Header: {}", authHeader);
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }

        log.info("Webhook SePay nhận được: transactionType={}, content={}, amount={}",
                request.getTransactionType(),
                request.getContent(),
                request.getTransferAmount()
        );

        try {
            workerWalletService.processDepositWebhook(request);
            log.info("Webhook SePay xử lý thành công: content={}", request.getContent());
        } catch (Exception e) {
            // Không ném lỗi ra ngoài — SePay sẽ retry nếu không nhận được 200 OK
            log.error("Webhook SePay xử lý thất bại: {}", e.getMessage(), e);
        }

        // Luôn trả 200 OK để SePay không retry liên tục
        return ResponseEntity.ok(ApiResponse.success(null, "OK"));
    }

    private boolean isValidApiKey(String authHeader) {
        if (sepayApiKey == null || sepayApiKey.isBlank()) {
            // Chưa cấu hình key → bỏ qua xác thực (chỉ dùng khi dev local)
            log.warn("fixit.webhook.sepay-api-key chưa được cấu hình — bỏ qua xác thực!");
            return true;
        }

        String expected = "Apikey " + sepayApiKey;
        return expected.equals(authHeader);
    }
}
