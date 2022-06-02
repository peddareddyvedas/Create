package com.goldsikka.goldsikka.Activitys;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chaos.view.PinView;
import com.goldsikka.goldsikka.Activitys.smsreceiver.SmsListener;
import com.goldsikka.goldsikka.Activitys.smsreceiver.SmsReceiver;
import com.goldsikka.goldsikka.Fragments.Forgotpassword_Fragment;
import com.goldsikka.goldsikka.R;
import com.goldsikka.goldsikka.Utils.NetworkUtils;
import com.goldsikka.goldsikka.Utils.ToastMessage;
import com.goldsikka.goldsikka.interfaces.ApiDao;
import com.goldsikka.goldsikka.model.Listmodel;
import com.goldsikka.goldsikka.netwokconnection.ApiClient;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotOtp extends AppCompatActivity {


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_otp)
    PinView et_otp;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tvotp)
    TextView tvotp;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btsubmit)
    Button btsubmit;
    String rs_email, stotp;
    ApiDao apiDao;
    String readOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_otp);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            rs_email = bundle.getString("rsemail");
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Forgot Password");
        //  toolbar.setTitleTextColor(getColor(R.color.colorWhite));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                Log.e("receiveSMS", "receiveOtp");
                Log.e("message", messageText);
                readOTP = extractDigits(messageText);
                Log.e("readotp", " " + readOTP);
                et_otp.setText(readOTP);//set code in edit text
                validation();
            }
        });
    }

    ///extracting the otp number from otp string
    public String extractDigits(String src) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            if (Character.isDigit(c)) {
                builder.append(c);
                Log.e("builder", " " + builder);
            }
        }
        return builder.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btsubmit)
    public void validation() {

        if (!NetworkUtils.isConnected(this)) {
            ToastMessage.onToast(this, getString(R.string.error_no_internet_connection), ToastMessage.ERROR);
            return;
        } else {
            tvotp.setVisibility(View.GONE);
            btsubmit.setVisibility(View.GONE);

            otpvalidation();
//            Timer timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    try {
//
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    ForgotOtp.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                        }
//                    });
//                }
//            }, 500);
        }
    }

    public void otpvalidation() {
        tvotp.setVisibility(View.GONE);
        stotp = et_otp.getText().toString().trim();
        if (stotp.isEmpty()) {
            btsubmit.setVisibility(View.VISIBLE);
            tvotp.setVisibility(View.VISIBLE);
            tvotp.setText("Please enter the OTP");
        } else {
            tvotp.setVisibility(View.GONE);
            btsubmit.setVisibility(View.GONE);
            otpapicall();
        }
    }

    public void otpapicall() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please Wait....");
        dialog.setCancelable(false);
        dialog.show();
        if (!NetworkUtils.isConnected(this)) {
            ToastMessage.onToast(this, getString(R.string.error_no_internet_connection), ToastMessage.ERROR);
            dialog.dismiss();
        } else {
            apiDao = ApiClient.getClient("").create(ApiDao.class);
            Call<Listmodel> getotp = apiDao.getforgot_otp(stotp, rs_email);
            getotp.enqueue(new Callback<Listmodel>() {
                @Override
                public void onResponse(Call<Listmodel> call, Response<Listmodel> response) {
                    int statuscode = response.code();
                    Listmodel listmodel = response.body();
                    if (statuscode == HttpsURLConnection.HTTP_OK) {
                        dialog.dismiss();
                        btsubmit.setVisibility(View.VISIBLE);
                        Intent intent = new Intent(ForgotOtp.this, ForgotChangePassword.class);
                        intent.putExtra("rsemail", listmodel.getEmail());
                        startActivity(intent);


                    } else {

                        dialog.dismiss();

                        btsubmit.setVisibility(View.VISIBLE);
                        assert response.errorBody() != null;
                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String st = jsonObject.getString("message");
                            ToastMessage.onToast(ForgotOtp.this, st, ToastMessage.ERROR);
                            JSONObject er = jsonObject.getJSONObject("errors");

                        } catch (JSONException | IOException jsonException) {
                            jsonException.printStackTrace();
                        }
                    }

                }

                @Override
                public void onFailure(Call<Listmodel> call, Throwable t) {
                    dialog.dismiss();
                    ToastMessage.onToast(ForgotOtp.this, "we have some issues", ToastMessage.ERROR);

                }
            });
        }
    }

}