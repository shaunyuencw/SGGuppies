package com.example.guppyhelp;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.guppyhelp.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    static PopupWindow popupWindow = null;


    boolean SOS = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        LinearLayout dark = (LinearLayout) findViewById(R.id.darkfilter);
        dark.setVisibility(View.INVISIBLE);



    }


    public void requestbuttonclicked(View view) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.request_confirmation, null);
        Spinner mySpinner = (Spinner) popupView.findViewById(R.id.emergency_type);
        Button requestbutton = (Button) findViewById(R.id.SOSButton);


        // create the popup window
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
            TextView ready = (TextView) findViewById(R.id.readystatus);
            TextView responded = (TextView) findViewById(R.id.responded);
            TextView noresponder= (TextView) findViewById(R.id.noresponders);
            TextView num = (TextView) findViewById(R.id.numberresponded);

            ready.setVisibility(View.VISIBLE);
            responded.setVisibility(View.GONE);
            noresponder.setVisibility(View.GONE);
            num.setVisibility(View.GONE);
            SOS = false;


        }

        if(popupWindow == null && SOS == true) {
            popupWindow = new PopupWindow(popupView, width, height, focusable);
            popupWindow.setOutsideTouchable(false);
            LinearLayout dark = (LinearLayout) findViewById(R.id.darkfilter);
            dark.setVisibility(View.VISIBLE);
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            popupWindow.setFocusable(true);
            popupWindow.update();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(popupView.getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.emergencytypes));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mySpinner.setAdapter(adapter);
        }

    }

    public void cancelrequest(View view){
        if(popupWindow!=null) {
            popupWindow.dismiss();
            popupWindow = null;
            LinearLayout dark = (LinearLayout) findViewById(R.id.darkfilter);
            dark.setVisibility(View.INVISIBLE);
            Button requestbutton = (Button) findViewById(R.id.SOSButton);
            requestbutton.setText("SOS");
            SOS = false;


        }

        //remove the request from the database??
    }

    public void requestaccepted(View view){
        popupWindow.dismiss();
        LinearLayout dark = (LinearLayout) findViewById(R.id.darkfilter);
        dark.setVisibility(View.INVISIBLE);
        TextView ready = (TextView) findViewById(R.id.readystatus);
        TextView responded = (TextView) findViewById(R.id.responded);
        TextView noresponder= (TextView) findViewById(R.id.noresponders);
        TextView num = (TextView) findViewById(R.id.numberresponded);

        ready.setVisibility(View.GONE);
        responded.setVisibility(View.VISIBLE);
        noresponder.setVisibility(View.VISIBLE);
        num.setVisibility(View.VISIBLE);


        popupWindow = null;

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View v1 = inflater.inflate(R.layout.fragment_request, null);
        ImageView anim = (ImageView) v1.findViewById(R.id.imgAnimation1);

        Handler hand = new Handler();
        Runnable runnableanim = new Runnable() {
            @Override
            public void run() {
                anim.animate().scaleX(8f).scaleY(8f).alpha(0f).setDuration(1000).withEndAction(new Runnable(){

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

        runnableanim.run();

    }





    //private Runnable runnableanim = new Runnable(){
      //  @Override
     //   public void run() {
       //     anim.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(1000).withEndAction(new Runnable(){

          //      @Override
           //     public void run(){
              //      anim.setScaleX(1f);
             //       anim.setScaleY(1f);
               //     anim.setAlpha(1f);
              //  }
           // });
         //   hand.postDelayed(runnableanim,1500);
       // }
    //};


}

