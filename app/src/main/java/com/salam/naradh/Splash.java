package com.salam.naradh;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Splash extends AppCompatActivity {

    SharedPreferences sd;
    SharedPreferences.Editor editor;
    String phone,android_id,fcm_token = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        MobileAds.initialize(Splash.this,"ca-app-pub-1679206260526965~9036544668");


        sd = getSharedPreferences("Naradh", Context.MODE_PRIVATE);
        editor = sd.edit();

        android_id =  Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);


        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {

                if (task.isSuccessful()){
                    fcm_token = task.getResult().getToken();
//                    Log.e("fcmToken",fcm_token);
                    new AsyncUpdateCheck().execute();

                }else {
                    Log.e("fcmTokenError", String.valueOf(task.getException()));
                }
            }
        });




        phone = sd.getString("phone","");








//        Thread logoTimer=new Thread(){
//            public void run(){
//                try{
//                    sleep(2000);
//                }
//                catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                finally{
//
//                }
//            }
//
//        };
//
//        logoTimer.start();




    }

    private class AsyncDemoUser extends AsyncTask<Void,Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject data = new JSONObject();
            try {
                data.put("android_id",android_id);
                data.put("device_type","android");
                data.put("fcm_token",fcm_token);

                PostHelper postHelper = new PostHelper(Splash.this);
                return postHelper.Post(URLUtils.demoUser,data.toString());

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
                Log.e("demoRes",jsonObject.toString());
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    editor.putString("android_id",jsonObject.optString("android_id"));
                    editor.putString("token",jsonObject.optString("token"));
                    editor.commit();
                    redirect();
                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    Toast.makeText(Splash.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                    redirect();
                }else {
                    Toast.makeText(Splash.this,"Error Occurred",Toast.LENGTH_LONG).show();
                    redirect();
                }
            }else {
                Toast.makeText(Splash.this,"Server is not responding",Toast.LENGTH_LONG).show();
                redirect();
            }
        }
    }

    private void redirect() {
        String android_id = sd.getString("android_id","");
        if (android_id.equalsIgnoreCase("")){
            startActivity(new Intent(Splash.this,LoginActivity.class));
            finish();
        }else {
            startActivity(new Intent(Splash.this,HomeActivity.class));
            finish();
        }
    }

    private class AsyncUpdateCheck extends AsyncTask<Void,Void,JSONObject>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            PostHelper postHelper  =  new PostHelper(Splash.this);
            try {
                return  postHelper.Post(URLUtils.checkUpdate,"");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
//            Log.e("updateRes",jsonObject.toString());
            if (jsonObject!=null){
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    String version = jsonObject.optJSONArray("data").optJSONObject(0).optString("update_version");
                    String updateOne = jsonObject.optJSONArray("data").optJSONObject(0).optString("update_note_one");
                    String updateTwo = jsonObject.optJSONArray("data").optJSONObject(0).optString("update_note_two");
                    String updateThree = jsonObject.optJSONArray("data").optJSONObject(0).optString("update_note_three");
                    PackageManager manager = Splash.this.getPackageManager();
                    String appVersion = "";
                    try {
                        PackageInfo info = manager.getPackageInfo(Splash.this.getPackageName(),0);
                        appVersion = info.versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (Float.parseFloat(version)>Float.parseFloat(appVersion)){
                        raiseDialog(updateOne,updateTwo,updateThree);
                    }else {
                        new AsyncDemoUser().execute();
                    }
                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    Toast.makeText(Splash.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(Splash.this,"Error occurred",Toast.LENGTH_LONG).show();
                }

            }else {
                Toast.makeText(Splash.this,"Error occurred",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void raiseDialog(String updateOne, String updateTwo, String updateThree) {
        final Dialog dialog = new Dialog(Splash.this);
        dialog.setContentView(R.layout.custom_update_dialog);
        dialog.setCancelable(false);
        try {
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, (int) (Resources.getSystem().getDisplayMetrics().widthPixels / 1.3));
        }catch (NullPointerException e){
            e.printStackTrace();
        }


        TextView tvOne = (TextView)dialog.findViewById(R.id.tv_update_one);
        TextView tvTwo = (TextView)dialog.findViewById(R.id.tv_update_two);
        TextView tvThree = (TextView)dialog.findViewById(R.id.tv_update_three);
        TextView tvMain = (TextView)dialog.findViewById(R.id.tv_main_update);

        tvOne.setText(updateOne);
        tvTwo.setText(updateTwo);
        tvThree.setText(updateThree);


        Button btUpdate = (Button)dialog.findViewById(R.id.bt_update);
        Button btCancel = (Button)dialog.findViewById(R.id.bt_cancel);

        tvMain.setText("New version of Naaradh update available in play store");



        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new AsyncDemoUser().execute();
                dialog.dismiss();
                String url = "https://play.google.com/store/apps/details?id=com.salam.naaradh";
                Intent viewintent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                startActivity(viewintent);
                finish();

            }
        });


        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new AsyncDemoUser().execute();
            }
        });

        if (!isFinishing()){
            dialog.show();
        }
    }
}
