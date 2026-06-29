package com.fixit.feature.customer.booking.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerFindingWorkerBinding;
import com.fixit.feature.customer.order.presentation.CustomerOrderViewModel;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * BƯỚC 1 (Tiếp theo): FILE ĐIỀU KHIỂN MÀN HÌNH ĐANG TÌM THỢ (RADAR)
 * Mục đích: Hiển thị hiệu ứng radar quét tìm thợ sửa chữa xung quanh.
 */
@AndroidEntryPoint
public class CustomerFindingWorkerFragment extends BaseFragment<FragmentCustomerFindingWorkerBinding> {

    private org.osmdroid.views.overlay.Marker bookingMarker;

    @NonNull
    @Override
    protected FragmentCustomerFindingWorkerBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        // Kết nối file giao diện fragment_customer_finding_worker.xml với code Java này
        return FragmentCustomerFindingWorkerBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        CustomerOrderViewModel orderViewModel = new ViewModelProvider(requireActivity()).get(CustomerOrderViewModel.class);

        // Khởi tạo bản đồ OsmDroid
        org.osmdroid.config.Configuration.getInstance().load(
                requireContext(),
                requireContext().getSharedPreferences("osmdroid", android.content.Context.MODE_PRIVATE)
        );
        org.osmdroid.config.Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        binding.mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        binding.mapView.setMultiTouchControls(true);
        binding.mapView.getController().setZoom(16.5);

        // Vị trí mặc định ban đầu (Hà Nội) trước khi có toạ độ đơn hàng
        org.osmdroid.util.GeoPoint defaultPoint = new org.osmdroid.util.GeoPoint(21.0285, 105.8542);
        binding.mapView.getController().setCenter(defaultPoint);

        // Nơi xử lý các hiệu ứng radar hoặc nút 'Hủy yêu cầu'
        binding.btnCancelSearch.setOnClickListener(v -> {
            // Khi nhấn hủy, gọi API hủy đơn hàng trên backend
            orderViewModel.cancelCurrentBooking("Khách hàng hủy tìm kiếm thợ", false);
        });
    }

    @Override
    protected void observeData() {
        CustomerOrderViewModel orderViewModel = new ViewModelProvider(requireActivity()).get(CustomerOrderViewModel.class);
        
        orderViewModel.currentBooking.observe(getViewLifecycleOwner(), booking -> {
            if (booking != null) {
                // Hiển thị thông tin dịch vụ, địa chỉ, mô tả lỗi
                if (booking.getServiceName() != null && !booking.getServiceName().isEmpty()) {
                    binding.tvServiceName.setText(booking.getServiceName());
                } else {
                    binding.tvServiceName.setText("Dịch vụ sửa chữa");
                }

                if (booking.getAddress() != null && !booking.getAddress().isEmpty()) {
                    binding.tvAddress.setText("📍 " + booking.getAddress());
                } else {
                    binding.tvAddress.setText("📍 Chưa xác định địa chỉ");
                }

                if (booking.getIssueDescription() != null && !booking.getIssueDescription().isEmpty()) {
                    binding.tvDescription.setText("Mô tả sự cố: " + booking.getIssueDescription());
                } else {
                    binding.tvDescription.setText("Mô tả sự cố: Chưa có mô tả");
                }

                // Hiển thị giá tiền của đơn hàng nếu có
                if (booking.getFinalPrice() != null) {
                    java.text.DecimalFormat df = new java.text.DecimalFormat("#,###đ");
                    binding.tvPriceRange.setText(df.format(booking.getFinalPrice()));
                } else {
                    binding.tvPriceRange.setText("Giá thỏa thuận");
                }

                java.math.BigDecimal lat = booking.getDestinationLat();
                java.math.BigDecimal lng = booking.getDestinationLng();
                if (lat != null && lng != null) {
                    org.osmdroid.util.GeoPoint point = new org.osmdroid.util.GeoPoint(lat.doubleValue(), lng.doubleValue());
                    binding.mapView.getController().setCenter(point);

                    if (bookingMarker == null) {
                        bookingMarker = new org.osmdroid.views.overlay.Marker(binding.mapView);
                        bookingMarker.setTitle("Vị trí yêu cầu");
                        bookingMarker.setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM);
                        binding.mapView.getOverlays().add(bookingMarker);
                    }
                    bookingMarker.setPosition(point);
                    binding.mapView.invalidate();
                }
            }
        });
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
