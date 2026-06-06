package com.fixit.feature.worker.kyc.presentation;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentWorkerKycBinding;
import com.fixit.feature.upload.domain.model.UploadPurpose;
import com.fixit.feature.upload.domain.model.UploadTargetType;
import com.fixit.feature.upload.presentation.UploadViewModel;

import java.util.UUID;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerKycFragment extends BaseFragment<FragmentWorkerKycBinding> {

    private UploadViewModel uploadViewModel;

    // Theo dõi trạng thái "đã lưu vào hàng đợi local" của 3 ảnh
    private boolean isFrontQueued = false;
    private boolean isBackQueued = false;
    private boolean isPortraitQueued = false;
    private boolean isSubmitting = false;
    private final String kycGroupId = UUID.randomUUID().toString();

    // 3 launcher cho 3 loại ảnh KYC
    private final ActivityResultLauncher<String> pickFrontLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    displayImage(uri, "front");
                    enqueueKycImage(uri, UploadPurpose.WORKER_KYC_FRONT, "front");
                }
            });

    private final ActivityResultLauncher<String> pickBackLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    displayImage(uri, "back");
                    enqueueKycImage(uri, UploadPurpose.WORKER_KYC_BACK, "back");
                }
            });

    private final ActivityResultLauncher<String> pickPortraitLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    displayImage(uri, "portrait");
                    enqueueKycImage(uri, UploadPurpose.WORKER_CERTIFICATE, "certificate");
                }
            });

    @Override
    protected FragmentWorkerKycBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerKycBinding.inflate(inflater, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Kiểm tra Room DB: nếu đang có KYC upload ngầm → redirect sang Pending ngay
        // Tránh user upload lại khi WorkManager đang chạy dở
        if (uploadViewModel != null) {
            uploadViewModel.clearStaleKycUploads(() -> {
                uploadViewModel.checkHasPendingKyc();
            });
        }
    }

    @Override
    protected void setupViews() {
        // init ViewModel sớm nhất có thể (trước khi setupViews cần nó)
        uploadViewModel = new ViewModelProvider(this).get(UploadViewModel.class);

        // Nút back trên toolbar
        binding.appBarLayout.toolbar.setTitle("Xác minh danh tính");
        binding.appBarLayout.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Thiết lập dữ liệu cho dropdown loại giấy tờ
        String[] docTypes = new String[] {
                "Căn cước công dân (CCCD)",
                "Chứng minh nhân dân (CMND) cũ",
                "Bằng lái xe",
                "Hộ chiếu"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                docTypes);
        binding.actvDocType.setAdapter(adapter);
        binding.actvDocType.setText(docTypes[0], false);

        // Click card → mở gallery chọn ảnh
        binding.cardUploadFront.setOnClickListener(v -> pickFrontLauncher.launch("image/*"));
        binding.cardUploadBack.setOnClickListener(v -> pickBackLauncher.launch("image/*"));
        binding.cardUploadPortrait.setOnClickListener(v -> pickPortraitLauncher.launch("image/*"));

        // Nút Gửi xét duyệt - chỉ enable khi cả 3 ảnh đã lưu vào hàng đợi
        binding.btnSubmitKYC.setEnabled(false);
        binding.btnSubmitKYC.setAlpha(0.5f);
        binding.btnSubmitKYC.setOnClickListener(v -> {
            if (!isFrontQueued || !isBackQueued || !isPortraitQueued) {
                Toast.makeText(requireContext(), "Vui lòng chọn đủ 3 ảnh", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isSubmitting)
                return;
            isSubmitting = true;

            // Fire & Navigate: kích hoạt upload nền qua WorkManager rồi navigate ngay
            // Không chờ upload hoàn tất — WorkManager tự xử lý trong nền.
            uploadViewModel.scheduleKycUpload(kycGroupId);
            Navigation.findNavController(requireView())
                    .navigate(com.fixit.R.id.workerKycUploadingFragment);
        });
    }

    @Override
    protected void observeData() {
        // Lắng nghe kết quả enqueue (ảnh đã lưu vào Room local)
        uploadViewModel.uploadResult.observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;

            if (result.isSuccess()) {
                switch (result.getPurpose()) {
                    case UploadPurpose.WORKER_KYC_FRONT:
                        isFrontQueued = true;
                        Toast.makeText(requireContext(), "✓ Ảnh mặt trước đã chọn", Toast.LENGTH_SHORT).show();
                        break;
                    case UploadPurpose.WORKER_KYC_BACK:
                        isBackQueued = true;
                        Toast.makeText(requireContext(), "✓ Ảnh mặt sau đã chọn", Toast.LENGTH_SHORT).show();
                        break;
                    case UploadPurpose.WORKER_CERTIFICATE:
                        isPortraitQueued = true;
                        Toast.makeText(requireContext(), "✓ Ảnh chân dung đã chọn", Toast.LENGTH_SHORT).show();
                        break;
                }
                updateSubmitButton();
            } else {
                Toast.makeText(requireContext(),
                        "Lỗi: " + result.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Nếu Room DB phát hiện KYC đang upload ngầm → redirect sang Uploading
        uploadViewModel.hasPendingKyc.observe(getViewLifecycleOwner(), hasPending -> {
            if (Boolean.TRUE.equals(hasPending) && isResumed()) {
                Navigation.findNavController(requireView())
                        .navigate(com.fixit.R.id.workerKycUploadingFragment);
            }
        });
    }

    /**
     * Đưa ảnh vào hàng đợi upload (lưu vào Room local).
     */
    private void enqueueKycImage(Uri uri, String purpose, String slotKey) {
        uploadViewModel.upload(
                requireContext(),
                uri,
                purpose,
                UploadTargetType.WORKER_KYC,
                null,
                kycGroupId,
                slotKey,
                null,
                false);
    }

    /**
     * Hiển thị ảnh đã chọn lên card tương ứng, ẩn icon/text placeholder.
     */
    private void displayImage(Uri uri, String type) {
        switch (type) {
            case "front":
                binding.ivKycFrontImage.setVisibility(View.VISIBLE);
                binding.icKycFrontIcon.setVisibility(View.GONE);
                binding.tvKycFrontLabel.setVisibility(View.GONE);
                Glide.with(this).load(uri).into(binding.ivKycFrontImage);
                break;
            case "back":
                binding.ivKycBackImage.setVisibility(View.VISIBLE);
                binding.icKycBackIcon.setVisibility(View.GONE);
                binding.tvKycBackLabel.setVisibility(View.GONE);
                Glide.with(this).load(uri).into(binding.ivKycBackImage);
                break;
            case "portrait":
                binding.ivKycPortraitImage.setVisibility(View.VISIBLE);
                binding.icKycPortraitIcon.setVisibility(View.GONE);
                binding.tvKycPortraitLabel.setVisibility(View.GONE);
                Glide.with(this).load(uri).into(binding.ivKycPortraitImage);
                break;
        }
    }

    /**
     * Enable nút Gửi khi cả 3 ảnh đã được enqueue vào hàng đợi.
     */
    private void updateSubmitButton() {
        boolean allReady = !isSubmitting && isFrontQueued && isBackQueued && isPortraitQueued;
        binding.btnSubmitKYC.setEnabled(allReady);
        binding.btnSubmitKYC.setAlpha(allReady ? 1.0f : 0.5f);
    }
}
