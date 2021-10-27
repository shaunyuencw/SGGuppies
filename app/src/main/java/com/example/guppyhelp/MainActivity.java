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

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.example.guppyhelp.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    static PopupWindow popupWindow = null;



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


        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = false; // lets taps outside the popup also dismiss it

        if(popupWindow == null) {
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
        }
    }

    public void requestaccepted(View view){
        popupWindow.dismiss();
        LinearLayout dark = (LinearLayout) findViewById(R.id.darkfilter);
        dark.setVisibility(View.INVISIBLE);

        //

        popupWindow = null;

        //
    }


}

