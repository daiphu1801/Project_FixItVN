package com.fixit.domain.wallet.mapper;

import com.fixit.domain.wallet.dto.response.*;
import com.fixit.domain.wallet.entity.TransactionHistory;
import com.fixit.domain.wallet.entity.WorkerBankAccount;
import com.fixit.domain.wallet.entity.WorkerWallet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class WorkerWalletMapper {

    public WorkerWalletResponse toWalletResponse(WorkerWallet wallet, BigDecimal incomeThisWeek, BigDecimal incomeThisMonth) {
        BigDecimal availableBalance = valueOrZero(wallet.getAvailableBalance());
        BigDecimal heldBalance = valueOrZero(wallet.getHeldBalance());
        BigDecimal debtBalance = valueOrZero(wallet.getDebtBalance());

        return WorkerWalletResponse.builder()
                .workerId(wallet.getWorkerId())
                .availableBalance(availableBalance)
                .heldBalance(heldBalance)
                .debtBalance(debtBalance)
                .totalBalance(availableBalance.add(heldBalance))
                .canWithdraw(
                        availableBalance.compareTo(BigDecimal.ZERO) > 0
                                && debtBalance.compareTo(BigDecimal.ZERO) == 0
                )
                .incomeThisWeek(incomeThisWeek)
                .incomeThisMonth(incomeThisMonth)
                .build();
    }

    public WorkerWalletResponse emptyWalletResponse(UUID workerId, BigDecimal incomeThisWeek, BigDecimal incomeThisMonth) {
        return WorkerWalletResponse.builder()
                .workerId(workerId)
                .availableBalance(BigDecimal.ZERO)
                .heldBalance(BigDecimal.ZERO)
                .debtBalance(BigDecimal.ZERO)
                .totalBalance(BigDecimal.ZERO)
                .canWithdraw(false)
                .incomeThisWeek(incomeThisWeek)
                .incomeThisMonth(incomeThisMonth)
                .build();
    }

    public WalletTransactionResponse toTransactionResponse(TransactionHistory transaction) {
        WorkerBankAccount targetBankAccount = transaction.getTargetBankAccount();

        return WalletTransactionResponse.builder()
                .transactionId(transaction.getId())
                .bookingId(transaction.getBooking() != null ? transaction.getBooking().getId() : null)
                .transactionType(transaction.getTransactionType() != null ? transaction.getTransactionType().name() : null)
                .amount(valueOrZero(transaction.getAmount()))
                .transactionCode(transaction.getTransactionCode())
                .gatewayReferenceCode(transaction.getGatewayReferenceCode())
                .targetBankAccountId(targetBankAccount != null ? targetBankAccount.getId() : null)
                .targetBankName(targetBankAccount != null ? targetBankAccount.getBankName() : null)
                .targetAccountNumberMasked(
                        targetBankAccount != null
                                ? maskAccountNumber(targetBankAccount.getAccountNumber())
                                : null
                )
                .status(transaction.getStatus() != null ? transaction.getStatus().name() : null)
                .adminNote(transaction.getAdminNote())
                .heldReleaseAt(transaction.getHeldReleaseAt())
                .transactionTime(transaction.getTransactionTime())
                .build();
    }

    public DepositResponse toDepositResponse(TransactionHistory transaction, BigDecimal currentDebt, DepositQrResponse qr) {
        BigDecimal amount = valueOrZero(transaction.getAmount());
        BigDecimal safeDebt = valueOrZero(currentDebt);
        BigDecimal debtPaidAmount = BigDecimal.ZERO;
        BigDecimal surplusToAvailable = BigDecimal.ZERO;

        if (transaction.getStatus() == com.fixit.domain.wallet.entity.TransactionStatus.Success) {
            debtPaidAmount = amount.min(safeDebt);
            surplusToAvailable = amount.subtract(debtPaidAmount).max(BigDecimal.ZERO);
        }

        String nextAction = "VIEW_RESULT";
        if (transaction.getStatus() == com.fixit.domain.wallet.entity.TransactionStatus.Pending) {
            nextAction = "SHOW_QR";
        }

        return DepositResponse.builder()
                .transactionId(transaction.getId())
                .workerId(transaction.getWallet() != null ? transaction.getWallet().getWorkerId() : null)
                .amount(amount)
                .transactionCode(transaction.getTransactionCode())
                .status(transaction.getStatus() != null ? transaction.getStatus().name() : null)
                .transactionType(transaction.getTransactionType() != null ? transaction.getTransactionType().name() : null)
                .debtBefore(safeDebt)
                .debtPaidAmount(debtPaidAmount)
                .surplusToAvailable(surplusToAvailable)
                .transactionTime(transaction.getTransactionTime())
                .nextAction(nextAction)
                .qr(qr)
                .build();
    }

    public WithdrawResponse toWithdrawResponse(TransactionHistory transaction) {
        WorkerBankAccount targetBankAccount = transaction.getTargetBankAccount();

        return WithdrawResponse.builder()
                .transactionId(transaction.getId())
                .workerId(transaction.getWallet() != null ? transaction.getWallet().getWorkerId() : null)
                .amount(valueOrZero(transaction.getAmount()))
                .transactionCode(transaction.getTransactionCode())
                .status(transaction.getStatus() != null ? transaction.getStatus().name() : null)
                .transactionType(transaction.getTransactionType() != null ? transaction.getTransactionType().name() : null)
                .targetBankName(targetBankAccount != null ? targetBankAccount.getBankName() : null)
                .targetAccountNumber(targetBankAccount != null ? targetBankAccount.getAccountNumber() : null)
                .targetAccountName(targetBankAccount != null ? targetBankAccount.getAccountName() : null)
                .transactionTime(transaction.getTransactionTime())
                .adminNote(transaction.getAdminNote())
                .build();
    }

    public DepositQrResponse buildDepositQrResponse(TransactionHistory transaction, String bankName, String bankCode, String accountNumber, String accountName, String qrUrl) {
        return DepositQrResponse.builder()
                .transactionId(transaction.getId())
                .amount(valueOrZero(transaction.getAmount()))
                .transactionCode(transaction.getTransactionCode())
                .bankName(bankName)
                .bankCode(bankCode)
                .accountNumber(accountNumber)
                .accountName(accountName)
                .transferContent(transaction.getTransactionCode())
                .qrUrl(qrUrl)
                .build();
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            return null;
        }

        String trimmed = accountNumber.trim();
        int length = trimmed.length();

        if (length <= 4) {
            return "****";
        }

        return "****" + trimmed.substring(length - 4);
    }
}
