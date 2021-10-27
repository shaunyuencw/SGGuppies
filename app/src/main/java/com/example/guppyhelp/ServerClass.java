package com.example.guppyhelp;

import android.content.Context;
import com.example.guppyhelp.R;

public class ServerClass {
    private static String getBaseURL(Context context){
        return "http://" + context.getResources().getString(R.string.server_ip_address) + "/SGGuppies/";
    }

    public static String getQueryURL(Context context, String queryLink){
        return getBaseURL(context) + queryLink;
    }

}
