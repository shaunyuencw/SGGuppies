package com.example.guppyhelp;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.material.snackbar.Snackbar;

public class RequestFragment extends Fragment {
    ListView zongweilist;
    String[] items;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_request, container, false);
        Button buttonClick = (Button)rootView.findViewById(R.id.sosButton);
        buttonClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClick((Button) view);
            }
        });

        Resources res = getResources();
        zongweilist = (ListView) rootView.findViewById(R.id.SOSList2);
        items = res.getStringArray(R.array.HelpReq);
        zongweilist.setAdapter(new ArrayAdapter<>(getActivity(),R.layout.zongwei_listview_detail,items));


        return rootView;
    }

    public void onButtonClick(Button view){
        Snackbar mySnackbar = Snackbar.make(view, "SOS message sent... sike u gon die", Snackbar.LENGTH_SHORT);
        mySnackbar.show();
    }
}