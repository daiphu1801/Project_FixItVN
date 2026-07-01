package com.fixit.feature.auth.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentRegisterBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterFragment extends BaseFragment<FragmentRegisterBinding> {

    private AuthViewModel viewModel;
    private String selectedRole = "CUSTOMER";

    @NonNull
    @Override
    protected FragmentRegisterBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentRegisterBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        if (getArguments() != null) {
            selectedRole = getArguments().getString("role", "CUSTOMER");
        }

        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        binding.tvLogin.setOnClickListener(v -> requireActivity().onBackPressed());

        binding.btnRegister.setOnClickListener(v -> {
            String fullName = binding.etFullName.getText().toString().trim();
            String phone = binding.etPhone.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim().toLowerCase();
            String password = binding.etPassword.getText().toString().trim();
            String confirmPass = binding.etConfirmPassword.getText().toString().trim();

            if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showError("Vui lòng nhập đầy đủ thông tin");
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showError("Email không đúng định dạng");
                return;
            }

            if (!password.equals(confirmPass)) {
                showError("Mật khẩu xác nhận không khớp");
                return;
            }

            String passwordPattern = "^(?=.*[0-9])(?=.*[.,!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|<>/?])[A-Z].{7,}$";
            if (!password.matches(passwordPattern)) {
                showError("Mật khẩu phải từ 8 ký tự trở lên, bắt đầu bằng chữ viết hoa, chứa ít nhất một chữ số và một ký tự đặc biệt (ví dụ: .,!)");
                return;
            }

            viewModel.register(phone, email, password, fullName, selectedRole);
        });
    }

    @Override
    protected void observeData() {
        viewModel.event.observe(getViewLifecycleOwner(), event -> {
            if (event != null && event.getType() == AuthEvent.Type.REGISTER_SUCCESS) {
                showSuccess("Đăng ký thành công!");
                requireActivity().onBackPressed();
            }
        });

        viewModel.uiState.observe(getViewLifecycleOwner(), state -> {
            if (state == null) {
                return;
            }
            binding.btnRegister.setEnabled(!state.isLoading());
            binding.btnRegister.setText(state.isLoading() ? "Đang xử lý..." : "Đăng ký");
            if (state.getErrorMessage() != null) {
                showError(state.getErrorMessage());
            }
        });
    }
}
