package com.example.guppyhelp;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
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
}

