package com.example.guppyhelp;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import static com.example.guppyhelp.MainActivity.popupWindow;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

public class RespondFragment extends Fragment {
    ListView zongweilist;
    String[] items;
    EditText comment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_respond, container, false);
        //comment = (EditText) rootView.findViewById(R.id.editTextTextPersonName2);
        Resources res = getResources();
        zongweilist = (ListView) rootView.findViewById(R.id.SOSList2);
        items = res.getStringArray(R.array.HelpReq);
        zongweilist.setAdapter(new ArrayAdapter<>(getActivity(),R.layout.zongwei_listview_detail,items));

        //tx1.setText("bob2");
        return rootView;
    }


}