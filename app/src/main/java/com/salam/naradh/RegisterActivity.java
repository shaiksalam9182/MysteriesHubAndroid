package com.salam.naradh;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {


    Toolbar toolBar;

    TextInputLayout tilFullname,tilPhone,tilEmail,tilPassword,tilCnfPassword;
    Button btRegister;
    EditText etFullName,etPhone,etEmal,etPassword,etCnfPassword;



    SignInButton googleSignIn;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager callbackManager;

    String name,phone,email,password,cnfPassword;





    SharedPreferences sd;
    SharedPreferences.Editor editor;

    ProgressDialog pdLoading;

    CountryCodePicker ccp;

    String countryCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_register);
        callbackManager = CallbackManager.Factory.create();

        toolBar = (Toolbar)findViewById(R.id.tb_register);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sd = getSharedPreferences("Naradh", Context.MODE_PRIVATE);
        editor = sd.edit();

        pdLoading = new ProgressDialog(RegisterActivity.this);



        tilFullname = (TextInputLayout)findViewById(R.id.til_name);
        tilPhone = (TextInputLayout)findViewById(R.id.til_phone);
        tilEmail = (TextInputLayout)findViewById(R.id.til_email);
        tilPassword = (TextInputLayout)findViewById(R.id.til_password);
        tilCnfPassword = (TextInputLayout)findViewById(R.id.til_cnf_password);


        etFullName = (EditText)findViewById(R.id.et_fullname);
        etPhone = (EditText)findViewById(R.id.et_phone);
        etEmal =(EditText)findViewById(R.id.et_email);
        etPassword = (EditText)findViewById(R.id.et_password);
        etCnfPassword = (EditText)findViewById(R.id.et_cnf_password);


        btRegister = (Button)findViewById(R.id.bt_register);

        ccp = (CountryCodePicker)findViewById(R.id.ccp);

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCode = ccp.getSelectedCountryCode();
            }
        });


        countryCode = ccp.getSelectedCountryCode();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

