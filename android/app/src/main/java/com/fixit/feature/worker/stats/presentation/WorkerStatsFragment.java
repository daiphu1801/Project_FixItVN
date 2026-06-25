package com.fixit.feature.worker.stats.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentWorkerStatsBinding;
import com.fixit.feature.worker.home.domain.model.Appointment;
import com.fixit.feature.worker.stats.domain.model.WorkerStats;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerStatsFragment extends BaseFragment<FragmentWorkerStatsBinding> {

    private WorkerStatsViewModel viewModel;
    private AppointmentHorizontalAdapter adapter;
    private final List<Appointment> appointmentList = new ArrayList<>();

    @Override
    protected FragmentWorkerStatsBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerStatsBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Setup Toolbar & Back Navigation
        if (binding.appBarLayout != null && binding.appBarLayout.toolbar != null) {
            binding.appBarLayout.toolbar.setTitle("Thống kê & Lịch làm");
            binding.appBarLayout.toolbar.setNavigationOnClickListener(v -> {
                if (navController != null) {
                    navController.navigateUp();
                }
            });
        }

        setupAppointmentList();
        setupFilters();
    }

    private void setupAppointmentList() {
        adapter = new AppointmentHorizontalAdapter(appointmentList);
        binding.rvUpcomingAppointments.setLayoutManager(new LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvUpcomingAppointments.setAdapter(adapter);
    }

    private void setupFilters() {
        binding.btnFilterToday.setOnClickListener(v -> {
            if (viewModel != null) viewModel.setFilter("today");
        });
        binding.btnFilterWeek.setOnClickListener(v -> {
            if (viewModel != null) viewModel.setFilter("week");
        });
        binding.btnFilterMonth.setOnClickListener(v -> {
            if (viewModel != null) viewModel.setFilter("month");
        });
    }

    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(this).get(WorkerStatsViewModel.class);

        // Listen to selected filter changes to style buttons
        viewModel.selectedFilter.observe(getViewLifecycleOwner(), this::updateFilterUI);

        // Listen to dynamic summary metrics
        viewModel.filteredTotalIncome.observe(getViewLifecycleOwner(), income ->
                binding.tvTotalIncome.setText(formatMoney(income)));

        viewModel.filteredTotalOrders.observe(getViewLifecycleOwner(), orders ->
                binding.tvTotalOrders.setText(orders + " đơn"));

        viewModel.filteredAvgPerOrder.observe(getViewLifecycleOwner(), avg ->
                binding.tvAvgPerOrder.setText(formatMoney(avg)));



        // Listen to real stats for chart drawing and advanced analytics
        viewModel.workerStats.observe(getViewLifecycleOwner(), stats -> {
            updateChartUI(stats);
            bindPerformanceStats(stats);
        });

        // Listen to real appointments of today
        viewModel.todayAppointments.observe(getViewLifecycleOwner(), appointments -> {
            appointmentList.clear();
            if (appointments != null) {
                appointmentList.addAll(appointments);
            }
            adapter.notifyDataSetChanged();
            
            // Adjust visibility if list is empty
            if (appointmentList.isEmpty()) {
                binding.rvUpcomingAppointments.setVisibility(View.GONE);
            } else {
                binding.rvUpcomingAppointments.setVisibility(View.VISIBLE);
            }
        });

        // Listen to base error updates
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.trim().isEmpty()) {
                showToast(msg);
            }
        });
    }

    private void updateFilterUI(String filter) {
        // Reset all buttons to gray
        binding.btnFilterToday.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#e2e8f0")));
        binding.btnFilterToday.setTextColor(android.graphics.Color.parseColor("#4a5568"));

        binding.btnFilterWeek.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#e2e8f0")));
        binding.btnFilterWeek.setTextColor(android.graphics.Color.parseColor("#4a5568"));

        binding.btnFilterMonth.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#e2e8f0")));
        binding.btnFilterMonth.setTextColor(android.graphics.Color.parseColor("#4a5568"));

        // Highlight active filter button in blue
        if ("today".equalsIgnoreCase(filter)) {
            binding.btnFilterToday.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#42c2ff")));
            binding.btnFilterToday.setTextColor(android.graphics.Color.WHITE);
        } else if ("week".equalsIgnoreCase(filter)) {
            binding.btnFilterWeek.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#42c2ff")));
            binding.btnFilterWeek.setTextColor(android.graphics.Color.WHITE);
        } else if ("month".equalsIgnoreCase(filter)) {
            binding.btnFilterMonth.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#42c2ff")));
            binding.btnFilterMonth.setTextColor(android.graphics.Color.WHITE);
        }
    }

    private void updateChartUI(WorkerStats stats) {
        if (stats == null || stats.getIncomeChart() == null || stats.getIncomeChart().isEmpty()) {
            return;
        }

        View[] bars = new View[] {
                binding.barMon, binding.barTue, binding.barWed, binding.barThu, binding.barFri, binding.barSat, binding.barSun
        };
        android.widget.TextView[] labels = new android.widget.TextView[] {
                binding.lblMon, binding.lblTue, binding.lblWed, binding.lblThu, binding.lblFri, binding.lblSat, binding.lblSun
        };

        List<WorkerStats.IncomeChartPoint> chartPoints = stats.getIncomeChart();
        int count = Math.min(chartPoints.size(), bars.length);

        // Find the peak income day for chart rendering scale
        long maxIncome = 0;
        for (int i = 0; i < count; i++) {
            long inc = chartPoints.get(i).getIncome();
            if (inc > maxIncome) {
                maxIncome = inc;
            }
        }

        int maxBarHeightPx = (int) (120 * getResources().getDisplayMetrics().density);
        int minBarHeightPx = (int) (4 * getResources().getDisplayMetrics().density);

        for (int i = 0; i < bars.length; i++) {
            if (i < count) {
                WorkerStats.IncomeChartPoint point = chartPoints.get(i);

                // Update standard day labels to Vietnamese (e.g. Mon -> T2)
                labels[i].setText(mapChartLabelToVietnamese(point.getLabel()));

                // Set column layout height proportional to income
                int height = minBarHeightPx;
                if (maxIncome > 0) {
                    height = (int) ((point.getIncome() * maxBarHeightPx) / maxIncome);
                    if (height < minBarHeightPx) {
                        height = minBarHeightPx;
                    }
                }

                ViewGroup.LayoutParams params = bars[i].getLayoutParams();
                params.height = height;
                bars[i].setLayoutParams(params);

                // Highlight the highest performing day of the week
                if (maxIncome > 0 && point.getIncome() == maxIncome) {
                    bars[i].setBackgroundColor(android.graphics.Color.parseColor("#42c2ff"));
                    labels[i].setTextColor(android.graphics.Color.parseColor("#0d1b2a"));
                    labels[i].setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
                } else {
                    bars[i].setBackgroundColor(android.graphics.Color.parseColor("#d6f2ff"));
                    labels[i].setTextColor(android.graphics.Color.parseColor("#4a5568"));
                    labels[i].setTypeface(android.graphics.Typeface.DEFAULT);
                }

                // Add interactive Toast feedback on column click
                final int index = i;
                bars[i].setOnClickListener(v -> {
                    String msg = String.format("Doanh thu %s: %s (%d đơn)",
                            mapChartLabelToVietnamese(point.getLabel()),
                            formatMoney(point.getIncome()),
                            point.getCompletedJobs()
                    );
                    showToast(msg);
                });
            } else {
                // Reset layout for unused columns
                ViewGroup.LayoutParams params = bars[i].getLayoutParams();
                params.height = minBarHeightPx;
                bars[i].setLayoutParams(params);
                bars[i].setBackgroundColor(android.graphics.Color.parseColor("#e2e8f0"));
                labels[i].setText("");
                bars[i].setOnClickListener(null);
            }
        }
    }

    private String mapChartLabelToVietnamese(String label) {
        if (label == null) return "";
        String lower = label.toLowerCase();
        if (lower.contains("mon")) return "T2";
        if (lower.contains("tue")) return "T3";
        if (lower.contains("wed")) return "T4";
        if (lower.contains("thu")) return "T5";
        if (lower.contains("fri")) return "T6";
        if (lower.contains("sat")) return "T7";
        if (lower.contains("sun")) return "CN";
        return label;
    }

    private String formatMoney(long amount) {
        return NumberFormat.getNumberInstance(new Locale("vi", "VN")).format(amount) + "đ";
    }

    private void bindPerformanceStats(WorkerStats stats) {
        if (stats == null) return;

        // 1. Completion Rate
        WorkerStats.JobCompletionRate rate = stats.getCompletionRate();
        if (rate != null) {
            int pct = (int) Math.round(rate.getCompletionRatePercent());
            binding.pbCompletionRate.setProgress(pct);
            binding.tvCompletionRateText.setText(String.format(Locale.getDefault(),
                    "%.1f%% Hoàn thành (%d/%d đơn)",
                    rate.getCompletionRatePercent(),
                    rate.getCompletedJobs(),
                    rate.getTotalJobs()
            ));
        }

        // 2. Rating Distribution
        binding.pbStar5.setProgress(0); binding.tvStar5Count.setText("0");
        binding.pbStar4.setProgress(0); binding.tvStar4Count.setText("0");
        binding.pbStar3.setProgress(0); binding.tvStar3Count.setText("0");
        binding.pbStar2.setProgress(0); binding.tvStar2Count.setText("0");
        binding.pbStar1.setProgress(0); binding.tvStar1Count.setText("0");

        List<WorkerStats.RatingCount> ratings = stats.getRatingDistribution();
        if (ratings != null && !ratings.isEmpty()) {
            int maxCount = 0;
            for (WorkerStats.RatingCount r : ratings) {
                if (r.getCount() > maxCount) {
                    maxCount = r.getCount();
                }
            }

            for (WorkerStats.RatingCount r : ratings) {
                int count = r.getCount();
                int pct = maxCount > 0 ? (count * 100) / maxCount : 0;
                switch (r.getRating()) {
                    case 5:
                        binding.pbStar5.setProgress(pct);
                        binding.tvStar5Count.setText(String.valueOf(count));
                        break;
                    case 4:
                        binding.pbStar4.setProgress(pct);
                        binding.tvStar4Count.setText(String.valueOf(count));
                        break;
                    case 3:
                        binding.pbStar3.setProgress(pct);
                        binding.tvStar3Count.setText(String.valueOf(count));
                        break;
                    case 2:
                        binding.pbStar2.setProgress(pct);
                        binding.tvStar2Count.setText(String.valueOf(count));
                        break;
                    case 1:
                        binding.pbStar1.setProgress(pct);
                        binding.tvStar1Count.setText(String.valueOf(count));
                        break;
                }
            }
        }

        // 3. Service Breakdown
        binding.layoutServiceBreakdown.removeAllViews();
        List<WorkerStats.ServiceBreakdown> serviceBreakdown = stats.getServiceBreakdown();
        if (serviceBreakdown != null && !serviceBreakdown.isEmpty()) {
            LayoutInflater inflater = LayoutInflater.from(requireContext());
            for (WorkerStats.ServiceBreakdown item : serviceBreakdown) {
                View row = inflater.inflate(R.layout.item_worker_service_stat, binding.layoutServiceBreakdown, false);
                
                android.widget.TextView tvName = row.findViewById(R.id.tvServiceStatName);
                ProgressBar pbShare = row.findViewById(R.id.pbServiceStatShare);
                android.widget.TextView tvRevenue = row.findViewById(R.id.tvServiceStatRevenue);

                tvName.setText(item.getCategoryName() + " (" + item.getBookingCount() + " đơn)");
                pbShare.setProgress((int) Math.round(item.getRevenuePercentage()));
                tvRevenue.setText(formatMoney(item.getTotalRevenue()) + " (" + Math.round(item.getRevenuePercentage()) + "%)");

                binding.layoutServiceBreakdown.addView(row);
            }
        } else {
            android.widget.TextView noData = new android.widget.TextView(requireContext());
            noData.setText("Không có dữ liệu cơ cấu dịch vụ");
            noData.setTextColor(android.graphics.Color.parseColor("#94a3b8"));
            noData.setTextSize(12);
            binding.layoutServiceBreakdown.addView(noData);
        }
    }
}
