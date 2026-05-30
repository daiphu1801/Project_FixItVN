package com.fixit.feature.customer.booking.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerFindingWorkerBinding;
import com.fixit.feature.customer.order.presentation.CustomerOrderViewModel;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * BƯỚC 1 (Tiếp theo): FILE ĐIỀU KHIỂN MÀN HÌNH ĐANG TÌM THỢ (RADAR)
 * Mục đích: Hiển thị hiệu ứng radar quét tìm thợ sửa chữa xung quanh.
 */
@AndroidEntryPoint
public class CustomerFindingWorkerFragment extends BaseFragment<FragmentCustomerFindingWorkerBinding> {

    @NonNull
    @Override
    protected FragmentCustomerFindingWorkerBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        // Kết nối file giao diện fragment_customer_finding_worker.xml với code Java này
        return FragmentCustomerFindingWorkerBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        CustomerOrderViewModel orderViewModel = new ViewModelProvider(requireActivity()).get(CustomerOrderViewModel.class);

        // Nơi xử lý các hiệu ứng radar hoặc nút 'Hủy yêu cầu'
        binding.btnCancelSearch.setOnClickListener(v -> {
            // Khi nhấn hủy, xóa trạng thái đơn hàng
            orderViewModel.cancelOrder();
        });

        // GIẢ LẬP: Sau 3 giây tìm thấy thợ (Dùng cho demo)
        binding.getRoot().postDelayed(() -> {
            if (orderViewModel.orderStatus.getValue() != null && orderViewModel.orderStatus.getValue() == 1) {
                orderViewModel.onWorkerAccepted();
            }
        }, 3000);
    }

    @Override
    protected void observeData() {
        // Lắng nghe tín hiệu khi có thợ nhận đơn để chuyển màn hình
    }
}
