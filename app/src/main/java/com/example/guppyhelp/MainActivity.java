package com.example.guppyhelp;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    static PopupWindow popupWindow = null;
    Calendar calender;
    SimpleDateFormat simpledateformat;
    String Date;
    static boolean tabCooldown = false;

    public boolean getBool(){
        return tabCooldown;
    }
    public void toggleBool(){
        if(tabCooldown){
            tabCooldown = false;
        } else {
            tabCooldown = true;
        }
    }

    boolean SOS = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView) findViewById(R.id.textViewUsername)).setText("Hi, "+getIntent().getExtras().getString("username"));
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        LinearLayout dark = (LinearLayout) findViewById(R.id.darkfilter);
        dark.setVisibility(View.INVISIBLE);
    }

}

