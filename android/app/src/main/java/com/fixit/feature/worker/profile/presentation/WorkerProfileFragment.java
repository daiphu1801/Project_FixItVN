package com.fixit.feature.worker.profile.presentation;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.fixit.core.common.AutoRefreshHelper;
import com.fixit.core.ui.BaseFragment;
import com.fixit.core.ui.ViewUtils;
import com.fixit.databinding.FragmentWorkerProfileBinding;
import com.fixit.feature.worker.profile.domain.model.WorkerProfile;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerProfileFragment extends BaseFragment<FragmentWorkerProfileBinding> {

    private WorkerProfileViewModel viewModel;
    private String currentVerificationStatus = "";
    private AutoRefreshHelper autoRefreshHelper;

    @Override
    protected FragmentWorkerProfileBinding inflateViewBinding(
            LayoutInflater inflater,
            ViewGroup container) {
        return FragmentWorkerProfileBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        binding.btnGoToWallet.setOnClickListener(v -> androidx.navigation.Navigation.findNavController(v)
                .navigate(com.fixit.R.id.workerWalletFragment));

        binding.btnChangePassword.setOnClickListener(v -> androidx.navigation.Navigation.findNavController(v)
                .navigate(com.fixit.R.id.changePasswordFragment));

        binding.btnSupportCenter.setOnClickListener(v -> Toast.makeText(requireContext(),
                 "Đang mở Trung tâm hỗ trợ...",
                 Toast.LENGTH_SHORT).show());

        binding.btnEditProfile.setOnClickListener(v -> androidx.navigation.Navigation.findNavController(v)
                .navigate(com.fixit.R.id.workerEditProfileFragment));

        binding.layoutResume.btnEditSpecialization
                 .setOnClickListener(v -> androidx.navigation.Navigation.findNavController(v)
                         .navigate(com.fixit.R.id.workerEditSpecializationFragment));

        binding.btnVerifyKYC.setOnClickListener(v -> {
            if ("APPROVED".equalsIgnoreCase(currentVerificationStatus)) {
                Toast.makeText(requireContext(), "Tài khoản của bạn đã được xác minh thành công!", Toast.LENGTH_SHORT).show();
            } else if ("PENDING".equalsIgnoreCase(currentVerificationStatus)) {
                androidx.navigation.Navigation.findNavController(v)
                        .navigate(com.fixit.R.id.workerKycPendingFragment);
            } else {
                androidx.navigation.Navigation.findNavController(v)
                        .navigate(com.fixit.R.id.workerKycFragment);
            }
        });

        binding.btnLogout.setOnClickListener(v -> showLogoutConfirmDialog());
    }

    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(this).get(WorkerProfileViewModel.class);

        viewModel.profile.observe(getViewLifecycleOwner(), this::bindProfile);

        viewModel.skills.observe(getViewLifecycleOwner(), skills -> {
            if (skills != null) {
                WorkerProfileSpecializationAdapter adapter = new WorkerProfileSpecializationAdapter(skills);
                binding.layoutResume.recyclerSpecializations.setAdapter(adapter);
            }
        });

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

        viewModel.isLoading.observe(getViewLifecycleOwner(), loading -> {
            if (binding.layoutLoading != null) {
                binding.layoutLoading.getRoot().setVisibility(loading ? android.view.View.VISIBLE : android.view.View.GONE);
            }
        });

        viewModel.walletBalance.observe(getViewLifecycleOwner(), balance -> {
            if (balance != null) {
                binding.tvWalletBalance.setText(balance);
            }
        });

        viewModel.loadProfile();
        viewModel.loadSkills();
        viewModel.loadWalletBalance();
    }

    private void bindProfile(WorkerProfile profile) {
        if (profile == null) {
            return;
        }

        currentVerificationStatus = profile.getVerificationStatus();

        binding.layoutProfileHeader.tvWorkerName.setText(
                nonBlank(profile.getFullName(), "Thợ FixIt"));

        // Hiển thị avatar
        String avatarUrl = profile.getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            binding.layoutProfileHeader.ivAvatar.setPadding(0, 0, 0, 0);
            binding.layoutProfileHeader.ivAvatar.setImageTintList(null);
            com.bumptech.glide.Glide.with(this)
                    .load(avatarUrl)
                    .circleCrop()
                    .into(binding.layoutProfileHeader.ivAvatar);
        } else {
            int padding = (int) (20 * getResources().getDisplayMetrics().density);
            binding.layoutProfileHeader.ivAvatar.setPadding(padding, padding, padding, padding);
            binding.layoutProfileHeader.ivAvatar.setImageTintList(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#42c2ff")));
            binding.layoutProfileHeader.ivAvatar.setImageResource(com.fixit.R.drawable.ic_lucide_user);
        }

        binding.tvPhoneNumber.setText(nonBlank(profile.getPhoneNumber(), "Chưa cập nhật"));
        binding.tvEmail.setText(nonBlank(profile.getEmail(), "Chưa cập nhật"));
        binding.tvAddress.setText(nonBlank(profile.getServiceArea(), "Chưa cập nhật"));
        binding.tvIdCard.setText(maskIdentityCard(profile.getIdentityCard()));
        binding.tvExperienceDescription.setText(nonBlank(
                profile.getExperienceDescription(),
                "Chưa cập nhật mô tả kinh nghiệm."));
        binding.tvServiceArea.setText(nonBlank(profile.getServiceArea(), "Chưa cập nhật"));

        binding.layoutProfileHeader.tvRating.setText(
                String.format(java.util.Locale.US, "%.1f ★", profile.getReputationScore()));

        // Wallet balance is updated via live data observation in observeData().

        // Ràng buộc hiển thị trạng thái KYC cho CCCD và Header Badge
        String verificationStatus = profile.getVerificationStatus();
        if ("APPROVED".equalsIgnoreCase(verificationStatus)) {
            binding.tvCccdStatus.setText("Đã duyệt");
            binding.tvCccdStatus.setTextColor(android.graphics.Color.parseColor("#22c55e"));
            binding.ivCccdCheck.setImageResource(com.fixit.R.drawable.ic_lucide_check_circle);
            binding.ivCccdCheck.setImageTintList(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#22c55e")));

            binding.layoutProfileHeader.tvVerifyStatus.setText("Đã xác thực");
            binding.layoutProfileHeader.tvVerifyStatus.setTextColor(android.graphics.Color.parseColor("#22c55e"));
            binding.layoutProfileHeader.tvVerifyStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#dcfce7")));
        } else if ("PENDING".equalsIgnoreCase(verificationStatus)) {
            binding.tvCccdStatus.setText("Đang chờ duyệt");
            binding.tvCccdStatus.setTextColor(android.graphics.Color.parseColor("#f59e0b"));
            binding.ivCccdCheck.setImageResource(com.fixit.R.drawable.ic_lucide_clock);
            binding.ivCccdCheck.setImageTintList(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#f59e0b")));

            binding.layoutProfileHeader.tvVerifyStatus.setText("Chờ duyệt");
            binding.layoutProfileHeader.tvVerifyStatus.setTextColor(android.graphics.Color.parseColor("#f59e0b"));
            binding.layoutProfileHeader.tvVerifyStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#fef3c7")));
        } else if ("REJECTED".equalsIgnoreCase(verificationStatus)) {
            binding.tvCccdStatus.setText("Bị từ chối");
            binding.tvCccdStatus.setTextColor(android.graphics.Color.parseColor("#ef4444"));
            binding.ivCccdCheck.setImageResource(com.fixit.R.drawable.ic_lucide_alert_circle);
            binding.ivCccdCheck.setImageTintList(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#ef4444")));

            binding.layoutProfileHeader.tvVerifyStatus.setText("Bị từ chối");
            binding.layoutProfileHeader.tvVerifyStatus.setTextColor(android.graphics.Color.parseColor("#ef4444"));
            binding.layoutProfileHeader.tvVerifyStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#fee2e2")));
        } else {
            binding.tvCccdStatus.setText("Chưa xác minh");
            binding.tvCccdStatus.setTextColor(android.graphics.Color.parseColor("#94a3b8"));
            binding.ivCccdCheck.setImageResource(com.fixit.R.drawable.ic_lucide_alert_circle);
            binding.ivCccdCheck.setImageTintList(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#94a3b8")));

            binding.layoutProfileHeader.tvVerifyStatus.setText("Chưa xác thực");
            binding.layoutProfileHeader.tvVerifyStatus.setTextColor(android.graphics.Color.parseColor("#94a3b8"));
            binding.layoutProfileHeader.tvVerifyStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#f1f5f9")));
        }
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
                com.fixit.feature.auth.presentation.AuthActivity.class);
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

    @Override
    public void onResume() {
        super.onResume();
        if (autoRefreshHelper == null) {
            autoRefreshHelper = new AutoRefreshHelper(
                    requireContext(),
                    0L,
                    () -> {
                        if (viewModel != null) {
                            viewModel.loadProfile();
                            viewModel.loadSkills();
                            viewModel.loadWalletBalance();
                        }
                    },
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