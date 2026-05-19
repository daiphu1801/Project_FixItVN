package com.fixit.feature.customer.home.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerHomeBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CustomerHomeFragment extends BaseFragment<FragmentCustomerHomeBinding> {

    @NonNull
    @Override
    protected FragmentCustomerHomeBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentCustomerHomeBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        /**
         * --- PHẦN 1: THIẾT LẬP DANH SÁCH DỊCH VỤ ---
         */
        
        // Thiết lập tên và icon cho từng dịch vụ trong lưới (Grid)
        binding.itemWashingMachine.tvServiceName.setText("Máy giặt");
        binding.itemWashingMachine.ivServiceIcon.setImageResource(R.drawable.ic_lucide_radar); // Tạm thời dùng icon radar

        binding.itemFridge.tvServiceName.setText("Tủ lạnh");
        binding.itemFridge.ivServiceIcon.setImageResource(R.drawable.ic_lucide_radar);

        binding.itemAirConditioner.tvServiceName.setText("Điều hòa");
        binding.itemAirConditioner.ivServiceIcon.setImageResource(R.drawable.ic_lucide_radar);

        binding.itemElectricity.tvServiceName.setText("Điện nước");
        binding.itemElectricity.ivServiceIcon.setImageResource(R.drawable.ic_lucide_radar);

        binding.itemPlumbing.tvServiceName.setText("Thông nghẹt");
        binding.itemPlumbing.ivServiceIcon.setImageResource(R.drawable.ic_lucide_radar);

        binding.itemFan.tvServiceName.setText("Quạt điện");
        binding.itemFan.ivServiceIcon.setImageResource(R.drawable.ic_lucide_radar);

        binding.itemStove.tvServiceName.setText("Bếp ga/từ");
        binding.itemStove.ivServiceIcon.setImageResource(R.drawable.ic_lucide_radar);

        binding.itemOther.tvServiceName.setText("Xem thêm");
        binding.itemOther.ivServiceIcon.setImageResource(R.drawable.ic_lucide_menu);

        /**
         * --- PHẦN 2: XỬ LÝ SỰ KIỆN CLICK ĐỂ TEST ---
         */

        // Khi ấn vào bất kỳ dịch vụ nào cũng sẽ chuyển sang màn hình Tìm thợ (Radar) để test
        binding.itemWashingMachine.getRoot().setOnClickListener(v -> navigateToFindingWorker("Sửa máy giặt"));
        binding.itemFridge.getRoot().setOnClickListener(v -> navigateToFindingWorker("Sửa tủ lạnh"));
        binding.itemAirConditioner.getRoot().setOnClickListener(v -> navigateToFindingWorker("Sửa điều hòa"));
        binding.itemElectricity.getRoot().setOnClickListener(v -> navigateToFindingWorker("Điện nước"));
        binding.itemPlumbing.getRoot().setOnClickListener(v -> navigateToFindingWorker("Thông nghẹt"));
        binding.itemFan.getRoot().setOnClickListener(v -> navigateToFindingWorker("Quạt điện"));
        binding.itemStove.getRoot().setOnClickListener(v -> navigateToFindingWorker("Bếp ga/từ"));
        
        // Ô "Xem thêm" vẫn cho qua trang tìm kiếm chung
        binding.itemOther.getRoot().setOnClickListener(v -> {
            if (navController != null) {
                navController.navigate(R.id.nav_customer_search);
            }
        });

        // Ô chọn dịch vụ phía trên (cardLocation)
        binding.cardLocation.setOnClickListener(v -> {
            if (navController != null) {
                navController.navigate(R.id.nav_customer_search);
            }
        });

        // Icon Chat ở góc phải header → mở danh sách tin nhắn
        binding.ivChatIcon.setOnClickListener(v -> {
            if (navController != null) {
                navController.navigate(R.id.nav_customer_list_msg);
            }
        });
    }

    private void navigateToFindingWorker(String serviceName) {
        // 1. Cập nhật tên dịch vụ lên ô hiển thị phía trên để người dùng thấy
        binding.tvLocationValue.setText(serviceName);
        
        // 2. Chuyển sang màn hình Đặt thợ (Booking)
        if (navController != null) {
            navController.navigate(R.id.nav_customer_booking);
        }
    }

    @Override
    protected void observeData() {
        // Observe viewmodel
    }
}
