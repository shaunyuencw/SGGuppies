package com.example.guppyhelp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.google.android.material.snackbar.Snackbar;

public class AEDDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aeddetails);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("AED Details");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
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