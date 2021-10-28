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

public class RequestFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView2 = inflater.inflate(R.layout.fragment_request, container, false);
        ImageView anim = (ImageView) rootView2.findViewById(R.id.imgAnimation1);
        TextView responded = (TextView) rootView2.findViewById(R.id.responded);
        TextView noresponder= (TextView) rootView2.findViewById(R.id.noresponders);
        TextView num = (TextView) rootView2.findViewById(R.id.numberresponded);

        num.setVisibility(View.GONE);
        responded.setVisibility(View.GONE);
        noresponder.setVisibility(View.GONE);
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


        return rootView2;
    }





}
