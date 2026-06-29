package com.fixit.feature.customer.order.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerOrderContainerBinding;
import com.fixit.feature.customer.booking.presentation.CustomerFindingWorkerFragment;
import com.fixit.feature.customer.order.presentation.CustomerOrderDetailFragment;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * FILE ĐIỀU PHỐI ĐƠN HÀNG (ORDER CONTAINER)
 * Mục đích: Tự động quyết định hiển thị giao diện nào khi nhấn vào tab "Đơn hàng"
 */
@AndroidEntryPoint
public class CustomerOrderContainerFragment extends BaseFragment<FragmentCustomerOrderContainerBinding> {

    private CustomerOrderViewModel viewModel;

    @NonNull
    @Override
    protected FragmentCustomerOrderContainerBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentCustomerOrderContainerBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        viewModel = new ViewModelProvider(requireActivity()).get(CustomerOrderViewModel.class);

        // Nút đặt thợ ngay khi trang trống
        binding.btnOrderNow.setOnClickListener(v -> {
            // Chuyển sang trang Tìm kiếm để người dùng tìm dịch vụ
            if (navController != null) {
                navController.navigate(R.id.nav_customer_search);
            }
        });

        // Chỉ kiểm tra active booking từ server khi trạng thái hiện tại là trống (0) hoặc chưa khởi tạo (null)
        if (viewModel.orderStatus.getValue() == null || viewModel.orderStatus.getValue() == 0) {
            viewModel.checkActiveBooking();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Chỉ gọi checkActiveBooking khi không có đơn hàng đang hoạt động trong ViewModel
        if (viewModel != null && (viewModel.orderStatus.getValue() == null || viewModel.orderStatus.getValue() == 0)) {
            viewModel.checkActiveBooking();
        }
    }

    private void checkOrderStatusAndDisplay(int status) {
        if (status == 0) {
            // TRƯỜNG HỢP 1: KHÔNG CÓ ĐƠN HÀNG -> Hiện thông báo trống
            binding.layoutEmptyOrder.setVisibility(View.VISIBLE);
            binding.orderContainer.setVisibility(View.GONE);
        } else if (status == 1) {
            // TRƯỜNG HỢP 2: ĐANG TÌM THỢ -> Hiện Radar
            showSubFragment(new CustomerFindingWorkerFragment());
        } else if (status == 2) {
            // TRƯỜNG HỢP 3: ĐÃ CÓ THỢ -> Hiện Chi tiết đơn
            showSubFragment(new CustomerOrderDetailFragment());
        }
    }

    private void showSubFragment(Fragment fragment) {
        binding.layoutEmptyOrder.setVisibility(View.GONE);
        binding.orderContainer.setVisibility(View.VISIBLE);
        
        getChildFragmentManager().beginTransaction()
                .replace(R.id.orderContainer, fragment)
                .commit();
    }

    @Override
    protected void observeData() {
        if (viewModel != null) {
            viewModel.orderStatus.observe(getViewLifecycleOwner(), this::checkOrderStatusAndDisplay);
        }
    }
}
