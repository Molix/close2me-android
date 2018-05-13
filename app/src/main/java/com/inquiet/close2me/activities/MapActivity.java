package com.inquiet.close2me.activities;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.inquiet.close2me.R;
import com.inquiet.close2me.utils.PermissionHelper;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int ZOOM_MIN = 16;
    private static final int ZOOM_MAX = 18;
    private static final int ZOOM_INITIAL = 17;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    private OnPermissionRequested onPermissionRequested;

    public interface OnPermissionRequested {
        public void onPermissionGranted();
        public void onPermissionDenied();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLocations().get(0);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), mMap.getCameraPosition().zoom));
            };
        };

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();;
        startLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }

    public void stopLocationUpdates() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(ZOOM_MIN);
        mMap.setMaxZoomPreference(ZOOM_MAX);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        initLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void initLocationUpdates() {
        if (PermissionHelper.needsToRequestLocationPermissions(this)) {
            PermissionHelper.requestLocationPermission(this);
        } else {
            mMap.setMyLocationEnabled(true);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(location.getLatitude(), location.getLongitude()), ZOOM_INITIAL)
                                );
                                double positionDelta = 0.0135;
                                double latitudeMin = location.getLatitude() - positionDelta;
                                double latitudeMax = location.getLatitude() + positionDelta;
                                double longitudeMin = location.getLongitude() - positionDelta;
                                double longitudeMax = location.getLongitude() + positionDelta;
                                LatLngBounds bounds = new LatLngBounds(
                                        new LatLng(latitudeMin, longitudeMin), new LatLng(latitudeMax, longitudeMax));
                                mMap.setLatLngBoundsForCameraTarget(bounds);
                            }
                        }
                    });

            mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(10000); // 10 secs
            mLocationRequest.setFastestInterval(2000); // 2 sec
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length != 1)
            return;

        int grantResult = grantResults[0];
        switch (requestCode) {
            case PermissionHelper.MILKAAPP_PERMISSION_LOCATION_REQUEST:
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    onPermissionRequested.onPermissionGranted();
                } else {
                    onPermissionRequested.onPermissionDenied();
                    PermissionHelper.notifyUserMustGrantPermissionsFromSettingsIfNeeded(this, requestCode);
                }
                break;
            default:
                break;
        }
    }

    public void checkLocationPermissionAndContinue(OnPermissionRequested callback) {
        if (PermissionHelper.needsToRequestLocationPermissions(this)) {
            onPermissionRequested = callback;
            PermissionHelper.requestLocationPermission(this);
        } else {
            callback.onPermissionGranted();
        }
    }
}
