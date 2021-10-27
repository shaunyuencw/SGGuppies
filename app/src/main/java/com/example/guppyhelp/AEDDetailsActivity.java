package com.example.guppyhelp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class AEDDetailsActivity extends AppCompatActivity {
    String person = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aeddetails);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("AED Details");
        person = getIntent().getExtras().getString("person");
        if (person.equals("admin")){
            // Change button UI
            Button rButton = findViewById(R.id.reportButton);
            Button aButton = findViewById(R.id.availButton);
            Button uButton = findViewById(R.id.unavailButton);
            rButton.setVisibility(View.GONE);
            aButton.setVisibility(View.VISIBLE);
            uButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public void onAvailableButtonClicked(View view){
        // Query to update status
        Snackbar.make(view, "UPDATE AVAILABLE", Snackbar.LENGTH_SHORT).show();
    }

    public void onUnavailableButtonClicked(View view){
        // Query to update status
        Snackbar.make(view, "UPDATE UNAVAILABLE", Snackbar.LENGTH_SHORT).show();
    }

    public void onReportButtonClicked(View view) {
        openActivity();
    }

    public void openActivity()
    {
        //Get the bundle
        Bundle bundle = getIntent().getExtras();
        Intent intent = new Intent(this, reportaed.class);
        intent.putExtras(bundle);

//Fire that second activity
        startActivity(intent);
    }
}