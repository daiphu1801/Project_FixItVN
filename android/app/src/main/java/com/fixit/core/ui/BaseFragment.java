package com.fixit.core.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewbinding.ViewBinding;
import android.widget.Toast;

/**
 * ĐÂY LÀ FILE LỚP NỀN (BASE) - NƠI CHỨA CÁC CẤU HÌNH DÙNG CHUNG CHO TẤT CẢ MÀN HÌNH
 * Mục đích: Giúp bạn không phải viết lại code khởi tạo NavController hay ViewBinding ở mỗi trang.
 */
public abstract class BaseFragment<VB extends ViewBinding> extends Fragment {

    // Biến _binding để quản lý bộ nhớ, tránh rò rỉ dữ liệu (Memory Leak)
    private VB _binding;
    
    // Biến binding này để bạn truy cập các thành phần giao diện (nút bấm, text...) trong XML
    protected VB binding;
    
    // Biến navController này là 'vô lăng' để điều khiển việc chuyển màn hình
    protected NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Tự động kết nối file XML với code Java thông qua ViewBinding
        _binding = inflateViewBinding(inflater, container);
        binding = _binding;
        // Trả về View gốc của Fragment để hiển thị lên màn hình
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // KHỞI TẠO BỘ ĐIỀU HƯỚNG: Lấy 'vô lăng' từ hệ thống để sẵn sàng sử dụng
        // Nhờ dòng này mà ở các lớp con (như HomeFragment) bạn chỉ cần gọi navController là dùng được luôn
        navController = Navigation.findNavController(view);
        
        // Gọi 2 hàm khung để lớp con điền logic vào
        setupViews();
        observeData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Hủy liên kết binding khi màn hình đóng lại để giải phóng bộ nhớ cho điện thoại
        _binding = null;
        binding = null;
    }

    // Các hàm abstract này ép buộc các lớp con phải triển khai theo đúng cấu trúc sạch sẽ
    protected abstract VB inflateViewBinding(LayoutInflater inflater, ViewGroup container);
    protected abstract void setupViews();
    protected abstract void observeData();

    protected void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    protected void showError(String message) {
        if (message != null) {
            String lowercaseMsg = message.toLowerCase();
            if (lowercaseMsg.contains("noconnectivityexception")
                    || lowercaseMsg.contains("failed to connect")
                    || lowercaseMsg.contains("unknownhostexception")
                    || lowercaseMsg.contains("connectexception")
                    || lowercaseMsg.contains("sockettimeoutexception")
                    || lowercaseMsg.contains("lỗi kết nối")
                    || lowercaseMsg.contains("timeout")
                    || lowercaseMsg.contains("no route to host")
                    || lowercaseMsg.contains("network")
                    || lowercaseMsg.contains("mất mạng")) {
                message = "Yêu cầu kết nối mạng để thực hiện chức năng này";
            }
        }
        if (getView() != null && getContext() != null) {
            com.google.android.material.snackbar.Snackbar snackbar = 
                    com.google.android.material.snackbar.Snackbar.make(getView(), message, com.google.android.material.snackbar.Snackbar.LENGTH_LONG);
            snackbar.setBackgroundTint(android.graphics.Color.parseColor("#E11D48")); // Rose 600 (High-end error red)
            snackbar.setTextColor(android.graphics.Color.WHITE);
            snackbar.show();
        } else {
            showToast(message);
        }
    }

    protected void showSuccess(String message) {
        if (getView() != null && getContext() != null) {
            com.google.android.material.snackbar.Snackbar snackbar = 
                    com.google.android.material.snackbar.Snackbar.make(getView(), message, com.google.android.material.snackbar.Snackbar.LENGTH_LONG);
            snackbar.setBackgroundTint(android.graphics.Color.parseColor("#10B981")); // Emerald 500 (High-end success green)
            snackbar.setTextColor(android.graphics.Color.WHITE);
            snackbar.show();
        } else {
            showToast(message);
        }
    }

    protected void showWarningDialog(String title, String message) {
        if (getContext() != null) {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
                    .setIcon(com.fixit.R.drawable.ic_lucide_alert_circle)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Đồng ý", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    protected void showSuccessDialog(String title, String message) {
        if (getContext() != null) {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
                    .setIcon(com.fixit.R.drawable.ic_lucide_check_circle)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Tuyệt vời", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }
}
