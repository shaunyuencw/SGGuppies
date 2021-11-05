package com.example.guppyhelp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class RequestFragment extends Fragment {
    static PopupWindow popupWindow = null;
    boolean SOS = false;

    final boolean[] isStillRefreshing = {false};
    Runnable runnableanim;
    String username;
    private Location lastKnownLocation = null;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        username = getActivity().getIntent().getExtras().getString("username");
        getRequestDetail(getContext());

        View rootView2 = inflater.inflate(R.layout.fragment_request, container, false);
        ImageView anim = rootView2.findViewById(R.id.imgAnimation1);
        TextView responded = rootView2.findViewById(R.id.responded);
        TextView ready = rootView2.findViewById(R.id.readystatus);
        TextView noresponder= rootView2.findViewById(R.id.noresponders);
        TextView num = rootView2.findViewById(R.id.numberresponded);
        Handler hand = new Handler();
        mSwipeRefreshLayout = rootView2.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if(!isStillRefreshing[0]){
                isStillRefreshing[0] = true;
                mSwipeRefreshLayout.setRefreshing(true);
                hand.removeCallbacks(runnableanim);
                getRequestDetail(getContext());
            }
            mSwipeRefreshLayout.setRefreshing(false);
        });

        num.setVisibility(View.GONE);
        responded.setVisibility(View.GONE);
        noresponder.setVisibility(View.GONE);
        ready.setVisibility(View.VISIBLE);

        runnableanim = new Runnable() {
            @Override
            public void run() {
                anim.animate().scaleX(6f).scaleY(6f).alpha(0f).setDuration(1000).withEndAction(() -> {
                    anim.setScaleX(1f);
                    anim.setScaleY(1f);
                    anim.setAlpha(1f);
                });
                hand.postDelayed(this, 1500);
            }
        };

        Button sos = rootView2.findViewById(R.id.SOSButton);

        sos.setOnClickListener(view -> {

            @SuppressLint("InflateParams") View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.request_confirmation, null);
            Spinner mySpinner = popupView.findViewById(R.id.emergency_type);
            @SuppressLint("CutPasteId") Button requestbutton = rootView2.findViewById(R.id.SOSButton);
            Button accept = popupView.findViewById(R.id.acceptrequestbutton);
            ImageView cancel = popupView.findViewById(R.id.cancelreq);

            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;

            if(!SOS){
                if(popupWindow == null){
                    popupWindow = new PopupWindow(popupView, width, height, false);
                    popupWindow.setOutsideTouchable(false);
                    LinearLayout dark = getActivity().findViewById(R.id.darkfilter);
                    dark.setVisibility(View.VISIBLE);
                    popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                    popupWindow.setFocusable(true);
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.setOnDismissListener(() -> {
                        popupWindow = null;
                        dark.setVisibility(View.INVISIBLE);
                    });
                    popupWindow.update();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(popupView.getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.emergencytypes));
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mySpinner.setAdapter(adapter);
                }
            }
            else{
                //change to SOS button
                requestbutton.setText("SOS");

                ready.setVisibility(View.VISIBLE);
                responded.setVisibility(View.GONE);
                noresponder.setVisibility(View.GONE);
                num.setVisibility(View.GONE);
                SOS = false;
                hand.removeCallbacks(runnableanim);

                // Update request status
                stop_request(getContext());
            }

            accept.setOnClickListener(view12 -> {
                // Change UI
                sentSOSUI();

                LinearLayout dark = getActivity().findViewById(R.id.darkfilter);
                dark.setVisibility(View.INVISIBLE);

                // Get location and update DB
                getLastLocation();
            });

            cancel.setOnClickListener(view1 -> {
                if(popupWindow!=null) {
                    popupWindow.dismiss();
                    popupWindow = null;
                    LinearLayout dark = getActivity().findViewById(R.id.darkfilter);
                    dark.setVisibility(View.INVISIBLE);
                    Button requestbutton1 = getActivity().findViewById(R.id.SOSButton);
                    requestbutton1.setText("SOS");
                    SOS = false;
                }
            });
        });

        return rootView2;
    }

    @SuppressLint("SetTextI18n")
    private void sentSOSUI(){
        TextView responded = getView().findViewById(R.id.responded);
        TextView ready = getView().findViewById(R.id.readystatus);
        TextView noresponder= getView().findViewById(R.id.noresponders);
        TextView num = getView().findViewById(R.id.numberresponded);
        Button requestbutton = getView().findViewById(R.id.SOSButton);

        requestbutton.setText("Stop");
        SOS = true;
        ready.setVisibility(View.GONE);
        responded.setVisibility(View.VISIBLE);
        noresponder.setVisibility(View.VISIBLE);
        num.setVisibility(View.VISIBLE);
        runnableanim.run();
    }

    private void closePopupWindow(){
        popupWindow.dismiss();
        popupWindow = null;
    }

    private void submit_request(Context context){
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String curDateTime = df.format(date);

        View popupView = popupWindow.getContentView();
        TextView comments = popupView.findViewById(R.id.comments);
        Spinner mySpinner = popupView.findViewById(R.id.emergency_type);
        String Comments = comments.getText().toString();
        String type = mySpinner.getSelectedItem().toString();

        closePopupWindow();

        Comments = Comments.replaceAll("['\"\\\\]", "\\\\$0");
        // requester_username, request_datetime, comments, type_of_emergency,
        // field_location, LONGTITUDE, LATITUDE
        String submit_request = "INSERT INTO request(requester_username, request_datetime, comments, type_of_emergency, field_location, LONGTITUDE, LATITUDE) " +
                "VALUES ('"+username+"', '"+curDateTime+"', '"+Comments+"', '"+type+"', " +
                ""+null+", "+lastKnownLocation.getLongitude()+", "+lastKnownLocation.getLatitude()+")";

        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        StringRequest mStringRequest = new StringRequest(Request.Method.POST, ServerClass.getQueryURL(context, "run_query.php"), response -> ((TextView) getView().findViewById(R.id.numberresponded)).setText("0"), error -> Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sql", submit_request);

                return params;
            }
        };

        mStringRequest.setShouldCache(false);
        mRequestQueue.add(mStringRequest);
    }

    private void stop_request(Context context){
        String stop_request_sql = "UPDATE request " +
                "SET status = 'Completed' " +
                "WHERE requester_username = '"+username+"' AND status = 'Active'";

        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        StringRequest mStringRequest = new StringRequest(Request.Method.POST, ServerClass.getQueryURL(context, "run_query.php"), response -> {
            // Request completed
        }, error -> Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sql", stop_request_sql);

                return params;
            }
        };

        mStringRequest.setShouldCache(false);
        mRequestQueue.add(mStringRequest);
    }

    private void getRequestDetail(Context context){
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, -30);
        c.add(Calendar.HOUR, 8);
        Date bef30dateTime = c.getTime();
        String bef30dateTimeStr = df.format(bef30dateTime);

        String get_request_detail_sql = "SELECT num_of_responder " +
                "FROM request " +
                "WHERE requester_username = '"+username+"' AND status = 'Active' AND request_datetime > '"+bef30dateTimeStr+"'";

        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        StringRequest mStringRequest = new StringRequest(Request.Method.POST, ServerClass.getQueryURL(context, "run_query.php"), response -> {

            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject messageObject = new JSONObject(jsonObject.getString("message"));
                JSONArray items = messageObject.getJSONArray("data");

                String numOfResponder = items.getJSONObject(0).getString("num_of_responder");
                ((TextView) getView().findViewById(R.id.numberresponded)).setText(numOfResponder);
                sentSOSUI();
                isStillRefreshing[0] = false;
            } catch (JSONException e) {
                Log.e("Debug", response);
            }

        }, error -> Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sql", get_request_detail_sql);

                return params;
            }
        };

        mStringRequest.setShouldCache(false);
        mRequestQueue.add(mStringRequest);
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationClient.getLastLocation()
            .addOnSuccessListener(location -> {
                // Update lastKnownLocation + update DB
                if (location != null) {
                    lastKnownLocation = location;
                    submit_request(getContext());
                }
            })
            .addOnFailureListener(e -> {
                Log.d("MapDemoActivity", "Error trying to get last GPS location");
                e.printStackTrace();
            });
    }
}
