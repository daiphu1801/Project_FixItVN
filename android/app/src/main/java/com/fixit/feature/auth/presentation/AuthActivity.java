package com.fixit.feature.auth.presentation;

import android.content.Intent;
import android.view.LayoutInflater;

import androidx.lifecycle.ViewModelProvider;

import com.fixit.core.ui.BaseActivity;
import com.fixit.databinding.ActivityAuthBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AuthActivity extends BaseActivity<ActivityAuthBinding> {

    private AuthViewModel viewModel;

    @Override
    protected ActivityAuthBinding inflateViewBinding(LayoutInflater inflater) {
        return ActivityAuthBinding.inflate(inflater);
    }

    @Override
    protected void setupViews() {
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        // Kiểm tra ngay khi khởi động: nếu có session đã lưu thì bỏ qua màn login
        viewModel.checkExistingSession();
    }

    @Override
    protected void observeData() {
        viewModel.event.observe(this, event -> {
            android.util.Log.d("FixIt_AuthActivity", "observeData: event = " + (event != null ? event.getType() : "null"));
            if (event == null) return;

            // Chỉ xử lý auto-navigate (REGISTER_SUCCESS không cần redirect ở đây)
            if (event.getType() == AuthEvent.Type.NAVIGATE_TO_WORKER) {
                android.util.Log.d("FixIt_AuthActivity", "observeData: Navigate to WorkerActivity");
                navigateTo(com.fixit.feature.worker.presentation.WorkerActivity.class);
            } else if (event.getType() == AuthEvent.Type.NAVIGATE_TO_CUSTOMER) {
                android.util.Log.d("FixIt_AuthActivity", "observeData: Navigate to CustomerActivity");
                navigateTo(com.fixit.feature.customer.presentation.CustomerActivity.class);
            }
        });
    }

    private void navigateTo(Class<?> destination) {
        Intent intent = new Intent(this, destination);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

