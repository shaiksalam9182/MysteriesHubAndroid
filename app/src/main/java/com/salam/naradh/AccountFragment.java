package com.salam.naradh;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    TextView tvName,tvPhone,tvNoOfPosts;
    SharedPreferences sd;
    SharedPreferences.Editor editor;
    String phone,token,android_id;


    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account,container,false);


        tvName = (TextView)view.findViewById(R.id.tv_name);
        tvPhone = (TextView)view.findViewById(R.id.tv_mobile);
        tvNoOfPosts = (TextView)view.findViewById(R.id.tv_posts_count);

        sd = getContext().getSharedPreferences("Naradh", Context.MODE_PRIVATE);
        editor =sd.edit();


        phone = sd.getString("phone","");
        token = sd.getString("token","");
        android_id  =  sd.getString("android_id","");

        new AsyncGetDetails().execute();


        return view;
    }

    private class AsyncGetDetails extends AsyncTask<Void,Void, JSONObject> {

        ProgressDialog pdLoading = new ProgressDialog(getContext());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("Loading..");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {

            JSONObject data = new JSONObject();

            try {
                data.put("phone",phone);
                data.put("token",token);
                data.put("android_id",android_id);
                PostHelper postHelper = new PostHelper(getContext());
                return  postHelper.Post(URLUtils.userProfile,data.toString());
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
            pdLoading.dismiss();
            if (jsonObject!=null){
//                Log.e("profrileRes",jsonObject.toString());
                if (jsonObject.optString("status").equalsIgnoreCase("success")){

                    tvName.setText("Full Name: "+jsonObject.optString("fullname"));
                    tvPhone.setText("Phone: "+jsonObject.optString("phone"));
                    tvNoOfPosts.setText("Your posts: "+jsonObject.optString("post_counts"));

                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    Toast.makeText(getContext(),jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
