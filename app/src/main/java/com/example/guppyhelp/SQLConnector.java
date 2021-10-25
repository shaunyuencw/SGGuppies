package com.example.guppyhelp;


import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQLConnector {
    public Connection connectionclass()
    {

        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://172.21.148.167:3306/cz2006?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=GMT", "kerubinz", "K3rub1nz");
            return connection;
        }
        catch(Exception ex){
            Log.e("ERror223", ex.getMessage());
            ex.printStackTrace();

        }
        return null;

    }

}




