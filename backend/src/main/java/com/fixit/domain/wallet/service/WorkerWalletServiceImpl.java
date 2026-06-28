package com.fixit.domain.wallet.service;

import com.fixit.domain.wallet.dto.request.DepositCreateRequest;
import com.fixit.domain.wallet.dto.request.SepayWebhookRequest;
import com.fixit.domain.wallet.dto.request.WithdrawRequest;
import com.fixit.domain.wallet.dto.response.DepositQrResponse;
import com.fixit.domain.wallet.dto.response.DepositResponse;
import com.fixit.domain.wallet.dto.response.WithdrawResponse;
import com.fixit.domain.wallet.dto.response.WorkerWalletResponse;
import com.fixit.domain.wallet.dto.response.WorkerWalletTransactionsResponse;
import com.fixit.domain.wallet.dto.response.WalletTransactionResponse;
import com.fixit.domain.wallet.entity.TransactionHistory;
import com.fixit.domain.wallet.entity.TransactionStatus;
import com.fixit.domain.wallet.entity.TransactionType;
import com.fixit.domain.wallet.entity.WorkerBankAccount;
import com.fixit.domain.wallet.entity.WorkerWallet;
import com.fixit.domain.wallet.repository.TransactionHistoryRepository;
import com.fixit.domain.wallet.repository.WorkerBankAccountRepository;
import com.fixit.domain.wallet.repository.WorkerWalletRepository;
import com.fixit.domain.wallet.mapper.WorkerWalletMapper;
import com.fixit.domain.wallet.util.VietQrHelper;
import com.fixit.domain.wallet.util.TransactionCodeGenerator;
import com.fixit.domain.worker.repository.projection.WorkerPerformanceStatsProjection;
import com.fixit.domain.worker.repository.query.WorkerHomeQueryRepository;
import com.fixit.domain.worker.support.CurrentWorkerResolver;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import com.fixit.domain.booking.entity.Booking;
import com.fixit.domain.booking.entity.BookingStatus;
import com.fixit.domain.booking.entity.QuotationStatus;
import com.fixit.domain.booking.entity.WorkerQuotation;
import com.fixit.domain.booking.repository.BookingRepository;
import com.fixit.domain.booking.repository.WorkerQuotationRepository;
import com.fixit.domain.notification.service.NotificationSenderService;

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
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkerWalletServiceImpl implements WorkerWalletService {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;
    private static final BigDecimal MIN_DEPOSIT_AMOUNT = new BigDecimal("10000");
    private static final BigDecimal MAX_DEPOSIT_AMOUNT = new BigDecimal("50000000");

    private final CurrentWorkerResolver currentWorkerResolver;
    private final WorkerWalletRepository workerWalletRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final WorkerHomeQueryRepository workerHomeQueryRepository;
    private final WorkerBankAccountRepository workerBankAccountRepository;
    private final BookingRepository bookingRepository;
    private final WorkerQuotationRepository workerQuotationRepository;
    private final NotificationSenderService notificationSenderService;

    private final WorkerWalletMapper workerWalletMapper;
    private final VietQrHelper vietQrHelper;
    private final TransactionCodeGenerator transactionCodeGenerator;

    private WorkerWalletService self;

    @org.springframework.beans.factory.annotation.Autowired
    public void setSelf(@org.springframework.context.annotation.Lazy WorkerWalletService self) {
        this.self = self;
    }

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
                .map(wallet -> workerWalletMapper.toWalletResponse(wallet, incomeThisWeek, incomeThisMonth))
                .orElseGet(() -> workerWalletMapper.emptyWalletResponse(workerId, incomeThisWeek, incomeThisMonth));
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
                .map(workerWalletMapper::toTransactionResponse)
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
                .transactionCode(transactionCodeGenerator.generateDepositCode(transactionHistoryRepository))
                .status(TransactionStatus.Pending)
                .adminNote("Nạp tiền trả nợ phí nền tảng. Nợ hiện tại: " + currentDebt.toPlainString())
                .transactionTime(OffsetDateTime.now())
                .build();

        TransactionHistory saved = transactionHistoryRepository.save(transaction);
        DepositQrResponse qr = buildDepositQrResponse(saved);
        return workerWalletMapper.toDepositResponse(saved, currentDebt, qr);
    }

    @Override
    @Transactional(readOnly = true)
    public DepositResponse getMyDepositDetail(UUID transactionId) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();
        TransactionHistory transaction = getMyDepositTransaction(workerId, transactionId);

        BigDecimal currentDebt = workerWalletRepository.findById(workerId)
                .map(wallet -> valueOrZero(wallet.getDebtBalance()))
                .orElse(BigDecimal.ZERO);

        DepositQrResponse qr = null;
        if (transaction.getStatus() == TransactionStatus.Pending) {
            qr = buildDepositQrResponse(transaction);
        }

        return workerWalletMapper.toDepositResponse(transaction, currentDebt, qr);
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

    private DepositQrResponse buildDepositQrResponse(TransactionHistory transaction) {
        String transferContent = transaction.getTransactionCode();
        String qrUrl = vietQrHelper.buildVietQrUrl(depositBankCode, depositAccountNumber, depositAccountName, transaction.getAmount(), transferContent);
        return workerWalletMapper.buildDepositQrResponse(transaction, depositBankName, depositBankCode, depositAccountNumber, depositAccountName, qrUrl);
    }

    @Override
    @Transactional
    public void processDepositWebhook(SepayWebhookRequest request) {
        if (!"in".equalsIgnoreCase(request.getTransactionType())) {
            return;
        }

        String content = request.getContent();
        if (content == null || content.isBlank()) {
            return;
        }

        Optional<TransactionHistory> transactionOpt =
                transactionHistoryRepository.findByTransactionCodeInContent(content.trim());
        if (transactionOpt.isEmpty()) {
            return;
        }
        TransactionHistory transaction = transactionOpt.get();

        if (transaction.getStatus() != TransactionStatus.Pending) {
            return;
        }

        BigDecimal webhookAmount = request.getTransferAmount();
        if (webhookAmount == null || webhookAmount.compareTo(transaction.getAmount()) != 0) {
            transaction.setStatus(TransactionStatus.Failed);
            transaction.setAdminNote("Webhook: số tiền không khớp. Nhận: "
                    + (webhookAmount != null ? webhookAmount.toPlainString() : "null")
                    + ", Yêu cầu: " + transaction.getAmount().toPlainString());
            transactionHistoryRepository.save(transaction);
            return;
        }

        WorkerWallet wallet = workerWalletRepository
                .findByWorkerIdForUpdate(transaction.getWallet().getWorkerId())
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        if (transaction.getTransactionType() == TransactionType.Release) {
            Booking booking = transaction.getBooking();
            if (booking == null) {
                throw new AppException(ErrorCode.BOOKING_NOT_FOUND);
            }

            WorkerQuotation quotation = workerQuotationRepository.findFirstByBooking_IdAndStatusOrderByCreatedAtDesc(booking.getId(), QuotationStatus.Accepted)
                    .orElse(null);

            BigDecimal laborCost = (quotation != null && quotation.getLaborCost() != null) ? quotation.getLaborCost() : BigDecimal.ZERO;
            BigDecimal commission = laborCost.multiply(new BigDecimal("0.10")).setScale(0, RoundingMode.HALF_UP);
            BigDecimal netAmount = transaction.getAmount().subtract(commission).max(BigDecimal.ZERO);

            wallet.setAvailableBalance(valueOrZero(wallet.getAvailableBalance()).add(netAmount));

            transaction.setStatus(TransactionStatus.Success);
            transaction.setGatewayReferenceCode(request.getReferenceCode());
            transaction.setAdminNote("Chuyển khoản SePay thành công qua: " + request.getGateway()
                    + ". Nhận: " + transaction.getAmount().toPlainString()
                    + ", Chiết khấu (10% công): " + commission.toPlainString()
                    + ", Thực nhận: " + netAmount.toPlainString());
            transaction.setTransactionTime(OffsetDateTime.now());

            booking.setStatus(BookingStatus.Completed);
            bookingRepository.save(booking);

            workerWalletRepository.save(wallet);
            transactionHistoryRepository.save(transaction);

            try {
                if (booking.getCustomer() != null && booking.getCustomer().getUser() != null) {
                    UUID customerUserId = booking.getCustomer().getUser().getId();
                    Map<String, String> data = Map.of(
                            "bookingId", booking.getId().toString(),
                            "status", "Completed",
                            "type", "PAYMENT_CONFIRMED"
                    );
                    notificationSenderService.sendNotification(
                            customerUserId,
                            "Thanh toán thành công",
                            "Đơn hàng của bạn đã được thanh toán thành công qua chuyển khoản ngân hàng. Đơn hàng đã hoàn tất!",
                            data
                    );
                }
            } catch (Exception e) {
                System.err.println("Failed to send notification for customer: " + e.getMessage());
            }

            try {
                if (booking.getWorker() != null && booking.getWorker().getUser() != null) {
                    UUID workerUserId = booking.getWorker().getUser().getId();
                    Map<String, String> data = Map.of(
                            "bookingId", booking.getId().toString(),
                            "status", "Completed",
                            "type", "PAYMENT_CONFIRMED"
                    );
                    notificationSenderService.sendNotification(
                            workerUserId,
                            "Nhận tiền thanh toán",
                            "Khách hàng đã chuyển khoản thành công. Ví khả dụng của bạn đã được cộng " + netAmount.toPlainString() + "đ (đã trừ 10% phí chiết khấu).",
                            data
                    );
                }
            } catch (Exception e) {
                System.err.println("Failed to send notification for worker: " + e.getMessage());
            }

        } else {
            BigDecimal depositAmount = valueOrZero(transaction.getAmount());
            BigDecimal currentDebt = valueOrZero(wallet.getDebtBalance());
            BigDecimal debtPaid = depositAmount.min(currentDebt);
            BigDecimal surplus = depositAmount.subtract(debtPaid).max(BigDecimal.ZERO);

            wallet.setDebtBalance(currentDebt.subtract(debtPaid).max(BigDecimal.ZERO));
            wallet.setAvailableBalance(valueOrZero(wallet.getAvailableBalance()).add(surplus));

            transaction.setStatus(TransactionStatus.Success);
            transaction.setGatewayReferenceCode(request.getReferenceCode());
            transaction.setAdminNote("Webhook SePay xác nhận: "
                    + request.getGateway() + " - " + request.getTransactionDate()
                    + ". Nợ đã trả: " + debtPaid.toPlainString()
                    + ". Dư cộng vào khả dụng: " + surplus.toPlainString());
            transaction.setTransactionTime(OffsetDateTime.now());

            workerWalletRepository.save(wallet);
            transactionHistoryRepository.save(transaction);
        }
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

    @Override
    @Transactional
    public WithdrawResponse createMyWithdraw(WithdrawRequest request) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();
        BigDecimal amount = request.getAmount();

        WorkerBankAccount bankAccount = workerBankAccountRepository.findByIdAndWorkerId(
                request.getTargetBankAccountId(),
                workerId
        ).orElseThrow(() -> new AppException(ErrorCode.WORKER_BANK_ACCOUNT_NOT_FOUND));

        WorkerWallet wallet = workerWalletRepository.findByWorkerIdForUpdate(workerId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        BigDecimal availableBalance = valueOrZero(wallet.getAvailableBalance());
        BigDecimal debtBalance = valueOrZero(wallet.getDebtBalance());

        if (debtBalance.compareTo(BigDecimal.ZERO) > 0) {
            throw new AppException(ErrorCode.WALLET_WITHDRAW_HAS_DEBT);
        }

        if (availableBalance.compareTo(amount) < 0) {
            throw new AppException(ErrorCode.WALLET_WITHDRAW_INSUFFICIENT_BALANCE);
        }

        if (transactionHistoryRepository.existsByWallet_WorkerIdAndTransactionTypeAndStatus(
                workerId,
                TransactionType.Withdraw,
                TransactionStatus.Pending
        )) {
            throw new AppException(ErrorCode.WALLET_WITHDRAW_PENDING_EXISTS);
        }

        wallet.setAvailableBalance(availableBalance.subtract(amount));
        workerWalletRepository.save(wallet);

        TransactionHistory transaction = TransactionHistory.builder()
                .wallet(wallet)
                .transactionType(TransactionType.Withdraw)
                .amount(amount)
                .transactionCode(transactionCodeGenerator.generateWithdrawCode(transactionHistoryRepository))
                .status(TransactionStatus.Pending)
                .targetBankAccount(bankAccount)
                .adminNote("Yêu cầu rút tiền đang chờ xử lý.")
                .transactionTime(OffsetDateTime.now())
                .build();

        TransactionHistory saved = transactionHistoryRepository.save(transaction);

        simulateAutomaticApproval(saved.getId());

        return workerWalletMapper.toWithdrawResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public WithdrawResponse getMyWithdrawDetail(UUID transactionId) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();
        if (transactionId == null) {
            throw new AppException(ErrorCode.WALLET_WITHDRAW_NOT_FOUND);
        }

        TransactionHistory transaction = transactionHistoryRepository.findByIdAndWallet_WorkerIdAndTransactionType(
                transactionId,
                workerId,
                TransactionType.Withdraw
        ).orElseThrow(() -> new AppException(ErrorCode.WALLET_WITHDRAW_NOT_FOUND));

        return workerWalletMapper.toWithdrawResponse(transaction);
    }

    @Override
    @Transactional
    public void cancelMyWithdraw(UUID transactionId) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();
        if (transactionId == null) {
            throw new AppException(ErrorCode.WALLET_WITHDRAW_NOT_FOUND);
        }

        TransactionHistory transaction = transactionHistoryRepository.findByIdAndWallet_WorkerIdAndTransactionType(
                transactionId,
                workerId,
                TransactionType.Withdraw
        ).orElseThrow(() -> new AppException(ErrorCode.WALLET_WITHDRAW_NOT_FOUND));

        if (transaction.getStatus() != TransactionStatus.Pending) {
            throw new AppException(ErrorCode.WALLET_DEPOSIT_INVALID_STATUS);
        }

        WorkerWallet wallet = workerWalletRepository.findByWorkerIdForUpdate(workerId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        wallet.setAvailableBalance(valueOrZero(wallet.getAvailableBalance()).add(transaction.getAmount()));
        workerWalletRepository.save(wallet);

        transaction.setStatus(TransactionStatus.Cancelled);
        transaction.setAdminNote("Hủy yêu cầu rút tiền do thợ yêu cầu.");
        transactionHistoryRepository.save(transaction);
    }

    @Override
    @Transactional
    public void approveWithdrawal(UUID transactionId, String referenceCode, String adminNote) {
        TransactionHistory transaction = transactionHistoryRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_WITHDRAW_NOT_FOUND));

        if (transaction.getTransactionType() != TransactionType.Withdraw) {
            throw new AppException(ErrorCode.WALLET_WITHDRAW_NOT_FOUND);
        }

        if (transaction.getStatus() != TransactionStatus.Pending) {
            throw new AppException(ErrorCode.WALLET_DEPOSIT_INVALID_STATUS);
        }

        transaction.setStatus(TransactionStatus.Success);
        transaction.setGatewayReferenceCode(referenceCode);
        transaction.setAdminNote(adminNote != null ? adminNote : "Admin đã duyệt yêu cầu rút tiền.");
        transaction.setTransactionTime(OffsetDateTime.now());
        transactionHistoryRepository.save(transaction);
    }

    @Override
    @Transactional
    public void rejectWithdrawal(UUID transactionId, String adminNote) {
        TransactionHistory transaction = transactionHistoryRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_WITHDRAW_NOT_FOUND));

        if (transaction.getTransactionType() != TransactionType.Withdraw) {
            throw new AppException(ErrorCode.WALLET_WITHDRAW_NOT_FOUND);
        }

        if (transaction.getStatus() != TransactionStatus.Pending) {
            throw new AppException(ErrorCode.WALLET_DEPOSIT_INVALID_STATUS);
        }

        UUID workerId = transaction.getWallet().getWorkerId();
        WorkerWallet wallet = workerWalletRepository.findByWorkerIdForUpdate(workerId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        wallet.setAvailableBalance(valueOrZero(wallet.getAvailableBalance()).add(transaction.getAmount()));
        workerWalletRepository.save(wallet);

        transaction.setStatus(TransactionStatus.Failed);
        transaction.setAdminNote(adminNote != null ? adminNote : "Admin từ chối yêu cầu rút tiền.");
        transaction.setTransactionTime(OffsetDateTime.now());
        transactionHistoryRepository.save(transaction);
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

    private void simulateAutomaticApproval(UUID transactionId) {
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            try {
                self.approveWithdrawal(transactionId, "MOCK_PAYOUT_" + System.currentTimeMillis(), "Tự động duyệt giả lập (Dev Mode)");
            } catch (Exception e) {
                System.err.println("Lỗi tự động duyệt giao dịch: " + e.getMessage());
            }
        });
    }
}