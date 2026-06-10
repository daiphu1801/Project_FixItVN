package com.fixit.feature.worker.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.fixit.core.ui.BaseActivity;
import com.fixit.databinding.ActivityWorkerBinding;

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

@AndroidEntryPoint
public class WorkerActivity extends BaseActivity<ActivityWorkerBinding> {

    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Inject
    RegisterDeviceTokenUseCase registerDeviceTokenUseCase;

    @Override
    protected ActivityWorkerBinding inflateViewBinding(LayoutInflater inflater) {
        return ActivityWorkerBinding.inflate(inflater);
    }

    @Override
    protected void setupViews() {
        checkNotificationPermission();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(binding.navHostFragment.getId());

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            // Wire bottom nav (5 items: Trang chủ, Đơn hàng, Tìm việc, Chat, Tài khoản)
            NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

            // Xử lý khi nhấn vào các mục trên Bottom Navigation để đồng bộ trải nghiệm
            binding.bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == com.fixit.R.id.workerHomeFragment) {
                    boolean popped = navController.popBackStack(com.fixit.R.id.workerHomeFragment, false);
                    if (!popped) {
                        navController.navigate(com.fixit.R.id.workerHomeFragment);
                    }
                    return true;
                }
                return NavigationUI.onNavDestinationSelected(item, navController);
            });

            // FAB ở giữa → simulate click item "Tìm việc" trong bottom nav
            // Để NavigationUI tự xử lý popUpTo đúng cách (tránh conflict back stack)
            binding.fabWorkerOnline.setOnClickListener(v ->
                    binding.bottomNavigationView.setSelectedItemId(com.fixit.R.id.workerJobFragment));

            // Đổi màu FAB khi item Tìm Việc được chọn
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == com.fixit.R.id.workerJobFragment) {
                    binding.fabWorkerOnline.setBackgroundTintList(
                            android.content.res.ColorStateList.valueOf(
                                    android.graphics.Color.parseColor("#22c55e")));
                } else {
                    binding.fabWorkerOnline.setBackgroundTintList(
                            android.content.res.ColorStateList.valueOf(
                                    android.graphics.Color.parseColor("#42c2ff")));
                }
            });
        }
    }

    @Override
    protected void observeData() {
        // TODO: Observe role-based navigation logic here in the future
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
                    Log.w("WorkerActivity", "Fetching FCM registration token failed", task.getException());
                    return;
                }

                String token = task.getResult();
                Log.d("WorkerActivity", "FCM Token: " + token);

                registerDeviceTokenUseCase.execute(token, "Android", result -> {
                    if (result.isSuccess()) {
                        Log.d("WorkerActivity", "Register FCM token success");
                    } else {
                        Log.e("WorkerActivity", "Register FCM token error: " + result.getError().getMessage());
                    }
                });
            });
        } catch (Exception e) {
            Log.e("WorkerActivity", "Error initializing Firebase Messaging: " + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchAndRegisterFcmToken();
            } else {
                Log.w("WorkerActivity", "POST_NOTIFICATIONS permission denied by user");
            }
        }
    }
}
