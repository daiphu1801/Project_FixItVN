package com.fixit.feature.worker.orders.presentation.detail;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.Navigation;

import com.fixit.R;
import com.fixit.databinding.FragmentWorkerOrderDetailBinding;
import com.fixit.databinding.LayoutOrderCustomerCardBinding;
import com.fixit.databinding.LayoutOrderMetaCardBinding;
import com.fixit.databinding.LayoutOrderTimelineCardBinding;
import com.fixit.databinding.LayoutPricingSummaryCardBinding;
import com.fixit.databinding.LayoutProofOfWorkSectionBinding;
import com.fixit.databinding.LayoutWorkerPaymentSectionBinding;
import com.fixit.feature.worker.orders.domain.model.JobStatus;
import com.fixit.feature.worker.orders.domain.model.WorkerOrder;
import com.fixit.feature.worker.orders.presentation.WorkerOrdersViewModel;

public class OrderDetailUiHelper {

    private final WorkerOrderDetailFragment fragment;
    private final FragmentWorkerOrderDetailBinding binding;
    private final LayoutOrderCustomerCardBinding customerBinding;
    private final LayoutOrderMetaCardBinding metaBinding;
    private final LayoutOrderTimelineCardBinding timelineBinding;
    private final LayoutPricingSummaryCardBinding pricingBinding;
    private final LayoutWorkerPaymentSectionBinding paymentBinding;
    private final LayoutProofOfWorkSectionBinding proofBinding;

    private final OrderTimelineHelper timelineHelper;
    private final OrderMapHelper mapHelper;
    private final OrderPaymentHelper paymentHelper;

    public OrderDetailUiHelper(
            WorkerOrderDetailFragment fragment,
            FragmentWorkerOrderDetailBinding binding,
            LayoutOrderCustomerCardBinding customerBinding,
            LayoutOrderMetaCardBinding metaBinding,
            LayoutOrderTimelineCardBinding timelineBinding,
            LayoutPricingSummaryCardBinding pricingBinding,
            LayoutWorkerPaymentSectionBinding paymentBinding,
            LayoutProofOfWorkSectionBinding proofBinding,
            WorkerOrdersViewModel viewModel,
            android.os.Bundle savedInstanceState) {
        this.fragment = fragment;
        this.binding = binding;
        this.customerBinding = customerBinding;
        this.metaBinding = metaBinding;
        this.timelineBinding = timelineBinding;
        this.pricingBinding = pricingBinding;
        this.paymentBinding = paymentBinding;
        this.proofBinding = proofBinding;

        this.timelineHelper = new OrderTimelineHelper(timelineBinding);
        this.mapHelper = new OrderMapHelper(binding.mapView);
        this.mapHelper.onCreate(savedInstanceState);
        this.paymentHelper = new OrderPaymentHelper(fragment, paymentBinding, viewModel);
    }

    public OrderMapHelper getMapHelper() {
        return mapHelper;
    }

    public OrderPaymentHelper getPaymentHelper() {
        return paymentHelper;
    }

    public void displayProofBeforeImage(Uri uri) {
        proofBinding.ivProofBeforeImage.setVisibility(View.VISIBLE);
        proofBinding.icProofBeforeCamera.setVisibility(View.GONE);
        proofBinding.tvProofBeforeLabel.setVisibility(View.GONE);
        com.bumptech.glide.Glide.with(fragment).load(uri).into(proofBinding.ivProofBeforeImage);
    }

    public void displayProofAfterImage(Uri uri) {
        proofBinding.ivProofAfterImage.setVisibility(View.VISIBLE);
        proofBinding.icProofAfterCamera.setVisibility(View.GONE);
        proofBinding.tvProofAfterLabel.setVisibility(View.GONE);
        com.bumptech.glide.Glide.with(fragment).load(uri).into(proofBinding.ivProofAfterImage);
    }

