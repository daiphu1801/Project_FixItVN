package com.fixit.feature.customer.order.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerOrderDetailBinding;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CustomerOrderDetailFragment extends BaseFragment<FragmentCustomerOrderDetailBinding> {

    @NonNull
    @Override
    protected FragmentCustomerOrderDetailBinding inflateViewBinding(@NonNull LayoutInflater inflater,
            ViewGroup container) {
        return FragmentCustomerOrderDetailBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        binding.ivClose.setOnClickListener(v -> {
            if (navController != null) {
                navController.popBackStack();
                
            }
        });

        binding.btnCancelOrder.setOnClickListener(v -> {
            if (navController != null) {
                navController.navigate(R.id.nav_customer_cancel_order);
            }
        });
           binding.btnMessage.setOnClickListener(v -> {
            android.os.Bundle args = new android.os.Bundle();
            // Do đây là bản mẫu thử nghiệm (MVP Mock Data), chúng ta sẽ truyền thông tin Thợ mẫu
            args.putString("workerId", "worker_tuan_123"); // ID Thợ mẫu
            args.putString("workerName", "Anh Tuấn - Thợ Điện"); // Tên Thợ mẫu
            
            if (navController != null) {
                // Điều hướng sang màn hình Chat chi tiết của Khách hàng
                navController.navigate(R.id.nav_customer_chat, args);
            }
        });

        // Other bindings for Technician info, etc.
    }

    @Override
    protected void observeData() {
    }
}
