package com.fixit.feature.customer.booking.data.remote.mapper;

import com.fixit.feature.customer.booking.data.remote.dto.response.BookingWorkerInfoDto;
import com.fixit.feature.customer.booking.data.remote.dto.response.CustomerBookingResponseDto;
import com.fixit.feature.customer.booking.domain.model.BookingWorkerInfo;
import com.fixit.feature.customer.booking.domain.model.CustomerBooking;

public class CustomerBookingMapper {

    public static CustomerBooking toDomain(CustomerBookingResponseDto dto) {
        if (dto == null) return null;

        BookingWorkerInfo workerInfo = null;
        if (dto.getWorker() != null) {
            BookingWorkerInfoDto wDto = dto.getWorker();
            workerInfo = new BookingWorkerInfo(
                    wDto.getWorkerId(),
                    wDto.getFullName(),
                    wDto.getAvatarUrl(),
                    wDto.getPhoneNumber()
            );
        }

        return new CustomerBooking(
                dto.getBookingId(),
                dto.getServiceId(),
                dto.getAddress(),
                dto.getDestinationLat(),
                dto.getDestinationLng(),
                dto.getIssueDescription(),
                dto.getPaymentMethod(),
                dto.getStatus(),
                dto.getCreatedAt(),
                dto.getServiceName(),
                dto.getLaborCost(),
                dto.getMaterialCost(),
                dto.getCancellationReason(),
                dto.getFinalPrice(),
                workerInfo
        );
    }
}
