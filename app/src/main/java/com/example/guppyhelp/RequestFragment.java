package com.example.guppyhelp;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RequestFragment extends Fragment {
    static PopupWindow popupWindow = null;
    boolean SOS = false;

    Calendar calender;
    SimpleDateFormat simpledateformat;
    String Date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView2 = inflater.inflate(R.layout.fragment_request, container, false);
        View rootView3 = inflater.inflate(R.layout.request_confirmation, container, false);
        ImageView anim = (ImageView) rootView2.findViewById(R.id.imgAnimation1);
        TextView responded = (TextView) rootView2.findViewById(R.id.responded);
        TextView ready = (TextView) rootView2.findViewById(R.id.readystatus);
        TextView noresponder= (TextView) rootView2.findViewById(R.id.noresponders);
        TextView num = (TextView) rootView2.findViewById(R.id.numberresponded);

        num.setVisibility(View.GONE);
        responded.setVisibility(View.GONE);
        noresponder.setVisibility(View.GONE);
        ready.setVisibility(View.VISIBLE);
        Handler hand = new Handler();
        Runnable runnableanim = new Runnable() {
            @Override
            public void run() {
                anim.animate().scaleX(6f).scaleY(6f).alpha(0f).setDuration(1000).withEndAction(new Runnable(){

                    @Override
                    public void run(){
                        anim.setScaleX(1f);
                        anim.setScaleY(1f);
                        anim.setAlpha(1f);
                    }
                });
                hand.postDelayed(this, 1500);
            }

        };

        Button sos = (Button) rootView2.findViewById(R.id.SOSButton);




        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.request_confirmation, null);
                Spinner mySpinner = (Spinner) popupView.findViewById(R.id.emergency_type);
                Button requestbutton = (Button) rootView2.findViewById(R.id.SOSButton);
                Button accept = (Button) popupView.findViewById(R.id.acceptrequestbutton);
                ImageView cancel = (ImageView) popupView.findViewById(R.id.cancelreq);

                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = false; // lets taps outside the popup also dismiss it
                if(SOS == false){
                    //change to stop button
                    requestbutton.setText("Stop");
                    SOS = true;
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

                }

                if(popupWindow == null && SOS == true) {
                    popupWindow = new PopupWindow(popupView, width, height, focusable);
                    popupWindow.setOutsideTouchable(false);
                    LinearLayout dark = (LinearLayout) getActivity().findViewById(R.id.darkfilter);
                    dark.setVisibility(View.VISIBLE);
                    popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                    popupWindow.setFocusable(true);
                    popupWindow.update();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(popupView.getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.emergencytypes));
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mySpinner.setAdapter(adapter);
                }

                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LinearLayout dark = (LinearLayout) getActivity().findViewById(R.id.darkfilter);
                        dark.setVisibility(View.INVISIBLE);
                        TextView comments = (TextView) popupView.findViewById(R.id.comments);

                        ready.setVisibility(View.GONE);
                        responded.setVisibility(View.VISIBLE);
                        noresponder.setVisibility(View.VISIBLE);
                        num.setVisibility(View.VISIBLE);
                        popupWindow.dismiss();
                        popupWindow = null;
                        runnableanim.run();

                        calender = Calendar.getInstance();
                        simpledateformat = new SimpleDateFormat("dd-MM-yyyy");

                        //strings to send to database

                        Date = simpledateformat.format(calender.getTime());
                        String Comments = comments.toString();
                        String type = mySpinner. getSelectedItem(). toString();

                        //TODO insert to request table..


                    }
                });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(popupWindow!=null) {
                                popupWindow.dismiss();
                                popupWindow = null;
                                LinearLayout dark = (LinearLayout) getActivity().findViewById(R.id.darkfilter);
                                dark.setVisibility(View.INVISIBLE);
                                Button requestbutton = (Button) getActivity().findViewById(R.id.SOSButton);
                                requestbutton.setText("SOS");
                                SOS = false;

                            }
                        }
                    });

            }
        });



        return rootView2;
    }





}
