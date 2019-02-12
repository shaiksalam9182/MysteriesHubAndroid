package com.salam.naradh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostHelper {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    Context mContext;
    OkHttpClient client = new OkHttpClient();
    public PostHelper(Context context) {
        this.mContext = context;
    }

    @SuppressLint("NewApi")
    public JSONObject Post(String url, String json) throws IOException, JSONException {

            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder().url(url).post(body).build();
            try (Response response = client.newCall(request).execute()) {
                Log.e("response_from_server",response.toString());

                return new JSONObject(response.body().string());
            }


    }

}
