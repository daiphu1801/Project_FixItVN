package com.fixit.domain.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fixit.domain.wallet.dto.request.SepayWebhookRequest;
import com.fixit.domain.wallet.service.WorkerWalletService;
import com.fixit.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Webhook endpoint nhận callback từ SePay khi có giao dịch ngân hàng vào
 *
 * Hỗ trợ 2 hình thức xác thực:
 * 1. Chữ ký HMAC-SHA256 gửi qua header X-SePay-Signature (khuyên dùng)
 * 2. Token tĩnh gửi qua header Authorization (Apikey ...)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
public class DepositWebHookController {

    private final WorkerWalletService workerWalletService;
    private final ObjectMapper objectMapper;

    @Value("${app.payment.sepay.api-token:${fixit.webhook.sepay-api-key:}}")
    private String sepayApiKey;

    @Value("${app.payment.sepay.webhook-secret:}")
    private String sepayWebhookSecret;

    @PostMapping("/sepay")
    public ResponseEntity<ApiResponse<Void>> handleSepayWebhook(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-SePay-Signature", required = false) String signatureHeader,
            @RequestHeader(value = "X-SePay-Timestamp", required = false) String timestampHeader,
            HttpServletRequest servletRequest
    ) {
        String body;
        try {
            body = servletRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            log.error("Không thể đọc request body", e);
            return ResponseEntity.status(400)
                    .body(ApiResponse.error(400, "Bad Request"));
        }

        // 1. Xác thực HMAC-SHA256 nếu có chữ ký
        if (signatureHeader != null && !signatureHeader.isBlank()) {
            if (!isValidSignature(body, signatureHeader, timestampHeader)) {
                log.warn("Webhook SePay bị từ chối: Chữ ký HMAC không hợp lệ. Signature: {}", signatureHeader);
                return ResponseEntity.status(401)
                        .body(ApiResponse.error(401, "Unauthorized"));
            }
        } 
        // 2. Ngược lại, xác thực API key dạng token nếu có cấu hình
        else if (!isValidApiKey(authHeader)) {
            log.warn("Webhook SePay bị từ chối: API key không hợp lệ. Header: {}", authHeader);
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }

        // Parse JSON sang object
        SepayWebhookRequest request;
        try {
            request = objectMapper.readValue(body, SepayWebhookRequest.class);
        } catch (Exception e) {
            log.error("Không thể parse request body thành SepayWebhookRequest: {}", e.getMessage());
            return ResponseEntity.status(400)
                    .body(ApiResponse.error(400, "Invalid JSON payload"));
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

    private boolean isValidSignature(String body, String signatureHeader, String timestampHeader) {
        if (sepayWebhookSecret == null || sepayWebhookSecret.isBlank() || "YOUR_SEPAY_WEBHOOK_SECRET".equals(sepayWebhookSecret)) {
            // Dev local chưa cấu hình -> bỏ qua xác thực chữ ký
            log.warn("app.payment.sepay.webhook-secret chưa được cấu hình hoặc là placeholder — bỏ qua xác thực chữ ký!");
            return true;
        }

        if (timestampHeader == null) {
            log.warn("Thiếu header X-SePay-Timestamp");
            return false;
        }

        try {
            String dataToSign = timestampHeader + "." + body;

            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(sepayWebhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKey);

            byte[] hashBytes = sha256Hmac.doFinal(dataToSign.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            String computedSignature = "sha256=" + hexString.toString();

            boolean isValid = computedSignature.equalsIgnoreCase(signatureHeader);
            if (!isValid) {
                log.warn("Chữ ký HMAC không khớp! Nhận: {}, Tính toán: {}", signatureHeader, computedSignature);
            }
            return isValid;
        } catch (Exception e) {
            log.error("Lỗi khi xác thực chữ ký HMAC", e);
            return false;
        }
    }

    private boolean isValidApiKey(String authHeader) {
        if (sepayApiKey == null || sepayApiKey.isBlank() || "YOUR_SEPAY_API_TOKEN".equals(sepayApiKey)) {
            // Chưa cấu hình key → bỏ qua xác thực (chỉ dùng khi dev local)
            log.warn("API key chưa được cấu hình hoặc là placeholder — bỏ qua xác thực!");
            return true;
        }

        String expected = "Apikey " + sepayApiKey;
        return expected.equals(authHeader);
    }
}
