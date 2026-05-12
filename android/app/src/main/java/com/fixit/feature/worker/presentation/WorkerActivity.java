package com.fixit.feature.worker.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.fixit.core.ui.BaseActivity;
import com.fixit.databinding.ActivityWorkerBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerActivity extends BaseActivity<ActivityWorkerBinding> {

    @Override
    protected ActivityWorkerBinding inflateViewBinding(LayoutInflater inflater) {
        return ActivityWorkerBinding.inflate(inflater);
    }

    @Override
    protected void setupViews() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(binding.navHostFragment.getId());

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            // Wire bottom nav (5 items: Trang chủ, Đơn hàng, Tìm việc, Chat, Tài khoản)
            NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

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
}
