package com.example.guppyhelp;

import android.app.Dialog;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.guppyhelp.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {
    static PopupWindow popupWindow = null;
    PopupWindow form;

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


        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = false; // lets taps outside the popup also dismiss it
        Dialog dialog = new Dialog(this);

        if(popupWindow == null) {
            popupWindow = new PopupWindow(popupView, width, height, focusable);
            popupWindow.setOutsideTouchable(false);
            LinearLayout dark = (LinearLayout) findViewById(R.id.darkfilter);
            dark.setVisibility(View.VISIBLE);
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        }

    }

    public void cancelrequest(View view){
        if(popupWindow!=null) {
            popupWindow.dismiss();
            popupWindow = null;
            LinearLayout dark = (LinearLayout) findViewById(R.id.darkfilter);
            dark.setVisibility(View.INVISIBLE);
        }
    }

    public void requestaccepted(View view){
        popupWindow.dismiss();
        LinearLayout dark = (LinearLayout) findViewById(R.id.darkfilter);
        dark.setVisibility(View.INVISIBLE);
        popupWindow = null;
    }
}

