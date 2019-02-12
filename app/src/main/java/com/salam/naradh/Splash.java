package com.salam.naradh;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Splash extends AppCompatActivity {

    SharedPreferences sd;
    SharedPreferences.Editor editor;
    String phone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        sd = getSharedPreferences("Naradh", Context.MODE_PRIVATE);
        editor = sd.edit();


        phone = sd.getString("phone","");


        Thread logoTimer=new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                finally{
                    if (phone.equalsIgnoreCase("")){
                        startActivity(new Intent(Splash.this,LoginActivity.class));
                        finish();
                    }else {
                        startActivity(new Intent(Splash.this,HomeActivity.class));
                        finish();
                    }
                }
            }

        };

        logoTimer.start();

    }
}
