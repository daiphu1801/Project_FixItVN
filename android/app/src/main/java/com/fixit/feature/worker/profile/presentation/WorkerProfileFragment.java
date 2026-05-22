package com.fixit.feature.worker.profile.presentation;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.fixit.core.ui.BaseFragment;
import com.fixit.core.ui.ViewUtils;
import com.fixit.databinding.FragmentWorkerProfileBinding;
import com.fixit.feature.worker.profile.domain.model.WorkerProfile;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerProfileFragment extends BaseFragment<FragmentWorkerProfileBinding> {

    private WorkerProfileViewModel viewModel;

    @Override
    protected FragmentWorkerProfileBinding inflateViewBinding(
            LayoutInflater inflater,
            ViewGroup container
    ) {
        return FragmentWorkerProfileBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        binding.btnGoToWallet.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v)
                        .navigate(com.fixit.R.id.workerWalletFragment)
        );

        binding.btnChangePassword.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v)
                        .navigate(com.fixit.R.id.changePasswordFragment)
        );

        binding.btnSupportCenter.setOnClickListener(v ->
                Toast.makeText(requireContext(),
                        "Đang mở Trung tâm hỗ trợ...",
                        Toast.LENGTH_SHORT
                ).show()
        );

        binding.btnEditProfile.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v)
                        .navigate(com.fixit.R.id.workerEditProfileFragment)
        );

        binding.layoutResume.btnEditSpecialization.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v)
                        .navigate(com.fixit.R.id.workerEditSpecializationFragment)
        );

        binding.btnVerifyKYC.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v)
                        .navigate(com.fixit.R.id.workerKycFragment)
        );

        binding.btnLogout.setOnClickListener(v -> showLogoutConfirmDialog());
    }

    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(this).get(WorkerProfileViewModel.class);

        viewModel.profile.observe(getViewLifecycleOwner(), this::bindProfile);

        viewModel.logoutSuccess.observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                navigateToAuth();
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

        binding.layoutProfileHeader.tvWorkerName.setText(
                nonBlank(profile.getFullName(), "Thợ FixIt")
        );
        binding.tvPhoneNumber.setText(nonBlank(profile.getPhoneNumber(), "Chưa cập nhật"));
        binding.tvEmail.setText(nonBlank(profile.getEmail(), "Chưa cập nhật"));
        binding.tvAddress.setText(nonBlank(profile.getServiceArea(), "Chưa cập nhật"));
        binding.tvIdCard.setText(maskIdentityCard(profile.getIdentityCard()));
        binding.tvExperienceDescription.setText(nonBlank(
                profile.getExperienceDescription(),
                "Chưa cập nhật mô tả kinh nghiệm."
        ));
        binding.tvServiceArea.setText(nonBlank(profile.getServiceArea(), "Chưa cập nhật"));

        binding.layoutProfileHeader.tvRating.setText(
                String.format(java.util.Locale.US, "%.1f ★", profile.getReputationScore())
        );

        /*
         * Wallet hiện chưa nối API wallet thật ở màn này.
         * Khi nối GET /workers/me/wallet thì mới set lại tvWalletBalance.
         */
        binding.tvWalletBalance.setText(ViewUtils.formatCurrency(0));
    }

    private void showLogoutConfirmDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất khỏi FixIt VN không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> viewModel.logout())
                .setNegativeButton("Huỷ", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void navigateToAuth() {
        Intent intent = new Intent(
                requireContext(),
                com.fixit.feature.auth.presentation.AuthActivity.class
        );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private String nonBlank(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }

        return value;
    }

    private String maskIdentityCard(String value) {
        if (value == null || value.length() < 4) {
            return "Chưa cập nhật";
        }

        String last4 = value.substring(value.length() - 4);
        return "********" + last4;
    }
}