//        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//            @Override
//            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//                Log.e("phoneVerify","verification completed"+phoneAuthCredential.getSmsCode());
//            }
//
//            @Override
//            public void onVerificationFailed(FirebaseException e) {
//                Log.e("phoneVerify",e.toString());
//            }
//
//            @Override
//            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                super.onCodeSent(s, forceResendingToken);
//                Log.e("phoneVerify","verification code send "+ s);
//            }
//        };

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        googleSignIn = (SignInButton)findViewById(R.id.btn_google_sign);

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });





        LoginButton fb_LoginButton = (LoginButton) findViewById(R.id.btn_facebook);
        assert fb_LoginButton != null;
        fb_LoginButton.setReadPermissions("public_profile email");






        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.e("CurrentAccessToken",AccessToken.getCurrentAccessToken()+"tokne");

                        if (AccessToken.getCurrentAccessToken() != null) {
//                            progressBar.setVisibility(View.VISIBLE);
                            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {

                                    Log.d("object", "" + object);
                                    try {

                                        JSONObject fb_object = new JSONObject();
                                        String fb_id = object.getString("id");

                                        URL fb_profile_image = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
                                        String profile_img = fb_profile_image.toString();

                                        Log.e("facebookres",object.optString("name")+" "+object.optString("email"));


                                        new AsyncSendFacebookData().execute(object.optString("name"),object.optString("email"));


                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }

                                }

                            });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,email,picture");
                            request.setParameters(parameters);
                            request.executeAsync();


                        }else {
                            Toast.makeText(RegisterActivity.this,"Access Token is empty",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancel() {
                        //UIUtils.showToastMsg(Register.this, "Facebook login cancel");
                        Toast.makeText(RegisterActivity.this,"Facebook login cancel",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.e("facebookError",error.toString());
                        //UIUtils.showToast(Login.this, error.getMessage());
                    }
                }

        );


        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etFullName.getText().toString();
                phone = "+"+countryCode+etPhone.getText().toString();
                email = etEmal.getText().toString();
                password = etPassword.getText().toString();
                cnfPassword = etCnfPassword.getText().toString();

                if (name.equalsIgnoreCase("")|| phone.equalsIgnoreCase("")||password.equalsIgnoreCase("")||cnfPassword.equalsIgnoreCase("")){
                    Toast.makeText(RegisterActivity.this,"Please fill required fields",Toast.LENGTH_LONG).show();
                }else if (!email.equalsIgnoreCase("")){
                    {
                        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            Toast.makeText(RegisterActivity.this, "Not a valid Email", Toast.LENGTH_LONG).show();
                        }
                    }
                }else if (!Patterns.PHONE.matcher(phone).matches()){
                    Toast.makeText(RegisterActivity.this,"Not a valid Phone number",Toast.LENGTH_LONG).show();
                }else if (!password.equalsIgnoreCase(cnfPassword)){
                    Toast.makeText(RegisterActivity.this,"Passwords are not matching",Toast.LENGTH_LONG).show();
                }else {

                    new AsyncSendManualData().execute(name,phone,email,password);
//                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                            phone,
//                            60,
//                            TimeUnit.SECONDS,
//                            RegisterActivity.this,
//                            mCallbacks
//                    );
                }
            }
        });




    }

    private void signIn() {
        pdLoading.setMessage("Loading..");
        pdLoading.setCancelable(false);
        pdLoading.show();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                getAccountDetails(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e("Google_signin", "Google sign in failed", e);

            }
        }
    }

    private void getAccountDetails(GoogleSignInAccount account) {
        Log.e("googleSignInResult",account.getDisplayName()+" "+account.getEmail()+" ");
        sendDataToServer(account.getDisplayName(),account.getEmail());
    }

    private void sendDataToServer(String displayName, String email) {
        new AsyncSendGoogleData().execute(displayName,email);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==android.R.id.home){
            finish();
        }



        return super.onOptionsItemSelected(item);
    }

    private class AsyncSendGoogleData extends AsyncTask<String,Void,JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {

            JSONObject data = new JSONObject();

            try {
                data.put("fullname",strings[0]);
                data.put("email",strings[1]);
                data.put("device_type","Android");
                data.put("login_by","google");
                data.put("verified","No");

                PostHelper postHelper = new PostHelper(RegisterActivity.this);
                Log.e("registerUrl", URLUtils.register);
                return postHelper.Post(URLUtils.register,data.toString());
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
                Log.e("google_res",jsonObject.toString());
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    Intent verify = new Intent(RegisterActivity.this,VerifyPhone.class);
                    verify.putExtra("login_by","google");
                    verify.putExtra("email",jsonObject.optString("email"));
                    startActivity(verify);
                }else {
                    if (jsonObject.optString("code").equalsIgnoreCase("300")){
                        Intent verify = new Intent(RegisterActivity.this,VerifyPhone.class);
                        verify.putExtra("login_by","google");
                        verify.putExtra("email",jsonObject.optString("email"));
                        startActivity(verify);
                        Toast.makeText(RegisterActivity.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(RegisterActivity.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class AsyncSendFacebookData extends AsyncTask<String,Void,JSONObject>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("Loading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject data = new JSONObject();

            try {
                data.put("fullname",strings[0]);
                data.put("email",strings[1]);
                data.put("device_type","Android");
                data.put("login_by","facebook");
                data.put("verified","No");

                PostHelper postHelper = new PostHelper(RegisterActivity.this);
                Log.e("registerUrl", URLUtils.register);
                return postHelper.Post(URLUtils.register,data.toString());
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
                Log.e("google_res",jsonObject.toString());
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    Intent verify = new Intent(RegisterActivity.this,VerifyPhone.class);
                    verify.putExtra("login_by","facebook");
                    verify.putExtra("email",jsonObject.optString("email"));
                    startActivity(verify);
                }else {
                    if (jsonObject.optString("code").equalsIgnoreCase("300")){
                        Intent verify = new Intent(RegisterActivity.this,VerifyPhone.class);
                        verify.putExtra("login_by","facebook");
                        verify.putExtra("email",jsonObject.optString("email"));
                        startActivity(verify);
                        Toast.makeText(RegisterActivity.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(RegisterActivity.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class AsyncSendManualData extends AsyncTask<String,Void,JSONObject>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("Loading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject data = new JSONObject();

            try {
                data.put("fullname",strings[0]);
                data.put("phone",strings[1]);
                data.put("email",strings[2]);
                data.put("password",strings[3]);
                data.put("device_type","Android");
                data.put("login_by","manual");
                data.put("verified","No");

                PostHelper postHelper = new PostHelper(RegisterActivity.this);
                Log.e("registerUrl", URLUtils.register);
                return postHelper.Post(URLUtils.register,data.toString());
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
                Log.e("google_res",jsonObject.toString());
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    Intent verify = new Intent(RegisterActivity.this,VerifyPhone.class);
                    verify.putExtra("login_by","manual");
                    verify.putExtra("phone",phone);
                    startActivity(verify);
                }else {
                    if (jsonObject.optString("code").equalsIgnoreCase("300")){
                        Intent verify = new Intent(RegisterActivity.this,VerifyPhone.class);
                        verify.putExtra("login_by","manual");
                        verify.putExtra("phone",phone);
                        startActivity(verify);
                        Toast.makeText(RegisterActivity.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(RegisterActivity.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
