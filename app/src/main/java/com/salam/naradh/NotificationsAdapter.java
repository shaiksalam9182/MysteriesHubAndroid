package com.salam.naradh;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ItemRowHolder> {
    ArrayList<HashMap> data;
    Context mContext;


    public NotificationsAdapter(ArrayList<HashMap> notificationsList, Context context) {


        data = notificationsList;
        mContext  =  context;

    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_notification,viewGroup,false);

        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRowHolder itemRowHolder, final int i) {

        itemRowHolder.tvTitle.setText(data.get(i).get("title").toString());
        itemRowHolder.tvDescription.setText(data.get(i).get("description").toString());
        itemRowHolder.cvPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent detailed = new Intent(mContext,DetailedNotification.class);
                detailed.putExtra("title",data.get(i).get("title").toString());
                detailed.putExtra("description",data.get(i).get("description").toString());
                mContext.startActivity(detailed);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder{

        TextView tvTitle,tvDescription;
        CardView cvPost;

        public ItemRowHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = (TextView)itemView.findViewById(R.id.tv_title);
            tvDescription = (TextView)itemView.findViewById(R.id.tv_description);
            cvPost = (CardView)itemView.findViewById(R.id.cv_post);

        }
    }
}
