package com.fixit.feature.customer.booking.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerLocationPickerBinding;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CustomerLocationPickerFragment extends BaseFragment<FragmentCustomerLocationPickerBinding> {

    public static final String REQUEST_KEY = "location_request";
    public static final String ADDRESS_KEY = "address_text";

    @NonNull
    @Override
    protected FragmentCustomerLocationPickerBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentCustomerLocationPickerBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Configure osmdroid
        org.osmdroid.config.Configuration.getInstance().load(requireContext(), requireContext().getSharedPreferences("osmdroid", android.content.Context.MODE_PRIVATE));

        // Initialize MapView
        binding.mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        binding.mapView.setMultiTouchControls(true);

        // Set default location (Hanoi)
        org.osmdroid.util.GeoPoint startPoint = new org.osmdroid.util.GeoPoint(21.0285, 105.8542);
        binding.mapView.getController().setZoom(16.0);
        binding.mapView.getController().setCenter(startPoint);

        // Fake Geocoder logic: When user pans the map, update the address
        binding.mapView.addMapListener(new org.osmdroid.events.MapListener() {
            @Override
            public boolean onScroll(org.osmdroid.events.ScrollEvent event) {
                // Do nothing while scrolling
                return false;
            }

            @Override
            public boolean onZoom(org.osmdroid.events.ZoomEvent event) {
                return false;
            }
        });

        // Use touch listener to detect when dragging ends
        binding.mapView.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                // Fake delay then update address
                binding.tvAddressLine1.setText("Vị trí tùy chỉnh");
                binding.tvAddressLine2.setText("Đã chọn từ bản đồ (" + 
                    String.format("%.4f", binding.mapView.getMapCenter().getLatitude()) + ", " + 
                    String.format("%.4f", binding.mapView.getMapCenter().getLongitude()) + ")");
            }
            return false;
        });

        // Back button
        binding.ivBack.setOnClickListener(v -> {
            if (navController != null) {
                navController.popBackStack();
            }
        });

        // Confirm button
        binding.btnConfirm.setOnClickListener(v -> {
            String address = binding.tvAddressLine1.getText().toString() + ", " + binding.tvAddressLine2.getText().toString();
            Bundle result = new Bundle();
            result.putString(ADDRESS_KEY, address);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
            
            if (navController != null) {
                navController.popBackStack();
            }
        });
        
        // Search functionality can be added later
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

    @Override
    protected void observeData() {
    }
}
