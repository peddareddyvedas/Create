package com.goldsikka.goldsikka.OrganizationWalletModule;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.goldsikka.goldsikka.R;
import com.goldsikka.goldsikka.Utils.AccountUtils;
import com.goldsikka.goldsikka.Utils.ErrorSnackBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Organizationwallet_mainpage extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.llhome)
    LinearLayout llhome;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.llpassbook)
    LinearLayout llpassbook;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.llabout)
    LinearLayout llabout;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.llsettings)
    LinearLayout llrefer;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ivdothome)
    ImageView ivdothome;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ivdotpassbook)
    ImageView ivdotpassbook;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ivdotabout)
    ImageView ivdotabout;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ivdotsettings)
    ImageView ivdotsettings;

    String contactId = "";
    String displayName = "";
    String phoneNumber;


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ivhome)
    ImageView ivhome;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ivpassbook)
    ImageView ivpassbook;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ivabout)
    ImageView ivabout;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ivsettings)
    ImageView ivsettings;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.mainframelayout)
    FrameLayout mainframelayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.framelayoutmain)
    FrameLayout framelayoutmain;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizationwallet_mainpage);
        ButterKnife.bind(this);
        Home();
        // requestPermissions();

        Log.e("MainAccessToken", AccountUtils.getAccessToken(this));
    }

    @SuppressLint({"NewApi", "NonConstantResourceId"})
    @OnClick(R.id.llhome)
    public void Home(){

        ivdotpassbook.setVisibility(View.GONE);
        ivdothome.setVisibility(View.VISIBLE);
        ivdotabout.setVisibility(View.GONE);
        ivdotsettings.setVisibility(View.GONE);
        OrganizationHomeFragment homeFragment = new OrganizationHomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainframelayout, homeFragment).commit();
    }
    @SuppressLint("NewApi")
    @OnClick(R.id.llpassbook)
    public void Passbook(){
        ivdotpassbook.setVisibility(View.VISIBLE);
        ivdothome.setVisibility(View.GONE);
        ivdotabout.setVisibility(View.GONE);
        ivdotsettings.setVisibility(View.GONE);
        OrganizationPassbookFragment passbookFragment = new OrganizationPassbookFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainframelayout, passbookFragment).commit();
    }
    @SuppressLint("NewApi")
    @OnClick(R.id.llabout)
    public void About(){
        ivdotpassbook.setVisibility(View.GONE);
        ivdothome.setVisibility(View.GONE);
        ivdotabout.setVisibility(View.VISIBLE);
        ivdotsettings.setVisibility(View.GONE);
        OrganizationAboutUsFragment aboutusfragment = new OrganizationAboutUsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainframelayout, aboutusfragment).commit();
    }
    @SuppressLint("NewApi")
    @OnClick(R.id.llsettings)
    public void Settings(){
        ivdotpassbook.setVisibility(View.GONE);
        ivdothome.setVisibility(View.GONE);
        ivdotabout.setVisibility(View.GONE);
        ivdotsettings.setVisibility(View.VISIBLE);
        OrganizationSettingsFragment settingsfragment = new OrganizationSettingsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainframelayout, settingsfragment).commit();
    }

    private  long backPressed;
    private static final long TIME_DELAY = 2000;
    @Override
    public void onBackPressed() {

        if (backPressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            ErrorSnackBar.onBackExit(this, framelayoutmain);
        }
        backPressed = System.currentTimeMillis();
    }

}
