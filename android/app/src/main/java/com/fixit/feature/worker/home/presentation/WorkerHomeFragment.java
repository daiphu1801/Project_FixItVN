package com.fixit.feature.worker.home.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fixit.core.ui.BaseFragment;
import com.fixit.core.ui.ViewUtils;
import com.fixit.databinding.FragmentWorkerHomeBinding;
import com.fixit.feature.worker.home.domain.model.WorkerHome;

import java.util.ArrayList;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerHomeFragment extends BaseFragment<FragmentWorkerHomeBinding> {

    private WorkerHomeViewModel viewModel;
    private AppointmentAdapter appointmentAdapter;

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

        binding.ivWorkerAvatar.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v)
                        .navigate(com.fixit.R.id.workerProfileFragment));

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

        bindStatus(home);
        bindStats(home.getStatsOverview());
        bindActiveOrder(home.getActiveOrder());
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
}