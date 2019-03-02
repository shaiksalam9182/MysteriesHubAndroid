package com.salam.naradh;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

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

    String phone,token,android_id = "";
    ArrayList<HashMap> postList,placesList,aliensList,moviesList;
    HashMap<String,String>  map;
    ImageView ivMain;
    TextView tvTitle;
    FrameLayout banner;
    int lastVisibleItem, totalItemCount = 0;
    DataAdapter postAdapter;
    boolean isLoading = false;

    TextView tvMore;
    boolean demouser  = true;

//    private static ViewPager mPager;
//    CircleIndicator indicator;

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

        ivMain = (ImageView)v.findViewById(R.id.img_mainOne);
        pdData = (ProgressBar)v.findViewById(R.id.pb_data);
        tvTitle = (TextView)v.findViewById(R.id.tv_thumb_title);
        banner = (FrameLayout)v.findViewById(R.id.banner_frame);
        tvMore = (TextView)v.findViewById(R.id.tv_more);

        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent more = new Intent(getContext(),MoreData.class);
                more.putExtra("type",type);
                getContext().startActivity(more);
            }
        });
        sd = getActivity().getSharedPreferences("Naradh", Context.MODE_PRIVATE);
        editor = sd.edit();

        phone = sd.getString("phone","");
        token = sd.getString("token","");
        android_id = sd.getString("android_id","");



        postList = new ArrayList<>();
        placesList = new ArrayList<>();
        aliensList = new ArrayList<>();
        moviesList = new ArrayList<>();

