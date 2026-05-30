package com.fixit.domain.wallet.controller;

import com.fixit.domain.wallet.dto.request.WorkerBankAccountCreateRequest;
import com.fixit.domain.wallet.dto.request.WorkerBankAccountUpdateRequest;
import com.fixit.domain.wallet.dto.response.WorkerBankAccountResponse;
import com.fixit.domain.wallet.dto.response.WorkerBankAccountsResponse;
import com.fixit.domain.wallet.service.WorkerBankAccountService;
import com.fixit.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workers/me/bank-accounts")
@RequiredArgsConstructor
public class WorkerBankAccountController {

    private final WorkerBankAccountService workerBankAccountService;

    @GetMapping
    public ApiResponse<WorkerBankAccountsResponse> getBankAccounts() {
        return ApiResponse.success(workerBankAccountService.getMyBankAccounts());
    }

    @PostMapping
    public ApiResponse<WorkerBankAccountResponse> createBankAccount(
            @Valid @RequestBody WorkerBankAccountCreateRequest request
    ) {
        WorkerBankAccountResponse response = workerBankAccountService.createMyBankAccount(request);
        return ApiResponse.success(response, "Thêm tài khoản ngân hàng thành công");
    }

    @PatchMapping("/{bankAccountId}")
    public ApiResponse<WorkerBankAccountResponse> updateBankAccount(
            @PathVariable UUID bankAccountId,
            @Valid @RequestBody WorkerBankAccountUpdateRequest request
    ) {
        WorkerBankAccountResponse response = workerBankAccountService.updateMyBankAccount(bankAccountId, request);
        return ApiResponse.success(response, "Cập nhật tài khoản ngân hàng thành công");
    }

    @DeleteMapping("/{bankAccountId}")
    public ApiResponse<Void> deleteBankAccount(@PathVariable UUID bankAccountId) {
        workerBankAccountService.deleteMyBankAccount(bankAccountId);
        return ApiResponse.success(null, "Xóa tài khoản ngân hàng thành công");
    }

    @PatchMapping("/{bankAccountId}/default")
    public ApiResponse<WorkerBankAccountResponse> setDefaultBankAccount(
            @PathVariable UUID bankAccountId
    ) {
        WorkerBankAccountResponse response = workerBankAccountService.setMyDefaultBankAccount(bankAccountId);
        return ApiResponse.success(response, "Đặt tài khoản mặc định thành công");
    }
}