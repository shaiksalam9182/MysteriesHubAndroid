package com.salam.naradh;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class VerifyPhone extends AppCompatActivity {


    EditText etPhone,etOtp;
    Button btVerify,btSend;

    String phone,OTP,login_by,recordId,email,getPhone;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String credential;
    FirebaseAuth mAuth;
    ProgressDialog pdLoading;
    CountryCodePicker ccp;
    String countryCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        pdLoading = new ProgressDialog(VerifyPhone.this);


        etPhone = (EditText)findViewById(R.id.et_phone);
        etOtp = (EditText)findViewById(R.id.et_otp);

        btVerify = (Button)findViewById(R.id.bt_verify);
        btSend = (Button)findViewById(R.id.bt_send);


        login_by = getIntent().getStringExtra("login_by");

        mAuth = FirebaseAuth.getInstance();

        ccp = (CountryCodePicker)findViewById(R.id.ccp);

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCode = ccp.getSelectedCountryCode();
            }
        });


        countryCode = ccp.getSelectedCountryCode();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
                etOtp.setText(phoneAuthCredential.getSmsCode());
//                Toast.makeText(VerifyPhone.this,"Successfully Verified",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                pdLoading.dismiss();
                Toast.makeText(VerifyPhone.this,"Unable to send OTP",Toast.LENGTH_LONG).show();
                Log.e("OTP error",e.toString());
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                pdLoading.dismiss();
                credential = s;
                btVerify.setVisibility(View.VISIBLE);
                btSend.setVisibility(View.GONE);
                etOtp.setVisibility(View.VISIBLE);
            }
        };


        if (login_by.equalsIgnoreCase("google")||login_by.equalsIgnoreCase("facebook")){
            email = getIntent().getStringExtra("email");
            etOtp.setVisibility(View.GONE);
            btVerify.setVisibility(View.GONE);
            btSend.setText("SendOTP");

        }else if (login_by.equalsIgnoreCase("manual")){
            phone = getIntent().getStringExtra("phone");
            etPhone.setText(phone);
            etPhone.setEnabled(false);


            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phone,
                            60,
                            TimeUnit.SECONDS,
                            VerifyPhone.this,
                            mCallbacks
                    );

        }


        btVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdLoading.setMessage("Loading..");
                pdLoading.setCancelable(false);
                pdLoading.show();
                OTP = etOtp.getText().toString();
                verifyPhoneNumberManually(OTP,credential);
            }
        });

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdLoading.setMessage("Loading...");
                pdLoading.setCancelable(false);
                pdLoading.show();
                phone = "+"+countryCode+etPhone.getText().toString();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+"+countryCode+etPhone.getText().toString(),
                        60,
                        TimeUnit.SECONDS,
                        VerifyPhone.this,
                        mCallbacks
                );
            }
        });

    }

    private void verifyPhoneNumberManually(String otp, String credential) {
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(credential,otp);

        signInWithPhoneAuthCredential(phoneAuthCredential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(VerifyPhone.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(VerifyPhone.this,"Successfully Verified",Toast.LENGTH_LONG).show();
                            if (login_by.equalsIgnoreCase("manual")){
                                new AsyncVerifyPhone().execute();
                            }else {
                                new AsyncVerifySocial().execute();
                            }
//                            startActivity(new Intent(VerifyPhone.this,LoginActivity.class));
//                            finish();
                        }else {
                            pdLoading.dismiss();
                            Log.e("errorverify",task.getException().toString());
                            Toast.makeText(VerifyPhone.this,"Error in verifying your phone",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private class AsyncVerifyPhone extends AsyncTask<Void,Void, JSONObject> {

//        ProgressDialog pdLoading = new ProgressDialog(VerifyPhone.this);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pdLoading.setMessage("verifying...");
//            pdLoading.setCancelable(false);
//            pdLoading.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {

            JSONObject data= new JSONObject();
            try {
                data.put("login_by",login_by);
                data.put("phone",phone);
                data.put("verified","Yes");

                PostHelper postHelper = new PostHelper(VerifyPhone.this);
                return  postHelper.Post(URLUtils.verify,data.toString());

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
                if (jsonObject.optString("code").equalsIgnoreCase("302")){
                    Toast.makeText(VerifyPhone.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                    startActivity(new Intent(VerifyPhone.this,LoginActivity.class));
                    finish();
                }else {
                    Toast.makeText(VerifyPhone.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                }
            }
            Log.e("verification_res",jsonObject.toString());
        }
    }

    private class AsyncVerifySocial extends AsyncTask<Void,Void,JSONObject>{

//        ProgressDialog pdLoading = new ProgressDialog(VerifyPhone.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pdLoading.setMessage("verifying...");
//            pdLoading.setCancelable(false);
//            pdLoading.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject data= new JSONObject();
            try {
                data.put("login_by",login_by);
                data.put("email",email);
                data.put("phone",phone);
                data.put("verified","Yes");
                Log.e("socialUpdate",data.toString());

                PostHelper postHelper = new PostHelper(VerifyPhone.this);
                return  postHelper.Post(URLUtils.verify,data.toString());

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
                if (jsonObject.optString("code").equalsIgnoreCase("302")){
                    Toast.makeText(VerifyPhone.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                    startActivity(new Intent(VerifyPhone.this,LoginActivity.class));
                    finish();
                }else {
                    Toast.makeText(VerifyPhone.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                }
            }
            Log.e("verification_res",jsonObject.toString());
        }
    }
}
