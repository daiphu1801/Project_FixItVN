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
import androidx.navigation.Navigation;

import com.fixit.R;
import com.fixit.databinding.FragmentLoginBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> {
            if (binding.layoutLoginGoogleExtra.getVisibility() == View.VISIBLE) {
                showMainLogin();
            } else {
                requireActivity().onBackPressed();
            }
        });

        binding.btnLoginGG.setOnClickListener(v -> {
            // Giả sử sau khi chọn tài khoản Google xong, hiện bước nhập SĐT
            showGoogleExtraStep();
        });

        binding.btnConfirmGoogle.setOnClickListener(v -> {
            // Xử lý hoàn tất đăng nhập Google
        });

        binding.tvRegister.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment, getArguments());
        });

        binding.tvForgotPassword.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_forgotPasswordFragment, getArguments());
        });

        binding.btnLogin.setOnClickListener(v -> {
            String phone = binding.etPhone.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.login(phone, password);
        });

        viewModel.authResult.observe(getViewLifecycleOwner(), authResponse -> {
            Toast.makeText(getContext(), "Đăng nhập thành công! Chào " + authResponse.getUser().getFullName(), Toast.LENGTH_SHORT).show();
            // TODO: Lưu token và chuyển vào màn hình chính (MainActivity hoặc CustomerActivity)
        });

        viewModel.error.observe(getViewLifecycleOwner(), errorMsg -> {
            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
        });

        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.btnLogin.setEnabled(!isLoading);
            binding.btnLogin.setText(isLoading ? "Đang xử lý..." : "Đăng nhập");
        });
    }

    private void showMainLogin() {
        binding.layoutLoginMain.setVisibility(View.VISIBLE);
        binding.layoutLoginGoogleExtra.setVisibility(View.GONE);
    }

    private void showGoogleExtraStep() {
        binding.layoutLoginMain.setVisibility(View.GONE);
        binding.layoutLoginGoogleExtra.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
