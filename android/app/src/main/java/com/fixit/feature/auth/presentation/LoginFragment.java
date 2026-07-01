package com.fixit.feature.auth.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends BaseFragment<FragmentLoginBinding> {

    private AuthViewModel viewModel;
    private String selectedRole = "Customer";

    private GoogleSignInClient googleSignInClient;
    private String googleIdToken = null;

    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        if (account != null) {
                            googleIdToken = account.getIdToken();
                            String email = account.getEmail() != null ? account.getEmail().trim().toLowerCase() : "";
                            binding.etGoogleEmail.setText(email);
                            showGoogleExtraStep();
                        }
                    } catch (ApiException e) {
                        android.util.Log.e("FixIt_LoginFragment", "Google Sign-In failed", e);
                        String errorMsg = "Google Sign-In thất bại (mã " + e.getStatusCode() + "). ";
                        if (e.getStatusCode() == 10) {
                            errorMsg += "Vui lòng kiểm tra lại Web Client ID hoặc SHA-1 trong Firebase.";
                        }
                        showError(errorMsg);
                    }
                }
            }
    );

    @NonNull
    @Override
    protected FragmentLoginBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentLoginBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        if (getArguments() != null) {
            selectedRole = getArguments().getString("role", "Customer");
        }

        // Configure Google Sign-In
        String webClientId = getString(R.string.google_web_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // Customize giao diện theo role
        applyRoleStyle();

        binding.btnBack.setOnClickListener(v -> {
            if (binding.layoutLoginGoogleExtra.getVisibility() == View.VISIBLE) {
                showMainLogin();
            } else {
                requireActivity().onBackPressed();
            }
        });

        binding.btnLoginGG.setOnClickListener(v -> {
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                googleSignInLauncher.launch(signInIntent);
            });
        });

        binding.btnConfirmGoogle.setOnClickListener(v -> {
            if (googleIdToken == null) {
                showError("Không lấy được token Google. Vui lòng thử lại.");
                return;
            }
            viewModel.loginWithGoogle(googleIdToken, selectedRole);
        });

        binding.tvRegister.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment, getArguments()));

        binding.tvForgotPassword.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_forgotPasswordFragment, getArguments()));

        binding.btnLogin.setOnClickListener(v -> {
            String phone = binding.etPhone.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (phone.isEmpty() || password.isEmpty()) {
                showError("Vui lòng nhập đầy đủ thông tin");
                return;
            }

            viewModel.login(phone, password, selectedRole);
        });
    }

    @Override
    protected void observeData() {
        viewModel.uiState.observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;
            boolean isLoading = state.isLoading();
            binding.btnLogin.setEnabled(!isLoading);
            binding.btnLogin.setText(isLoading ? "Đang xử lý..." : "Đăng nhập");
            binding.btnLoginGG.setEnabled(!isLoading);
            binding.btnConfirmGoogle.setEnabled(!isLoading);
            binding.btnConfirmGoogle.setText(isLoading ? "Đang xử lý..." : "Tiếp tục");

            if (state.getErrorMessage() != null) {
                showError(state.getErrorMessage());
            }
        });
    }

    /**
     * Tuỳ biến UI theo vai trò đăng nhập:
     * - CUSTOMER: màu xanh dương, badge "Khách hàng", hiện nút Google
     * - WORKER:   màu cam/amber, badge "Thợ sửa chữa", ẩn nút Google
     */
    private void applyRoleStyle() {
        boolean isWorker = "Worker".equalsIgnoreCase(selectedRole);

        if (isWorker) {
            // === WORKER THEME (màu amber #f59e0b) ===
            String workerAccent = "#f59e0b";

            // Badge background + text + icon
            binding.layoutRoleBadge.setBackgroundResource(R.drawable.bg_badge_amber);
            binding.tvRoleBadge.setText("Thợ sửa chữa");
            binding.tvRoleBadge.setTextColor(android.graphics.Color.parseColor(workerAccent));
            binding.ivRoleIcon.setImageResource(R.drawable.ic_lucide_wrench);
            binding.ivRoleIcon.setColorFilter(android.graphics.Color.parseColor(workerAccent));

            // Header & Subtitle
            binding.tvLoginHeader.setText("Chào mừng thợ!");
            binding.tvLoginSubtitle.setText("Đăng nhập để nhận và quản lý đơn sửa chữa");

            // Button màu amber
            binding.btnLogin.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor(workerAccent)));

            // Ẩn Google Login — Worker không dùng Google
            binding.btnLoginGG.setVisibility(View.GONE);
            binding.layoutDivider.setVisibility(View.GONE);

            // Stroke màu amber cho input
            binding.tilPhone.setBoxStrokeColorStateList(
                    android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor(workerAccent)));
            binding.tilPassword.setBoxStrokeColorStateList(
                    android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor(workerAccent)));

        } else {
            // === CUSTOMER THEME (màu xanh #42c2ff) ===
            // Badge background + text + icon
            binding.layoutRoleBadge.setBackgroundResource(R.drawable.bg_badge_light_blue);
            binding.tvRoleBadge.setText("Khách hàng");
            binding.tvRoleBadge.setTextColor(android.graphics.Color.parseColor("#42c2ff"));
            binding.ivRoleIcon.setImageResource(R.drawable.ic_lucide_user);
            binding.ivRoleIcon.setColorFilter(android.graphics.Color.parseColor("#42c2ff"));

            // Header & Subtitle
            binding.tvLoginHeader.setText("Đăng nhập");
            binding.tvLoginSubtitle.setText("Chào mừng trở lại!");

            // Hiện Google Login
            binding.btnLoginGG.setVisibility(View.VISIBLE);
            binding.layoutDivider.setVisibility(View.VISIBLE);
        }
    }

    private void showMainLogin() {
        binding.layoutLoginMain.setVisibility(View.VISIBLE);
        binding.layoutLoginGoogleExtra.setVisibility(View.GONE);
    }

    private void showGoogleExtraStep() {
        binding.layoutLoginMain.setVisibility(View.GONE);
        binding.layoutLoginGoogleExtra.setVisibility(View.VISIBLE);
    }
}