    public void bindOrderData(WorkerOrder order) {
        if (order == null) return;

        String orderId = order.getOrderId();
        if (orderId != null && orderId.length() > 8) {
            binding.tvOrderId.setText("#" + orderId.substring(0, 8));
        } else {
            binding.tvOrderId.setText(orderId != null ? "#" + orderId : "");
        }
        binding.tvOrderDetailService.setText(order.getServiceTitle());
        customerBinding.tvOrderDetailCustomerName.setText(order.getCustomerName());
        metaBinding.tvOrderDetailAddress.setText(order.getAddress());
        metaBinding.tvOrderDetailScheduledTime.setText(order.getTimeSlot());
        pricingBinding.tvOrderDetailPrice.setText(order.getPrice());

        if (mapHelper != null) {
            mapHelper.loadMap(fragment.requireContext(), order.getAddress());
        }

        String status = order.getStatus();
        if ("ongoing".equals(status)) {
            binding.tvOrderDetailStatus.setText("ĐANG THỰC HIỆN");
            binding.tvOrderDetailStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#eff6ff")));
            binding.tvOrderDetailStatus.setTextColor(Color.parseColor("#2563eb"));
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

    public void updateTimelineUI(JobStatus status, WorkerOrder currentOrder) {
        if (status == null) return;

        if (status == JobStatus.SURVEYING) {
            pricingBinding.btnAddExtraFee.setVisibility(View.VISIBLE);
        } else {
            pricingBinding.btnAddExtraFee.setVisibility(View.GONE);
        }

        binding.btnCancelOrderDetail.setVisibility(View.VISIBLE);
        binding.btnCompleteOrderDetail.setVisibility(View.VISIBLE);
        binding.btnCompleteOrderDetail.setText(status.getNextActionText());
        binding.btnCompleteOrderDetail.setEnabled(true);
        binding.btnCompleteOrderDetail.setAlpha(1.0f);
        binding.btnCompleteOrderDetail.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2563eb")));

        binding.btnBottomConfirmCash.setVisibility(View.GONE);
        binding.btnBottomShowQr.setVisibility(View.GONE);

        if (status == JobStatus.REPAIRING) {
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

        } else if (status == JobStatus.WAITING_APPROVAL) {
            binding.btnCancelOrderDetail.setVisibility(View.GONE);
            binding.btnCompleteOrderDetail.setVisibility(View.VISIBLE);
            binding.btnCompleteOrderDetail.setEnabled(false);
            binding.btnCompleteOrderDetail.setAlpha(0.7f);
            binding.btnCompleteOrderDetail.setText("Chờ khách hàng nghiệm thu");
            binding.btnCompleteOrderDetail.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#f59e0b")));

            paymentBinding.cardPaymentSection.setVisibility(View.VISIBLE);

            if (currentOrder != null && ("CASH".equalsIgnoreCase(currentOrder.getPaymentMethod())
                    || "TIEN_MAT".equalsIgnoreCase(currentOrder.getPaymentMethod()))) {
                paymentBinding.tvPaymentStatus.setText("Đã nhận tiền mặt (Chờ duyệt)");
                paymentBinding.tvPaymentStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DCFCE7")));
                paymentBinding.tvPaymentStatus.setTextColor(Color.parseColor("#22C55E"));

                binding.btnBottomConfirmCash.setVisibility(View.GONE);
                binding.btnBottomShowQr.setVisibility(View.GONE);
                paymentBinding.btnShowQr.setVisibility(View.GONE);
                paymentBinding.btnConfirmCash.setVisibility(View.GONE);
                paymentBinding.llQrContainer.setVisibility(View.GONE);
            } else {
                paymentBinding.tvPaymentStatus.setText("Chờ khách thanh toán qua QR");
                paymentBinding.tvPaymentStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#eff6ff")));
                paymentBinding.tvPaymentStatus.setTextColor(Color.parseColor("#2563eb"));

                binding.btnBottomConfirmCash.setVisibility(View.GONE);
                binding.btnBottomShowQr.setVisibility(View.VISIBLE);
                binding.btnBottomShowQr.setEnabled(true);

                paymentBinding.btnShowQr.setVisibility(View.VISIBLE);
                paymentBinding.btnShowQr.setEnabled(true);
                paymentBinding.btnConfirmCash.setVisibility(View.GONE);
            }

        } else if (status == JobStatus.COMPLETED) {
            binding.btnCancelOrderDetail.setVisibility(View.GONE);
            binding.btnCompleteOrderDetail.setVisibility(View.VISIBLE);
            binding.btnCompleteOrderDetail.setEnabled(false);
            binding.btnCompleteOrderDetail.setAlpha(0.5f);
            binding.btnCompleteOrderDetail.setText("Đã hoàn thành & thanh toán");
            binding.btnCompleteOrderDetail.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#10b981")));

            paymentBinding.cardPaymentSection.setVisibility(View.VISIBLE);
            paymentBinding.tvPaymentStatus.setText("Đã thanh toán");
            paymentBinding.tvPaymentStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DCFCE7")));
            paymentBinding.tvPaymentStatus.setTextColor(Color.parseColor("#22C55E"));
            paymentBinding.llQrContainer.setVisibility(View.GONE);

        } else {
            paymentBinding.cardPaymentSection.setVisibility(View.GONE);
        }

        if (timelineHelper != null) {
            timelineHelper.updateTimelineUI(status);
        }
    }

    public void setupClickListeners(
            Runnable onPickBefore,
            Runnable onPickAfter,
            WorkerOrdersViewModel viewModel) {

        proofBinding.cardProofBefore.setOnClickListener(v -> onPickBefore.run());
        proofBinding.cardProofAfter.setOnClickListener(v -> onPickAfter.run());

        binding.cardMapPlaceholder.setOnClickListener(v -> {
            WorkerOrder order = fragment.getCurrentOrder();
            if (order != null && order.getAddress() != null) {
                String address = order.getAddress();
                try {
                    Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    fragment.startActivity(mapIntent);
                } catch (Exception e) {
                    try {
                        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(address)));
                        fragment.startActivity(webIntent);
                    } catch (Exception ex) {
                        Toast.makeText(fragment.requireContext(), "Không thể mở bản đồ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        if (binding.appBarLayout.toolbar != null) {
            binding.appBarLayout.toolbar.setTitle("Chi tiết đơn hàng");
            binding.appBarLayout.toolbar.setNavigationOnClickListener(
                    v -> Navigation.findNavController(v).navigateUp());
            binding.appBarLayout.toolbar.inflateMenu(R.menu.menu_order_detail);
            binding.appBarLayout.toolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_refresh) {
                    String orderId = fragment.getCurrentOrderId();
                    if (orderId != null) {
                        viewModel.loadOrderDetails(orderId);
                        Toast.makeText(fragment.requireContext(), "Đang làm mới chi tiết đơn hàng...", Toast.LENGTH_SHORT)
                                .show();
                    }
                    return true;
                }
                return false;
            });
        }

        customerBinding.btnChatCustomer.setOnClickListener(v -> {
            WorkerOrder order = fragment.getCurrentOrder();
            if (order != null) {
                android.os.Bundle args = new android.os.Bundle();
                args.putString("workerId", order.getCustomerId());
                args.putString("workerName", order.getCustomerName());
                Navigation.findNavController(v)
                        .navigate(R.id.nav_worker_chat_detail, args);
            } else {
                Toast.makeText(fragment.requireContext(), "Không thể tải thông tin khách hàng", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnCancelOrderDetail.setOnClickListener(v ->
                Toast.makeText(fragment.requireContext(), "Hủy đơn hàng", Toast.LENGTH_SHORT).show()
        );

        binding.btnCompleteOrderDetail.setOnClickListener(v -> {
            WorkerOrder order = fragment.getCurrentOrder();
            if (order != null) {
                String nextAction = binding.btnCompleteOrderDetail.getText().toString();
                new AlertDialog.Builder(fragment.requireContext())
                        .setTitle("Xác nhận trạng thái")
                        .setMessage("Bạn có chắc chắn muốn thực hiện hành động \"" + nextAction + "\"?")
                        .setPositiveButton("Đồng ý", (dialog, which) -> {
                            viewModel.advanceStatus(order.getOrderId());
                            Toast.makeText(fragment.requireContext(), "Đang cập nhật trạng thái...", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Hủy bỏ", null)
                        .show();
            }
        });

        pricingBinding.btnAddExtraFee.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(R.id.workerExtraCostFragment));

        paymentBinding.btnShowQr.setOnClickListener(v -> fragment.showPaymentQrCode());
        paymentBinding.btnConfirmCash.setOnClickListener(v -> fragment.confirmCashPayment());

        binding.btnBottomShowQr.setOnClickListener(v -> {
            fragment.showPaymentQrCode();
            binding.scrollOrderDetail.post(() ->
                    binding.scrollOrderDetail.smoothScrollTo(0, binding.sectionPayment.getRoot().getTop())
            );
        });

        binding.btnBottomConfirmCash.setOnClickListener(v -> fragment.confirmCashPayment());
    }
}
