package com.fixit.ui.worker.stats;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.fixit.base.BaseFragment;
import com.fixit.databinding.FragmentWorkerStatsBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerStatsFragment extends BaseFragment<FragmentWorkerStatsBinding> {

    @Override
    protected FragmentWorkerStatsBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerStatsBinding.inflate(inflater, container, false);
    }

    private AppointmentHorizontalAdapter adapter;

    @Override
    protected void setupViews() {
        setupAppointmentList();
        setupFilters();
        
        binding.ivStatsWorkerAvatar.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(v).navigate(com.fixit.R.id.workerProfileFragment);
        });
    }

    private void setupAppointmentList() {
        java.util.List<com.fixit.data.model.Appointment> mockAppointments = java.util.Arrays.asList(
                new com.fixit.data.model.Appointment("14:00 - 15:30", "Sửa máy giặt", "123 Xuân Thủy, Cầu Giấy"),
                new com.fixit.data.model.Appointment("16:30 - 18:00", "Lắp điều hòa", "45 Cầu Giấy, Hà Nội"),
                new com.fixit.data.model.Appointment("09:00 Mai", "Sửa bình nóng lạnh", "12 Trần Thái Tông")
        );

        adapter = new AppointmentHorizontalAdapter(mockAppointments);
        binding.rvUpcomingAppointments.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(
                requireContext(), androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
        binding.rvUpcomingAppointments.setAdapter(adapter);
    }

    private void setupFilters() {
        binding.btnFilterToday.setOnClickListener(v -> updateFilter(binding.btnFilterToday));
        binding.btnFilterWeek.setOnClickListener(v -> updateFilter(binding.btnFilterWeek));
        binding.btnFilterMonth.setOnClickListener(v -> updateFilter(binding.btnFilterMonth));
    }

    private void updateFilter(com.google.android.material.button.MaterialButton selectedButton) {
        // Reset all buttons
        binding.btnFilterToday.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#e2e8f0")));
        binding.btnFilterToday.setTextColor(android.graphics.Color.parseColor("#4a5568"));
        
        binding.btnFilterWeek.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#e2e8f0")));
        binding.btnFilterWeek.setTextColor(android.graphics.Color.parseColor("#4a5568"));
        
        binding.btnFilterMonth.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#e2e8f0")));
        binding.btnFilterMonth.setTextColor(android.graphics.Color.parseColor("#4a5568"));

        // Highlight selected
        selectedButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#42c2ff")));
        selectedButton.setTextColor(android.graphics.Color.WHITE);
        
        // TODO: Update chart data based on filter
    }

    @Override
    protected void observeData() {
        // TODO: Observe stats data
    }
}
