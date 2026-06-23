package com.fixit.feature.customer.complaint.presentation;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerComplaintCreateBinding;
import com.fixit.feature.upload.presentation.UploadViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CustomerComplaintCreateFragment extends BaseFragment<FragmentCustomerComplaintCreateBinding> {

    private CustomerComplaintViewModel viewModel;
    private UploadViewModel uploadViewModel;
    private String bookingId;

    // Image picker for evidence photo selection
    private final ActivityResultLauncher<String> pickEvidenceLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    addEvidenceImageToUI(uri);
                    // Upload to Cloudinary with purpose "COMPLAINT_EVIDENCE"
                    uploadViewModel.upload(requireContext(), uri, "COMPLAINT_EVIDENCE");
                }
            }
    );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CustomerComplaintViewModel.class);
        uploadViewModel = new ViewModelProvider(this).get(UploadViewModel.class);
    }

    @NonNull
    @Override
    protected FragmentCustomerComplaintCreateBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentCustomerComplaintCreateBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        if (getArguments() != null) {
            bookingId = getArguments().getString("bookingId");
        }

        if (bookingId != null) {
            binding.tvOrderTitle.setText("📦 Đơn hàng #" + (bookingId.length() > 8 ? bookingId.substring(0, 8) : bookingId));
            
            // Cài đặt thông tin hiển thị mock dựa trên bookingId
            if ("ORD_PENDING_123".equals(bookingId)) {
                binding.tvServiceTitle.setText("Dịch vụ: Sửa đường ống nước");
                binding.tvPrice.setText("Giá trị: 350.000đ");
            } else if ("ORD_RESPONDED_123".equals(bookingId)) {
                binding.tvServiceTitle.setText("Dịch vụ: Sửa chữa điều hòa");
                binding.tvPrice.setText("Giá trị: 500.000đ");
            } else {
                binding.tvServiceTitle.setText("Dịch vụ: Sửa chữa thiết bị gia dụng");
                binding.tvPrice.setText("Giá trị: 250.000đ");
            }
        }

        // Toolbar Back click
        binding.ivBack.setOnClickListener(v -> {
            if (navController != null) navController.popBackStack();
        });

        // Add photo button click
        binding.btnAddEvidence.setOnClickListener(v -> pickEvidenceLauncher.launch("image/*"));

        // Submit button click
        binding.btnSubmit.setOnClickListener(v -> {
            String reason = binding.etReason.getText().toString().trim();
            if (reason.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập lý do khiếu nại của bạn", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gửi dữ liệu khiếu nại lên ViewModel (bao gồm cả danh sách URL ảnh đã tải lên thành công)
            viewModel.createComplaint(bookingId, reason, uploadViewModel.getConfirmedFileUrls());
        });
    }

    @Override
    protected void observeData() {
        // Theo dõi kết quả tạo khiếu nại
        viewModel.createResult.observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.isSuccess()) {
                Toast.makeText(requireContext(), "Gửi khiếu nại thành công!", Toast.LENGTH_SHORT).show();
                uploadViewModel.clearConfirmedUploads();
                if (navController != null) {
                    navController.popBackStack();
                }
            } else {
                String error = result.getError() != null ? result.getError().getMessage() : "Có lỗi xảy ra";
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Trạng thái loading của form
        viewModel.isLoading.observe(getViewLifecycleOwner(), loading -> {
            if (binding.layoutLoading != null) {
                binding.layoutLoading.getRoot().setVisibility(loading ? View.VISIBLE : View.GONE);
            }
        });

        // Trạng thái upload ảnh
        uploadViewModel.uploadResult.observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.isSuccess()) {
                Toast.makeText(requireContext(), "Tải ảnh bằng chứng thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), result.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Thêm thumbnail ảnh đã chọn vào giao diện danh sách ngang
     */
    private void addEvidenceImageToUI(Uri uri) {
        binding.hsvEvidenceImages.setVisibility(View.VISIBLE);

        ImageView imageView = new ImageView(requireContext());
        int size = (int) (80 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.setMarginEnd((int) (8 * getResources().getDisplayMetrics().density));
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Bo góc ảnh
        Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(imageView);

        binding.llEvidenceImages.addView(imageView);
    }
}
