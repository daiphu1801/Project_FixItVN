package com.fixit.feature.worker.profile.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentWorkerEditProfileBinding;
import com.fixit.feature.worker.profile.domain.model.WorkerProfile;
import com.fixit.feature.worker.profile.domain.model.WorkerProfileUpdateInput;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerEditProfileFragment extends BaseFragment<FragmentWorkerEditProfileBinding> {

    private WorkerProfileViewModel viewModel;
    private String currentAvatarUrl;

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

        binding.btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(this).get(WorkerProfileViewModel.class);

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

        viewModel.loadProfile();
    }

    private void bindProfile(WorkerProfile profile) {
        if (profile == null) {
            return;
        }

        currentAvatarUrl = profile.getAvatarUrl();

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
                currentAvatarUrl,
                bio,
                serviceArea
        );

        viewModel.updateProfile(input);
    }
}