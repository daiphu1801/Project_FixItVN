package com.fixit.feature.auth.presentation;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentForgotpasswordBinding;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * FILE ĐIỀU KHIỂN MÀN HÌNH QUÊN MẬT KHẨU
 * Mục đích: Giúp người dùng lấy lại mật khẩu qua Email hoặc Số điện thoại và mã OTP.
 */
@AndroidEntryPoint
public class ForgotPasswordFragment extends BaseFragment<FragmentForgotpasswordBinding> {

    private AuthViewModel viewModel;

    // Lưu lại giá trị email/phone để dùng ở bước 2 (đặt lại mật khẩu)
    private String userEmail = "";
    private String userPhone = "";

    // true = đang dùng email, false = đang dùng phone
    private boolean isEmailMethod = true;

    private CountDownTimer countDownTimer;

    @NonNull
    @Override
    protected FragmentForgotpasswordBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentForgotpasswordBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        // Lắng nghe thay đổi phương thức (Email / SĐT)
        binding.rgResetMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.rbEmail.getId()) {
                isEmailMethod = true;
                binding.tilEmail.setVisibility(View.VISIBLE);
                binding.tilPhone.setVisibility(View.GONE);
            } else {
                isEmailMethod = false;
                binding.tilEmail.setVisibility(View.GONE);
                binding.tilPhone.setVisibility(View.VISIBLE);
            }
        });

        // Xử lý nút quay lại
        binding.btnBack.setOnClickListener(v -> {
            if (binding.layoutStepOTP.getVisibility() == View.VISIBLE) {
                showEmailStep();
            } else {
                if (navController != null) {
                    navController.navigateUp();
                }
            }
        });

        // Xử lý nút gửi mã xác thực / đặt lại mật khẩu
        binding.btnSendCode.setOnClickListener(v -> {
            if (binding.layoutStepEmail.getVisibility() == View.VISIBLE) {
                handleSendOtp();
            } else {
                handleResetPassword();
            }
        });

        // Gửi lại mã OTP
        binding.tvResendCode.setOnClickListener(v -> {
            viewModel.forgotPassword(
                    isEmailMethod ? userEmail : null,
                    isEmailMethod ? null : userPhone
            );
        });
    }

    private void handleSendOtp() {
        if (isEmailMethod) {
            String email = binding.etEmail.getText().toString().trim().toLowerCase();
            if (email.isEmpty()) {
                showError("Vui lòng nhập email");
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showError("Định dạng email không hợp lệ");
                return;
            }
            userEmail = email;
            userPhone = "";
            viewModel.forgotPassword(userEmail, null);
        } else {
            String phone = binding.etPhone.getText().toString().trim();
            if (phone.isEmpty()) {
                showError("Vui lòng nhập số điện thoại");
                return;
            }
            if (phone.length() < 9) {
                showError("Số điện thoại không hợp lệ");
                return;
            }
            userPhone = phone;
            userEmail = "";
            viewModel.forgotPassword(null, userPhone);
        }
    }

    private void handleResetPassword() {
        String otpCode = binding.etOTP.getText().toString().trim();
        String newPassword = binding.etNewPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        if (otpCode.isEmpty() || otpCode.length() < 6) {
            showError("Vui lòng nhập đúng mã OTP 6 số");
            return;
        }

        if (newPassword.isEmpty()) {
            showError("Vui lòng nhập mật khẩu mới");
            return;
        }

        if (newPassword.length() < 6) {
            showError("Mật khẩu phải chứa ít nhất 6 ký tự");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Xác nhận mật khẩu không khớp");
            return;
        }

        viewModel.resetPassword(
                isEmailMethod ? userEmail : null,
                isEmailMethod ? null : userPhone,
                otpCode,
                newPassword
        );
    }

    private void startResendTimer() {
        binding.tvResendCode.setEnabled(false);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.tvResendCode.setText("Gửi lại mã (" + (millisUntilFinished / 1000) + "s)");
            }

            @Override
            public void onFinish() {
                binding.tvResendCode.setText("Gửi lại mã");
                binding.tvResendCode.setEnabled(true);
            }
        }.start();
    }

    // Hiển thị giao diện nhập Email/SĐT (Bước 1)
    private void showEmailStep() {
        binding.layoutStepEmail.setVisibility(View.VISIBLE);
        binding.layoutStepOTP.setVisibility(View.GONE);
        binding.btnSendCode.setText("Gửi mã xác thực");
    }

    // Hiển thị giao diện nhập mã OTP + mật khẩu mới (Bước 2)
    private void showOTPStep() {
        binding.layoutStepEmail.setVisibility(View.GONE);
        binding.layoutStepOTP.setVisibility(View.VISIBLE);
        binding.btnSendCode.setText("Đặt lại mật khẩu");

        // Hiển thị gợi ý để user biết OTP đã gửi tới đâu
        String destination = isEmailMethod
                ? "email: " + userEmail
                : "số điện thoại: " + userPhone;
        binding.tvVerifyOTPDesc.setText("Nhập mã OTP đã gửi đến " + destination);
    }

    @Override
    protected void observeData() {
        viewModel.uiState.observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;
            boolean isLoading = state.isLoading();
            binding.btnSendCode.setEnabled(!isLoading);
            binding.btnSendCode.setText(isLoading ? "Đang xử lý..." :
                (binding.layoutStepEmail.getVisibility() == View.VISIBLE ? "Gửi mã xác thực" : "Đặt lại mật khẩu"));

            // Chỉ hiện Toast lỗi khi KHÔNG phải loading và có message lỗi
            if (!isLoading && state.getErrorMessage() != null) {
                showError(state.getErrorMessage());
                // Reset state ngay sau khi đã hiện lỗi — tránh Toast xuất hiện lại khi re-observe
                viewModel.resetState();
            }
        });

        viewModel.event.observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            if (event.getType() == AuthEvent.Type.FORGOT_PASSWORD_SUCCESS) {
                // Consume event trước để tránh bị phát lại
                viewModel.consumeEvent();
                String method = isEmailMethod ? "email của bạn" : "số điện thoại của bạn";
                showSuccess("Đã gửi mã OTP đến " + method);
                showOTPStep();
                startResendTimer();
            } else if (event.getType() == AuthEvent.Type.RESET_PASSWORD_SUCCESS) {
                viewModel.consumeEvent();
                showSuccess("Đặt lại mật khẩu thành công! Hãy đăng nhập lại.");
                if (navController != null) {
                    navController.navigateUp();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onDestroy();
    }
}
