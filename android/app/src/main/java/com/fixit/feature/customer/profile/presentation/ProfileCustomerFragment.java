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
import com.fixit.core.common.AutoRefreshHelper;
import com.fixit.core.storage.SessionStorage;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentProfileCustomerBinding;
import com.fixit.feature.auth.presentation.AuthActivity;
import com.fixit.feature.upload.domain.model.UploadPurpose;
import com.fixit.feature.upload.domain.model.UploadTargetType;
import com.fixit.feature.upload.presentation.UploadViewModel;

import com.fixit.feature.auth.domain.usecase.LogoutUseCase;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileCustomerFragment extends BaseFragment<FragmentProfileCustomerBinding> {

    @Inject
    LogoutUseCase logoutUseCase;

    @Inject
    SessionStorage sessionStorage;

    @Inject
    com.fixit.feature.customer.profile.domain.usecase.GetCustomerProfileUseCase getCustomerProfileUseCase;

    private UploadViewModel uploadViewModel;
    private AutoRefreshHelper autoRefreshHelper;

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
        // Chuyển đến màn hình thông tin tài khoản khi click vào card profile
        binding.cardProfile.setOnClickListener(v -> {
            if (navController != null) {
                navController.navigate(R.id.nav_customer_account_info);
            }
        });

        // Click vào mục "Thông tin cá nhân" trong danh sách menu
        binding.layoutAccountInfo.setOnClickListener(v -> {
            if (navController != null) {
                navController.navigate(R.id.nav_customer_account_info);
            }
        });

        // Click vào mục "Địa chỉ của tôi"
        binding.layoutAddress.setOnClickListener(v -> {
            // TODO: navigate tới màn hình quản lý địa chỉ khi đã có
            Toast.makeText(requireContext(), "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
        });

        // Click vào mục "Trung tâm hỗ trợ"
        binding.layoutSupportCenter.setOnClickListener(v -> {
            // TODO: navigate tới màn hình trung tâm hỗ trợ khi đã có
            Toast.makeText(requireContext(), "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
        });

        // Click vào mục "Đổi mật khẩu"
        binding.layoutChangePassword.setOnClickListener(v -> {
            // TODO: navigate tới màn hình đổi mật khẩu khi đã có
            Toast.makeText(requireContext(), "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
        });

        // Click vào avatar (kèm icon camera) → mở gallery chọn ảnh đại diện
        binding.avatarContainer.setOnClickListener(v -> pickAvatarLauncher.launch("image/*"));

        // Đăng xuất
        binding.btnLogout.setOnClickListener(v -> {
            android.util.Log.d("FixIt_ProfileCustomer", "btnLogout clicked. Showing confirmation dialog.");
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc muốn đăng xuất khỏi FixIt VN không?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {
                        android.util.Log.d("FixIt_ProfileCustomer", "btnLogout positive button clicked. Executing logoutUseCase.");
                        logoutUseCase.execute(result -> {
                            android.util.Log.d("FixIt_ProfileCustomer", "logoutUseCase result: success = " + result.isSuccess());
                            if (result.isSuccess()) {
                                // Chuyển về màn hình đăng nhập (AuthActivity)
                                android.util.Log.d("FixIt_ProfileCustomer", "Navigating to AuthActivity and finishing CustomerActivity.");
                                Intent intent = new Intent(requireContext(), AuthActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                requireActivity().finish();
                            } else {
                                Toast.makeText(requireContext(), "Đăng xuất thất bại: " +
                                        (result.getError() != null ? result.getError().getMessage() : "Lỗi không xác định"),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Huỷ", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    private void loadCustomerData() {
        // Hiển thị dữ liệu từ session cache trước (tránh màn hình trống)
        if (sessionStorage != null && sessionStorage.getSession() != null) {
            com.fixit.feature.auth.domain.model.User user = sessionStorage.getSession().getUser();
            if (user != null) {
                if (user.getFullName() != null && !user.getFullName().isEmpty()) {
                    binding.tvProfileName.setText(user.getFullName());
                }
                if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                    binding.tvProfilePhone.setText(user.getPhone());
                }
            }
        }

        // Sau đó tải dữ liệu mới nhất từ server
        if (getCustomerProfileUseCase != null) {
            getCustomerProfileUseCase.execute(result -> {
                if (result.isSuccess() && result.getData() != null) {
                    com.fixit.feature.customer.profile.domain.model.CustomerProfile profile = result.getData();

                    // Phải cập nhật UI trên Main Thread
                    requireActivity().runOnUiThread(() -> {
                        if (binding == null) return;
                        if (profile.getFullName() != null && !profile.getFullName().isEmpty()) {
                            binding.tvProfileName.setText(profile.getFullName());
                        }
                        if (profile.getPhoneNumber() != null && !profile.getPhoneNumber().isEmpty()) {
                            binding.tvProfilePhone.setText(profile.getPhoneNumber());
                        }
                    });

                    // Lưu lại vào session cache để hiển thị nhanh lần sau
                    if (sessionStorage != null) {
                        com.fixit.feature.auth.domain.model.Session currentSession = sessionStorage.getSession();
                        if (currentSession != null && currentSession.getUser() != null) {
                            com.fixit.feature.auth.domain.model.User updatedUser = new com.fixit.feature.auth.domain.model.User(
                                    currentSession.getUser().getId(),
                                    profile.getPhoneNumber() != null ? profile.getPhoneNumber() : currentSession.getUser().getPhone(),
                                    profile.getFullName(),
                                    currentSession.getUser().getRole()
                            );
                            com.fixit.feature.auth.domain.model.Session newSession = new com.fixit.feature.auth.domain.model.Session(
                                    currentSession.getAccessToken(),
                                    currentSession.getRefreshToken(),
                                    updatedUser
                            );
                            sessionStorage.saveSession(newSession);
                        }
                    }
                }
            });
        }

        String savedAvatar = requireContext().getSharedPreferences(com.fixit.core.common.Constants.PREF_NAME, android.content.Context.MODE_PRIVATE)
                .getString("user_avatar", null);
        if (savedAvatar != null && !savedAvatar.isEmpty()) {
            Glide.with(this).load(savedAvatar).circleCrop().into(binding.ivAvatar);
        }
    }

    @Override
    protected void observeData() {
        // Observe kết quả upload avatar
        uploadViewModel.uploadResult.observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.isSuccess()) {
                Toast.makeText(requireContext(), "Ảnh đại diện đã cập nhật", Toast.LENGTH_SHORT).show();
                if (result.getConfirmedUpload() != null) {
                    String fileUrl = result.getConfirmedUpload().getFileUrl();
                    requireContext().getSharedPreferences(com.fixit.core.common.Constants.PREF_NAME, android.content.Context.MODE_PRIVATE)
                            .edit()
                            .putString("user_avatar", fileUrl)
                            .apply();
                    Glide.with(this).load(fileUrl).circleCrop().into(binding.ivAvatar);

                    try {
                        Intent intent = new Intent("com.fixit.PROFILE_UPDATE");
                        requireContext().sendBroadcast(intent);
                    } catch (Exception ignored) {}
                }
            } else {
                Toast.makeText(requireContext(), result.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        profileViewModel.getProfileData().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                binding.tvProfileName.setText(profile.getFullName());
            }
        });

        profileViewModel.getLogoutSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Intent intent = new Intent(requireContext(), AuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (profileViewModel != null) {
            profileViewModel.loadProfile();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (autoRefreshHelper == null) {
            autoRefreshHelper = new AutoRefreshHelper(
                    requireContext(),
                    0L,
                    this::loadCustomerData,
                    "com.fixit.PROFILE_UPDATE"
            );
        }
        autoRefreshHelper.start();
    }

    @Override
    public void onPause() {
        if (autoRefreshHelper != null) {
            autoRefreshHelper.stop();
        }
        super.onPause();
    }
}
