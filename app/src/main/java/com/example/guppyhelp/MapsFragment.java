package com.example.guppyhelp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Volley import
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsFragment extends Fragment {
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    Marker lastAccessedMarker = null;
    PopupWindow popupWindow = null;
    private Location lastKnownLocation = null;
    GoogleMap map;
    String person = null;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(final GoogleMap googleMap) {
            map = googleMap;

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    closeAEDDetailPanel();
                    showAEDDetails(marker);
                    return true;
                }
            });

            googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener(){
                @Override
                public boolean onMyLocationButtonClick()
                {
                    closeAEDDetailPanel();
                    updateSurroundingAED();
                    return false;
                }
            });

            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    closeAEDDetailPanel();
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

    // Reload surrounding AEDs
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                        @SuppressLint("ResourceType") final View myLocationButton = mapFragment.getView().findViewById(0x2);
                        myLocationButton.performClick();
                    }
                }
            });


    private void showAEDDetails(Marker marker){
        // TODO ADD TODAYS OPERATING TIME?
        // Increase AED icon size
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18.0f));
        String markerSnippet = marker.getSnippet();
        List<String> markerInfo = Arrays.asList(markerSnippet.split("&"));
        String status = markerInfo.get(3);
        if (status.equals("Available")){
            marker.setIcon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_aed_icon_2));
        } else if (status.equals("Unavailable")){
            marker.setIcon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_aed_unava_icon_2));
        } else if (status.equals("Pending")){
            marker.setIcon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_aed_pending_icon_2));
        }
        lastAccessedMarker = marker;
        // Inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        View popupView = inflater.inflate(R.layout.popup_aed, null);
        // Change text in popup
        TextView aedBuildingText = popupView.findViewById(R.id.pop_aedBuildingText);
        TextView aedLocationText = popupView.findViewById(R.id.pop_aedLocationText);
        aedBuildingText.setText(markerInfo.get(0));
        aedLocationText.setText(markerInfo.get(1));
        // Create onClickListener for buttons
        TextView moreDetailsButton = popupView.findViewById(R.id.pop_moreDetailButton);
        moreDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAEDDetails = new Intent(getActivity(), AEDDetailsActivity.class);
                intentAEDDetails.putExtra("id", lastAccessedMarker.getTitle());
                intentAEDDetails.putExtra("person", person);
                someActivityResultLauncher.launch(intentAEDDetails);
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
                closeAEDDetailPanel();
            }
        });
        // Show the popup window
        popupWindow.showAsDropDown(getView(), 0, -getView().getHeight()+popupView.getHeight());
    }

    private void closeAEDDetailPanel(){
        // Close AED popup
        if (lastAccessedMarker != null){
            String markerSnippet = lastAccessedMarker.getSnippet();
            String status = Arrays.asList(markerSnippet.split("&")).get(3);

            if (status.equals("Available")){
                lastAccessedMarker.setIcon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_aed_icon));
            } else if (status.equals("Unavailable")){
                lastAccessedMarker.setIcon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_aed_unava_icon));
            } else if (status.equals("Pending")){
                lastAccessedMarker.setIcon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_aed_pending_icon));
            }
            popupWindow.dismiss();
        }
    }

    private void get_aed_info(Context context){
        ServerClass serverClass = new ServerClass();
        Double lat = lastKnownLocation.getLatitude();
        Double lng = lastKnownLocation.getLongitude();

        String get_aed_SQL = "SELECT objectid, longtitude, latitude, building_n, aed_loca_1, operating_, status " +
                "FROM aedlocation " +
                "WHERE SQRT(POW(69.1 * (LATITUDE - "+lat+"), 2) + POW(69.1 * ("+lng+" - LONGTITUDE) * COS(LATITUDE / 57.3), 2)) <= 0.124274";

        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        StringRequest mStringRequest = new StringRequest(Request.Method.POST, serverClass.getQueryURL(context, "run_query.php"), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject messageObject = new JSONObject(jsonObject.getString("message"));
                    JSONArray items = messageObject.getJSONArray("data");

                    try {
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject aed = items.getJSONObject(i);

                            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(Double.parseDouble(aed.getString("latitude")), Double.parseDouble(aed.getString("longtitude"))))
                                    .title(aed.getString("objectid"))
                                    .snippet(aed.getString("building_n") + "&" + aed.getString("aed_loca_1") + "&" + aed.getString("operating_") + "&" + aed.getString("status"));

                            if (aed.getString("status").equals("Pending")){
                                markerOptions.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_aed_pending_icon));
                            }
                            else if (aed.getString("status").equals("Unavailable")){
                                markerOptions.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_aed_unava_icon));
                            }
                            else{
                                markerOptions.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_aed_icon));
                            }

                            map.addMarker(markerOptions);
                        }
                        Toast.makeText(getActivity(), "Map updated", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    Log.e("Debug", response);
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("sql", get_aed_SQL);

                return params;
            }
        };

        mStringRequest.setShouldCache(false);
        mRequestQueue.add(mStringRequest);
    }

    private void updateSurroundingAED(){
        getLastLocation();
        map.clear();
        lastAccessedMarker = null;
    }

    // Get path function
    private void getPath(Marker marker){
        LatLng destination, origin;
        destination = marker.getPosition();
        origin = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
        String url = "https://www.google.com/maps/dir/?api=1&origin=" + origin.latitude + "," + origin.longitude + "&destination=" + destination.latitude + "," + destination.longitude + "&travelmode=walking";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
        /*if(lastKnownLocation != null) {
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
        }*/
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
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 18.0f));
                            // Get AED from DB
                            Toast.makeText(getActivity(), "Updating map...", Toast.LENGTH_SHORT).show();
                            get_aed_info(getActivity());
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
        person = getActivity().getIntent().getExtras().getString("person");
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