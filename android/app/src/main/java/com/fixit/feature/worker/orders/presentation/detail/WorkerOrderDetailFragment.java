package com.fixit.feature.worker.orders.presentation.detail;

import com.fixit.feature.worker.orders.presentation.WorkerOrdersViewModel;
import com.fixit.feature.worker.orders.domain.model.JobStatus;
import com.fixit.feature.worker.orders.domain.model.WorkerOrder;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentWorkerOrderDetailBinding;
import com.fixit.databinding.LayoutOrderCustomerCardBinding;
import com.fixit.databinding.LayoutOrderMetaCardBinding;
import com.fixit.databinding.LayoutOrderTimelineCardBinding;
import com.fixit.databinding.LayoutPricingSummaryCardBinding;
import com.fixit.databinding.LayoutWorkerPaymentSectionBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerOrderDetailFragment extends BaseFragment<FragmentWorkerOrderDetailBinding> {

    private WorkerOrdersViewModel viewModel;
    private WorkerOrder currentOrder;

    // Sub-layout bindings cho các section đã tách
    private LayoutOrderCustomerCardBinding customerBinding;
    private LayoutOrderMetaCardBinding metaBinding;
    private LayoutOrderTimelineCardBinding timelineBinding;
    private LayoutPricingSummaryCardBinding pricingBinding;
    private LayoutWorkerPaymentSectionBinding paymentBinding;

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
        // Lấy binding của các sub-layout từ include tags
        customerBinding  = LayoutOrderCustomerCardBinding.bind(binding.cardCustomerInfo.getRoot());
        metaBinding      = LayoutOrderMetaCardBinding.bind(binding.cardOrderMeta.getRoot());
        timelineBinding  = LayoutOrderTimelineCardBinding.bind(binding.cardTimeline.getRoot());
        pricingBinding   = LayoutPricingSummaryCardBinding.bind(binding.cardPricingSummary.getRoot());
        paymentBinding   = LayoutWorkerPaymentSectionBinding.bind(binding.sectionPayment.getRoot());

        // Setup Toolbar
        if (binding.appBarLayout.toolbar != null) {
            binding.appBarLayout.toolbar.setTitle("Chi tiết đơn hàng");
            binding.appBarLayout.toolbar.setNavigationOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v).navigateUp()
            );
        }

        // Chat với khách hàng
        customerBinding.btnChatCustomer.setOnClickListener(v -> {
            String orderId = getArguments() != null ? getArguments().getString("orderId") : "ORD001";
            android.os.Bundle args = new android.os.Bundle();
            args.putString("orderId", orderId);
            args.putString("chatTitle", customerBinding.tvOrderDetailCustomerName.getText().toString());
            androidx.navigation.Navigation.findNavController(v).navigate(com.fixit.R.id.workerChatFragment, args);
        });

        // Hủy đơn
        binding.btnCancelOrderDetail.setOnClickListener(v ->
            Toast.makeText(requireContext(), "Hủy đơn hàng", Toast.LENGTH_SHORT).show()
        );

        // Tiến độ tiếp theo
        binding.btnCompleteOrderDetail.setOnClickListener(v -> {
            viewModel.advanceStatus();
            Toast.makeText(requireContext(), "Đã cập nhật trạng thái mới", Toast.LENGTH_SHORT).show();
        });

        // Thêm chi phí phát sinh
        pricingBinding.btnAddExtraFee.setOnClickListener(v ->
            androidx.navigation.Navigation.findNavController(v).navigate(com.fixit.R.id.workerExtraCostFragment)
        );

        // Hiển thị QR thanh toán
        paymentBinding.btnShowQr.setOnClickListener(v -> showPaymentQrCode());

        // Xác nhận tiền mặt
        paymentBinding.btnConfirmCash.setOnClickListener(v -> confirmCashPayment());

        // Hiển thị QR thanh toán ở thanh đáy thích ứng
        binding.btnBottomShowQr.setOnClickListener(v -> {
            showPaymentQrCode();
            // Cuộn mượt đến phần thanh toán QR
            binding.scrollOrderDetail.post(() -> 
                binding.scrollOrderDetail.smoothScrollTo(0, binding.sectionPayment.getRoot().getTop())
            );
        });

        // Xác nhận tiền mặt ở thanh đáy thích ứng
        binding.btnBottomConfirmCash.setOnClickListener(v -> confirmCashPayment());
    }

    @Override
    protected void observeData() {
        String orderId = getArguments() != null ? getArguments().getString("orderId") : null;

        if (orderId != null) {
            WorkerOrder order = viewModel.getOrderById(orderId);
            if (order != null) {
                currentOrder = order;
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

    private void bindOrderData(WorkerOrder order) {
        binding.tvOrderId.setText("#" + order.getOrderId());
        binding.tvOrderDetailService.setText(order.getServiceTitle());
        // Sub-layout bindings
        customerBinding.tvOrderDetailCustomerName.setText(order.getCustomerName());
        metaBinding.tvOrderDetailAddress.setText(order.getAddress());
        metaBinding.tvOrderDetailScheduledTime.setText(order.getTimeSlot());
        pricingBinding.tvOrderDetailPrice.setText(order.getPrice());

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

        // Cấu hình mặc định cho các trạng thái thông thường
        binding.btnCancelOrderDetail.setVisibility(View.VISIBLE);
        binding.btnCompleteOrderDetail.setVisibility(View.VISIBLE);
        binding.btnCompleteOrderDetail.setText(status.getNextActionText());
        binding.btnCompleteOrderDetail.setEnabled(true);
        binding.btnCompleteOrderDetail.setAlpha(1.0f);
        binding.btnCompleteOrderDetail.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0ea5e9")));

        binding.btnBottomConfirmCash.setVisibility(View.GONE);
        binding.btnBottomShowQr.setVisibility(View.GONE);

        if (status == JobStatus.REPAIRING) {
            // Trạng thái đang sửa chữa & Chờ thanh toán -> Kích hoạt thanh đáy thích ứng kép
            binding.btnCancelOrderDetail.setVisibility(View.GONE);
            binding.btnCompleteOrderDetail.setVisibility(View.GONE);

            binding.btnBottomConfirmCash.setVisibility(View.VISIBLE);
            binding.btnBottomConfirmCash.setEnabled(true);
            binding.btnBottomShowQr.setVisibility(View.VISIBLE);
            binding.btnBottomShowQr.setEnabled(true);

            paymentBinding.cardPaymentSection.setVisibility(View.VISIBLE);
            paymentBinding.tvPaymentStatus.setText("Chờ khách thanh toán");
            paymentBinding.tvPaymentStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FEF3C7")));
            paymentBinding.tvPaymentStatus.setTextColor(Color.parseColor("#D97706"));

        } else if (status == JobStatus.COMPLETED) {
            // Trạng thái đã hoàn thành -> 1 nút chiếm 100% bề ngang, ẩn nút hủy
            binding.btnCancelOrderDetail.setVisibility(View.GONE);
            binding.btnCompleteOrderDetail.setVisibility(View.VISIBLE);
            binding.btnCompleteOrderDetail.setEnabled(false);
            binding.btnCompleteOrderDetail.setAlpha(0.5f);
            binding.btnCompleteOrderDetail.setText("Đã hoàn thành & thanh toán");
            binding.btnCompleteOrderDetail.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#10b981"))); // Xanh lá cây sang trọng

            paymentBinding.cardPaymentSection.setVisibility(View.VISIBLE);
            paymentBinding.tvPaymentStatus.setText("Đã thanh toán");
            paymentBinding.tvPaymentStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DCFCE7")));
            paymentBinding.tvPaymentStatus.setTextColor(Color.parseColor("#22C55E"));
            paymentBinding.llQrContainer.setVisibility(View.GONE);

        } else {
            paymentBinding.cardPaymentSection.setVisibility(View.GONE);
        }

        updateStep(1, currentStep);
        updateStep(2, currentStep);
        updateStep(3, currentStep);
        updateStep(4, currentStep);
        updateStep(5, currentStep);
    }

    private void showPaymentQrCode() {
        if (currentOrder == null) return;

        paymentBinding.llQrContainer.setVisibility(View.VISIBLE);
        paymentBinding.pbQrLoading.setVisibility(View.VISIBLE);

        long totalAmount = calculateTotalAmount(currentOrder);
        String qrUrl = viewModel.generateVietQrUrl(currentOrder.getOrderId(), totalAmount);

        if (!qrUrl.isEmpty()) {
            Glide.with(this)
                .load(qrUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                            Target<Drawable> target, boolean isFirstResource) {
                        paymentBinding.pbQrLoading.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                            Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        paymentBinding.pbQrLoading.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(paymentBinding.ivPaymentQr);
        } else {
            paymentBinding.pbQrLoading.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Lỗi tạo mã QR", Toast.LENGTH_SHORT).show();
        }

        paymentBinding.tvPaymentStatus.setText("Chờ khách thanh toán");
        paymentBinding.tvPaymentStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FEF3C7")));
        paymentBinding.tvPaymentStatus.setTextColor(Color.parseColor("#D97706"));
        paymentBinding.tvPaymentSimulationHint.setText("Đang chờ khách quét và thanh toán... (Tự động cập nhật)");

        // Giả lập khách hàng thanh toán sau 4 giây
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            if (isAdded() && viewModel.currentStatus.getValue() == JobStatus.REPAIRING) {
                paymentBinding.tvPaymentStatus.setText("Đã thanh toán qua QR");
                paymentBinding.tvPaymentStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DCFCE7")));
                paymentBinding.tvPaymentStatus.setTextColor(Color.parseColor("#22C55E"));
                paymentBinding.tvPaymentSimulationHint.setText("Thanh toán trực tuyến thành công!");

                Toast.makeText(requireContext(), "Khách hàng đã thanh toán qua QR thành công!", Toast.LENGTH_LONG).show();

                paymentBinding.btnShowQr.setEnabled(false);
                paymentBinding.btnConfirmCash.setVisibility(View.GONE);

                viewModel.advanceStatus();
            }
        }, 4000);
    }

    private void confirmCashPayment() {
        if (currentOrder == null) return;

        paymentBinding.tvPaymentStatus.setText("Đã thanh toán (Tiền mặt)");
        paymentBinding.tvPaymentStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DCFCE7")));
        paymentBinding.tvPaymentStatus.setTextColor(Color.parseColor("#22C55E"));

        Toast.makeText(requireContext(), "Đã xác nhận thu tiền mặt từ khách hàng!", Toast.LENGTH_SHORT).show();

        paymentBinding.btnShowQr.setEnabled(false);
        paymentBinding.btnConfirmCash.setEnabled(false);
        paymentBinding.llQrContainer.setVisibility(View.GONE);

        viewModel.advanceStatus();
    }

    private long calculateTotalAmount(WorkerOrder order) {
        long basePrice = 0;
        if (order != null) {
            try {
                basePrice = Long.parseLong(order.getPrice().replaceAll("[^\\d]", ""));
            } catch (Exception ignored) {}
        }
        return basePrice + viewModel.calculateTotalExtra();
    }

    private void updateStep(int stepIndex, int currentStepIndex) {
        int colorActive  = Color.parseColor("#0ea5e9");
        int colorDone    = Color.parseColor("#0ea5e9");
        int colorPending = Color.parseColor("#e2e8f0");
        int textActive   = Color.parseColor("#0ea5e9");
        int textDone     = Color.parseColor("#0d1b2a");
        int textPending  = Color.parseColor("#94a3b8");

        if (stepIndex < currentStepIndex) {
            setStepState(stepIndex, true, false, colorDone, textDone);
        } else if (stepIndex == currentStepIndex) {
            setStepState(stepIndex, false, true, colorActive, textActive);
        } else {
            setStepState(stepIndex, false, false, colorPending, textPending);
        }
    }

    private void setStepState(int stepIndex, boolean isDone, boolean isActive, int color, int textColor) {
        switch (stepIndex) {
            case 1:
                timelineBinding.step1Icon.setImageResource(com.fixit.R.drawable.ic_lucide_check_circle);
                timelineBinding.step1Icon.setImageTintList(ColorStateList.valueOf(color));
                timelineBinding.step1Line.setBackgroundColor(color);
                timelineBinding.step1Title.setTextColor(textColor);
                break;
            case 2:
                timelineBinding.step2Icon.setImageResource(com.fixit.R.drawable.ic_lucide_check_circle);
                timelineBinding.step2Icon.setImageTintList(ColorStateList.valueOf(color));
                timelineBinding.step2Line.setBackgroundColor(color);
                timelineBinding.step2Title.setTextColor(textColor);
                break;
            case 3:
                timelineBinding.step3IconContainer.setVisibility(View.VISIBLE);
                timelineBinding.step3Pulse.setVisibility(isActive ? View.VISIBLE : View.GONE);
                timelineBinding.step3Dot.setVisibility(View.VISIBLE);
                timelineBinding.step3Dot.setBackgroundTintList(ColorStateList.valueOf(color));
                timelineBinding.step3Line.setBackgroundColor(color);
                timelineBinding.step3Title.setTextColor(textColor);
                break;
            case 4:
                timelineBinding.step4Dot.setBackgroundTintList(ColorStateList.valueOf(color));
                timelineBinding.step4Line.setBackgroundColor(color);
                timelineBinding.step4Title.setTextColor(textColor);
                break;
            case 5:
                timelineBinding.step5Dot.setBackgroundTintList(ColorStateList.valueOf(color));
                timelineBinding.step5Title.setTextColor(textColor);
                break;
        }
    }
}
