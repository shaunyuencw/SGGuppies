package com.example.guppyhelp;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.example.guppyhelp.MainActivity.popupWindow;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class RequestFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_request, container, false);
    }

    public void requestbuttonclicked(View view) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.request_confirmation, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = false; // lets taps outside the popup also dismiss it
        Dialog dialog = new Dialog(getContext());

        if(popupWindow == null) {
            popupWindow = new PopupWindow(popupView, width, height, focusable);
            popupWindow.setOutsideTouchable(false);
            LinearLayout dark = (LinearLayout) getView().findViewById(R.id.darkfilter);
            dark.setVisibility(View.VISIBLE);
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        }

    }

    public void cancelrequest(View view){
        if(popupWindow!=null) {
            popupWindow.dismiss();
            popupWindow = null;
            LinearLayout dark = (LinearLayout) getView().findViewById(R.id.darkfilter);
            dark.setVisibility(View.INVISIBLE);
        }
    }

    public void requestaccepted(View view){
        popupWindow.dismiss();
        LinearLayout dark = (LinearLayout) getView().findViewById(R.id.darkfilter);
        dark.setVisibility(View.INVISIBLE);
        popupWindow = null;
    }

}