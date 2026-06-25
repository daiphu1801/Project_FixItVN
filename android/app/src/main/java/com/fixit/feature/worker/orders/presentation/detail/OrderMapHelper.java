package com.fixit.feature.worker.orders.presentation.detail;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;
import java.util.Locale;

public class OrderMapHelper {
    private static final String TAG = "OrderMapHelper";
    private final MapView mapView;

    public OrderMapHelper(MapView mapView) {
        this.mapView = mapView;
    }

    public void onCreate(Bundle savedInstanceState) {
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
    }

    public void onResume() {
        if (mapView != null) {
            mapView.onResume();
        }
    }

    public void onPause() {
        if (mapView != null) {
            mapView.onPause();
        }
    }

    public void onDestroy() {
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    public void onLowMemory() {
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    public void loadMap(Context context, String address) {
        if (address == null || address.trim().isEmpty() || mapView == null) {
            return;
        }

        mapView.getMapAsync(googleMap -> {
            new Thread(() -> {
                try {
                    android.location.Geocoder geocoder = new android.location.Geocoder(context, Locale.getDefault());
                    List<android.location.Address> addresses = geocoder.getFromLocationName(address, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        android.location.Address loc = addresses.get(0);
                        LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());

                        new Handler(Looper.getMainLooper()).post(() -> {
                            if (mapView != null) {
                                googleMap.clear();
                                googleMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(address));
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Geocoding failed: " + e.getMessage());
                }
            }).start();
        });
    }
}
