package com.fixit.feature.worker.stats.domain.model;

import java.util.List;

public class WorkerStats {

    private final String workerId;
    private final StatsOverview overview;
    private final List<IncomeChartPoint> incomeChart;
    private final List<ServiceBreakdown> serviceBreakdown;
    private final List<RatingCount> ratingDistribution;
    private final JobCompletionRate completionRate;

    public WorkerStats(
            String workerId,
            StatsOverview overview,
            List<IncomeChartPoint> incomeChart,
            List<ServiceBreakdown> serviceBreakdown,
            List<RatingCount> ratingDistribution,
            JobCompletionRate completionRate
    ) {
        this.workerId = workerId;
        this.overview = overview;
        this.incomeChart = incomeChart;
        this.serviceBreakdown = serviceBreakdown;
        this.ratingDistribution = ratingDistribution;
        this.completionRate = completionRate;
    }

    public String getWorkerId() {
        return workerId;
    }

    public StatsOverview getOverview() {
        return overview;
    }

    public List<IncomeChartPoint> getIncomeChart() {
        return incomeChart;
    }

    public List<ServiceBreakdown> getServiceBreakdown() {
        return serviceBreakdown;
    }

    public List<RatingCount> getRatingDistribution() {
        return ratingDistribution;
    }

    public JobCompletionRate getCompletionRate() {
        return completionRate;
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

    public static class ServiceBreakdown {
        private final String categoryName;
        private final int bookingCount;
        private final long totalRevenue;
        private final double revenuePercentage;

        public ServiceBreakdown(String categoryName, int bookingCount, long totalRevenue, double revenuePercentage) {
            this.categoryName = categoryName;
            this.bookingCount = bookingCount;
            this.totalRevenue = totalRevenue;
            this.revenuePercentage = revenuePercentage;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public int getBookingCount() {
            return bookingCount;
        }

        public long getTotalRevenue() {
            return totalRevenue;
        }

        public double getRevenuePercentage() {
            return revenuePercentage;
        }
    }

    public static class RatingCount {
        private final int rating;
        private final int count;

        public RatingCount(int rating, int count) {
            this.rating = rating;
            this.count = count;
        }

        public int getRating() {
            return rating;
        }

        public int getCount() {
            return count;
        }
    }

    public static class JobCompletionRate {
        private final int totalJobs;
        private final int completedJobs;
        private final int cancelledJobs;
        private final double completionRatePercent;

        public JobCompletionRate(int totalJobs, int completedJobs, int cancelledJobs, double completionRatePercent) {
            this.totalJobs = totalJobs;
            this.completedJobs = completedJobs;
            this.cancelledJobs = cancelledJobs;
            this.completionRatePercent = completionRatePercent;
        }

        public int getTotalJobs() {
            return totalJobs;
        }

        public int getCompletedJobs() {
            return completedJobs;
        }

        public int getCancelledJobs() {
            return cancelledJobs;
        }

        public double getCompletionRatePercent() {
            return completionRatePercent;
        }
    }
}
