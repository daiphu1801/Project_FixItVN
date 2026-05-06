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
    private AppointmentAdapter appointmentAdapter;

    @Override
    protected FragmentWorkerHomeBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerHomeBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // UC-W02: Toggle trạng thái Sẵn sàng nhận việc
        binding.switchOnlineStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.tvStatusDesc.setText("Sẵn sàng nhận việc");
                binding.tvStatusDesc.setTextColor(android.graphics.Color.parseColor("#0d1b2a"));
                binding.cardToggle.setStrokeColor(android.graphics.Color.parseColor("#42c2ff"));
            } else {
                binding.tvStatusDesc.setText("Đang nghỉ ngơi");
                binding.tvStatusDesc.setTextColor(android.graphics.Color.parseColor("#94a3b8"));
                binding.cardToggle.setStrokeColor(android.graphics.Color.parseColor("#e2e8f0"));
            }
        });

        // UC-W01: Nút xem chi tiết đơn đang chạy
        binding.layoutActiveOrder.btnViewActiveOrder.setOnClickListener(v -> {
            // TODO: Navigate sang fragment_worker_order_detail
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
        // TODO: Observe data from ViewModel and update UI
    }
}
