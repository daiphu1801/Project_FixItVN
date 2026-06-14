package com.fixit.feature.worker.kyc.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentWorkerKycUploadingBinding;
import com.fixit.feature.upload.domain.model.QueuedUpload;
import com.fixit.feature.upload.presentation.UploadViewModel;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerKycUploadingFragment extends BaseFragment<FragmentWorkerKycUploadingBinding> {

    private UploadViewModel uploadViewModel;
    private String activeGroupId = null;

    @Override
    protected FragmentWorkerKycUploadingBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerKycUploadingBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        uploadViewModel = new ViewModelProvider(this).get(UploadViewModel.class);

        binding.appBarLayout.toolbar.setTitle("Đang gửi hồ sơ");
        // Không cho back bằng nút quay lại để tránh ngắt quãng trạng thái UI, 
        // nhưng user vẫn có thể rời đi qua các nút khác hoặc back button của hệ thống nếu muốn.
        binding.appBarLayout.toolbar.setNavigationIcon(null);

        binding.btnRetryUpload.setOnClickListener(v -> {
            if (activeGroupId != null) {
                binding.cardError.setVisibility(View.GONE);
                binding.btnRetryUpload.setVisibility(View.GONE);
                binding.btnCancelUpload.setVisibility(View.GONE);
                binding.progressUploading.setVisibility(View.VISIBLE);
                
                uploadViewModel.retryKycGroup(activeGroupId);
            }
        });

        binding.btnCancelUpload.setOnClickListener(v -> {
            if (activeGroupId != null) {
                uploadViewModel.cancelKycGroup(activeGroupId);
            }
        });
    }

    @Override
    protected void observeData() {
        // Lắng nghe danh sách chi tiết tiến trình upload các ảnh
        uploadViewModel.kycUploadsList.observe(getViewLifecycleOwner(), this::updateUploadsUi);

        // Lắng nghe tín hiệu khi không còn pending upload trong DB -> thành công
        uploadViewModel.kycUploadDone.observe(getViewLifecycleOwner(), done -> {
            if (Boolean.TRUE.equals(done)) {
                if (isAdded() && isResumed()) {
                    Navigation.findNavController(requireView())
                            .navigate(R.id.workerKycPendingFragment);
                }
            }
        });

        // Lắng nghe tín hiệu khi người dùng hủy bỏ
        uploadViewModel.kycUploadCancelled.observe(getViewLifecycleOwner(), cancelled -> {
            if (Boolean.TRUE.equals(cancelled)) {
                if (isAdded() && isResumed()) {
                    Navigation.findNavController(requireView()).navigateUp();
                }
            }
        });
    }

    private void updateUploadsUi(List<QueuedUpload> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        boolean hasError = false;
        String errorMessage = null;

        for (QueuedUpload upload : list) {
            if (upload.getGroupId() != null) {
                this.activeGroupId = upload.getGroupId();
            }
            String status = upload.getStatus();
            if (status.endsWith("_FAILED") || upload.getRetryCount() >= 10) {
                hasError = true;
                if (upload.getLastError() != null) {
                    errorMessage = upload.getLastError();
                }
            }
        }

        updateSlotUi("front", list);
        updateSlotUi("back", list);
        updateSlotUi("certificate", list);

        if (hasError) {
            binding.progressUploading.setVisibility(View.GONE);
            binding.cardError.setVisibility(View.VISIBLE);
            binding.btnRetryUpload.setVisibility(View.VISIBLE);
            binding.btnCancelUpload.setVisibility(View.VISIBLE);
            if (errorMessage != null) {
                binding.tvErrorMessage.setText("Tải lên thất bại: " + errorMessage);
            } else {
                binding.tvErrorMessage.setText("Tải lên thất bại. Vui lòng kiểm tra lại kết nối mạng.");
            }
        } else {
            binding.progressUploading.setVisibility(View.VISIBLE);
            binding.cardError.setVisibility(View.GONE);
            binding.btnRetryUpload.setVisibility(View.GONE);
            binding.btnCancelUpload.setVisibility(View.GONE);
        }
    }

    private void updateSlotUi(String slotKey, List<QueuedUpload> list) {
        QueuedUpload upload = null;
        for (QueuedUpload item : list) {
            if (slotKey.equals(item.getSlotKey())) {
                upload = item;
                break;
            }
        }

        android.widget.TextView tvStatus;
        android.widget.ImageView ivStatus;
        if ("front".equals(slotKey)) {
            tvStatus = binding.tvStatusFront;
            ivStatus = binding.ivStatusFront;
        } else if ("back".equals(slotKey)) {
            tvStatus = binding.tvStatusBack;
            ivStatus = binding.ivStatusBack;
        } else {
            tvStatus = binding.tvStatusPortrait;
            ivStatus = binding.ivStatusPortrait;
        }

        if (upload == null) {
            tvStatus.setText("Thành công");
            tvStatus.setTextColor(android.graphics.Color.parseColor("#16a34a"));
            ivStatus.setImageResource(R.drawable.ic_lucide_check_circle);
            ivStatus.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#16a34a")));
            return;
        }

        String status = upload.getStatus();
        if (status.endsWith("_FAILED") || upload.getRetryCount() >= 10) {
            tvStatus.setText("Lỗi tải lên");
            tvStatus.setTextColor(android.graphics.Color.parseColor("#dc2626"));
            ivStatus.setImageResource(R.drawable.ic_lucide_alert_circle);
            ivStatus.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#dc2626")));
        } else if ("CONFIRMED".equals(status) || "CONSUMED".equals(status) || "CLOUDINARY_UPLOADED".equals(status)) {
            tvStatus.setText("Thành công");
            tvStatus.setTextColor(android.graphics.Color.parseColor("#16a34a"));
            ivStatus.setImageResource(R.drawable.ic_lucide_check_circle);
            ivStatus.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#16a34a")));
        } else {
            tvStatus.setText("Đang tải lên...");
            tvStatus.setTextColor(android.graphics.Color.parseColor("#0ea5e9"));
            ivStatus.setImageResource(R.drawable.ic_lucide_timer);
            ivStatus.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#0ea5e9")));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (uploadViewModel != null) {
            uploadViewModel.startKycUploadPolling();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (uploadViewModel != null) {
            uploadViewModel.stopKycUploadPolling();
        }
    }
}
