package com.salam.naradh;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {


    TextView tvRegister;

    EditText etPhone,etPassword;

    Button btLogin;

    String phone,password,fcmToken;

    SignInButton googleSignIn;

    ProgressDialog pdLoading;

    private GoogleSignInClient mGoogleSignInClient;

    private CallbackManager callbackManager;

    CountryCodePicker ccp;

    String countryCode = "";

    SharedPreferences sd;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();


        etPhone = (EditText)findViewById(R.id.et_phone);
        etPassword = (EditText)findViewById(R.id.et_password);

        btLogin = (Button)findViewById(R.id.bt_login);

        googleSignIn = (SignInButton)findViewById(R.id.btn_google_sign);

        pdLoading = new ProgressDialog(LoginActivity.this);

        sd = getSharedPreferences("Naradh", Context.MODE_PRIVATE);
        editor = sd.edit();

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

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        getFcmToken();

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = "+"+countryCode+etPhone.getText().toString();
                password = etPassword.getText().toString();


                if (phone.equalsIgnoreCase("")||password.equalsIgnoreCase("")){
                    Toast.makeText(LoginActivity.this,"Please fill all required fields",Toast.LENGTH_LONG).show();
                }else if (!Patterns.PHONE.matcher(phone).matches()){
                    Toast.makeText(LoginActivity.this,"Not a valid phone number",Toast.LENGTH_LONG).show();
                }else {
                    new AsyncManualSignIn().execute();
                }
            }
        });



        tvRegister = (TextView)findViewById(R.id.tv_register);

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });



        LoginButton fb_LoginButton = (LoginButton) findViewById(R.id.btn_facebook);
        assert fb_LoginButton != null;
        fb_LoginButton.setReadPermissions("public_profile email");




        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.e("CurrentAccessToken", AccessToken.getCurrentAccessToken()+"tokne");

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
                            Toast.makeText(LoginActivity.this,"Access Token is empty",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancel() {
                        //UIUtils.showToastMsg(Register.this, "Facebook login cancel");
                        Toast.makeText(LoginActivity.this,"Facebook login cancel",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.e("facebookError",error.toString());
                        //UIUtils.showToast(Login.this, error.getMessage());
                    }
                }

        );
    }

    private void signIn() {
        pdLoading.setMessage("Loading..");
        pdLoading.setCancelable(false);
        pdLoading.show();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 100);
    }

    private void getFcmToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {

                if (task.isSuccessful()){
                    fcmToken = task.getResult().getToken();
                    Log.e("fcmToken",fcmToken);
                }else {
                    Log.e("fcmTokenError", String.valueOf(task.getException()));
                }
            }
        });
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
        new AsyncGoogleSignIn().execute(email);
    }

    private class AsyncGoogleSignIn extends AsyncTask<String,Void,JSONObject> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject data= new JSONObject();
            try {
                data.put("email",strings[0]);
                data.put("login_by","google");
                data.put("device_type","Android");
                data.put("fcm_token",fcmToken);

                PostHelper postHelper = new PostHelper(LoginActivity.this);
                return  postHelper.Post(URLUtils.login,data.toString());
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
                Log.e("google_signin_res",jsonObject.toString());
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    Toast.makeText(LoginActivity.this,"Successfully loggedIn",Toast.LENGTH_LONG).show();

                    storeDetails(jsonObject);




                }else if (jsonObject.optString("status").equalsIgnoreCase("Failed")){
                    Toast.makeText(LoginActivity.this,"No records found",Toast.LENGTH_LONG).show();
                }
                else if (jsonObject.optString("code").equalsIgnoreCase("300")){
                    Toast.makeText(LoginActivity.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(LoginActivity.this,"Error occured",Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(LoginActivity.this,"No response from server",Toast.LENGTH_LONG).show();
            }

        }
    }

    private void storeDetails(JSONObject jsonObject) {
        editor.putString("phone",jsonObject.optString("phone"));
        editor.putString("token",jsonObject.optString("token"));
        editor.putString("login_by",jsonObject.optString("login_by"));
        editor.commit();
        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
        finish();
    }

    private class AsyncSendFacebookData extends AsyncTask<String, Void, JSONObject> {

        ProgressDialog pdLoading = new ProgressDialog(LoginActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("Loading..");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject data= new JSONObject();
            try {
                data.put("email",strings[1]);
                data.put("login_by","facebook");
                data.put("device_type","Android");
                data.put("fcm_token",fcmToken);

                PostHelper postHelper = new PostHelper(LoginActivity.this);
                Log.e("sendingdata",data.toString());
                return  postHelper.Post(URLUtils.login,data.toString());
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
//            Log.e("facebook_signIn",s.toString());
            pdLoading.dismiss();
            if (jsonObject!=null){
                Log.e("facebook_sign_in_res",jsonObject.toString());
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    Toast.makeText(LoginActivity.this,"Successfully loggedIn",Toast.LENGTH_LONG).show();
                    storeDetails(jsonObject);
                }else if (jsonObject.optString("code").equalsIgnoreCase("300")){
                    Toast.makeText(LoginActivity.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(LoginActivity.this,"Error occured",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class AsyncManualSignIn extends AsyncTask<Void,Void,JSONObject>{
        ProgressDialog pdLoading = new ProgressDialog(LoginActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("Loading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject data= new JSONObject();
            try {
                data.put("phone",phone);
                data.put("password",password);
                data.put("login_by","manual");
                data.put("device_type","Android");
                data.put("fcm_token",fcmToken);

                PostHelper postHelper = new PostHelper(LoginActivity.this);
                Log.e("sendingData",data.toString());
                return  postHelper.Post(URLUtils.login,data.toString());
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
                Log.e("manual_res",jsonObject.toString());
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    Toast.makeText(LoginActivity.this,"Successfully loggedIn",Toast.LENGTH_LONG).show();
                    storeDetails(jsonObject);
                }else if (jsonObject.optString("code").equalsIgnoreCase("300")){
                    Toast.makeText(LoginActivity.this,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(LoginActivity.this,"Error occured",Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
