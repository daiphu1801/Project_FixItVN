package com.fixit.feature.customer.order.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerCancelOrderBinding;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CustomerCancelOrderFragment extends BaseFragment<FragmentCustomerCancelOrderBinding> {

    @NonNull
    @Override
    protected FragmentCustomerCancelOrderBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentCustomerCancelOrderBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        binding.ivBack.setOnClickListener(v -> {
            if (navController != null) {
                navController.popBackStack();
            }
        });

        binding.btnKeepOrder.setOnClickListener(v -> {
            if (navController != null) {
                navController.popBackStack();
            }
        });

        // Clear worker reason if customer reason is picked
        binding.rgReasonCustomer.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                binding.rgReasonWorker.clearCheck();
            }
        });

        // Clear customer reason if worker reason is picked
        binding.rgReasonWorker.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                binding.rgReasonCustomer.clearCheck();
            }
        });

        binding.btnConfirmCancel.setOnClickListener(v -> {
            int customerCheckedId = binding.rgReasonCustomer.getCheckedRadioButtonId();
            int workerCheckedId = binding.rgReasonWorker.getCheckedRadioButtonId();

            if (customerCheckedId != -1) {
                // THAY ĐỔI TỪ PHÍA TÔI -> Quay về Trang chủ
                Toast.makeText(requireContext(), "Đã hủy đơn thành công", Toast.LENGTH_SHORT).show();
                if (navController != null) {
                    navController.navigate(R.id.nav_customer_home);
                }
            } else if (workerCheckedId != -1) {
                // VẤN ĐỀ TỪ PHÍA THỢ -> Quay về màn hình Finding Worker (Radar)
                Toast.makeText(requireContext(), "Đang tìm thợ khác cho bạn", Toast.LENGTH_SHORT).show();
                if (navController != null) {
                    navController.navigate(R.id.nav_customer_finding_worker);
                }
            } else {
                Toast.makeText(requireContext(), "Vui lòng chọn lý do hủy", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void observeData() {
    }
}
