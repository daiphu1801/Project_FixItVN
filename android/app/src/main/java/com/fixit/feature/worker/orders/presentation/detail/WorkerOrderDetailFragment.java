package com.fixit.feature.worker.orders.presentation.detail;

import com.fixit.feature.worker.orders.presentation.WorkerOrdersViewModel;
import com.fixit.feature.worker.orders.domain.model.JobStatus;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentWorkerOrderDetailBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerOrderDetailFragment extends BaseFragment<FragmentWorkerOrderDetailBinding> {

    private WorkerOrdersViewModel viewModel;

    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(WorkerOrdersViewModel.class);
    }

    @Override
    protected FragmentWorkerOrderDetailBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerOrderDetailBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Setup Toolbar
        if (binding.appBarLayout.toolbar != null) {
            binding.appBarLayout.toolbar.setTitle("Chi tiết đơn hàng");
            binding.appBarLayout.toolbar.setNavigationOnClickListener(
                    v -> androidx.navigation.Navigation.findNavController(v).navigateUp());
        }

        // Action Buttons
        binding.btnCancelOrderDetail
                .setOnClickListener(v -> Toast.makeText(requireContext(), "Hủy đơn hàng", Toast.LENGTH_SHORT).show());

        binding.btnCompleteOrderDetail.setOnClickListener(v -> {
            JobStatus current = viewModel.currentStatus.getValue();
            if (current == JobStatus.REPAIRING) {
                // Chuyển sang màn hình hóa đơn để thu tiền
                String orderId = getArguments() != null ? getArguments().getString("orderId") : "ORD001";
                android.os.Bundle args = new android.os.Bundle();
                args.putString("orderId", orderId);
                androidx.navigation.Navigation.findNavController(v).navigate(com.fixit.R.id.workerInvoiceFragment,
                        args);
            } else {
                viewModel.advanceStatus();
                Toast.makeText(requireContext(), "Đã cập nhật trạng thái mới", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnAddExtraFee.setOnClickListener(v -> androidx.navigation.Navigation.findNavController(v)
                .navigate(com.fixit.R.id.workerExtraCostFragment));
    }

    @Override
    protected void observeData() {
        String orderId = getArguments() != null ? getArguments().getString("orderId") : null;

        if (orderId != null) {
            com.fixit.feature.worker.orders.domain.model.WorkerOrder order = viewModel.getOrderById(orderId);
            if (order != null) {
                bindOrderData(order);
                viewModel.initializeStatus(order.getStatus());
            } else {
                Toast.makeText(requireContext(), "Không tìm thấy đơn hàng: " + orderId, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Lỗi: Không nhận được ID đơn hàng", Toast.LENGTH_SHORT).show();
        }

        viewModel.currentStatus.observe(getViewLifecycleOwner(), this::updateTimelineUI);
    }

    private void bindOrderData(com.fixit.feature.worker.orders.domain.model.WorkerOrder order) {
        binding.tvOrderId.setText("#" + order.getOrderId());
        binding.tvOrderDetailService.setText(order.getServiceTitle());
        binding.tvOrderDetailCustomerName.setText(order.getCustomerName());
        binding.tvOrderDetailAddress.setText(order.getAddress());
        binding.tvOrderDetailScheduledTime.setText(order.getTimeSlot());
        binding.tvOrderDetailPrice.setText(order.getPrice());

        binding.btnChatCustomer.setOnClickListener(v -> {
            android.os.Bundle args = new android.os.Bundle();
            args.putString("workerId", order.getCustomerId()); // key 'workerId' maps to receiverId in ChatCustomerFragment
            args.putString("workerName", order.getCustomerName()); // key 'workerName' maps to receiverName in ChatCustomerFragment
            androidx.navigation.Navigation.findNavController(v).navigate(com.fixit.R.id.nav_worker_chat_detail, args);
        });

        // Cập nhật nhãn trạng thái (Badge)
        String status = order.getStatus();
        if ("ongoing".equals(status)) {
            binding.tvOrderDetailStatus.setText("ĐANG THỰC HIỆN");
            binding.tvOrderDetailStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0F2FE")));
            binding.tvOrderDetailStatus.setTextColor(Color.parseColor("#0ea5e9"));
        } else if ("completed".equals(status)) {
            binding.tvOrderDetailStatus.setText("ĐÃ HOÀN THÀNH");
            binding.tvOrderDetailStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DCFCE7")));
            binding.tvOrderDetailStatus.setTextColor(Color.parseColor("#22c55e"));
        } else {
            binding.tvOrderDetailStatus.setText("CHỜ XỬ LÝ");
            binding.tvOrderDetailStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F1F5F9")));
            binding.tvOrderDetailStatus.setTextColor(Color.parseColor("#64748b"));
        }
    }

    private void updateTimelineUI(JobStatus status) {
        int currentStep = status.getStep();

        // Cập nhật text nút bấm dựa trên trạng thái tiếp theo
        binding.btnCompleteOrderDetail.setText(status.getNextActionText());
        if (status == JobStatus.COMPLETED) {
            binding.btnCompleteOrderDetail.setEnabled(false);
            binding.btnCompleteOrderDetail.setAlpha(0.5f);
        }

        // Helper để cập nhật từng step
        updateStep(1, currentStep);
        updateStep(2, currentStep);
        updateStep(3, currentStep);
        updateStep(4, currentStep);
        updateStep(5, currentStep);
    }

    private void updateStep(int stepIndex, int currentStepIndex) {
        int colorActive = Color.parseColor("#0ea5e9");
        int colorDone = Color.parseColor("#0ea5e9");
        int colorPending = Color.parseColor("#e2e8f0");
        int textActive = Color.parseColor("#0ea5e9");
        int textDone = Color.parseColor("#0d1b2a");
        int textPending = Color.parseColor("#94a3b8");

        if (stepIndex < currentStepIndex) {
            // Trạng thái: Đã xong
            setStepState(stepIndex, true, false, colorDone, textDone);
        } else if (stepIndex == currentStepIndex) {
            // Trạng thái: Đang thực hiện
            setStepState(stepIndex, false, true, colorActive, textActive);
        } else {
            // Trạng thái: Chưa tới
            setStepState(stepIndex, false, false, colorPending, textPending);
        }
    }

    private void setStepState(int stepIndex, boolean isDone, boolean isActive, int color, int textColor) {
        // Tùy biến icon và line theo từng bước (Dựa trên ID đã đặt trong XML)
        switch (stepIndex) {
            case 1:
                binding.step1Icon.setImageResource(isDone ? com.fixit.R.drawable.ic_lucide_check_circle
                        : com.fixit.R.drawable.ic_lucide_check_circle);
                binding.step1Icon.setImageTintList(ColorStateList.valueOf(color));
                binding.step1Line.setBackgroundColor(color);
                binding.step1Title.setTextColor(textColor);
                break;
            case 2:
                binding.step2Icon.setImageResource(isDone ? com.fixit.R.drawable.ic_lucide_check_circle
                        : com.fixit.R.drawable.ic_lucide_check_circle);
                binding.step2Icon.setImageTintList(ColorStateList.valueOf(color));
                binding.step2Line.setBackgroundColor(color);
                binding.step2Title.setTextColor(textColor);
                break;
            case 3:
                binding.step3IconContainer.setVisibility(isActive ? View.VISIBLE : View.GONE);
                binding.step3Dot.setVisibility(!isActive && !isDone ? View.VISIBLE : View.GONE);
                // Nếu đã xong thì hiện icon check (tạm thời dùng icon mặc định nếu chưa có)
                if (isDone) {
                    binding.step3IconContainer.setVisibility(View.GONE);
                    binding.step3Dot.setVisibility(View.VISIBLE);
                    binding.step3Dot.setBackgroundTintList(ColorStateList.valueOf(color));
                }
                binding.step3Line.setBackgroundColor(color);
                binding.step3Title.setTextColor(textColor);
                break;
            case 4:
                binding.step4Dot.setBackgroundTintList(ColorStateList.valueOf(color));
                binding.step4Line.setBackgroundColor(color);
                binding.step4Title.setTextColor(textColor);
                break;
            case 5:
                binding.step5Dot.setBackgroundTintList(ColorStateList.valueOf(color));
                binding.step5Title.setTextColor(textColor);
                break;
        }
    }
}
