package com.fixit.feature.customer.history.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentOrderDetailFinishedBinding;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * FILE ĐIỀU KHIỂN CHI TIẾT ĐƠN HÀNG ĐÃ HOÀN THÀNH
 * Mục đích: Hiển thị thông tin chi tiết của một đơn hàng sau khi thợ đã sửa xong.
 */
@AndroidEntryPoint
public class OrderDetailFinishedFragment extends BaseFragment<FragmentOrderDetailFinishedBinding> {

    @NonNull
    @Override
    protected FragmentOrderDetailFinishedBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        // Kết nối file giao diện fragment_order_detail_finished.xml với code Java này
        return FragmentOrderDetailFinishedBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Cài đặt nút quay lại
        binding.ivBack.setOnClickListener(v -> {
            if (navController != null) {
                navController.popBackStack();
            }
        });
        
        // Nơi cài đặt các nút bấm khác như: Đánh giá thợ, Đặt lại đơn...
    }

    @Override
    protected void observeData() {
        // Lấy dữ liệu chi tiết đơn hàng từ Server để hiển thị
    }
}
