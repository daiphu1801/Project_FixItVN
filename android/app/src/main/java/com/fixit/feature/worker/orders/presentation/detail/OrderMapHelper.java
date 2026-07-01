package com.fixit.feature.worker.orders.presentation.detail;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;
import java.util.Locale;

public class OrderMapHelper {
    private static final String TAG = "OrderMapHelper";
    private final MapView mapView;

    public OrderMapHelper(MapView mapView) {
        this.mapView = mapView;
        if (this.mapView != null) {
            this.mapView.setTileSource(TileSourceFactory.MAPNIK);
            this.mapView.setMultiTouchControls(true);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        // OSMDroid does not require onCreate
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
        // OSMDroid has no explicit onDestroy
    }

    public void onLowMemory() {
    }

    public void loadMap(Context context, String address) {
        if (address == null || address.trim().isEmpty() || mapView == null) {
            return;
        }

        new Thread(() -> {
            try {
                android.location.Geocoder geocoder = new android.location.Geocoder(context, Locale.getDefault());
                List<android.location.Address> addresses = geocoder.getFromLocationName(address, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    android.location.Address loc = addresses.get(0);
                    GeoPoint geoPoint = new GeoPoint(loc.getLatitude(), loc.getLongitude());

                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (mapView != null) {
                            mapView.getOverlays().clear();
                            Marker marker = new Marker(mapView);
                            marker.setPosition(geoPoint);
                            marker.setTitle(address);
                            mapView.getOverlays().add(marker);
                            mapView.getController().setCenter(geoPoint);
                            mapView.getController().setZoom(16.5);
                            mapView.invalidate();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Geocoding failed: " + e.getMessage());
            }
        }).start();
    }

    public void loadMapByCoords(double lat, double lng) {
        if (mapView == null) return;

        GeoPoint geoPoint = new GeoPoint(lat, lng);
        new Handler(Looper.getMainLooper()).post(() -> {
            if (mapView != null) {
                mapView.getOverlays().clear();
                Marker marker = new Marker(mapView);
                marker.setPosition(geoPoint);
                marker.setTitle("Địa điểm");
                mapView.getOverlays().add(marker);
                mapView.getController().setCenter(geoPoint);
                mapView.getController().setZoom(16.5);
                mapView.invalidate();
            }
        });
    }
}
