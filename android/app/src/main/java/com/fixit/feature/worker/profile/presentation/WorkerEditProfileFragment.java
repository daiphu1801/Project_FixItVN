package com.fixit.feature.worker.profile.presentation;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentWorkerEditProfileBinding;
import com.fixit.feature.upload.domain.model.UploadPurpose;
import com.fixit.feature.upload.domain.model.UploadTargetType;
import com.fixit.feature.upload.presentation.UploadViewModel;
import com.fixit.feature.worker.profile.domain.model.WorkerProfile;
import com.fixit.feature.worker.profile.domain.model.WorkerProfileUpdateInput;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerEditProfileFragment extends BaseFragment<FragmentWorkerEditProfileBinding> {

    private WorkerProfileViewModel viewModel;
    private UploadViewModel uploadViewModel;
    private String currentAvatarUrl;

    // Image picker cho avatar
    private final ActivityResultLauncher<String> pickAvatarLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    // Hiển thị ảnh mới ngay lập tức
                    binding.ivAvatar.setPadding(0, 0, 0, 0);
                    binding.ivAvatar.setImageTintList(null);
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

    @Override
    protected FragmentWorkerEditProfileBinding inflateViewBinding(
            LayoutInflater inflater,
            ViewGroup container
    ) {
        return FragmentWorkerEditProfileBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        binding.appBarLayout.toolbar.setTitle("Chỉnh sửa hồ sơ");
        binding.appBarLayout.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).navigateUp()
        );

        /*
         * Các trường không cho sửa trực tiếp:
         * - phone: về sau sửa qua OTP/change phone riêng.
         * - cccd: sửa qua KYC/Admin.
         */
        binding.etPhone.setEnabled(false);
        binding.etCccd.setEnabled(false);

        // Click avatar hoặc text "Thay đổi ảnh đại diện" → mở gallery
        binding.ivAvatar.setOnClickListener(v -> pickAvatarLauncher.launch("image/*"));
        binding.tvChangeAvatar.setOnClickListener(v -> pickAvatarLauncher.launch("image/*"));

        binding.btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(this).get(WorkerProfileViewModel.class);
        uploadViewModel = new ViewModelProvider(this).get(UploadViewModel.class);

        viewModel.profile.observe(getViewLifecycleOwner(), this::bindProfile);

        viewModel.profileUpdated.observe(getViewLifecycleOwner(), updated -> {
            if (Boolean.TRUE.equals(updated)) {
                Toast.makeText(requireContext(), "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        viewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.trim().isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe kết quả upload avatar
        uploadViewModel.uploadResult.observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.isSuccess() && UploadPurpose.AVATAR.equals(result.getPurpose())) {
                Toast.makeText(requireContext(), "Ảnh đại diện đã vào hàng đợi cập nhật", Toast.LENGTH_SHORT).show();
            } else if (!result.isSuccess()) {
                Toast.makeText(requireContext(), result.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.isLoading.observe(getViewLifecycleOwner(), loading -> {
            if (binding.layoutLoading != null) {
                binding.layoutLoading.getRoot().setVisibility(loading ? android.view.View.VISIBLE : android.view.View.GONE);
            }
        });

        viewModel.loadProfile();
    }

    private void bindProfile(WorkerProfile profile) {
        if (profile == null) {
            return;
        }

        currentAvatarUrl = profile.getAvatarUrl();

        // Hiển thị avatar hiện tại
        if (currentAvatarUrl != null && !currentAvatarUrl.isEmpty()) {
            binding.ivAvatar.setPadding(0, 0, 0, 0);
            binding.ivAvatar.setImageTintList(null);
            Glide.with(this).load(currentAvatarUrl).circleCrop().into(binding.ivAvatar);
        } else {
            // Khôi phục placeholder mặc định
            int padding = (int) (16 * getResources().getDisplayMetrics().density);
            binding.ivAvatar.setPadding(padding, padding, padding, padding);
            binding.ivAvatar.setImageTintList(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#64748b")
            ));
            binding.ivAvatar.setImageResource(com.fixit.R.drawable.ic_lucide_user);
        }

        binding.etFullName.setText(profile.getFullName());
        binding.etPhone.setText(profile.getPhoneNumber());
        binding.etEmail.setText(profile.getEmail());

        /*
         * Backend hiện chưa có address riêng cho worker.
         * Tạm hiển thị serviceArea ở etAddress để không để field mock.
         */
        binding.etAddress.setText(profile.getServiceArea());

        binding.etBio.setText(profile.getExperienceDescription());
        binding.etServiceArea.setText(profile.getServiceArea());
        binding.etCccd.setText(profile.getIdentityCard());
    }

    private void saveProfile() {
        String fullName = binding.etFullName.getText() != null
                ? binding.etFullName.getText().toString().trim()
                : "";

        String email = binding.etEmail.getText() != null
                ? binding.etEmail.getText().toString().trim()
                : "";

        String bio = binding.etBio.getText() != null
                ? binding.etBio.getText().toString().trim()
                : "";

        String serviceArea = binding.etServiceArea.getText() != null
                ? binding.etServiceArea.getText().toString().trim()
                : "";

        if (fullName.isEmpty()) {
            binding.etFullName.setError("Vui lòng nhập họ tên");
            return;
        }

        WorkerProfileUpdateInput input = new WorkerProfileUpdateInput(
                fullName,
                email,
                null,
                bio,
                serviceArea
        );

        viewModel.updateProfile(input);
    }
}