//        mPager = (ViewPager)v. findViewById(R.id.pager);
//        indicator = (CircleIndicator)v.findViewById(R.id.indicator);

        if (type.equalsIgnoreCase("Posts")){
            lastVisibleItem = 0;
            totalItemCount = 0;
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
                if (phone.equalsIgnoreCase("")){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Please login to contribute");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            startActivity(intent);
                            dialogInterface.dismiss();

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                }else {
                    getActivity().startActivity(new Intent(getActivity(),WriterActivity.class));
                }

            }
        });

        ivMain.getLayoutParams().width = deviceWidth;
        ivMain.getLayoutParams().height = (int) (deviceWidth/1.6);
        banner.getLayoutParams().height = (int) (deviceWidth/1.6);




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


            if (phone.equalsIgnoreCase("")){

                JSONObject data = new JSONObject();

                try {
                    data.put("token",token);
                    data.put("android_id",android_id);
                    data.put("skip","0");

                    PostHelper postHelper = new PostHelper(getActivity());
                    return  postHelper.Post(URLUtils.demoUserPostLimit,data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else {
                JSONObject data = new JSONObject();

                try {
                    data.put("phone",phone);
                    data.put("token",token);
                    data.put("android_id",android_id);
                    data.put("skip","0");

                    PostHelper postHelper = new PostHelper(getActivity());
                    return  postHelper.Post(URLUtils.readPostsLimit,data.toString());
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
            pdData.setVisibility(View.GONE);
//            Log.e("readPostsRes",jsonObject.toString());
            if (jsonObject!=null){
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    parseJsondata(jsonObject);
                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    if (jsonObject.optString("code").equalsIgnoreCase("500")){
                        Toast.makeText(getActivity(),jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                        Intent login = new Intent(getContext(),LoginActivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getContext().startActivity(login);
                        getActivity().finish();
                    }else {
                        Toast.makeText(getActivity(),jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(getActivity(),"Error occured",Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(getActivity(),"Server Error",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void parseJsondata(JSONObject jsonObject) {
        final JSONArray data = jsonObject.optJSONArray("data");
        JSONObject temp;
        HashMap<String,String> adMap;
        for (int i = 1;i<data.length();i++){
            map = new HashMap<String, String>();
            temp = data.optJSONObject(i);
            if (i%3==0){
                adMap = new HashMap<String, String>();
                adMap.put("type","ad");
                postList.add(adMap);
            }
            map.put("post_id",temp.optString("post_id"));
            map.put("title",temp.optString("title"));
            map.put("description",temp.optString("description"));
            map.put("post_by",temp.optString("post_by"));
            map.put("image",temp.optString("image"));
            map.put("type","data");
            map.put("likes_count",temp.optString("likes_count"));
            map.put("dis_likes_count",temp.optString("dis_likes_count"));

            postList.add(map);

        }

        Log.e("postList",postList.toString());

        Glide.with(getContext()).load(data.optJSONObject(0).optString("image")).into(ivMain);
        tvTitle.setText(data.optJSONObject(0).optString("title"));

        ivMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = data.optJSONObject(0).optString("post_id");
                String title = data.optJSONObject(0).optString("title");
                String description =  data.optJSONObject(0).optString("description");
                String type = "posts";

                Intent descView = new Intent(getContext(),DescriptionView.class);
                descView.putExtra("id",id);
                descView.putExtra("title",title);
                descView.putExtra("description",description);
                descView.putExtra("type",type);
                startActivity(descView);
            }
        });

//        mPager.setAdapter(new SliderAdapter(getActivity(),postList));
//        indicator.setViewPager(mPager);






        //rvPosts.setHasFixedSize(true);
        rvPosts.setNestedScrollingEnabled(true);
        rvPosts.setLayoutManager(new LinearLayoutManager(getActivity()));

        rvPosts.setAdapter(new LimitDataAdapter(postList,getContext(),"posts"));
//        rvPosts.setItemAnimator(new DefaultItemAnimator());


//        postAdapter = new DataAdapter(postList,getActivity(),"posts");
//        rvPosts.setAdapter(postAdapter);
//
//        rvPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//
//                LinearLayoutManager manager = (LinearLayoutManager)rvPosts.getLayoutManager();
//                Log.e("loadmore","onscrolled");
//                if (!isLoading){
//
//                    if (manager!=null && manager.findLastCompletelyVisibleItemPosition()==postList.size()-1){
//
//                        new AsyncSkipPost().execute(postList.size());
//                        isLoading = true;
//                    }
//                }
//
//            }
//        });


    }



    private class AsyncReadPlaces extends AsyncTask<Void,Void,JSONObject>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdData.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {

            if (phone.equalsIgnoreCase("")){
                JSONObject data = new JSONObject();

                try {
                    data.put("token",token);
                    data.put("skip","0");
                    data.put("android_id",android_id);

                    PostHelper postHelper = new PostHelper(getActivity());
                    return  postHelper.Post(URLUtils.demoUserPlaceLimit,data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else {
                JSONObject data = new JSONObject();

                try {
                    data.put("phone",phone);
                    data.put("token",token);
                    data.put("skip","0");
                    data.put("android_id",android_id);

                    PostHelper postHelper = new PostHelper(getActivity());
                    return  postHelper.Post(URLUtils.readPlacesLimit,data.toString());
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
            pdData.setVisibility(View.GONE);
            if (jsonObject!=null){
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    Log.e("placesRes",jsonObject.toString());
                    parsePlacesJsondata(jsonObject);
                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    if (jsonObject.optString("code").equalsIgnoreCase("500")){
                        Toast.makeText(getActivity(),jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                        Intent login = new Intent(getContext(),LoginActivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getContext().startActivity(login);
                        getActivity().finish();
                    }else {
                        Toast.makeText(getActivity(),jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getActivity(),"Error occured",Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(getActivity(),"Server Error",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void parsePlacesJsondata(JSONObject jsonObject) {
        final JSONArray data = jsonObject.optJSONArray("data");
        JSONObject temp;
        HashMap<String,String> adMap;
        for (int i = 1;i<data.length();i++){
            map = new HashMap<String, String>();
            temp = data.optJSONObject(i);
            if (i%3==0){
                adMap = new HashMap<String, String>();
                adMap.put("type","ad");
                postList.add(adMap);
            }
            map.put("place_id",temp.optString("place_id"));
            map.put("title",temp.optString("title"));
            map.put("description",temp.optString("description"));
            map.put("post_by",temp.optString("post_by"));
            map.put("image",temp.optString("image"));
            map.put("type","data");
            map.put("likes_count",temp.optString("likes_count"));
            map.put("dis_likes_count",temp.optString("dis_likes_count"));

            placesList.add(map);

        }

//        mPager.setAdapter(new SliderAdapter(getActivity(),placesList));
//        indicator.setViewPager(mPager);

        Glide.with(getContext()).load(data.optJSONObject(0).optString("image")).into(ivMain);
        tvTitle.setText(data.optJSONObject(0).optString("title"));

        ivMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = data.optJSONObject(0).optString("place_id");
                String title = data.optJSONObject(0).optString("title");
                String description =  data.optJSONObject(0).optString("description");
                String type = "places";

                Intent descView = new Intent(getContext(),DescriptionView.class);
                descView.putExtra("id",id);
                descView.putExtra("title",title);
                descView.putExtra("description",description);
                descView.putExtra("type",type);
                startActivity(descView);
            }
        });

        rvPosts.setHasFixedSize(true);
        rvPosts.setNestedScrollingEnabled(false);
        rvPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvPosts.setItemAnimator(new DefaultItemAnimator());
        rvPosts.setAdapter(new LimitDataAdapter(placesList,getContext(),"places"));


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

            if (phone.equalsIgnoreCase("")){
                JSONObject data = new JSONObject();

                try {
                    data.put("token",token);
                    data.put("skip","0");
                    data.put("android_id",android_id);

                    PostHelper postHelper = new PostHelper(getActivity());
                    return  postHelper.Post(URLUtils.demoUserAlienLimit,data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                JSONObject data = new JSONObject();

                try {
                    data.put("phone",phone);
                    data.put("token",token);
                    data.put("skip","0");
                    data.put("android_id",android_id);

                    PostHelper postHelper = new PostHelper(getActivity());
                    return  postHelper.Post(URLUtils.readALiensLimit,data.toString());
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
            pdData.setVisibility(View.GONE);
            if (jsonObject!=null){
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    Log.e("aliensRes",jsonObject.toString());
                    parseAliensJsondata(jsonObject);
                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    if (jsonObject.optString("code").equalsIgnoreCase("500")){
                        Toast.makeText(getActivity(),jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                        Intent login = new Intent(getContext(),LoginActivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getContext().startActivity(login);
                        getActivity().finish();
                    }else {
                        Toast.makeText(getActivity(),jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getActivity(),"Error occured",Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(getActivity(),"Server Error",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void parseAliensJsondata(JSONObject jsonObject) {
        final JSONArray data = jsonObject.optJSONArray("data");
        JSONObject temp;
        HashMap<String,String> adMap;
        for (int i = 1;i<data.length();i++){
            map = new HashMap<String, String>();
            temp = data.optJSONObject(i);
            if ( i%3==0){
                adMap = new HashMap<String, String>();
                adMap.put("type","ad");
                postList.add(adMap);
            }
            map.put("alienPost_id",temp.optString("alienPost_id"));
            map.put("title",temp.optString("title"));
            map.put("description",temp.optString("description"));
            map.put("post_by",temp.optString("post_by"));
            map.put("image",temp.optString("image"));
            map.put("type","data");
            map.put("likes_count",temp.optString("likes_count"));
            map.put("dis_likes_count",temp.optString("dis_likes_count"));

            aliensList.add(map);



        }
//        mPager.setAdapter(new SliderAdapter(getActivity(),aliensList));
//        indicator.setViewPager(mPager);

        Glide.with(getContext()).load(data.optJSONObject(0).optString("image")).into(ivMain);
        tvTitle.setText(data.optJSONObject(0).optString("title"));


        ivMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = data.optJSONObject(0).optString("alienPost_id");
                String title = data.optJSONObject(0).optString("title");
                String description =  data.optJSONObject(0).optString("description");
                String type = "aliens";

                Intent descView = new Intent(getContext(),DescriptionView.class);
                descView.putExtra("id",id);
                descView.putExtra("title",title);
                descView.putExtra("description",description);
                descView.putExtra("type",type);
                startActivity(descView);
            }
        });

        rvPosts.setHasFixedSize(true);
        rvPosts.setNestedScrollingEnabled(false);
        rvPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvPosts.setItemAnimator(new DefaultItemAnimator());
        rvPosts.setAdapter(new LimitDataAdapter(aliensList,getContext(),"aliens"));


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

            if (phone.equalsIgnoreCase("")){
                JSONObject data = new JSONObject();

                try {
                    data.put("token",token);
                    data.put("skip","0");
                    data.put("android_id",android_id);

                    PostHelper postHelper = new PostHelper(getActivity());
                    return  postHelper.Post(URLUtils.demoUserMovieLimit,data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                JSONObject data = new JSONObject();

                try {
                    data.put("phone",phone);
                    data.put("token",token);
                    data.put("skip","0");
                    data.put("android_id",android_id);

                    PostHelper postHelper = new PostHelper(getActivity());
                    return  postHelper.Post(URLUtils.readMoviesLimit,data.toString());
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
            pdData.setVisibility(View.GONE);
            if (jsonObject!=null){
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    Log.e("aliensRes",jsonObject.toString());
                    parseMoviesJsondata(jsonObject);
                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    if (jsonObject.optString("code").equalsIgnoreCase("500")){
                        Toast.makeText(getActivity(),jsonObject.optString("message"),Toast.LENGTH_LONG).show();

                        Intent login = new Intent(getContext(),LoginActivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getContext().startActivity(login);
                        getActivity().finish();
                    }else {
                        Toast.makeText(getActivity(),jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getActivity(),"Error occured",Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(getActivity(),"Server Error",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void parseMoviesJsondata(JSONObject jsonObject) {
        final JSONArray data = jsonObject.optJSONArray("data");
        JSONObject temp;
        HashMap<String,String> adMap;
        for (int i = 1;i<data.length();i++){
            map = new HashMap<String, String>();
            temp = data.optJSONObject(i);
            if ( i%3==0){
                adMap = new HashMap<String, String>();
                adMap.put("type","ad");
                postList.add(adMap);
            }
            map.put("movie_id",temp.optString("movie_id"));
            map.put("title",temp.optString("title"));
            map.put("description",temp.optString("description"));
            map.put("post_by",temp.optString("post_by"));
            map.put("image",temp.optString("image"));
            map.put("type","data");
            map.put("likes_count",temp.optString("likes_count"));
            map.put("dis_likes_count",temp.optString("dis_likes_count"));
            moviesList.add(map);
        }

//        mPager.setAdapter(new SliderAdapter(getActivity(),moviesList));
////        Log.e("dataList",datalist.toString());
//        indicator.setViewPager(mPager);
        Glide.with(getContext()).load(data.optJSONObject(0).optString("image")).into(ivMain);
        tvTitle.setText(data.optJSONObject(0).optString("title"));

        ivMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = data.optJSONObject(0).optString("movie_id");
                String title = data.optJSONObject(0).optString("title");
                String description =  data.optJSONObject(0).optString("description");
                String type = "movies";

                Intent descView = new Intent(getContext(),DescriptionView.class);
                descView.putExtra("id",id);
                descView.putExtra("title",title);
                descView.putExtra("description",description);
                descView.putExtra("type",type);
                startActivity(descView);
            }
        });


        rvPosts.setHasFixedSize(true);
        rvPosts.setNestedScrollingEnabled(false);
        rvPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvPosts.setItemAnimator(new DefaultItemAnimator());
        rvPosts.setAdapter(new LimitDataAdapter(moviesList,getContext(),"movies"));





    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CardView mCardView;
        ImageView ivPost;
        LinearLayout llText;
        TextView tvTitle,likesCount,disLikesCount;
        int deviceWidth,deviceHeight;


        public MyViewHolder(final View itemView) {
            super(itemView);
            mCardView = (CardView)itemView.findViewById(R.id.cv_post);
            ivPost = (ImageView)itemView.findViewById(R.id.iv_post_image);
            llText = (LinearLayout)itemView.findViewById(R.id.ll_post);
            tvTitle = (TextView)itemView.findViewById(R.id.tv_post);
            likesCount =(TextView)itemView.findViewById(R.id.tv_likes_count);
            disLikesCount = (TextView)itemView.findViewById(R.id.tv_dis_likes_count);

            deviceWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            deviceHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
//            plusLogo = (ImageView)itemView.findViewById(R.id.img_plus_logo);
        }
    }

    private class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;

        private boolean isLoading;
        private int visibleThreshold = 3;
        private int lastVisibleItem, totalItemCount;

        private OnLoadMoreListener mOnLoadMoreListener;

        public PostAdapter(){

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rvPosts.getLayoutManager();
            rvPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            });
        }

        public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.mOnLoadMoreListener = mOnLoadMoreListener;
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_post_view, parent, false);
                return new MyViewHolder(view);
            } else if (viewType == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.progress_bar, parent, false);
                return new LoadingViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder itemRowHolder, final int i) {
            if (itemRowHolder instanceof MyViewHolder) {
                MyViewHolder myViewHolder = (MyViewHolder) itemRowHolder;
                myViewHolder.mCardView.getLayoutParams().height = (int) (myViewHolder.deviceWidth/3.74);

                myViewHolder.ivPost.getLayoutParams().width = (myViewHolder.deviceWidth/100)*30;
                myViewHolder.ivPost.getLayoutParams().height = myViewHolder.mCardView.getLayoutParams().height;


                myViewHolder.llText.getLayoutParams().width = (myViewHolder.deviceWidth/100)*70;
                myViewHolder.llText.getLayoutParams().height = myViewHolder.mCardView.getLayoutParams().height;

                Glide.with(getContext()).load(postList.get(i).get("image").toString()).into(myViewHolder.ivPost);
                myViewHolder.tvTitle.setText(postList.get(i).get("title").toString());

                if (postList.get(i).get("likes_count").toString().equalsIgnoreCase("")||postList.get(i).get("likes_count")==null){
                    myViewHolder.likesCount.setText("Likes: 0");
                }else {
                    myViewHolder.likesCount.setText("Likes: "+postList.get(i).get("likes_count").toString());
                }

                if (postList.get(i).get("dis_likes_count").toString().equalsIgnoreCase("")||postList.get(i).get("dis_likes_count")==null){
                    myViewHolder.disLikesCount.setText("DisLikes: 0");
                }else {
                    myViewHolder.disLikesCount.setText("DisLikes: "+postList.get(i).get("dis_likes_count").toString());
                }

                myViewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            String id = postList.get(i).get("post_id").toString();
                            String title = postList.get(i).get("title").toString();
                            String description =  postList.get(i).get("description").toString();
                            String type = "posts";

                            Intent descView = new Intent(getContext(),DescriptionView.class);
                            descView.putExtra("id",id);
                            descView.putExtra("title",title);
                            descView.putExtra("description",description);
                            descView.putExtra("type",type);
                            getContext().startActivity(descView);


                    }
                });
            } else if (itemRowHolder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) itemRowHolder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }

        }

        @Override
        public int getItemViewType(int position) {
            return postList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @Override
        public int getItemCount() {
            return postList == null ? 0 : postList.size();
        }

        public void setLoaded() {
            isLoading = false;
        }
    }

    private class AsyncSkipPost extends AsyncTask<Integer,Void,JSONObject>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            postList.add(null);
            postAdapter.notifyItemInserted(postList.size());
        }

        @Override
        protected JSONObject doInBackground(Integer... integers) {

            postList.remove(postList.size()-1);
            int scrollPosition = postList.size();
            postAdapter.notifyItemRemoved(scrollPosition);
            int currentSize = scrollPosition;
            int nextLimit = currentSize+10;

            JSONObject data = new JSONObject();
            try {
                data.put("phone",phone);
                data.put("token",token);
                data.put("android_id",android_id);
                data.put("skip",integers[0]);
                PostHelper postHelper = new PostHelper(getContext());
                return postHelper.Post(URLUtils.readPosts,data.toString());
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
            Log.e("skipPost",jsonObject.toString());



            if (jsonObject.optString("status").equalsIgnoreCase("success")){
                final JSONArray data = jsonObject.optJSONArray("data");

                postAdapter.notifyItemRemoved(postList.size());
                JSONObject temp;
                for (int i = 1;i<data.length();i++){
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

                postAdapter.notifyDataSetChanged();
               isLoading = false;
            }


        }
    }
}
