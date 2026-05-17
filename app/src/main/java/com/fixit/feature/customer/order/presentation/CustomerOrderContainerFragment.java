package com.fixit.feature.customer.order.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerOrderContainerBinding;
import com.fixit.feature.customer.booking.presentation.CustomerFindingWorkerFragment;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * FILE ĐIỀU PHỐI ĐƠN HÀNG (ORDER CONTAINER)
 * Mục đích: Tự động quyết định hiển thị giao diện nào khi nhấn vào tab "Đơn hàng"
 */
@AndroidEntryPoint
public class CustomerOrderContainerFragment extends BaseFragment<FragmentCustomerOrderContainerBinding> {

    // Giả lập trạng thái đơn hàng (Bạn sẽ thay thế bằng logic thực tế sau này)
    // 0: Trống, 1: Đang tìm thợ (Radar), 2: Đã có thợ (Chi tiết)
    private int orderStatus = 2;

    @NonNull
    @Override
    protected FragmentCustomerOrderContainerBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentCustomerOrderContainerBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Tạm thời tôi tạo một hàm giả lập để bạn dễ hình dung
        checkOrderStatusAndDisplay();

        // Nút đặt thợ ngay khi trang trống
        binding.btnOrderNow.setOnClickListener(v -> {
            // Chuyển sang trang chủ để người dùng đặt thợ
            if (navController != null) {
                navController.navigate(R.id.nav_customer_home);
            }
        });
    }

    private void checkOrderStatusAndDisplay() {
        if (orderStatus == 0) {
            // TRƯỜNG HỢP 1: KHÔNG CÓ ĐƠN HÀNG
            binding.layoutEmptyOrder.setVisibility(View.VISIBLE);
            binding.orderContainer.setVisibility(View.GONE);
        } else if (orderStatus == 1) {
            // TRƯỜNG HỢP 2: ĐANG TÌM THỢ (Hiện màn hình Radar)
            showSubFragment(new CustomerFindingWorkerFragment());
        } else if (orderStatus == 2) {
            // TRƯỜNG HỢP 3: ĐÃ CÓ THỢ (Hiện màn hình Chi tiết)
            showSubFragment(new CustomerOrderDetailFragment());
        }
    }

    // Hàm dùng để lồng các Fragment con vào trong Container
    private void showSubFragment(Fragment fragment) {
        binding.layoutEmptyOrder.setVisibility(View.GONE);
        binding.orderContainer.setVisibility(View.VISIBLE);
        
        getChildFragmentManager().beginTransaction()
                .replace(R.id.orderContainer, fragment)
                .commit();
    }

    @Override
    protected void observeData() {
        // Sau này bạn sẽ lắng nghe dữ liệu từ Server tại đây để tự động cập nhật orderStatus
    }
}
