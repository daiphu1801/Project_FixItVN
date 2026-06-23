package com.fixit.feature.customer.complaint.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerComplaintDetailBinding;
import com.fixit.feature.customer.complaint.domain.model.Complaint;

import dagger.hilt.android.AndroidEntryPoint;
import java.util.List;

@AndroidEntryPoint
public class CustomerComplaintDetailFragment extends BaseFragment<FragmentCustomerComplaintDetailBinding> {

    private CustomerComplaintViewModel viewModel;
    private String bookingId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CustomerComplaintViewModel.class);
    }

    @NonNull
    @Override
    protected FragmentCustomerComplaintDetailBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentCustomerComplaintDetailBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        if (getArguments() != null) {
            bookingId = getArguments().getString("bookingId");
        }

        // Toolbar Back click
        binding.ivBack.setOnClickListener(v -> {
            if (navController != null) navController.popBackStack();
        });

        // Cancel complaint button click
        binding.btnCancelComplaint.setOnClickListener(v -> {
            if (viewModel.complaint.getValue() != null) {
                String complaintId = viewModel.complaint.getValue().getId();
                viewModel.cancelComplaint(bookingId, complaintId);
            }
        });

        // Load data
        if (bookingId != null) {
            viewModel.loadBookingComplaint(bookingId);
        }
    }

    @Override
    protected void observeData() {
        // Theo dõi chi tiết khiếu nại
        viewModel.complaint.observe(getViewLifecycleOwner(), complaint -> {
            if (complaint == null) {
                binding.tvCustomerReason.setText("Không có nội dung khiếu nại.");
                binding.hsvCustomerEvidences.setVisibility(View.GONE);
                binding.layoutWorkerResponse.setVisibility(View.GONE);
                binding.layoutBottom.setVisibility(View.GONE);
                return;
            }

            displayComplaintDetails(complaint);
        });

        // Theo dõi kết quả hủy khiếu nại
        viewModel.cancelResult.observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.isSuccess()) {
                Toast.makeText(requireContext(), "Đã rút khiếu nại thành công!", Toast.LENGTH_SHORT).show();
                if (navController != null) navController.popBackStack();
            } else {
                String error = result.getError() != null ? result.getError().getMessage() : "Có lỗi xảy ra";
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Trạng thái tải trang
        viewModel.isLoading.observe(getViewLifecycleOwner(), loading -> {
            if (binding.layoutLoading != null) {
                binding.layoutLoading.getRoot().setVisibility(loading ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void displayComplaintDetails(Complaint complaint) {
        // Cài đặt nội dung
        binding.tvCustomerReason.setText(complaint.getCustomerReason());

        // Hiển thị danh sách ảnh bằng chứng của khách hàng
        binding.llCustomerEvidences.removeAllViews();
        List<String> customerUrls = complaint.getCustomerEvidenceUrls();
        if (customerUrls != null && !customerUrls.isEmpty()) {
            binding.hsvCustomerEvidences.setVisibility(View.VISIBLE);
            for (String url : customerUrls) {
                addEvidenceImageToLayout(binding.llCustomerEvidences, url);
            }
        } else {
            binding.hsvCustomerEvidences.setVisibility(View.GONE);
        }

        // Cập nhật trạng thái hiển thị
        String status = complaint.getStatus();
        if ("Pending".equalsIgnoreCase(status)) {
            binding.tvStatus.setText("Chờ thợ phản hồi");
            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#F59E0B")); // Gold
            binding.tvStatusDesc.setText("Thợ đang trong thời gian giải trình tranh chấp.");
            binding.layoutWorkerResponse.setVisibility(View.GONE);
            binding.layoutBottom.setVisibility(View.VISIBLE);
        } else if ("Worker_Responded".equalsIgnoreCase(status)) {
            binding.tvStatus.setText("Thợ đã giải trình");
            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#EA580C")); // Orange
            binding.tvStatusDesc.setText("Hệ thống (Admin) đang xem xét bằng chứng của hai bên.");
            
            // Hiển thị phần giải trình của thợ
            binding.layoutWorkerResponse.setVisibility(View.VISIBLE);
            binding.tvWorkerResponse.setText(complaint.getWorkerResponse());

            // Ảnh giải trình của thợ
            binding.llWorkerEvidences.removeAllViews();
            List<String> workerUrls = complaint.getWorkerEvidenceUrls();
            if (workerUrls != null && !workerUrls.isEmpty()) {
                binding.hsvWorkerEvidences.setVisibility(View.VISIBLE);
                for (String url : workerUrls) {
                    addEvidenceImageToLayout(binding.llWorkerEvidences, url);
                }
            } else {
                binding.hsvWorkerEvidences.setVisibility(View.GONE);
            }
            binding.layoutBottom.setVisibility(View.VISIBLE);
        } else if ("Resolved".equalsIgnoreCase(status)) {
            binding.tvStatus.setText("Đã giải quyết");
            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#10B981")); // Green
            binding.tvStatusDesc.setText("Khiếu nại này đã được đóng.");
            
            binding.layoutWorkerResponse.setVisibility(View.VISIBLE);
            binding.tvWorkerResponse.setText(complaint.getWorkerResponse() != null ? complaint.getWorkerResponse() : "Đã hoàn tất xử lý.");
            binding.hsvWorkerEvidences.setVisibility(View.GONE);
            binding.layoutBottom.setVisibility(View.GONE);
        } else {
            binding.tvStatus.setText(status);
            binding.tvStatus.setTextColor(android.graphics.Color.GRAY);
            binding.tvStatusDesc.setText("Trạng thái không xác định.");
            binding.layoutWorkerResponse.setVisibility(View.GONE);
            binding.layoutBottom.setVisibility(View.GONE);
        }
    }

    private void addEvidenceImageToLayout(LinearLayout layout, String url) {
        ImageView imageView = new ImageView(requireContext());
        int size = (int) (80 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.setMarginEnd((int) (8 * getResources().getDisplayMetrics().density));
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Glide.with(this)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.ic_lucide_image)
                .into(imageView);

        layout.addView(imageView);
    }
}
