package com.fixit.feature.worker.orders.presentation.complaint;

import com.fixit.feature.worker.orders.presentation.WorkerOrdersViewModel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.fixit.core.ui.BaseFragment;
import com.fixit.feature.worker.orders.domain.model.WorkerOrder;
import com.fixit.databinding.FragmentWorkerComplaintBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerComplaintFragment extends BaseFragment<FragmentWorkerComplaintBinding> {

    private WorkerOrdersViewModel viewModel;
    private String orderId;

    @Override
    protected FragmentWorkerComplaintBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerComplaintBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        if (getArguments() != null) {
            orderId = getArguments().getString("orderId");
        }

        binding.btnSubmitResponse.setOnClickListener(v -> {
            String response = binding.etResponse.getText().toString();
            if (response.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập giải trình của bạn", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Mock logic phản hồi
            Toast.makeText(requireContext(), "Đã gửi phản hồi thành công", Toast.LENGTH_SHORT).show();
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        // Setup Toolbar
        android.view.View btnBack = requireView().findViewById(com.fixit.R.id.btnBack);
        if (btnBack != null) {
            btnBack.setVisibility(android.view.View.VISIBLE);
            btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        }
        
        android.widget.TextView tvTitle = requireView().findViewById(com.fixit.R.id.tvToolbarTitle);
        if (tvTitle != null) tvTitle.setText("Chi tiết khiếu nại");
    }

    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(requireActivity()).get(WorkerOrdersViewModel.class);
        
        WorkerOrder order = viewModel.getOrderById(orderId);
        if (order != null) {
            binding.tvOrderTitle.setText("📦 Đơn #" + order.getOrderId() + " – " + order.getServiceTitle());
            binding.tvComplaintReason.setText(order.getComplaintReason());
            binding.tvCountdown.setText(order.getComplaintDeadline());
            binding.tvFrozenAmount.setText(order.getPrice());
        }
    }
}
