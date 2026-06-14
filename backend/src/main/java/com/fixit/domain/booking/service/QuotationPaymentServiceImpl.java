package com.fixit.domain.booking.service;

import com.fixit.domain.booking.dto.request.WorkerQuotationCreateRequest;
import com.fixit.domain.booking.entity.Booking;
import com.fixit.domain.booking.entity.BookingStatus;
import com.fixit.domain.booking.entity.QuotationStatus;
import com.fixit.domain.booking.entity.WorkerQuotation;
import com.fixit.domain.booking.repository.BookingRepository;
import com.fixit.domain.booking.repository.WorkerQuotationRepository;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuotationPaymentServiceImpl implements QuotationPaymentService {

    private final BookingRepository bookingRepository;
    private final WorkerQuotationRepository workerQuotationRepository;

    @Override
    @Transactional
    public WorkerQuotation submitQuotation(UUID workerId, UUID bookingId, WorkerQuotationCreateRequest request) {
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
        
        workerQuotationRepository.save(quotation);

        // Đổi trạng thái Booking chờ khách duyệt
        booking.setStatus(BookingStatus.Waiting_Approval);
        booking.setFinalPrice(total);
        bookingRepository.save(booking);

        return quotation;
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
    }

    @Override
    @Transactional
    public void processPayment(UUID customerId, UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (!booking.getCustomer().getCustomerId().equals(customerId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        booking.setStatus(BookingStatus.Completed);
        bookingRepository.save(booking);
    }
}
