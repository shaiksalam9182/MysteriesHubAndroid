package com.salam.naradh;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

class SliderAdapter extends PagerAdapter {

    Context mContext;
    ArrayList<HashMap> mBannerData;
    private LayoutInflater inflater;


    public SliderAdapter(FragmentActivity activity, ArrayList<HashMap> bannerData) {
        mContext = activity;
        mBannerData =  bannerData;
        inflater = LayoutInflater.from(activity);
        Log.e("slideradpter","called"+mBannerData);
    }


    @Override
    public int getCount() {
        return mBannerData.size()-2;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        View v = inflater.inflate(R.layout.slider_view,container,false);
        ImageView img = (ImageView)v.findViewById(R.id.image_banner);
        TextView tvTitle = (TextView)v.findViewById(R.id.tv_thumb_title);
        TextView tvDesc = (TextView)v.findViewById(R.id.tv_thumb_desc);

        LinearLayout llBanner = (LinearLayout)v.findViewById(R.id.ll_banner);

        llBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent singlevideopage = new Intent(mContext, SingleVideoPage.class);
//                singlevideopage.putExtra("videoID", mBannerData.get(position).get("videoId").toString());
//                mContext.startActivity(singlevideopage);
            }
        });

        tvTitle.setText(mBannerData.get(position).get("title").toString());
        Log.e("titleOfSlider",mBannerData.get(position).get("title").toString());
        tvDesc.setText(mBannerData.get(position).get("description").toString());

        Glide.with(mContext).load(mBannerData.get(position).get("image").toString()).into(img);

        container.addView(v,0);


        return v;
    }
}
