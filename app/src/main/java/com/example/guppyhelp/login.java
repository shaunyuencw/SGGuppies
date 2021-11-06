package com.example.guppyhelp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class login extends AppCompatActivity {
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    String person = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final boolean[] buttonIsPressed = {false};
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView email = findViewById(R.id.email);
        TextView password = findViewById(R.id.password);

        Button login = findViewById(R.id.login);

        login.setOnClickListener(view -> {
            if (!buttonIsPressed[0]){
                buttonIsPressed[0] = true;
                InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                boolean permission = false;
                boolean gps = false;
                // Check if permission granted, enable setMyLocation & setMyLocationButton
                if(checkAndRequestPermissions()) {
                    permission = true;
                } else {
                    Snackbar.make(view, "Please enable location permission", Snackbar.LENGTH_SHORT).show();
                }

                if(!isLocationEnabled(getBaseContext()))
                {
                    turnGPSOn();
                } else {
                    gps = true;
                }

                if(gps && permission){
                    if(email.getText().toString().equals("admin") && password.getText().toString().equals("123")){
                        //correct account
                        person = "admin";
                        openActivity();
                    }else if(!(email.getText().toString().equals("")) && password.getText().toString().equals("123")){
                        person = "user";
                        openActivity();
                    }else{
                        //incorrect
                        Snackbar.make(view, "YOU SHALL NOT PASS!!!", Snackbar.LENGTH_SHORT).show();
                    }
                }
                buttonIsPressed[0] = false;
            }
        });

    }
    public void openActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        if(person.equals("admin")){
            bundle.putString("person", "admin");
        } else {
            bundle.putString("person", "user");
        }
        TextView email = findViewById(R.id.email);
        bundle.putString("username", email.getText().toString());
        intent.putExtras(bundle);
        startActivity(intent);
    }
    private boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(locationManager);
    }
    private  boolean checkAndRequestPermissions() {
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
    private void turnGPSOn(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

// Setting Dialog Title
        alertDialog.setTitle("GPS is off");
// Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
// On pressing Settings button
        alertDialog.setPositiveButton("Settings", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        });

// on pressing cancel button
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
// Showing Alert Message
        alertDialog.show();
    }
}