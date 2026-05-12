package com.fixit.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.fixit.databinding.FragmentForgotpasswordBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ForgotPasswordFragment extends Fragment {

    private FragmentForgotpasswordBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentForgotpasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> {
            if (binding.layoutStepOTP.getVisibility() == View.VISIBLE) {
                // Nếu đang ở bước OTP, nhấn Back sẽ quay lại bước Email
                showEmailStep();
            } else {
                // Nếu đang ở bước Email, nhấn Back sẽ thoát màn hình
                Navigation.findNavController(v).navigateUp();
            }
        });

        binding.btnSendCode.setOnClickListener(v -> {
            if (binding.layoutStepEmail.getVisibility() == View.VISIBLE) {
                // Giả lập gửi mã thành công, chuyển sang bước OTP
                showOTPStep();
            } else {
                // Xử lý xác thực mã OTP
            }
        });
    }

    private void showEmailStep() {
        binding.layoutStepEmail.setVisibility(View.VISIBLE);
        binding.layoutStepOTP.setVisibility(View.GONE);
        binding.btnSendCode.setText("Gửi mã xác thực");
    }

    private void showOTPStep() {
        binding.layoutStepEmail.setVisibility(View.GONE);
        binding.layoutStepOTP.setVisibility(View.VISIBLE);
        binding.btnSendCode.setText("Xác thực mã OTP");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
