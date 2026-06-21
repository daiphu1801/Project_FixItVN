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

        // Open Worker Public Profile when clicking on avatar or name
        binding.ivTechnicianAvatar.setOnClickListener(v -> navigateToWorkerProfile());
        binding.tvTechnicianName.setOnClickListener(v -> navigateToWorkerProfile());
    }

    private void navigateToWorkerProfile() {
        if (navController != null) {
            android.os.Bundle args = new android.os.Bundle();
            // TODO: Replace with real worker ID from order details when API is integrated
            args.putString("workerId", com.fixit.core.common.Constants.DEBUG_WORKER_ID);
            navController.navigate(R.id.nav_worker_public_profile, args);
        }
    }

    @Override
    protected void observeData() {
    }
}
