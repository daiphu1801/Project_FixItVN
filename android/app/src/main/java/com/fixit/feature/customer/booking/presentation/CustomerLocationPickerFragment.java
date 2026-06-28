package com.fixit.feature.customer.booking.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerLocationPickerBinding;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CustomerLocationPickerFragment extends BaseFragment<FragmentCustomerLocationPickerBinding> {

    public static final String REQUEST_KEY = "location_request";
    public static final String ADDRESS_KEY = "address_text";
    public static final String LATITUDE_KEY = "latitude";
    public static final String LONGITUDE_KEY = "longitude";

    private final android.os.Handler mapMoveHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable mapMoveRunnable;

    @NonNull
    @Override
    protected FragmentCustomerLocationPickerBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentCustomerLocationPickerBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Cấu hình osmdroid
        org.osmdroid.config.Configuration.getInstance().load(requireContext(), requireContext().getSharedPreferences("osmdroid", android.content.Context.MODE_PRIVATE));
        org.osmdroid.config.Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        // Khởi tạo MapView
        binding.mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        binding.mapView.setMultiTouchControls(true);

        // Vị trí mặc định (Hà Nội)
        org.osmdroid.util.GeoPoint startPoint = new org.osmdroid.util.GeoPoint(21.0285, 105.8542);
        binding.mapView.getController().setZoom(16.0);
        binding.mapView.getController().setCenter(startPoint);

        // Cập nhật text ban đầu
        updateAddressText(21.0285, 105.8542);

        // Khởi tạo Runnable cập nhật toạ độ sau khi dừng di chuyển bản đồ
        mapMoveRunnable = () -> {
            if (binding != null && binding.mapView != null) {
                double lat = binding.mapView.getMapCenter().getLatitude();
                double lng = binding.mapView.getMapCenter().getLongitude();
                updateAddressText(lat, lng);
            }
        };

        // Lắng nghe sự kiện di chuyển và zoom trên bản đồ (Debounce 600ms)
        binding.mapView.addMapListener(new org.osmdroid.events.MapListener() {
            @Override
            public boolean onScroll(org.osmdroid.events.ScrollEvent event) {
                mapMoveHandler.removeCallbacks(mapMoveRunnable);
                mapMoveHandler.postDelayed(mapMoveRunnable, 600);
                return false;
            }

            @Override
            public boolean onZoom(org.osmdroid.events.ZoomEvent event) {
                mapMoveHandler.removeCallbacks(mapMoveRunnable);
                mapMoveHandler.postDelayed(mapMoveRunnable, 600);
                return false;
            }
        });

        // Nút quay lại
        binding.ivBack.setOnClickListener(v -> {
            if (navController != null) {
                navController.popBackStack();
            }
        });

        // Tìm kiếm địa chỉ thực tế sử dụng Geocoder hoặc Nominatim API
        binding.ivSearch.setOnClickListener(v -> performSearch());
        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        // Nút xác nhận sử dụng vị trí
        binding.btnConfirm.setOnClickListener(v -> {
            double lat = binding.mapView.getMapCenter().getLatitude();
            double lng = binding.mapView.getMapCenter().getLongitude();
            String address = binding.tvAddressLine2.getText().toString();
            if (address.contains("Đang lấy") || address.isEmpty()) {
                address = "Khu vực Cầu Giấy, Hà Nội";
            }

            Bundle result = new Bundle();
            result.putString(ADDRESS_KEY, address);
            result.putDouble(LATITUDE_KEY, lat);
            result.putDouble(LONGITUDE_KEY, lng);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);

            if (navController != null) {
                navController.popBackStack();
            }
        });
    }

    private void performSearch() {
        String query = binding.etSearch.getText().toString().trim();
        if (query.isEmpty()) return;

        binding.tvAddressLine1.setText("Đang tìm kiếm...");
        binding.tvAddressLine2.setText(query);

        new Thread(() -> {
            // 1. Thử dùng Android Geocoder trước
            try {
                android.location.Geocoder geocoder = new android.location.Geocoder(requireContext(), java.util.Locale.getDefault());
                java.util.List<android.location.Address> addresses = geocoder.getFromLocationName(query, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    android.location.Address address = addresses.get(0);
                    double lat = address.getLatitude();
                    double lng = address.getLongitude();
                    String addressText = address.getAddressLine(0);

                    requireActivity().runOnUiThread(() -> {
                        org.osmdroid.util.GeoPoint searchPoint = new org.osmdroid.util.GeoPoint(lat, lng);
                        binding.mapView.getController().animateTo(searchPoint);
                        binding.tvAddressLine1.setText(address.getFeatureName() != null ? address.getFeatureName() : "Kết quả tìm kiếm");
                        binding.tvAddressLine2.setText(addressText);
                    });
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 2. Fallback Nominatim Search API nếu Geocoder thất bại
            try {
                String urlStr = "https://nominatim.openstreetmap.org/search?format=json&limit=1&q=" +
                        java.net.URLEncoder.encode(query, "UTF-8");
                java.net.URL url = new java.net.URL(urlStr);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", requireContext().getPackageName());

                if (conn.getResponseCode() == 200) {
                    java.io.InputStream in = new java.io.BufferedInputStream(conn.getInputStream());
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(in, "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    String json = sb.toString();
                    int latIndex = json.indexOf("\"lat\":\"");
                    int lonIndex = json.indexOf("\"lon\":\"");
                    int dispIndex = json.indexOf("\"display_name\":\"");

                    if (latIndex != -1 && lonIndex != -1 && dispIndex != -1) {
                        int latStart = latIndex + 7;
                        int latEnd = json.indexOf("\"", latStart);
                        double lat = Double.parseDouble(json.substring(latStart, latEnd));

                        int lonStart = lonIndex + 7;
                        int lonEnd = json.indexOf("\"", lonStart);
                        double lng = Double.parseDouble(json.substring(lonStart, lonEnd));

                        int dispStart = dispIndex + 16;
                        int dispEnd = json.indexOf("\"", dispStart);
                        String rawAddress = json.substring(dispStart, dispEnd);
                        String decodedAddress = decodeUnicode(rawAddress);

                        requireActivity().runOnUiThread(() -> {
                            org.osmdroid.util.GeoPoint searchPoint = new org.osmdroid.util.GeoPoint(lat, lng);
                            binding.mapView.getController().animateTo(searchPoint);
                            binding.tvAddressLine1.setText("Kết quả tìm kiếm");
                            binding.tvAddressLine2.setText(decodedAddress);
                        });
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Nếu không tìm thấy kết quả nào
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Không tìm thấy địa điểm này", Toast.LENGTH_SHORT).show();
                binding.tvAddressLine1.setText("Không tìm thấy");
                binding.tvAddressLine2.setText("Hãy thử tìm kiếm cụm từ khác.");
            });
        }).start();
    }

    private void updateAddressText(double lat, double lng) {
        binding.tvAddressLine1.setText("Đang lấy địa chỉ...");
        binding.tvAddressLine2.setText(String.format("Toạ độ: %.5f, %.5f", lat, lng));

        new Thread(() -> {
            // 1. Thử dùng Android Geocoder
            try {
                android.location.Geocoder geocoder = new android.location.Geocoder(requireContext(), java.util.Locale.getDefault());
                java.util.List<android.location.Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    android.location.Address address = addresses.get(0);
                    String addressText = address.getAddressLine(0);
                    String featureName = address.getFeatureName();

                    requireActivity().runOnUiThread(() -> {
                        binding.tvAddressLine1.setText(featureName != null ? featureName : "Vị trí đã chọn");
                        binding.tvAddressLine2.setText(addressText);
                    });
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 2. Fallback Nominatim Reverse Geocoding API
            try {
                String urlStr = String.format(java.util.Locale.US,
                        "https://nominatim.openstreetmap.org/reverse?format=json&lat=%.6f&lon=%.6f&accept-language=vi",
                        lat, lng);
                java.net.URL url = new java.net.URL(urlStr);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", requireContext().getPackageName());

                if (conn.getResponseCode() == 200) {
                    java.io.InputStream in = new java.io.BufferedInputStream(conn.getInputStream());
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(in, "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    String json = sb.toString();
                    int dispIndex = json.indexOf("\"display_name\":\"");
                    if (dispIndex != -1) {
                        int start = dispIndex + 16;
                        int end = json.indexOf("\"", start);
                        if (end != -1) {
                            String rawAddress = json.substring(start, end);
                            String decodedAddress = decodeUnicode(rawAddress);
                            requireActivity().runOnUiThread(() -> {
                                binding.tvAddressLine1.setText("Vị trí đã chọn");
                                binding.tvAddressLine2.setText(decodedAddress);
                            });
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Fallback cuối cùng
            requireActivity().runOnUiThread(() -> {
                binding.tvAddressLine1.setText("Vị trí đã chọn");
                binding.tvAddressLine2.setText(String.format("Khu vực hoạt động (%.5f, %.5f)", lat, lng));
            });
        }).start();
    }

    private String decodeUnicode(String unicodeStr) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < unicodeStr.length()) {
            char c = unicodeStr.charAt(i);
            if (c == '\\' && i + 1 < unicodeStr.length() && unicodeStr.charAt(i + 1) == 'u') {
                if (i + 5 < unicodeStr.length()) {
                    String hex = unicodeStr.substring(i + 2, i + 6);
                    try {
                        int code = Integer.parseInt(hex, 16);
                        sb.append((char) code);
                        i += 6;
                        continue;
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            }
            sb.append(c);
            i++;
        }
        return sb.toString();
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
        mapMoveHandler.removeCallbacks(mapMoveRunnable);
        if (binding != null && binding.mapView != null) {
            binding.mapView.onPause();
        }
    }

    @Override
    protected void observeData() {
    }
}
