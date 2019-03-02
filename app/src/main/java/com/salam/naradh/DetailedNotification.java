package com.salam.naradh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailedNotification extends AppCompatActivity {


    TextView tvToolbarTitle,tvTitle,tvDescription;
    ImageView imgBack;

    String title,description = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_notification);
        tvToolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        imgBack = (ImageView)findViewById(R.id.img_back);
        tvTitle = (TextView)findViewById(R.id.tv_title);
        tvDescription = (TextView)findViewById(R.id.tv_description);



        title = getIntent().getStringExtra("title");
        description = getIntent().getStringExtra("description");




        tvTitle.setText(title);
        tvDescription.setText(description);
        tvToolbarTitle.setText(title);




        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}
