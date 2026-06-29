package com.fixit.feature.customer.order.presentation;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerOrderDetailBinding;
import com.fixit.feature.customer.booking.domain.model.CustomerBooking;
import com.fixit.feature.customer.booking.presentation.QuotationBottomSheet;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CustomerOrderDetailFragment extends BaseFragment<FragmentCustomerOrderDetailBinding> {

    private CustomerOrderViewModel viewModel;
    private final DecimalFormat priceFormat = new DecimalFormat("#,###");
    private Marker customerMarker;
    private Marker workerMarker;

    @NonNull
    @Override
    protected FragmentCustomerOrderDetailBinding inflateViewBinding(@NonNull LayoutInflater inflater,
            ViewGroup container) {
        return FragmentCustomerOrderDetailBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        viewModel = new ViewModelProvider(requireActivity()).get(CustomerOrderViewModel.class);

        // Khởi tạo bản đồ OsmDroid
        Configuration.getInstance().load(
                requireContext(),
                requireContext().getSharedPreferences("osmdroid", android.content.Context.MODE_PRIVATE)
        );
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        binding.mapView.setTileSource(TileSourceFactory.MAPNIK);
        binding.mapView.setMultiTouchControls(true);
        binding.mapView.getController().setZoom(15.5);

        String orderId = null;
        if (getArguments() != null) {
            orderId = getArguments().getString("orderId");
        }
        if (orderId != null) {
            viewModel.loadBooking(orderId);
        }

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

        binding.btnConfirmAcceptance.setOnClickListener(v -> {
            CustomerBooking booking = viewModel.currentBooking.getValue();
            if (booking == null || booking.getBookingId() == null) return;

            String quotationStatus = booking.getQuotationStatus();
            if ("Pending".equals(quotationStatus)) {
                // Trạng thái báo giá đang chờ duyệt → hiển thị QuotationBottomSheet
                if (booking.getQuotationId() != null && booking.getLaborCost() != null) {
                    com.fixit.feature.customer.booking.presentation.QuotationBottomSheet.newInstance(
                            booking.getBookingId(),
                            booking.getQuotationId(),
                            booking.getLaborCost(),
                            booking.getMaterialCost() != null ? booking.getMaterialCost() : BigDecimal.ZERO
                    ).show(getChildFragmentManager(), "quotation_sheet");
                }
            } else {
                // Trạng thái nghiệm thu thực sự → hiển thị dialog chọn phương thức thanh toán
                showPaymentMethodDialog(booking.getBookingId());
            }
        });

        binding.ivCopyContent.setOnClickListener(v -> {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                    requireContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Payment Content", binding.tvBankTransferContent.getText());
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(requireContext(), "Đã sao chép nội dung chuyển khoản", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSimulatePayment.setOnClickListener(v -> {
            CustomerBooking booking = viewModel.currentBooking.getValue();
            if (booking != null && booking.getBookingId() != null) {
                binding.btnSimulatePayment.setEnabled(false);
                binding.btnSimulatePayment.setText("Đang giả lập...");
                viewModel.simulateBankTransfer(booking.getBookingId(), result -> {
                    binding.btnSimulatePayment.setEnabled(true);
                    binding.btnSimulatePayment.setText("[Giả lập] Xác nhận đã chuyển khoản");
                    if (result != null && result.isSuccess()) {
                        Toast.makeText(requireContext(), "Thanh toán giả lập thành công!", Toast.LENGTH_SHORT).show();
                        if (navController != null) {
                            navController.popBackStack();
                        }
                    } else {
                        String errorMsg = result != null && result.getError() != null
                                ? result.getError().getMessage()
                                : "Lỗi giả lập";
                        Toast.makeText(requireContext(), "Lỗi: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    protected void observeData() {
        if (viewModel == null) return;

        viewModel.currentBooking.observe(getViewLifecycleOwner(), this::bindBookingData);

        // Observe signal hiển thị QuotationBottomSheet khi thợ gửi báo giá
        viewModel.showQuotation.observe(getViewLifecycleOwner(), shouldShow -> {
            if (Boolean.TRUE.equals(shouldShow)) {
                CustomerBooking booking = viewModel.currentBooking.getValue();
                if (booking != null && booking.getQuotationId() != null
                        && booking.getLaborCost() != null) {
                    QuotationBottomSheet.newInstance(
                            booking.getBookingId(),
                            booking.getQuotationId(),
                            booking.getLaborCost(),
                            booking.getMaterialCost() != null ? booking.getMaterialCost() : BigDecimal.ZERO
                    ).show(getChildFragmentManager(), "quotation_sheet");
                    viewModel.clearShowQuotation();
                }
            }
        });
    }

    private void bindBookingData(CustomerBooking booking) {
        if (booking == null) return;

        // Mã đơn hàng
        String shortId = booking.getBookingId() != null && booking.getBookingId().length() > 8
                ? booking.getBookingId().substring(0, 8).toUpperCase()
                : booking.getBookingId();
        binding.tvOrderCode.setText("#" + shortId);

        // Tên dịch vụ
        String serviceName = booking.getServiceName();
        if (serviceName == null || serviceName.isEmpty()) {
            serviceName = "Dịch vụ sửa chữa";
        }
        binding.tvServiceName.setText(serviceName);

        // Mô tả vấn đề
        binding.tvNote.setText(booking.getIssueDescription());

        // Địa chỉ
        binding.tvAddress.setText(booking.getAddress());

        // Thời gian
        if (booking.getCreatedAt() != null) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault());
                java.util.Date date = inputFormat.parse(booking.getCreatedAt());
                binding.tvSchedule.setText(outputFormat.format(date));
            } catch (Exception e) {
                binding.tvSchedule.setText(booking.getCreatedAt());
            }
        }

        // Chi tiết giá (laborCost + materialCost)
        bindQuotationDetail(booking);

        // Chi phí tổng / ước tính
        if (booking.getLaborCost() != null) {
            // Đã có báo giá chính thức từ thợ
            BigDecimal total = booking.getLaborCost().add(
                    booking.getMaterialCost() != null ? booking.getMaterialCost() : BigDecimal.ZERO);
            binding.tvEstimatedCost.setText(priceFormat.format(total) + "đ");
            binding.tvCostLabel.setText("Chi phí xác nhận");
        } else if (booking.getFinalPrice() != null) {
            binding.tvEstimatedCost.setText(priceFormat.format(booking.getFinalPrice()) + "đ");
            binding.tvCostLabel.setText("Chi phí dự kiến");
        } else {
            binding.tvEstimatedCost.setText("Khảo sát báo giá sau");
            binding.tvCostLabel.setText("Chi phí dự kiến");
        }

        // Thông tin thợ
        bindWorkerInfo(booking);

        // Quản lý hiển thị nút hành động theo trạng thái
        bindActionButtons(booking);

        // Cập nhật bản đồ và các Marker
        updateMapMarkers(booking);

        // Cập nhật Timeline tiến độ
        updateTimeline(booking);
    }

    private void bindQuotationDetail(CustomerBooking booking) {
        if (booking.getLaborCost() != null) {
            binding.llQuotationDetail.setVisibility(View.VISIBLE);
            binding.tvQuotationLaborCost.setText(priceFormat.format(booking.getLaborCost()) + "đ");

            BigDecimal matCost = booking.getMaterialCost() != null
                    ? booking.getMaterialCost() : BigDecimal.ZERO;
            binding.tvQuotationMaterialCost.setText(priceFormat.format(matCost) + "đ");

            BigDecimal total = booking.getLaborCost().add(matCost);
            binding.tvQuotationTotal.setText(priceFormat.format(total) + "đ");
        } else {
            binding.llQuotationDetail.setVisibility(View.GONE);
        }
    }

    private void bindWorkerInfo(CustomerBooking booking) {
        if (booking.getWorker() != null) {
            binding.tvTechnicianName.setVisibility(View.VISIBLE);
            binding.ivTechnicianAvatar.setVisibility(View.VISIBLE);
            binding.tvTechnicianDesc.setVisibility(View.VISIBLE);
            binding.layoutRating.setVisibility(View.VISIBLE);
            binding.btnMessage.setVisibility(View.VISIBLE);
            binding.tvTechnicianStatus.setVisibility(View.VISIBLE);

            binding.tvTechnicianName.setText(booking.getWorker().getFullName());

            Glide.with(this)
                    .load(booking.getWorker().getAvatarUrl())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(binding.ivTechnicianAvatar);

            // Cập nhật trạng thái thợ theo status booking thực tế
            binding.tvTechnicianStatus.setText(mapStatusLabel(booking.getStatus()));

            // Nút nhắn tin
            binding.btnMessage.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("workerId", booking.getWorker().getWorkerId());
                args.putString("workerName", booking.getWorker().getFullName());
                if (navController != null) {
                    navController.navigate(R.id.nav_customer_chat, args);
                }
            });

            // Click xem profile thợ
            binding.ivTechnicianAvatar.setOnClickListener(v -> navigateToWorkerProfile(booking.getWorker().getWorkerId()));
            binding.tvTechnicianName.setOnClickListener(v -> navigateToWorkerProfile(booking.getWorker().getWorkerId()));
        } else {
            binding.tvTechnicianName.setVisibility(View.GONE);
            binding.ivTechnicianAvatar.setVisibility(View.GONE);
            binding.tvTechnicianDesc.setVisibility(View.GONE);
            binding.layoutRating.setVisibility(View.GONE);
            binding.btnMessage.setVisibility(View.GONE);
            binding.tvTechnicianStatus.setVisibility(View.GONE);
        }
    }

    /**
     * Ánh xạ trạng thái đơn → label hiển thị trên badge thợ
     */
    private String mapStatusLabel(String status) {
        if (status == null) return "Đang xử lý";
        switch (status.toLowerCase()) {
            case "accepted":         return "Đang di chuyển đến";
            case "surveying":        return "Đang khảo sát thiết bị";
            case "in_progress":      return "Đang sửa chữa";
            case "waiting_approval": return "Chờ bạn duyệt";
            case "waiting_payment":  return "Chờ xác nhận nhận tiền";
            case "completed":        return "Đã hoàn thành";
            case "cancelled":        return "Đã hủy";
            default:                 return status;
        }
    }

    private void bindActionButtons(CustomerBooking booking) {
        // Reset tất cả về GONE trước
        binding.llApprovalSection.setVisibility(View.GONE);
        binding.btnConfirmAcceptance.setVisibility(View.GONE);
        binding.tvWaitingPaymentInfo.setVisibility(View.GONE);
        binding.cvBankTransferDetails.setVisibility(View.GONE);
        binding.btnCancelOrder.setVisibility(View.GONE);

        if (booking == null || booking.getStatus() == null) return;
        String status = booking.getStatus();
        switch (status.toLowerCase()) {
            case "pending":
            case "accepted":
                binding.btnCancelOrder.setVisibility(View.VISIBLE);
                break;
            case "surveying":
            case "in_progress":
                // Không cho hủy khi thợ đang làm việc
                break;
            case "waiting_approval":
                binding.llApprovalSection.setVisibility(View.VISIBLE);
                binding.btnConfirmAcceptance.setVisibility(View.VISIBLE);
                // Phân biệt: Báo giá đang chờ duyệt (Pending) vs Nghiệm thu thực sự
                if ("Pending".equals(booking.getQuotationStatus())) {
                    binding.btnConfirmAcceptance.setText("Đồng ý báo giá");
                } else {
                    binding.btnConfirmAcceptance.setText("Xác nhận nghiệm thu");
                }
                break;
            case "waiting_payment":
                binding.llApprovalSection.setVisibility(View.VISIBLE);
                if ("bank_transfer".equalsIgnoreCase(booking.getPaymentMethod())) {
                    binding.cvBankTransferDetails.setVisibility(View.VISIBLE);
                    
                    // Tính tổng tiền
                    BigDecimal totalAmount = BigDecimal.ZERO;
                    if (booking.getLaborCost() != null) {
                        totalAmount = booking.getLaborCost().add(
                                booking.getMaterialCost() != null ? booking.getMaterialCost() : BigDecimal.ZERO);
                    } else if (booking.getFinalPrice() != null) {
                        totalAmount = booking.getFinalPrice();
                    }
                    
                    binding.tvBankTransferAmount.setText(priceFormat.format(totalAmount) + "đ");
                    
                    // Nội dung chuyển khoản: sử dụng paymentCode
                    String paymentCode = booking.getPaymentCode() != null ? booking.getPaymentCode() : "";
                    binding.tvBankTransferContent.setText(paymentCode);
                    
                    // Load VietQR qua Glide
                    String qrUrl = "https://img.vietqr.io/image/MB-970422920260627-compact2.png?amount=" 
                            + totalAmount.toPlainString() 
                            + "&addInfo=" + paymentCode 
                            + "&accountName=CONG%20TY%20FIXIT%20VIET%20NAM";
                    
                    Glide.with(this)
                            .load(qrUrl)
                            .placeholder(R.drawable.img_map_placeholder)
                            .error(R.drawable.img_map_placeholder)
                            .into(binding.ivPaymentQR);
                } else {
                    binding.tvWaitingPaymentInfo.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void showPaymentMethodDialog(String bookingId) {
        com.google.android.material.bottomsheet.BottomSheetDialog dialog =
                new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());

        android.view.View view = android.view.LayoutInflater.from(requireContext())
                .inflate(com.fixit.R.layout.dialog_payment_method_picker, null);
        dialog.setContentView(view);

        android.widget.RadioGroup rgPayment = view.findViewById(com.fixit.R.id.rgPaymentMethodPicker);
        android.widget.Button btnConfirm = view.findViewById(com.fixit.R.id.btnConfirmPaymentMethod);

        btnConfirm.setOnClickListener(btn -> {
            int checkedId = rgPayment.getCheckedRadioButtonId();
            String selectedMethod = (checkedId == com.fixit.R.id.rbBankTransfer) ? "BANK_TRANSFER" : "CASH";
            dialog.dismiss();

            binding.btnConfirmAcceptance.setEnabled(false);
            binding.btnConfirmAcceptance.setText("Đang xử lý...");
            viewModel.confirmAndPayBooking(bookingId, selectedMethod, result -> {
                binding.btnConfirmAcceptance.setEnabled(true);
                binding.btnConfirmAcceptance.setText("Xác nhận nghiệm thu");
                if (result != null && result.isSuccess()) {
                    Toast.makeText(requireContext(), "Nghiệm thu thành công!", Toast.LENGTH_SHORT).show();
                    if (navController != null) navController.popBackStack();
                } else {
                    String errMsg = result != null && result.getError() != null
                            ? result.getError().getMessage() : "Lỗi không xác định";
                    Toast.makeText(requireContext(), "Thất bại: " + errMsg, Toast.LENGTH_LONG).show();
                }
            });
        });

        dialog.show();
    }

    private void navigateToWorkerProfile(String workerId) {
        if (workerId == null || workerId.isEmpty()) return;
        if (navController != null) {
            Bundle args = new Bundle();
            args.putString("workerId", workerId);
            navController.navigate(R.id.nav_worker_public_profile, args);
        }
    }

    private void updateMapMarkers(CustomerBooking booking) {
        if (booking == null || binding.mapView == null) return;

        BigDecimal lat = booking.getDestinationLat();
        BigDecimal lng = booking.getDestinationLng();
        GeoPoint customerPoint = null;

        if (lat != null && lng != null) {
            customerPoint = new GeoPoint(lat.doubleValue(), lng.doubleValue());
            if (customerMarker == null) {
                customerMarker = new Marker(binding.mapView);
                customerMarker.setTitle("Vị trí yêu cầu sửa chữa");
                customerMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                binding.mapView.getOverlays().add(customerMarker);
            }
            customerMarker.setPosition(customerPoint);
        }

        GeoPoint workerPoint = null;
        if (booking.getWorker() != null && booking.getWorker().getLatitude() != null && booking.getWorker().getLongitude() != null) {
            double wLat = booking.getWorker().getLatitude().doubleValue();
            double wLng = booking.getWorker().getLongitude().doubleValue();
            if (wLat != 0.0 && wLng != 0.0) {
                workerPoint = new GeoPoint(wLat, wLng);
                if (workerMarker == null) {
                    workerMarker = new Marker(binding.mapView);
                    workerMarker.setTitle("Vị trí thợ: " + booking.getWorker().getFullName());
                    workerMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    binding.mapView.getOverlays().add(workerMarker);
                }
                workerMarker.setPosition(workerPoint);
            }
        } else {
            if (workerMarker != null) {
                binding.mapView.getOverlays().remove(workerMarker);
                workerMarker = null;
            }
        }

        if (customerPoint != null && workerPoint != null) {
            double midLat = (customerPoint.getLatitude() + workerPoint.getLatitude()) / 2.0;
            double midLng = (customerPoint.getLongitude() + workerPoint.getLongitude()) / 2.0;
            binding.mapView.getController().setCenter(new GeoPoint(midLat, midLng));
            binding.mapView.getController().setZoom(15.0);
        } else if (customerPoint != null) {
            binding.mapView.getController().setCenter(customerPoint);
            binding.mapView.getController().setZoom(16.5);
        }

        binding.mapView.invalidate();
    }

    private void updateTimeline(CustomerBooking booking) {
        if (booking == null || binding.layoutTimeline == null) return;

        String status = booking.getStatus();
        int currentStep = getStepFromStatus(status);
        boolean isAllCompleted = "completed".equalsIgnoreCase(status);

        String timeStep1 = "";
        String timeStep2 = "";
        String timeStep3 = "";
        String timeStep4 = "";
        String timeStep5 = "";
        if (booking.getDoneActions() != null) {
            for (String action : booking.getDoneActions()) {
                if (action == null || !action.contains("|")) continue;
                String[] parts = action.split("\\|", 2);
                if (parts.length < 2) continue;
                String actStatus = parts[0].toUpperCase();
                String actTime = formatTimelineTime(parts[1]);
                switch (actStatus) {
                    case "PENDING":
                        timeStep1 = actTime;
                        break;
                    case "ACCEPTED":
                        timeStep2 = actTime;
                        break;
                    case "SURVEYING":
                        timeStep3 = actTime;
                        break;
                    case "IN_PROGRESS":
                    case "REPAIRING":
                        timeStep4 = actTime;
                        break;
                    case "COMPLETED":
                        timeStep5 = actTime;
                        break;
                }
            }
        }

        setStepTime(1, timeStep1);
        setStepTime(2, timeStep2);
        setStepTime(3, timeStep3);
        setStepTime(4, timeStep4);
        setStepTime(5, timeStep5);

        for (int i = 1; i <= 5; i++) {
            if (isAllCompleted) {
                updateStepState(i, true, false);
            } else {
                if (i < currentStep) {
                    updateStepState(i, true, false);
                } else if (i == currentStep) {
                    updateStepState(i, false, true);
                } else {
                    updateStepState(i, false, false);
                }
            }
        }
    }

    private int getStepFromStatus(String status) {
        if (status == null) return 1;
        switch (status.toLowerCase()) {
            case "pending":
                return 1;
            case "accepted":
            case "arriving":
                return 2;
            case "surveying":
                return 3;
            case "in_progress":
                return 4;
            case "waiting_approval":
            case "waiting_payment":
            case "completed":
                return 5;
            default:
                return 1;
        }
    }

    private String formatTimelineTime(String isoString) {
        if (isoString == null) return "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            java.util.Date date = inputFormat.parse(isoString);
            return outputFormat.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    private void setStepTime(int stepIndex, String time) {
        com.fixit.databinding.LayoutOrderTimelineCardBinding timeline = binding.layoutTimeline;
        android.widget.TextView tvTime = null;
        switch (stepIndex) {
            case 1: tvTime = timeline.step1Time; break;
            case 2: tvTime = timeline.step2Time; break;
            case 3: tvTime = timeline.step3Time; break;
            case 4: tvTime = timeline.step4Time; break;
            case 5: tvTime = timeline.step5Time; break;
        }
        if (tvTime != null) {
            if (time != null && !time.isEmpty()) {
                tvTime.setText(time);
                tvTime.setVisibility(View.VISIBLE);
            } else {
                tvTime.setVisibility(View.GONE);
            }
        }
    }

    private void updateStepState(int stepIndex, boolean isDone, boolean isActive) {
        com.fixit.databinding.LayoutOrderTimelineCardBinding timeline = binding.layoutTimeline;
        int colorActive = Color.parseColor("#2563eb");
        int colorDone = Color.parseColor("#2563eb");
        int colorPending = Color.parseColor("#e2e8f0");
        int textActive = Color.parseColor("#2563eb");
        int textDone = Color.parseColor("#0f172a");
        int textPending = Color.parseColor("#64748b");

        int color = isDone ? colorDone : (isActive ? colorActive : colorPending);
        int textColor = isDone ? textDone : (isActive ? textActive : textPending);

        float density = getResources().getDisplayMetrics().density;
        int padding = isDone ? 0 : (int) (5 * density);

        android.widget.ImageView ivIcon = null;
        android.widget.TextView tvTitle = null;
        View vLine = null;
        View vPulse = null;

        switch (stepIndex) {
            case 1:
                ivIcon = timeline.step1Icon;
                tvTitle = timeline.step1Title;
                vLine = timeline.step1Line;
                vPulse = timeline.step1Pulse;
                break;
            case 2:
                ivIcon = timeline.step2Icon;
                tvTitle = timeline.step2Title;
                vLine = timeline.step2Line;
                vPulse = timeline.step2Pulse;
                break;
            case 3:
                ivIcon = timeline.step3Icon;
                tvTitle = timeline.step3Title;
                vLine = timeline.step3Line;
                vPulse = timeline.step3Pulse;
                break;
            case 4:
                ivIcon = timeline.step4Icon;
                tvTitle = timeline.step4Title;
                vLine = timeline.step4Line;
                vPulse = timeline.step4Pulse;
                break;
            case 5:
                ivIcon = timeline.step5Icon;
                tvTitle = timeline.step5Title;
                vPulse = timeline.step5Pulse;
                break;
        }

        if (ivIcon != null) {
            ivIcon.setPadding(padding, padding, padding, padding);
            ivIcon.setImageResource(isDone ? R.drawable.ic_lucide_check_circle : R.drawable.circle_background);
            ivIcon.setImageTintList(android.content.res.ColorStateList.valueOf(color));
        }
        if (tvTitle != null) {
            tvTitle.setTextColor(textColor);
        }
        if (vLine != null) {
            vLine.setBackgroundColor(isDone ? color : Color.parseColor("#e2e8f0"));
        }
        if (vPulse != null) {
            vPulse.setVisibility(isActive ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null && binding.mapView != null) {
            binding.mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (binding != null && binding.mapView != null) {
            binding.mapView.onPause();
        }
    }
}
