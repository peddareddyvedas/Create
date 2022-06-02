package com.goldsikka.goldsikka.Activitys.MoneyWallet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldsikka.goldsikka.Activitys.BaseActivity;
import com.goldsikka.goldsikka.Activitys.Events.Fragments.EventsListAdapter;
import com.goldsikka.goldsikka.Activitys.MoneyWallet.Adapter.WalletAddAdapter;
import com.goldsikka.goldsikka.Fragments.baseinterface;
import com.goldsikka.goldsikka.R;
import com.goldsikka.goldsikka.Utils.AccountUtils;
import com.goldsikka.goldsikka.model.Listmodel;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;

public class AddMonet_to_Wallet extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lladdmoney)
    LinearLayout llcreateevent;

    ViewPager viewPager;
    TabLayout tabLayout;
    Toolbar toolbar;
    WalletAddAdapter adapter;

    TextView uidTv, unameTv, titleTv;

    RelativeLayout backbtn;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_monet_to_wallet);

        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tablayout);

        unameTv = findViewById(R.id.uname);
        uidTv = findViewById(R.id.uid);

        unameTv.setText(AccountUtils.getName(this));
        uidTv.setText(AccountUtils.getCustomerID(this));


        titleTv = findViewById(R.id.title);
        titleTv.setVisibility(View.VISIBLE);
        titleTv.setText("Money Wallet");

        adapter = new WalletAddAdapter(getSupportFragmentManager());
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(adapter);

    }

//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_add_monet_to_wallet;
//    }
//
//    @Override
//    protected void initView() {
//
//        ButterKnife.bind(this);
//        viewPager = findViewById(R.id.viewpager);
//        tabLayout = findViewById(R.id.tablayout);
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        setTitle("Money Wallet");
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//
//        adapter = new WalletAddAdapter(getSupportFragmentManager());
//        tabLayout.setupWithViewPager(viewPager);
//        viewPager.setAdapter(adapter);
//
//    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
        super.onBackPressed();
        finish();
    }


}