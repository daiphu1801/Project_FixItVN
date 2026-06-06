package com.fixit.feature.customer.profile.presentation;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentProfileCustomerBinding;
import com.fixit.feature.auth.presentation.AuthActivity;
import com.fixit.feature.upload.domain.model.UploadPurpose;
import com.fixit.feature.upload.domain.model.UploadTargetType;
import com.fixit.feature.upload.presentation.UploadViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileCustomerFragment extends BaseFragment<FragmentProfileCustomerBinding> {

    private UploadViewModel uploadViewModel;

    // Image picker cho avatar khách hàng
    private final ActivityResultLauncher<String> pickAvatarLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    // Hiển thị ảnh mới ngay lập tức trên CircleImageView
                    Glide.with(this).load(uri).circleCrop().into(binding.ivAvatar);
                    // Upload lên server
                    uploadViewModel.upload(
                            requireContext(),
                            uri,
                            UploadPurpose.AVATAR,
                            UploadTargetType.USER_AVATAR,
                            null,
                            null,
                            "avatar",
                            null
                    );
                }
            }
    );

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

        // Click vào avatar (kèm icon camera) → mở gallery chọn ảnh đại diện
        binding.avatarContainer.setOnClickListener(v -> pickAvatarLauncher.launch("image/*"));

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
        uploadViewModel = new ViewModelProvider(this).get(UploadViewModel.class);

        // Observe kết quả upload avatar
        uploadViewModel.uploadResult.observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.isSuccess()) {
                Toast.makeText(requireContext(), "Ảnh đại diện đã cập nhật", Toast.LENGTH_SHORT).show();
                // TODO: Gọi API cập nhật profile với URL mới
                // result.getConfirmedUpload().getFileUrl()
            } else {
                Toast.makeText(requireContext(), result.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
