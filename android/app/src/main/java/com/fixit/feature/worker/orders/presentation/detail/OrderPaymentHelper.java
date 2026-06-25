package com.fixit.feature.worker.orders.presentation.detail;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fixit.databinding.LayoutWorkerPaymentSectionBinding;
import com.fixit.feature.worker.orders.domain.model.JobStatus;
import com.fixit.feature.worker.orders.domain.model.WorkerOrder;
import com.fixit.feature.worker.orders.presentation.WorkerOrdersViewModel;

public class OrderPaymentHelper {
    private final Fragment fragment;
    private final LayoutWorkerPaymentSectionBinding binding;
    private final WorkerOrdersViewModel viewModel;

    public OrderPaymentHelper(Fragment fragment, LayoutWorkerPaymentSectionBinding binding, WorkerOrdersViewModel viewModel) {
        this.fragment = fragment;
        this.binding = binding;
        this.viewModel = viewModel;
    }

    public void showPaymentQrCode(WorkerOrder currentOrder) {
        if (currentOrder == null || fragment.getContext() == null) {
            return;
        }

        if (currentOrder.getProofAfterUrl() == null || currentOrder.getProofAfterUrl().isEmpty()) {
            Toast.makeText(fragment.requireContext(),
                    "Bạn phải tải lên ảnh bằng chứng SAU khi sửa chữa trước khi thanh toán và hoàn thành!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        binding.llQrContainer.setVisibility(View.VISIBLE);
        binding.pbQrLoading.setVisibility(View.VISIBLE);

        long totalAmount = calculateTotalAmount(currentOrder);
        String qrUrl = viewModel.generateVietQrUrl(currentOrder.getOrderId(), totalAmount);

        if (!qrUrl.isEmpty()) {
            Glide.with(fragment)
                    .load(qrUrl)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                Target<Drawable> target, boolean isFirstResource) {
                            binding.pbQrLoading.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            binding.pbQrLoading.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(binding.ivPaymentQr);
        } else {
            binding.pbQrLoading.setVisibility(View.GONE);
            Toast.makeText(fragment.requireContext(), "Lỗi tạo mã QR", Toast.LENGTH_SHORT).show();
        }

        binding.tvPaymentStatus.setText("Chờ khách thanh toán");
        binding.tvPaymentStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FEF3C7")));
        binding.tvPaymentStatus.setTextColor(Color.parseColor("#D97706"));
        binding.tvPaymentSimulationHint.setText("Đang chờ khách quét và thanh toán... (Tự động cập nhật)");

        // Giả lập khách hàng thanh toán sau 4 giây
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            if (fragment.isAdded() && viewModel.currentStatus.getValue() == JobStatus.REPAIRING) {
                binding.tvPaymentStatus.setText("Đã thanh toán qua QR");
                binding.tvPaymentStatus
                        .setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DCFCE7")));
                binding.tvPaymentStatus.setTextColor(Color.parseColor("#22C55E"));
                binding.tvPaymentSimulationHint.setText("Thanh toán trực tuyến thành công!");

                Toast.makeText(fragment.requireContext(), "Khách hàng đã thanh toán qua QR thành công!", Toast.LENGTH_LONG)
                        .show();

                binding.btnShowQr.setEnabled(false);
                binding.btnConfirmCash.setVisibility(View.GONE);

                viewModel.advanceStatus(currentOrder.getOrderId());
            }
        }, 4000);
    }

    public void confirmCashPayment(WorkerOrder currentOrder) {
        if (currentOrder == null || fragment.getContext() == null) {
            return;
        }

        if (currentOrder.getProofAfterUrl() == null || currentOrder.getProofAfterUrl().isEmpty()) {
            Toast.makeText(fragment.requireContext(),
                    "Bạn phải tải lên ảnh bằng chứng SAU khi sửa chữa trước khi xác nhận hoàn thành!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        new androidx.appcompat.app.AlertDialog.Builder(fragment.requireContext())
                .setTitle("Xác nhận thanh toán tiền mặt")
                .setMessage("Bạn có chắc chắn đã nhận đủ tiền mặt từ khách hàng?")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    binding.tvPaymentStatus.setText("Đã thanh toán (Tiền mặt)");
                    binding.tvPaymentStatus
                            .setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DCFCE7")));
                    binding.tvPaymentStatus.setTextColor(Color.parseColor("#22C55E"));

                    Toast.makeText(fragment.requireContext(), "Đã xác nhận thu tiền mặt từ khách hàng!", Toast.LENGTH_SHORT)
                            .show();

                    binding.btnShowQr.setEnabled(false);
                    binding.btnConfirmCash.setEnabled(false);
                    binding.llQrContainer.setVisibility(View.GONE);

                    viewModel.advanceStatus(currentOrder.getOrderId());
                })
                .setNegativeButton("Hủy bỏ", null)
                .show();
    }

    private long calculateTotalAmount(WorkerOrder order) {
        long basePrice = 0;
        if (order != null) {
            try {
                basePrice = Long.parseLong(order.getPrice().replaceAll("[^\\d]", ""));
            } catch (Exception ignored) {
            }
        }
        return basePrice + viewModel.calculateTotalExtra();
    }
}
