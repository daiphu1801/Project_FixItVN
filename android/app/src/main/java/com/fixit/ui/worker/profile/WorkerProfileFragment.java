package com.fixit.ui.worker.profile;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.fixit.base.BaseFragment;
import com.fixit.databinding.FragmentWorkerProfileBinding;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment màn hình Tài khoản của Thợ.
 *
 * Vai trò: Hub trung tâm – gom thông tin cá nhân + lối vào nhanh Ví + cài đặt.
 *
 * Luồng điều hướng:
 *   - Vào: từ bottom navigation tab "Tài khoản" hoặc nhấn avatar tại Trang chủ.
 *   - Ra (Ví): btnGoToWallet → workerWalletFragment (trong cùng NavController).
 *   - Ra (Đăng xuất): Intent sang AuthActivity, xóa toàn bộ back stack.
 */
@AndroidEntryPoint
public class WorkerProfileFragment extends BaseFragment<FragmentWorkerProfileBinding> {

    private WorkerProfileViewModel viewModel;

    // ──────────────────────────────────────────────────────────────────────────
    // 1. Inflate View Binding
    // ──────────────────────────────────────────────────────────────────────────
    @Override
    protected FragmentWorkerProfileBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerProfileBinding.inflate(inflater, container, false);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 2. Setup Views – Wire up all click listeners (KHÔNG có logic nghiệp vụ)
    // ──────────────────────────────────────────────────────────────────────────
    @Override
    protected void setupViews() {

        // --- Nút "Xem ví →" trên card Ví tiền ---
        // Điều hướng trực tiếp sang tab Ví trong bottom navigation
        binding.btnGoToWallet.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v)
                        .navigate(com.fixit.R.id.workerWalletFragment)
        );

        // --- Đổi mật khẩu ---
        binding.btnChangePassword.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v)
                        .navigate(com.fixit.R.id.changePasswordFragment)
        );

        // --- Trung tâm hỗ trợ ---
        binding.btnSupportCenter.setOnClickListener(v ->
                android.widget.Toast.makeText(requireContext(),
                        "Đang mở Trung tâm hỗ trợ...", android.widget.Toast.LENGTH_SHORT).show()
        );

        // Nút Edit Profile
        binding.btnEditProfile.setOnClickListener(v -> 
                androidx.navigation.Navigation.findNavController(v).navigate(com.fixit.R.id.workerEditProfileFragment)
        );

        // Nút Edit Lĩnh vực chuyên môn
        binding.layoutResume.btnEditSpecialization.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v).navigate(com.fixit.R.id.workerEditSpecializationFragment)
        );

        // Click để Xác minh danh tính
        binding.btnVerifyKYC.setOnClickListener(v -> 
                androidx.navigation.Navigation.findNavController(v)
                        .navigate(com.fixit.R.id.workerKycFragment)
        );

        // --- Nút Đăng xuất ---
        binding.btnLogout.setOnClickListener(v -> showLogoutConfirmDialog());
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 3. Observe Data – Lắng nghe dữ liệu từ ViewModel và cập nhật UI
    // ──────────────────────────────────────────────────────────────────────────
    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(this).get(WorkerProfileViewModel.class);

        // Lắng nghe sự kiện đăng xuất thành công
        viewModel.logoutSuccess.observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                navigateToAuth();
            }
        });

        // TODO: Khi ViewModel có dữ liệu thực từ API, uncomment và cập nhật:
        // viewModel.getWorkerProfile().observe(getViewLifecycleOwner(), profile -> { ... });
        // viewModel.fetchWorkerProfile();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 4. Private Helpers
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Hiển thị Dialog xác nhận đăng xuất.
     * Mục đích: Tránh người dùng vô tình nhấn Đăng xuất.
     */
    private void showLogoutConfirmDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất khỏi FixIt VN không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    viewModel.logout();
                    navigateToAuth();
                })
                .setNegativeButton("Huỷ", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Chuyển sang AuthActivity và xóa toàn bộ back stack.
     * Đây là hành động sau khi đăng xuất thành công.
     *
     * Lý do dùng Intent thay vì NavController:
     *   AuthActivity nằm ở một Task/Back stack riêng (android:exported=true).
     *   NavController chỉ điều phối trong cùng một Activity.
     */
    private void navigateToAuth() {
        // Thay "AuthActivity" bằng class thực tế của bạn
        Intent intent = new Intent(requireContext(), com.fixit.ui.auth.AuthActivity.class);
        // Xóa toàn bộ back stack – người dùng không thể bấm Back để quay lại
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
