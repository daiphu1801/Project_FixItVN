package com.fixit.feature.worker.home.data.remote.mapper;

import com.fixit.feature.worker.home.data.remote.dto.WorkerHomeResponse;
import com.fixit.feature.worker.home.domain.model.Appointment;
import com.fixit.feature.worker.home.domain.model.WorkerHome;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class WorkerHomeMapper {

    private WorkerHomeMapper() {
    }

    public static WorkerHome toDomain(WorkerHomeResponse response) {
        if (response == null) {
            return null;
        }

        WorkerHome.ActiveOrder activeOrder = null;
        if (response.getActiveOrder() != null) {
            WorkerHomeResponse.ActiveOrderSummary item = response.getActiveOrder();

            activeOrder = new WorkerHome.ActiveOrder(
                    item.getBookingId(),
                    safeText(item.getServiceName(), "Đơn đang thực hiện"),
                    safeText(item.getCustomerName(), "Khách hàng"),
                    safeText(item.getAddress(), ""),
                    safeText(item.getStatus(), ""),
                    safeText(item.getStatusText(), item.getStatus()),
                    item.getScheduledTime(),
                    moneyToLong(item.getFinalPrice()),
                    safeText(item.getNextAction(), "")
            );
        }

        WorkerHome.StatsOverview statsOverview = new WorkerHome.StatsOverview(
                0,
                0,
                0,
                0,
                0,
                0.0,
                0
        );

        if (response.getStatsOverview() != null) {
            WorkerHomeResponse.WorkerStatsOverview s = response.getStatsOverview();

            statsOverview = new WorkerHome.StatsOverview(
                    safeInt(s.getCompletedJobsToday()),
                    safeInt(s.getCompletedJobsThisMonth()),
                    moneyToLong(s.getIncomeToday()),
                    moneyToLong(s.getIncomeThisWeek()),
                    moneyToLong(s.getIncomeThisMonth()),
                    decimalToDouble(s.getAverageRating()),
                    safeInt(s.getTotalReviews())
            );
        }

        List<Appointment> appointments = new ArrayList<>();
        if (response.getTodayAppointments() != null) {
            for (WorkerHomeResponse.TodayAppointmentItem item : response.getTodayAppointments()) {
                appointments.add(new Appointment(
                        formatTime(item.getScheduledTime()),
                        safeText(item.getServiceName(), "Lịch hẹn"),
                        safeText(item.getAddress(), "")
                ));
            }
        }

        List<WorkerHome.IncomeChartPoint> incomeChart = new ArrayList<>();
        if (response.getIncomeChart() != null) {
            for (WorkerHomeResponse.IncomeChartPoint item : response.getIncomeChart()) {
                incomeChart.add(new WorkerHome.IncomeChartPoint(
                        safeText(item.getLabel(), ""),
                        moneyToLong(item.getIncome()),
                        safeInt(item.getCompletedJobs())
                ));
            }
        }

        return new WorkerHome(
                response.getWorkerId(),
                safeText(response.getFullName(), "Thợ FixIt"),
                response.getAvatarUrl(),
                safeText(response.getGreetingText(), "Xin chào,"),
                Boolean.TRUE.equals(response.getAvailable()),
                safeText(response.getStatusText(), Boolean.TRUE.equals(response.getAvailable()) ? "ONLINE" : "OFFLINE"),
                safeText(response.getStatusHelpText(), "Nhấn để thay đổi trạng thái →"),
                safeText(response.getVerificationStatus(), ""),
                Boolean.TRUE.equals(response.getCanReceiveJob()),
                response.getReceiveJobBlockedReason(),
                safeInt(response.getPendingAssignmentCount()),
                activeOrder,
                statsOverview,
                appointments,
                incomeChart
        );
    }

    private static String safeText(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value;
    }

    private static int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private static long moneyToLong(BigDecimal value) {
        return value == null ? 0L : value.longValue();
    }

    private static double decimalToDouble(BigDecimal value) {
        return value == null ? 0.0 : value.doubleValue();
    }

    private static String formatTime(String scheduledTime) {
        if (scheduledTime == null || scheduledTime.trim().isEmpty()) {
            return "--:--";
        }

        // Backend đang trả dạng "2026-05-22T10:30:00"
        if (scheduledTime.length() >= 16 && scheduledTime.contains("T")) {
            return scheduledTime.substring(11, 16);
        }

        return scheduledTime;
    }
}