package com.fixit.feature.worker.home.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentWorkerHomeBinding;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerHomeFragment extends BaseFragment<FragmentWorkerHomeBinding> {

    private WorkerHomeViewModel viewModel;
    private com.fixit.feature.worker.presentation.WorkerStatusViewModel statusViewModel;
    private AppointmentAdapter appointmentAdapter;

    @Override
    protected FragmentWorkerHomeBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
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

        binding.layoutActiveOrder.btnViewActiveOrder.setOnClickListener(v -> {
            // TODO: Navigate to workerOrderDetailFragment when active-order API is ready.
        });

        binding.ivWorkerAvatar.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v)
                        .navigate(com.fixit.R.id.workerProfileFragment));

        binding.tvViewStatsDetail.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v)
                        .navigate(com.fixit.R.id.workerStatsFragment));

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
        statusViewModel = new ViewModelProvider(requireActivity())
                .get(com.fixit.feature.worker.presentation.WorkerStatusViewModel.class);

        viewModel.todayAppointments.observe(getViewLifecycleOwner(),
                appointments -> appointmentAdapter.submitList(appointments));

        statusViewModel.isOnline.observe(getViewLifecycleOwner(), isOnline -> {
            if (isOnline) {
                binding.tvStatusDesc.setText("ONLINE - Dang nhan viec");
                binding.tvStatusDesc.setTextColor(android.graphics.Color.parseColor("#16a34a"));
                binding.viewStatusDot.setBackgroundColor(android.graphics.Color.parseColor("#22c55e"));
            } else {
                binding.tvStatusDesc.setText("OFFLINE");
                binding.tvStatusDesc.setTextColor(android.graphics.Color.parseColor("#94a3b8"));
                binding.viewStatusDot.setBackgroundColor(android.graphics.Color.parseColor("#94a3b8"));
            }
        });
    }
}
