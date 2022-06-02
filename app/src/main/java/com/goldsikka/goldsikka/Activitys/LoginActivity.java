package com.goldsikka.goldsikka.Activitys;

import android.Manifest;
import android.annotation.SuppressLint;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;


import android.view.MenuItem;
import android.view.View;


import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.goldsikka.goldsikka.Fragments.Forgotpassword_Fragment;
import com.goldsikka.goldsikka.Fragments.baseinterface;
import com.goldsikka.goldsikka.NewDesignsActivity.MainFragmentActivity;
import com.goldsikka.goldsikka.OrganizationWalletModule.Organizationwallet_mainpage;
import com.goldsikka.goldsikka.R;
import com.goldsikka.goldsikka.Utils.AccountUtils;
import com.goldsikka.goldsikka.Utils.NetworkUtils;
import com.goldsikka.goldsikka.Utils.ToastMessage;
import com.goldsikka.goldsikka.Utils.shared_preference;
import com.goldsikka.goldsikka.WelcomeActivity;
import com.goldsikka.goldsikka.interfaces.ApiDao;
import com.goldsikka.goldsikka.model.Listmodel;
import com.goldsikka.goldsikka.netwokconnection.ApiClient;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/// **** eiY0vP8G7qA:APA91bE1_fL0w9DuYludrbmmwQs540CrvbalH7TVMEojP4FuqdEERwbemrmxpLNSNND-Zjn9-H1_v03zsLhw5f-_Ac7k4d5qYnSPyEtNDEAjR5ddw-23lB79r50bF26pdc_8azkgFxNh /
public class LoginActivity extends BaseActivity implements baseinterface, GoogleApiClient.OnConnectionFailedListener {

    String stmobile, stpassword;

