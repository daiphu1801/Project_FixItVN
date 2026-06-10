package com.fixit.feature.worker.orders.presentation.invoice;

import com.fixit.feature.worker.orders.presentation.WorkerOrdersViewModel;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.feature.worker.orders.domain.model.ExtraCostItem;
import com.fixit.feature.worker.orders.domain.model.WorkerOrder;
import com.fixit.databinding.FragmentWorkerInvoiceBinding;
import com.fixit.databinding.ItemInvoiceRowBinding;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerInvoiceFragment extends BaseFragment<FragmentWorkerInvoiceBinding> {

    private WorkerOrdersViewModel viewModel;
    private String orderId = "ORD001"; // Default

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new androidx.lifecycle.ViewModelProvider(requireActivity()).get(WorkerOrdersViewModel.class);
        if (getArguments() != null) {
            orderId = getArguments().getString("orderId", "ORD001");
        }
    }

    @Override
    protected FragmentWorkerInvoiceBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerInvoiceBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        setupHeader();
        setupListeners();
    }

    @Override
    protected void observeData() {
        viewModel.orderDetails.observe(getViewLifecycleOwner(), order -> {
            if (order != null) {
                binding.tvOrderSummary.setText("Đơn hàng #" + orderId + " - Khách: " + order.getCustomerName());
                setupInvoiceItems(order);
            }
        });

        viewModel.loadOrderDetails(orderId);
    }

    private void setupHeader() {
        binding.llWorkerTopbar.tvToolbarTitle.setText("Hóa đơn thanh toán");
        binding.llWorkerTopbar.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void setupInvoiceItems(WorkerOrder order) {
        binding.llInvoiceItems.removeAllViews();
        
        long basePrice = 0;
        if (order != null) {
            // Parse base price
            try {
                basePrice = Long.parseLong(order.getPrice().replaceAll("[^\\d]", ""));
                addInvoiceRow(order.getServiceTitle(), 1, basePrice);
            } catch (Exception ignored) {}
        }

        List<ExtraCostItem> extras = viewModel.extraItems.getValue();
        long extraTotal = 0;
        if (extras != null) {
            for (ExtraCostItem item : extras) {
                addInvoiceRow(item.name, item.quantity, item.unitPrice);
                extraTotal += item.getTotal();
            }
        }

        long grandTotal = basePrice + extraTotal;
        binding.tvInvoiceGrandTotal.setText(String.format("%,d đ", grandTotal));
        
        // Prepare QR for later if needed
        loadQrCode(grandTotal);
    }

    private void addInvoiceRow(String name, int qty, long price) {
        ItemInvoiceRowBinding itemBinding = ItemInvoiceRowBinding.inflate(getLayoutInflater(), binding.llInvoiceItems, false);
        itemBinding.tvInvoiceItemName.setText(name);
        itemBinding.tvInvoiceItemQty.setText("x" + qty);
        itemBinding.tvInvoiceItemPrice.setText(String.format("%,d đ", (long) qty * price));
        binding.llInvoiceItems.addView(itemBinding.getRoot());
    }

    private void setupListeners() {
        binding.rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbTransfer) {
                binding.cvVietQR.setVisibility(View.VISIBLE);
                binding.tvPaymentHint.setText("Khách chuyển khoản cho Admin. Bạn sẽ nhận được 85% giá trị đơn hàng vào Ví khả dụng sau khi trừ phí.");
                binding.tvPaymentHint.setBackgroundColor(android.graphics.Color.parseColor("#e0f2fe"));
                binding.tvPaymentHint.setTextColor(android.graphics.Color.parseColor("#0369a1"));
            } else {
                binding.cvVietQR.setVisibility(View.GONE);
                binding.tvPaymentHint.setText("Lưu ý: Bạn sẽ thu đủ tiền mặt từ khách. Hệ thống sẽ ghi nhận 15% hoa hồng vào Ví nợ của bạn.");
                binding.tvPaymentHint.setBackgroundColor(android.graphics.Color.parseColor("#fffbeb"));
                binding.tvPaymentHint.setTextColor(android.graphics.Color.parseColor("#92400e"));
            }
        });

        binding.btnFinalizeOrder.setOnClickListener(v -> {
            String method = binding.rbCash.isChecked() ? "Tiền mặt" : "Chuyển khoản";
            Toast.makeText(requireContext(), "Đã xác nhận thanh toán " + method, Toast.LENGTH_SHORT).show();
            
            // Di chuyển sang màn hình khảo sát sau việc
            androidx.navigation.Navigation.findNavController(v)
                    .navigate(R.id.workerQuestionnaireFragment);
        });
    }

    private void loadQrCode(long amount) {
        String qrUrl = viewModel.generateVietQrUrl(orderId, amount);
        if (qrUrl.isEmpty()) return;

        binding.pbQrLoading.setVisibility(View.VISIBLE);
        Glide.with(this)
            .load(qrUrl)
            .listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    binding.pbQrLoading.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    binding.pbQrLoading.setVisibility(View.GONE);
                    return false;
                }
            })
            .into(binding.ivQrCode);
    }
}
