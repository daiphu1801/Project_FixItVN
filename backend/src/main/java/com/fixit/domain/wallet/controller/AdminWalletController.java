package com.fixit.domain.wallet.controller;

import com.fixit.domain.wallet.dto.request.WithdrawApproveRequest;
import com.fixit.domain.wallet.dto.request.WithdrawRejectRequest;
import com.fixit.domain.wallet.service.WorkerWalletService;
import com.fixit.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/wallet/withdrawals")
@RequiredArgsConstructor
public class AdminWalletController {

    private final WorkerWalletService workerWalletService;

    @PostMapping("/{transactionId}/approve")
    @PreAuthorize("hasRole('Admin')")
    public ApiResponse<Void> approveWithdrawal(
            @PathVariable UUID transactionId,
            @RequestBody(required = false) WithdrawApproveRequest request
    ) {
        String referenceCode = request != null ? request.getReferenceCode() : null;
        String adminNote = request != null ? request.getAdminNote() : null;
        if (referenceCode == null || referenceCode.isBlank()) {
            referenceCode = "MANUAL_PAYOUT_" + System.currentTimeMillis();
        }
        workerWalletService.approveWithdrawal(transactionId, referenceCode, adminNote);
        return ApiResponse.success(null, "Duyệt yêu cầu rút tiền thành công");
    }

    @PostMapping("/{transactionId}/reject")
    @PreAuthorize("hasRole('Admin')")
    public ApiResponse<Void> rejectWithdrawal(
            @PathVariable UUID transactionId,
            @RequestBody(required = false) WithdrawRejectRequest request
    ) {
        String adminNote = request != null ? request.getAdminNote() : null;
        workerWalletService.rejectWithdrawal(transactionId, adminNote);
        return ApiResponse.success(null, "Từ chối yêu cầu rút tiền thành công");
    }
}
