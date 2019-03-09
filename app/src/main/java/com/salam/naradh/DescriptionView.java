package com.salam.naradh;

import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class DescriptionView extends AppCompatActivity {

    ImageView imgBack,imgShare;
    TextView tvTitle,tvStatus;
    Editor editor;
    String title,description,id,type,phone,token,android_id,comingFrom = "";
    ImageView imgLike,imgDisLike;

    SharedPreferences sd;
    SharedPreferences.Editor edit;
    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_view);

        sd = getSharedPreferences("Naradh", Context.MODE_PRIVATE);
        edit  = sd.edit();

        phone = sd.getString("phone","");
        token = sd.getString("token","");
        android_id = sd.getString("android_id","");




//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = getIntent().getStringExtra("title");
        description = getIntent().getStringExtra("description");
        type = getIntent().getStringExtra("type");
        id = getIntent().getStringExtra("id");
        id = id.replace(" ","");
        comingFrom = getIntent().getStringExtra("url");


        imgBack = (ImageView)findViewById(R.id.img_back);
        imgShare = (ImageView)findViewById(R.id.img_share);
        tvStatus = (TextView)findViewById(R.id.tv_status);

        tvTitle = (TextView)findViewById(R.id.tv_title);
        editor = (Editor)findViewById(R.id.renderer);


        imgLike = (ImageView)findViewById(R.id.img_like);
        imgDisLike = (ImageView)findViewById(R.id.img_disl_like);


        if (phone.equalsIgnoreCase("")){
            imgLike.setVisibility(View.GONE);
            imgDisLike.setVisibility(View.GONE);
            adView = (AdView)findViewById(R.id.adView);
            adView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }else {
            imgLike.setVisibility(View.GONE);
            imgDisLike.setVisibility(View.GONE);
            new AsyncGetSuggestionsData().execute();

        }

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, URLUtils.base+type+"?id="+id);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        if (comingFrom!=null){
            if (phone.equalsIgnoreCase("")){
                imgLike.setVisibility(View.GONE);
                imgDisLike.setVisibility(View.GONE);
                adView = (AdView)findViewById(R.id.adView);
                adView.setVisibility(View.VISIBLE);
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
                new AsynGetData().execute();
            }else {
                imgLike.setVisibility(View.GONE);
                imgDisLike.setVisibility(View.GONE);
                new AsynGetData().execute();
                new AsyncGetSuggestionsData().execute();
                Toast.makeText(DescriptionView.this,"Here we will call new api along suggestion api",Toast.LENGTH_LONG).show();
            }

        }else {
            editor.render(description);
            tvTitle.setText(title);
        }




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

                    new AsyncLikePlace().execute();

                }else if (type.equalsIgnoreCase("aliens")){

                    new AsyncLikeAlien().execute();

                }else if (type.equalsIgnoreCase("movies")){

                    new AsyncLikeMovie().execute();
                }
            }
        });

        imgDisLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equalsIgnoreCase("posts")){

                    new AsyncDisLikePost().execute();

                }else if (type.equalsIgnoreCase("places")){

                    new AsyncDisLikePlace().execute();

                }else if (type.equalsIgnoreCase("aliens")){

                    new AsyncDisLikeAlien().execute();

                }else if (type.equalsIgnoreCase("movies")){

                    new AsyncDisLikeMovie().execute();
                }
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
                data.put("android_id",android_id);
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
                    if (jsonObject.optString("code").equalsIgnoreCase("500")){
                        Toast.makeText(DescriptionView.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                        Intent login = new Intent(DescriptionView.this,LoginActivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                       startActivity(login);
                       finish();
                    }else {
                        Toast.makeText(DescriptionView.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                    };
                }else {
                    Toast.makeText(DescriptionView.this,"Error occurred",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class AsyncLikePlace extends AsyncTask<Void,Void,JSONObject>{


        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject data = new JSONObject();
            try {
                data.put("phone",phone);
                data.put("token",token);
                data.put("place_id",id);
                data.put("android_id",android_id);
                PostHelper postHelper = new PostHelper(DescriptionView.this);
                return postHelper.Post(URLUtils.likePlace,data.toString());
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
                    if (jsonObject.optString("code").equalsIgnoreCase("500")){
                        Toast.makeText(DescriptionView.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                        Intent login = new Intent(DescriptionView.this,LoginActivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(login);
                        finish();
                    }else {
                        Toast.makeText(DescriptionView.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                    };
                }else {
                    Toast.makeText(DescriptionView.this,"Error occurred",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class AsyncLikeAlien extends AsyncTask<Void,Void,JSONObject>{



        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject data = new JSONObject();
            try {
                data.put("phone",phone);
                data.put("token",token);
                data.put("alienPost_id",id);
                data.put("android_id",android_id);
                PostHelper postHelper = new PostHelper(DescriptionView.this);
                return postHelper.Post(URLUtils.likeAlien,data.toString());
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
                    if (jsonObject.optString("code").equalsIgnoreCase("500")){
                        Toast.makeText(DescriptionView.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                        Intent login = new Intent(DescriptionView.this,LoginActivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(login);
                        finish();
                    }else {
                        Toast.makeText(DescriptionView.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                    };
                }else {
                    Toast.makeText(DescriptionView.this,"Error occurred",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class AsyncLikeMovie extends AsyncTask<Void,Void,JSONObject>{


        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject data = new JSONObject();
            try {
                data.put("phone",phone);
                data.put("token",token);
                data.put("movie_id",id);
                data.put("android_id",android_id);
                PostHelper postHelper = new PostHelper(DescriptionView.this);
                return postHelper.Post(URLUtils.likeMovie,data.toString());
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
                    if (jsonObject.optString("code").equalsIgnoreCase("500")){
                        Toast.makeText(DescriptionView.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                        Intent login = new Intent(DescriptionView.this,LoginActivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(login);
                        finish();
                    }else {
                        Toast.makeText(DescriptionView.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                    };
                }else {
                    Toast.makeText(DescriptionView.this,"Error occurred",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class AsyncDisLikePost extends AsyncTask<Void,Void,JSONObject>{
        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject data = new JSONObject();
            try {
                data.put("phone",phone);
                data.put("token",token);
                data.put("post_id",id);
                data.put("android_id",android_id);
                PostHelper postHelper = new PostHelper(DescriptionView.this);
                return postHelper.Post(URLUtils.disLikePost,data.toString());
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
                    if (jsonObject.optString("code").equalsIgnoreCase("500")){
                        Toast.makeText(DescriptionView.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                        Intent login = new Intent(DescriptionView.this,LoginActivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(login);
                        finish();
                    }else {
                        Toast.makeText(DescriptionView.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                    };
                }else {
                    Toast.makeText(DescriptionView.this,"Error occurred",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class AsyncDisLikePlace extends AsyncTask<Void,Void,JSONObject>{
        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject data = new JSONObject();
            try {
                data.put("phone",phone);
                data.put("token",token);
                data.put("place_id",id);
                data.put("android_id",android_id);
                PostHelper postHelper = new PostHelper(DescriptionView.this);
                return postHelper.Post(URLUtils.disLikePlace,data.toString());
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
                    if (jsonObject.optString("code").equalsIgnoreCase("500")){
                        Toast.makeText(DescriptionView.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                        Intent login = new Intent(DescriptionView.this,LoginActivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(login);
                        finish();
                    }else {
                        Toast.makeText(DescriptionView.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                    };
                }else {
                    Toast.makeText(DescriptionView.this,"Error occurred",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class AsyncDisLikeAlien extends AsyncTask<Void,Void,JSONObject>{
        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject data = new JSONObject();
            try {
                data.put("phone",phone);
                data.put("token",token);
                data.put("alienPost_id",id);
                data.put("android_id",android_id);
                PostHelper postHelper = new PostHelper(DescriptionView.this);
                return postHelper.Post(URLUtils.disLikeAlien,data.toString());
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
                    if (jsonObject.optString("code").equalsIgnoreCase("500")){
                        Toast.makeText(DescriptionView.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                        Intent login = new Intent(DescriptionView.this,LoginActivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(login);
                        finish();
                    }else {
                        Toast.makeText(DescriptionView.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                    };
                }else {
                    Toast.makeText(DescriptionView.this,"Error occurred",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class AsyncDisLikeMovie extends AsyncTask<Void,Void,JSONObject>{
        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject data = new JSONObject();
            try {
                data.put("phone",phone);
                data.put("token",token);
                data.put("movie_id",id);
                data.put("android_id",android_id);
                PostHelper postHelper = new PostHelper(DescriptionView.this);
                return postHelper.Post(URLUtils.disLikeMovie,data.toString());
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
                    if (jsonObject.optString("code").equalsIgnoreCase("500")){
                        Toast.makeText(DescriptionView.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                        Intent login = new Intent(DescriptionView.this,LoginActivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(login);
                        finish();
                    }else {
                        Toast.makeText(DescriptionView.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                    };
                }else {
                    Toast.makeText(DescriptionView.this,"Error occurred",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class AsyncGetSuggestionsData extends AsyncTask<Void,Void,JSONObject>{
        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject data = new JSONObject();
            try {
                data.put("phone",phone);
                data.put("token",token);
                data.put("android_id",android_id);
                data.put("post_id",id);
                data.put("type",type);

                PostHelper postHelper = new PostHelper(DescriptionView.this);
                Log.e("sendingData",data.toString());
                return postHelper.Post(URLUtils.verifySuggestion,data.toString());
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
                Log.e("suggestionRes",jsonObject.toString());
                if (jsonObject.optString("status").equalsIgnoreCase("success")){

                    if (jsonObject.optString("action").equalsIgnoreCase("nothing")){
                        imgLike.setVisibility(View.VISIBLE);
                        imgDisLike.setVisibility(View.VISIBLE);

                    }else {
                        adView = (AdView)findViewById(R.id.adView);
                        adView.setVisibility(View.VISIBLE);
                        AdRequest adRequest = new AdRequest.Builder().build();
                        adView.loadAd(adRequest);
                    }
                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    Toast.makeText(DescriptionView.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                }else {
                    imgDisLike.setVisibility(View.GONE);
                    imgLike.setVisibility(View.GONE);
                }
            }

        }
    }

    private class AsynGetData extends AsyncTask<Void,Void,JSONObject>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject data = new JSONObject();

            try {
                data.put("type",type);
                data.put("id","+"+id);
                Log.e("sendingData",data.toString());
                PostHelper postHelper = new PostHelper(DescriptionView.this);
                return postHelper.Post(URLUtils.getData,data.toString());
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
                Log.e("getDataRes",jsonObject.toString());
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    JSONObject data = jsonObject.optJSONObject("data");
                    tvTitle.setText(data.optString("title"));
                    editor.render(data.optString("description"));

                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    Toast.makeText(DescriptionView.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(DescriptionView.this,"Error occurred",Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(DescriptionView.this,"Something went wrong.Check your network connection",Toast.LENGTH_LONG).show();
            }
        }
    }
}
