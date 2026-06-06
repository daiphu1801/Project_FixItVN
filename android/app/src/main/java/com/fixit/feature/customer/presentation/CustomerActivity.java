package com.fixit.feature.customer.presentation;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.fixit.R;
import com.fixit.core.ui.BaseActivity;
import com.fixit.databinding.ActivityCustomerBinding;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * CẬP NHẬT: KẾT NỐI THANH ĐIỀU HƯỚNG 5 MỤC (TRANG CHỦ, ĐƠN HÀNG, LỊCH SỬ, THỢ QUEN, CÁ NHÂN)
 */
@AndroidEntryPoint
public class CustomerActivity extends BaseActivity<ActivityCustomerBinding> {

    private NavController navController;

    @Override
    protected ActivityCustomerBinding inflateViewBinding(android.view.LayoutInflater inflater) {
        return ActivityCustomerBinding.inflate(inflater);
    }

    @Override
    protected void setupViews() {
        setupNavigation();
    }

    private void setupNavigation() {
        // 1. Tìm NavHostFragment (Cái khung dùng để chứa và hiển thị các màn hình Fragment)
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navHostFragmentCustomer);
        
        if (navHostFragment != null) {
            // 2. Lấy bộ điều hướng (navController) - đây là 'người lái xe' cho ứng dụng
            navController = navHostFragment.getNavController();
            
            // 3. TỰ ĐỘNG KẾT NỐI: Lệnh này giúp thanh Menu bên dưới tự động nhận diện 5 nút bấm
            // Nó sẽ so khớp ID của nút bấm trong file Menu và ID của màn hình trong file Navigation.
            NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

            // Xử lý khi người dùng nhấn lại vào các mục trên Bottom Navigation
            binding.bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                
                // Nếu đang ở màn hình khác và ấn vào "Trang chủ", quay về màn hình Home
                if (itemId == R.id.nav_customer_home) {
                    boolean popped = navController.popBackStack(R.id.nav_customer_home, false);
                    if (!popped) {
                        navController.navigate(R.id.nav_customer_home);
                    }
                    return true;
                }
                
                // Các mục khác sử dụng mặc định của NavigationUI
                return NavigationUI.onNavDestinationSelected(item, navController);
            });
            
            // Chú thích: Tôi đã loại bỏ phần code liên quan đến nút tròn FAB và Placeholder 
            // để phù hợp với giao diện 5 mục dàn hàng ngang như ảnh bạn vừa gửi.
        }
    }

    @Override
    protected void observeData() {
        // Quan sát dữ liệu khách hàng (nếu có)
    }
}
