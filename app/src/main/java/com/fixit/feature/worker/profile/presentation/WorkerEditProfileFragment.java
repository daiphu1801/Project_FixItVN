package com.fixit.feature.worker.profile.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentWorkerEditProfileBinding;

public class WorkerEditProfileFragment extends BaseFragment<FragmentWorkerEditProfileBinding> {

    @Override
    protected FragmentWorkerEditProfileBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerEditProfileBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Cài đặt toolbar
        binding.appBarLayout.toolbar.setTitle("Chỉnh sửa hồ sơ");
        binding.appBarLayout.toolbar.setNavigationOnClickListener(v -> 
                Navigation.findNavController(v).navigateUp());

        // Mặc định trường CCCD đã được cấu hình trong XML với endIconMode="password_toggle" 
        // và inputType="textPassword", nên hệ thống sẽ tự động hiển thị dấu sao và 
        // có icon mắt để toggle.

        // Xử lý nút lưu
        binding.btnSaveProfile.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigateUp();
        });
    }

    @Override
    protected void observeData() {
        // Tải dữ liệu thật từ ViewModel nếu có
    }
}
