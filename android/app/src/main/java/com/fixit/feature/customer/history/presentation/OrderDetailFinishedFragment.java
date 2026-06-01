package com.fixit.feature.customer.history.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentOrderDetailFinishedBinding;
import com.fixit.feature.customer.review.presentation.CustomerReviewDialog;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * FILE ĐIỀU KHIỂN CHI TIẾT ĐƠN HÀNG ĐÃ HOÀN THÀNH
 * Mục đích: Hiển thị thông tin chi tiết của một đơn hàng sau khi thợ đã sửa xong.
 */
@AndroidEntryPoint
public class OrderDetailFinishedFragment extends BaseFragment<FragmentOrderDetailFinishedBinding>
        implements CustomerReviewDialog.OnReviewSubmittedListener {

    private String orderId;
    private String workerId;
    private String workerName;

    @NonNull
    @Override
    protected FragmentOrderDetailFinishedBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        // Kết nối file giao diện fragment_order_detail_finished.xml với code Java này
        return FragmentOrderDetailFinishedBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        if (getArguments() != null) {
            orderId = getArguments().getString("orderId");
            workerId = getArguments().getString("workerId");
            workerName = getArguments().getString("workerName", "Thợ");
        }

        // Cài đặt nút quay lại
        binding.ivBack.setOnClickListener(v -> {
            if (navController != null) {
                navController.popBackStack();
            }
        });

        // Hiển thị tên thợ lên thẻ thông tin thợ
        binding.tvWorkerName.setText(workerName);
        
        // Sự kiện khi bấm nút "Đánh giá ngay"
        binding.btnRate.setOnClickListener(v -> {
            if (orderId != null) {
                CustomerReviewDialog dialog = CustomerReviewDialog.newInstance(orderId);
                // Hiển thị dialog từ fragment con để nhận callback OnReviewSubmittedListener chính xác
                dialog.show(getChildFragmentManager(), "CustomerReviewDialog");
            } else {
                Toast.makeText(requireContext(), "Không tìm thấy thông tin mã đơn hàng!", Toast.LENGTH_SHORT).show();
            }
        });

        // Sự kiện khi bấm vào thẻ thông tin thợ để xem Hồ sơ công khai
        binding.cardWorkerInfo.setOnClickListener(v -> {
            if (workerId != null) {
                Bundle args = new Bundle();
                args.putString("workerId", workerId);
                args.putString("workerName", workerName);
                if (navController != null) {
                    navController.navigate(R.id.nav_customer_worker_public_profile, args);
                }
            } else {
                Toast.makeText(requireContext(), "Không tìm thấy thông tin thợ sửa chữa!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void observeData() {
        // Lấy dữ liệu chi tiết đơn hàng từ Server để hiển thị (tùy chọn)
    }

    @Override
    public void onReviewSubmitted() {
        // Sau khi khách hàng gửi đánh giá thành công, vô hiệu hóa nút bấm
        binding.btnRate.setEnabled(false);
        binding.btnRate.setText("✓ Đã đánh giá");
    }
}
