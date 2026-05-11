package com.fixit.ui.worker.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fixit.base.BaseFragment;
import com.fixit.data.model.Appointment;
import com.fixit.databinding.FragmentWorkerHomeBinding;

import java.util.Arrays;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerHomeFragment extends BaseFragment<FragmentWorkerHomeBinding> {

    private WorkerHomeViewModel viewModel;
    private com.fixit.ui.worker.WorkerStatusViewModel statusViewModel;
    private AppointmentAdapter appointmentAdapter;

    @Override
    protected FragmentWorkerHomeBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerHomeBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Card trạng thái → click chuyển sang tab Tìm việc để bật/tắt
        binding.cardToggle.setOnClickListener(v -> {
            com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                    requireActivity().findViewById(com.fixit.R.id.bottomNavigationView);
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(com.fixit.R.id.workerJobFragment);
            }
        });

        // UC-W01: Nút xem chi tiết đơn đang chạy
        binding.layoutActiveOrder.btnViewActiveOrder.setOnClickListener(v -> {
            // TODO: Navigate sang fragment_worker_order_detail
        });

        // Điều hướng sang Hồ sơ cá nhân khi nhấn vào Avatar
        binding.ivWorkerAvatar.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(v).navigate(com.fixit.R.id.workerProfileFragment);
        });

        // Điều hướng sang màn hình Thống kê đầy đủ
        binding.tvViewStatsDetail.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(v).navigate(com.fixit.R.id.workerStatsFragment);
        });

        // Setup RecyclerView cho Lịch hẹn hôm nay
        setupAppointmentList();
    }

    private void setupAppointmentList() {
        // Mock data - sau này sẽ thay bằng ViewModel + LiveData
        List<Appointment> mockAppointments = Arrays.asList(
                new Appointment("08:00 Hôm nay", "Sửa máy lạnh không mát", "123 Nguyễn Trãi, Quận 1, TP.HCM"),
                new Appointment("10:30 Hôm nay", "Thay ổ khóa cửa chính", "78 Đinh Tiên Hoàng, Bình Thạnh"),
                new Appointment("14:00 Hôm nay", "Thông tắc bồn rửa bát", "456 Lê Văn Sỹ, Quận 3, TP.HCM")
        );

        appointmentAdapter = new AppointmentAdapter(mockAppointments);
        binding.rvAppointments.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAppointments.setAdapter(appointmentAdapter);
    }

    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(this).get(WorkerHomeViewModel.class);
        statusViewModel = new ViewModelProvider(requireActivity()).get(com.fixit.ui.worker.WorkerStatusViewModel.class);

        // TODO: Observe data from ViewModel and update UI

        statusViewModel.isOnline.observe(getViewLifecycleOwner(), isOnline -> {
            if (isOnline) {
                binding.tvStatusDesc.setText("ONLINE — Đang nhận việc");
                binding.tvStatusDesc.setTextColor(android.graphics.Color.parseColor("#16a34a")); // Xanh lá
                binding.viewStatusDot.setBackgroundColor(android.graphics.Color.parseColor("#22c55e"));
            } else {
                binding.tvStatusDesc.setText("OFFLINE");
                binding.tvStatusDesc.setTextColor(android.graphics.Color.parseColor("#94a3b8")); // Xám
                binding.viewStatusDot.setBackgroundColor(android.graphics.Color.parseColor("#94a3b8"));
            }
        });
    }
}
