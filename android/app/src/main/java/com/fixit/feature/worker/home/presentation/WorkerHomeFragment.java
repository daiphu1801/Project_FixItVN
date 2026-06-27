package com.fixit.feature.worker.home.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fixit.core.common.AutoRefreshHelper;
import com.fixit.core.ui.BaseFragment;
import com.fixit.core.ui.ViewUtils;
import com.fixit.databinding.FragmentWorkerHomeBinding;
import com.fixit.feature.worker.home.domain.model.WorkerHome;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerHomeFragment extends BaseFragment<FragmentWorkerHomeBinding> {

    private WorkerHomeViewModel viewModel;
    private AppointmentAdapter appointmentAdapter;
    private AutoRefreshHelper autoRefreshHelper;

    @Override
    protected FragmentWorkerHomeBinding inflateViewBinding(
            LayoutInflater inflater,
            ViewGroup container
    ) {
        return FragmentWorkerHomeBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        binding.cardToggle.setOnClickListener(v -> {
            com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                    requireActivity().findViewById(com.fixit.R.id.bottomNavigationView);
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(com.fixit.R.id.workerJobFragment);
            }
        });

        binding.ivWorkerAvatar.setOnClickListener(v -> {
            com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                    requireActivity().findViewById(com.fixit.R.id.bottomNavigationView);
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(com.fixit.R.id.workerProfileFragment);
            } else {
                androidx.navigation.Navigation.findNavController(v)
                        .navigate(com.fixit.R.id.workerProfileFragment);
            }
        });

        binding.tvViewStatsDetail.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v)
                        .navigate(com.fixit.R.id.workerStatsFragment));

        binding.ivChatWorker.setOnClickListener(v -> {
            if (navController != null) {
                navController.navigate(com.fixit.R.id.workerChatFragment);
            }
        });

        binding.tvViewAllAppointments.setOnClickListener(v -> {
            com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                    requireActivity().findViewById(com.fixit.R.id.bottomNavigationView);
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(com.fixit.R.id.workerOrdersFragment);
            }
        });

        setupAppointmentList();
    }

    private void setupAppointmentList() {
        appointmentAdapter = new AppointmentAdapter(new ArrayList<>());
        binding.rvAppointments.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAppointments.setAdapter(appointmentAdapter);
    }

    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(this).get(WorkerHomeViewModel.class);

        viewModel.workerHome.observe(getViewLifecycleOwner(), this::bindWorkerHome);

        viewModel.todayAppointments.observe(getViewLifecycleOwner(), appointments -> {
            appointmentAdapter.submitList(appointments);

            boolean empty = appointments == null || appointments.isEmpty();
            binding.rvAppointments.setVisibility(empty ? View.GONE : View.VISIBLE);
            binding.tvEmptyAppointments.setVisibility(empty ? View.VISIBLE : View.GONE);
        });

        viewModel.isLoading.observe(getViewLifecycleOwner(), loading -> {
            if (binding.layoutLoading != null) {
                binding.layoutLoading.getRoot().setVisibility(loading ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.trim().isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindWorkerHome(WorkerHome home) {
        if (home == null) {
            return;
        }

        binding.tvGreeting.setText(nonBlank(home.getGreetingText(), "Xin chào,"));
        binding.tvWorkerName.setText(nonBlank(home.getFullName(), "Thợ FixIt"));

        // Hiển thị avatar thợ ở trang chủ
        String avatarUrl = home.getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            binding.ivWorkerAvatar.setPadding(0, 0, 0, 0);
            binding.ivWorkerAvatar.setImageTintList(null);
            com.bumptech.glide.Glide.with(this)
                    .load(avatarUrl)
                    .circleCrop()
                    .into(binding.ivWorkerAvatar);
        } else {
            binding.ivWorkerAvatar.setPadding(0, 0, 0, 0);
            binding.ivWorkerAvatar.setImageTintList(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#42c2ff")
            ));
            binding.ivWorkerAvatar.setImageResource(com.fixit.R.drawable.ic_lucide_user);
        }

        bindStatus(home);
        bindStats(home.getStatsOverview());
        bindActiveOrder(home.getActiveOrder());
        bindIncomeChart(home.getIncomeChart());
    }

    private void bindStatus(WorkerHome home) {
        boolean available = home.isAvailable();

        if (available) {
            binding.tvStatusDesc.setText("ONLINE - Đang nhận việc");
            binding.tvStatusDesc.setTextColor(android.graphics.Color.parseColor("#16a34a"));
            binding.viewStatusDot.setBackgroundColor(android.graphics.Color.parseColor("#22c55e"));
        } else {
            binding.tvStatusDesc.setText("OFFLINE");
            binding.tvStatusDesc.setTextColor(android.graphics.Color.parseColor("#94a3b8"));
            binding.viewStatusDot.setBackgroundColor(android.graphics.Color.parseColor("#94a3b8"));
        }

        if (!home.isCanReceiveJob() && home.getReceiveJobBlockedReason() != null) {
            binding.tvStatusHelp.setText(home.getReceiveJobBlockedReason());
            binding.tvStatusHelp.setTextColor(android.graphics.Color.parseColor("#ef4444"));
        } else {
            binding.tvStatusHelp.setText(nonBlank(
                    home.getStatusHelpText(),
                    "Nhấn để thay đổi trạng thái →"
            ));
            binding.tvStatusHelp.setTextColor(android.graphics.Color.parseColor("#42c2ff"));
        }
    }

    private void bindStats(WorkerHome.StatsOverview stats) {
        if (stats == null) {
            binding.layoutStats.tvOrdersToday.setText("0");
            binding.layoutStats.tvRevenueToday.setText(ViewUtils.formatCurrency(0));
            binding.layoutStats.tvRating.setText("0.0 ★");
            return;
        }

        binding.layoutStats.tvOrdersToday.setText(String.valueOf(stats.getCompletedJobsToday()));
        binding.layoutStats.tvRevenueToday.setText(ViewUtils.formatCurrency(stats.getIncomeToday()));
        binding.layoutStats.tvRating.setText(
                String.format(Locale.US, "%.1f ★", stats.getAverageRating())
        );
    }

    private void bindActiveOrder(WorkerHome.ActiveOrder activeOrder) {
        if (activeOrder == null) {
            binding.layoutActiveOrder.getRoot().setVisibility(View.GONE);
            return;
        }

        binding.layoutActiveOrder.getRoot().setVisibility(View.VISIBLE);

        binding.layoutActiveOrder.tvActiveOrderStatus.setText(
                nonBlank(activeOrder.getStatusText(), "Đang thực hiện")
        );

        binding.layoutActiveOrder.tvActiveOrderTitle.setText(
                nonBlank(activeOrder.getServiceName(), "Đơn đang thực hiện")
        );

        binding.layoutActiveOrder.tvActiveOrderAddress.setText(
                nonBlank(activeOrder.getAddress(), "")
        );

        binding.layoutActiveOrder.btnViewActiveOrder.setOnClickListener(v -> {
            if (navController == null || activeOrder.getBookingId() == null) {
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putString("orderId", activeOrder.getBookingId());
            navController.navigate(com.fixit.R.id.workerOrderDetailFragment, bundle);
        });
    }

    private String nonBlank(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (autoRefreshHelper == null) {
            autoRefreshHelper = new AutoRefreshHelper(
                    requireContext(),
                    0L,
                    () -> {
                        if (viewModel != null) {
                            viewModel.loadWorkerHome(false);
                        }
                    },
                    "com.fixit.BOOKING_UPDATE",
                    "com.fixit.PROFILE_UPDATE"
            );
        }
        autoRefreshHelper.start();
    }

    @Override
    public void onPause() {
        if (autoRefreshHelper != null) {
            autoRefreshHelper.stop();
        }
        super.onPause();
    }

    private void bindIncomeChart(List<WorkerHome.IncomeChartPoint> chartPoints) {
        if (chartPoints == null || chartPoints.isEmpty()) {
            return;
        }

        View[] bars = new View[] {
                binding.layoutChart.barMon, binding.layoutChart.barTue, binding.layoutChart.barWed,
                binding.layoutChart.barThu, binding.layoutChart.barFri, binding.layoutChart.barSat,
                binding.layoutChart.barSun
        };
        android.widget.TextView[] labels = new android.widget.TextView[] {
                binding.layoutChart.lblMon, binding.layoutChart.lblTue, binding.layoutChart.lblWed,
                binding.layoutChart.lblThu, binding.layoutChart.lblFri, binding.layoutChart.lblSat,
                binding.layoutChart.lblSun
        };

        int count = Math.min(chartPoints.size(), bars.length);

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
                WorkerHome.IncomeChartPoint point = chartPoints.get(i);

                labels[i].setText(mapChartLabelToVietnamese(point.getLabel()));

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

                if (maxIncome > 0 && point.getIncome() == maxIncome) {
                    bars[i].setBackgroundColor(android.graphics.Color.parseColor("#42c2ff"));
                    labels[i].setTextColor(android.graphics.Color.parseColor("#0d1b2a"));
                    labels[i].setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
                } else {
                    bars[i].setBackgroundColor(android.graphics.Color.parseColor("#d6f2ff"));
                    labels[i].setTextColor(android.graphics.Color.parseColor("#4a5568"));
                    labels[i].setTypeface(android.graphics.Typeface.DEFAULT);
                }

                bars[i].setOnClickListener(v -> {
                    String msg = String.format("Doanh thu %s: %s (%d đơn)",
                            mapChartLabelToVietnamese(point.getLabel()),
                            ViewUtils.formatCurrency(point.getIncome()),
                            point.getCompletedJobs()
                    );
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                });
            } else {
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
}