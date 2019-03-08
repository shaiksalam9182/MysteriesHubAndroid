package com.salam.naradh;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.HashMap;

class LimitDataAdapter extends RecyclerView.Adapter<LimitDataAdapter.MyViewHolder> {

    ArrayList<HashMap> dataList;
    Context mContext;
    String mType = "";


    public LimitDataAdapter(ArrayList<HashMap> postList, Context context, String type) {
        dataList = postList;
        mContext = context;
        mType = type;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,  int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_final_post_view, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {


        myViewHolder.mCardView.getLayoutParams().height = (int) (myViewHolder.deviceWidth/3.74);


        if (dataList.get(i).get("type").toString().equalsIgnoreCase("ad")){
            AdRequest adRequest = new AdRequest.Builder().build();
            myViewHolder.adView.loadAd(adRequest);
            myViewHolder.adView.setAdListener(new AdListener(){

            });
            myViewHolder.adFrame.setVisibility(View.VISIBLE);
        }else {
            myViewHolder.adFrame.setVisibility(View.GONE);
            myViewHolder.ivPost.getLayoutParams().width = (int) (myViewHolder.mCardView.getLayoutParams().height/1.3);
            myViewHolder.ivPost.getLayoutParams().height = (int) (myViewHolder.mCardView.getLayoutParams().height/1.3);

            myViewHolder.tvTitle.setTextSize(myViewHolder.mCardView.getLayoutParams().height/15);
            myViewHolder.likesCount.setTextSize(myViewHolder.mCardView.getLayoutParams().height/15);
            myViewHolder.disLikesCount.setTextSize(myViewHolder.mCardView.getLayoutParams().height/15);


//        myViewHolder.llText.getLayoutParams().width = (myViewHolder.deviceWidth/100)*70;
//        myViewHolder.llText.getLayoutParams().height = myViewHolder.mCardView.getLayoutParams().height;

            Glide.with(mContext).load(dataList.get(i).get("image").toString()).into(myViewHolder.ivPost);
            myViewHolder.tvTitle.setText(dataList.get(i).get("title").toString());

            if (dataList.get(i).get("likes_count").toString().equalsIgnoreCase("")||dataList.get(i).get("likes_count")==null){
                myViewHolder.likesCount.setText("Likes: 0");
            }else {
                myViewHolder.likesCount.setText("Likes: "+dataList.get(i).get("likes_count").toString());
            }

            if (dataList.get(i).get("dis_likes_count").toString().equalsIgnoreCase("")||dataList.get(i).get("dis_likes_count")==null){
                myViewHolder.disLikesCount.setText("DisLikes: 0");
            }else {
                myViewHolder.disLikesCount.setText("DisLikes: "+dataList.get(i).get("dis_likes_count").toString());
            }

            myViewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mType.equalsIgnoreCase("posts")){
                        String id = dataList.get(i).get("post_id").toString();
                        String title = dataList.get(i).get("title").toString();
                        String description =  dataList.get(i).get("description").toString();
                        String type = "posts";

                        Intent descView = new Intent(mContext,DescriptionView.class);
                        descView.putExtra("id",id);
                        descView.putExtra("title",title);
                        descView.putExtra("description",description);
                        descView.putExtra("type",type);
                        mContext.startActivity(descView);

                    }else if (mType.equalsIgnoreCase("places")){
                        String id = dataList.get(i).get("place_id").toString();
                        String title = dataList.get(i).get("title").toString();
                        String description =  dataList.get(i).get("description").toString();
                        String type = "places";

                        Intent descView = new Intent(mContext,DescriptionView.class);
                        descView.putExtra("id",id);
                        descView.putExtra("title",title);
                        descView.putExtra("type",type);
                        descView.putExtra("description",description);
                        mContext.startActivity(descView);

                    }else if (mType.equalsIgnoreCase("aliens")){
                        String id = dataList.get(i).get("alienPost_id").toString();
                        String title = dataList.get(i).get("title").toString();
                        String description =  dataList.get(i).get("description").toString();
                        String type = "aliens";

                        Intent descView = new Intent(mContext,DescriptionView.class);
                        descView.putExtra("id",id);
                        descView.putExtra("title",title);
                        descView.putExtra("type",type);
                        descView.putExtra("description",description);
                        mContext.startActivity(descView);

                    }else if (mType.equalsIgnoreCase("movies")){
                        String id = dataList.get(i).get("movie_id").toString();
                        String title = dataList.get(i).get("title").toString();
                        String description =  dataList.get(i).get("description").toString();
                        String type = "movies";

                        Intent descView = new Intent(mContext,DescriptionView.class);
                        descView.putExtra("id",id);
                        descView.putExtra("type",type);
                        descView.putExtra("title",title);
                        descView.putExtra("description",description);
                        mContext.startActivity(descView);

                    }
                }
            });
        }





    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CardView mCardView;
        ImageView ivPost;
        LinearLayout llText;
        TextView tvTitle,likesCount,disLikesCount;
        int deviceWidth,deviceHeight;
        AdView adView;
        FrameLayout adFrame;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mCardView = (CardView)itemView.findViewById(R.id.cv_post);
            ivPost = (ImageView)itemView.findViewById(R.id.iv_post_image);
            llText = (LinearLayout)itemView.findViewById(R.id.ll_post);
            tvTitle = (TextView)itemView.findViewById(R.id.tv_post);
            likesCount =(TextView)itemView.findViewById(R.id.tv_likes_count);
            disLikesCount = (TextView)itemView.findViewById(R.id.tv_dis_likes_count);
            adView = (AdView)itemView.findViewById(R.id.adView);
            adFrame = (FrameLayout) itemView.findViewById(R.id.ad_frame);


            deviceWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            deviceHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        }
    }

}
