package com.fixit.feature.worker.home.domain.model;

import java.util.List;

public class WorkerHome {

    private final String workerId;
    private final String fullName;
    private final String avatarUrl;
    private final String greetingText;

    private final boolean available;
    private final String statusText;
    private final String statusHelpText;

    private final String verificationStatus;
    private final boolean canReceiveJob;
    private final String receiveJobBlockedReason;

    private final int pendingAssignmentCount;

    private final ActiveOrder activeOrder;
    private final StatsOverview statsOverview;
    private final List<Appointment> todayAppointments;
    private final List<IncomeChartPoint> incomeChart;

    public WorkerHome(
            String workerId,
            String fullName,
            String avatarUrl,
            String greetingText,
            boolean available,
            String statusText,
            String statusHelpText,
            String verificationStatus,
            boolean canReceiveJob,
            String receiveJobBlockedReason,
            int pendingAssignmentCount,
            ActiveOrder activeOrder,
            StatsOverview statsOverview,
            List<Appointment> todayAppointments,
            List<IncomeChartPoint> incomeChart
    ) {
        this.workerId = workerId;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.greetingText = greetingText;
        this.available = available;
        this.statusText = statusText;
        this.statusHelpText = statusHelpText;
        this.verificationStatus = verificationStatus;
        this.canReceiveJob = canReceiveJob;
        this.receiveJobBlockedReason = receiveJobBlockedReason;
        this.pendingAssignmentCount = pendingAssignmentCount;
        this.activeOrder = activeOrder;
        this.statsOverview = statsOverview;
        this.todayAppointments = todayAppointments;
        this.incomeChart = incomeChart;
    }

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

    public boolean isAvailable() {
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

    public boolean isCanReceiveJob() {
        return canReceiveJob;
    }

    public String getReceiveJobBlockedReason() {
        return receiveJobBlockedReason;
    }

    public int getPendingAssignmentCount() {
        return pendingAssignmentCount;
    }

    public ActiveOrder getActiveOrder() {
        return activeOrder;
    }

    public StatsOverview getStatsOverview() {
        return statsOverview;
    }

    public List<Appointment> getTodayAppointments() {
        return todayAppointments;
    }

    public List<IncomeChartPoint> getIncomeChart() {
        return incomeChart;
    }

    public static class ActiveOrder {
        private final String bookingId;
        private final String serviceName;
        private final String customerName;
        private final String address;
        private final String status;
        private final String statusText;
        private final String scheduledTime;
        private final long finalPrice;
        private final String nextAction;

        public ActiveOrder(
                String bookingId,
                String serviceName,
                String customerName,
                String address,
                String status,
                String statusText,
                String scheduledTime,
                long finalPrice,
                String nextAction
        ) {
            this.bookingId = bookingId;
            this.serviceName = serviceName;
            this.customerName = customerName;
            this.address = address;
            this.status = status;
            this.statusText = statusText;
            this.scheduledTime = scheduledTime;
            this.finalPrice = finalPrice;
            this.nextAction = nextAction;
        }

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

        public long getFinalPrice() {
            return finalPrice;
        }

        public String getNextAction() {
            return nextAction;
        }
    }

    public static class StatsOverview {
        private final int completedJobsToday;
        private final int completedJobsThisMonth;
        private final long incomeToday;
        private final long incomeThisWeek;
        private final long incomeThisMonth;
        private final double averageRating;
        private final int totalReviews;

        public StatsOverview(
                int completedJobsToday,
                int completedJobsThisMonth,
                long incomeToday,
                long incomeThisWeek,
                long incomeThisMonth,
                double averageRating,
                int totalReviews
        ) {
            this.completedJobsToday = completedJobsToday;
            this.completedJobsThisMonth = completedJobsThisMonth;
            this.incomeToday = incomeToday;
            this.incomeThisWeek = incomeThisWeek;
            this.incomeThisMonth = incomeThisMonth;
            this.averageRating = averageRating;
            this.totalReviews = totalReviews;
        }

        public int getCompletedJobsToday() {
            return completedJobsToday;
        }

        public int getCompletedJobsThisMonth() {
            return completedJobsThisMonth;
        }

        public long getIncomeToday() {
            return incomeToday;
        }

        public long getIncomeThisWeek() {
            return incomeThisWeek;
        }

        public long getIncomeThisMonth() {
            return incomeThisMonth;
        }

        public double getAverageRating() {
            return averageRating;
        }

        public int getTotalReviews() {
            return totalReviews;
        }
    }

    public static class IncomeChartPoint {
        private final String label;
        private final long income;
        private final int completedJobs;

        public IncomeChartPoint(String label, long income, int completedJobs) {
            this.label = label;
            this.income = income;
            this.completedJobs = completedJobs;
        }

        public String getLabel() {
            return label;
        }

        public long getIncome() {
            return income;
        }

        public int getCompletedJobs() {
            return completedJobs;
        }
    }
}