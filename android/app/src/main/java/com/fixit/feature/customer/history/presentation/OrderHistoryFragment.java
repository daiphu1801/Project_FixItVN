package com.fixit.feature.customer.history.presentation;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerOrderHistoryBinding;
import com.fixit.databinding.ItemOrderHistoryBinding;

import dagger.hilt.android.AndroidEntryPoint;
import java.util.ArrayList;
import java.util.List;
import com.fixit.feature.customer.order.presentation.CustomerOrderViewModel;

/**
 * FILE ĐIỀU KHIỂN MÀN HÌNH LỊCH SỬ ĐƠN HÀNG
 * Mục đích: Hiển thị danh sách các đơn hàng của khách hàng.
 */
@AndroidEntryPoint
public class OrderHistoryFragment extends BaseFragment<FragmentCustomerOrderHistoryBinding> {

    private OrderHistoryAdapter adapter;
    private CustomerOrderViewModel orderViewModel;

    @NonNull
    @Override
    protected FragmentCustomerOrderHistoryBinding inflateViewBinding(@NonNull LayoutInflater inflater,
            ViewGroup container) {
        // Kết nối file giao diện fragment_customer_order_history.xml với code Java này
        return FragmentCustomerOrderHistoryBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Cài đặt nút quay lại (Back)
        binding.ivBack.setOnClickListener(v -> {
            if (navController != null) {
                navController.popBackStack();
            }
        });

        setupFilterChips();

        adapter = new OrderHistoryAdapter();
        binding.rvOrders.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(requireContext()));
        binding.rvOrders.setAdapter(adapter);

        orderViewModel = new androidx.lifecycle.ViewModelProvider(requireActivity()).get(CustomerOrderViewModel.class);
        orderViewModel.fetchBookings();
    }

    private void setupFilterChips() {
        List<TextView> chips = new ArrayList<>();
        chips.add(binding.chipAll);
        chips.add(binding.chipComing);
        chips.add(binding.chipDone);
        chips.add(binding.chipCanceled);

        for (TextView chip : chips) {
            chip.setOnClickListener(v -> {
                updateChipsState(chips, chip);
                filterData(chip.getText().toString());
            });
        }
    }

    private void updateChipsState(List<TextView> allChips, TextView selectedChip) {
        for (TextView chip : allChips) {
            if (chip == selectedChip) {
                chip.setBackgroundResource(R.drawable.bg_chip_active);
                chip.setTextColor(Color.WHITE);
            } else {
                chip.setBackgroundResource(R.drawable.bg_chip_inactive);
                chip.setTextColor(Color.parseColor("#64748B"));
            }
        }
    }

    private void filterData(String status) {
        // Có thể filter list ở đây
    }

    @Override
    protected void observeData() {
        orderViewModel.bookingHistory.observe(getViewLifecycleOwner(), list -> {
            adapter.submitList(list);
        });

        orderViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && binding.layoutLoading != null) {
                binding.layoutLoading.getRoot()
                        .setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
            }
        });
    }
}
