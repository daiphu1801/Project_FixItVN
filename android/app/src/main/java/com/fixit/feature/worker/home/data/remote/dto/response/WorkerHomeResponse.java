package com.fixit.feature.worker.home.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;

public class WorkerHomeResponse {

    @SerializedName("workerId")
    private String workerId;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("avatarUrl")
    private String avatarUrl;

    @SerializedName("greetingText")
    private String greetingText;

    @SerializedName("hasUnreadNotification")
    private Boolean hasUnreadNotification;

    @SerializedName("unreadNotificationCount")
    private Integer unreadNotificationCount;

    @SerializedName("available")
    private Boolean available;

    @SerializedName("statusText")
    private String statusText;

    @SerializedName("statusHelpText")
    private String statusHelpText;

    @SerializedName("verificationStatus")
    private String verificationStatus;

    @SerializedName("reputationScore")
    private BigDecimal reputationScore;

    @SerializedName("canReceiveJob")
    private Boolean canReceiveJob;

    @SerializedName("receiveJobBlockedReason")
    private String receiveJobBlockedReason;

    @SerializedName("todayAppointmentCount")
    private Integer todayAppointmentCount;

    @SerializedName("pendingAssignmentCount")
    private Integer pendingAssignmentCount;

    @SerializedName("availableBalance")
    private BigDecimal availableBalance;

    @SerializedName("heldBalance")
    private BigDecimal heldBalance;

    @SerializedName("debtBalance")
    private BigDecimal debtBalance;

    @SerializedName("activeOrder")
    private ActiveOrderSummary activeOrder;

    @SerializedName("statsOverview")
    private WorkerStatsOverview statsOverview;

    @SerializedName("incomeChart")
    private List<IncomeChartPoint> incomeChart;

    public List<IncomeChartPoint> getIncomeChart() {
        return incomeChart;
    }

    @SerializedName("todayAppointments")
    private List<TodayAppointmentItem> todayAppointments;

    public String getWorkerId() {
        return workerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getGreetingText() {
        return greetingText;
    }

    public Boolean getHasUnreadNotification() {
        return hasUnreadNotification;
    }

    public Integer getUnreadNotificationCount() {
        return unreadNotificationCount;
    }

    public Boolean getAvailable() {
        return available;
    }

    public String getStatusText() {
        return statusText;
    }

    public String getStatusHelpText() {
        return statusHelpText;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public BigDecimal getReputationScore() {
        return reputationScore;
    }

    public Boolean getCanReceiveJob() {
        return canReceiveJob;
    }

    public String getReceiveJobBlockedReason() {
        return receiveJobBlockedReason;
    }

    public Integer getTodayAppointmentCount() {
        return todayAppointmentCount;
    }

    public Integer getPendingAssignmentCount() {
        return pendingAssignmentCount;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public BigDecimal getHeldBalance() {
        return heldBalance;
    }

    public BigDecimal getDebtBalance() {
        return debtBalance;
    }

    public ActiveOrderSummary getActiveOrder() {
        return activeOrder;
    }

    public WorkerStatsOverview getStatsOverview() {
        return statsOverview;
    }

    public List<TodayAppointmentItem> getTodayAppointments() {
        return todayAppointments;
    }

    public static class ActiveOrderSummary {
        @SerializedName("bookingId")
        private String bookingId;

        @SerializedName("serviceName")
        private String serviceName;

        @SerializedName("customerName")
        private String customerName;

        @SerializedName("address")
        private String address;

        @SerializedName("status")
        private String status;

        @SerializedName("statusText")
        private String statusText;

        @SerializedName("scheduledTime")
        private String scheduledTime;

        @SerializedName("finalPrice")
        private BigDecimal finalPrice;

        @SerializedName("nextAction")
        private String nextAction;

        public String getBookingId() {
            return bookingId;
        }

        public String getServiceName() {
            return serviceName;
        }

        public String getCustomerName() {
            return customerName;
        }

        public String getAddress() {
            return address;
        }

        public String getStatus() {
            return status;
        }

        public String getStatusText() {
            return statusText;
        }

        public String getScheduledTime() {
            return scheduledTime;
        }

        public BigDecimal getFinalPrice() {
            return finalPrice;
        }

        public String getNextAction() {
            return nextAction;
        }
    }

    public static class WorkerStatsOverview {
        @SerializedName("completedJobsToday")
        private Integer completedJobsToday;

        @SerializedName("completedJobsThisMonth")
        private Integer completedJobsThisMonth;

        @SerializedName("incomeToday")
        private BigDecimal incomeToday;

        @SerializedName("incomeThisWeek")
        private BigDecimal incomeThisWeek;

        @SerializedName("incomeThisMonth")
        private BigDecimal incomeThisMonth;

        @SerializedName("averageRating")
        private BigDecimal averageRating;

        @SerializedName("totalReviews")
        private Integer totalReviews;

        public Integer getCompletedJobsToday() {
            return completedJobsToday;
        }

        public Integer getCompletedJobsThisMonth() {
            return completedJobsThisMonth;
        }

        public BigDecimal getIncomeToday() {
            return incomeToday;
        }

        public BigDecimal getIncomeThisWeek() {
            return incomeThisWeek;
        }

        public BigDecimal getIncomeThisMonth() {
            return incomeThisMonth;
        }

        public BigDecimal getAverageRating() {
            return averageRating;
        }

        public Integer getTotalReviews() {
            return totalReviews;
        }
    }

    public static class TodayAppointmentItem {
        @SerializedName("bookingId")
        private String bookingId;

        @SerializedName("serviceName")
        private String serviceName;

        @SerializedName("customerName")
        private String customerName;

        @SerializedName("address")
        private String address;

        @SerializedName("status")
        private String status;

        @SerializedName("statusText")
        private String statusText;

        @SerializedName("scheduledTime")
        private String scheduledTime;

        @SerializedName("finalPrice")
        private BigDecimal finalPrice;

        public String getBookingId() {
            return bookingId;
        }

        public String getServiceName() {
            return serviceName;
        }

        public String getCustomerName() {
            return customerName;
        }

        public String getAddress() {
            return address;
        }

        public String getStatus() {
            return status;
        }

        public String getStatusText() {
            return statusText;
        }

        public String getScheduledTime() {
            return scheduledTime;
        }

        public BigDecimal getFinalPrice() {
            return finalPrice;
        }
    }

    public static class IncomeChartPoint {
        @SerializedName("label")
        private String label;
        @SerializedName("income")
        private BigDecimal income;
        @SerializedName("completedJobs")
        private Integer completedJobs;

        public String getLabel() {
            return label;
        }

        public BigDecimal getIncome() {
            return income;
        }

        public Integer getCompletedJobs() {
            return completedJobs;
        }
    }
}