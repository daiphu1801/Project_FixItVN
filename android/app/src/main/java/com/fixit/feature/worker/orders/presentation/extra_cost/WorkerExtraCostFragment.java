package com.fixit.feature.worker.orders.presentation.extra_cost;

import com.fixit.feature.worker.orders.presentation.WorkerOrdersViewModel;
import com.fixit.feature.worker.orders.domain.model.ExtraCostItem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentWorkerExtraCostBinding;
import com.fixit.databinding.ItemExtraCostBinding;

import dagger.hilt.android.AndroidEntryPoint;
import javax.inject.Inject;
import com.fixit.feature.worker.orders.domain.usecase.SubmitQuotationUseCase;

@AndroidEntryPoint
public class WorkerExtraCostFragment extends BaseFragment<FragmentWorkerExtraCostBinding> {

    @Inject
    SubmitQuotationUseCase submitQuotationUseCase;

    private WorkerOrdersViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new androidx.lifecycle.ViewModelProvider(requireActivity()).get(WorkerOrdersViewModel.class);
    }

    @Override
    protected FragmentWorkerExtraCostBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerExtraCostBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        setupHeader();
        setupListeners();
        
        // Add first item by default
        if (binding.llItemsContainer.getChildCount() == 0) {
            addItemRow();
        }
    }

    @Override
    protected void observeData() {
    }

    private void setupHeader() {
        binding.llWorkerTopbar.tvToolbarTitle.setText("Thêm chi phí");
        binding.llWorkerTopbar.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void setupListeners() {
        binding.btnAddItem.setOnClickListener(v -> addItemRow());

        binding.btnSubmitExtra.setOnClickListener(v -> {
            java.util.List<ExtraCostItem> items = new java.util.ArrayList<>();
            java.math.BigDecimal materialCost = java.math.BigDecimal.ZERO;
            for (int i = 0; i < binding.llItemsContainer.getChildCount(); i++) {
                View view = binding.llItemsContainer.getChildAt(i);
                ItemExtraCostBinding itemBinding = ItemExtraCostBinding.bind(view);
                
                String name = itemBinding.etItemName.getText().toString().trim();
                String qtyStr = itemBinding.etQuantity.getText().toString().trim();
                String priceStr = itemBinding.etPrice.getText().toString().trim();

                if (!name.isEmpty() && !qtyStr.isEmpty() && !priceStr.isEmpty()) {
                    try {
                        int qty = Integer.parseInt(qtyStr);
                        long price = Long.parseLong(priceStr.replaceAll("[^\\d]", ""));
                        items.add(new ExtraCostItem(name, qty, price));
                        materialCost = materialCost.add(java.math.BigDecimal.valueOf((long) qty * price));
                    } catch (Exception ignored) {}
                }
            }

            viewModel.setExtraItems(items);

            com.fixit.feature.worker.orders.domain.model.WorkerOrder order = viewModel.orderDetails.getValue();
            if (order == null || order.getOrderId() == null) {
                Toast.makeText(requireContext(), "Lỗi: Không tìm thấy thông tin đơn hàng để gửi báo giá", Toast.LENGTH_SHORT).show();
                return;
            }

            String bookingId = order.getOrderId();
            java.math.BigDecimal laborCost = parsePrice(order.getPrice());

            binding.btnSubmitExtra.setEnabled(false);
            binding.btnSubmitExtra.setText("Đang gửi báo giá...");

            submitQuotationUseCase.execute(bookingId, laborCost, materialCost, result -> {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    if (result.isSuccess()) {
                        Toast.makeText(requireContext(), "Đã gửi báo giá chi tiết cho khách hàng!", Toast.LENGTH_SHORT).show();
                        viewModel.loadOrderDetails(bookingId, false);
                        Navigation.findNavController(v).navigateUp();
                    } else {
                        binding.btnSubmitExtra.setEnabled(true);
                        binding.btnSubmitExtra.setText("Gửi báo giá cho khách");
                        String error = result.getError() != null ? result.getError().getMessage() : "Lỗi không xác định";
                        Toast.makeText(requireContext(), "Lỗi gửi báo giá: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }

    private java.math.BigDecimal parsePrice(String priceStr) {
        if (priceStr == null || priceStr.isEmpty()) {
            return java.math.BigDecimal.ZERO;
        }
        try {
            String clean = priceStr.replaceAll("[^\\d]", "");
            if (clean.isEmpty()) return java.math.BigDecimal.ZERO;
            return new java.math.BigDecimal(clean);
        } catch (Exception e) {
            return java.math.BigDecimal.ZERO;
        }
    }

    private void addItemRow() {
        ItemExtraCostBinding itemBinding = ItemExtraCostBinding.inflate(getLayoutInflater(), binding.llItemsContainer, false);
        
        itemBinding.btnRemoveItem.setOnClickListener(v -> {
            if (binding.llItemsContainer.getChildCount() > 1) {
                binding.llItemsContainer.removeView(itemBinding.getRoot());
                updateTotal();
            }
        });

        android.text.TextWatcher watcher = new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTotal();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        };

        itemBinding.etQuantity.addTextChangedListener(watcher);
        itemBinding.etPrice.addTextChangedListener(watcher);

        binding.llItemsContainer.addView(itemBinding.getRoot());
    }

    private void updateTotal() {
        long total = 0;
        for (int i = 0; i < binding.llItemsContainer.getChildCount(); i++) {
            View view = binding.llItemsContainer.getChildAt(i);
            ItemExtraCostBinding itemBinding = ItemExtraCostBinding.bind(view);
            
            try {
                int qty = Integer.parseInt(itemBinding.etQuantity.getText().toString());
                long price = Long.parseLong(itemBinding.etPrice.getText().toString().replaceAll("[^\\d]", ""));
                total += (long) qty * price;
            } catch (Exception ignored) {}
        }
        
        binding.tvTotalExtra.setText(String.format("%,d đ", total));
    }
}
