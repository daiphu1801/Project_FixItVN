package com.fixit.ui.worker.profile;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.navigation.Navigation;

import com.fixit.base.BaseFragment;
import com.fixit.databinding.FragmentChangePasswordBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChangePasswordFragment extends BaseFragment<FragmentChangePasswordBinding> {

    @Override
    protected FragmentChangePasswordBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentChangePasswordBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Cài đặt Toolbar
        binding.appBarLayout.toolbar.setTitle("Đổi mật khẩu");
        binding.appBarLayout.toolbar.setNavigationOnClickListener(v -> 
                Navigation.findNavController(v).navigateUp());

        // Xử lý nút cập nhật
        binding.btnUpdatePassword.setOnClickListener(v -> {
            String currentPass = binding.etCurrentPassword.getText().toString().trim();
            String newPass = binding.etNewPassword.getText().toString().trim();
            String confirmPass = binding.etConfirmPassword.getText().toString().trim();

            if (validatePasswords(currentPass, newPass, confirmPass)) {
                // TODO: Gọi API đổi mật khẩu
                Toast.makeText(requireContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigateUp();
            }
        });
    }

    private boolean validatePasswords(String current, String newPass, String confirm) {
        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (newPass.length() < 8) {
            Toast.makeText(requireContext(), "Mật khẩu mới phải có ít nhất 8 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPass.equals(confirm)) {
            Toast.makeText(requireContext(), "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (newPass.equals(current)) {
            Toast.makeText(requireContext(), "Mật khẩu mới không được trùng với mật khẩu cũ", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    protected void observeData() {
        // ViewModel logic if needed
    }
}
