package com.example.guppyhelp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    Marker lastAccessedMarker = null;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        @Override
        public void onMapReady(final GoogleMap googleMap) {

            /** LatLng sydney = new LatLng(-34, 151);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(test, 15.0f));
            */

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    closeAEDDetailPanel(lastAccessedMarker);
                    showAEDDetails(googleMap, marker);
                    return true;
                }
            });

            googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener(){
                @Override
                public boolean onMyLocationButtonClick()
                {
                    closeAEDDetailPanel(lastAccessedMarker);
                    updateSurroundingAED(googleMap);
                    return false;
                }
            });

            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    closeAEDDetailPanel(lastAccessedMarker);
                }
            });

            // Get myLocationButton
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            final View myLocationButton = mapFragment.getView().findViewById(0x2);
            // Change myLocationButton position
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) myLocationButton.getLayoutParams();
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
            rlp.setMargins(0,0,30,30);

            // Check if permission granted, enable setMyLocation & setMyLocationButton
            if(checkAndRequestPermissions()) {
                if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                } else {
                    Snackbar.make(getView(), "Please allow Location Access", Snackbar.LENGTH_SHORT).show();
                }
            }

            // When map is loaded, camera to current location
            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    myLocationButton.performClick();
                }
            });
        }
    };

    private void showAEDDetails(GoogleMap googleMap, Marker marker){
        // Show AED details
        Snackbar.make(getView(), "AED CLICKED", Snackbar.LENGTH_SHORT).show();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 16.0f));
        marker.setIcon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_aed_icon_2));
        lastAccessedMarker = marker;
    }

    private void closeAEDDetailPanel(Marker marker){
        if (marker != null){
            marker.setIcon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_aed_icon));
        }
    }

    private void updateSurroundingAED(GoogleMap googleMap){
        // SQL query to get all surrounding AED
        Snackbar.make(getView(), "UPDATE SURROUND AED", Snackbar.LENGTH_SHORT).show();
        googleMap.clear();
        lastAccessedMarker = null;
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(1.372072, 103.848963))
                .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_aed_icon)));
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(1.372136, 103.847054))
                .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_aed_icon)));
    }

    // Change vector to bitmap
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private  boolean checkAndRequestPermissions() {
        int locationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}