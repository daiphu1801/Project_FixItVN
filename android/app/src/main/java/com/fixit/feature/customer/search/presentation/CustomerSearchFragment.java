package com.fixit.feature.customer.search.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerSearchBinding;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * FILE ĐIỀU KHIỂN GIAO DIỆN TÌM KIẾM (CUSTOMER SEARCH FRAGMENT)
 * Mục đích: Quản lý các hành động của người dùng tại màn hình tìm kiếm.
 */
@AndroidEntryPoint
public class CustomerSearchFragment extends BaseFragment<FragmentCustomerSearchBinding> {

    // Hàm này giúp kết nối file giao diện XML (fragment_customer_search.xml) với code Java này
    @NonNull
    @Override
    protected FragmentCustomerSearchBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentCustomerSearchBinding.inflate(inflater, container, false);
    }

    // Nơi thực hiện các cài đặt ban đầu cho giao diện Tìm kiếm 
    // Ví dụ: Bắt sự kiện khi người dùng gõ từ khóa vào thanh tìm kiếm.
    @Override
    protected void setupViews() {
        // Sự kiện khi nhấn nút Quay lại (Back)
        binding.btnBack.setOnClickListener(v -> {
            if (navController != null) {
                navController.popBackStack();
            }
        });
    }

    // Nơi nhận dữ liệu từ ViewModel để cập nhật lên màn hình
    // Ví dụ: Hiển thị danh sách các thợ sửa chữa tìm thấy được.
    @Override
    protected void observeData() {
        // Code cập nhật danh sách tìm kiếm sẽ viết ở đây.
    }
}
