package com.example.guppyhelp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class reportaed extends AppCompatActivity {
    ImageView IDProf;
    Button Upload_Btn;
    Button submit;
    TextView description;
    int SELECT_PICTURE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportaed);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Report AED");
        submit = findViewById(R.id.reportButton);
        IDProf = findViewById(R.id.IdProf);
        Upload_Btn = findViewById(R.id.UploadBtn);
        description = findViewById(R.id.description);

        IDProf.setOnClickListener(v -> {
            //selectImage();
        });
        Upload_Btn.setOnClickListener(view -> imageChooser());
        submit.setOnClickListener(view -> {
            String aedId = getIntent().getExtras().getString("id");
            reportAEDSQL(view.getContext(), aedId);
        });
    }

    private void reportAEDSQL(Context context, String aedId){
        String desc = ((EditText) findViewById(R.id.description)).getText().toString();

        String reportStatus_sql = "UPDATE aedlocation " +
                "SET status = 'Pending', report_description = '"+desc+"'" +
                "WHERE objectid = '"+aedId+"'";

        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        StringRequest mStringRequest = new StringRequest(Request.Method.POST, ServerClass.getQueryURL(context, "run_query.php"), response -> {
            Toast.makeText(context, "Thank you for reporting", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }, error -> Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sql", reportStatus_sql);

                return params;
            }
        };

        mStringRequest.setShouldCache(false);
        mRequestQueue.add(mStringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setResult(RESULT_OK);
        finish();
        return true;
    }

    // this function is triggered when
    // the Select Image Button is clicked
    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        Intent chooserIntent = Intent.createChooser(i, "Select Picture");
        startActivity(chooserIntent);
    }

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    IDProf.setImageURI(selectedImageUri);
                }
            }
        }
    }
}