package com.example.guppyhelp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class RespondFragment extends Fragment {
    ListView requestListView;
    private ArrayList<String> sendData = new ArrayList<String>();
    private HashMap<String, ArrayList<String>> allData = new HashMap<>();
    final boolean[] isStillRefreshing = {false};
    String person = null;
    private Location lastKnownLocation = null;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_respond, container, false);
        Resources res = getResources();
        requestListView = (ListView) rootView.findViewById(R.id.SOSList2);
        person = getActivity().getIntent().getExtras().getString("username");
        getLastLocation();
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh2);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isStillRefreshing[0]){
                    isStillRefreshing[0] = true;
                    mSwipeRefreshLayout.setRefreshing(true);
                    requestListView.setAdapter(null);
                    sendData = new ArrayList<String>();
                    allData = new HashMap<>();
                    getNDisplayRequests(getActivity());
                    mSwipeRefreshLayout.setRefreshing(false);
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                    return;
                }
            }
        });
        return rootView;
    }

    private void getNDisplayRequests(Context context){
        ServerClass serverClass = new ServerClass();

        Double lat = lastKnownLocation.getLatitude();
        Double lng = lastKnownLocation.getLongitude();

        String get_requests_sql = "SELECT request_id, requester_username, request_datetime, comments, type_of_emergency, longtitude, " +
                "latitude, num_of_responder FROM request " +
                "WHERE status = 'Active' AND request_datetime > DATE_SUB(NOW(), INTERVAL 30 MINUTE) AND requester_username <> '" + person +
                "' AND SQRT(POW(69.1 * (LATITUDE - " + lat + "), 2) + POW(69.1 * (" + lng + " - LONGTITUDE) * COS(LATITUDE / 57.3), 2)) <= 0.124274";

        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        StringRequest mStringRequest = new StringRequest(Request.Method.POST, serverClass.getQueryURL(context, "run_query.php"), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String tempStr;
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject messageObject = new JSONObject(jsonObject.getString("message"));
                    JSONArray items = messageObject.getJSONArray("data");

                    try {
                        for (int i = 0; i < items.length(); i++) {
                            tempStr = String.valueOf(i);
                            ArrayList<String> data = new ArrayList<String>();
                            JSONObject request = items.getJSONObject(i);

                            data.add(request.getString("request_id"));
                            if (request.get("comments").equals("")){
                                data.add("No description included");
                            }
                            else{
                                data.add(request.getString("comments"));
                            }
                            data.add(request.getString("type_of_emergency"));
                            data.add("Requested by: " + request.getString("requester_username"));

                            data.add(request.getString("latitude"));
                            data.add(request.getString("longtitude"));

                            sendData.add(tempStr);
                            allData.put(tempStr, data);

                            isStillRefreshing[0] = false;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    Log.e("Debug", response);
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                }

                mylistadapter my = new mylistadapter(getActivity(),R.layout.listview_item,sendData);
                requestListView.setAdapter(my);
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
                params.put("sql", get_requests_sql);

                return params;
            }
        };

        mStringRequest.setShouldCache(false);
        mRequestQueue.add(mStringRequest);
    }

    private class mylistadapter extends ArrayAdapter<String>{
        private int layout;
        public mylistadapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder main = null;
            if(convertView == null){
               LayoutInflater inflate = LayoutInflater.from(getContext());
               convertView = inflate.inflate(layout,parent,false);
               ViewHolder vh =new ViewHolder();
               vh.button = (Button) convertView.findViewById(R.id.button2);
               vh.desc = (TextView) convertView.findViewById(R.id.textViewDesc);
               vh.type = (TextView) convertView.findViewById(R.id.textViewType);
               vh.req = (TextView) convertView.findViewById(R.id.textViewRequest);
               vh.dist = (TextView) convertView.findViewById(R.id.textViewDist);
               ArrayList<String> curData = allData.get(String.valueOf(position));
               vh.desc.setText(curData.get(1));
               vh.type.setText(curData.get(2));
               vh.req.setText(curData.get(3));

               double lat1 = lastKnownLocation.getLatitude();
               double lon1 = lastKnownLocation.getLongitude();
               double lat2 = Double.parseDouble(curData.get(4));
               double lon2 = Double.parseDouble(curData.get(5));

               int R = 6371; // km
               double φ1 = lat1 * Math.PI/180; // φ, λ in radians
               double φ2 = lat2 * Math.PI/180;
               double Δφ = (lat2-lat1) * Math.PI/180;
               double Δλ = (lon2-lon1) * Math.PI/180;
               double a = Math.sin(Δφ/2) * Math.sin(Δφ/2) + Math.cos(φ1) * Math.cos(φ2) * Math.sin(Δλ/2) * Math.sin(Δλ/2);
               double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
               double dist = (R * c)*1000; // in metres
               double roundOffDist = Math.round(dist * 100.0) / 100.0;

               vh.dist.setText(String.valueOf(roundOffDist) + "m");

               vh.button.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       updateCounter(getContext(), String.valueOf(position));
                   }
               });
               convertView.setTag(vh);
            }else{
                main = (ViewHolder) convertView.getTag();
                main.desc.setText(getItem(position));
            }

            return convertView;
        }
    }

    private void updateCounter(Context context, String position){
        ServerClass serverClass = new ServerClass();

        String id = allData.get(position).get(0);
        Integer requestId = Integer.valueOf(id);

        String incrementNumOfResponderSql = "UPDATE request " +
                "SET num_of_responder = 1 + (SELECT num_of_responder FROM request WHERE request_id = "+requestId+")" +
                "WHERE request_id = "+requestId;

        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        StringRequest mStringRequest = new StringRequest(Request.Method.POST, serverClass.getQueryURL(context, "run_query.php"), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ArrayList<String> curData = allData.get(String.valueOf(position));
                LatLng destination = new LatLng(Double.parseDouble(curData.get(4)), Double.parseDouble(curData.get(5)));
                getPath(destination);
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
                params.put("sql", incrementNumOfResponderSql);

                return params;
            }
        };

        mStringRequest.setShouldCache(false);
        mRequestQueue.add(mStringRequest);
    }

    // Get path function
    private void getPath(LatLng destination){
        LatLng origin;
        origin = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
        String url = "https://www.google.com/maps/dir/?api=1&origin=" + origin.latitude + "," + origin.longitude + "&destination=" + destination.latitude + "," + destination.longitude + "&travelmode=walking";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
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
                    getNDisplayRequests(getActivity());
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

    public class ViewHolder{
        TextView desc;
        TextView type;
        TextView req;
        TextView dist;
        Button button;
    }
}