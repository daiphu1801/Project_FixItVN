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

@AndroidEntryPoint
public class WorkerExtraCostFragment extends BaseFragment<FragmentWorkerExtraCostBinding> {

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
            for (int i = 0; i < binding.llItemsContainer.getChildCount(); i++) {
                View view = binding.llItemsContainer.getChildAt(i);
                ItemExtraCostBinding itemBinding = ItemExtraCostBinding.bind(view);
                
                String name = itemBinding.etItemName.getText().toString();
                String qtyStr = itemBinding.etQuantity.getText().toString();
                String priceStr = itemBinding.etPrice.getText().toString();

                if (!name.isEmpty() && !qtyStr.isEmpty() && !priceStr.isEmpty()) {
                    items.add(new ExtraCostItem(
                        name,
                        Integer.parseInt(qtyStr),
                        Long.parseLong(priceStr.replaceAll("[^\\d]", ""))
                    ));
                }
            }

            viewModel.setExtraItems(items);
            Toast.makeText(requireContext(), "Đã cập nhật báo giá phát sinh!", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigateUp();
        });
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
