package com.example.guppyhelp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.guppyhelp.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {
    static boolean tabCooldown = false;

    public boolean getBool(){
        return tabCooldown;
    }
    public void toggleBool(){
        tabCooldown = !tabCooldown;
    }

    @SuppressLint("SetTextI18n")
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
        LinearLayout dark = findViewById(R.id.darkfilter);
        dark.setVisibility(View.INVISIBLE);
    }

}

