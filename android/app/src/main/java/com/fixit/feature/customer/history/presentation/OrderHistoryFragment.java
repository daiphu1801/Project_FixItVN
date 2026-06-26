package com.fixit.feature.customer.history.presentation;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerOrderHistoryBinding;
import com.fixit.feature.customer.booking.domain.model.CustomerBooking;
import com.fixit.feature.customer.order.presentation.CustomerOrderViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * FILE ĐIỀU KHIỂN MÀN HÌNH LỊCH SỬ ĐƠN HÀNG
 * Mục đích: Hiển thị danh sách các đơn hàng của khách hàng, cho phép tìm kiếm và lọc trạng thái.
 */
@AndroidEntryPoint
public class OrderHistoryFragment extends BaseFragment<FragmentCustomerOrderHistoryBinding> {

    private OrderHistoryAdapter adapter;
    private CustomerOrderViewModel orderViewModel;
    private final List<CustomerBooking> allBookings = new ArrayList<>();
    private String currentFilterStatus = "Tất cả";
    private String currentSearchQuery = "";

    @NonNull
    @Override
    protected FragmentCustomerOrderHistoryBinding inflateViewBinding(@NonNull LayoutInflater inflater,
            ViewGroup container) {
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

        // Thiết lập RecyclerView và Adapter
        adapter = new OrderHistoryAdapter();
        binding.rvOrders.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(requireContext()));
        binding.rvOrders.setAdapter(adapter);

        // Thiết lập Tìm kiếm
        binding.etSearchOrder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString();
                applyFilterAndSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        orderViewModel = new ViewModelProvider(requireActivity()).get(CustomerOrderViewModel.class);
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
        currentFilterStatus = status;
        applyFilterAndSearch();
    }

    private void applyFilterAndSearch() {
        List<CustomerBooking> filteredList = new ArrayList<>();
        for (CustomerBooking booking : allBookings) {
            boolean matchesStatus = false;
            String status = booking.getStatus();

            if ("Tất cả".equals(currentFilterStatus)) {
                matchesStatus = true;
            } else if ("Đang đến".equals(currentFilterStatus)) {
                // Lọc các trạng thái đang thực hiện
                matchesStatus = "PENDING".equalsIgnoreCase(status)
                        || "ACCEPTED".equalsIgnoreCase(status)
                        || "ASSIGNED".equalsIgnoreCase(status)
                        || "SURVEYING".equalsIgnoreCase(status)
                        || "WAITING_APPROVAL".equalsIgnoreCase(status)
                        || "IN_PROGRESS".equalsIgnoreCase(status);
            } else if ("Hoàn thành".equals(currentFilterStatus)) {
                matchesStatus = "COMPLETED".equalsIgnoreCase(status);
            } else if ("Đã hủy".equals(currentFilterStatus)) {
                matchesStatus = "CANCELLED".equalsIgnoreCase(status);
            }

            boolean matchesSearch = true;
            if (currentSearchQuery != null && !currentSearchQuery.trim().isEmpty()) {
                String query = currentSearchQuery.toLowerCase(Locale.getDefault());
                String id = booking.getBookingId() != null ? booking.getBookingId().toLowerCase(Locale.getDefault()) : "";
                String service = booking.getServiceName() != null ? booking.getServiceName().toLowerCase(Locale.getDefault()) : "";
                String worker = booking.getWorker() != null && booking.getWorker().getFullName() != null
                        ? booking.getWorker().getFullName().toLowerCase(Locale.getDefault())
                        : "";
                matchesSearch = id.contains(query) || service.contains(query) || worker.contains(query);
            }

            if (matchesStatus && matchesSearch) {
                filteredList.add(booking);
            }
        }
        adapter.submitList(filteredList);
    }

    @Override
    protected void observeData() {
        orderViewModel.bookingHistory.observe(getViewLifecycleOwner(), list -> {
            allBookings.clear();
            if (list != null) {
                allBookings.addAll(list);
            }
            applyFilterAndSearch();
        });

        orderViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && binding.layoutLoading != null) {
                binding.layoutLoading.getRoot()
                        .setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
            }
        });
    }
}
