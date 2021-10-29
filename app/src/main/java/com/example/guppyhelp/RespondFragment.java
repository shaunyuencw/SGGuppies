package com.example.guppyhelp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RespondFragment extends Fragment {
    ListView zongweilist;
    private ArrayList<String> data = new ArrayList<String>();
    EditText comment;
    String person = null;
    private Location lastKnownLocation = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_respond, container, false);
        Resources res = getResources();
        zongweilist = (ListView) rootView.findViewById(R.id.SOSList2);
        person = getActivity().getIntent().getExtras().getString("username");
        getLastLocation();

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
                // TODO Get and populate ListView
                String tempStr;
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject messageObject = new JSONObject(jsonObject.getString("message"));
                    JSONArray items = messageObject.getJSONArray("data");

                    try {
                        for (int i = 0; i < items.length(); i++) {
                            tempStr = "";
                            JSONObject request = items.getJSONObject(i);

                            if (request.get("comments").equals("")){
                                tempStr += "No description included \n";
                            }
                            else{
                                tempStr += request.getString("comments") + "\n";
                            }
                            tempStr += request.get("type_of_emergency") + "\n Requested by " + request.getString("requester_username");
                            data.add(tempStr);
                        }
                        Toast.makeText(getActivity(), "Active requests updated", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    Log.e("Debug", response);
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                }

                mylistadapter my = new mylistadapter(getActivity(),R.layout.test,data);

                //zongweilist.setAdapter(new ArrayAdapter<>(getActivity(),R.layout.zongwei_listview_detail,items));
                zongweilist.setAdapter(my);

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
               vh.desc = (TextView) convertView.findViewById(R.id.testview);
               vh.desc.setText(getItem(position).toString());
               vh.button.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Toast.makeText(getContext(),"working" + position,Toast.LENGTH_SHORT).show();
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
        Button button;
    }
}