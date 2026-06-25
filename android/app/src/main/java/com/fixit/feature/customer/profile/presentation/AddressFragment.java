package com.fixit.feature.customer.profile.presentation;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import dagger.hilt.android.AndroidEntryPoint;

// Màn hình (Tivi) hiển thị Sổ địa chỉ
@AndroidEntryPoint
public class AddressFragment extends Fragment {

    private AddressViewModel viewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bắt sóng đài AddressViewModel
        viewModel = new ViewModelProvider(this).get(AddressViewModel.class);

        observeViewModel();
        
        // Mở app lên là tự động chạy load dữ liệu
        viewModel.loadAddresses();
    }

    private void observeViewModel() {
        // Cắm ăng-ten vào Kênh Danh sách địa chỉ
        viewModel.getAddressesData().observe(getViewLifecycleOwner(), addresses -> {
            if (addresses != null && !addresses.isEmpty()) {
                // Thành công! Ở bước code Layout (Giao diện) thực tế, 
                // bạn sẽ lôi danh sách này ném vào RecyclerView Adapter để in ra 1 list.
                // Tạm thời hiển thị Toast báo thành công:
                Toast.makeText(getContext(), "Tải thành công " + addresses.size() + " địa chỉ!", Toast.LENGTH_SHORT).show();
            }
        });

        // Cắm ăng-ten vào Kênh Lỗi
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), "LỖI RỒI: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
