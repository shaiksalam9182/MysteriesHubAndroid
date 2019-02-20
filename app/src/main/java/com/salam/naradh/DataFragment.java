package com.salam.naradh;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import me.relex.circleindicator.CircleIndicator;


/**
 * A simple {@link Fragment} subclass.
 */
public class DataFragment extends Fragment {
    String type = "";
    TextView tvData;
    RelativeLayout rlSlider;
    RecyclerView rvPosts;
    int deviceWidth;
    FloatingActionButton fabWrite;
    SharedPreferences sd;
    SharedPreferences.Editor editor;

    ProgressBar pdData;

    String phone,token = "";
    ArrayList<HashMap> postList,placesList,aliensList,moviesList;
    HashMap<String,String>  map;

    private static ViewPager mPager;
    CircleIndicator indicator;

    public DataFragment() {
        // Required empty public constructor
    }


    public static DataFragment newInstance(String data) {

        Bundle args = new Bundle();
        args.putString("type",data);

        DataFragment fragment = new DataFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_data,container,false);
        // Inflate the layout for this fragment


        type = getArguments().getString("type");

        rlSlider = (RelativeLayout)v.findViewById(R.id.rl_slider);
        rvPosts =(RecyclerView)v.findViewById(R.id.rv_posts);

        pdData = (ProgressBar)v.findViewById(R.id.pb_data);

        sd = getActivity().getSharedPreferences("Naradh", Context.MODE_PRIVATE);
        editor = sd.edit();

        phone = sd.getString("phone","");
        token = sd.getString("token","");


        postList = new ArrayList<>();
        placesList = new ArrayList<>();
        aliensList = new ArrayList<>();
        moviesList = new ArrayList<>();

        mPager = (ViewPager)v. findViewById(R.id.pager);
        indicator = (CircleIndicator)v.findViewById(R.id.indicator);

        if (type.equalsIgnoreCase("Posts")){
            new AsyncReadPosts().execute();

        }else if (type.equalsIgnoreCase("Places")){
            new AsyncReadPlaces().execute();

        }else if (type.equalsIgnoreCase("Aliens")){

            new AsyncReadAliens().execute();

        }else if (type.equalsIgnoreCase("Movies")){

            new AsyncReadMovies().execute();
        }



        deviceWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        fabWrite = (FloatingActionButton)v.findViewById(R.id.fab_write);

        fabWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(),WriterActivity.class));
            }
        });

        rlSlider.getLayoutParams().width = deviceWidth;
        rlSlider.getLayoutParams().height = (int) (deviceWidth/1.6);


        return v;
    }

    private class AsyncReadPosts extends AsyncTask<Void,Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdData.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject data = new JSONObject();

            try {
                data.put("phone",phone);
                data.put("token",token);
                data.put("skip","0");

                PostHelper postHelper = new PostHelper(getActivity());
                return  postHelper.Post(URLUtils.readPosts,data.toString());
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
            pdData.setVisibility(View.GONE);
//            Log.e("readPostsRes",jsonObject.toString());
            if (jsonObject!=null){
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    parseJsondata(jsonObject);
                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    Toast.makeText(getActivity(),jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getActivity(),"Error occured",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void parseJsondata(JSONObject jsonObject) {
        JSONArray data = jsonObject.optJSONArray("data");
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

            postList.add(map);

        }

        mPager.setAdapter(new SliderAdapter(getActivity(),postList));
        indicator.setViewPager(mPager);

        rvPosts.setHasFixedSize(true);
        rvPosts.setNestedScrollingEnabled(false);
        rvPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvPosts.setItemAnimator(new DefaultItemAnimator());
        rvPosts.setAdapter(new DataAdapter(postList,getActivity(),"posts"));
//        Log.e("dataList",datalist.toString());

    }

    private class AsyncReadPlaces extends AsyncTask<Void,Void,JSONObject>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdData.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject data = new JSONObject();

            try {
                data.put("phone",phone);
                data.put("token",token);
                data.put("skip","0");

                PostHelper postHelper = new PostHelper(getActivity());
                return  postHelper.Post(URLUtils.readPlaces,data.toString());
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
            pdData.setVisibility(View.GONE);
            if (jsonObject!=null){
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    Log.e("placesRes",jsonObject.toString());
                    parsePlacesJsondata(jsonObject);
                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    Toast.makeText(getActivity(),jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getActivity(),"Error occured",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void parsePlacesJsondata(JSONObject jsonObject) {
        JSONArray data = jsonObject.optJSONArray("data");
        JSONObject temp;
        for (int i = 0;i<data.length();i++){
            map = new HashMap<String, String>();
            temp = data.optJSONObject(i);
            map.put("place_id",temp.optString("place_id"));
            map.put("title",temp.optString("title"));
            map.put("description",temp.optString("description"));
            map.put("post_by",temp.optString("post_by"));
            map.put("image",temp.optString("image"));
            map.put("likes_count",temp.optString("likes_count"));
            map.put("dis_likes_count",temp.optString("dis_likes_count"));

            placesList.add(map);

        }

        mPager.setAdapter(new SliderAdapter(getActivity(),placesList));
        indicator.setViewPager(mPager);

        rvPosts.setHasFixedSize(true);
        rvPosts.setNestedScrollingEnabled(false);
        rvPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvPosts.setItemAnimator(new DefaultItemAnimator());
        rvPosts.setAdapter(new DataAdapter(placesList,getActivity(),"places"));


//        Log.e("dataList",datalist.toString());
    }

    private class AsyncReadAliens extends AsyncTask<Void,Void,JSONObject>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdData.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject data = new JSONObject();

            try {
                data.put("phone",phone);
                data.put("token",token);
                data.put("skip","0");

                PostHelper postHelper = new PostHelper(getActivity());
                return  postHelper.Post(URLUtils.readAliens,data.toString());
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
            pdData.setVisibility(View.GONE);
            if (jsonObject!=null){
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    Log.e("aliensRes",jsonObject.toString());
                    parseAliensJsondata(jsonObject);
                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    Toast.makeText(getActivity(),jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getActivity(),"Error occured",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void parseAliensJsondata(JSONObject jsonObject) {
        JSONArray data = jsonObject.optJSONArray("data");
        JSONObject temp;
        for (int i = 0;i<data.length();i++){
            map = new HashMap<String, String>();
            temp = data.optJSONObject(i);
            map.put("alienPost_id",temp.optString("alienPost_id"));
            map.put("title",temp.optString("title"));
            map.put("description",temp.optString("description"));
            map.put("post_by",temp.optString("post_by"));
            map.put("image",temp.optString("image"));
            map.put("likes_count",temp.optString("likes_count"));
            map.put("dis_likes_count",temp.optString("dis_likes_count"));

            aliensList.add(map);



        }
        mPager.setAdapter(new SliderAdapter(getActivity(),aliensList));
        indicator.setViewPager(mPager);

        rvPosts.setHasFixedSize(true);
        rvPosts.setNestedScrollingEnabled(false);
        rvPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvPosts.setItemAnimator(new DefaultItemAnimator());
        rvPosts.setAdapter(new DataAdapter(aliensList,getActivity(),"aliens"));


//        Log.e("dataList",datalist.toString());
    }

    private class AsyncReadMovies extends AsyncTask<Void,Void,JSONObject>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdData.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject data = new JSONObject();

            try {
                data.put("phone",phone);
                data.put("token",token);
                data.put("skip","0");

                PostHelper postHelper = new PostHelper(getActivity());
                return  postHelper.Post(URLUtils.readMovies,data.toString());
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
            pdData.setVisibility(View.GONE);
            if (jsonObject!=null){
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    Log.e("aliensRes",jsonObject.toString());
                    parseMoviesJsondata(jsonObject);
                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    Toast.makeText(getActivity(),jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getActivity(),"Error occured",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void parseMoviesJsondata(JSONObject jsonObject) {
        JSONArray data = jsonObject.optJSONArray("data");
        JSONObject temp;
        for (int i = 0;i<data.length();i++){
            map = new HashMap<String, String>();
            temp = data.optJSONObject(i);
            map.put("movie_id",temp.optString("movie_id"));
            map.put("title",temp.optString("title"));
            map.put("description",temp.optString("description"));
            map.put("post_by",temp.optString("post_by"));
            map.put("image",temp.optString("image"));
            map.put("likes_count",temp.optString("likes_count"));
            map.put("dis_likes_count",temp.optString("dis_likes_count"));
            moviesList.add(map);
        }

        mPager.setAdapter(new SliderAdapter(getActivity(),moviesList));
//        Log.e("dataList",datalist.toString());
        indicator.setViewPager(mPager);


        rvPosts.setHasFixedSize(true);
        rvPosts.setNestedScrollingEnabled(false);
        rvPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvPosts.setItemAnimator(new DefaultItemAnimator());
        rvPosts.setAdapter(new DataAdapter(moviesList,getActivity(),"movies"));


    }
}
