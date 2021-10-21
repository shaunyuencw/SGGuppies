package com.example.guppyhelp;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectiontoSQL {
    Connection con;
    String username, pass, ip, port, database;

    public Connection connectionclass()
    {
        ip = "172.21.148.167";
        database = "cz2006";
        username = "kerubinz";
        pass ="K3rub1nz";
        port ="3306";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionUrl = null;

        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionUrl = "jdc:jtds:sqlserver://" + ip +":" + port + ";" + "databasename" + database+ ";user=" + username +";password=" + pass +";";
            connection = DriverManager.getConnection(ConnectionUrl);
        }
        catch(Exception ex){
            Log.e("ERror", ex.getMessage());

        }

        return connection;


    }

}
