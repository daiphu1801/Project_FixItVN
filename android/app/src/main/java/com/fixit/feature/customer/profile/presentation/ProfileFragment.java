package com.fixit.feature.customer.profile.presentation;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.fixit.feature.customer.profile.domain.model.CustomerProfile;

import dagger.hilt.android.AndroidEntryPoint;

// CÚ PHÁP: @AndroidEntryPoint
// Ý NGHĨA: Báo cho Android biết đây là một Màn Hình có xài đồ nghề (ViewModel) từ thư viện tiêm phụ thuộc Hilt.
@AndroidEntryPoint

// CÚ PHÁP: public class [Tên] extends Fragment
// Ý NGHĨA: Khai báo File 18 chính là CÁI TIVI (Màn hình hiển thị) của Android.
public class ProfileFragment extends Fragment {

    // Nơi chứa Trạm phát sóng
    private CustomerProfileViewModel viewModel;

    // Các linh kiện trên màn hình (Được kỹ sư giao diện vẽ sẵn bên file XML)
    private TextView txtFullName;
    private ProgressBar progressBar; // Vòng tròn xoay xoay

    // Kế thừa hàm onViewCreated (Khi Tivi vừa được bật điện lên)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // BƯỚC 1: Tìm các linh kiện trên màn hình (Nối dây điện cho Tivi)
        // txtFullName = view.findViewById(R.id.txtFullName);
        // progressBar = view.findViewById(R.id.progressBar);

        // BƯỚC 2: Dò đài để bắt đúng Trạm phát sóng CustomerProfileViewModel
        viewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);

        // BƯỚC 3: CẮM ĂNG-TEN BẮT SÓNG 3 KÊNH (Observe LiveData)
        observeViewModel();

        // BƯỚC 4: Người dùng vừa vào màn hình, tự động vuốt lấy dữ liệu
        viewModel.loadProfile();
    }

    private void observeViewModel() {
        // Cắm ăng-ten vào Kênh 1 (Kênh Dữ liệu)
        // Ý nghĩa: Cứ hễ Trạm phát sóng có phim mới (profile), hàm này lập tức nhảy vào in tên lên màn hình!
        viewModel.getProfileData().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                // txtFullName.setText(profile.getFullName());
                Toast.makeText(getContext(), "Xin chào " + profile.getFullName(), Toast.LENGTH_SHORT).show();
            }
        });

        // Cắm ăng-ten vào Kênh 2 (Kênh Loading)
        // Ý nghĩa: Hễ Kênh 2 phát True -> Hiện vòng xoay. Phát False -> Giấu vòng xoay đi.
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // if (isLoading) progressBar.setVisibility(View.VISIBLE);
            // else progressBar.setVisibility(View.GONE);
        });

        // Cắm ăng-ten vào Kênh 3 (Kênh Lỗi)
        // Ý nghĩa: Hễ rớt mạng, lập tức hiện một cái thông báo nhỏ (Toast) dưới đáy màn hình chửi đổng lên.
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), "CÓ BIẾN: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
