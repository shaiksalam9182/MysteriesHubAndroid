package com.salam.naradh;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

class DataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<HashMap> dataList;
    Context mContext;
    String mType = "";


    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    public DataAdapter(ArrayList<HashMap> postList, FragmentActivity activity,String type) {
        dataList = postList;
        mContext = activity;
        mType = type;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i==VIEW_TYPE_ITEM){
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_final_post_view,viewGroup,false);
            return new ItemViewHolder(v);
        }else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progress_bar,viewGroup,false);
            return new LoadingViewHolder(v);
        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int i) {

//        itemRowHolder.mCardView.getLayoutParams().width =  itemRowHolder.deviceWidth;
        if (holder instanceof ItemViewHolder){
            ItemViewHolder itemRowHolder = (ItemViewHolder) holder;


            itemRowHolder.mCardView.getLayoutParams().height = (int) (itemRowHolder.deviceWidth/3.74);

            itemRowHolder.ivPost.getLayoutParams().width = (itemRowHolder.deviceWidth/100)*30;
            itemRowHolder.ivPost.getLayoutParams().height = itemRowHolder.mCardView.getLayoutParams().height;


            itemRowHolder.llText.getLayoutParams().width = (itemRowHolder.deviceWidth/100)*70;
            itemRowHolder.llText.getLayoutParams().height = itemRowHolder.mCardView.getLayoutParams().height;

            Glide.with(mContext).load(dataList.get(i).get("image").toString()).into(itemRowHolder.ivPost);
            itemRowHolder.tvTitle.setText(dataList.get(i).get("title").toString());

            if (dataList.get(i).get("likes_count").toString().equalsIgnoreCase("")||dataList.get(i).get("likes_count")==null){
                itemRowHolder.likesCount.setText("Likes: 0");
            }else {
                itemRowHolder.likesCount.setText("Likes: "+dataList.get(i).get("likes_count").toString());
            }

            if (dataList.get(i).get("dis_likes_count").toString().equalsIgnoreCase("")||dataList.get(i).get("dis_likes_count")==null){
                itemRowHolder.disLikesCount.setText("DisLikes: 0");
            }else {
                itemRowHolder.disLikesCount.setText("DisLikes: "+dataList.get(i).get("dis_likes_count").toString());
            }

            itemRowHolder.mCardView.setOnClickListener(new View.OnClickListener() {
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


        }else if (holder instanceof LoadingViewHolder){

            LoadingViewHolder itemRowHolder = (LoadingViewHolder) holder;

        }





    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }



    public class ItemViewHolder extends RecyclerView.ViewHolder{


        CardView mCardView;
        ImageView ivPost;
        LinearLayout llText;
        TextView tvTitle,likesCount,disLikesCount;
        int deviceWidth,deviceHeight;




        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            mCardView = (CardView)itemView.findViewById(R.id.cv_post);
            ivPost = (ImageView)itemView.findViewById(R.id.iv_post_image);
            llText = (LinearLayout)itemView.findViewById(R.id.ll_post);
            tvTitle = (TextView)itemView.findViewById(R.id.tv_post);
            likesCount =(TextView)itemView.findViewById(R.id.tv_likes_count);
            disLikesCount = (TextView)itemView.findViewById(R.id.tv_dis_likes_count);

            deviceWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            deviceHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder{

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progress_bar);
        }
    }



}
