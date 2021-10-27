package com.example.guppyhelp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;


import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView email = (TextView) findViewById(R.id.email);
        TextView password = (TextView) findViewById(R.id.password);

        Button login = (Button) findViewById(R.id.login);

        //admin
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().equals("admin") && password.getText().toString().equals("123")){
                    //correct account
                    openActivity(1);
                }else if(email.getText().toString().equals("user") && password.getText().toString().equals("123")){
                    openActivity(0);
                }else{
                    //incorrect
                    Snackbar.make(view, "YOU SHALL NOT PASS!!!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void openActivity(int num)
    {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        if(num == 1){
            bundle.putString("person", "admin");
        } else {
            bundle.putString("person", "user");
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }
}