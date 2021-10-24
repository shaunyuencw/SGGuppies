package com.example.guppyhelp;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectiontoSQL {
    public Connection connectionclass()
    {

        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://172.21.148.167:3306/cz2006", "kerubinz", "K3rub1nz");
            return connection;
        }
        catch(Exception ex){
            Log.e("ERror223", ex.getMessage());

        }
        return null;

    }

}
