package com.salam.naradh;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.irshulx.Editor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class DescriptionView extends AppCompatActivity {

    ImageView imgBack,imgShare;
    TextView tvTitle,tvStatus;
    Editor editor;
    String title,description,id,type,phone,token;
    ImageView imgLike,imgDisLike;

    SharedPreferences sd;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_view);

        sd = getSharedPreferences("Naradh", Context.MODE_PRIVATE);
        edit  = sd.edit();

        phone = sd.getString("phone","");
        token = sd.getString("token","");

//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = getIntent().getStringExtra("title");
        description = getIntent().getStringExtra("description");
        type = getIntent().getStringExtra("type");
        id = getIntent().getStringExtra("id");


        imgBack = (ImageView)findViewById(R.id.img_back);
        imgShare = (ImageView)findViewById(R.id.img_share);
        tvStatus = (TextView)findViewById(R.id.tv_status);

        tvTitle = (TextView)findViewById(R.id.tv_title);
        editor = (Editor)findViewById(R.id.renderer);


        imgLike = (ImageView)findViewById(R.id.img_like);
        imgDisLike = (ImageView)findViewById(R.id.img_disl_like);

        editor.render(description);
        tvTitle.setText(title);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equalsIgnoreCase("posts")){
                    new AsyncLikePost().execute();

                }else if (type.equalsIgnoreCase("places")){

                }else if (type.equalsIgnoreCase("aliens")){

                }else if (type.equalsIgnoreCase("movies")){

                }
            }
        });

        imgDisLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    private class AsyncLikePost extends AsyncTask<Void,Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject data = new JSONObject();
            try {
                data.put("phone",phone);
                data.put("token",token);
                data.put("post_id",id);
                PostHelper postHelper = new PostHelper(DescriptionView.this);
                return postHelper.Post(URLUtils.likePost,data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (jsonObject!=null){
                Log.e("likeRes",jsonObject.toString());
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    imgLike.setVisibility(View.GONE);
                    imgDisLike.setVisibility(View.GONE);
                    tvStatus.setVisibility(View.VISIBLE);
                    tvStatus.setText("Thanks for the feedback");
                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    Toast.makeText(DescriptionView.this,"Sorry, We are facing problems",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(DescriptionView.this,"Error occurred",Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
