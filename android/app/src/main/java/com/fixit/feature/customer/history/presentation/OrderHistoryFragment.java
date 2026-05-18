package com.fixit.feature.customer.history.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentOrderHistoryBinding;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * FILE ĐIỀU KHIỂN MÀN HÌNH LỊCH SỬ ĐƠN HÀNG
 * Mục đích: Hiển thị danh sách các đơn hàng của khách hàng.
 */
@AndroidEntryPoint
public class OrderHistoryFragment extends BaseFragment<FragmentOrderHistoryBinding> {

    @NonNull
    @Override
    protected FragmentOrderHistoryBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        // Kết nối file giao diện fragment_order_history.xml với code Java này
        return FragmentOrderHistoryBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Cài đặt nút quay lại (Back)
        binding.ivBack.setOnClickListener(v -> {
            if (navController != null) {
                navController.popBackStack();
            }
        });
        
        // Gợi ý: Sau này bạn sẽ cài đặt danh sách đơn hàng (RecyclerView) tại đây
    }

    @Override
    protected void observeData() {
        // Gợi ý: Sau này bạn sẽ lấy dữ liệu đơn hàng từ ViewModel tại đây
    }
}
