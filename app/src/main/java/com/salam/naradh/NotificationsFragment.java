package com.salam.naradh;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment {

    ProgressBar pbNot;
    RecyclerView rvNot;
    SharedPreferences sd;
    SharedPreferences.Editor editor;
    String phone,token,android_id;
    ArrayList<HashMap> notificationsList;
    HashMap<String,String> map;


    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_notifications,container,false);



        pbNot = (ProgressBar)v.findViewById(R.id.pb_not);
        rvNot = (RecyclerView)v.findViewById(R.id.rv_notifications);

        sd = getContext().getSharedPreferences("Naradh", Context.MODE_PRIVATE);
        editor = sd.edit();


        phone = sd.getString("phone","");
        token = sd.getString("token","");
        android_id = sd.getString("android_id","");

        notificationsList = new ArrayList<>();


        new AsyncGetNotifications().execute();





        return v;
    }

    private class AsyncGetNotifications extends AsyncTask<Void,Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbNot.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject data= new JSONObject();
            try {
                data.put("phone",phone);
                data.put("token",token);
                data.put("android_id",android_id);
                PostHelper postHelper = new PostHelper(getContext());
                return  postHelper.Post(URLUtils.readNotifications,data.toString());
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
            pbNot.setVisibility(View.GONE);
            if (jsonObject!=null){
//                Log.e("notificationRes",jsonObject.toString());
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    parseJsonData(jsonObject);

                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    Toast.makeText(getContext(),jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getContext(),"Error occured",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void parseJsonData(JSONObject jsonObject) {
        JSONArray data = jsonObject.optJSONArray("data");
        JSONObject temp;
        for (int i = 0;i<data.length();i++){
            map =  new HashMap<String, String>();
            temp = data.optJSONObject(i);
            map.put("notification_id",temp.optString("notification_id"));
            map.put("title",temp.optString("title"));
            map.put("description",temp.optString("description"));
            map.put("type",temp.optString("type"));
            map.put("type_id",temp.optString("type_id"));
            notificationsList.add(map);
        }

        rvNot.setHasFixedSize(true);
        rvNot.setNestedScrollingEnabled(false);
        rvNot.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvNot.setItemAnimator(new DefaultItemAnimator());
        rvNot.setAdapter(new NotificationsAdapter(notificationsList,getContext()));
    }


}
