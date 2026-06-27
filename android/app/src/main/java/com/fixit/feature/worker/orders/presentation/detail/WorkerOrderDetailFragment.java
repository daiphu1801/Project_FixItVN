package com.fixit.feature.worker.orders.presentation.detail;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentWorkerOrderDetailBinding;
import com.fixit.databinding.LayoutOrderCustomerCardBinding;
import com.fixit.databinding.LayoutOrderMetaCardBinding;
import com.fixit.databinding.LayoutOrderTimelineCardBinding;
import com.fixit.databinding.LayoutPricingSummaryCardBinding;
import com.fixit.databinding.LayoutProofOfWorkSectionBinding;
import com.fixit.databinding.LayoutWorkerPaymentSectionBinding;
import com.fixit.core.common.AutoRefreshHelper;
import com.fixit.feature.upload.domain.model.UploadPurpose;
import com.fixit.feature.upload.domain.model.UploadTargetType;
import com.fixit.feature.upload.presentation.UploadViewModel;
import com.fixit.feature.worker.orders.domain.model.JobStatus;
import com.fixit.feature.worker.orders.domain.model.WorkerOrder;
import com.fixit.feature.worker.orders.presentation.WorkerOrdersViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerOrderDetailFragment extends BaseFragment<FragmentWorkerOrderDetailBinding> {

    private WorkerOrdersViewModel viewModel;
    private UploadViewModel uploadViewModel;
    private WorkerOrder currentOrder;
    private boolean hasShownSurveyNotice = false;
    private AutoRefreshHelper autoRefreshHelper;
    private android.os.Bundle savedInstanceState;

    // UI Helper coordinates layout states
    private OrderDetailUiHelper uiHelper;

    // ActivityResult Launcher cho chọn ảnh từ gallery
    private final ActivityResultLauncher<String> pickBeforeImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null && uiHelper != null) {
                    uiHelper.displayProofBeforeImage(uri);
                    uploadViewModel.upload(
                            requireContext(),
                            uri,
                            UploadPurpose.PROOF_BEFORE_REPAIR,
                            UploadTargetType.PROOF_OF_WORK,
                            getCurrentOrderId(),
                            null,
                            null,
                            null,
                            true
                    );
                }
            });

    private final ActivityResultLauncher<String> pickAfterImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null && uiHelper != null) {
                    uiHelper.displayProofAfterImage(uri);
                    uploadViewModel.upload(
                            requireContext(),
                            uri,
                            UploadPurpose.PROOF_AFTER_REPAIR,
                            UploadTargetType.PROOF_OF_WORK,
                            getCurrentOrderId(),
                            null,
                            null,
                            null,
                            true
                    );
                }
            });

    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        viewModel = new ViewModelProvider(requireActivity()).get(WorkerOrdersViewModel.class);
        uploadViewModel = new ViewModelProvider(this).get(UploadViewModel.class);
    }

    @Override
    protected FragmentWorkerOrderDetailBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerOrderDetailBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Lấy binding của các sub-layout từ include tags
        LayoutOrderCustomerCardBinding customerBinding = LayoutOrderCustomerCardBinding.bind(binding.cardCustomerInfo.getRoot());
        LayoutOrderMetaCardBinding metaBinding = LayoutOrderMetaCardBinding.bind(binding.cardOrderMeta.getRoot());
        LayoutOrderTimelineCardBinding timelineBinding = LayoutOrderTimelineCardBinding.bind(binding.cardTimeline.getRoot());
        LayoutPricingSummaryCardBinding pricingBinding = LayoutPricingSummaryCardBinding.bind(binding.cardPricingSummary.getRoot());
        LayoutWorkerPaymentSectionBinding paymentBinding = LayoutWorkerPaymentSectionBinding.bind(binding.sectionPayment.getRoot());
        LayoutProofOfWorkSectionBinding proofBinding = LayoutProofOfWorkSectionBinding.bind(binding.sectionProofOfWork.getRoot());

        uiHelper = new OrderDetailUiHelper(
                this, binding,
                customerBinding, metaBinding, timelineBinding,
                pricingBinding, paymentBinding, proofBinding,
                viewModel, savedInstanceState
        );

        uiHelper.setupClickListeners(
                () -> pickBeforeImageLauncher.launch("image/*"),
                () -> pickAfterImageLauncher.launch("image/*"),
                viewModel
        );
    }

    @Override
    protected void observeData() {
        String orderId = getArguments() != null ? getArguments().getString("orderId") : null;

        viewModel.orderDetails.observe(getViewLifecycleOwner(), order -> {
            if (order != null) {
                currentOrder = order;
                uiHelper.bindOrderData(order);
            }
        });

        if (orderId != null) {
            viewModel.loadOrderDetails(orderId);
        } else {
            Toast.makeText(requireContext(), "Lỗi: Không nhận được ID đơn hàng", Toast.LENGTH_SHORT).show();
        }

        viewModel.currentStatus.observe(getViewLifecycleOwner(), status -> {
            if (status != null) {
                uiHelper.updateTimelineUI(status, currentOrder);
                if (status == JobStatus.SURVEYING) {
                    showSurveyNoticeDialog();
                }
            }
        });

        viewModel.statusUpdateSuccess.observe(getViewLifecycleOwner(), successMessage -> {
            if (successMessage != null && !successMessage.isEmpty()) {
                Toast.makeText(requireContext(), successMessage, Toast.LENGTH_SHORT).show();
                viewModel.clearStatusUpdateSuccess();
            }
        });

        viewModel.isLoading.observe(getViewLifecycleOwner(), loading -> {
            if (binding.layoutLoading != null) {
                binding.layoutLoading.getRoot().setVisibility(loading ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.errorMessage.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe kết quả upload ảnh bằng chứng
        uploadViewModel.uploadResult.observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            if (result.isSuccess()) {
                Toast.makeText(requireContext(), "Upload ảnh thành công", Toast.LENGTH_SHORT).show();
                String currentId = getCurrentOrderId();
                if (currentId != null) {
                    viewModel.loadOrderDetails(currentId, false);
                }
            } else {
                Toast.makeText(requireContext(), result.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showPaymentQrCode() {
        if (uiHelper != null && uiHelper.getPaymentHelper() != null && currentOrder != null) {
            uiHelper.getPaymentHelper().showPaymentQrCode(currentOrder);
        }
    }

    public void confirmCashPayment() {
        if (uiHelper != null && uiHelper.getPaymentHelper() != null && currentOrder != null) {
            uiHelper.getPaymentHelper().confirmCashPayment(currentOrder);
        }
    }

    public WorkerOrder getCurrentOrder() {
        return currentOrder;
    }

    public String getCurrentOrderId() {
        if (currentOrder != null && currentOrder.getOrderId() != null) {
            return currentOrder.getOrderId();
        }
        return getArguments() != null ? getArguments().getString("orderId") : null;
    }

    private void showSurveyNoticeDialog() {
        if (hasShownSurveyNotice)
            return;
        hasShownSurveyNotice = true;

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Khảo sát & Báo giá")
                .setMessage(
                        "Bạn đã chuyển sang bước Khảo sát. Nếu có chi phí phát sinh (như phụ tùng thay thế, dịch vụ phát sinh ngoài gói), hãy nhấn vào nút \"+ Thêm chi phí phát sinh\" bên dưới trước khi bắt đầu sửa chữa nhé!")
                .setPositiveButton("Đã hiểu", null)
                .setIcon(com.fixit.R.drawable.ic_lucide_info)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (uiHelper != null && uiHelper.getMapHelper() != null) {
            uiHelper.getMapHelper().onResume();
        }
        if (autoRefreshHelper == null) {
            autoRefreshHelper = new AutoRefreshHelper(
                    requireContext(),
                    0L,
                    () -> {
                        String orderId = getCurrentOrderId();
                        if (viewModel != null && orderId != null) {
                            viewModel.loadOrderDetails(orderId, false);
                        }
                    },
                    "com.fixit.BOOKING_UPDATE"
            );
        }
        autoRefreshHelper.start();
    }

    @Override
    public void onPause() {
        if (uiHelper != null && uiHelper.getMapHelper() != null) {
            uiHelper.getMapHelper().onPause();
        }
        if (autoRefreshHelper != null) {
            autoRefreshHelper.stop();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (uiHelper != null && uiHelper.getMapHelper() != null) {
            uiHelper.getMapHelper().onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (uiHelper != null && uiHelper.getMapHelper() != null) {
            uiHelper.getMapHelper().onLowMemory();
        }
    }
}
