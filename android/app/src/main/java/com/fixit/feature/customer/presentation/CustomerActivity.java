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

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.fixit.feature.notification.domain.usecase.RegisterDeviceTokenUseCase;
import com.google.firebase.messaging.FirebaseMessaging;
import javax.inject.Inject;

/**
 * CẬP NHẬT: KẾT NỐI THANH ĐIỀU HƯỚNG 5 MỤC (TRANG CHỦ, ĐƠN HÀNG, LỊCH SỬ, THỢ QUEN, CÁ NHÂN)
 */
@AndroidEntryPoint
public class CustomerActivity extends BaseActivity<ActivityCustomerBinding> {

    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Inject
    RegisterDeviceTokenUseCase registerDeviceTokenUseCase;

    private NavController navController;

    @Override
    protected ActivityCustomerBinding inflateViewBinding(android.view.LayoutInflater inflater) {
        return ActivityCustomerBinding.inflate(inflater);
    }

    @Override
    protected void setupViews() {
        setupNavigation();
        checkNotificationPermission();
    }

    private void setupNavigation() {
        // 1. Tìm NavHostFragment (Cái khung dùng để chứa và hiển thị các màn hình Fragment)
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navHostFragmentCustomer);
        
        if (navHostFragment != null) {
            // 2. Lấy bộ điều hướng (navController) - đây là 'người lái xe' cho ứng dụng
            navController = navHostFragment.getNavController();
            
            // 3. TỰ ĐỘNG KẾT NỐI: Lệnh này giúp thanh Menu bên dưới tự động nhận diện các nút bấm
            // Nó sẽ so khớp ID của nút bấm trong file Menu và ID của màn hình trong file Navigation.
            NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

            // Nút Tìm thợ (FAB) chuyển đến giao diện tìm kiếm
            binding.fabFindWorker.setOnClickListener(v -> {
                navController.navigate(R.id.nav_customer_search);
            });

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

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
            } else {
                fetchAndRegisterFcmToken();
            }
        } else {
            fetchAndRegisterFcmToken();
        }
    }

    private void fetchAndRegisterFcmToken() {
        try {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w("CustomerActivity", "Fetching FCM registration token failed", task.getException());
                    return;
                }

                String token = task.getResult();
                Log.d("CustomerActivity", "FCM Token: " + token);

                registerDeviceTokenUseCase.execute(token, "Android", result -> {
                    if (result.isSuccess()) {
                        Log.d("CustomerActivity", "Register FCM token success");
                    } else {
                        Log.e("CustomerActivity", "Register FCM token error: " + result.getError().getMessage());
                    }
                });
            });
        } catch (Exception e) {
            Log.e("CustomerActivity", "Error initializing Firebase Messaging: " + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchAndRegisterFcmToken();
            } else {
                Log.w("CustomerActivity", "POST_NOTIFICATIONS permission denied by user");
            }
        }
    }
}
