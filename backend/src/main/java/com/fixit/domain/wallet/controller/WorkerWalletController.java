package com.fixit.domain.wallet.controller;

import com.fixit.domain.wallet.dto.request.DepositCreateRequest;
import com.fixit.domain.wallet.dto.response.DepositQrResponse;
import com.fixit.domain.wallet.dto.response.DepositResponse;
import com.fixit.domain.wallet.dto.response.WorkerWalletResponse;
import com.fixit.domain.wallet.dto.response.WorkerWalletTransactionsResponse;
import com.fixit.domain.wallet.service.WorkerWalletService;
import com.fixit.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workers/me/wallet")
@RequiredArgsConstructor
public class WorkerWalletController {

    private final WorkerWalletService workerWalletService;

    @GetMapping
    public ApiResponse<WorkerWalletResponse> getWallet() {
        return ApiResponse.success(workerWalletService.getMyWallet());
    }

    @GetMapping("/transactions")
    public ApiResponse<WorkerWalletTransactionsResponse> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type
    ) {
        WorkerWalletTransactionsResponse response =
                workerWalletService.getMyTransactions(page, size, type);

        return ApiResponse.success(response);
    }

    @PostMapping("/deposits")
    public ApiResponse<DepositResponse> createDeposit(
            @Valid @RequestBody DepositCreateRequest request
    ) {
        DepositResponse response = workerWalletService.createMyDeposit(request);
        return ApiResponse.success(response, "Tạo lệnh nạp tiền trả nợ thành công");
    }

    @GetMapping("/deposits/{transactionId}")
    public ApiResponse<DepositResponse> getDepositDetail(
            @PathVariable UUID transactionId
    ) {
        return ApiResponse.success(workerWalletService.getMyDepositDetail(transactionId));
    }

    @GetMapping("/deposits/{transactionId}/qr")
    public ApiResponse<DepositQrResponse> getDepositQr(
            @PathVariable UUID transactionId
    ) {
        return ApiResponse.success(workerWalletService.getMyDepositQr(transactionId));
    }
}