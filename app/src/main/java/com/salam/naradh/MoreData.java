package com.salam.naradh;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MoreData extends AppCompatActivity {
    
    RecyclerView rvMore;
    String type;
    SharedPreferences sd;
    SharedPreferences.Editor editor;
    String email,token,user_id = "";
    ArrayList<HashMap> dataList;
    HashMap<String,String> map;
    ProgressBar progressBar;
    ImageView imgBack;
    TextView toolbarTitle;
    boolean isLoading = false;
    DataAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_data);
        
        rvMore = (RecyclerView)findViewById(R.id.rv_more);
        
        type = getIntent().getStringExtra("type");
        
        dataList = new ArrayList<>();
        
        sd = getSharedPreferences("Naradh", Context.MODE_PRIVATE);
        editor = sd.edit();
        
        email = sd.getString("email","");
        token = sd.getString("token","");
        user_id = sd.getString("user_id","");
        
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);

        imgBack = (ImageView)findViewById(R.id.img_back);
        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        
        if (type.equalsIgnoreCase("Posts")){
            new AsyncGetPosts().execute();
            toolbarTitle.setText("Posts");
        }else if (type.equalsIgnoreCase("Places")){
            new AsyncGetPlaces().execute();
            toolbarTitle.setText("Places");
        }else if (type.equalsIgnoreCase("Aliens")){
            new AsyncGetAliens().execute();
            toolbarTitle.setText("Aliens");
        }else if (type.equalsIgnoreCase("Movies")){
            new AsyncGetMovies().execute();
            toolbarTitle.setText("Movies");
        }
        
        
        
        
        
        
    }

    private class AsyncGetPosts extends AsyncTask<Void,Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {

            if (email.equalsIgnoreCase("")){
                JSONObject data = new JSONObject();

                try {
                    data.put("token",token);
                    data.put("user_id",user_id);
                    data.put("skip","0");

                    PostHelper postHelper = new PostHelper(MoreData.this);
                    return  postHelper.Post(URLUtils.demoPost,data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else {
                JSONObject data = new JSONObject();

                try {
                    data.put("email",email);
                    data.put("token",token);
                    data.put("user_id",user_id);
                    data.put("skip","0");

                    PostHelper postHelper = new PostHelper(MoreData.this);
                    return  postHelper.Post(URLUtils.readPosts,data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }




            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            progressBar.setVisibility(View.GONE);
            if (jsonObject!=null){
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    parseJsondata(jsonObject);
                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    if (jsonObject.optString("code").equalsIgnoreCase("500")){
                        Toast.makeText(MoreData.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                        Intent login = new Intent(MoreData.this,LoginActivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(login);
                        finish();
                    }else {
                        Toast.makeText(MoreData.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(MoreData.this,"Error occured",Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(MoreData.this,"Server Error",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void parseJsondata(JSONObject jsonObject) {
        final JSONArray data = jsonObject.optJSONArray("data");
        JSONObject temp;
        for (int i = 0;i<data.length();i++){
            map = new HashMap<String, String>();
            temp = data.optJSONObject(i);
            map.put("post_id",temp.optString("post_id"));
            map.put("title",temp.optString("title"));
            map.put("description",temp.optString("description"));
            map.put("post_by",temp.optString("post_by"));
            map.put("image",temp.optString("image"));
            map.put("likes_count",temp.optString("likes_count"));
            map.put("dis_likes_count",temp.optString("dis_likes_count"));

            dataList.add(map);

        }



//        mPager.setAdapter(new SliderAdapter(MoreData.this,postList));
//        indicator.setViewPager(mPager);






        //rvPosts.setHasFixedSize(true);
        rvMore.setNestedScrollingEnabled(true);
        rvMore.setLayoutManager(new LinearLayoutManager(MoreData.this));
        dataAdapter = new DataAdapter(dataList,MoreData.this,"posts");
        rvMore.setAdapter(dataAdapter);
//        rvPosts.setItemAnimator(new DefaultItemAnimator());


//        postAdapter = new DataAdapter(postList,MoreData.this,"posts");
//        rvPosts.setAdapter(postAdapter);
//
        rvMore.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                LinearLayoutManager manager = (LinearLayoutManager)rvMore.getLayoutManager();
                Log.e("loadmore","onscrolled");
                if (!isLoading){

                    if (manager!=null && manager.findLastCompletelyVisibleItemPosition()==dataList.size()-1){

                        new AsyncSkipPost().execute(dataList.size());
                        isLoading = true;
                    }
                }

            }
        });


    }


    private class AsyncSkipPost extends AsyncTask<Integer,Void,JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dataList.add(null);
            dataAdapter.notifyItemInserted(dataList.size());
        }

        @Override
        protected JSONObject doInBackground(Integer... integers) {


//            int scrollPosition = dataList.size();
//            dataAdapter.notifyItemRemoved(scrollPosition);
//            int currentSize = scrollPosition;
//            int nextLimit = currentSize + 10;

            if (email.equalsIgnoreCase("")){
                JSONObject data = new JSONObject();
                try {

                    data.put("token", token);
                    data.put("user_id", user_id);
                    data.put("skip", integers[0]);
                    PostHelper postHelper = new PostHelper(MoreData.this);
                    return postHelper.Post(URLUtils.demoPost, data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                JSONObject data = new JSONObject();
                try {
                    data.put("email",email);
                    data.put("token", token);
                    data.put("user_id", user_id);
                    data.put("skip", integers[0]);
                    PostHelper postHelper = new PostHelper(MoreData.this);
                    return postHelper.Post(URLUtils.readPosts, data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            Log.e("skipPost", jsonObject.toString());


            if (jsonObject.optString("status").equalsIgnoreCase("success")) {
                final JSONArray data = jsonObject.optJSONArray("data");
                dataList.remove(dataList.size() - 1);
                dataAdapter.notifyItemRemoved(dataList.size());
                JSONObject temp;
                for (int i = 1; i < data.length(); i++) {
                    map = new HashMap<String, String>();
                    temp = data.optJSONObject(i);
                    map.put("post_id", temp.optString("post_id"));
                    map.put("title", temp.optString("title"));
                    map.put("description", temp.optString("description"));
                    map.put("post_by", temp.optString("post_by"));
                    map.put("image", temp.optString("image"));
                    map.put("likes_count", temp.optString("likes_count"));
                    map.put("dis_likes_count", temp.optString("dis_likes_count"));

                    dataList.add(map);

                }

                dataAdapter.notifyDataSetChanged();
                isLoading = false;
            }


        }
    }






    private class AsyncGetPlaces extends AsyncTask<Void,Void,JSONObject>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {

            if (email.equalsIgnoreCase("")){

                JSONObject data = new JSONObject();

                try {
                    data.put("token",token);
                    data.put("skip","0");
                    data.put("user_id",user_id);

                    PostHelper postHelper = new PostHelper(MoreData.this);
                    return  postHelper.Post(URLUtils.demoPlace,data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else {
                JSONObject data = new JSONObject();

                try {
                    data.put("email",email);
                    data.put("token",token);
                    data.put("skip","0");
                    data.put("user_id",user_id);

                    PostHelper postHelper = new PostHelper(MoreData.this);
                    return  postHelper.Post(URLUtils.readPlaces,data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            progressBar.setVisibility(View.GONE);
            if (jsonObject!=null){
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    Log.e("placesRes",jsonObject.toString());
                    parsePlacesJsondata(jsonObject);
                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    if (jsonObject.optString("code").equalsIgnoreCase("500")){
                        Toast.makeText(MoreData.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                        Intent login = new Intent(MoreData.this,LoginActivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(login);
                        finish();
                    }else {
                        Toast.makeText(MoreData.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(MoreData.this,"Error occured",Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(MoreData.this,"Server Error",Toast.LENGTH_LONG).show();
            }
        }
    }


    private void parsePlacesJsondata(JSONObject jsonObject) {
        final JSONArray data = jsonObject.optJSONArray("data");
        JSONObject temp;
        for (int i = 1;i<data.length();i++){
            map = new HashMap<String, String>();
            temp = data.optJSONObject(i);
            map.put("place_id",temp.optString("place_id"));
            map.put("title",temp.optString("title"));
            map.put("description",temp.optString("description"));
            map.put("post_by",temp.optString("post_by"));
            map.put("image",temp.optString("image"));
            map.put("likes_count",temp.optString("likes_count"));
            map.put("dis_likes_count",temp.optString("dis_likes_count"));

            dataList.add(map);

        }

//        mPager.setAdapter(new SliderAdapter(getActivity(),placesList));
//        indicator.setViewPager(mPager);



//        rvMore.setHasFixedSize(true);
        rvMore.setNestedScrollingEnabled(false);
        rvMore.setLayoutManager(new LinearLayoutManager(MoreData.this));
//        rvMore.setItemAnimator(new DefaultItemAnimator());
        dataAdapter = new DataAdapter(dataList,MoreData.this,"places");
        rvMore.setAdapter(dataAdapter);


        rvMore.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                LinearLayoutManager manager = (LinearLayoutManager)rvMore.getLayoutManager();
                Log.e("loadmore","onscrolled");
                if (!isLoading){

                    if (manager!=null && manager.findLastCompletelyVisibleItemPosition()==dataList.size()-1){

                        new AsyncSkipPlace().execute(dataList.size());
                        isLoading = true;
                    }
                }

            }
        });





//        Log.e("dataList",datalist.toString());
    }




    private class AsyncSkipPlace extends AsyncTask<Integer,Void,JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dataList.add(null);
            dataAdapter.notifyItemInserted(dataList.size());
        }

        @Override
        protected JSONObject doInBackground(Integer... integers) {


//            int scrollPosition = dataList.size();
//            dataAdapter.notifyItemRemoved(scrollPosition);
//            int currentSize = scrollPosition;
//            int nextLimit = currentSize + 10;

            if (email.equalsIgnoreCase("")){
                JSONObject data = new JSONObject();
                try {

                    data.put("token", token);
                    data.put("user_id", user_id);
                    data.put("skip", integers[0]);
                    PostHelper postHelper = new PostHelper(MoreData.this);
                    return postHelper.Post(URLUtils.demoPlace, data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                JSONObject data = new JSONObject();
                try {
                    data.put("email", email);
                    data.put("token", token);
                    data.put("user_id", user_id);
                    data.put("skip", integers[0]);
                    PostHelper postHelper = new PostHelper(MoreData.this);
                    return postHelper.Post(URLUtils.readPlaces, data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            Log.e("skipPost", jsonObject.toString());


            if (jsonObject.optString("status").equalsIgnoreCase("success")) {
                final JSONArray data = jsonObject.optJSONArray("data");
                dataList.remove(dataList.size() - 1);
                dataAdapter.notifyItemRemoved(dataList.size());
                JSONObject temp;
                for (int i = 1; i < data.length(); i++) {
                    map = new HashMap<String, String>();
                    temp = data.optJSONObject(i);
                    map.put("place_id", temp.optString("post_id"));
                    map.put("title", temp.optString("title"));
                    map.put("description", temp.optString("description"));
                    map.put("post_by", temp.optString("post_by"));
                    map.put("image", temp.optString("image"));
                    map.put("likes_count", temp.optString("likes_count"));
                    map.put("dis_likes_count", temp.optString("dis_likes_count"));

                    dataList.add(map);

                }

                dataAdapter.notifyDataSetChanged();
                isLoading = false;
            }


        }
    }







    private class AsyncGetAliens extends AsyncTask<Void,Void,JSONObject>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {

            if (email.equalsIgnoreCase("")){

                JSONObject data = new JSONObject();

                try {
                    data.put("token",token);
                    data.put("skip","0");
                    data.put("user_id",user_id);

                    PostHelper postHelper = new PostHelper(MoreData.this);
                    return  postHelper.Post(URLUtils.demoAlien,data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else {
                JSONObject data = new JSONObject();

                try {
                    data.put("email",email);
                    data.put("token",token);
                    data.put("skip","0");
                    data.put("user_id",user_id);

                    PostHelper postHelper = new PostHelper(MoreData.this);
                    return  postHelper.Post(URLUtils.readAliens,data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            progressBar.setVisibility(View.GONE);
            if (jsonObject!=null){
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    Log.e("aliensRes",jsonObject.toString());
                    parseAliensJsondata(jsonObject);
                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    if (jsonObject.optString("code").equalsIgnoreCase("500")){
                        Toast.makeText(MoreData.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                        Intent login = new Intent(MoreData.this,LoginActivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                       startActivity(login);
                        finish();
                    }else {
                        Toast.makeText(MoreData.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(MoreData.this,"Error occured",Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(MoreData.this,"Server Error",Toast.LENGTH_LONG).show();
            }
        }
    }


    private void parseAliensJsondata(JSONObject jsonObject) {
        final JSONArray data = jsonObject.optJSONArray("data");
        JSONObject temp;
        for (int i = 1;i<data.length();i++){
            map = new HashMap<String, String>();
            temp = data.optJSONObject(i);
            map.put("alienPost_id",temp.optString("alienPost_id"));
            map.put("title",temp.optString("title"));
            map.put("description",temp.optString("description"));
            map.put("post_by",temp.optString("post_by"));
            map.put("image",temp.optString("image"));
            map.put("likes_count",temp.optString("likes_count"));
            map.put("dis_likes_count",temp.optString("dis_likes_count"));

            dataList.add(map);



        }
//        mPager.setAdapter(new SliderAdapter(getActivity(),aliensList));
//        indicator.setViewPager(mPager);


//        rvMore.setHasFixedSize(true);
        rvMore.setNestedScrollingEnabled(true);
        rvMore.setLayoutManager(new LinearLayoutManager(MoreData.this));
//        rvMore.setItemAnimator(new DefaultItemAnimator());
        dataAdapter = new DataAdapter(dataList,MoreData.this,"aliens");
        rvMore.setAdapter(dataAdapter);


        rvMore.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                LinearLayoutManager manager = (LinearLayoutManager)rvMore.getLayoutManager();
                Log.e("loadmore","onscrolled");
                if (!isLoading){

                    if (manager!=null && manager.findLastCompletelyVisibleItemPosition()==dataList.size()-1){

                        new AsyncSkipAlien().execute(dataList.size());
                        isLoading = true;
                    }
                }

            }
        });

//        Log.e("dataList",datalist.toString());
    }


    private class AsyncSkipAlien extends AsyncTask<Integer,Void,JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dataList.add(null);
            dataAdapter.notifyItemInserted(dataList.size());
        }

        @Override
        protected JSONObject doInBackground(Integer... integers) {


//            int scrollPosition = dataList.size();
////            dataAdapter.notifyItemRemoved(scrollPosition);
////            int currentSize = scrollPosition;
////            int nextLimit = currentSize + 10;

            if (email.equalsIgnoreCase("")){

                JSONObject data = new JSONObject();
                try {
                    data.put("token", token);
                    data.put("user_id", user_id);
                    data.put("skip", integers[0]);
                    PostHelper postHelper = new PostHelper(MoreData.this);
                    return postHelper.Post(URLUtils.demoAlien, data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {

                JSONObject data = new JSONObject();
                try {
                    data.put("email", email);
                    data.put("token", token);
                    data.put("user_id", user_id);
                    data.put("skip", integers[0]);
                    PostHelper postHelper = new PostHelper(MoreData.this);
                    return postHelper.Post(URLUtils.readAliens, data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            Log.e("skipPost", jsonObject.toString());


            if (jsonObject.optString("status").equalsIgnoreCase("success")) {
                final JSONArray data = jsonObject.optJSONArray("data");
                dataList.remove(dataList.size() - 1);
                dataAdapter.notifyItemRemoved(dataList.size());
                JSONObject temp;
                for (int i = 1; i < data.length(); i++) {
                    map = new HashMap<String, String>();
                    temp = data.optJSONObject(i);
                    map.put("alienPost_id", temp.optString("post_id"));
                    map.put("title", temp.optString("title"));
                    map.put("description", temp.optString("description"));
                    map.put("post_by", temp.optString("post_by"));
                    map.put("image", temp.optString("image"));
                    map.put("likes_count", temp.optString("likes_count"));
                    map.put("dis_likes_count", temp.optString("dis_likes_count"));

                    dataList.add(map);

                }

                dataAdapter.notifyDataSetChanged();
                isLoading = false;
            }


        }
    }


    private class AsyncGetMovies extends AsyncTask<Void,Void,JSONObject>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {

            if (email.equalsIgnoreCase("")){
                JSONObject data = new JSONObject();

                try {
                    data.put("token",token);
                    data.put("skip","0");
                    data.put("user_id",user_id);

                    PostHelper postHelper = new PostHelper(MoreData.this);
                    return  postHelper.Post(URLUtils.demoMovie,data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                JSONObject data = new JSONObject();

                try {
                    data.put("email",email);
                    data.put("token",token);
                    data.put("skip","0");
                    data.put("user_id",user_id);

                    PostHelper postHelper = new PostHelper(MoreData.this);
                    return  postHelper.Post(URLUtils.readMovies,data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            progressBar.setVisibility(View.GONE);
            if (jsonObject!=null){
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    Log.e("aliensRes",jsonObject.toString());
                    parseMoviesJsondata(jsonObject);
                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    if (jsonObject.optString("code").equalsIgnoreCase("500")){
                        Toast.makeText(MoreData.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();

                        Intent login = new Intent(MoreData.this,LoginActivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(login);
                        finish();
                    }else {
                        Toast.makeText(MoreData.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(MoreData.this,"Error occurred",Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(MoreData.this,"Server Error",Toast.LENGTH_LONG).show();
            }
        }
    }


    private void parseMoviesJsondata(JSONObject jsonObject) {
        final JSONArray data = jsonObject.optJSONArray("data");
        JSONObject temp;
        for (int i = 1;i<data.length();i++){
            map = new HashMap<String, String>();
            temp = data.optJSONObject(i);
            map.put("movie_id",temp.optString("movie_id"));
            map.put("title",temp.optString("title"));
            map.put("description",temp.optString("description"));
            map.put("post_by",temp.optString("post_by"));
            map.put("image",temp.optString("image"));
            map.put("likes_count",temp.optString("likes_count"));
            map.put("dis_likes_count",temp.optString("dis_likes_count"));
            dataList.add(map);
        }

//        mPager.setAdapter(new SliderAdapter(getActivity(),moviesList));
////        Log.e("dataList",datalist.toString());
//        indicator.setViewPager(mPager);



//        rvMore.setHasFixedSize(true);
        rvMore.setNestedScrollingEnabled(false);
        rvMore.setLayoutManager(new LinearLayoutManager(MoreData.this));
        rvMore.setItemAnimator(new DefaultItemAnimator());
        dataAdapter = new DataAdapter(dataList,MoreData.this,"movies");
        rvMore.setAdapter(dataAdapter);


        rvMore.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                LinearLayoutManager manager = (LinearLayoutManager)rvMore.getLayoutManager();
                Log.e("loadmore","onscrolled");
                if (!isLoading){

                    if (manager!=null && manager.findLastCompletelyVisibleItemPosition()==dataList.size()-1){

                        new AsyncSkipMovie().execute(dataList.size());
                        isLoading = true;
                    }
                }

            }
        });




    }

    private class AsyncSkipMovie extends AsyncTask<Integer,Void,JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dataList.add(null);
            dataAdapter.notifyItemInserted(dataList.size());
        }

        @Override
        protected JSONObject doInBackground(Integer... integers) {

//
//            int scrollPosition = dataList.size();
//            dataAdapter.notifyItemRemoved(scrollPosition);
//            int currentSize = scrollPosition;
//            int nextLimit = currentSize + 10;

            if (email.equalsIgnoreCase("")){
                JSONObject data = new JSONObject();
                try {
                    data.put("token", token);
                    data.put("user_id", user_id);
                    data.put("skip", integers[0]);
                    PostHelper postHelper = new PostHelper(MoreData.this);
                    return postHelper.Post(URLUtils.demoMovie, data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                JSONObject data = new JSONObject();
                try {
                    data.put("email", email);
                    data.put("token", token);
                    data.put("user_id", user_id);
                    data.put("skip", integers[0]);
                    PostHelper postHelper = new PostHelper(MoreData.this);
                    return postHelper.Post(URLUtils.readMovies, data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            Log.e("skipPost", jsonObject.toString());


            if (jsonObject.optString("status").equalsIgnoreCase("success")) {
                final JSONArray data = jsonObject.optJSONArray("data");
                dataList.remove(dataList.size() - 1);
                dataAdapter.notifyItemRemoved(dataList.size());
                JSONObject temp;
                for (int i = 1; i < data.length(); i++) {
                    map = new HashMap<String, String>();
                    temp = data.optJSONObject(i);
                    map.put("movie_id", temp.optString("post_id"));
                    map.put("title", temp.optString("title"));
                    map.put("description", temp.optString("description"));
                    map.put("post_by", temp.optString("post_by"));
                    map.put("image", temp.optString("image"));
                    map.put("likes_count", temp.optString("likes_count"));
                    map.put("dis_likes_count", temp.optString("dis_likes_count"));

                    dataList.add(map);

                }

                dataAdapter.notifyDataSetChanged();
                isLoading = false;
            }


        }
    }
}
