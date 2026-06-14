package com.fixit.domain.booking.service;

import com.fixit.domain.booking.dto.request.CustomerBookingCreateRequest;
import com.fixit.domain.booking.dto.response.CustomerBookingResponse;
import com.fixit.domain.booking.entity.Booking;
import com.fixit.domain.booking.entity.BookingStatus;
import com.fixit.domain.booking.repository.BookingRepository;
import com.fixit.domain.customer.entity.Customer;
import com.fixit.domain.customer.repository.CustomerRepository;
import com.fixit.domain.service_categories.entity.ServiceCategory;
import com.fixit.domain.service_categories.repository.ServiceCategoryRepository;
import com.fixit.domain.auth.entity.User;
import com.fixit.domain.worker.entity.Worker;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerBookingServiceImpl implements CustomerBookingService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;

    @Override
    @Transactional
    public CustomerBookingResponse createBooking(UUID customerId, CustomerBookingCreateRequest request) {
        log.info("Khách hàng {} đang tạo đơn cho dịch vụ ID={}", customerId, request.getServiceId());

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        ServiceCategory serviceCategory = serviceCategoryRepository.findById(request.getServiceId())
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_CATEGORY_NOT_FOUND));

        Booking newBooking = Booking.builder()
                .customer(customer)
                .serviceCategory(serviceCategory)
                .address(request.getAddress())
                .destinationLat(request.getDestinationLat())
                .destinationLng(request.getDestinationLng())
                .issueDescription(request.getIssueDescription())
                .paymentMethod(request.getPaymentMethod())
                .status(BookingStatus.Pending)
                .build();

        newBooking = bookingRepository.save(newBooking);

        log.info("Tạo đơn thành công với ID={}. Đang chờ hệ thống matching ghép thợ...", newBooking.getId());
        
        // Ghi chú: WorkerMatchingScheduler sẽ tự động quét thấy đơn này trong vòng 5 giây 
        // và gán thợ thông qua thuật toán Cost Scoring.

        return mapToResponse(newBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerBookingResponse getBookingDetail(UUID customerId, UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        // Kiểm tra quyền sở hữu đơn hàng
        if (!booking.getCustomer().getCustomerId().equals(customerId)) {
            throw new IllegalArgumentException("Không có quyền truy cập đơn hàng này");
        }

        return mapToResponse(booking);
    }

    private CustomerBookingResponse mapToResponse(Booking booking) {
        CustomerBookingResponse.WorkerInfo workerInfo = null;

        // Nếu đơn đã có thợ nhận (hoặc đang được hệ thống đề xuất cho 1 thợ)
        Worker worker = booking.getWorker();
        if (worker != null) {
            User workerUser = worker.getUser();
            workerInfo = CustomerBookingResponse.WorkerInfo.builder()
                    .workerId(worker.getWorkerId())
                    .fullName(worker.getFullName())
                    .avatarUrl(workerUser != null ? workerUser.getAvatarUrl() : null)
                    .phoneNumber(workerUser != null ? workerUser.getPhoneNumber() : null)
                    .build();
        }

        return CustomerBookingResponse.builder()
                .bookingId(booking.getId())
                .serviceId(booking.getServiceCategory().getId())
                .address(booking.getAddress())
                .destinationLat(booking.getDestinationLat())
                .destinationLng(booking.getDestinationLng())
                .issueDescription(booking.getIssueDescription())
                .paymentMethod(booking.getPaymentMethod())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .worker(workerInfo)
                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public java.util.List<CustomerBookingResponse> getBookings(UUID customerId) {
        return bookingRepository.findByCustomer_CustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelBooking(UUID customerId, UUID bookingId, com.fixit.domain.booking.dto.request.CustomerBookingCancelRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (!booking.getCustomer().getCustomerId().equals(customerId)) {
            throw new IllegalArgumentException("Không có quyền truy cập đơn hàng này");
        }

        if (request.isWorkerFault()) {
            // Hủy thợ hiện tại, đổi trạng thái đơn về Pending để ghép thợ khác
            log.info("Khách hàng yêu cầu đổi thợ cho đơn {}. Lý do: {}", bookingId, request.getReason());
            booking.setWorker(null);
            booking.setStatus(BookingStatus.Pending);
            // Trong thực tế cần đánh dấu thợ cũ bị loại bỏ khỏi đơn này để tránh ghép lại
        } else {
            // Hủy hẳn đơn
            log.info("Khách hàng hủy hẳn đơn {}. Lý do: {}", bookingId, request.getReason());
            booking.setStatus(BookingStatus.Cancelled);
        }

        bookingRepository.save(booking);
    }
}
