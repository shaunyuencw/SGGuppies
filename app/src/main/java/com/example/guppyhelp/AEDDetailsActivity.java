package com.example.guppyhelp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AEDDetailsActivity extends AppCompatActivity {
    String person = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getAEDDetail(this);

        setContentView(R.layout.activity_aeddetails);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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
        // Query to update status to Available
        updateAEDStatus(this, 1);
    }

    public void onUnavailableButtonClicked(View view){
        // Query to update status to Unavailable
        updateAEDStatus(this,0);
    }

    private void getAEDDetail(Context context){
        String aedId = getIntent().getExtras().getString("id");
        String getAEDDetails_sql = "SELECT building_n, aed_loca_1, operating_, status " +
                "FROM aedlocation " +
                "WHERE objectid = "+aedId+"";

        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        StringRequest mStringRequest = new StringRequest(Request.Method.POST, ServerClass.getQueryURL(context, "run_query.php"), response -> {

            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject messageObject = new JSONObject(jsonObject.getString("message"));
                JSONArray items = messageObject.getJSONArray("data");

                JSONObject aed = items.getJSONObject(0);

                ((TextView) findViewById(R.id.buildingText)).setText(aed.getString("building_n"));
                ((TextView) findViewById(R.id.aedLocationText)).setText(aed.getString("aed_loca_1"));

                String timings = aed.getString("operating_");
                List<String> timerInfo = Arrays.asList(timings.split(","));
                ((TextView) findViewById(R.id.monTimeText)).setText(timerInfo.get(0));
                 ((TextView) findViewById(R.id.tueTimeText)).setText(timerInfo.get(1));
                 ((TextView) findViewById(R.id.wedTimeText)).setText(timerInfo.get(2));
                 ((TextView) findViewById(R.id.thuTimeText)).setText(timerInfo.get(3));
                 ((TextView) findViewById(R.id.friTimeText)).setText(timerInfo.get(4));
                 ((TextView) findViewById(R.id.satTimeText)).setText(timerInfo.get(5));
                 ((TextView) findViewById(R.id.sunTimeText)).setText(timerInfo.get(6));

                ((TextView) findViewById(R.id.statusText)).setText(aed.getString("status"));

            } catch (JSONException e) {
                Log.e("Debug", response);
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }

        }, error -> Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sql", getAEDDetails_sql);

                return params;
            }
        };

        mStringRequest.setShouldCache(false);
        mRequestQueue.add(mStringRequest);
    }

    private void updateAEDStatus(Context context, int num){
        String newStatus;
        if(num == 1){
            newStatus = "Available";
        } else {
            newStatus = "Unavailable";
        }
        String aedId = getIntent().getExtras().getString("id");
        String updateStatus_sql = "UPDATE aedlocation " +
                "SET status = '"+newStatus+"' " +
                "WHERE objectid = '"+aedId+"'";

        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        StringRequest mStringRequest = new StringRequest(Request.Method.POST, ServerClass.getQueryURL(context, "run_query.php"), response -> {
            if(num == 1){
                Toast.makeText(context, "Status updated to Available", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Status updated to Unavailable", Toast.LENGTH_SHORT).show();
            }
            setResult(RESULT_OK);
            finish();
        }, error -> Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sql", updateStatus_sql);

                return params;
            }
        };

        mStringRequest.setShouldCache(false);
        mRequestQueue.add(mStringRequest);
    }

    public void onReportButtonClicked(View view) {
        openActivity();
    }

    // After child intent complete, send back to parent to refresh
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    setResult(RESULT_OK);
                    finish();
                }
            });

    public void openActivity()
    {
        //Get the bundle
        Bundle bundle = getIntent().getExtras();
        Intent intent = new Intent(this, reportaed.class);
        intent.putExtras(bundle);

        //Fire that second activity
        someActivityResultLauncher.launch(intent);
    }
}