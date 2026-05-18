package com.fixit.feature.auth.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentForgotpasswordBinding;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * FILE ĐIỀU KHIỂN MÀN HÌNH QUÊN MẬT KHẨU
 * Mục đích: Giúp người dùng lấy lại mật khẩu qua Email và mã OTP.
 */
@AndroidEntryPoint
public class ForgotPasswordFragment extends BaseFragment<FragmentForgotpasswordBinding> {

    @NonNull
    @Override
    protected FragmentForgotpasswordBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        // Kết nối giao diện fragment_forgotpassword.xml
        return FragmentForgotpasswordBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Xử lý nút quay lại
        binding.btnBack.setOnClickListener(v -> {
            if (binding.layoutStepOTP.getVisibility() == View.VISIBLE) {
                // Nếu đang ở bước nhập OTP, nhấn Back sẽ quay lại bước nhập Email
                showEmailStep();
            } else {
                // Nếu đang ở bước Email, nhấn Back sẽ thoát màn hình để về trang Đăng nhập
                if (navController != null) {
                    navController.navigateUp();
                }
            }
        });

        // Xử lý nút gửi mã xác thực / xác thực OTP
        binding.btnSendCode.setOnClickListener(v -> {
            if (binding.layoutStepEmail.getVisibility() == View.VISIBLE) {
                // Giả lập gửi mã thành công, chuyển sang bước nhập OTP
                showOTPStep();
            } else {
                // Nơi viết code xử lý xác thực mã OTP thực tế
            }
        });
    }

    // Hiển thị giao diện nhập Email
    private void showEmailStep() {
        binding.layoutStepEmail.setVisibility(View.VISIBLE);
        binding.layoutStepOTP.setVisibility(View.GONE);
        binding.btnSendCode.setText("Gửi mã xác thực");
    }

    // Hiển thị giao diện nhập mã OTP
    private void showOTPStep() {
        binding.layoutStepEmail.setVisibility(View.GONE);
        binding.layoutStepOTP.setVisibility(View.VISIBLE);
        binding.btnSendCode.setText("Xác thực mã OTP");
    }

    @Override
    protected void observeData() {
        // Lắng nghe kết quả từ Server (gửi mail thành công, OTP đúng/sai)
    }
}
