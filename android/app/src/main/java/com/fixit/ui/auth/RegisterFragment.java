package com.fixit.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.fixit.databinding.FragmentRegisterBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private AuthViewModel viewModel;
    private String selectedRole = "CUSTOMER"; // Mặc định

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        if (getArguments() != null) {
            selectedRole = getArguments().getString("role", "CUSTOMER");
        }
        
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        binding.tvLogin.setOnClickListener(v -> requireActivity().onBackPressed());

        binding.btnRegister.setOnClickListener(v -> {
            String fullName = binding.etFullName.getText().toString().trim();
            String phone = binding.etPhone.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            String confirmPass = binding.etConfirmPassword.getText().toString().trim();

            if (fullName.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPass)) {
                Toast.makeText(getContext(), "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.register(phone, password, fullName, selectedRole);
        });

        viewModel.authResult.observe(getViewLifecycleOwner(), authResponse -> {
            Toast.makeText(getContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
            // Chuyển sang màn hình chính hoặc login
            requireActivity().onBackPressed();
        });

        viewModel.error.observe(getViewLifecycleOwner(), errorMsg -> {
            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
        });

        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.btnRegister.setEnabled(!isLoading);
            binding.btnRegister.setText(isLoading ? "Đang xử lý..." : "Đăng ký");
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
