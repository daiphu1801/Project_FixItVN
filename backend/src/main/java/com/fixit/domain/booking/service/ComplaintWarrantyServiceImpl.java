package com.fixit.domain.booking.service;

import com.fixit.domain.booking.dto.request.ComplaintRequest;
import com.fixit.domain.booking.dto.request.WorkerComplaintResponseRequest;
import com.fixit.domain.booking.dto.response.ComplaintResponse;
import com.fixit.domain.booking.entity.Booking;
import com.fixit.domain.booking.entity.BookingStatus;
import com.fixit.domain.booking.entity.ComplaintStatus;
import com.fixit.domain.booking.entity.ComplaintWarranty;
import com.fixit.domain.booking.repository.BookingRepository;
import com.fixit.domain.booking.repository.ComplaintWarrantyRepository;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplaintWarrantyServiceImpl implements ComplaintWarrantyService {

    private final BookingRepository bookingRepository;
    private final ComplaintWarrantyRepository complaintWarrantyRepository;

    @Override
    @Transactional
    public ComplaintResponse createComplaint(UUID customerId, UUID bookingId, ComplaintRequest request) {
        log.info("Khách hàng {} tạo khiếu nại cho đơn hàng {}", customerId, bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        // Kiểm tra xem đơn hàng có thuộc về khách hàng này không
        if (!booking.getCustomer().getCustomerId().equals(customerId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        // Chỉ cho phép khiếu nại đơn hàng đã hoàn thành (Completed)
        if (booking.getStatus() != BookingStatus.Completed) {
            throw new AppException(ErrorCode.BOOKING_NOT_COMPLETED);
        }

        // Kiểm tra xem đã có khiếu nại trước đó chưa
        if (complaintWarrantyRepository.findByBooking_Id(bookingId).isPresent()) {
            throw new AppException(ErrorCode.COMPLAINT_ALREADY_EXISTS);
        }

        ComplaintWarranty complaint = ComplaintWarranty.builder()
                .booking(booking)
                .customerReason(request.getCustomerReason())
                .evidenceImageUrls(toCsv(request.getEvidenceImageUrls()))
                .status(ComplaintStatus.Pending)
                .deadlineToRespond(OffsetDateTime.now().plusDays(3)) // Hạn phản hồi là 3 ngày
                .createdAt(OffsetDateTime.now())
                .build();

        complaint = complaintWarrantyRepository.save(complaint);

        return mapToResponse(complaint);
    }

    @Override
    @Transactional(readOnly = true)
    public ComplaintResponse getComplaint(UUID bookingId) {
        ComplaintWarranty complaint = complaintWarrantyRepository.findByBooking_Id(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPLAINT_NOT_FOUND));

        return mapToResponse(complaint);
    }

    @Override
    @Transactional
    public void cancelComplaint(UUID customerId, UUID bookingId, UUID complaintId) {
        log.info("Khách hàng {} yêu cầu hủy khiếu nại {}", customerId, complaintId);

        ComplaintWarranty complaint = complaintWarrantyRepository.findById(complaintId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPLAINT_NOT_FOUND));

        // Kiểm tra xem đơn hàng có thuộc về khách hàng này không
        if (!complaint.getBooking().getCustomer().getCustomerId().equals(customerId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        // Chỉ cho phép hủy khiếu nại khi chưa có phản hồi (Pending)
        if (complaint.getStatus() != ComplaintStatus.Pending) {
            throw new AppException(ErrorCode.COMPLAINT_INVALID_STATUS);
        }

        complaintWarrantyRepository.delete(complaint);
    }

    @Override
    @Transactional
    public ComplaintResponse respondToComplaint(UUID workerUserId, UUID bookingId, WorkerComplaintResponseRequest request) {
        log.info("Thợ sửa {} gửi phản hồi cho khiếu nại đơn hàng {}", workerUserId, bookingId);

        ComplaintWarranty complaint = complaintWarrantyRepository.findByBooking_Id(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.COMPLAINT_NOT_FOUND));

        // Kiểm tra xem thợ sửa được phân công đơn này có khớp với user đăng nhập hay không
        Booking booking = complaint.getBooking();
        if (booking.getWorker() == null || !booking.getWorker().getWorkerId().equals(workerUserId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        // Chỉ cho phép phản hồi khi khiếu nại đang ở trạng thái Pending
        if (complaint.getStatus() != ComplaintStatus.Pending) {
            throw new AppException(ErrorCode.COMPLAINT_INVALID_STATUS);
        }

        // Cập nhật thông tin phản hồi của thợ
        complaint.setWorkerResponse(request.getWorkerResponse());
        complaint.setWorkerEvidenceImageUrls(toCsv(request.getEvidenceImageUrls()));
        complaint.setStatus(ComplaintStatus.Worker_Responded);

        complaint = complaintWarrantyRepository.save(complaint);

        return mapToResponse(complaint);
    }

    private ComplaintResponse mapToResponse(ComplaintWarranty complaint) {
        return ComplaintResponse.builder()
                .id(complaint.getId())
                .bookingId(complaint.getBooking().getId())
                .customerReason(complaint.getCustomerReason())
                .workerResponse(complaint.getWorkerResponse())
                .evidenceImageUrls(toList(complaint.getEvidenceImageUrls()))
                .workerEvidenceImageUrls(toList(complaint.getWorkerEvidenceImageUrls()))
                .status(complaint.getStatus().name())
                .deadlineToRespond(complaint.getDeadlineToRespond())
                .createdAt(complaint.getCreatedAt())
                .build();
    }

    private List<String> toList(String commaSeparated) {
        if (commaSeparated == null || commaSeparated.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(commaSeparated.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private String toCsv(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return list.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(","));
    }
}
