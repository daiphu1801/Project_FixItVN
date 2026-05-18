package com.fixit.feature.customer.profile.presentation;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentProfileCustomerBinding;
import com.fixit.feature.auth.presentation.AuthActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileCustomerFragment extends BaseFragment<FragmentProfileCustomerBinding> {

    @NonNull
    @Override
    protected FragmentProfileCustomerBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentProfileCustomerBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Chuyển đến màn hình thông tin tài khoản
        binding.cardProfile.setOnClickListener(v -> {
            if (navController != null) {
                navController.navigate(R.id.nav_customer_account_info);
            }
        });

        // Đăng xuất
        binding.btnLogout.setOnClickListener(v -> {
            // Thực hiện logout (xóa token, session...) tại đây nếu cần
            
            // Chuyển về màn hình đăng nhập (AuthActivity)
            Intent intent = new Intent(requireContext(), AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    @Override
    protected void observeData() {
    }
}
