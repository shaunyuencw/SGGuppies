package com.example.guppyhelp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

public class MapsFragment extends Fragment {
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    Marker lastAccessedMarker = null;
    PopupWindow popupWindow = null;
    private Location lastKnownLocation = null;
    GoogleMap map;
    Polyline polyline = null;

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
            map = googleMap;
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
            @SuppressLint("ResourceType") final View myLocationButton = mapFragment.getView().findViewById(0x2);
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

    private void showAEDDetails(GoogleMap googleMap, final Marker marker){
        // Increase AED icon size
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 16.0f));
        marker.setIcon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_aed_icon_2));
        lastAccessedMarker = marker;

        // Inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        View popupView = inflater.inflate(R.layout.popup_aed, null);
        // Change text in popup
        TextView aedLocationText = popupView.findViewById(R.id.pop_aedLocationText);
        TextView aedTimeText = popupView.findViewById(R.id.pop_aedTimeText);
        String markerSnippet = marker.getSnippet();
        List<String> markerInfo = Arrays.asList(markerSnippet.split(","));
        aedLocationText.setText(markerInfo.get(0));
        aedTimeText.setText(markerInfo.get(1));
        // Create onClickListener for buttons
        TextView moreDetailsButton = popupView.findViewById(R.id.pop_moreDetailButton);
        moreDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAEDDetails = new Intent(getActivity(), AEDDetailsActivity.class);
                startActivity(intentAEDDetails);
            }
        });
        TextView navigateButton = popupView.findViewById(R.id.pop_navigateButton);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPath(marker);
            }
        });
        // Create the popup window
        popupWindow = new PopupWindow();
        popupWindow.setContentView(popupView);
        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        // When tab is pressed, change icon back + dismiss popup
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                closeAEDDetailPanel(marker);
            }
        });
        // Show the popup window
        popupWindow.showAsDropDown(getView(), 0, -getView().getHeight()+popupView.getHeight());
    }

    private void closeAEDDetailPanel(Marker marker){
        // Close AED popup
        if (marker != null){
            marker.setIcon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_aed_icon));
            popupWindow.dismiss();
        }
    }

    private void updateSurroundingAED(GoogleMap googleMap){
        // SQL query to get all surrounding AED
        getLastLocation();
        if (lastKnownLocation != null){
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 16.0f));
        }
        googleMap.clear();
        lastAccessedMarker = null;
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(1.372072, 103.848963))
                .title("AED id 1")
                .snippet("TO BE CHANGED (location),TO BE CHANGED (time)")
                .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_aed_icon)));
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(1.372136, 103.847054))
                .title("AED id 2")
                .snippet("TO BE CHANGED (location),TO BE CHANGED (time)")
                .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_aed_icon)));
    }

    // Get path function
    private void getPath(Marker marker){
        LatLng destination, origin;
        destination = marker.getPosition();
        origin = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
        if(lastKnownLocation != null) {
            // Instantiates a new Polyline object and adds points to define a rectangle
            PolylineOptions polylineOptions = new PolylineOptions()
                    .add(new LatLng(origin.latitude, origin.longitude))
                    .add(new LatLng(destination.latitude, destination.longitude))
                    .color(Color.CYAN);  // North of the previous point, but at the same longitude
                    // Closes the polyline.

            // Get back the mutable Polyline
            if(polyline != null)
            {
                polyline.remove();
            }
            polyline = map.addPolyline(polylineOptions);
            closeAEDDetailPanel(marker);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 16.0f));
        }
        //String text = Double.toString(origin.longitude) + " " + Double.toString(origin.latitude);
        //Snackbar.make(getView(), text, Snackbar.LENGTH_SHORT).show();
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            lastKnownLocation = location;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
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