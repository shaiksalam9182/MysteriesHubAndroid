package com.salam.naradh;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class UrlHandler extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url_handler);


        if (getIntent()!=null){
            Log.e("handler_url",getIntent().getStringExtra("payload"));
            try {
                String data = getIntent().getStringExtra("payload");
                String[] dataarray = data.split("&&");
                String typeData = dataarray[0];
                String idData = dataarray[1];
                String[] typearray = typeData.split("=");
                String type = typearray[1];
                String[] idarray = idData.split("=");
                String id = idarray[1];
                Log.e("type,id", type + "\n" + id);

                Intent descriptionView = new Intent(UrlHandler.this,DescriptionView.class);
                descriptionView.putExtra("type",type);
                descriptionView.putExtra("id",id);
                descriptionView.putExtra("url","url");
                startActivity(descriptionView);
                finish();
            }catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }else {
            Log.e("handler_url","nothing");
        }
    }
}
