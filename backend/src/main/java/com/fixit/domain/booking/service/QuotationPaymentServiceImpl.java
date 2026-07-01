package com.fixit.domain.booking.service;

import com.fixit.domain.booking.dto.request.WorkerQuotationCreateRequest;
import com.fixit.domain.booking.entity.Booking;
import com.fixit.domain.booking.entity.BookingHistory;
import com.fixit.domain.booking.entity.BookingStatus;
import com.fixit.domain.booking.entity.QuotationStatus;
import com.fixit.domain.booking.entity.WorkerQuotation;
import com.fixit.domain.booking.repository.BookingHistoryRepository;
import com.fixit.domain.booking.repository.BookingRepository;
import com.fixit.domain.booking.repository.WorkerQuotationRepository;
import com.fixit.domain.notification.service.NotificationSenderService;
import com.fixit.domain.worker.support.CurrentWorkerResolver;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import com.fixit.domain.wallet.entity.WorkerWallet;
import com.fixit.domain.wallet.entity.TransactionHistory;
import com.fixit.domain.wallet.entity.TransactionType;
import com.fixit.domain.wallet.entity.TransactionStatus;
import com.fixit.domain.wallet.repository.WorkerWalletRepository;
import com.fixit.domain.wallet.repository.TransactionHistoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuotationPaymentServiceImpl implements QuotationPaymentService {

    private final BookingRepository bookingRepository;
    private final WorkerQuotationRepository workerQuotationRepository;
    private final NotificationSenderService notificationSenderService;
    private final WorkerWalletRepository workerWalletRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final BookingHistoryRepository bookingHistoryRepository;

    @Override
    @Transactional
    public com.fixit.domain.booking.dto.response.WorkerQuotationResponse submitQuotation(UUID workerId, UUID bookingId, WorkerQuotationCreateRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (!booking.getWorker().getWorkerId().equals(workerId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        // Tạo báo giá
        BigDecimal total = request.getLaborCost().add(request.getMaterialCost());
        WorkerQuotation quotation = WorkerQuotation.builder()
                .worker(booking.getWorker())
                .booking(booking)
                .laborCost(request.getLaborCost())
                .materialCost(request.getMaterialCost())
                .totalProposedPrice(total)
                .status(QuotationStatus.Pending)
                .build();

        WorkerQuotation savedQuotation = workerQuotationRepository.save(quotation);

        // Đổi trạng thái Booking chờ khách duyệt
        booking.setStatus(BookingStatus.Waiting_Approval);
        booking.setFinalPrice(total);
        bookingRepository.save(booking);

        // Gửi thông báo cho khách hàng
        try {
            if (booking.getCustomer() != null && booking.getCustomer().getUser() != null) {
                UUID customerUserId = booking.getCustomer().getUser().getId();
                String workerName = booking.getWorker() != null ? booking.getWorker().getFullName() : "Thợ";
                Map<String, String> data = Map.of(
                        "bookingId", booking.getId().toString(),
                        "status", "Waiting_Approval",
                        "type", "NEW_QUOTATION"
                );
                notificationSenderService.sendNotification(
                        customerUserId,
                        "Báo giá mới từ thợ",
                        workerName + " đã gửi báo giá chi tiết cho yêu cầu sửa chữa của bạn. Vui lòng kiểm tra và xác nhận.",
                        data
                );
            }
        } catch (Exception e) {
            log.error("Failed to send notification for quotation: {}", bookingId, e);
        }

        return com.fixit.domain.booking.dto.response.WorkerQuotationResponse.builder()
                .id(savedQuotation.getId())
                .bookingId(booking.getId())
                .workerId(workerId)
                .laborCost(savedQuotation.getLaborCost())
                .materialCost(savedQuotation.getMaterialCost())
                .totalProposedPrice(savedQuotation.getTotalProposedPrice())
                .status(savedQuotation.getStatus().name())
                .createdAt(savedQuotation.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void acceptQuotation(UUID customerId, UUID bookingId, UUID quotationId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (!booking.getCustomer().getCustomerId().equals(customerId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        WorkerQuotation quotation = workerQuotationRepository.findById(quotationId)
                .orElseThrow(() -> new AppException(ErrorCode.QUOTATION_NOT_FOUND));

        quotation.setStatus(QuotationStatus.Accepted);
        workerQuotationRepository.save(quotation);

        booking.setStatus(BookingStatus.In_Progress);
        bookingRepository.save(booking);

        // Lưu lịch sử trạng thái In_Progress vào booking_histories để thợ hoàn thành đơn không bị báo lỗi thiếu bước
        bookingHistoryRepository.save(
                BookingHistory.builder()
                        .booking(booking)
                        .statusUpdate("In_Progress")
                        .updatedAt(OffsetDateTime.now())
                        .build()
        );

        // Gửi thông báo FCM cho thợ để cập nhật giao diện real-time
        try {
            if (booking.getWorker() != null && booking.getWorker().getUser() != null) {
                UUID workerUserId = booking.getWorker().getUser().getId();
                String customerName = booking.getCustomer() != null ? booking.getCustomer().getFullName() : "Khách hàng";
                Map<String, String> data = Map.of(
                        "bookingId", booking.getId().toString(),
                        "status", "In_Progress",
                        "type", "QUOTATION_ACCEPTED"
                );
                notificationSenderService.sendNotification(
                        workerUserId,
                        "Khách đã duyệt báo giá",
                        customerName + " đã đồng ý báo giá của bạn. Hãy tiến hành sửa chữa!",
                        data
                );
            }
        } catch (Exception e) {
            log.error("Failed to send notification for quotation acceptance: {}", bookingId, e);
        }
    }

    @Override
    @Transactional
    public void customerConfirmAcceptance(UUID customerId, UUID bookingId,
                                          com.fixit.domain.booking.entity.BookingPaymentMethod paymentMethod) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (!booking.getCustomer().getCustomerId().equals(customerId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        if (booking.getStatus() != BookingStatus.Waiting_Approval) {
            throw new AppException(ErrorCode.BOOKING_INVALID_STATUS_TRANSITION);
        }

        // Cập nhật phương thức thanh toán khách vừa chọn (CASH hoặc BANK_TRANSFER)
        if (paymentMethod != null) {
            booking.setPaymentMethod(paymentMethod);
        }

        // Chuyển sang Waiting_Payment - chờ thợ xác nhận đã nhận tiền
        booking.setStatus(BookingStatus.Waiting_Payment);
        bookingRepository.save(booking);

        // Lấy báo giá được duyệt để tính commission (fallback về finalPrice nếu không tìm thấy báo giá Accepted)
        BigDecimal laborCost;
        java.util.Optional<WorkerQuotation> quotationOpt = workerQuotationRepository
                .findFirstByBooking_IdAndStatusOrderByCreatedAtDesc(booking.getId(), QuotationStatus.Accepted);
        if (quotationOpt.isPresent()) {
            laborCost = quotationOpt.get().getLaborCost() != null ? quotationOpt.get().getLaborCost() : BigDecimal.ZERO;
        } else {
            // Fallback: dùng finalPrice của đơn hàng nếu không có báo giá chính thức
            log.warn("Đơn hàng {} không có báo giá Accepted. Dùng finalPrice làm fallback.", bookingId);
            laborCost = booking.getFinalPrice() != null ? booking.getFinalPrice() : BigDecimal.ZERO;
        }
        BigDecimal commission = laborCost.multiply(new BigDecimal("0.10")).setScale(0, RoundingMode.HALF_UP);

        // Lấy/tạo ví thợ
        WorkerWallet wallet = workerWalletRepository.findByWorkerIdForUpdate(booking.getWorker().getWorkerId())
                .orElseGet(() -> {
                    WorkerWallet newWallet = WorkerWallet.builder()
                            .workerId(booking.getWorker().getWorkerId())
                            .availableBalance(BigDecimal.ZERO)
                            .heldBalance(BigDecimal.ZERO)
                            .debtBalance(BigDecimal.ZERO)
                            .build();
                    return workerWalletRepository.save(newWallet);
                });

        if (booking.getPaymentMethod() == com.fixit.domain.booking.entity.BookingPaymentMethod.BANK_TRANSFER) {
            // Flow B: Chuyển khoản ngân hàng (VietQR của công ty)
            // Tạo 1 giao dịch Release ở trạng thái Pending, với mã paymentCode duy nhất
            String paymentCode = "FIX" + OffsetDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                    + new java.util.Random().nextInt(1000, 10000);

            TransactionHistory releaseTx = TransactionHistory.builder()
                    .wallet(wallet)
                    .booking(booking)
                    .transactionType(TransactionType.Release)
                    .amount(booking.getFinalPrice())
                    .transactionCode(paymentCode)
                    .status(TransactionStatus.Pending)
                    .adminNote("Chờ khách hàng chuyển khoản thanh toán đơn hàng " + booking.getId() + " qua số tài khoản công ty")
                    .transactionTime(OffsetDateTime.now())
                    .build();
            transactionHistoryRepository.save(releaseTx);

            log.info("Đã tạo giao dịch Release Pending cho đơn hàng {}, paymentCode={}", booking.getId(), paymentCode);
        } else {
            // Flow CASH: Trừ trực tiếp 10% commission của laborCost
            BigDecimal available = wallet.getAvailableBalance() != null ? wallet.getAvailableBalance() : BigDecimal.ZERO;
            BigDecimal debt = wallet.getDebtBalance() != null ? wallet.getDebtBalance() : BigDecimal.ZERO;

            if (available.compareTo(commission) >= 0) {
                wallet.setAvailableBalance(available.subtract(commission));
            } else {
                BigDecimal remainingCommission = commission.subtract(available);
                wallet.setAvailableBalance(BigDecimal.ZERO);
                wallet.setDebtBalance(debt.add(remainingCommission));
            }
            workerWalletRepository.save(wallet);

            String feeCode = "FEE" + OffsetDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                    + new java.util.Random().nextInt(1000, 10000);

            TransactionHistory feeDeductionTx = TransactionHistory.builder()
                    .wallet(wallet)
                    .booking(booking)
                    .transactionType(TransactionType.Fee_Deduction)
                    .amount(commission)
                    .transactionCode(feeCode)
                    .status(TransactionStatus.Success)
                    .adminNote("Khấu trừ chiết khấu 10% cho đơn hàng " + booking.getId() + " thanh toán bằng Tiền mặt")
                    .transactionTime(OffsetDateTime.now())
                    .build();
            transactionHistoryRepository.save(feeDeductionTx);

            log.info("Đã khấu trừ 10% commission ({}) cho đơn hàng CASH {}, thợ ID={}", commission, booking.getId(), booking.getWorker().getWorkerId());
        }

        // Gửi thông báo cho thợ
        try {
            if (booking.getWorker() != null && booking.getWorker().getUser() != null) {
                UUID workerUserId = booking.getWorker().getUser().getId();
                String customerName = booking.getCustomer() != null ? booking.getCustomer().getFullName() : "Khách hàng";
                Map<String, String> data = Map.of(
                        "bookingId", booking.getId().toString(),
                        "status", "Waiting_Payment",
                        "type", "CUSTOMER_ACCEPTED"
                );
                notificationSenderService.sendNotification(
                        workerUserId,
                        "Khách đã nghiệm thu",
                        customerName + " đã xác nhận nghiệm thu. Vui lòng xác nhận đã nhận đủ tiền hoặc chờ chuyển khoản.",
                        data
                );
            }
        } catch (Exception e) {
            log.error("Failed to send notification for booking: {}", bookingId, e);
        }
    }

    @Override
    @Transactional
    public void workerConfirmPayment(UUID workerId, UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (!booking.getWorker().getWorkerId().equals(workerId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        if (booking.getStatus() != BookingStatus.Waiting_Payment) {
            throw new AppException(ErrorCode.BOOKING_INVALID_STATUS_TRANSITION);
        }

        // Hoàn tất đơn
        booking.setStatus(BookingStatus.Completed);
        booking.setCompletedAt(OffsetDateTime.now());
        bookingRepository.save(booking);

        // Gửi thông báo cho khách
        try {
            if (booking.getCustomer() != null && booking.getCustomer().getUser() != null) {
                UUID customerUserId = booking.getCustomer().getUser().getId();
                String workerName = booking.getWorker() != null ? booking.getWorker().getFullName() : "Thợ";
                Map<String, String> data = Map.of(
                        "bookingId", booking.getId().toString(),
                        "status", "Completed",
                        "type", "PAYMENT_CONFIRMED"
                );
                notificationSenderService.sendNotification(
                        customerUserId,
                        "Đơn hàng hoàn tất",
                        workerName + " đã xác nhận nhận đủ tiền. Đơn hàng của bạn đã hoàn thành.",
                        data
                );
            }
        } catch (Exception e) {
            log.error("Failed to send notification for booking: {}", bookingId, e);
        }
    }
}
