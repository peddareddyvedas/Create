package com.goldsikka.goldsikka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.goldsikka.goldsikka.Activitys.LoginActivity;
import com.goldsikka.goldsikka.Activitys.RegistationActivity;
import com.goldsikka.goldsikka.NewDesignsActivity.MainFragmentActivity;
import com.goldsikka.goldsikka.OrganizationWalletModule.Organizationwallet_mainpage;
import com.goldsikka.goldsikka.Utils.AccountUtils;
import com.goldsikka.goldsikka.Utils.NetworkUtils;
import com.goldsikka.goldsikka.Utils.ToastMessage;
import com.goldsikka.goldsikka.Utils.shared_preference;

public class WelcomeActivity extends AppCompatActivity {

    public shared_preference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        sharedPreference = new shared_preference(this);

        if (sharedPreference.readloginstatus()){
            if (AccountUtils.getRoleid(this).equals("1")) {
                startActivity(new Intent(this, MainFragmentActivity.class));
                finish();
            }else if (AccountUtils.getRoleid(this).equals("2")){
                startActivity(new Intent(this, Organizationwallet_mainpage.class));
                finish();
            }
        }
    }

    public void login(View view){
        if (!NetworkUtils.isConnected(this)){
            ToastMessage.onToast(this, getString(R.string.error_no_internet_connection), ToastMessage.ERROR);
            return;
        }else {
            startActivity(new Intent(this, LoginActivity.class));
        }

    }

    public void signup(View view){
        if (!NetworkUtils.isConnected(this)){
            ToastMessage.onToast(this, getString(R.string.error_no_internet_connection), ToastMessage.ERROR);
            return;
        }else {
            startActivity(new Intent(this, RegistationActivity.class));
        }
    }
}