    ApiDao apiDao;
    String rs_name, roleId, rs_accesstoken, rs_roleid, rs_maksedphoone;
    // GifImageView loading_gif;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_emailmobile)
    EditText etmobile;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_password)
    EditText etpassword;

    @SuppressLint("NonConstantResourceId")
    @BindView(value = R.id.tvemailerror)
    TextView tv_emailmobile;

    @SuppressLint("NonConstantResourceId")
    @BindView(value = R.id.tvpasserror)
    TextView tv_password1;

    @SuppressLint("NonConstantResourceId")
    @BindView(value = R.id.btn_login)
    Button btn_login;

    @SuppressLint("NonConstantResourceId")
    @BindView(value = R.id.ivconfirmeye)
    ImageView ivconfirmeye;
    View view;
    String appVersion, androidVersion, uuid, device_token;
    @SuppressLint("NonConstantResourceId")
    @BindView(value = R.id.google)
    Button google;
    Button fb;
    private int RC_SIGN_IN = 1;
    String socialmedialogin = "jhjbj";
    String socailmedianame = "jhb";
    String roleid, accesstoken;
    private shared_preference sharedPreference;

    private CallbackManager callbackManager;
    private LoginManager loginManager;
    GoogleSignInOptions gso;
    private GoogleApiClient googleApiClient;
    ImageView iv_back;
    TextView signup;
    private static final int REQUEST = 112;

    protected int getLayoutId() {
//             generateHashKey();

        FacebookSdk.sdkInitialize(LoginActivity.this);
        callbackManager = CallbackManager.Factory.create();
        facebookLogin();


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        sharedPreference = new shared_preference(this);

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        return R.layout.activity_login;

    }

    @SuppressLint({"NewApi"})
    @Override
    protected void initView() {
        ButterKnife.bind(this);
        etmobile.setHint(Html.fromHtml(getString(R.string.username_password)));
        etpassword.setHint(Html.fromHtml(getString(R.string.password)));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Login");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // loading_gif = findViewById(R.id.loading_gif);

        etmobile.addTextChangedListener(textWatcher);
        etpassword.addTextChangedListener(textWatcher);

        apiDao = ApiClient.getClient(rs_accesstoken).create(ApiDao.class);
        iv_back = (ImageView) findViewById(R.id.loginback);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        if (!NetworkUtils.isConnected(this)) {
            ToastMessage.onToast(this, getString(R.string.error_no_internet_connection), ToastMessage.ERROR);
            return;
        } else {
            submitbuttonvisible(view);
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {

                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {

                            if (task.isSuccessful()) {
                                device_token = task.getResult().getToken(); // device token
                                Log.e("Device Tocken", "device token: Token: " + device_token);
                            } else {
                                Toast.makeText(LoginActivity.this, "Token generation Failed", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
            uuid = UUID.randomUUID().toString();
            Log.e("uuid", uuid);//device UUID

            appVersion = String.valueOf(Build.VERSION.SDK_INT);
            Log.e("sdkVersion", appVersion);//app version
            // String BrandName = MAN;      // Manufacturer will come I think, Correct me if I am wrong :)  Brand name like Samsung or Mircomax

            androidVersion = Build.VERSION.RELEASE;
            Log.e("osVersion", androidVersion);//android version

            ivconfirmeye.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (etpassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                        etpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        ((ImageView) (v)).setImageResource(R.drawable.eye);
                        //Show Password
                        etpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        ((ImageView) (v)).setImageResource(R.drawable.eye_off);
                        //Hide Password
                        etpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                }
            });
        }

        fb = (Button) findViewById(R.id.fb);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline(getApplicationContext())) {
                    //mProgress.show();
                    loginManager.logInWithReadPermissions(
                            LoginActivity.this,
                            Arrays.asList(
                                    "email",
                                    "public_profile",
                                    "user_birthday"));
                }
            }
        });
        signup = (TextView) findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NetworkUtils.isConnected(getApplicationContext())) {
                    ToastMessage.onToast(getApplicationContext(), getString(R.string.error_no_internet_connection), ToastMessage.ERROR);
                    return;
                } else {
                    startActivity(new Intent(getApplicationContext(), RegistationActivity.class));
                }
            }
        });
    }

    @OnClick(R.id.google)
    public void google() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e("requestcode", "" + requestCode + " " + resultCode);

    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.e("", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
//            assert acct != null;
            String personName = acct.getDisplayName();
       //     String personPhotoUrl = Objects.requireNonNull(acct.getPhotoUrl()).toString();
            String email = acct.getEmail();
            socialmedialogin = acct.getEmail();
            socailmedianame = socialmedialogin;
            Log.e("googleData", "Name: " + personName + ", email: " + email + ", Image: " );
            //  Toast.makeText(getApplicationContext(), " email:" + email, Toast.LENGTH_SHORT).show();
            googlevalidation();


        } else {
            // Signed out, show unauthenticated UI.
        }
    }


    /*  private void signOut() {
          Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                  new ResultCallback<Status>() {
                      @Override
                      public void onResult(Status status) {
                          // [START_EXCLUDE]
                          updateUI(false);
                          // [END_EXCLUDE]
                      }
                  });
      }*/
    public void logougoogle() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            //  gotoMainActivity();
                            //  startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            //  ToastMessage.onToast(getApplicationContext(),"Session close",ToastMessage.ERROR);
                        } else {
                            Toast.makeText(getApplicationContext(), "Session not close", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void facebookLogin() {
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();
        loginManager.registerCallback(
                callbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(

                                loginResult.getAccessToken(),

                                new GraphRequest.GraphJSONObjectCallback() {

                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {

                                        if (object != null) {
                                            try {
                                                String name = object.getString("name");
                                                String email1 = object.getString("email");
                                                String fbUserID = object.getString("id");
                                                socialmedialogin = email1;
                                                socailmedianame = socialmedialogin;
                                                Log.e("FBStatus", "" + socialmedialogin);
                                                Log.e("FBStatus", "" + socailmedianame);

                                                Log.e("FBStatusemail", "" + email1);
                                                Log.e("FBStatusemail", "" + name);
                                                Log.e("FBStatusemail", "" + fbUserID);
//                                                Toast.makeText(getApplicationContext(), " email:" + email1, Toast.LENGTH_SHORT).show();


                                                // do action after Facebook login success
                                                // or call your API
                                                //  googlevalidation();

                                                Call<Listmodel> call = apiDao.get_google_login(email1, "", "");
                                                responsemethod(call);

                                                disconnectFromFacebook();


                                            } catch (JSONException | NullPointerException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString(
                                "fields",
                                "id, name, email, gender, birthday");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Log.v("LoginScreen", "---onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        // here write code when get error
                        Log.v("LoginScreen", "----onError: "
                                + error.getMessage());
                    }
                });
    }


    public void disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/permissions/",
                null,
                HttpMethod.DELETE,
                new GraphRequest
                        .Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        LoginManager.getInstance().logOut();
                    }
                })
                .executeAsync();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
        super.onBackPressed();
        // finish();
    }

    public void submitbuttonvisible(View view) {
        stmobile = etmobile.getText().toString().trim();
        stpassword = etpassword.getText().toString().trim();

        if (stmobile.equals("") || stpassword.equals("")) {

            btn_login.setEnabled(false);
            btn_login.setBackgroundResource(R.drawable.backgroundvisablity);
        } else {
            btn_login.setEnabled(true);
            btn_login.setBackgroundResource(R.drawable.buttonbackground);
        }
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            submitbuttonvisible(view);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @OnClick(R.id.btn_login)
    public void init_validation(View v) {
        requestPermissions();
    }

    public void movetoOtp(View v) {
        tv_emailmobile.setVisibility(View.GONE);
        tv_password1.setVisibility(View.GONE);
        btn_login.setVisibility(View.GONE);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        validation(v);
                        btn_login.setVisibility(View.VISIBLE);
                    }
                });
            }
        }, 0);
    }


    //////Runtime permissions to read the OTP SMS

    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_SMS) &&
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.RECEIVE_SMS)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Info")
                    .setMessage("SMS permissions are required to auto read sms")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(LoginActivity.this,
                                    new String[]{Manifest.permission.READ_SMS,
                                            Manifest.permission.RECEIVE_SMS}, 101);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS,
                            Manifest.permission.RECEIVE_SMS}, 101);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            movetoOtp(view);
        } else {
            movetoOtp(view);

        }
    }

    public void validation(View view) {

        stmobile = etmobile.getText().toString().trim();
        stpassword = etpassword.getText().toString().trim();

        tv_emailmobile.setVisibility(View.GONE);
        tv_password1.setVisibility(View.GONE);


        if (stmobile.isEmpty()) {
            tv_emailmobile.setVisibility(View.VISIBLE);
            tv_emailmobile.setText("Please Enter Mobile/Email");
        } else if (stpassword.isEmpty()) {
            tv_password1.setText("Please Enter Password");
            tv_password1.setVisibility(View.VISIBLE);
        } else {
            Call<Listmodel> call = apiDao.getlogin(stmobile, stpassword, "Android", appVersion, androidVersion, uuid, device_token, "GS");
            responsemethod(call);
        }

    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.tv_forgotpass)
    public void forgotpassword() {
        //disconnectFromFacebook();

        if (!NetworkUtils.isConnected(this)) {
            ToastMessage.onToast(this, getString(R.string.error_no_internet_connection), ToastMessage.ERROR);
            return;
        } else {
            Intent intent = new Intent(LoginActivity.this, Forgotpassword_Fragment.class);
            startActivity(intent);
        }
    }

    public void googlevalidation() {

        Call<Listmodel> call = apiDao.get_google_login(socialmedialogin, "", "");
        responsemethod(call);

    }

    /// Total response from server
    @Override
    public void responce(@NonNull Response<Listmodel> response, int stauscode) {

        if (stauscode == 500) {
            ToastMessage.onToast(this, "User Not Found", ToastMessage.ERROR);
            logougoogle();


        } else if (stauscode == HttpsURLConnection.HTTP_OK || stauscode == HttpsURLConnection.HTTP_CREATED) {
            List<Listmodel> list = Collections.singletonList(response.body());

            for (Listmodel listmodel : list) {
                rs_roleid = listmodel.getRole_id();
                rs_accesstoken = listmodel.getAccessToken();
                Log.e("AccessToken", rs_accesstoken);
                rs_name = listmodel.getName();
                rs_maksedphoone = listmodel.getMaskedPhone();
                roleId = listmodel.getRoleId();
                // boolean msg = listmodel.getMessageStatus();
                if (socailmedianame.equals(socialmedialogin)) {
                    Log.e("sate1", "CallHome");
                    googleonsucess();
                } else {
                    Log.e("sate1", "calllogin");
                    onsucess();
                }
            }
        } else if (stauscode == 400) {
            ToastMessage.onToast(getApplicationContext(), "Invalid Credentials", ToastMessage.ERROR);
        }
    }


    @Override
    public void listresponce(@NonNull Response<List<Listmodel>> response, int stauscode) {

    }

    public void onsucess() {
        AccountUtils.setPassword(this, stpassword);
        Intent intent = new Intent(this, OTPActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("credential", "login");
        intent.putExtra("accesstoken", rs_accesstoken);
        Log.e("AccessToken", rs_accesstoken);
        intent.putExtra("roleId", roleId);
        startActivity(intent);
        finish();

    }

    public void googleonsucess() {
        sharedPreference.WriteLoginStatus(true);
        AccountUtils.setAccessToken(this, rs_accesstoken);
        AccountUtils.setRoleid(this, roleId);
        if (roleId.equals("1")) {
            Intent intent = new Intent(this, MainFragmentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            logougoogle();
            finish();
        } else if (roleId.equals("2")) {
            Intent intent = new Intent(this, Organizationwallet_mainpage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            logougoogle();
            finish();
        }


    }
    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);


    }*/

    private void generateHashKey() {

        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.goldsikka.goldsikka",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {
            ///PwkKnqideeNH9+UTdOCurR59wIE=
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}