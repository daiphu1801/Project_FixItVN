package com.fixit.feature.customer.history.presentation;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerOrderHistoryBinding;
import dagger.hilt.android.AndroidEntryPoint;
import java.util.ArrayList;
import java.util.List;

/**
 * FILE ĐIỀU KHIỂN MÀN HÌNH LỊCH SỬ ĐƠN HÀNG
 * Mục đích: Hiển thị danh sách các đơn hàng của khách hàng.
 */
@AndroidEntryPoint
public class OrderHistoryFragment extends BaseFragment<FragmentCustomerOrderHistoryBinding> {

    @NonNull
    @Override
    protected FragmentCustomerOrderHistoryBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
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
                // Sau này sẽ thêm logic gọi API hoặc lọc dữ liệu tại đây
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
        // Mock logic lọc dữ liệu
    }

    @Override
    protected void observeData() {
        // Gợi ý: Sau này bạn sẽ lấy dữ liệu đơn hàng từ ViewModel tại đây
    }
}
