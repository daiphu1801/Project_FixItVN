package com.fixit.feature.worker.job.presentation;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentWorkerJobBinding;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * WorkerJobFragment — Màn hình Tìm Việc (trung tâm điều khiển nhận đơn)
 *
 * Luồng UC-W02: Bật/Tắt trạng thái Sẵn sàng nhận việc
 *
 * Trạng thái UI:
 *   OFFLINE → nút xanh "BẮT ĐẦU NHẬN VIỆC", bản đồ mờ, chấm xám
 *   ONLINE  → nút đỏ "DỪNG NHẬN VIỆC",      bản đồ sáng, chấm xanh nhấp nháy
 *   NỢ TIỀN → nút bị khóa (disabled), banner cảnh báo đỏ hiện lên
 */
@AndroidEntryPoint
public class WorkerJobFragment extends BaseFragment<FragmentWorkerJobBinding> {

    private WorkerJobViewModel viewModel;
    private com.fixit.feature.worker.presentation.WorkerStatusViewModel statusViewModel;
    private org.osmdroid.views.overlay.Marker workerMarker;

    // ──────────────────────────────────────────────────────────────────────────
    @Override
    protected FragmentWorkerJobBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerJobBinding.inflate(inflater, container, false);
    }

    // ──────────────────────────────────────────────────────────────────────────
    @Override
    protected void setupViews() {
        // Khởi tạo bản đồ OsmDroid
        org.osmdroid.config.Configuration.getInstance().load(
                requireContext(),
                requireContext().getSharedPreferences("osmdroid", android.content.Context.MODE_PRIVATE)
        );
        org.osmdroid.config.Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        binding.mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        binding.mapView.setMultiTouchControls(true);
        binding.mapView.getController().setZoom(16.5);

        // Vị trí mặc định ban đầu (Hà Nội) trước khi có toạ độ của thợ
        org.osmdroid.util.GeoPoint startPoint = new org.osmdroid.util.GeoPoint(21.0285, 105.8542);
        binding.mapView.getController().setCenter(startPoint);

        // Nút Toggle nhận việc
        binding.btnToggleOnline.setOnClickListener(v -> {
            if (viewModel.hasDebt()) {
                showWarningDialog("Không thể nhận việc", "Bạn cần thanh toán khoản nợ chiết khấu trước!");
                return;
            }
            if (statusViewModel != null) {
                statusViewModel.toggleOnlineStatus();
            }
        });

        // Nút "Nạp tiền" trong banner cảnh báo → điều hướng sang Ví
        binding.btnGoToWalletFromJob.setOnClickListener(v ->
                Navigation.findNavController(requireView())
                        .navigate(R.id.workerWalletFragment));
    }

    // ──────────────────────────────────────────────────────────────────────────
    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(this).get(WorkerJobViewModel.class);
        statusViewModel = new ViewModelProvider(requireActivity()).get(com.fixit.feature.worker.presentation.WorkerStatusViewModel.class);

        // Tên thợ
        viewModel.workerName.observe(getViewLifecycleOwner(),
                name -> binding.tvJobWorkerName.setText(name));

        // Khu vực hoạt động
        viewModel.serviceArea.observe(getViewLifecycleOwner(), area -> {
            binding.tvServiceArea.setText(area + " ▾");
            binding.tvMapLocationLabel.setText("📍 " + area);
        });

        // Đơn hôm nay
        viewModel.todayOrders.observe(getViewLifecycleOwner(),
                count -> binding.tvTodayOrders.setText(String.valueOf(count)));

        // Điểm đánh giá
        viewModel.rating.observe(getViewLifecycleOwner(),
                r -> binding.tvRatingScore.setText(String.format("%.1f", r)));

        // Nợ tiền → hiện/ẩn banner cảnh báo
        viewModel.debtBalance.observe(getViewLifecycleOwner(), debt -> {
            boolean hasDebt = debt != null && debt > 0;
            binding.bannerDebt.setVisibility(hasDebt ? View.VISIBLE : View.GONE);
            // Khóa nút toggle nếu đang nợ
            binding.btnToggleOnline.setEnabled(!hasDebt);
            if (hasDebt) {
                binding.btnToggleOnline.setAlpha(0.5f);
                binding.btnToggleOnline.setText("Thanh toán nợ để nhận việc");
                binding.btnToggleOnline.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(Color.parseColor("#94a3b8")));
            }
        });

        // Toạ độ thợ
        viewModel.latitude.observe(getViewLifecycleOwner(), lat -> updateMapPosition());
        viewModel.longitude.observe(getViewLifecycleOwner(), lng -> updateMapPosition());

        // Trạng thái Online/Offline → cập nhật toàn bộ UI
        statusViewModel.isOnline.observe(getViewLifecycleOwner(), this::applyOnlineState);

        statusViewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.trim().isEmpty()) {
                showWarningDialog("Không thể nhận việc", message);
                statusViewModel.clearError();
            }
        });
    }

    private void updateMapPosition() {
        Double lat = viewModel.latitude.getValue();
        Double lng = viewModel.longitude.getValue();
        if (lat == null || lng == null || (lat == 0.0 && lng == 0.0)) {
            return;
        }

        org.osmdroid.util.GeoPoint point = new org.osmdroid.util.GeoPoint(lat, lng);
        binding.mapView.getController().animateTo(point);

        if (workerMarker == null) {
            workerMarker = new org.osmdroid.views.overlay.Marker(binding.mapView);
            workerMarker.setTitle("Vị trí hoạt động của tôi");
            workerMarker.setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM);
            binding.mapView.getOverlays().add(workerMarker);
        }
        workerMarker.setPosition(point);
        binding.mapView.invalidate();
    }

    /**
     * Cập nhật toàn bộ giao diện theo trạng thái Online / Offline.
     */
    private void applyOnlineState(boolean isOnline) {
        if (isOnline) {
            // ── ONLINE ─────────────────────────────────────────────────────
            // Nút toggle: đỏ "DỪNG NHẬN VIỆC"
            binding.btnToggleOnline.setText("DỪNG NHẬN VIỆC");
            binding.btnToggleOnline.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(Color.parseColor("#ef4444")));
            binding.btnToggleOnline.setAlpha(1f);
            binding.btnToggleOnline.setEnabled(true);

            // Card trạng thái: xanh
            binding.tvStatusLabel.setText("ONLINE");
            binding.tvStatusLabel.setTextColor(Color.parseColor("#16a34a"));
            binding.tvStatusDesc.setText("Sẵn sàng nhận việc! Hệ thống đang tìm đơn cho bạn...");
            binding.viewStatusDot.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(Color.parseColor("#22c55e")));
            binding.viewDivider.setBackgroundColor(Color.parseColor("#86efac"));
            binding.cardStatus.setCardBackgroundColor(Color.parseColor("#f0fdf4"));
            ((com.google.android.material.card.MaterialCardView) binding.cardStatus)
                    .setStrokeColor(Color.parseColor("#86efac"));

            // Bản đồ: sáng + ẩn overlay
            binding.viewMapOverlay.setVisibility(View.GONE);
            binding.layoutRadarContainer.setVisibility(View.VISIBLE);
            binding.tvMapStatus.setText("Đang phát sóng");
            binding.tvMapStatus.setBackgroundColor(Color.parseColor("#cc22c55e"));
            startRadarPulse();

        } else {
            // ── OFFLINE ────────────────────────────────────────────────────
            // Nút toggle: xanh lá "BẮT ĐẦU NHẬN VIỆC"
            binding.btnToggleOnline.setText("BẮT ĐẦU NHẬN VIỆC");
            binding.btnToggleOnline.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(Color.parseColor("#22c55e")));

            // Card trạng thái: xám
            binding.tvStatusLabel.setText("OFFLINE");
            binding.tvStatusLabel.setTextColor(Color.parseColor("#94a3b8"));
            binding.tvStatusDesc.setText("Bạn đang không hiển thị trên bản đồ");
            binding.viewStatusDot.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(Color.parseColor("#94a3b8")));
            binding.viewDivider.setBackgroundColor(Color.parseColor("#e2e8f0"));
            binding.cardStatus.setCardBackgroundColor(Color.parseColor("#f1f5f9"));
            ((com.google.android.material.card.MaterialCardView) binding.cardStatus)
                    .setStrokeColor(Color.parseColor("#e2e8f0"));

            // Bản đồ: mờ + hiện overlay
            binding.viewMapOverlay.setVisibility(View.VISIBLE);
            binding.layoutRadarContainer.setVisibility(View.GONE);
            binding.tvMapStatus.setText("Đang tắt");
            binding.tvMapStatus.setBackgroundColor(Color.parseColor("#cc42c2ff"));
            binding.ivMapRadar.clearAnimation();
        }
    }

    /**
     * Hiệu ứng nhấp nháy cho icon Radar khi ONLINE.
     */
    private void startRadarPulse() {
        AlphaAnimation pulse = new AlphaAnimation(1.0f, 0.3f);
        pulse.setDuration(800);
        pulse.setRepeatCount(Animation.INFINITE);
        pulse.setRepeatMode(Animation.REVERSE);
        binding.ivMapRadar.startAnimation(pulse);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null && binding.mapView != null) {
            binding.mapView.onResume();
        }
        if (viewModel != null) {
            viewModel.loadProfile();
            viewModel.loadSummary();
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
