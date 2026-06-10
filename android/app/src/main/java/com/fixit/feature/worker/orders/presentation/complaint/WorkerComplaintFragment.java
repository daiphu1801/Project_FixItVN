package com.fixit.feature.worker.orders.presentation.complaint;

import com.fixit.feature.worker.orders.presentation.WorkerOrdersViewModel;

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
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.fixit.core.ui.BaseFragment;
import com.fixit.feature.worker.orders.domain.model.WorkerOrder;
import com.fixit.databinding.FragmentWorkerComplaintBinding;
import com.fixit.feature.upload.domain.model.UploadPurpose;
import com.fixit.feature.upload.presentation.UploadViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerComplaintFragment extends BaseFragment<FragmentWorkerComplaintBinding> {

    private WorkerOrdersViewModel viewModel;
    private UploadViewModel uploadViewModel;
    private String orderId;

    // Image picker cho ảnh bằng chứng
    private final ActivityResultLauncher<String> pickEvidenceLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    addEvidenceImageToUI(uri);
                    uploadViewModel.upload(requireContext(), uri, UploadPurpose.COMPLAINT_EVIDENCE);
                }
            }
    );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(WorkerOrdersViewModel.class);
        uploadViewModel = new ViewModelProvider(this).get(UploadViewModel.class);
    }

    @Override
    protected FragmentWorkerComplaintBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerComplaintBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        if (getArguments() != null) {
            orderId = getArguments().getString("orderId");
        }

        // Nút thêm ảnh bằng chứng → mở gallery
        binding.btnAddEvidence.setOnClickListener(v -> pickEvidenceLauncher.launch("image/*"));

        binding.btnSubmitResponse.setOnClickListener(v -> {
            String response = binding.etResponse.getText().toString();
            if (response.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập giải trình của bạn", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // TODO: Gửi response + danh sách ảnh bằng chứng đã upload lên API
            // uploadViewModel.getConfirmedFileUrls() chứa danh sách URL ảnh
            Toast.makeText(requireContext(), "Đã gửi phản hồi thành công", Toast.LENGTH_SHORT).show();
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        // Setup Toolbar
        View btnBack = requireView().findViewById(com.fixit.R.id.btnBack);
        if (btnBack != null) {
            btnBack.setVisibility(View.VISIBLE);
            btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        }
        
        android.widget.TextView tvTitle = requireView().findViewById(com.fixit.R.id.tvToolbarTitle);
        if (tvTitle != null) tvTitle.setText("Chi tiết khiếu nại");
    }

    @Override
    protected void observeData() {
        viewModel.orderDetails.observe(getViewLifecycleOwner(), order -> {
            if (order != null) {
                binding.tvOrderTitle.setText("📦 Đơn #" + order.getOrderId() + " – " + order.getServiceTitle());
                binding.tvComplaintReason.setText(order.getComplaintReason());
                binding.tvCountdown.setText(order.getComplaintDeadline());
                binding.tvFrozenAmount.setText(order.getPrice());
            }
        });

        if (orderId != null) {
            viewModel.loadOrderDetails(orderId);
        }

        // Observe kết quả upload ảnh bằng chứng
        uploadViewModel.uploadResult.observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            if (result.isSuccess()) {
                Toast.makeText(requireContext(), "Ảnh bằng chứng đã tải lên", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), result.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Thêm thumbnail ảnh bằng chứng vào danh sách ngang.
     */
    private void addEvidenceImageToUI(Uri uri) {
        binding.hsvEvidenceImages.setVisibility(View.VISIBLE);

        ImageView imageView = new ImageView(requireContext());
        int size = (int) (80 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.setMarginEnd((int) (8 * getResources().getDisplayMetrics().density));
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Bo góc bằng Glide transform
        Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(imageView);

        binding.llEvidenceImages.addView(imageView);
    }
}
