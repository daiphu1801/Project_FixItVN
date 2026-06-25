package com.fixit.feature.worker.orders.data.remote.mapper;
 
import com.fixit.feature.worker.orders.data.remote.dto.WorkerBookingDetailResponseDto;
import com.fixit.feature.worker.orders.data.remote.dto.WorkerScheduleResponseDto.ScheduleItemDto;
import com.fixit.feature.worker.orders.data.remote.dto.WorkerHistoryResponseDto.HistoryItemDto;
import com.fixit.feature.worker.orders.domain.model.WorkerOrder;
import com.fixit.feature.worker.orders.domain.model.JobStatus;
 
import java.util.ArrayList;
import java.util.List;
 
public class WorkerOrdersMapper {
 
    public static WorkerOrder map(WorkerBookingDetailResponseDto dto) {
        if (dto == null) return null;
 
        String mappedStatus = mapStatus(dto.getStatus(), dto.getDoneActions());
        String formattedPrice = formatPrice(dto.getFinalPrice());
        String formattedTime = formatTime(dto.getScheduledTime());
 
        WorkerOrder order = new WorkerOrder(
                dto.getBookingId(),
                null,
                dto.getServiceName(),
                dto.getAddress(),
                formattedTime,
                formattedPrice,
                mappedStatus,
                dto.getCustomerName()
        );
        order.setJobStatus(mapJobStatus(dto.getStatus(), dto.getDoneActions()));
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setIssueDescription(dto.getIssueDescription());

        if (dto.getProofOfWorks() != null) {
            for (com.fixit.feature.upload.data.remote.dto.response.ProofOfWorkResponse pow : dto.getProofOfWorks()) {
                if ("BEFORE_REPAIR".equals(pow.getProofType())) {
                    order.setProofBeforeUrl(pow.getImageUrl());
                } else if ("AFTER_REPAIR".equals(pow.getProofType())) {
                    order.setProofAfterUrl(pow.getImageUrl());
                }
            }
        }
        return order;
    }

    public static WorkerOrder map(ScheduleItemDto dto) {
        if (dto == null) return null;

        // For schedule items, we don't have doneActions list directly, but we can assume basic status mapping or default.
        String mappedStatus = mapStatus(dto.getStatus(), null);
        String formattedPrice = formatPrice(dto.getFinalPrice());
        String formattedTime = formatTime(dto.getScheduledTime());

        WorkerOrder order = new WorkerOrder(
                dto.getBookingId(),
                null,
                dto.getServiceName(),
                dto.getAddress(),
                formattedTime,
                formattedPrice,
                mappedStatus,
                dto.getCustomerName()
        );
        order.setJobStatus(mapJobStatus(dto.getStatus(), null));
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setIssueDescription(dto.getIssueDescription());
        return order;
    }

    public static WorkerOrder map(HistoryItemDto dto) {
        if (dto == null) return null;

        String mappedStatus = mapStatus(dto.getStatus(), null);
        String formattedPrice = formatPrice(dto.getFinalPrice());
        String formattedTime = formatTime(dto.getScheduledTime());

        WorkerOrder order = new WorkerOrder(
                dto.getBookingId(),
                null,
                dto.getServiceName(),
                dto.getAddress(),
                formattedTime,
                formattedPrice,
                mappedStatus,
                dto.getCustomerName()
        );
        order.setJobStatus(mapJobStatus(dto.getStatus(), null));
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setIssueDescription(dto.getIssueDescription());
        return order;
    }
    
    private static String mapStatus(String backendStatus, List<String> doneActions) {
        if (backendStatus == null) return "pending";

        switch (backendStatus) {
            case "Completed":
                return "completed";
            case "Cancelled":
                return "cancelled";
            case "Accepted":
                // If it is Accepted, we differentiate between pending (no start-moving) and ongoing (started moving)
                if (doneActions != null && (doneActions.contains("Moving") || doneActions.contains("Arrived"))) {
                    return "ongoing";
                }
                return "pending";
            case "Surveying":
            case "In_Progress":
            case "Waiting_Approval":
                return "ongoing";
            default:
                return "pending";
        }
    }

    private static String formatPrice(Double price) {
        if (price == null || price == 0) {
            return "Chưa báo giá";
        }
        return String.format("%,.0f đ", price);
    }

    private static String formatTime(String scheduledTime) {
        if (scheduledTime == null || scheduledTime.trim().isEmpty()) {
            return "";
        }
        // Example: "2026-06-08T14:30:00" -> "Hôm nay 14:30" or "14:30"
        if (scheduledTime.length() >= 16 && scheduledTime.contains("T")) {
            try {
                String datePart = scheduledTime.substring(0, 10);
                String timePart = scheduledTime.substring(11, 16);
                // Return time part with a simple format
                return "Ngày " + datePart.substring(8, 10) + "/" + datePart.substring(5, 7) + " " + timePart;
            } catch (Exception e) {
                return scheduledTime;
            }
        }
        return scheduledTime;
    }

    public static JobStatus mapJobStatus(String backendStatus, List<String> doneActions) {
        if (backendStatus == null) return JobStatus.ACCEPTED;

        switch (backendStatus) {
            case "Accepted":
                if (doneActions != null) {
                    if (doneActions.contains("Arrived")) {
                        return JobStatus.SURVEYING;
                    } else if (doneActions.contains("Moving")) {
                        return JobStatus.ARRIVING;
                    }
                }
                return JobStatus.ACCEPTED;
            case "Surveying":
                return JobStatus.SURVEYING;
            case "In_Progress":
                return JobStatus.REPAIRING;
            case "Waiting_Approval":
                if (doneActions != null) {
                    if (doneActions.contains("Worker_Completed")) {
                        return JobStatus.WAITING_APPROVAL;
                    }
                    if (doneActions.contains("In_Progress")) {
                        return JobStatus.REPAIRING;
                    }
                }
                return JobStatus.SURVEYING;
            case "Completed":
            case "Cancelled":
                return JobStatus.COMPLETED;
            default:
                return JobStatus.ACCEPTED;
        }
    }
}
