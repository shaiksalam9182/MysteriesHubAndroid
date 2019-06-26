package com.salam.naradh;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment {


    RadioButton rbBug,rbFeature,rbSuggestion;
    EditText etFeedback;
    Button btSubmit;
    String type = "",feedBack = "",email,token,user_id;


    SharedPreferences sd;
    SharedPreferences.Editor editor;


    public FeedbackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feedback,container,false);

        rbBug = (RadioButton)view.findViewById(R.id.rb_bug);
        rbFeature = (RadioButton)view.findViewById(R.id.rb_feature);
        rbSuggestion = (RadioButton)view.findViewById(R.id.rb_suggestion);

        etFeedback = (EditText)view.findViewById(R.id.et_feedback);


        btSubmit = (Button)view.findViewById(R.id.bt_submit);

        sd = getContext().getSharedPreferences("Naradh", Context.MODE_PRIVATE);
        editor = sd.edit();


        email = sd.getString("email","");
        token = sd.getString("token","");
        user_id = sd.getString("user_id","");

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedBack = etFeedback.getText().toString();
                if (feedBack.equalsIgnoreCase("")||type.equalsIgnoreCase("")){
                    Toast.makeText(getContext(),"Please fill all fields",Toast.LENGTH_LONG).show();
                }else {
                    new AsyncSendFeedBack().execute();
                }
            }
        });



        rbBug.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    rbFeature.setChecked(false);
                    rbSuggestion.setChecked(false);
                    type = "bug";
                }
            }
        });


        rbFeature.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    rbBug.setChecked(false);
                    rbSuggestion.setChecked(false);
                    type = "feature";
                }
            }
        });

        rbSuggestion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    rbBug.setChecked(false);
                    rbFeature.setChecked(false);
                    type = "suggestion";
                }
            }
        });

        return view;
    }

    private class AsyncSendFeedBack extends AsyncTask<Void,Void, JSONObject> {

        ProgressDialog pdLoading = new ProgressDialog(getContext());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("Sending...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject data = new JSONObject();
            try {
                data.put("email",email);
                data.put("token",token);
                data.put("user_id",user_id);
                data.put("type",type);
                data.put("message",feedBack);

                PostHelper postHelper = new PostHelper(getContext());
                return postHelper.Post(URLUtils.sendFeedback,data.toString());
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
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
//                    Toast.makeText(getContext(),"Successfully sent",Toast.LENGTH_LONG).show();
                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext(), android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    builder.setTitle("Thanks");
                    builder.setMessage("Thanks for submitting feedback. We will get back to you soon");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    builder.show();

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
                }
            }else {
                Toast.makeText(getContext(),"Error occured",Toast.LENGTH_LONG).show();
            }
        }
    }
}
