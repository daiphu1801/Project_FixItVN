package com.fixit.feature.worker.kyc.presentation;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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

import java.io.File;
import java.util.UUID;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerKycFragment extends BaseFragment<FragmentWorkerKycBinding> {

    private UploadViewModel uploadViewModel;
    private WorkerKycViewModel workerKycViewModel;

    // Theo dõi trạng thái "đã lưu vào hàng đợi local" của 3 ảnh
    private boolean isFrontQueued = false;
    private boolean isBackQueued = false;
    private boolean isPortraitQueued = false;
    private boolean isSubmitting = false;
    private final String kycGroupId = UUID.randomUUID().toString();

    private boolean isVnptFlow = false;
    private android.app.AlertDialog progressDialog;

    // Launcher cho VNPT eKYC SDK Activity
    private final ActivityResultLauncher<Intent> vnptKycLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    com.fixit.core.ekyc.VnptKycHelper.VnptKycResult kycResult = 
                            com.fixit.core.ekyc.VnptKycHelper.parseResult(result.getData());
                    if (kycResult != null) {
                        // Nén và lưu các ảnh nhận được từ SDK vào Room
                        processAndEnqueueLocalImage(kycResult.getFrontImagePath(), UploadPurpose.WORKER_KYC_FRONT, "front");
                        processAndEnqueueLocalImage(kycResult.getBackImagePath(), UploadPurpose.WORKER_KYC_BACK, "back");
                        processAndEnqueueLocalImage(kycResult.getSelfieImagePath(), UploadPurpose.WORKER_KYC_SELFIE, "selfie");
                    } else {
                        Toast.makeText(requireContext(), "Không nhận được kết quả từ VNPT eKYC", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Xác thực VNPT eKYC bị hủy", Toast.LENGTH_SHORT).show();
                }
            }
    );

    // 3 launcher chọn ảnh thủ công từ gallery
    private final ActivityResultLauncher<String> pickFrontLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    isVnptFlow = false;
                    processAndEnqueueUri(uri, UploadPurpose.WORKER_KYC_FRONT, "front");
                }
            });

    private final ActivityResultLauncher<String> pickBackLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    isVnptFlow = false;
                    processAndEnqueueUri(uri, UploadPurpose.WORKER_KYC_BACK, "back");
                }
            });

    private final ActivityResultLauncher<String> pickPortraitLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    isVnptFlow = false;
                    processAndEnqueueUri(uri, UploadPurpose.WORKER_KYC_SELFIE, "selfie");
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
        if (uploadViewModel != null) {
            uploadViewModel.clearStaleKycUploads(() -> {
                uploadViewModel.checkHasPendingKyc();
            });
        }
    }

    @Override
    protected void setupViews() {
        // init ViewModels
        uploadViewModel = new ViewModelProvider(this).get(UploadViewModel.class);
        workerKycViewModel = new ViewModelProvider(this).get(WorkerKycViewModel.class);

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

        // Cấu hình hiển thị VNPT eKYC SDK
        if (com.fixit.core.ekyc.VnptKycHelper.isSdkAvailable()) {
            binding.cardVnptKyc.setVisibility(View.VISIBLE);
            binding.groupManualUpload.setVisibility(View.GONE);

            binding.btnStartVnptKyc.setOnClickListener(v -> {
                isVnptFlow = true;
                workerKycViewModel.loadKycConfig();
            });

            binding.tvSwitchToManual.setOnClickListener(v -> {
                binding.cardVnptKyc.setVisibility(View.GONE);
                binding.groupManualUpload.setVisibility(View.VISIBLE);
            });
        } else {
            binding.cardVnptKyc.setVisibility(View.GONE);
            binding.groupManualUpload.setVisibility(View.VISIBLE);
        }

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

            // Kích hoạt upload nền qua WorkManager rồi chuyển màn
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
                    case UploadPurpose.WORKER_KYC_SELFIE:
                        isPortraitQueued = true;
                        Toast.makeText(requireContext(), "✓ Ảnh chân dung đã chọn", Toast.LENGTH_SHORT).show();
                        break;
                }
                updateSubmitButton();

                // Nếu chạy luồng VNPT eKYC và cả 3 ảnh đã enqueue thành công -> Tự động submit
                if (isVnptFlow && isFrontQueued && isBackQueued && isPortraitQueued && !isSubmitting) {
                    isSubmitting = true;
                    uploadViewModel.scheduleKycUpload(kycGroupId);
                    Navigation.findNavController(requireView())
                            .navigate(com.fixit.R.id.workerKycUploadingFragment);
                }
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

        // Lắng nghe trạng thái loading cấu hình VNPT eKYC
        workerKycViewModel.isLoading.observe(getViewLifecycleOwner(), this::showLoading);

        // Lắng nghe kết quả trả về cấu hình VNPT eKYC
        workerKycViewModel.kycConfig.observe(getViewLifecycleOwner(), config -> {
            if (config != null) {
                Intent intent = com.fixit.core.ekyc.VnptKycHelper.createKycIntent(
                        requireContext(),
                        config.getTokenId(),
                        config.getTokenKey(),
                        config.getApiUrl()
                );
                if (intent != null) {
                    vnptKycLauncher.launch(intent);
                } else {
                    Toast.makeText(requireContext(), "Không thể khởi tạo VNPT eKYC SDK", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Lắng nghe thông báo lỗi
        workerKycViewModel.error.observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean show) {
        if (binding.layoutLoading != null) {
            binding.layoutLoading.getRoot().setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void processAndEnqueueUri(Uri uri, String purpose, String slotKey) {
        if (uri == null) return;
        try {
            // 1. Copy Uri sang file tạm trong cache
            com.fixit.feature.upload.domain.model.LocalUploadFile tempFile =
                    com.fixit.feature.upload.util.UploadFilePreparer.fromUri(requireContext(), uri, purpose);

            // 2. Nén ảnh chất lượng 80% JPEG
            File originalFile = tempFile.getFile();
            File compressedFile = com.fixit.core.common.ImageCompressor.compressImage(
                    requireContext(),
                    originalFile,
                    "ekyc_" + slotKey + "_" + System.currentTimeMillis() + ".jpg"
            );

            // 3. Xóa file tạm gốc
            if (originalFile.exists()) {
                originalFile.delete();
            }

            Uri compressedUri = Uri.fromFile(compressedFile);

            // 4. Hiển thị và enqueue lên Room
            displayImage(compressedUri, slotKey.equals("selfie") ? "portrait" : slotKey);
            enqueueKycImage(compressedUri, purpose, slotKey);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Lỗi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void processAndEnqueueLocalImage(String path, String purpose, String slotKey) {
        if (path == null || path.isEmpty()) {
            return;
        }
        try {
            File originalFile = new File(path);
            if (!originalFile.exists()) {
                return;
            }
            // Nén ảnh chất lượng 80% JPEG
            File compressedFile = com.fixit.core.common.ImageCompressor.compressImage(
                    requireContext(),
                    originalFile,
                    "ekyc_" + slotKey + "_" + System.currentTimeMillis() + ".jpg"
            );
            Uri uri = Uri.fromFile(compressedFile);

            displayImage(uri, slotKey.equals("selfie") ? "portrait" : slotKey);
            enqueueKycImage(uri, purpose, slotKey);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Lỗi xử lý ảnh từ SDK: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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

