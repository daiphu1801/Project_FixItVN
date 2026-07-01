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

    private String fullName = null;
    private String email = null;
    private String bio = null;
    private String serviceArea = null;
    private Double selectedLatitude = null;
    private Double selectedLongitude = null;
    private String currentAvatarUrl = null;

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

        // Đặt ô nhập Khu vực hoạt động thành chỉ chọn từ bản đồ
        binding.etServiceArea.setFocusable(false);
        binding.etServiceArea.setClickable(true);
        binding.etServiceArea.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(com.fixit.R.id.workerLocationPickerFragment);
        });
        
        binding.tilServiceArea.setEndIconOnClickListener(v -> {
            Navigation.findNavController(v).navigate(com.fixit.R.id.workerLocationPickerFragment);
        });

        // Đăng ký nhận kết quả từ Map Picker
        getParentFragmentManager().setFragmentResultListener(
                WorkerLocationPickerFragment.REQUEST_KEY,
                this,
                (requestKey, bundle) -> {
                    String address = bundle.getString(WorkerLocationPickerFragment.ADDRESS_KEY);
                    double lat = bundle.getDouble(WorkerLocationPickerFragment.LATITUDE_KEY);
                    double lng = bundle.getDouble(WorkerLocationPickerFragment.LONGITUDE_KEY);
                    
                    serviceArea = address;
                    selectedLatitude = lat;
                    selectedLongitude = lng;
                    
                    if (binding != null) {
                        binding.etServiceArea.setText(address);
                    }
                }
        );

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
                showSuccess("Cập nhật hồ sơ thành công");
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        viewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.trim().isEmpty()) {
                showError(message);
            }
        });

        // Observe kết quả upload avatar
        uploadViewModel.uploadResult.observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.isSuccess() && UploadPurpose.AVATAR.equals(result.getPurpose())) {
                showSuccess("Ảnh đại diện đã vào hàng đợi cập nhật");
            } else if (!result.isSuccess()) {
                showError(result.getErrorMessage());
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

        // Chỉ gán giá trị lần đầu tiên khi tải từ server
        if (fullName == null) fullName = profile.getFullName();
        if (email == null) email = profile.getEmail();
        if (bio == null) bio = profile.getExperienceDescription();
        if (serviceArea == null) serviceArea = profile.getServiceArea();
        if (selectedLatitude == null) selectedLatitude = profile.getLatitude();
        if (selectedLongitude == null) selectedLongitude = profile.getLongitude();
        if (currentAvatarUrl == null) currentAvatarUrl = profile.getAvatarUrl();

        updateUI();
    }

    private void updateUI() {
        if (binding == null) return;

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

        binding.etFullName.setText(fullName);
        binding.etPhone.setText(viewModel.profile.getValue() != null ? viewModel.profile.getValue().getPhoneNumber() : "");
        binding.etEmail.setText(email);

        binding.etAddress.setText(viewModel.profile.getValue() != null ? viewModel.profile.getValue().getServiceArea() : "");
        binding.etBio.setText(bio);
        binding.etServiceArea.setText(serviceArea);
        binding.etCccd.setText(viewModel.profile.getValue() != null ? viewModel.profile.getValue().getIdentityCard() : "");
    }

    @Override
    public void onDestroyView() {
        // Lưu lại trạng thái người dùng đang chỉnh sửa trước khi View bị huỷ để khôi phục sau đó
        if (binding != null) {
            fullName = binding.etFullName.getText() != null ? binding.etFullName.getText().toString().trim() : null;
            email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim().toLowerCase() : null;
            bio = binding.etBio.getText() != null ? binding.etBio.getText().toString().trim() : null;
            serviceArea = binding.etServiceArea.getText() != null ? binding.etServiceArea.getText().toString().trim() : null;
        }
        super.onDestroyView();
    }

    private void saveProfile() {
        String inputFullName = binding.etFullName.getText() != null
                ? binding.etFullName.getText().toString().trim()
                : "";

        String inputEmail = binding.etEmail.getText() != null
                ? binding.etEmail.getText().toString().trim().toLowerCase()
                : "";

        String inputBio = binding.etBio.getText() != null
                ? binding.etBio.getText().toString().trim()
                : "";

        String inputServiceArea = binding.etServiceArea.getText() != null
                ? binding.etServiceArea.getText().toString().trim()
                : "";

        if (inputFullName.isEmpty()) {
            binding.etFullName.setError("Vui lòng nhập họ tên");
            return;
        }

        WorkerProfileUpdateInput input = new WorkerProfileUpdateInput(
                inputFullName,
                inputEmail,
                null,
                inputBio,
                inputServiceArea,
                selectedLatitude,
                selectedLongitude
        );

        viewModel.updateProfile(input);
    }
}
