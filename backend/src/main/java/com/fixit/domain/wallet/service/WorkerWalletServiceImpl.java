package com.fixit.domain.wallet.service;

import com.fixit.domain.wallet.dto.request.DepositCreateRequest;
import com.fixit.domain.wallet.dto.request.SepayWebhookRequest;
import com.fixit.domain.wallet.dto.response.DepositQrResponse;
import com.fixit.domain.wallet.dto.response.DepositResponse;
import com.fixit.domain.wallet.dto.response.WalletTransactionResponse;
import com.fixit.domain.wallet.dto.response.WorkerWalletResponse;
import com.fixit.domain.wallet.dto.response.WorkerWalletTransactionsResponse;
import com.fixit.domain.wallet.entity.TransactionHistory;
import com.fixit.domain.wallet.entity.TransactionStatus;
import com.fixit.domain.wallet.entity.TransactionType;
import com.fixit.domain.wallet.entity.WorkerBankAccount;
import com.fixit.domain.wallet.entity.WorkerWallet;
import com.fixit.domain.wallet.repository.TransactionHistoryRepository;
import com.fixit.domain.wallet.repository.WorkerWalletRepository;
import com.fixit.domain.worker.repository.projection.WorkerPerformanceStatsProjection;
import com.fixit.domain.worker.repository.query.WorkerHomeQueryRepository;
import com.fixit.domain.worker.support.CurrentWorkerResolver;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkerWalletServiceImpl implements WorkerWalletService {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;
    private static final BigDecimal MIN_DEPOSIT_AMOUNT = new BigDecimal("10000");
    private static final BigDecimal MAX_DEPOSIT_AMOUNT = new BigDecimal("50000000");
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final CurrentWorkerResolver currentWorkerResolver;
    private final WorkerWalletRepository workerWalletRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final WorkerHomeQueryRepository workerHomeQueryRepository;

    @Value("${app.payment.deposit.bank-code:MB}")
    private String depositBankCode;

    @Value("${app.payment.deposit.bank-name:MBBank}")
    private String depositBankName;

    @Value("${app.payment.deposit.account-number:0000000000}")
    private String depositAccountNumber;

    @Value("${app.payment.deposit.account-name:CONG TY FIXIT VN}")
    private String depositAccountName;

    @Override
    @Transactional(readOnly = true)
    public WorkerWalletResponse getMyWallet() {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();

        WorkerPerformanceStatsProjection stats = workerHomeQueryRepository.findStatsOverview(workerId);
        BigDecimal incomeThisWeek = stats != null && stats.getIncomeThisWeek() != null ? stats.getIncomeThisWeek() : BigDecimal.ZERO;
        BigDecimal incomeThisMonth = stats != null && stats.getIncomeThisMonth() != null ? stats.getIncomeThisMonth() : BigDecimal.ZERO;

        return workerWalletRepository.findById(workerId)
                .map(wallet -> toWalletResponse(wallet, incomeThisWeek, incomeThisMonth))
                .orElseGet(() -> emptyWalletResponse(workerId, incomeThisWeek, incomeThisMonth));
    }

    @Override
    @Transactional(readOnly = true)
    public WorkerWalletTransactionsResponse getMyTransactions(int page, int size, String type) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();

        Pageable pageable = PageRequest.of(
                normalizePage(page),
                normalizeSize(size),
                Sort.by(Sort.Direction.DESC, "transactionTime")
        );

        TransactionType transactionType = parseType(type);

        Page<TransactionHistory> result = transactionType == null
                ? transactionHistoryRepository.findByWallet_WorkerId(workerId, pageable)
                : transactionHistoryRepository.findByWallet_WorkerIdAndTransactionType(workerId, transactionType, pageable);

        List<WalletTransactionResponse> transactions = result.getContent()
                .stream()
                .map(this::toTransactionResponse)
                .toList();

        return WorkerWalletTransactionsResponse.builder()
                .workerId(workerId)
                .type(transactionType != null ? transactionType.name() : null)
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .hasNext(result.hasNext())
                .transactions(transactions)
                .build();
    }

    @Override
    @Transactional
    public DepositResponse createMyDeposit(DepositCreateRequest request) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();
        BigDecimal amount = normalizeDepositAmount(request != null ? request.getAmount() : null);

        WorkerWallet wallet = workerWalletRepository.findByWorkerIdForUpdate(workerId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        BigDecimal currentDebt = valueOrZero(wallet.getDebtBalance());

        if (transactionHistoryRepository.existsByWallet_WorkerIdAndTransactionTypeAndStatus(
                workerId,
                TransactionType.Deposit,
                TransactionStatus.Pending
        )) {
            throw new AppException(ErrorCode.WALLET_DEPOSIT_PENDING_EXISTS);
        }

        TransactionHistory transaction = TransactionHistory.builder()
                .wallet(wallet)
                .transactionType(TransactionType.Deposit)
                .amount(amount)
                .transactionCode(generateTransactionCode())
                .status(TransactionStatus.Pending)
                .adminNote("Nạp tiền trả nợ phí nền tảng. Nợ hiện tại: " + currentDebt.toPlainString())
                .transactionTime(OffsetDateTime.now())
                .build();

        TransactionHistory saved = transactionHistoryRepository.save(transaction);
        return toDepositResponse(saved, currentDebt);
    }

    @Override
    @Transactional(readOnly = true)
    public DepositResponse getMyDepositDetail(UUID transactionId) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();
        TransactionHistory transaction = getMyDepositTransaction(workerId, transactionId);

        BigDecimal currentDebt = workerWalletRepository.findById(workerId)
                .map(wallet -> valueOrZero(wallet.getDebtBalance()))
                .orElse(BigDecimal.ZERO);

        return toDepositResponse(transaction, currentDebt);
    }

    @Override
    @Transactional(readOnly = true)
    public DepositQrResponse getMyDepositQr(UUID transactionId) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();
        TransactionHistory transaction = getMyDepositTransaction(workerId, transactionId);

        if (transaction.getStatus() != TransactionStatus.Pending) {
            throw new AppException(ErrorCode.WALLET_DEPOSIT_INVALID_STATUS);
        }

        return buildDepositQrResponse(transaction);
    }

    private TransactionHistory getMyDepositTransaction(UUID workerId, UUID transactionId) {
        if (transactionId == null) {
            throw new AppException(ErrorCode.WALLET_DEPOSIT_NOT_FOUND);
        }

        return transactionHistoryRepository.findByIdAndWallet_WorkerIdAndTransactionType(
                        transactionId,
                        workerId,
                        TransactionType.Deposit
                )
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_DEPOSIT_NOT_FOUND));
    }

    private DepositResponse toDepositResponse(TransactionHistory transaction, BigDecimal currentDebt) {
        BigDecimal amount = valueOrZero(transaction.getAmount());
        BigDecimal safeDebt = valueOrZero(currentDebt);
        BigDecimal debtPaidAmount = BigDecimal.ZERO;
        BigDecimal surplusToAvailable = BigDecimal.ZERO;

        if (transaction.getStatus() == TransactionStatus.Success) {
            debtPaidAmount = amount.min(safeDebt);
            surplusToAvailable = amount.subtract(debtPaidAmount).max(BigDecimal.ZERO);
        }

        DepositQrResponse qr = null;
        String nextAction = "VIEW_RESULT";

        if (transaction.getStatus() == TransactionStatus.Pending) {
            qr = buildDepositQrResponse(transaction);
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

    private BigDecimal normalizeDepositAmount(BigDecimal amount) {
        if (amount == null) {
            throw new AppException(ErrorCode.WALLET_DEPOSIT_INVALID_AMOUNT);
        }

        if (amount.scale() > 0) {
            try {
                amount = amount.setScale(0, RoundingMode.UNNECESSARY);
            } catch (ArithmeticException exception) {
                throw new AppException(ErrorCode.WALLET_DEPOSIT_INVALID_AMOUNT);
            }
        }

        if (amount.compareTo(MIN_DEPOSIT_AMOUNT) < 0 || amount.compareTo(MAX_DEPOSIT_AMOUNT) > 0) {
            throw new AppException(ErrorCode.WALLET_DEPOSIT_INVALID_AMOUNT);
        }

        return amount;
    }

    private String generateTransactionCode() {
        String datePart = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        for (int attempt = 0; attempt < 5; attempt++) {
            int randomPart = SECURE_RANDOM.nextInt(1000, 10000);
            String code = "DEP" + datePart + randomPart;

            if (!transactionHistoryRepository.existsByTransactionCode(code)) {
                return code;
            }
        }

        return "DEP" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
    }

    private String buildVietQrUrl(BigDecimal amount, String transferContent) {
        String encodedAccountName = encode(depositAccountName);
        String encodedContent = encode(transferContent);
        String amountValue = valueOrZero(amount).setScale(0, RoundingMode.UNNECESSARY).toPlainString();

        return "https://img.vietqr.io/image/"
                + depositBankCode
                + "-"
                + depositAccountNumber
                + "-compact2.png?amount="
                + amountValue
                + "&addInfo="
                + encodedContent
                + "&accountName="
                + encodedAccountName;
    }

    private DepositQrResponse buildDepositQrResponse(TransactionHistory transaction) {
        String transferContent = transaction.getTransactionCode();
        String qrUrl = buildVietQrUrl(transaction.getAmount(), transferContent);

        return DepositQrResponse.builder()
                .transactionId(transaction.getId())
                .amount(valueOrZero(transaction.getAmount()))
                .transactionCode(transaction.getTransactionCode())
                .bankName(depositBankName)
                .bankCode(depositBankCode)
                .accountNumber(depositAccountNumber)
                .accountName(depositAccountName)
                .transferContent(transferContent)
                .qrUrl(qrUrl)
                .build();
    }

    private String encode(String value) {
        return URLEncoder.encode(value != null ? value : "", StandardCharsets.UTF_8);
    }

    private WorkerWalletResponse toWalletResponse(WorkerWallet wallet, BigDecimal incomeThisWeek, BigDecimal incomeThisMonth) {
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

    private WorkerWalletResponse emptyWalletResponse(UUID workerId, BigDecimal incomeThisWeek, BigDecimal incomeThisMonth) {
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

    private WalletTransactionResponse toTransactionResponse(TransactionHistory transaction) {
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

    private TransactionType parseType(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }

        try {
            return TransactionType.valueOf(type.trim());
        } catch (IllegalArgumentException exception) {
            throw new AppException(ErrorCode.INVALID_REQUEST_PARAMETER);
        }
    }

    private int normalizePage(int page) {
        return Math.max(page, 0);
    }

    private int normalizeSize(int size) {
        if (size <= 0) {
            return DEFAULT_SIZE;
        }

        return Math.min(size, MAX_SIZE);
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


    @Override
    @Transactional
    public void processDepositWebhook(SepayWebhookRequest request) {
        // Chỉ xử lý giao dịch tiền VÀO
        if (!"in".equalsIgnoreCase(request.getTransactionType())) {
            return;
        }
        // Tìm mã giao dịch trong nội dung chuyển khoản
        // SePay gửi nội dung dạng: "DEP20260612143022 1234" hoặc "Chuyen khoan DEP20260612143022"
        String content = request.getContent();
        if (content == null || content.isBlank()) {
            return;
        }
        // Tìm TransactionHistory theo transactionCode trong nội dung
        Optional<TransactionHistory> transactionOpt =
                transactionHistoryRepository.findByTransactionCodeInContent(content.trim());
        if (transactionOpt.isEmpty()) {
            // Không tìm thấy giao dịch khớp — bỏ qua (có thể là giao dịch khác)
            return;
        }
        TransactionHistory transaction = transactionOpt.get();
        // Chỉ xử lý giao dịch đang ở trạng thái Pending
        if (transaction.getStatus() != TransactionStatus.Pending) {
            return;
        }
        // Kiểm tra số tiền khớp (cho phép chênh lệch không đáng kể)
        BigDecimal webhookAmount = request.getTransferAmount();
        if (webhookAmount == null || webhookAmount.compareTo(transaction.getAmount()) != 0) {
            // Số tiền không khớp — đánh dấu lỗi, không xử lý
            transaction.setStatus(TransactionStatus.Failed);
            transaction.setAdminNote("Webhook: số tiền không khớp. Nhận: "
                    + (webhookAmount != null ? webhookAmount.toPlainString() : "null")
                    + ", Yêu cầu: " + transaction.getAmount().toPlainString());
            transactionHistoryRepository.save(transaction);
            return;
        }
        // Lấy ví thợ với pessimistic lock
        WorkerWallet wallet = workerWalletRepository
                .findByWorkerIdForUpdate(transaction.getWallet().getWorkerId())
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));
        BigDecimal depositAmount = valueOrZero(transaction.getAmount());
        BigDecimal currentDebt = valueOrZero(wallet.getDebtBalance());
        // Tính toán: trả nợ trước, phần dư cộng vào khả dụng
        BigDecimal debtPaid = depositAmount.min(currentDebt);
        BigDecimal surplus = depositAmount.subtract(debtPaid).max(BigDecimal.ZERO);
        wallet.setDebtBalance(currentDebt.subtract(debtPaid).max(BigDecimal.ZERO));
        wallet.setAvailableBalance(valueOrZero(wallet.getAvailableBalance()).add(surplus));
        // Cập nhật giao dịch thành công
        transaction.setStatus(TransactionStatus.Success);
        transaction.setGatewayReferenceCode(request.getReferenceCode());
        transaction.setAdminNote("Webhook SePay xác nhận: "
                + request.getGateway() + " - " + request.getTransactionDate()
                + ". Nợ đã trả: " + debtPaid.toPlainString()
                + ". Dư cộng vào khả dụng: " + surplus.toPlainString());
        transaction.setTransactionTime(java.time.OffsetDateTime.now());
        workerWalletRepository.save(wallet);
        transactionHistoryRepository.save(transaction);
    }

    @Override
    @Transactional
    public void cancelMyDeposit(UUID transactionId) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();
        TransactionHistory transaction = getMyDepositTransaction(workerId, transactionId);

        if (transaction.getStatus() != TransactionStatus.Pending) {
            throw new AppException(ErrorCode.WALLET_DEPOSIT_INVALID_STATUS);
        }

        transaction.setStatus(TransactionStatus.Cancelled);
        transaction.setAdminNote("Hủy giao dịch do người dùng yêu cầu.");
        transactionHistoryRepository.save(transaction);
    }
}