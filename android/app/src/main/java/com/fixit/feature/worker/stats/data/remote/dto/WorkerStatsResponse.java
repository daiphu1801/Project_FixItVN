package com.fixit.feature.worker.stats.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;

public class WorkerStatsResponse {

    @SerializedName("workerId")
    private String workerId;

    @SerializedName("overview")
    private StatsOverview overview;

    @SerializedName("incomeChart")
    private List<IncomeChartPoint> incomeChart;

    @SerializedName("serviceBreakdown")
    private List<ServiceBreakdown> serviceBreakdown;

    @SerializedName("ratingDistribution")
    private List<RatingCount> ratingDistribution;

    @SerializedName("completionRate")
    private JobCompletionRate completionRate;

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

    public static class ServiceBreakdown {
        @SerializedName("categoryName")
        private String categoryName;

        @SerializedName("bookingCount")
        private Integer bookingCount;

        @SerializedName("totalRevenue")
        private BigDecimal totalRevenue;

        @SerializedName("revenuePercentage")
        private Double revenuePercentage;

        public String getCategoryName() {
            return categoryName;
        }

        public Integer getBookingCount() {
            return bookingCount;
        }

        public BigDecimal getTotalRevenue() {
            return totalRevenue;
        }

        public Double getRevenuePercentage() {
            return revenuePercentage;
        }
    }

    public static class RatingCount {
        @SerializedName("rating")
        private Integer rating;

        @SerializedName("count")
        private Integer count;

        public Integer getRating() {
            return rating;
        }

        public Integer getCount() {
            return count;
        }
    }

    public static class JobCompletionRate {
        @SerializedName("totalJobs")
        private Integer totalJobs;

        @SerializedName("completedJobs")
        private Integer completedJobs;

        @SerializedName("cancelledJobs")
        private Integer cancelledJobs;

        @SerializedName("completionRatePercent")
        private Double completionRatePercent;

        public Integer getTotalJobs() {
            return totalJobs;
        }

        public Integer getCompletedJobs() {
            return completedJobs;
        }

        public Integer getCancelledJobs() {
            return cancelledJobs;
        }

        public Double getCompletionRatePercent() {
            return completionRatePercent;
        }
    }
}
