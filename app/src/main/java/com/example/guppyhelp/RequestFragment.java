package com.example.guppyhelp;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

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
        TextView tx1 = (TextView) rootView.findViewById(R.id.sqltest);
        Connection connect;
        try{
            SQLConnector con = new SQLConnector();
            connect = con.connectionclass();

            String query ="SELECT * from request";
            Statement st = connect.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next())
            {
                tx1.setText(rs.getString(1)); // Basically column 1 of the sql
                // I insert a Value 10
            }  // and trying to set it to the textbox currently is "asdgf"



        } catch (Exception ex) {
            Log.e("ERror2345", ex.getMessage());
        }
        //tx1.setText("bob2");
        return rootView;

    }

    public void onButtonClick(Button view){
        Snackbar mySnackbar = Snackbar.make(view, "SOS message sent... sike u gon die", Snackbar.LENGTH_SHORT);
        mySnackbar.show();
    }
}