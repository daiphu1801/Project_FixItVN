package com.fixit.feature.worker.kyc.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentWorkerKycUploadingBinding;
import com.fixit.feature.upload.presentation.UploadViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerKycUploadingFragment extends BaseFragment<FragmentWorkerKycUploadingBinding> {

    private UploadViewModel uploadViewModel;

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

        // Nút Về trang chủ hoặc Hủy/Thử lại nếu muốn.
        // Tạm thời nếu đang tải lên thì ẩn nút Thử lại.
        binding.btnRetryUpload.setOnClickListener(v -> {
            binding.cardError.setVisibility(View.GONE);
            binding.btnRetryUpload.setVisibility(View.GONE);
            binding.progressUploading.setVisibility(View.VISIBLE);
            
            // Kích hoạt lại hàng đợi upload
            uploadViewModel.processQueue();
            uploadViewModel.startKycUploadPolling();
        });
    }

    @Override
    protected void observeData() {
        // Lắng nghe tín hiệu khi không còn pending upload trong DB
        uploadViewModel.kycUploadDone.observe(getViewLifecycleOwner(), done -> {
            if (Boolean.TRUE.equals(done)) {
                // Tải xong hoặc đã hết hàng chờ -> chuyển sang màn chờ duyệt của hệ thống
                if (isAdded() && isResumed()) {
                    Navigation.findNavController(requireView())
                            .navigate(R.id.workerKycPendingFragment);
                }
            }
        });
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
